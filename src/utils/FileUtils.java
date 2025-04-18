package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import static utils.Constants.DELIMITER;
import static utils.Constants.DELIMITER_REGEX;
import static utils.Constants.QUOTE;

public class FileUtils {
    private static final String DATASET_PATH = "Datasets/";

    public static List<String[]> readFile(String fileName) {
        List<String[]> data = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATASET_PATH + fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split by comma but respect quoted values
                data.add(parseCsvLine(line));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        
        return data;
    }

    /**
     * Parses a CSV line respecting quoted values that may contain commas
     */
    private static String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(cleanToken(currentToken.toString()));
                currentToken.setLength(0);
            } else {
                currentToken.append(c);
            }
        }
        
        // Add the last token
        tokens.add(cleanToken(currentToken.toString()));
        
        return tokens.toArray(new String[0]);
    }

    /**
     * Cleans a token by removing surrounding quotes and trimming whitespace
     */
    private static String cleanToken(String token) {
        token = token.trim();
        if (token.startsWith("\"") && token.endsWith("\"")) {
            token = token.substring(1, token.length() - 1);
        }
        return token;
    }

    public static boolean writeFile(String fileName, List<String[]> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATASET_PATH + fileName))) {
            for (String[] row : data) {
                writer.write(toCsvLine(row));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Converts an array of strings to a CSV line, properly escaping values that contain commas
     */
    private static String toCsvLine(String[] tokens) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            // Quote values that contain commas or quotes
            if (token.contains(",") || token.contains("\"")) {
                token = QUOTE + token.replace("\"", "\"\"") + QUOTE;
            }
            line.append(token);
            if (i < tokens.length - 1) {
                line.append(DELIMITER);
            }
        }
        return line.toString();
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
