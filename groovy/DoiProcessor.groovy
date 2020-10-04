import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 *
 */
class DoiProcessor {

    void run(String path) {
        XmlFileVisitor visitor = new XmlFileVisitor()
        Path directory = Paths.get(path)
        Files.walkFileTree(directory, visitor)


        visitor.publisherIndex.sort().each { host,list ->
            File outfile = new File("/Users/suderman/Projects/identify-galaxy/resources/indices/${host}.csv")
            outfile.withPrintWriter { writer ->
                list.each { writer.println "${it.url()},${it.type()}" }
            }
            println "Wrote ${outfile.path} ${list.size()}"
        }
//        visitor.typeIndex.each { String type, List<Record> records ->
//            println type
//            records.each { println it.url(type) }
//            println()
//        }
    }

    static void main(String[] args) {
        new DoiProcessor().run("/Users/suderman/Projects/identify-galaxy/doi")
    }
}
