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
package jp.codic.plugins.netbeans.utils;

import java.awt.Component;
import java.awt.Container;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import jp.codic.plugins.netbeans.options.CodicOptions;
import jp.codic.plugins.netbeans.ui.customizer.CodicCustomizerProvider;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.project.ui.CustomizerProvider;
import org.netbeans.spi.project.ui.CustomizerProvider2;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class UiUtils {

    private UiUtils() {
    }

    public static void showDialog(String message, int messageType) {
        NotifyDescriptor.Message mess = new NotifyDescriptor.Message(message, messageType);
        DialogDisplayer.getDefault().notify(mess);
    }

    public static void showErrorDialog(String message) {
        showDialog(message, NotifyDescriptor.ERROR_MESSAGE);
    }

    public static void showWarningDialog(String message) {
        showDialog(message, NotifyDescriptor.WARNING_MESSAGE);
    }

    public static void showPlainDialog(String message) {
        showDialog(message, NotifyDescriptor.PLAIN_MESSAGE);
    }

    public static void showOptionsDialog() {
        OptionsDisplayer.getDefault().open(CodicOptions.SUB_PATH);
    }

    public static void showCustomizer(Project project) {
        if (project == null) {
            return;
        }
        CustomizerProvider provider = project.getLookup().lookup(CustomizerProvider.class);
        if (provider != null) {
            if (provider instanceof CustomizerProvider2) {
                ((CustomizerProvider2) provider).showCustomizer(CodicCustomizerProvider.CATEGORY_NAME, null);
                return;
            }
            provider.showCustomizer();
        }
    }

    public static void showCustomizer(JTextComponent textComponent) {
        showCustomizer(getProject(textComponent));
    }

    @CheckForNull
    private static Project getProject(JTextComponent component) {
        if (component == null) {
            return null;
        }
        Document document = component.getDocument();
        if (document == null) {
            return null;
        }
        FileObject fileObject = NbEditorUtilities.getFileObject(document);
        if (fileObject == null) {
            return null;
        }
        return FileOwnerQuery.getOwner(fileObject);
    }

    public static void setAllComponentsEnabled(Container container, boolean isEnabled) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(isEnabled);
            if (component instanceof Container) {
                setAllComponentsEnabled((Container) component, isEnabled);
            }
        }
    }

}
