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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jp.codic.plugins.netbeans.client.CodicAPIException;
import jp.codic.plugins.netbeans.client.CodicClient;
import jp.codic.plugins.netbeans.client.CodicClientFactory;
import jp.codic.plugins.netbeans.client.Entry;
import jp.codic.plugins.netbeans.client.Project;
import jp.codic.plugins.netbeans.client.Translation;
import jp.codic.plugins.netbeans.client.config.CodicConfig;
import jp.codic.plugins.netbeans.client.params.GetEntriesParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import jp.codic.plugins.netbeans.utils.UiUtils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;

public class CodicImpl extends Codic {

    private final CodicConfig config;
    private final long projectId;
    private final Casing casing;
    private static final Logger LOGGER = Logger.getLogger(CodicImpl.class.getName());

    public CodicImpl(@NonNull CodicConfig config, long projectId, Casing casing) {
        assert config != null;
        this.config = config;
        this.projectId = projectId;
        this.casing = casing;
    }

    public CodicImpl(@NonNull CodicConfig config, long projectId) {
        this(config, projectId, Casing.None);
    }

    @Override
    public List<Translation> getTranslations(GetTranslationsParams params) {
        try {
            return createCodicClient().getTranslations(params);
        } catch (final CodicAPIException ex) {
            showErrorDialog(ex);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Project> getProjects() {
        try {
            return createCodicClient().getProjects();
        } catch (CodicAPIException ex) {
            showErrorDialog(ex);
        }
        return Collections.emptyList();
    }

    @Override
    @CheckForNull
    public Project getProject(int id) {
        try {
            return createCodicClient().getProject(id);
        } catch (CodicAPIException ex) {
            showErrorDialog(ex);
        }
        return null;
    }

    @Override
    public List<Entry> getEntries(GetEntriesParams params) {
        try {
            return createCodicClient().getEntries(params);
        } catch (CodicAPIException ex) {
            showErrorDialog(ex);
        }
        return Collections.emptyList();
    }

    @Override
    @CheckForNull
    public Entry getEntry(int id) {
        try {
            return createCodicClient().getEntry(id);
        } catch (CodicAPIException ex) {
            showErrorDialog(ex);
        }
        return null;
    }

    @Override
    public GetTranslationsParams.Casing getCasing() {
        return casing;
    }

    @Override
    public long getProjectId() {
        return projectId;
    }

    private void showErrorDialog(final CodicAPIException ex) {
        LOGGER.log(Level.WARNING, ex.getMessage());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UiUtils.showErrorDialog(ex.getMessage());
            }
        });
    }

    private CodicClient createCodicClient() {
        return CodicClientFactory.create(config);
    }

}
