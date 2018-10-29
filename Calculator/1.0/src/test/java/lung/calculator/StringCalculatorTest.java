package lung.calculator;

import lung.calculator.exception.NegativeNumberException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author WAN, Kwok Lung
 */
public class StringCalculatorTest {

    @Test
    public void givenExample() {
        Assert.assertEquals(1, StringCalculator.add("1"));
    }

    @Test
    public void givenExample2() {
        Assert.assertEquals(3, StringCalculator.add("1,2"));
    }

    @Test
    public void givenExample3() {
        Assert.assertEquals(6, StringCalculator.add("1\n2,3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenExample4() {
        StringCalculator.add("1,\n");
    }

    @Test
    public void givenExample5() {
        Assert.assertEquals(3, StringCalculator.add("//;\n1;2"));
    }

    @Test
    public void givenExample6() {
        Assert.assertEquals(6, StringCalculator.add("//***\n1***2***3"));
    }

    @Test
    public void givenExample7() {
        Assert.assertEquals(6, StringCalculator.add("//*|%\n1*2%3"));
    }

    @Test(expected = NullPointerException.class)
    public void whenInputIsNull() {
        StringCalculator.add(null);
    }

    @Test
    public void whenInputIsEmpty() {
        Assert.assertEquals(0, StringCalculator.add(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidInput() {
        StringCalculator.add("a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidInput2() {
        StringCalculator.add("//");
    }

    @Test
    public void singleNumber() {
        Assert.assertEquals(0, StringCalculator.add("0"));
        Assert.assertEquals(5, StringCalculator.add("5"));
        Assert.assertEquals(1000, StringCalculator.add("1000"));

        // Numbers larger than 1000 should be regarded as 0
        Assert.assertEquals(0, StringCalculator.add("1001"));

        // Negative numbers would throw NegativeNumberException
        assertThrowNegativeNumberException("-1");
        assertThrowNegativeNumberException("-1000");
        assertThrowNegativeNumberException("-1001");
    }

    public boolean isThrowNegativeNumberException(String numbers) {
        try {
            StringCalculator.add(numbers);
        } catch (NegativeNumberException e) {
            return true;
        }
        return false;
    }

    public void assertThrowNegativeNumberException(String numbers) {
        Assert.assertTrue(isThrowNegativeNumberException(numbers));
    }

    public void assertNotThrowNegativeNumberException(String numbers) {
        Assert.assertFalse(isThrowNegativeNumberException(numbers));
    }

    @Test
    public void singleNumbersInMultipleLines() {
        Assert.assertEquals(5, StringCalculator.add("0\n5"));
        Assert.assertEquals(1010, StringCalculator.add("0\n10\n1000"));
        Assert.assertEquals(1025, StringCalculator.add("0\n10\n15\n1000"));

        // Numbers larger than 1000 should be regarded as 0
        Assert.assertEquals(1000, StringCalculator.add("0\n1000"));
        Assert.assertEquals(1000, StringCalculator.add("0\n1000\n1500"));
        Assert.assertEquals(1005, StringCalculator.add("0\n1000\n1500\n2000\n5"));

        // NegativeNumberException should be thrown if any negative number is found.
        assertThrowNegativeNumberException("0\n-5");
        assertThrowNegativeNumberException("0\n-10\n1000");
        assertThrowNegativeNumberException("0\n10\n-1000");
        assertThrowNegativeNumberException("0\n-10\n-1000");
        assertThrowNegativeNumberException("0\n-1000\n1500\n2000\n5");
        assertThrowNegativeNumberException("0\n1000\n-1500\n2000\n5");
        assertThrowNegativeNumberException("0\n1000\n1500\n-2000\n5");
        assertThrowNegativeNumberException("0\n1000\n1500\n2000\n-5");
        assertNotThrowNegativeNumberException("-0");
        assertNotThrowNegativeNumberException("-0\n5");
    }

    @Test
    public void singleNumberWithSingleDelimiter() {
        Assert.assertEquals(0, StringCalculator.add("//,\n0"));
        Assert.assertEquals(5, StringCalculator.add("//.\n5"));
        Assert.assertEquals(1000, StringCalculator.add("//]\n1000"));

        // Numbers larger than 1000 should be regarded as 0
        Assert.assertEquals(0, StringCalculator.add("//,\n1001"));
        Assert.assertEquals(0, StringCalculator.add("//.\n1001"));
        Assert.assertEquals(0, StringCalculator.add("//]\n1001"));

        // NegativeNumberException should be thrown if any negative number is found.
        assertThrowNegativeNumberException("//.\n-5");
        assertThrowNegativeNumberException("//.\n-1005");
    }

    @Test
    public void singleNumberWithMultipleDelimiters() {
        Assert.assertEquals(0, StringCalculator.add("//,|.\n0"));
        Assert.assertEquals(5, StringCalculator.add("//~|!|@|#|$|%|^|&|*|(|)|_|+|{|}|:|\"|<|>|?|-|=|[|]|\\|;|'|,|.|/\n5"));
        Assert.assertEquals(1000, StringCalculator.add("//~|!|@|#|$|%|^|&|*|(|)|_|+|{|}|:|\"|<|>|?|-|=|[|]|\\|;|'|,|.|/\n1000"));

        // Numbers larger than 1000 should be regarded as 0
        Assert.assertEquals(0, StringCalculator.add("//~|!|@|#|$|%|^|&|*|(|)|_|+|{|}|:|\"|<|>|?|-|=|[|]|\\|;|'|,|.|/\n1001"));

        // NegativeNumberException should be thrown if any negative number is found.
        assertThrowNegativeNumberException("//~|!|@|#|$|%|^|&|*|(|)|_|+|{|}|:|\"|<|>|?|=|[|]|\\|;|'|,|.|/\n-1001");
    }

    @Test
    public void multipleNumbersWithDelimiter() {
        Assert.assertEquals(1005, StringCalculator.add("//,\n0,5,1000"));

        // Numbers larger than 1000 should be regarded as 0
        Assert.assertEquals(1005, StringCalculator.add("//,\n0,5,1000,1001"));

        // NegativeNumberException should be thrown if any negative number is found.
        assertThrowNegativeNumberException("//,\n0,5,-1000,1001");
    }

    @Test
    public void multipleNumbersWithMultipleDelimiters() {
        Assert.assertEquals(1005, StringCalculator.add("//,|.\n0,5.1000"));
        Assert.assertEquals(31, StringCalculator.add("//~|!|@|#|$|%|^|&|*|(|)|_|+|{|}|:|\"|<|>|?|-|=|[|]|\\|;|'|,|.|/\n1~1!1@1#1$1%1^1&1*1(1)1_1+1{1}1:1\"1<1>1?1-1=1[1]1\\1;1'1,1.1/1"));

        // Numbers larger than 1000 should be regarded as 0
        Assert.assertEquals(29, StringCalculator.add("//~|!|@|#|$|%|^|&|*|(|)|_|+|{|}|:|\"|<|>|?|-|=|[|]|\\|;|'|,|.|/\n1~1!1@1#1$1%1^1&1*1(1)1_1+1{1}1:1\"1<1>1?1-1001=1[1]1\\1;1'1,1001.1/1"));

        // NegativeNumberException should be thrown if any negative number is found.
        assertThrowNegativeNumberException("//~|!|@|#|$|%|^|&|*|(|)|_|+|{|}|:|\"|<|>|?|=|[|]|\\|;|'|,|.|/\n1~1!1@1#1$1%1^1&1*1(1)1_1+1{1}1:1\"1<1>1?1=1[1]1\\1;1'-1,1001.1/1");
    }

    @Test
    public void multipleNumbersWithMultipleDelimitersWithUnusedDelimiters() {
        Assert.assertEquals(35, StringCalculator.add("//,|.|[\n5,10.20"));
    }

    @Test
    public void multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers() {
        Assert.assertEquals(35, StringCalculator.add("//,|,,\n5,10,,20"));
    }

    @Test
    public void multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers2() {
        Assert.assertEquals(35, StringCalculator.add("//,|,,\n5,,10,20"));
    }

    @Test
    public void multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers3() {
        Assert.assertEquals(35, StringCalculator.add("//,,|,\n5,10,,20"));
    }

    @Test
    public void multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers4() {
        Assert.assertEquals(35, StringCalculator.add("//,,|,\n5,,10,20"));
    }

}
