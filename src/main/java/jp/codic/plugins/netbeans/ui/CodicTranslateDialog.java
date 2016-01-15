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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import jp.codic.plugins.netbeans.client.Translation;
import jp.codic.plugins.netbeans.client.Word;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import jp.codic.plugins.netbeans.codic.Codic;
import jp.codic.plugins.netbeans.codic.CodicFactory;
import jp.codic.plugins.netbeans.codic.IllegalCodicConfigException;
import jp.codic.plugins.netbeans.utils.CodicUtils;
import jp.codic.plugins.netbeans.utils.UiUtils;
import org.netbeans.api.annotations.common.NonNull;
import org.openide.awt.CloseButtonFactory;
import org.openide.text.NbDocument;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

/**
 *
 * @author junichi11
 */
public class CodicTranslateDialog extends JDialog implements FocusListener {

    private static final long serialVersionUID = 7403196290650270030L;
    private static final Logger LOGGER = Logger.getLogger(CodicTranslateDialog.class.getName());

    private static volatile CodicTranslateDialog dialog;
    private static final AWTEventLisnerImpl AWT_EVENT_LISNER = new AWTEventLisnerImpl();
    private static final CloseAction CLOSE_ACTION = new CloseAction();
    private static final KeyStroke ESC_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    private static final String CLOSE_KEY = "codic.translate.closekey"; // NOI18N
    private static final String DIALOG_NAME = "codic.translate.dialog"; // NOI18N

    private final DefaultListModel<Translation> translationListModel = new DefaultListModel<>();
    private final List<Translation> translations;
    private final JTextComponent textComponent;
    private final DefaultComboBoxModel<Casing> casingComboBoxModel = new DefaultComboBoxModel(Casing.values());

    /**
     * Creates new form CodicTranslateDialog
     */
    private CodicTranslateDialog(Frame parent, List<Translation> translations, @NonNull JTextComponent textComponent, Casing casing) {
        super(parent);
        this.translations = translations;
        this.textComponent = textComponent;
        initComponents();
        setCasing(casing);
        init();
    }

    private void init() {
        addFocusListener(this);
        casingComboBox.setRenderer(new TranslationListCellRenderer(casingComboBox.getRenderer()));
        casingComboBox.setModel(casingComboBoxModel);
        translationList.setCellRenderer(new TranslationListCellRenderer(translationList.getCellRenderer()));
        translationList.setModel(translationListModel);
        if (textComponent != null) {
            targetTextField.setText(textComponent.getSelectedText());
        }
        setTranslations(translations);
    }

    public static void showDialog(int x, int y, List<Translation> translations, JTextComponent textComponent, Casing casing) {
        showDialog(x, y, translations, textComponent, casing, true);
    }

