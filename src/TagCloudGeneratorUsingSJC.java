import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * This program takes a input of a file that has text, and generates a HTML page
 * that shows the words in tag cloud format in order.
 *
 * @author Abhayjeet S., Wesam K., Pravin H.
 */
public final class TagCloudGeneratorUsingSJC {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private TagCloudGeneratorUsingSJC() {
        // no code needed here
    }

    /**
     * Min number of occurrence of a word.
     */
    private static int minOccur = 0;
    /**
     * Max number of occurrence of a word.
     */
    private static int maxOccur = 0;

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        //Creating output and input streams
        Scanner in = new Scanner(System.in);

        //Asking for input file
        System.out.print("Please enter the name of the file "
                + "(include folder name, file name, and extension): ");

        String fileName = in.nextLine();

        //Creating a input stream from the input file
        BufferedReader inputFileReader = null;
        try {
            inputFileReader = new BufferedReader(new FileReader(fileName));
        } catch (IOException e) {
            in.close();
            System.err.println("Error: Unable to read file.");
            return;
        }

        //Asking for output file
        System.out.print("Please enter the name of the output file "
                + "(include folder name, file name, and extension): ");

        String outputFileName = in.nextLine();

        PrintWriter outputFileWriter = null;

        try {
            outputFileWriter = new PrintWriter(
                    new BufferedWriter(new FileWriter(outputFileName)));
        } catch (IOException e) {
            in.close();
            inputFileReader.close();
            System.err.println("Error: Unable to write to file.");
            return;
        }

        //Asking for the size tag cloud
        System.out.print("Please enter the size of the tag cloud: ");
        int size = in.nextInt();

        //Creating set for separators and using method to extract
        final String separatorsStr = " \t\n\r,-.!?[]';:/()";
        Set<Character> separatorSet = new HashSet<Character>();
        generateElements(separatorsStr, separatorSet);

        //Creating map for words and count
        //Calling method to update them based on the input file
        Map<String, Integer> wordsAndCount = new HashMap<>();
        updateMap(wordsAndCount, separatorSet, inputFileReader);

        //Creating a list for number ordering.
        Comparator<Map.Entry<String, Integer>> numComp = new NumberLT();
        List<Map.Entry<String, Integer>> wordsCountDesending = new ArrayList<>(
                wordsAndCount.entrySet());

        //Sorting for numerical ordering.
        wordsCountDesending.sort(numComp);

        //Extracting top words as specified and updating min and max
        //Note: wordsCountDesending is not restored
        Map<String, Integer> topWords = new HashMap<>();
        int index = 0;
        while (index < size && wordsCountDesending.size() > 0) {
            Map.Entry<String, Integer> pair = wordsCountDesending.remove(0);

            //Decreasing order thus first is max and last is min
            if (index == 0) {
                maxOccur = pair.getValue();
            } else if (index == size - 1) {
                minOccur = pair.getValue();
            }

            //Adding to top words map
            topWords.put(pair.getKey(), pair.getValue());
            index++;
        }

        //Creating a list for alphabetical ordering of the top words.
        Comparator<Map.Entry<String, Integer>> strComp = new StringLT();
        List<Map.Entry<String, Integer>> wordsAlphabetical = new ArrayList<>(
                topWords.entrySet());

        //Sorting for alphabetical.
        wordsAlphabetical.sort(strComp);

        //Calling method to create HTML page
        createPage(wordsAlphabetical, fileName, outputFileWriter, size);

        //Telling user the program is done
        System.out.print("Valid HTML pages have been generated and the program "
                + "has termainted.");

