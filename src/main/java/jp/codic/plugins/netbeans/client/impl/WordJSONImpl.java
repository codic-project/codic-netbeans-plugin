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

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jp.codic.plugins.netbeans.client.Candidate;
import jp.codic.plugins.netbeans.client.Word;
import jp.codic.plugins.netbeans.client.utils.StringUtils;

public class WordJSONImpl implements Word {

    private boolean successfull;
    private String text;
    @SerializedName("translated_text")
    private String translatedText;
    private List<CandidateJSONImpl> candidates;

    @Override
    public boolean isSuccess() {
        return successfull;
    }

    @Override
    public String getText() {
        return StringUtils.toNonNull(text);
    }

    @Override
    public String getTranslatedText() {
        return StringUtils.toNonNull(translatedText);
    }

    @Override
    public List<Candidate> getCandidates() {
        if (candidates == null) {
            return Collections.emptyList();
        }
        return new ArrayList<Candidate>(candidates);
    }

}
