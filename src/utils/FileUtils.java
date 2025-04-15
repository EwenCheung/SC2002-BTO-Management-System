package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import static utils.Constants.DELIMITER;
import static utils.Constants.DELIMITER_REGEX;

public class FileUtils {
    private static final String DATASET_PATH = "Datasets/";

    public static List<String[]> readFile(String fileName) {
        List<String[]> data = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATASET_PATH + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Use the escaped delimiter pattern for regex operations
                data.add(line.split(DELIMITER_REGEX));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        
        return data;
    }

    public static boolean writeFile(String fileName, List<String[]> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATASET_PATH + fileName))) {
            for (String[] row : data) {
                writer.write(String.join(DELIMITER, row));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateFile(String fileName, int rowIndex, int colIndex, String value) {
        List<String[]> data = readFile(fileName);
        if (rowIndex < 0 || rowIndex >= data.size()) {
            return false;
        }

        String[] row = data.get(rowIndex);
        if (colIndex < 0 || colIndex >= row.length) {
            return false;
        }

        row[colIndex] = value;
        return writeFile(fileName, data);
    }

    /**
     * Returns a string consisting of the specified character repeated count times.
     * This is a compatibility method for Java 8 (replacing String.repeat() from Java 11+).
     * 
     * @param c     The character to repeat
     * @param count The number of times to repeat
     * @return      A string with the character repeated the specified number of times
     */
    public static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
