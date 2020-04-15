package org.bitbucket.srbarber1997.configuration.util;

/**
 * Class that is used to scramble strings in various ways.
 * !!! <b>This should not be used instead of encryption or hashing</b> !!!
 * @author srbarber1997
 */
public class Scrambler {

    /**
     * Key used to scramble a string
     */
    private int key = 0;

    /**
     * Constructor used to create an integer key from a string
     */
    public Scrambler(String key) {
        for (char c : key.toCharArray())
            this.key += (short) c;
    }

    /**
     * Method used to scramble a string using a generated key.
     * Works by adding to a char to get a different char
     * @param string to scramble
     * @return scrambled string
     */
    public String scrambleCharacters(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray())
            stringBuilder.append((char) (c + key));

        return stringBuilder.toString();
    }

    /**
     * Method to unscramble a scrambled string. Uses the same key
     * used to first scramble the string to reverse the process.
     * Works by taking away the same integer from the char to get
     * back to the original char.
     * @param string to unscramble
     * @return unscrambled string
     */
    public String unscrambleCharacters(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray())
            stringBuilder.append((char) (c - key));

        return stringBuilder.toString();
    }
}
