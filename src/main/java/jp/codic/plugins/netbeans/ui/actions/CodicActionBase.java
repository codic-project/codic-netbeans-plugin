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

import jp.codic.plugins.netbeans.codic.Codic;
import jp.codic.plugins.netbeans.codic.IllegalCodicConfigException;
import jp.codic.plugins.netbeans.codic.CodicFactory;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import jp.codic.plugins.netbeans.codic.IllegalCodicConfigException.ConfigType;
import jp.codic.plugins.netbeans.utils.UiUtils;
import org.netbeans.editor.BaseAction;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public abstract class CodicActionBase extends BaseAction {

    private String selectedText;
    private static final long serialVersionUID = -296413330455525229L;
    private static final Logger LOGGER = Logger.getLogger(CodicActionBase.class.getName());
    protected static final RequestProcessor RP = new RequestProcessor(CodicActionBase.class);

    @Override
    public void actionPerformed(ActionEvent event, JTextComponent textComponent) {
        selectedText = textComponent.getSelectedText();
        Codic codic = null;
        try {
            codic = CodicFactory.getDefault(textComponent);
        } catch (IllegalCodicConfigException ex) {
            LOGGER.log(Level.INFO, ex.getMessage());
            if (ex.getType() == ConfigType.Global) {
                UiUtils.showOptionsDialog();
            } else {
                UiUtils.showCustomizer(textComponent);
            }
        }
        if (codic == null) {
            return;
        }
        actionPerform(event, textComponent, codic);
    }

    public String getSelectedText() {
        return selectedText;
    }

    protected abstract void actionPerform(ActionEvent event, JTextComponent textComponent, Codic codic);

}
