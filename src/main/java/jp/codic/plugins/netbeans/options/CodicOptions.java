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
package jp.codic.plugins.netbeans.options;

import java.util.prefs.Preferences;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public final class CodicOptions {

    public static final String SUB_PATH = "Advanced/Codic"; // NOI18N

    private static final String ACCESS_TOKEN = "codic.access.token"; // NOI18N
    private static final String PROJECT_ID = "codic.project.id"; // NOI18N
    private static final String CASING = "codic.casing"; // NOI18N

    private static final CodicOptions INSTANCE = new CodicOptions();

    private CodicOptions() {
    }

    public static CodicOptions getInstance() {
        return INSTANCE;
    }

    public String getAccessToken() {
        return getPreferences().get(ACCESS_TOKEN, null);
    }

    public void setAccessToken(String accessToken) {
        getPreferences().put(ACCESS_TOKEN, accessToken);
    }

    public Casing getCasing() {
        String casing = getPreferences().get(CASING, null);
        return Casing.valueOfString(casing);
    }

    public void setCasing(Casing casing) {
        getPreferences().put(CASING, casing.toString());
    }

    public long getProjectId() {
        return getPreferences().getLong(PROJECT_ID, -1);
    }

    public void setProjectId(long id) {
        getPreferences().putLong(PROJECT_ID, id);
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(CodicOptions.class);
    }

}
