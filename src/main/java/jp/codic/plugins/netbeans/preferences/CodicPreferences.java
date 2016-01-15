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
package jp.codic.plugins.netbeans.preferences;

import java.util.prefs.Preferences;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;

/**
 *
 * @author junichi11
 */
public final class CodicPreferences {

    private static final String ACCESS_TOKEN = "codic-access-token"; // NOI18N
    private static final String CASING = "codic-casing"; // NOI18N
    private static final String PROJECT_ID = "codic-project-id"; // NOI18N
    private static final String USE_GLOBAL_OPTIONS = "use-global-options"; // NOI18N

    private CodicPreferences() {
    }

    public static boolean useGlobalOptions(Project project) {
        return getPreferences(project).getBoolean(USE_GLOBAL_OPTIONS, true);
    }

    public static void setUseGlobalOptions(Project project, boolean use) {
        getPreferences(project).putBoolean(USE_GLOBAL_OPTIONS, use);
    }

    public static String getAccessToken(Project project) {
        return getPreferences(project).get(ACCESS_TOKEN, ""); // NOI18N
    }

    public static void setAccessToken(Project project, String accessToken) {
        getPreferences(project).put(ACCESS_TOKEN, accessToken);
    }

    public static long getProjectId(Project project) {
        return getPreferences(project).getLong(PROJECT_ID, -1);
    }

    public static void setProjectId(Project project, long projectId) {
        getPreferences(project).putLong(PROJECT_ID, projectId);
    }

    public static Casing getCasing(Project project) {
        String casing = getPreferences(project).get(CASING, null);
        if (casing == null) {
            return Casing.None;
        }
        return Casing.valueOfString(casing);
    }

    public static void setCasing(Project project, Casing casing) {
        getPreferences(project).put(CASING, casing.getCasingName());
    }

    private static Preferences getPreferences(Project project) {
        return getPreferences(project, false);
    }

    private static Preferences getPreferences(Project project, boolean isShared) {
        return ProjectUtils.getPreferences(project, CodicPreferences.class, isShared);
    }
}
