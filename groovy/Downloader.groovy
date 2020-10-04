import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Uses multiple threads to download DOI metadata from dx.doi.org.  The list of DOI
 * is read from a text file (one URL per line) specified on the command line.
 *
 */
class Downloader {

    List<String> failed = []
    AtomicInteger downloaded = new AtomicInteger()

    String download(String url) {
        return download(new URI(url).toURL())
    }

    synchronized void fail(int code, URL url) {
        failed.add("$code $url")
    }

    String download(URL url) {
        println "Getting $url"
        HttpURLConnection connection = url.openConnection()
        connection.setInstanceFollowRedirects(true)
        connection.setRequestProperty("Accept", "application/vnd.crossref.unixsd+xml")
        connection.setRequestProperty("User-Agent", "LappsgridDownloader/1.0 (http://www.lappsgrid.org mailto:suderman@ccs.vassar.edu)")

        connection.setRequestMethod('GET')
        connection.setConnectTimeout(60000)
        int code = connection.responseCode
        if (code == 301 || code == 302) {
            String location = connection.getHeaderField('Location')
            if (location != null) {
                println "Redirecting to $location"
                return download(location)
            }
            else {
                println "WARNING: Redirect with no location!"
                return null
            }
        }
        else if (code == 200) {
            return connection.inputStream.text
        }
        return null
    }

    void process(String inpath, String outpath) {
        ExecutorService executor = Executors.newFixedThreadPool(2)
        File file = new File(inpath)
        if (!file.exists()) {
            println "Unable to find $inpath"
            return
        }
        if (outpath.endsWith("/")) {
            outpath = outpath.substring(0, outpath.length() - 1)
        }
        List<String> lines = file.readLines()
        lines.each { String line ->
            Runnable task = {
                URL url = new URI(line).toURL()
                String content = download(line)
                if (content != null) {
                    try {
                        String relpath = outpath + url.getPath() + '.xml'
                        File xmlFile = new File(relpath)
                        File parent = xmlFile.getParentFile()
                        if (!parent.exists()) {
                            parent.mkdirs()
                        }
                        xmlFile.text = content
                        println "Wrote ${xmlFile.path}"
                        downloaded.incrementAndGet()
                    }
                    catch (Exception e) {
                        e.printStackTrace()
                    }
                }
                else {
                    println "Unable to download $line"
                    fail(999, line)
                }
            }
            executor.submit(task)

        }
        executor.shutdown()
        if (!executor.awaitTermination(600, TimeUnit.SECONDS)) {
            println "Timeout waiting for termination..."
        }
        if (failed.size() > 0) {
            println "There were ${failed.size()} failed downloads"
            failed.each { println it }
        }
        println "Downloaded ${downloaded.get()} files."
        println "Done."
    }

    static void main(String[] args) {
        if (args.length != 2) {
            println "USAGE: groovy Downloader.groovy /path/to/doi-file-list.txt /output/directory/"
            return
        }
        new Downloader().process(args[0], args[1])
//        new Downloader().process("/Users/suderman/Projects/identify-galaxy/python/doi-file-list.txt")
//        println new Downloader().get("http://dx.doi.org/10.1371/journal.pgen.1005052")
    }
}
