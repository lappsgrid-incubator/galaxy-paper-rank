/**
 * Parses download links from a DOI/XML file.  Each parse returns a Record object
 * which contains a mapping between the available mime-types and download URLs.
 */
class DoiParser {
    XmlParser parser = XML.Parser()

    Record process(File file) {
        Record record = new Record(file)
        Node node = null
        try {
            node = parser.parse(file)
        }
        catch (Exception e) {
            println "Invalid XML file: ${file.path} ${e.message}"
            return record
        }
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

}
