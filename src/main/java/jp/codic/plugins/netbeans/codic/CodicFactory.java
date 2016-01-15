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

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import jp.codic.plugins.netbeans.client.config.CodicConfig;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import jp.codic.plugins.netbeans.codic.IllegalCodicConfigException.ConfigType;
import jp.codic.plugins.netbeans.options.CodicOptions;
import jp.codic.plugins.netbeans.utils.CodicUtils;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.filesystems.FileObject;
import jp.codic.plugins.netbeans.preferences.CodicPreferences;

/**
 *
 * @author junichi11
 */
public final class CodicFactory {

    private CodicFactory() {
    }

    public static Codic getDefault(JTextComponent textComponent) throws IllegalCodicConfigException {
        Document document = textComponent.getDocument();
        // project configuration
        if (document != null) {
            FileObject fileObject = NbEditorUtilities.getFileObject(document);
            if (fileObject != null) {
                Project project = FileOwnerQuery.getOwner(fileObject);
                if (project != null && !CodicPreferences.useGlobalOptions(project)) {
                    // get project config
                    String accessToken = CodicPreferences.getAccessToken(project);
                    if (accessToken == null || accessToken.isEmpty()) {
                        throw new IllegalCodicConfigException("Access Token of Project Options is empty.", ConfigType.Project); // NOI18N
                    }
                    return newCodic(
                            getDefaultConfig(accessToken),
                            CodicPreferences.getProjectId(project),
                            CodicPreferences.getCasing(project)
                    );
                }
            }
        }

        // global configuration
        CodicOptions options = CodicOptions.getInstance();
        String accessToken = options.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalCodicConfigException("Access Token of Global Options is empty.", ConfigType.Global); // NOI18N
        }
        long projectId = options.getProjectId();
        Casing casing = options.getCasing();
        return newCodic(getDefaultConfig(accessToken), projectId, casing);
    }

    public static CodicConfig getDefaultConfig(String accessToken) {
        return new CodicConfig(accessToken, CodicUtils.getUserAgent());
    }

    public static Codic newCodic(CodicConfig config, long projectId, Casing casing) {
        return new CodicImpl(config, projectId, casing);
    }

}
