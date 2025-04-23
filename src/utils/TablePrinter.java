package utils;

import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for printing formatted tables in CLI applications.
 * This class handles automatic column width calculation and consistent formatting
 * to improve readability of tabular data throughout the application.
 */
public class TablePrinter {
    private final String[] headers;
    private final List<String[]> rows;
    private final int[] columnWidths;
    private final int numColumns;
    private final int padding = 4; // Padding between columns
    
    /**
     * Constructs a new table with the specified column headers.
     * 
     * @param headers Array of column headers that define the table structure
     */
    public TablePrinter(String[] headers) {
        this.headers = headers;
        this.numColumns = headers.length;
        this.rows = new ArrayList<>();
        this.columnWidths = new int[numColumns];
        
        // Initialize column widths with header lengths
        for (int i = 0; i < numColumns; i++) {
            columnWidths[i] = headers[i].length();
        }
    }
    
    /**
     * Adds a row of data to the table.
     * The row must have the same number of columns as the table headers.
     * 
     * @param rowData Array of string values representing one row of data
     * @throws IllegalArgumentException if the row data does not match the number of columns
     */
    public void addRow(String[] rowData) {
        if (rowData.length != numColumns) {
            throw new IllegalArgumentException("Row data must have " + numColumns + " columns");
        }
        
        // Update column widths if needed
        for (int i = 0; i < numColumns; i++) {
            if (rowData[i] != null) {
                columnWidths[i] = Math.max(columnWidths[i], rowData[i].length());
            }
        }
        
        rows.add(rowData);
    }
    
    /**
     * Adds a row of data to the table using varargs for convenience.
     * Automatically converts all objects to strings using their toString() method.
     * 
     * @param rowData Variable number of objects representing one row of data
     * @throws IllegalArgumentException if the number of arguments does not match the number of columns
     */
    public void addRow(Object... rowData) {
        if (rowData.length != numColumns) {
            throw new IllegalArgumentException("Row data must have " + numColumns + " columns");
        }
        
        String[] stringData = new String[numColumns];
        for (int i = 0; i < numColumns; i++) {
            stringData[i] = rowData[i] == null ? "" : rowData[i].toString();
            columnWidths[i] = Math.max(columnWidths[i], stringData[i].length());
        }
        
        rows.add(stringData);
    }
    
    /**
     * Prints the formatted table to the console.
     * Includes headers, a divider line, and all data rows with proper alignment.
     */
    public void print() {
        // Calculate the width of each padded column
        int totalWidth = 0;
        for (int width : columnWidths) {
            totalWidth += width + padding;
        }
        
        // Print headers
        for (int i = 0; i < numColumns; i++) {
            System.out.print(padRight(headers[i], columnWidths[i] + padding));
        }
        System.out.println();
        
        // Print divider
        System.out.println(FileUtils.repeatChar('-', totalWidth));
        
        // Print rows
        for (String[] row : rows) {
            for (int i = 0; i < numColumns; i++) {
                String cell = row[i] == null ? "" : row[i];
                System.out.print(padRight(cell, columnWidths[i] + padding));
            }
            System.out.println();
        }
    }
    
    /**
     * Helper method to pad a string to the right with spaces.
     * Ensures consistent column alignment in the table.
     * 
     * @param s String to pad
     * @param width Total width including string
     * @return Padded string
     */
    private String padRight(String s, int width) {
        if (s == null) return String.format("%-" + width + "s", "");
        return String.format("%-" + width + "s", s);
    }
    
    /**
     * Returns the table as a formatted string.
     * Useful for saving tables to files or including them in other text content.
     * 
     * @return Formatted table as string
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        
        // Calculate the width of each padded column
        int totalWidth = 0;
        for (int width : columnWidths) {
            totalWidth += width + padding;
        }
        
        // Add headers
        for (int i = 0; i < numColumns; i++) {
            result.append(padRight(headers[i], columnWidths[i] + padding));
        }
        result.append("\n");
        
        // Add divider
        result.append(FileUtils.repeatChar('-', totalWidth)).append("\n");
        
        // Add rows
        for (String[] row : rows) {
            for (int i = 0; i < numColumns; i++) {
                String cell = row[i] == null ? "" : row[i];
                result.append(padRight(cell, columnWidths[i] + padding));
            }
            result.append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * Formats a value for display in a table cell, truncating if longer than maxLength.
     * Useful for keeping table columns at a consistent width.
     * 
     * @param value The string value to format
     * @param maxLength Maximum length before truncating
     * @return Formatted string, truncated with ellipsis if necessary
     */
    public static String formatCell(String value, int maxLength) {
        if (value == null) return "";
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 3) + "...";
    }
}