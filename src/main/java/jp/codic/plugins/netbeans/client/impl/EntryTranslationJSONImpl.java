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
package jp.codic.plugins.netbeans.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jp.codic.plugins.netbeans.client.EntryTranslation;
import jp.codic.plugins.netbeans.client.Label;
import jp.codic.plugins.netbeans.client.utils.StringUtils;

public class EntryTranslationJSONImpl implements EntryTranslation {

    private int etymology;
    private String pos;
    private String text;
    private List<LabelJSONImpl> labels;
    private String note;

    @Override
    public int getEtymology() {
        return etymology;
    }

    @Override
    public String getPos() {
        return StringUtils.toNonNull(pos);
    }

    @Override
    public String getText() {
        return StringUtils.toNonNull(text);
    }

    @Override
    public List<Label> getLabels() {
        return labels == null ? Collections.<Label>emptyList() : new ArrayList<Label>(labels);
    }

    @Override
    public String getNote() {
        return StringUtils.toNonNull(note);
    }

}
