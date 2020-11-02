/**
 *
 */
class HTTP {

    HttpURLConnection connection
    List<Closure> actions
//    static HTTP get(String url) {
//        return get(new URI(url).toURL())
//    }

    HTTP(String url) {
        this(new URI(url).toURL())
    }

    HTTP(URL url) {
        this.connection = url.openConnection()
        this.actions = []
    }

    HTTP header(String name, String value) {
        actions << { conn -> conn.setRequestProperty(name, value) }
        return this
    }

    HTTP timeout(long msec) {
        actions << { conn -> conn.setConnectTimeout(msec) }
        return this
    }

    void get(Closure handler) {
        actions << { conn -> conn..setRequestMethod("GET") }
        actions.each { act -> act(connection) }
        Response response = new Response()
        while (connection.responseCode == 301 || connection.responseCode == 302) {
            String location = connection.getHeaderField("Location")
            if (location == null) {
                break
            }
            connection = new URI(location).toURL().openConnection()
            actions.each { it(connection) }
        }

        response.code = connection.responseCode
        response.message = connection.responseMessage
        response.headers = connection.headerFields
        response.body = connection.inputStream.bytes
        handler(response)
    }

    class Response {
        int code
        String message
        Map<String,List<String>> headers
        byte[] body
    }
}
