import groovy.cli.picocli.CliBuilder
import groovy.json.JsonSlurper

/**
 * Download files from Springer.
 * <p>The Springer REST/API requires an API key and they ask that we rate limit
 * ourselves so we don't use more than one thread when downloading.  The API is
 * so simple we simply use the URL.getText() method to fetch the metadata and
 * article.
 */
class Springer {

    String key
    String path
    String outpath
    JsonSlurper parser

    Springer(String key, String filename, String outpath) {
        this.key = key
        this.path = filename
        this.parser = new JsonSlurper()
        this.outpath = outpath
    }

    void run() {
        File destination = new File(outpath)
        if (!destination.exists()) {
            println "Output directory does not exist"
            return
        }
        if (!destination.isDirectory()) {
            println "Destination is not a directory"
            return
        }

        File file = new File(path)
        if (!file.exists()) {
            println "Unable to find ${file.path}"
            return
        }

        if (!file.isFile()) {
            println "${file.path} is not a file!"
            return
        }

        List<String> errors = []
        List<String> closed = []
        file.eachLine { String line ->
//        String line = file.readLines()[0]
            String pdfUrl = line.split(",")[0]
            String doi = getDoi(pdfUrl)
            if (isOpenAccess(doi)) {
                if (!save(doi, pdfUrl, destination)) {
                    errors.add(pdfUrl)
                }
            }
            else {
                closed.add(pdfUrl)
            }
        }
        if (errors.size() == 0) {
            println "There were no errors"
        }
        else {
            println "Errors"
            errors.each { println it}
            println()
        }
        if (closed.size() > 0) {
            println "Closed"
            closed.each { println it }
        }
    }

    boolean save(String doi, String url, File destination) {
        File file = new File(destination, doi + ".pdf")
        if (!file.parentFile.exists()) {
            if (!file.parentFile.mkdirs()) {
                return false
            }
        }
        try {
            def response = HTTP.get(url).response()
            if (response.code == 200) {
                file.withOutputStream { OutputStream out ->
                    println "Downloaded ${response.body.length} bytes from $url"
                    out.write(response.body)
                }
                println "Wrote ${file.path}"
            }
            else {
                println "ERROR ${response.code} ${response.message} $url"
            }
            return response.code == 200
        }
        catch (Exception e) {
            println e
            return false
        }
    }

    boolean isOpenAccess(String doi) {
        String metaUrl = "http://api.springer.com/metadata/json?api_key=${key}&q=doi%3A${doi}&s=1&p=1"
        String json = null
        try {
            json = new URI(metaUrl).toURL().text
        }
        catch (Exception ignored) {
            println ignored
        }
        if (json == null) return false
        def article = parser.parseText(json)
        if (article.records.size() > 0) {
//            println article.records[0].openaccess
            return article.records[0].openaccess
        }
//        println "No records!"
        return false
    }

    String getDoi(String uri) {
        String[] segments = new URI(uri).toURL().file.split("/")
        return segments[-2] + "/" + segments[-1].replace(".pdf", "")

    }

    static String TEST_JSON = '{"apiMessage":"This JSON was provided by Springer Nature","query":"doi:10.1007/s11306-017-1242-7","apiKey":"cca92c4d2252b4755de9ec3d4e14c5f8","result":[{"total":"1","start":"1","pageLength":"1","recordsDisplayed":"1"}],"records":[{"contentType":"Article","identifier":"doi:10.1007/s11306-017-1242-7","url":[{"format":"","platform":"","value":"http://dx.doi.org/10.1007/s11306-017-1242-7"}],"title":"Navigating freely-available software tools for metabolomics analysis","creators":[{ "creator":"Spicer, Rachel" },{ "creator":"Salek, Reza M." },{ "creator":"Moreno, Pablo" },{ "creator":"Cañueto, Daniel" },{ "creator":"Steinbeck, Christoph" }],"publicationName":"Metabolomics","openaccess":"true","doi":"10.1007/s11306-017-1242-7","publisher":"Springer","publicationDate":"2017-08-09","publicationType":"Journal","issn":"1573-3890","volume":"13","number":"9","genre":["ReviewPaper","Review Article"],"startingPage":"1","endingPage":"16","journalId":"11306","copyright":"©2017 The Author(s)","abstract":"Introduction The field of metabolomics has expanded greatly over the past two decades, both as an experimental science with applications in many areas, as well as in regards to data standards and bioinformatics software tools. The diversity of experimental designs and instrumental technologies used for metabolomics has led to the need for distinct data analysis methods and the development of many software tools. Objectives To compile a comprehensive list of the most widely used freely available software and tools that are used primarily in metabolomics. Methods The most widely used tools were selected for inclusion in the review by either ≥ 50 citations on Web of Science (as of 08/09/16) or the use of the tool being reported in the recent Metabolomics Society survey. Tools were then categorised by the type of instrumental data (i.e. LC–MS, GC–MS or NMR) and the functionality (i.e. pre- and post-processing, statistical analysis, workflow and other functions) they are designed for. Results A comprehensive list of the most used tools was compiled. Each tool is discussed within the context of its application domain and in relation to comparable tools of the same domain. An extended list including additional tools is available at https://github.com/RASpicer/MetabolomicsTools which is classified and searchable via a simple controlled vocabulary. Conclusion This review presents the most widely used tools for metabolomics analysis, categorised based on their main functionality. As future work, we suggest a direct comparison of tools’ abilities to perform specific data analysis tasks e.g. peak picking."}],"facets":[{"name":"subject","values":[{"value":"Biochemistry, general","count":"1"},{"value":"Biomedicine, general","count":"1"},{"value":"Cell Biology","count":"1"},{"value":"Developmental Biology","count":"1"},{"value":"Life Sciences","count":"1"},{"value":"Molecular Medicine","count":"1"}]},{"name":"keyword","values":[{"value":"Bioinformatics","count":"1"},{"value":"Data analysis","count":"1"},{"value":"Freely available","count":"1"},{"value":"Metabolomics","count":"1"},{"value":"Software","count":"1"}]},{"name":"pub","values":[{"value":"Metabolomics","count":"1"}]},{"name":"year","values":[{"value":"2017","count":"1"}]},{"name":"country","values":[{"value":"Germany","count":"1"},{"value":"Spain","count":"1"},{"value":"United Kingdom","count":"1"}]},{"name":"type","values":[{"value":"Journal","count":"1"}]}]}'
    static void main(String[] args) {
        CliBuilder cli = new CliBuilder()

        cli.k(longOpt:'key', args:1, argName:'APIKEY', required: true, order:1, 'Springer API key')
        cli.f(longOpt:'file', args:1, argName:'FILE', required: true, order:1, 'input file containing a list of URLs')
        cli.p(longOpt:'pdf', args:1, argName:'DIR', required: true, order:1, 'directory where PDF files will be saved')

        cli.h(longOpt:'help', usageHelp: true, order:100, 'show this help messages')
        cli.v(longOpt:'version', versionHelp: true, order:90, 'display version information')
        cli.usageMessage.with {
            sortOptions(false)
            footerHeading('NOTES')
            footer('The output PDF directory must already exist.')
        }
        def options = cli.parse(args)
        if (!options) {
//            cli.usage()
            return;
        }
        if (options.h) {
            cli.usage()
            return
        }
        if (options.v) {
            println "Springer Downloader 1.0"
            return
        }

        new Springer(options.k, options.f, options.p).run()
    }
}
