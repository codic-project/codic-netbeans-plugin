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

import java.util.Collections;
import java.util.List;
import jp.codic.plugins.netbeans.client.Translation;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams;
import jp.codic.plugins.netbeans.client.params.GetTranslationsParams.Casing;
import jp.codic.plugins.netbeans.codic.Codic;
import org.apache.commons.lang.StringUtils;
import org.openide.modules.ModuleInfo;
import org.openide.modules.Modules;

/**
 *
 * @author junichi11
 */
public final class CodicUtils {

    private static String VERSION;
    private static final String USER_AGENT_FORMAT = "Codic NetBeans Plugin/%s"; // NOI18N
    private static final String CODE_NAME_BASE = "jp.codic.plugins.netbneas"; // NOI18N

    private CodicUtils() {
    }

    public static List<Translation> getTranslations(Codic codic, String selectedText) {
        if (selectedText == null || selectedText.isEmpty() || !containsFullwidth(selectedText)) {
            return Collections.emptyList();
        }
        // params
        GetTranslationsParams params = new GetTranslationsParams(selectedText);
        GetTranslationsParams.Casing casing = codic.getCasing();
        if (casing != GetTranslationsParams.Casing.None) {
            params.casing(casing);
        }
        long projectId = codic.getProjectId();
        if (projectId > 0) {
            params.projectId(projectId);
        }

        return codic.getTranslations(params);
    }

    /**
     * Check whether specified text contains a fullwidth character.
     *
     * @param text
     * @return {@code true} if text has a fullwith character, otherwise
     * {@code false}
     */
    public static boolean containsFullwidth(String text) {
        return text.getBytes().length != text.length();
    }

    public static String convertTo(String defaultText, Casing casing) {
        switch (casing) {
            case Camel:
                return CodicUtils.toCamelCase(defaultText);
            case Hyphen:
                return CodicUtils.toHyphenCase(defaultText);
            case LowerUnderscore:
                return CodicUtils.toLowerUnderscoreCase(defaultText);
            case UpperUnderscore:
                return CodicUtils.toUpperUnderscoreCase(defaultText);
            case Pascal:
                return CodicUtils.toPascalCase(defaultText);
            default:
                return defaultText;
        }
    }

    /**
     * Convert from default case to pascal case.
     *
     * @param text
     * @return pascal case text
     */
    public static String toPascalCase(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        String[] words = toWords(text);
        for (String word : words) {
            sb.append(StringUtils.capitalize(word));
        }
        return sb.toString();
    }

    /**
     * Convert from default case to camel case.
     *
     * @param text
     * @return camel case text
     */
    public static String toCamelCase(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        String[] words = toWords(text);
        boolean first = true;
        for (String word : words) {
            sb.append(first ? word.toLowerCase() : StringUtils.capitalize(word));
            if (first) {
                first = false;
            }
        }
        return sb.toString();
    }

    /**
     * Convert from default case to hyphen case.
     *
     * @param text
     * @return hyphen case text
     */
    public static String toHyphenCase(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        String[] words = toWords(text);
        boolean first = true;
        for (String word : words) {
            if (first) {
                first = false;
            } else {
                sb.append("-"); // NOI18N
            }
            sb.append(word);
        }
        return sb.toString();
    }

    public static String toLowerUnderscoreCase(String text) {
        return toSnakeCase(text, true);
    }

    public static String toUpperUnderscoreCase(String text) {
        return toSnakeCase(text, false);
    }

    /**
     * Convert from default case to snake case.
     *
     * @param text
     * @param lower {@code true} if lowercase is used, otherwise {@code false}
     * @return snake case text
     */
    public static String toSnakeCase(String text, boolean lower) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        String[] words = toWords(text);
        boolean first = true;
        for (String word : words) {
            if (first) {
                first = false;
            } else {
                sb.append("_");  // NOI18N
            }
            sb.append(lower ? word.toLowerCase() : word.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * Split the specific text by white space. (e.g. "some apple" -> ["some",
     * "apple"])
     *
     * @param text separated text with white space
     * @return words
     */
    private static String[] toWords(String text) {
        if (text == null) {
            return new String[0];
        }
        return text.split(" "); // NOI18N
    }

    public static String getUserAgent() {
        return String.format(USER_AGENT_FORMAT, getVersion());
    }

    public static String getVersion() {
        if (VERSION == null) {
            ModuleInfo info = Modules.getDefault().findCodeNameBase(CODE_NAME_BASE);
            VERSION = info.getSpecificationVersion().toString();
        }
        return VERSION;
    }

}
