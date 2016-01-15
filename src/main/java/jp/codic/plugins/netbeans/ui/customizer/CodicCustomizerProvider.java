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
package jp.codic.plugins.netbeans.ui.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import jp.codic.plugins.netbeans.ui.ConfigVO;
import org.apache.commons.lang.StringUtils;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import jp.codic.plugins.netbeans.preferences.CodicPreferences;

/**
 *
 * @author junichi11
 */
public class CodicCustomizerProvider implements ProjectCustomizer.CompositeCategoryProvider {

    private Category category;
    private Lookup context;
    private Project project;
    private CodicCustomizerPanel panel;

    public static final String CATEGORY_NAME = "Codic"; // NOI18N

    @ProjectCustomizer.CompositeCategoryProvider.Registrations({
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-clientproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-earproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-j2ee-ejbjarproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-java-j2seproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-php-project", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-web-project", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org.netbeans.modules.web.clientproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-web-clientproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-ruby-rubyproject", position = 5000),
        @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = "org-netbeans-modules-ruby-railsprojects", position = 5000)
    })
    public static CodicCustomizerProvider createCodicCustomizer() {
        return new CodicCustomizerProvider();
    }

    private CodicCustomizerProvider() {
    }

    @Override
    public ProjectCustomizer.Category createCategory(Lookup lookup) {
        return Category.create(CATEGORY_NAME, CATEGORY_NAME, null);
    }

    @Override
    public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
        this.category = category;
        this.context = context;

        // add listener
        category.setOkButtonListener(new OKButtonActionListener());

        return getPanel();
    }

    private CodicCustomizerPanel getPanel() {
        Project currentProject = context.lookup(Project.class);
        if (panel == null || project != currentProject) {
            project = currentProject;
            ConfigVO config;
            if (project != null) {
                config = new ConfigVO(
                        CodicPreferences.getAccessToken(project),
                        CodicPreferences.getProjectId(project),
                        CodicPreferences.getCasing(project)
                );
            } else {
                config = new ConfigVO("", -1, Casing.None);  // NOI18N
            }
            panel = new CodicCustomizerPanel(config);
            panel.setUseGlobalOptions(CodicPreferences.useGlobalOptions(project));
        }
        return panel;
    }

    private void save() {
        if (project != null) {
            boolean useGlobalOptions = getPanel().useGlobalOptions();
            CodicPreferences.setUseGlobalOptions(project, useGlobalOptions);
            if (useGlobalOptions) {
                // initialize accessToken and project id
                if (!CodicPreferences.getAccessToken(project).isEmpty()) {
                    CodicPreferences.setAccessToken(project, ""); // NOI18N
                    CodicPreferences.setProjectId(project, -1);
                }
                CodicPreferences.setCasing(project, getPanel().getCasing());
                panel = null;
            } else {
                String accessToken = getPanel().getAccessToken();
                CodicPreferences.setAccessToken(project, accessToken);
                CodicPreferences.setCasing(project, getPanel().getCasing());
                jp.codic.plugins.netbeans.client.Project codicProject = getPanel().getProject();
                long projectId = codicProject == null ? -1 : codicProject.getId();
                if (StringUtils.isEmpty(accessToken)) {
                    projectId = -1;
                }
                CodicPreferences.setProjectId(project, projectId);
                if (StringUtils.isEmpty(accessToken)) {
                    panel = null;
                }
            }
        }
    }

    private class OKButtonActionListener implements ActionListener {

        public OKButtonActionListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!category.isValid()) {
                return;
            }
            save();
        }
    }

}
