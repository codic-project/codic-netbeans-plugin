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
package jp.codic.plugins.netbeans.client;

import org.netbeans.api.annotations.common.CheckForNull;

/**
 *
 * @author junichi11
 */
public class CodicAPIException extends Exception {

    private static final long serialVersionUID = -4943334769504374318L;
    private int code;
    private CodicErrorResponse errorResponse;

    /**
     * Creates a new instance of <code>CodicAPIException</code> without detail
     * message.
     */
    public CodicAPIException() {
    }

    public CodicAPIException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of <code>CodicAPIException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CodicAPIException(String msg) {
        super(msg);
    }

    public CodicAPIException(String message, int code) {
        super(message);
        this.code = code;
    }

    public CodicAPIException(CodicErrorResponse errorResponse) {
        super(errorResponse.toString());
        for (CodicError error : errorResponse.getErrors()) {
            this.code = error.getCode();
            break;
        }
        this.errorResponse = errorResponse;
    }

    public int getCode() {
        return code;
    }

    @CheckForNull
    public CodicErrorResponse getErrorResponse() {
        return errorResponse;
    }

}