        //Closing input stream
        in.close();
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param separatorSet
     *            the {@code HashSet} to be replaced
     * @replaces charSet
     * @ensures charSet = entries(str)
     */
    private static void generateElements(String str,
            Set<Character> separatorSet) {
        assert str != null : "Violation of: str is not null";
        assert separatorSet != null : "Violation of: charSet is not null";

        //Staring position
        int position = 0;

        //Entering while loop until all separators are covered
        while (position < str.length()) {

            //Getting character
            char x = str.charAt(position);

            //If separatorSet doesn't contain the character adding it
            if (!separatorSet.contains(x)) {
                separatorSet.add(x);
            }

            position++;
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separatorSet
     *            the {@code HashSet} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separatorSet) {

        //Declaring a variable for final index
        int finalPosition = position;

        //Checking if the current index is a separator
        if (separatorSet.contains(text.charAt(position))) {

            //Entering while loop until separators are extracted or length is
            //crossed, and incrementing final index
            while (finalPosition < text.length()
                    && separatorSet.contains(text.charAt(finalPosition))) {
                finalPosition++;
            }

            //Else if the current index is a letter
        } else {

            //Entering while loop until letters are extracted or length is
            //crossed, and incrementing final index
            while (finalPosition < text.length()
                    && !separatorSet.contains(text.charAt(finalPosition))) {
                finalPosition++;
            }
        }

        //Getting a substring from initial index to final index
        String nextStr = text.substring(position, finalPosition);

        return nextStr;
    }

    /**
     * Method updates map that contains words and their count.
     *
     * @param wordsAndCount
     *            Map that contains words (key) and their count (value)
     * @param separatorSet
     *            Set that contains possible separators in text
     * @param in
     *            String that contains the name of the input file
     * @throws IOException
     * @updates wordsAndCount
     * @requires charSet all possible separators
     * @ensures <pre>
     * wordsAndCount contains entries as keys and associated counts as values.
     *  </pre>
     */
    private static void updateMap(Map<String, Integer> wordsAndCount,
            Set<Character> separatorSet, BufferedReader in) throws IOException {

        //Getting first line
        String text = in.readLine();

        //Entering loop until at the end of file
        while (text != null) {

            //Declaring a index variable for the line
            int position = 0;

            //Entering loop until index variable equals text length
            while (position < text.length()) {

                //Calling method to get word or separators
                String wordOrSeparator = nextWordOrSeparator(text, position,
                        separatorSet);

                //Updating index variable
                position += wordOrSeparator.length();

                //Converting to lower case to avoid repeated entries in map
                wordOrSeparator = wordOrSeparator.toLowerCase();

                //Checking if the string extracted is a word by checking first
                //character
                if (!separatorSet.contains(wordOrSeparator.charAt(0))) {

                    //If Map has the word updating count (value)
                    if (wordsAndCount.containsKey(wordOrSeparator)) {
                        int count = wordsAndCount.get(wordOrSeparator);
                        count++;
                        wordsAndCount.replace(wordOrSeparator, count);

                        //Else adding word to Map with word (key) and
                        //count (value)
                    } else {
                        wordsAndCount.put(wordOrSeparator, 1);
                    }
                }
            }

            //Getting next line
            text = in.readLine();
        }
        //Closing input stream
        in.close();
    }

    /**
     * Method generates valid HTML format page for the words and their counts.
     *
     * @param wordsAlpha
     *            List that contains words (key) and their count (value)
     * @param inputFile
     *            String that contains the name of the input file
     * @param out
     *            PrintWriter that will write to the output file
     * @param size
     *            Size of the tag cloud
     * @clears wordsAlpha
     * @ensures <pre>
     * Valid HTML page will be generated and will be saved to location provided
     * by outputFileName.
     * </pre>
     */
    private static void createPage(List<Map.Entry<String, Integer>> wordsAlpha,
            String inputFile, PrintWriter out, int size) {

        // Prints HTML headers
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + "Top " + size + " words in " + inputFile
                + "</title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/"
                        + "assignments/projects/tag-cloud-generator/data/tagcloud.css\" "
                        + "rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println(
                "<h2>" + "Top " + size + " words in " + inputFile + "</h2>");
        out.println("<hr>");
        out.println("<div class=\"cdiv\">");
        out.println("<p class=\"cbox\">");

        //Printing Words
        //Note: wordsAlpha is no longer restored (updated contract)
        while (wordsAlpha.size() > 0) {
            Map.Entry<String, Integer> pair = wordsAlpha.remove(0);
            out.println("<span style=\"cusor:default\" class=\"f"
                    + fontSizer(pair.getValue()) + "\" title=\"count:"
                    + pair.getValue() + "\">" + pair.getKey().toLowerCase()
                    + "</span>");
        }

        // Outputs closing tags of generated HTML file
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    /**
     * Returns the font size using linear conversions for any cases that are not
     * edge.
     *
     * @param occurs
     *            number of occurrences of the word
     * @return fontSize number used as font size
     */
    private static int fontSizer(int occurs) {
        //Range according to CSS
        final int maxFont = 48;
        final int minFont = 11;

        //Variable to hold font
        int fontSize = minFont;

        //Edge cases
        if (occurs == minOccur) {
            fontSize = minFont;
        } else if (occurs == maxOccur) {
            fontSize = maxFont;
        } else {
            //Using Linear Conversion

            // Calculate proportions
            double occurRange = maxOccur - minOccur;
            double fontRange = maxFont - minFont;
            double scaled = (occurs - minOccur) / occurRange;

            // Apply the linear equation
            fontSize = (int) (scaled * fontRange + minFont);
        }

        return fontSize;
    }

    /**
     * Returns zero if strings are equal. Negative if s1 comes first not s2,
     * which is correct order. Positive integer if s2 comes first not s1, which
     * is not the correct order. Used to sort strings alphabetically.
     */
    private static class StringLT
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> s1,
                Map.Entry<String, Integer> s2) {
            return (s1.getKey().compareToIgnoreCase(s2.getKey()));
        }
    }

    /**
     * Returns zero if integers are equal. Negative if i2 comes first not i1,
     * which is correct order. Positive integer if i1 comes first not i2, which
     * is not the correct order. Used to sort integers decreasing order.
     */
    private static class NumberLT
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> i1,
                Map.Entry<String, Integer> i2) {
            return (i2.getValue().compareTo(i1.getValue()));
        }
    }
}
