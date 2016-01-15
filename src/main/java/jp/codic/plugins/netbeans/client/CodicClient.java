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
package jp.codic.plugins.netbeans.client;

import java.util.List;
import jp.codic.plugins.netbeans.client.params.GetEntriesParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams;

/**
 *
 * @author junichi11
 */
public interface CodicClient {

    public static final String TRANSLATE_RESOURCE = "/v1.1/engine/translate.json"; // NOI18N
    public static final String PROJECTS_RESOURCE = "/v1/user_projects.json"; // NOI18N
    public static final String PROJECT_RESOURCE_FORMAT = "/v1/user_projects/%s.json"; // NOI18N
    public static final String LOOKUP_RESOURCE = "/v1/ced/lookup.json"; // NOI18N
    public static final String ENTRIES_RESOURCE_FORMAT = "/v1/ced/entries/%s.json"; // NOI18N

    public List<Translation> getTranslations(GetTranslationsParams params) throws CodicAPIException;

    public List<Project> getProjects() throws CodicAPIException;

    public Project getProject(int id) throws CodicAPIException;

    public List<Entry> getEntries(GetEntriesParams params) throws CodicAPIException;

    public Entry getEntry(int id) throws CodicAPIException;

    public boolean verifyConnection() throws CodicAPIException;

}
