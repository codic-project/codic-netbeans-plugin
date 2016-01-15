/*
 * The MIT License
 *
 * Copyright 2016 junichi11.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.codic.plugins.netbeans.client.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jp.codic.plugins.netbeans.client.CodicAPIException;
import jp.codic.plugins.netbeans.client.params.Params;
import org.openide.util.Pair;

public class CodicHttpClientImpl implements CodicHttpClient {

    private String accessToken;
    private int readTimeout = 2000;
    private int connectTimeout = 1000;
    private final String userAgent;

    public CodicHttpClientImpl(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public CodicHttpResponse get(String endpoint, Params params) throws CodicAPIException {
        StringBuilder url = new StringBuilder();
        url.append(endpoint);
        if (params != null && !params.getParameters().isEmpty()) {
            url.append("?").append(buildParamString(params)); // NOI18N
        }
        HttpURLConnection connection = openUrlConnection(url.toString(), "GET"); // NOI18N
        return new CodicHttpResponseImpl(connection);
    }

    private String buildParamString(Params params) throws CodicAPIException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        if (params != null) {
            for (Pair<String, String> parameter : params.getParameters()) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&"); // NOI18N
                }
                try {
                    sb.append(URLEncoder.encode(parameter.first(), StandardCharsets.UTF_8.name()));
                    sb.append("="); // NOI18N
                    sb.append(URLEncoder.encode(parameter.second(), StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException ex) {
                    throw new CodicAPIException(ex);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public CodicHttpResponse post(String endpoint, Params params) throws CodicAPIException {
        HttpURLConnection connection = openUrlConnection(endpoint, "POST"); // NOI18N
        connection.setDoInput(true);
        connection.setDoOutput(true);
        if (params != null && !params.getParameters().isEmpty()) {
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.write(buildParamString(params).getBytes(StandardCharsets.UTF_8.name()));
                outputStream.flush();
            } catch (IOException ex) {
                throw new CodicAPIException(ex);
            }
        }
        return new CodicHttpResponseImpl(connection);
    }

    private HttpURLConnection openUrlConnection(String url, String method) throws CodicAPIException {
        return openUrlConnection(url, method, null);
    }

    private HttpURLConnection openUrlConnection(String url, String mehtod, String contentType) throws CodicAPIException {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(mehtod);
            connection.setRequestProperty("Accept", "*/*"); // NOI18N
            connection.setRequestProperty("User-Agent", userAgent); // NOI18N
            if (contentType != null && !contentType.isEmpty()) {
                connection.setRequestProperty("Content-Type", contentType); // NOI18N
            }
            if (accessToken != null && !accessToken.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + accessToken); // NOI18N
            }
            connection.setReadTimeout(readTimeout);
            connection.setConnectTimeout(connectTimeout);
        } catch (IOException ex) {
            throw new CodicAPIException(ex.getMessage());
        }
        return connection;
    }

}
