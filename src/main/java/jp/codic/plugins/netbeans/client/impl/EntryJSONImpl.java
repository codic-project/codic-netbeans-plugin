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
import jp.codic.plugins.netbeans.client.Entry;
import jp.codic.plugins.netbeans.client.EntryTranslation;
import jp.codic.plugins.netbeans.client.Pronunciation;
import jp.codic.plugins.netbeans.client.utils.StringUtils;

public class EntryJSONImpl implements Entry {

    private long id;
    private String title;
    private String digest;
    private List<PronunciationJSONImpl> pronunciations;
    private List<EntryTranslationJSONImpl> translations;
    private String note;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return StringUtils.toNonNull(title);
    }

    @Override
    public String getDigest() {
        return StringUtils.toNonNull(digest);
    }

    @Override
    public List<Pronunciation> getPronunciations() {
        return pronunciations == null ? Collections.<Pronunciation>emptyList() : new ArrayList<Pronunciation>(pronunciations);
    }

    @Override
    public List<EntryTranslation> getTranslations() {
        return translations == null ? Collections.<EntryTranslation>emptyList() : new ArrayList<EntryTranslation>(translations);
    }

    @Override
    public String getNote() {
        return StringUtils.toNonNull(note);
    }

}
