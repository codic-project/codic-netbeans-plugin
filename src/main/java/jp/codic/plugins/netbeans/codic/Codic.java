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
package jp.codic.plugins.netbeans.codic;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.codic.plugins.netbeans.client.CodicAPIException;
import jp.codic.plugins.netbeans.client.CodicClient;
import jp.codic.plugins.netbeans.client.CodicClientFactory;
import jp.codic.plugins.netbeans.client.Entry;
import jp.codic.plugins.netbeans.client.Project;
import jp.codic.plugins.netbeans.client.Translation;
import jp.codic.plugins.netbeans.client.params.GetEntriesParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import org.openide.awt.HtmlBrowser;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public abstract class Codic {

    private static final Logger LOGGER = Logger.getLogger(Codic.class.getName());
    private static final String CODIC_URL = "https://codic.jp/"; // NOI18N
    private static final RequestProcessor RP = new RequestProcessor(Codic.class);

    public abstract List<Translation> getTranslations(GetTranslationsParams params);

    public abstract List<Project> getProjects();

    public abstract Project getProject(int id);

    public abstract List<Entry> getEntries(GetEntriesParams params);

    public abstract Entry getEntry(int id);

    public abstract Casing getCasing();

    public abstract long getProjectId();

    public static RequestProcessor getRequestProcessor() {
        return RP;
    }

    public static void openCodicSite() {
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(CODIC_URL));
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    public static boolean verifyConnection(String accessToken) throws CodicAPIException {
        CodicClient client = CodicClientFactory.create(CodicFactory.getDefaultConfig(accessToken));
        return client.verifyConnection();
    }

}
