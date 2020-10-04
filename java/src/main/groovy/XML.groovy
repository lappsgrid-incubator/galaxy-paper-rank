/**
 * Factory method to consistently create XML parsers.  In particular loading
 * external DTD's should be disabled for security.
 */
class XML {
    private XML() {}

    static XmlParser Parser() {
        XmlParser parser = new XmlParser()
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        return parser
    }
}
