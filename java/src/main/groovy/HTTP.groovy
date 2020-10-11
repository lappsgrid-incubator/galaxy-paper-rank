/**
 *
 */
class HTTP {

    HttpURLConnection connection

    static HTTP get(String url) {
        return get(new URI(url).toURL())
    }

    static HTTP get(URL url) {
        HTTP http = new HTTP()
        http.connection = url.openConnection()
        http.connection.setRequestMethod("GET")
        return http
    }

    HTTP header(String name, String value) {
        connection.setRequestProperty(name, value)
        return this
    }

    HTTP timeout(long msec) {
        connection.setConnectTimeout(msec)
        return this
    }

    Response response() {
        Response response = new Response()
        if (connection.responseCode == 301 || connection.responseCode == 302) {
            String location = connection.getHeaderField("Location")
            if (location != null) {
                return HTTP.get(location).response()
            }
        }
        response.code = connection.responseCode
        response.message = connection.responseMessage
        response.headers = connection.headerFields
        response.body = connection.inputStream.bytes
        return response
    }
    public class Response {
        int code
        String message
        Map<String,List<String>> headers
        byte[] body
    }
}
