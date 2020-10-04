import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/**
 *
 */
class XmlFileVisitor extends SimpleFileVisitor<Path> {

    PathMatcher glob
    DoiParser parser
    Map<String,List<Record>> typeIndex
    Map<String,List<Record>> publisherIndex

    XmlFileVisitor() {
        glob = FileSystems.getDefault().getPathMatcher("regex:.+\\.xml\$")
        parser = new DoiParser()
        typeIndex = [:]
        publisherIndex = [:]
    }

    @Override
    FileVisitResult visitFile(Path file, BasicFileAttributes atts) {
//        println "Visiting $file"
        if (glob.matches(file)) {
//            println "Parsing $file"
            Record record = parser.process(file.toFile())
            record.types.each { type,url ->
                List<Record> list = typeIndex[type]
                addRecord(type, record, typeIndex)
            }
            URL url = record.url()
            if (url) {
                addRecord(url.host, record, publisherIndex)
            }
        }
        return FileVisitResult.CONTINUE
    }

    void addRecord(String key, Record record, Map<String,List<Record>> index) {
        List<Record> list = index[key]
        if (list == null) {
            list = []
            index[key] = list
        }
        list.add(record)
    }
}
