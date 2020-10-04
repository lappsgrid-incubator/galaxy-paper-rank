import org.junit.Test

/**
 *
 */
class Integration {

    @Test
    void testPublishersCSV() {
        String[] args = "-s0sa /Users/suderman/Projects/identify-galaxy/resources/publishers.csv".split()
        CVS2Markdown.main(args)
    }
}
