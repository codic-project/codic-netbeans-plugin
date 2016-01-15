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
package jp.codic.plugins.netbeans.client.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import jp.codic.plugins.netbeans.client.CodicAPIException;
import jp.codic.plugins.netbeans.client.CodicClient;
import jp.codic.plugins.netbeans.client.CodicErrorResponse;
import jp.codic.plugins.netbeans.client.Entry;
import jp.codic.plugins.netbeans.client.Project;
import jp.codic.plugins.netbeans.client.Translation;
import jp.codic.plugins.netbeans.client.config.CodicConfig;
import jp.codic.plugins.netbeans.client.http.CodicHttpClient;
import jp.codic.plugins.netbeans.client.http.CodicHttpClientImpl;
import jp.codic.plugins.netbeans.client.http.CodicHttpResponse;
import jp.codic.plugins.netbeans.client.params.GetEntriesParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams;
import jp.codic.plugins.netbeans.client.params.Params;

public class CodicClientImpl implements CodicClient {

    private final CodicConfig config;
    private final CodicHttpClient httpClient;
    private final Gson gson = new Gson();

    public CodicClientImpl(CodicConfig config) {
        this.config = config;
        CodicHttpClientImpl httpClientImpl = new CodicHttpClientImpl(config.getUserAgent());
        httpClientImpl.setAccessToken(config.getAccessToken());
        this.httpClient = httpClientImpl;
    }

    @Override
    public List<Translation> getTranslations(GetTranslationsParams params) throws CodicAPIException {
        CodicHttpResponse response = get(buildEndpoint(TRANSLATE_RESOURCE), params);
        checkError(response);
        Translation[] translations = gson.fromJson(response.asString(), TranslationJSONImpl[].class);
        return Arrays.asList(translations);
    }

    @Override
    public List<Project> getProjects() throws CodicAPIException {
        CodicHttpResponse response = get(buildEndpoint(PROJECTS_RESOURCE));
        checkError(response);
        Project[] projects = gson.fromJson(response.asString(), ProjectJSONImpl[].class);
        return Arrays.asList(projects);
    }

    @Override
    public Project getProject(int id) throws CodicAPIException {
        CodicHttpResponse response = get(buildEndpoint(String.format(PROJECT_RESOURCE_FORMAT, id)));
        checkError(response);
        Project project = gson.fromJson(response.asString(), ProjectJSONImpl.class);
        return project;
    }

    @Override
    public List<Entry> getEntries(GetEntriesParams params) throws CodicAPIException {
        CodicHttpResponse response = get(buildEndpoint(LOOKUP_RESOURCE), params);
        checkError(response);
        Entry[] entries = gson.fromJson(response.asString(), EntryJSONImpl[].class);
        return Arrays.asList(entries);
    }

    @Override
    public Entry getEntry(int id) throws CodicAPIException {
        CodicHttpResponse response = get(buildEndpoint(String.format(PROJECT_RESOURCE_FORMAT, id)));
        checkError(response);
        Entry entry = gson.fromJson(response.asString(), EntryJSONImpl.class);
        return entry;
    }

    @Override
    public boolean verifyConnection() throws CodicAPIException {
        CodicHttpResponse response = get(buildEndpoint("")); // NOI18N
        checkError(response);
        return true;
    }

    private String buildEndpoint(String resourceUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(config.getBaseUrl());
        sb.append(resourceUrl == null ? "" : resourceUrl); // NOI18N
        return sb.toString();
    }

    private CodicHttpResponse get(String endpoint) throws CodicAPIException {
        return get(endpoint, null);
    }

    private CodicHttpResponse get(String endpoint, Params params) throws CodicAPIException {
        return httpClient.get(endpoint, params);
    }

    private void checkError(CodicHttpResponse response) throws CodicAPIException {
        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            try {
                CodicErrorResponse errorResponse = gson.fromJson(response.asString(), CodicErrorResponseJSONImpl.class);
                if (errorResponse != null) {
                    throw new CodicAPIException(errorResponse);
                } else {
                    throw new CodicAPIException(response.asString(), response.getStatusCode());
                }
            } catch (JsonSyntaxException ex) {
                throw new CodicAPIException(response.asString(), response.getStatusCode());
            }
        }
    }

}
