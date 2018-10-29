package lung.calculator;

import com.google.common.base.Joiner;
import lung.calculator.exception.NegativeNumberException;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Developed with Java Version: Oracle Java JDK 1.8.0_181
 *
 * @Author WAN, Kwok Lung
 */
public class StringCalculator {

    private static final Logger logger = LoggerFactory.getLogger(StringCalculator.class);

    /**
     * The "add" method implemented as described in the doc.
     *
     * @param numbers
     */
    public static int add(String numbers) {

        // Saves the start timestamp to be used later to calculate the elapsed
        // time.
        final long startTime = System.nanoTime();

        // Initiates Apache log4j
        BasicConfigurator.configure();

        //logger.info("numbers=" + StringEscapeUtils.escapeJava(numbers));

        // Input "numbers" cannot be null.
        Objects.requireNonNull(numbers, "Input cannot be null.");

        // Input "numbers" cannot be an empty String.
        if (numbers.isEmpty()) return 0;

        /*
        Although the document states that "there is no need to check for invalid
        inputs", we check it because the case "1,\n" is mentioned in the doc.

        If I use Pattern.split(CharSequence, -1), the program can keep the
        trailing empty strings and thus I can detect the input is invalid.
        i.e. "1,\n" will be split to ["1,", ""]

        Ref: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html#split-java.lang.CharSequence-int-
        However, Pattern.splitAsStream(CharSequence) has no way to keep trailing
        empty strings, so I have to check it beforehand.
        i.e. "1,\n" will be split to ["1,"]
         */
        if (numbers.charAt(0) == '\n' ||
                numbers.charAt(numbers.length() - 1) == '\n') {
            throw new IllegalArgumentException("Boundary cannot be newlines.");
        }

        // The delimiter parsed from the input "numbers"
        final String delimiter;

        // Default delimiter which is supposed to be "," when no "//" is included
        // in the input to declare delimiters
        final String defaultDelimiter = ",";

        // The number of lines to skip when parsing for numbers.
        // If there is a delimiter line found i.e. //[[delimiter]..]\n,
        // skip will be 1, or otherwise 0.
        int skip = 0;

        // If the delimiter-prefix "//" is found
        if (numbers.charAt(0) == '/' && numbers.charAt(1) == '/') {

            try {
                // Extracts the delimiter declaration line
                // @throw StringIndexOutOfBoundsException
                String firstLine = numbers.substring(2, numbers.indexOf('\n'));

                // Since the delimiter declaration line is found, the first line
                // will be skipped when parsing lines for numbers.
                skip = 1;

                // Pattern.quote is to escape the special characters.
                delimiter = Pattern.compile(Pattern.quote("|"))

                        // Reads in by streaming and split the delimiter
                        // declaration line with "|" to create a stream of
                        // delimiters.
                        .splitAsStream(firstLine)

                        /*
                        Here the found delimiters have to be sorted from the
                        longest to the shortest in order to handle the cases
                        where there are some delimiters which are the
                        substring of others.

                        See: multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers
                        multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers2
                        multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers3
                        multipleNumbersWithMultipleDelimitersWithSomeDelimitersAsSubstringOfOthers4

                        If the delimiters were not sorted,

                        "//,|,,\n5,10,,20" would fail because it will be split as
                        5|10|(empty)|20 because the "10,,20" will first be
                        matched by "," as the delimiter "," is before ",,"

                        "//,,|,\n5,10,,20" would not fail because it will be split as
                        5|10|20 because the "10,,20" will first be
                        matched by ",," as the delimiter ",," is before ","
                         */
                        .sorted(Comparator.comparingInt(String::length).reversed())

                        // For each delimiter found, each is escaped before
                        // joining to form a regex pattern.
                        .map(d -> Pattern.quote(d))

                        // Joins all delimiters by "|" to form a regex pattern.
                        .collect(Collectors.joining("|"));

            } catch (StringIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Invalid format.");
            }

        } else {
            // No delimiter-prefix "//" is found, so the default delimiter is used.
            delimiter = defaultDelimiter;
        }

        /*
        For parallel streaming, I need to use concurrent/synchronized data
        structure.

        CopyOnWriteArrayList is not good because each write would generate a new
        array.

        Collections.synchonizedList(list) is not good because so much writings
        there and performance would be degraded by locking (context switching).

        If I remember correctly, ConcurrentLinkedQueue is the only lock-free
        data structure in Java so far.

        */
        //final ConcurrentLinkedQueue<Integer> negativeNumbers = new ConcurrentLinkedQueue();

        /*
        For serial streaming, I choose LinkedList because data size would
        increase gradually and there is no random position access needed.
         */
        final LinkedList<Integer> negativeNumbers = new LinkedList<>();

        // Build a Pattern to split with "\n"
        int output = Pattern.compile("\n")

                // Reads in by streaming and split the input string with "\n"
                // to create a stream of lines.
                .splitAsStream(numbers)

                //.parallel()

                // If the first line is the delimiter declaration line, there it
                // will skip 1 line, or other skip 0 line.
                .skip(skip)

                // For each line, calculates the sum of all numbers on the line
                // and returns it.
                .mapToInt( (String line) ->

                        // For each line, split the numbers with either the user
                        // defined delimiters or the default delimiter.
                        Pattern.compile(delimiter)

                                // Reads in by streaming and split the line
                                // with the delimiter to create a stream of
                                // number-substring
                                .splitAsStream(line)

                                // For each substring which is supposed to be a
                                // number, it tries to convert it to an int.
                                .mapToInt( (String numString) -> {

                                    // Empty substring
                                    // The doc does not mention how to handle
                                    // empty substrings, here they are just
                                    // ignored. In case, they should be treated
                                    // as errors, set it to if(true) .
                                    if (false) {
                                        if (numString.isEmpty())
                                            throw new NumberFormatException("Number cannot be empty.");
                                    }

                                    // Tries to parse the substring to an int.
                                    // @throw NumberFormatException
                                    int num = Integer.parseInt(numString);

                                    // If it's a negative number, it should be
                                    // treated as 0. Although the doc mentions
                                    // that an exception NegativeNumberException
                                    // should be thrown, I decide not to throw
                                    // here because the doc mentions that ALL
                                    // negative numbers should be printed upon
                                    // the exception, so I need to collect all
                                    // negative numbers first and throw an
                                    // exception at the end after all numbers
                                    // are parsed. Here all negative numbers
                                    // are stored to be printed later.
                                    // 0 is returned.
                                    if (num < 0) {
                                        negativeNumbers.add(num);
                                        return 0;
                                    }

                                    // Numbers larger than 1000 should be
                                    // treated as 0.
                                    if (num > 1000) return 0;

                                    // Returns the num
                                    return num;
                                })

                                // Aggregates and calculates the sum of all
                                // numbers found on the line and returns it
                                .sum()
                )

                // Aggregates and sums up all the sums calculated on each line
                .sum();

        // If negative numbers have been found, they will be printed and
        // @throw NegativeNumberException
        if (negativeNumbers.size() > 0) {
            String joined = Joiner.on(",").join(negativeNumbers.stream().collect(Collectors.toList()));
            throw new NegativeNumberException(joined);
        }

        // Calculates and prints the elapsed time
        final long stopTime = System.nanoTime();
        final long elapsedTime = stopTime - startTime;
        logger.info(String.format("numbers=%s Elapsed:%d", StringEscapeUtils.escapeJava(numbers), elapsedTime));

        // Returns the final answer which is the sum of the numbers found
        return output;
    }
}
