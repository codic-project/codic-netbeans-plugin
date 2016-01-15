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
package jp.codic.plugins.netbeans.ui;

import java.awt.Cursor;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jp.codic.plugins.netbeans.client.CodicAPIException;
import jp.codic.plugins.netbeans.client.CodicClient;
import jp.codic.plugins.netbeans.client.CodicClientFactory;
import jp.codic.plugins.netbeans.client.CodicError;
import jp.codic.plugins.netbeans.client.CodicErrorResponse;
import jp.codic.plugins.netbeans.client.Project;
import jp.codic.plugins.netbeans.client.config.CodicConfig;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import jp.codic.plugins.netbeans.codic.Codic;
import jp.codic.plugins.netbeans.codic.CodicFactory;
import jp.codic.plugins.netbeans.utils.UiUtils;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class CodicConfigPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -2827500772569453364L;
    private final DefaultComboBoxModel<Project> projectComboBoxModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<Casing> casingComboBoxModel = new DefaultComboBoxModel(Casing.values());
    private String connectionError;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private volatile boolean verified = false;

    /**
     * Creates new form CodicConfigPanel
     */
    public CodicConfigPanel() {
        initComponents();
        init();
    }

    private void init() {
        setError(" "); // NOI18N
        accessTokenTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
        projectComboBox.setModel(projectComboBoxModel);
        projectComboBox.setRenderer(new CodicConfigCellRenderer(projectComboBox.getRenderer()));
        casingComboBox.setModel(casingComboBoxModel);
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
    }

    public void setError(String message) {
        if (message == null) {
            message = " "; // NOI18N
        }
        errorLabel.setText(message);
    }

    public boolean isVerified() {
        return verified;
    }

    public void setConfg(ConfigVO config) {
        setAccessToken(config.getAccessToken());
        setProjects(config.getAccessToken(), config.getProjectId(), false);
        setSelectedCasing(config.getCasing());
    }

    private void setAccessToken(String accessToken) {
        if (accessToken == null) {
            return;
        }
        accessTokenTextField.setText(accessToken);
    }

    private void setProjects(String accessToken, boolean showSuccessDialog) {
        setProjects(accessToken, -1, showSuccessDialog);
    }

    @NbBundle.Messages("CodicConfigPanel.connection.successful=Connection Successful!")
    private void setProjects(String accessToken, final long selectedId, final boolean showSuccessDialog) {
        if (accessToken == null || accessToken.isEmpty()) {
            return;
        }
        verifyConnectionButton.setEnabled(false);
        projectComboBox.removeAllItems();
        connectionError = null;
        RequestProcessor rp = Codic.getRequestProcessor();
        rp.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<Project> projects = getProjects();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setProjects(projects, selectedId);
                            if (showSuccessDialog) {
                                UiUtils.showPlainDialog(Bundle.CodicConfigPanel_connection_successful());
                            }
                            verifyConnectionButton.setEnabled(true);
                        }
                    });
                    verified = true;
                } catch (CodicAPIException ex) {
                    setConnectionError(ex);
                } catch (Exception ex) {
                    connectionError = ex.getMessage();
                } finally {
                    if (connectionError != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                verifyConnectionButton.setEnabled(true);
                            }
                        });
                    }
                    fireChange();
                }
            }
        });
    }

    private List<Project> getProjects() throws CodicAPIException {
        CodicClient client = CodicClientFactory.create(CodicFactory.getDefaultConfig(getAccessToken()));
        List<Project> projects = client.getProjects();
        return projects;
    }

    private void setProjects(List<Project> projects, long selectedId) {
        boolean first = true;
        for (Project project : projects) {
            projectComboBoxModel.addElement(project);
            if (project.getId() == selectedId) {
                projectComboBoxModel.setSelectedItem(project);
            }
            if (first) {
                if (selectedId < 0) {
                    projectComboBoxModel.setSelectedItem(project);
                }
                first = false;
            }
        }
    }

    private void setConnectionError(CodicAPIException ex) {
        // set error message
        CodicErrorResponse errorResponse = ex.getErrorResponse();
        if (errorResponse != null) {
            for (CodicError error : errorResponse.getErrors()) {
                connectionError = String.format("%s(%s)", error.getMessage(), error.getCode()); // NOI18N
                break;
            }
        } else {
            connectionError = String.format("%s(%s)", ex.getMessage(), ex.getCode()); // NOI18N
        }
    }

    private void setSelectedCasing(Casing casing) {
        casingComboBoxModel.setSelectedItem(casing);
    }

    public String getAccessToken() {
        return accessTokenTextField.getText().trim();
    }

    public Casing getSelectedCasing() {
        return (Casing) casingComboBox.getSelectedItem();
    }

    public Project getSelectedProject() {
        return (Project) projectComboBox.getSelectedItem();
    }

    public void addChangeListener(ChangeListener changeListener) {
        changeSupport.addChangeListener(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeSupport.removeChangeListener(changeListener);
    }

    @NbBundle.Messages("CodicConfigPanel.verification.error=Please click the Verify button")
    void fireChange() {
        if (connectionError != null) {
            setError(connectionError);
            return;
        }
        if (!verified) {
            setError(Bundle.CodicConfigPanel_verification_error());
            return;
        }
        setError(" "); // NOI18N
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        accessTokenLabel = new javax.swing.JLabel();
        accessTokenTextField = new javax.swing.JTextField();
        projectLabel = new javax.swing.JLabel();
        projectComboBox = new javax.swing.JComboBox<>();
        webSiteLabel = new javax.swing.JLabel();
        verifyConnectionButton = new javax.swing.JButton();
        casingLabel = new javax.swing.JLabel();
        casingComboBox = new javax.swing.JComboBox<>();
        errorLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(accessTokenLabel, org.openide.util.NbBundle.getMessage(CodicConfigPanel.class, "CodicConfigPanel.accessTokenLabel.text")); // NOI18N

        accessTokenTextField.setText(org.openide.util.NbBundle.getMessage(CodicConfigPanel.class, "CodicConfigPanel.accessTokenTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(projectLabel, org.openide.util.NbBundle.getMessage(CodicConfigPanel.class, "CodicConfigPanel.projectLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(webSiteLabel, org.openide.util.NbBundle.getMessage(CodicConfigPanel.class, "CodicConfigPanel.webSiteLabel.text")); // NOI18N
        webSiteLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                webSiteLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                webSiteLabelMouseEntered(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(verifyConnectionButton, org.openide.util.NbBundle.getMessage(CodicConfigPanel.class, "CodicConfigPanel.verifyConnectionButton.text")); // NOI18N
        verifyConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyConnectionButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(casingLabel, org.openide.util.NbBundle.getMessage(CodicConfigPanel.class, "CodicConfigPanel.casingLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(CodicConfigPanel.class, "CodicConfigPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accessTokenLabel)
                            .addComponent(projectLabel)
                            .addComponent(casingLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(projectComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(casingComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(accessTokenTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(verifyConnectionButton))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(errorLabel)
                            .addComponent(webSiteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accessTokenLabel)
                    .addComponent(accessTokenTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(verifyConnectionButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(projectLabel)
                    .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(casingLabel)
                    .addComponent(casingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webSiteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void webSiteLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_webSiteLabelMouseEntered
        webSiteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_webSiteLabelMouseEntered

    private void webSiteLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_webSiteLabelMouseClicked
        Codic.openCodicSite();
    }//GEN-LAST:event_webSiteLabelMouseClicked

    private void verifyConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyConnectionButtonActionPerformed
        setProjects(getAccessToken(), true);
    }//GEN-LAST:event_verifyConnectionButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accessTokenLabel;
    private javax.swing.JTextField accessTokenTextField;
    private javax.swing.JComboBox<Casing> casingComboBox;
    private javax.swing.JLabel casingLabel;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JComboBox<Project> projectComboBox;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JButton verifyConnectionButton;
    private javax.swing.JLabel webSiteLabel;
    // End of variables declaration//GEN-END:variables

    private class DefaultDocumentListener implements DocumentListener {

        public DefaultDocumentListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            verified = false;
            fireChange();
        }
    }
}
