import org.xml.sax.ErrorHandler
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException

/**
 * Factory method to consistently create XML parsers.  In particular loading
 * external DTDs should be disabled for security reasons.
 */
class XML {
    private XML() {}

    static XmlParser Parser() {
        XmlParser parser = new XmlParser()
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        parser.setErrorHandler(new SilentErrorHandler())
        return parser
    }

    static class SilentErrorHandler implements ErrorHandler {

        @Override
        void warning(SAXParseException exception) throws SAXException {

        }

        @Override
        void error(SAXParseException exception) throws SAXException {

        }

        @Override
        void fatalError(SAXParseException exception) throws SAXException {

        }
    }
}
