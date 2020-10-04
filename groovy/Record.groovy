/**
 * Information about download links found in DOI XML metadata files.
 */
class Record {
    /** The XML file this information was parsed from. */
    File file
    /** Was the link marked for text data mining. */
    boolean tdm
    /** Map mime types to a URL */
    Map<String,String> types

    Record() {
        tdm = false
        types = [:]
    }
    Record(File file) {
        this()
        this.file = file
    }

    /**
     *
     * @return the mime type of the most approriate download link.
     */
    String type() {
        for (int i = 0; i < Types.ALL.length; ++i) {
            String type = Types.ALL[i]
            if (types[type]) {
                return type
            }
        }
        return null
    }

    URL url() {
        return url(type())
    }

    URL url(String type) {
        if (type == null || types[type] == null) {
            return null
        }
        return new URI(types[type]).toURL()
    }
}
