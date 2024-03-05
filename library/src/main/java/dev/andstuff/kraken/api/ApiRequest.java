package dev.andstuff.kraken.api;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents an HTTPS request for querying the Kraken API.
 *
 * @author nyg
 */
class ApiRequest {

    private static final String ERROR_NULL_METHOD = "The API method can't be null.";
    private static final String ERROR_NULL_SIGNATURE = "The signature can't be null.";
    private static final String ERROR_NULL_KEY = "The key can't be null.";
    private static final String ERROR_NO_PARAMETERS = "The parameters can't be null or empty.";
    private static final String ERROR_INCOMPLETE_PRIVATE_METHOD =
            "A private method request requires the API key, the message signature and the method parameters.";

    private static final String GITHUB_NYG = "github.nyg";
    private static final String REQUEST_API_SIGN = "API-Sign";
    private static final String REQUEST_API_KEY = "API-Key";
    private static final String REQUEST_USER_AGENT = "User-Agent";
    private static final String REQUEST_POST = "POST";

    private static final String PUBLIC_URL = "https://api.kraken.com/0/public/";
    private static final String PRIVATE_URL = "https://api.kraken.com/0/private/";

    private static final String AMPERSAND = "&";
    private static final String EQUAL_SIGN = "=";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * The request URL.
     */
    private URL url;

    /**
     * The request message signature.
     */
    private String signature;

    /**
     * The API key.
     */
    private String key;

    /**
     * The request's POST data.
     */
    private StringBuilder postData;

    /**
     * Tells whether the API method is public or private.
     */
    private boolean isPublic;

    /**
     * Executes the request and returns its response.
     *
     * @return the request's response
     * @throws IOException if the underlying {@link HttpsURLConnection} could
     *                     not be set up or executed
     */
    public JsonNode execute() throws IOException {

        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_POST);
            connection.addRequestProperty(REQUEST_USER_AGENT, GITHUB_NYG);

            // set key & signature is method is private
            if (!isPublic) {

                if (key == null || signature == null || postData == null) {
                    throw new IllegalStateException(ERROR_INCOMPLETE_PRIVATE_METHOD);
                }

                connection.addRequestProperty(REQUEST_API_KEY, key);
                connection.addRequestProperty(REQUEST_API_SIGN, signature);
            }

            // write POST data to request
            if (postData != null && !postData.toString().isEmpty()) {

                connection.setDoOutput(true);

                try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
                    out.write(postData.toString());
                }
            }

            // execute request and read response
            return OBJECT_MAPPER.readTree(connection.getInputStream());
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Sets the API method of the request.
     *
     * @param method the API method
     * @return the path of the request taking the method into account
     */
    public String setMethod(KrakenApi.Method method) {

        if (method == null) {
            throw new IllegalArgumentException(ERROR_NULL_METHOD);
        }

        isPublic = method.isPublic;
        try {
            url = new URI((isPublic ? PUBLIC_URL : PRIVATE_URL) + method.name).toURL();
            return url.getPath();
        }
        catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Could not set method URL", e);
        }
    }

    /**
     * Sets the parameters of the API method. Only supports "1-dimension" map.
     * Nulls for keys or values are converted to the string "null".
     *
     * @param parameters a map containing parameter names and values.
     * @return the parameters in POST data format, or null if the parameters are
     * null or empty
     * @throws UnsupportedEncodingException if the named encoding is not
     *                                      supported
     * @throws IllegalArgumentException     if the map is null of empty
     */
    public String setParameters(Map<String, String> parameters) throws UnsupportedEncodingException {

        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException(ERROR_NO_PARAMETERS);
        }

        postData = new StringBuilder();
        for (Entry<String, String> entry : parameters.entrySet()) {
            postData.append(entry.getKey())
                    .append(EQUAL_SIGN)
                    .append(KrakenUtils.urlEncode(entry.getValue()))
                    .append(AMPERSAND);
        }

        return postData.toString();
    }

    /**
     * Sets the value of the API-Key request property.
     *
     * @param key the key
     * @throws IllegalArgumentException if the key is null
     */
    public void setKey(String key) {

        if (key == null) {
            throw new IllegalArgumentException(ERROR_NULL_KEY);
        }

        this.key = key;
    }

    /**
     * Sets the value of the API-Sign request property.
     *
     * @param signature the signature
     * @throws IllegalArgumentException if the signature is null
     */
    public void setSignature(String signature) {

        if (signature == null) {
            throw new IllegalArgumentException(ERROR_NULL_SIGNATURE);
        }

        this.signature = signature;
    }
}
