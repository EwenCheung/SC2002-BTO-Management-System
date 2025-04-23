package utils;

import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for printing formatted tables in CLI applications.
 * This class handles automatic column width calculation and consistent formatting.
 */
public class TablePrinter {
    private final String[] headers;
    private final List<String[]> rows;
    private final int[] columnWidths;
    private final int numColumns;
    private final int padding = 4; // Padding between columns
    
    /**
     * Constructor that initializes a new table
     * @param headers Column headers
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
     * Add a row to the table
     * @param rowData Row data (must match number of columns)
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
     * Add a row from an array of objects, converting them to strings
     * @param rowData Row data objects (must match number of columns)
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
     * Print the table
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
     * Helper method to pad a string to the right with spaces
     * @param s String to pad
     * @param width Total width including string
     * @return Padded string
     */
    private String padRight(String s, int width) {
        if (s == null) return String.format("%-" + width + "s", "");
        return String.format("%-" + width + "s", s);
    }
    
    /**
     * Get the table as a string
     * @return Formatted table as string
     */
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
     * Format a value for display, truncating if necessary
     * @param value The value to format
     * @param maxLength Maximum length before truncating
     * @return Formatted string
     */
    public static String formatCell(String value, int maxLength) {
        if (value == null) return "";
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 3) + "...";
    }
}