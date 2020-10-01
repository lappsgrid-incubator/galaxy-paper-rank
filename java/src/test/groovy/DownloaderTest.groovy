import org.junit.Test

/**
 *
 */
class DownloaderTest {

    @Test
    void run() {
        new Downloader().process("/Users/suderman/Projects/identify-galaxy/python/doi-file-list.txt")
    }
}
