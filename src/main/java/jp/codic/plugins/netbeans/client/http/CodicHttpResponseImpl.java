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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodicHttpResponseImpl implements CodicHttpResponse {

    private HttpURLConnection connection;
    private int statusCode;
    private InputStream inputStream;
    private String asString;
    private static final Logger LOGGER = Logger.getLogger(CodicHttpResponseImpl.class.getName());

    public CodicHttpResponseImpl(HttpURLConnection connection) {
        try {
            this.connection = connection;
            this.statusCode = connection.getResponseCode();
            this.inputStream = new BufferedInputStream(connection.getInputStream());
        } catch (IOException ex) {
            this.inputStream = new BufferedInputStream(connection.getErrorStream());
        }
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String asString() {
        if (asString == null && inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n"); // NOI18N
                }
                asString = sb.toString();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage());
                asString = ex.getMessage();
            }
        }
        return asString;
    }

    @Override
    public InputStream asInputStream() {
        return inputStream;
    }

}