    public static void showDialog(int x, int y, List<Translation> translations, JTextComponent textComponent, Casing casing, boolean undecorated) {
        if (dialog != null) {
            return;
        }
        // add listener
        Toolkit.getDefaultToolkit().addAWTEventListener(AWT_EVENT_LISNER, AWTEvent.MOUSE_EVENT_MASK);

        Frame mainWindow = WindowManager.getDefault().getMainWindow();
        dialog = new CodicTranslateDialog(mainWindow, translations, textComponent, casing);
        dialog.dispose();
        dialog.setName(DIALOG_NAME);
        dialog.setUndecorated(undecorated);
        dialog.setLocation(x, y);

        // close by ESC key
        JRootPane rootPane = dialog.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ESC_STROKE, CLOSE_KEY);
        rootPane.getActionMap().put(CLOSE_KEY, CLOSE_ACTION);
        dialog.pack();
        dialog.setVisible(true);
        dialog.requestFocus();
        dialog.requestFocusInWindow();
    }

    private static void closeDialog() {
        if (dialog != null) {
            // remove listener
            Toolkit.getDefaultToolkit().removeAWTEventListener(AWT_EVENT_LISNER);

            dialog.setVisible(false);
            dialog.dispose();
        }
        dialog = null;
    }

    @Override
    public void focusGained(FocusEvent e) {
        targetTextField.requestFocus();
        targetTextField.requestFocusInWindow();
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    private void setCasing(Casing casing) {
        casingComboBoxModel.setSelectedItem(casing);
    }

    private Casing getSelectedCasing() {
        return (Casing) casingComboBox.getSelectedItem();
    }

    private String getTargetText() {
        return targetTextField.getText().trim();
    }

    private void setTranslations(List<Translation> translations) {
        assert EventQueue.isDispatchThread();
        translationListModel.removeAllElements();
        boolean first = true;
        Casing selectedCasing = getSelectedCasing();
        for (Translation translation : translations) {
            translationListModel.addElement(translation);
            if (first) {
                translationList.setSelectedValue(translation, true);
                first = false;
            }

            // XXX add an option?
            if (selectedCasing == Casing.None) {
                for (Casing casing : Casing.values()) {
                    if (casing != Casing.None) {
                        translationListModel.addElement(new TranslationImpl(translation, casing));
                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        translationScrollPane = new javax.swing.JScrollPane();
        translationList = new javax.swing.JList<>();
        targetTextField = new javax.swing.JTextField();
        casingComboBox = new javax.swing.JComboBox<>();
        closeButton = CloseButtonFactory.createBigCloseButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(titleLabel, org.openide.util.NbBundle.getMessage(CodicTranslateDialog.class, "CodicTranslateDialog.titleLabel.text")); // NOI18N
        titleLabel.setFocusable(false);
        titleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        translationList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                translationListMouseClicked(evt);
            }
        });
        translationList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                translationListKeyPressed(evt);
            }
        });
        translationScrollPane.setViewportView(translationList);

        targetTextField.setText(org.openide.util.NbBundle.getMessage(CodicTranslateDialog.class, "CodicTranslateDialog.targetTextField.text")); // NOI18N
        targetTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                targetTextFieldKeyPressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(closeButton, org.openide.util.NbBundle.getMessage(CodicTranslateDialog.class, "CodicTranslateDialog.closeButton.text")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(translationScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(targetTextField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(casingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(casingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(translationScrollPane))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void translationListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_translationListKeyPressed
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER && evt.getModifiers() == 0) {
            insertTranslation();
        }
    }//GEN-LAST:event_translationListKeyPressed

    private void insertTranslation() {
        assert EventQueue.isDispatchThread();
        final Translation translation = translationList.getSelectedValue();
        if (translation == null) {
            return;
        }
        if (textComponent == null) {
            return;
        }
        final Document document = textComponent.getDocument();
        if (document == null) {
            return;
        }

        NbDocument.runAtomic((StyledDocument) document, new Runnable() {
            @Override
            public void run() {
                int selectionStart = textComponent.getSelectionStart();
                int selectionEnd = textComponent.getSelectionEnd();
                try {
                    int removeLength = selectionEnd - selectionStart;
                    if (removeLength > 0) {
                        document.remove(selectionStart, removeLength);
                    }
                    document.insertString(selectionStart, translation.getTranslatedText(), null);
                } catch (BadLocationException ex) {
                    LOGGER.log(Level.WARNING, "Invalid offset:" + ex.offsetRequested(), ex); // NOI18N
                }
            }
        });
        closeDialog();
    }

    private void targetTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_targetTextFieldKeyPressed
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER && evt.getModifiers() == 0) {
            // insert the selected translation
            insertTranslation();
        } else if (evt.isShiftDown()) {
            if (keyCode == KeyEvent.VK_ENTER) {
                updateTranslations();
            } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {
                moveSelectedCasing(keyCode);
            }
        } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_UP) {
            moveSelectedTranslation(keyCode);
        }
    }//GEN-LAST:event_targetTextFieldKeyPressed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        closeDialog();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void translationListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_translationListMouseClicked
        insertTranslation();
    }//GEN-LAST:event_translationListMouseClicked

    @NbBundle.Messages("CodicTranslateDialog.no.fullwidth.char=There is no fullwidth charactor.")
    private void updateTranslations() {
        assert EventQueue.isDispatchThread();
        if (textComponent == null) {
            return;
        }
        String target = getTargetText();
        if (target.isEmpty()) {
            return;
        }
        if (!CodicUtils.containsFullwidth(target)) {
            UiUtils.showWarningDialog(Bundle.CodicTranslateDialog_no_fullwidth_char());
            return;
        }
        try {
            final Codic codic = CodicFactory.getDefault(textComponent);
            final GetTranslationsParams params = new GetTranslationsParams(target)
                    .casing(getSelectedCasing())
                    .projectId(codic.getProjectId());
            RequestProcessor rp = Codic.getRequestProcessor();
            rp.submit(new UpdateTranslationTask(codic, params));
        } catch (IllegalCodicConfigException ex) {
            if (ex.getType() == IllegalCodicConfigException.ConfigType.Global) {
                UiUtils.showOptionsDialog();
            } else {
                UiUtils.showCustomizer(textComponent);
            }
        }
    }

    private void moveSelectedTranslation(int keycode) {
        assert EventQueue.isDispatchThread();
        switch (keycode) {
            case KeyEvent.VK_DOWN: // fall-through
            case KeyEvent.VK_UP:
                int currentIndex = translationList.getSelectedIndex();
                int size = translationListModel.getSize();
                if (size > 0) {
                    int nextIndex = keycode == KeyEvent.VK_DOWN ? ++currentIndex : --currentIndex;
                    if (nextIndex < 0) {
                        nextIndex += size;
                    }
                    nextIndex = nextIndex % size;
                    Translation nextTranslation = translationListModel.get(nextIndex);
                    translationList.setSelectedValue(nextTranslation, true);
                }
                break;
            default:
            // noop
        }
    }

    private void moveSelectedCasing(int keycode) {
        assert EventQueue.isDispatchThread();
        switch (keycode) {
            case KeyEvent.VK_DOWN: // fall-through
            case KeyEvent.VK_UP:
                int currentIndex = casingComboBox.getSelectedIndex();
                int size = casingComboBoxModel.getSize();
                if (size > 0) {
                    int nextIndex = keycode == KeyEvent.VK_DOWN ? ++currentIndex : --currentIndex;
                    if (nextIndex < 0) {
                        nextIndex += size;
                    }
                    nextIndex = nextIndex % size;
                    casingComboBox.setSelectedIndex(nextIndex);
                }
                break;
            default:
            // noop
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Casing> casingComboBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField targetTextField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JList<Translation> translationList;
    private javax.swing.JScrollPane translationScrollPane;
    // End of variables declaration//GEN-END:variables

    private class UpdateTranslationTask implements Runnable {

        private final Codic codic;
        private final GetTranslationsParams params;

        public UpdateTranslationTask(Codic codic, GetTranslationsParams params) {
            this.codic = codic;
            this.params = params;
        }

        @Override
        public void run() {
            final List<Translation> translations = codic.getTranslations(params);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setTranslations(translations);
                }
            });
        }
    }

    //~ inner classes
    private static final class AWTEventLisnerImpl implements AWTEventListener {

        @Override
        public void eventDispatched(AWTEvent event) {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                if (mouseEvent.getID() == MouseEvent.MOUSE_CLICKED && mouseEvent.getClickCount() > 0) {
                    Object source = mouseEvent.getSource();
                    if (source instanceof Component) {
                        Component component = (Component) source;
                        Container container = SwingUtilities.getAncestorNamed(DIALOG_NAME, component);
                        if (container != null) {
                            // clicked on CodicTranslateDialog
                            return;
                        }
                    }
                    closeDialog();
                }
            }
        }
    }

    private static class CloseAction extends AbstractAction {

        private static final long serialVersionUID = 4611696748574164073L;

        @Override
        public void actionPerformed(ActionEvent e) {
            closeDialog();
        }

    }

    private static class TranslationListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -1068576834112122282L;

        private final ListCellRenderer renderer;

        public TranslationListCellRenderer(ListCellRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String text = null;
            if (value instanceof Translation) {
                Translation translation = (Translation) value;
                text = translation.getTranslatedText();
            } else if (value instanceof Casing) {
                Casing casing = (Casing) value;
                text = casing.getSimpleDisplay();
            }
            if (text == null) {
                text = " "; // NOI18N
            }
            JLabel label = (JLabel) renderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            return label;
        }

    }

    private static class TranslationImpl implements Translation {

        private final Translation base;
        private final Casing casing;

        public TranslationImpl(Translation parent, Casing casing) {
            this.base = parent;
            this.casing = casing;
        }

        @Override
        public boolean isSuccess() {
            return base.isSuccess();
        }

        @Override
        public String getText() {
            return base.getText();
        }

        @Override
        public String getTranslatedText() {
            String translatedText = base.getTranslatedText();
            return CodicUtils.convertTo(translatedText, casing);
        }

        @Override
        public String getTranslatedTextInCasing() {
            String translatedTextInCasing = base.getTranslatedTextInCasing();
            return CodicUtils.convertTo(translatedTextInCasing, casing);
        }

        @Override
        public List<Word> getWords() {
            return base.getWords();
        }

    }
}
