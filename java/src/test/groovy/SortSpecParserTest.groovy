import org.junit.Test

/**
 *
 */
class SortSpecParserTest {
    @Test
    void testNumericDescending() {
        CVS2Markdown.CellComparator cc = CVS2Markdown.SortSpecParser.parse("1nd")
        assert cc != null
        assert cc.column == 1
        assert cc.numeric
        assert !cc.ascending
    }

    @Test
    void testNumericAscending() {
        CVS2Markdown.CellComparator cc = CVS2Markdown.SortSpecParser.parse("an2")
        assert cc != null
        assert cc.column == 2
        assert cc.numeric
        assert cc.ascending
    }

    @Test
    void testStringDescending() {
        CVS2Markdown.CellComparator cc = CVS2Markdown.SortSpecParser.parse("s19d")
        assert cc != null
        assert cc.column == 19
        assert !cc.numeric
        assert !cc.ascending
    }

    @Test
    void testStringAscending() {
        CVS2Markdown.CellComparator cc = CVS2Markdown.SortSpecParser.parse("19as")
        assert cc != null
        assert cc.column == 19
        assert !cc.numeric
        assert cc.ascending
    }

    @Test
    void invalidOption() {
        CVS2Markdown.CellComparator cc = CVS2Markdown.SortSpecParser.parse("4fs")
        assert cc == null
    }

    @Test
    void noColumnNumber() {
        CVS2Markdown.CellComparator cc = CVS2Markdown.SortSpecParser.parse("ns")
        assert cc == null
    }
}
