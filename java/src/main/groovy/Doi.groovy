import groovy.xml.QName

/**
 *
 */
class Doi {

    XmlParser parser = XML.Parser()

    String download(String doi) {
        String token = System.getenv('CROSSREF_API_TOKEN')
        if (token == null) {
            println "The CROSSREF_API_TOKEN environment variable has not been set."
            return null
        }

        Map headers = [
                "Accept":'application/vnd.crossref.unixsd+xml',
                'User-Agent': 'LappsgridDownloader/1.0 (http://www.lappsgrid.org mailto:suderman@cs.vassar.edu)',
                'CR-Clickthrough-Client-Token': token
        ]

        String xml = null
        new HTTP("https://dx.doi.org/${doi}").headers(headers).get { HTTP.Response response ->
            if (response.code == 200) {
                xml = new String(response.body)
            }
            else {
                failed = true
                println "Unable to download DOI record. Status ${response.code}"
                println response.message
                println "Headers"
                response.headers.each { name, value ->
                    println "\t$name : $value"
                }
            }

        }
        if (xml == null) {
            return null
        }
        Node root = parser.parseText(xml)


    }

    void test() {
        String path = "/Users/suderman/Projects/identify-galaxy/doi/10.1002/bit.26404.xml"
        String token = System.getenv("CROSSREF_API_TOKEN")
        if (token == null) {
            println "The CROSSREF_API_TOKEN environment variable has not been set."
            return
        }

        String xml = new File(path).text
        XmlParser parser = XML.Parser()
        Node root = parser.parseText(xml)
        def nodes = findAll(root) { Node node ->
            node.name().localPart == 'collection' && node.attribute("property") == 'text-mining'
        }
        if (nodes.size() > 0) {
            Node node = nodes[0]
            String url = getResourceUrl(node, "application/pdf")
            if (url == null) {
                url = getResourceUrl(node, 'application/xml')
            }
            if (url == null) {
                url = getResourceUrl(node, 'text/xml')
            }
            if (url == null) {
                println "No download URL found."
            }
            else {
                println "Download URL: $url"
                new HTTP(url).header('CR-Clickthrough-Client-Token', token).get { HTTP.Response response ->
                    if (response.code == 200) {
                        println "File downloaded"
                        println "Size: ${response.body.length}"
                    }
                    else {
                        println "Status: ${response.code}"
                        println "Reason: ${response.message}"
                        response.headers.each { name,value ->
                            println "$name : $value"
                        }
                    }
                }
            }
        }
//        println "Found ${nodes.size()} nodes"
    }

    String getResourceUrl(Node node, String mimeType) {
        List<Node> nodes = findAll(node) { Node n ->
            n.name().localPart == 'resource' && n.attribute('mime_type') == mimeType
        }
        if (nodes.size() > 0) {
            return nodes[0].text()
        }
        return null
    }
    List<Node> findAll(Node root, Closure condition) {
        List<Node> results = []
        Stack<Node> stack = new Stack()
        stack.push(root)
        while (!stack.empty()) {
            def node = stack.pop()
            if (node instanceof Node) {
                if (condition(node)) {
                    results << node
                }
                else {
                    node.children().each { stack.push(it) }
                }
            }
        }
        return results
    }
    static void main(String[] args) {
        new Doi().test()
    }
}
