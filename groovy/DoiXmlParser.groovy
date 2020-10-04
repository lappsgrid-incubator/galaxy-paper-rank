import groovy.xml.QName

import java.sql.SQLOutput

/**
 * Parses the DOI metadata files looking for download links approved for TDM (Text Data Mining).
 *
 */
class DoiXmlParser {

//    static XmlParser xmlParser() {
//        XmlParser parser = new XmlParser()
//        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
//        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
//        return parser
//    }

    XmlParser parser = XML.Parser()

    String download(String url, String cookie=null) {
        return download(new URI(url).toURL(), cookie)
    }

    String download(URL url, String cookie=null) {
        HttpURLConnection connection = url.openConnection()
        connection.setInstanceFollowRedirects(true)
        connection.setRequestProperty("User-Agent", "LappsgridDownloader/1.0 (http://www.lappsgrid.org mailto:suderman@cs.vassar.edu)")
        connection.setRequestMethod('GET')
        connection.setConnectTimeout(60000)
        int code = connection.responseCode
        if (code == 301 || code == 302) {
            String location = connection.getHeaderField('Location')
            if (location != null) {
//                println "Redirecting to $location"
                return download(location, cookie)
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

    Record process(File file) {
        Node node = null
        try {
            node = parser.parse(file)
        }
        catch (Exception e) {
            println "Invalid XML file: ${file.path} ${e.message}"
            return
        }
        Record record = new Record(file)
        Stack<Node> stack = new Stack<>()
        stack.push(node)
        while (!stack.empty()) {
            node = stack.pop()
            if (!(node instanceof Node)) continue
            if (getName(node) == 'collection') { //} && node.attribute("property") == 'text-mining') {
                if (node.attribute("property") == 'text-mining') {
                    record.tdm = true
                }
                NodeList nodes = node.get("item")
                nodes.each { Node n ->
                    Node resource = n.get("resource").get(0)
                    if (resource != null && resource.attribute("mime_type")) {
                        String type = resource.attribute("mime_type")
                        record.types[type] = resource.value().get(0)
                    }
                }
            }
            else {
                node.children().each { child ->
                    if (child instanceof Node) {
                        stack.push(child)
                    }
                }
            }
        }
        return record
    }

    String getName(Node node) {
        def name = node.name()
        if (name instanceof String) return name
        return name.localPart
    }

    void run(String path, String outdir="/tmp/xml/") {
        FileFilter filter = { File f -> f.isDirectory() || f.name.endsWith(".xml") }
        File directory = new File(path)
        if (!directory.exists()) {
            println "Directory not found: $path"
            return
        }

        if (!outdir.endsWith("/")) outdir += '/'

        Stack<File> files = new Stack<>()
        files.push(directory)
        Map records = [:]
        while (!files.empty()) {
            directory = files.pop()
            for (File entry : directory.listFiles(filter)) {
                if (entry.isDirectory()) {
                    files.push(entry)
                }
                else {
                    Record record = process(entry)
                    if (record) {
                        records[entry.path] = record
                    }
                }
            }
        }

        int noTDM = 0
        int hasTDM = 0
        Map<String,List<Record>> publishers = new HashMap<>()
        records.each { key,record ->
            if (record == null) {
                println("$key has a null record")
            }
            else if (record.tdm) {
                URL url = record.url()
                if (url) {
                    ++hasTDM
                    List<Record> list = publishers.get(url.host)
                    if (list == null) {
                        list = []
                        publishers.put(url.host, list)
                    }
                    list.add(record)
                }
            }
            else {
                ++noTDM
            }
        }
        int downloaded = 0
        publishers.each { k,list ->
            boolean process = true
            list.each { Record record ->
                String type = record.type()
                URL url = record.url(type)
                if (process && type == 'application/xml' && !url.host.contains('wiley')) {
                    String content = download(url)
                    if (content) {
                        String outpath = outdir + record.file.name //url.host
                        if (content.contains("<html")) {
                            type = "html"
                            process = false
                        }
                        else {
                            if (!outpath.endsWith(".xml")) {
                                outpath += ".xml"
                            }
                            type = "SAVED"
                            ++downloaded
                            new File(outpath).text = content
                            println "Wrote $outpath"
                        }
                    }
                }
            }
        }
        println "$hasTDM articles have a TDM collection"
        println "$noTDM articles do not have a TDM section"
        println "Downloaded $downloaded files"
        println "Done."
    }

    static void main(String[] args) {
        DoiXmlParser app = new DoiXmlParser()
        if (args.length == 0) {
            //TODO When no args print a usage message.
            app.run("/Users/suderman/Projects/identify-galaxy/doi", "/tmp/xml")
//            println app.download("http://journals.sagepub.com/doi/full-xml/10.1177/1094342017704893")
        }
        else {
            app.run(args[0], args[1])
        }
//        new DoiXmlParser().run()
//        new DoiXmlParser().process(new File("/Users/suderman/Projects/identify-galaxy/doi/10.1109/pdp.2017.29.xml"))
    }
}


/*
f1000research.com   download XML files
tandfonline.com download PDF
www.nature.com  append .pdf to URL
peerj.com   download XML
cdn.elifesciences.org   download XML
 */