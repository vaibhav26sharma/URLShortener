package com.java.urlshortener.urlshortener.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class will have the logic of obtaining a unique ID
 * for our shortened URL as well as converting the unique URL
 * back to a dictionary key which will map to original URL
 * <p>
 * Dictionary will store, unique ID as key and original URL as value
 */
public class IDConverter {
    public static final IDConverter INSTANCE = new IDConverter();

    private IDConverter() {
        initializeCharToIndexTable();
        initiailzeIndextoCharTable();
    }

    //This will store Base62 table representation
    //of characters to values, used while decoding the characters
    //in shortened url to its key.
    private static HashMap<Character, Integer> chartoIndexTable;

    //Represents the conversion chart in the base10 to base62 ,
    // used while converting/encoding
    //decimal ID to Base62
    private static List<Character> indexToCharTable;

    private void initializeCharToIndexTable() {
        chartoIndexTable = new HashMap<>();
        //Store 0->a, 1->b......,25->z,......, 52->0, 61->9

        //Small alphabets
        for (int i = 0; i < 26; i++) {
            char c = 'a';
            c += i;
            chartoIndexTable.put(c, i);
        }

        //Capital alphabets
        for (int i = 26; i < 52; i++) {
            char c = 'A';
            c += (i - 26);
            chartoIndexTable.put(c, i);
        }

        //Digits
        for (int i = 52; i < 62; i++) {
            char c = '0';
            c += (i - 52);
            chartoIndexTable.put(c, i);
        }
    }

    private void initiailzeIndextoCharTable() {
        indexToCharTable = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            char c = 'a';
            c += i;
            indexToCharTable.add(c);
        }

        for (int i = 26; i < 52; i++) {
            char c = 'A';
            c += i;
            indexToCharTable.add(c);
        }

        for (int i = 52; i < 62; i++) {
            char c = '0';
            c += i;
            indexToCharTable.add(c);
        }
    }

    /**
     * It takes in an id (base10) and converts it into base62 using convertBase10ToBase62ID().
     * It then converts each component of the base62 number into a character using the
     * indexToCharTable conversion
     *
     * @param id
     * @return
     */
    public static String createUniqueID(Long id) {
        List<Integer> base62ID = convertBase10ToBase62ID(id);
        StringBuilder uniqueURLID = new StringBuilder();
        for (int digit : base62ID) {
            //After conversion to base62,
            //replace digits with its equivalent
            //in base62, using the indexToCharTable
            uniqueURLID.append(indexToCharTable.get(digit));
        }
        return uniqueURLID.toString();
    }

    /**
     * It takes in a unique URL(base62) , breaks & stores URL's each
     * character in a list & converts it into base10 using
     * convertBase62ToBase10ID().
     *
     * @param uniqueID
     * @return
     */
    public static Long getDictionaryKeyFromBase62UniqueID(String uniqueID) {
        List<Character> base62IDs = new ArrayList<>();
        for (int i = 0; i < uniqueID.length(); i++) {
            base62IDs.add(uniqueID.charAt(i));
        }

        Long dictionaryKey = convertBase62ToBase10ID(base62IDs);
        return dictionaryKey;
    }

    private static List<Integer> convertBase10ToBase62ID(Long id) {
        List<Integer> digits = new LinkedList<>();
        while (id > 0) {
            int remainder = (int) (id % 62);
            //It will follow LIFO to create correct base62 expression, hence addFirst()
            ((LinkedList<Integer>) digits).addFirst(remainder);
            //Quotient
            id /= 62;
        }
        return digits;
    }

    private static Long convertBase62ToBase10ID(List<Character> ids) {
        long id = 0L;
        for (int i = 0, exponent = ids.size() - 1; i < ids.size(); ++i, --exponent) {
            int base10 = chartoIndexTable.get(ids.get(i));
            //1*62^3 + 2*62^1 + 1*62^0
            id += (base10 * Math.pow(62.0, exponent));
        }
        return id;
    }

}
