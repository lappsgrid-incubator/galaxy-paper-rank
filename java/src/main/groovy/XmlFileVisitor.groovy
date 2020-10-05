import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/**
 * FileVisitor called when walking a directory tree looking for DOI XML files. Each
 * XML files is parsed and any download links, and their mime-types, are indexed.
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
        if (glob.matches(file)) {
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
