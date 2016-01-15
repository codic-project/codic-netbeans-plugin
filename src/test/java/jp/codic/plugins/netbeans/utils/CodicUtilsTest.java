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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author junichi11
 */
public class CodicUtilsTest {

    public CodicUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of containsFullwidth method, of class CodicUtils.
     */
    @Test
    public void testContainsFullwidth() {
        assertTrue(CodicUtils.containsFullwidth("ひらがな"));
        assertTrue(CodicUtils.containsFullwidth("カタカナ"));
        assertTrue(CodicUtils.containsFullwidth("漢字"));
        assertTrue(CodicUtils.containsFullwidth("１２３"));
        assertTrue(CodicUtils.containsFullwidth("テスト123abc"));

        assertFalse(CodicUtils.containsFullwidth("abc123-?!"));
    }

    /**
     * Test of toPascalCase method, of class CodicUtils.
     */
    @Test
    public void testToPascalCase() {
        assertEquals(null, CodicUtils.toPascalCase(null));
        assertEquals("", CodicUtils.toPascalCase(""));
        assertEquals("AbcXyz", CodicUtils.toPascalCase("abc xyz"));
        assertEquals("ToPascalCase", CodicUtils.toPascalCase("to pascal case"));
        assertEquals("ABanana", CodicUtils.toPascalCase("a  banana"));
    }

    /**
     * Test of toCamelCase method, of class CodicUtils.
     */
    @Test
    public void testToCamelCase() {
        assertEquals(null, CodicUtils.toCamelCase(null));
        assertEquals("", CodicUtils.toCamelCase(""));
        assertEquals("camel", CodicUtils.toCamelCase("camel"));
        assertEquals("toCamelCase", CodicUtils.toCamelCase("to camel case"));
    }

    /**
     * Test of toHyphenCase method, of class CodicUtils.
     */
    @Test
    public void testToHyphenCase() {
        assertEquals(null, CodicUtils.toHyphenCase(null));
        assertEquals("", CodicUtils.toHyphenCase(""));
        assertEquals("hyphen", CodicUtils.toHyphenCase("hyphen"));
        assertEquals("to-hyphen-case", CodicUtils.toHyphenCase("to hyphen case"));
    }

    /**
     * Test of toLowerUnderscoreCase method, of class CodicUtils.
     */
    @Test
    public void testToLowerUnderscoreCase() {
        assertEquals(null, CodicUtils.toLowerUnderscoreCase(null));
        assertEquals("", CodicUtils.toLowerUnderscoreCase(""));
        assertEquals("lower", CodicUtils.toLowerUnderscoreCase("lower"));
        assertEquals("to_lower_underscore_case", CodicUtils.toLowerUnderscoreCase("to lower underscore case"));
    }

    /**
     * Test of toUpperUnderscoreCase method, of class CodicUtils.
     */
    @Test
    public void testToUpperUnderscoreCase() {
        assertEquals(null, CodicUtils.toUpperUnderscoreCase(null));
        assertEquals("", CodicUtils.toUpperUnderscoreCase(""));
        assertEquals("UPPER", CodicUtils.toUpperUnderscoreCase("upper"));
        assertEquals("TO_UPPER_UNDERSCORE_CASE", CodicUtils.toUpperUnderscoreCase("to upper underscore case"));
    }

}
