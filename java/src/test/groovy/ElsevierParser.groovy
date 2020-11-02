/**
 * Extracts all text from the <body> element of a Elsevier XML document.
 *
 * The program expects two parameters when run; an input directory containing
 * XML files and an output directory where the text file will be written. The
 * file names will be preserved with the .xml extension being changed to .txt
 *
 */
class ElsevierParser {

    XmlParser parser = new XmlParser()

    void run(File directory, File outdir) {
        Set<String> tags = new TreeSet<>()
        FileFilter filter = { File f -> return f.name.endsWith(".xml") }
        File[] files = directory.listFiles(filter)
        files.each { File f ->
            println "Parsing ${f.name}"
            String fname = f.name.replace(".xml", ".txt")
            File outfile = new File(outdir, fname)
            Node root = parser.parse(f)
            Stack stack = new Stack<>()
            stack.push(root)
            while (!stack.empty()) {
                def node = stack.pop()
                Node n = (Node) node
                if (n.name().localPart == 'body') {
                    print(n, outfile)
                }
                else {
                    n.children().each {
                        if (!(it instanceof String)) {
                            stack.push(it)
                        }
                    }
                }
            }
        }
    }

    void print(Node article, File file) {
        file.withPrintWriter { PrintWriter out ->
            Stack<Node> stack = new Stack<>()
            stack.push(article)
            while (!stack.empty()) {
                Node node = stack.pop()
                node.children().each { child ->
                    if (child instanceof String) {
                        out.println(child)
                    }
                    else {
                        stack.push(child)
                    }
                }
            }
        }
        println "Wrote ${file.path}"
    }

    static void main(String[] args) {
        if (args.length != 2) {
            println 'USAGE: groovy ElsevierParser.groovy <indir> <outdir>'
            return
        }
        File indir = new File(args[0])
        if (!indir.exists()) {
            println "No such directory ${indir.path}"
            return
        }
        File outdir = new File(args[1])
        if (!outdir.exists()) {
            println "No such directory ${outdir.path}"
            return
        }
        new ElsevierParser().run(indir, outdir)
    }
}
