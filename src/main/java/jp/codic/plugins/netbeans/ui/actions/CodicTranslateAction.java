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
package jp.codic.plugins.netbeans.ui.actions;

import java.awt.Point;
import java.awt.Rectangle;
import jp.codic.plugins.netbeans.codic.Codic;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import jp.codic.plugins.netbeans.client.Translation;
import jp.codic.plugins.netbeans.ui.CodicTranslateDialog;
import jp.codic.plugins.netbeans.utils.CodicUtils;
import org.netbeans.api.editor.EditorActionRegistration;

@EditorActionRegistration(name = "codic-translate")
public class CodicTranslateAction extends CodicActionBase {

    private static final long serialVersionUID = -858190259444458416L;
    private static final Logger LOGGER = Logger.getLogger(CodicTranslateAction.class.getName());

    @Override
    protected void actionPerform(ActionEvent event, final JTextComponent textComponent, final Codic codic) {
        final String selectedText = getSelectedText();
        RP.post(new Runnable() {
            @Override
            public void run() {
                final List<Translation> translations = CodicUtils.getTranslations(codic, selectedText);
                // show dialog
                try {
                    Rectangle rectangle = textComponent.modelToView(textComponent.getCaretPosition());
                    final Point point = new Point(rectangle.x, rectangle.y);
                    SwingUtilities.convertPointToScreen(point, textComponent);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            CodicTranslateDialog.showDialog(point.x, point.y, translations, textComponent, codic.getCasing());
                        }
                    });
                } catch (BadLocationException ex) {
                    LOGGER.log(Level.WARNING, "incorrect position:{0}", ex.offsetRequested()); // NOI18N
                }
            }
        });

    }

}
