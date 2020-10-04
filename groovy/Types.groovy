/**
 * The MIME types we recognize.  The ALL arrays defines the types in
 * our order of preference given a choice.
 */
class Types {
    static final String XML = "application/xml"
    static final String PDF = "application/pdf"
    static final String HTML = "text/html"
    static final String TEXT = "text/plain"
    static final String TEXT_XML = "text/xml"

    static final String[] ALL = [ PDF, XML, TEXT_XML, HTML, TEXT ] as String[]
}
