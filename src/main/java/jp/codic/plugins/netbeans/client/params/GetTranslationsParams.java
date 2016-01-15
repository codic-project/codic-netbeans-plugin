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
package jp.codic.plugins.netbeans.client.params;

import org.openide.util.Pair;

public class GetTranslationsParams extends Params {

    public enum Casing {
        None("", ""), // NOI18N
        Camel("camel", "aA"), // NOI18N
        Hyphen("hyphen", "a-a"), // NOI18N
        LowerUnderscore("lower underscore", "a_a"), // NOI18N
        Pascal("pascal", "AA"), // NOI18N
        UpperUnderscore("upper underscore", "A_A"), // NOI18N
        ;

        private final String caseName;
        private final String simpleDisplay;

        private Casing(String caseName, String simpleDisplay) {
            this.caseName = caseName;
            this.simpleDisplay = simpleDisplay;
        }

        @Override
        public String toString() {
            return caseName;
        }

        public String getCasingName() {
            return caseName;
        }

        public String getSimpleDisplay() {
            return simpleDisplay;
        }

        public static Casing valueOfString(String caseName) {
            if (caseName != null && !caseName.isEmpty()) {
                for (Casing value : values()) {
                    if (value.toString().equals(caseName)) {
                        return value;
                    }
                }
            }
            return None;
        }
    }

    public enum AcronymStyle {
        MSNaming("MS naming"), // NOI18N
        Guidelines("guidelines"), // NOI18N
        CamelStrict("camel strict"), // NOI18N
        Literal("literal"), // NOI18N
        None(""); // NOI18N

        private final String styleName;

        private AcronymStyle(String styleName) {
            this.styleName = styleName;
        }

        @Override
        public String toString() {
            return styleName;
        }

    }

    private final String text;
    private long id;
    private Casing casing = Casing.None;
    private AcronymStyle acronymStyle = AcronymStyle.None;

    public GetTranslationsParams(String text) {
        this.text = text;
    }

    public GetTranslationsParams projectId(long id) {
        this.id = id;
        return this;
    }

    public GetTranslationsParams casing(Casing casing) {
        this.casing = casing;
        return this;
    }

    public GetTranslationsParams acronymStyle(AcronymStyle acronymStyle) {
        this.acronymStyle = acronymStyle;
        return this;
    }

    @Override
    void buildParameters() {
        parameters.clear();
        parameters.add(Pair.of("text", text));  // NOI18N
        if (id > 0) {
            parameters.add(Pair.of("project_id", String.valueOf(id))); // NOI18N
        }
        if (casing != Casing.None) {
            parameters.add(Pair.of("casing", casing.toString())); // NOI18N
        }
        if (casing == Casing.Camel || casing == Casing.Pascal) {
            if (acronymStyle != AcronymStyle.None) {
                parameters.add(Pair.of("acronym_style", acronymStyle.toString())); // NOI18N
            }
        }
    }

}
