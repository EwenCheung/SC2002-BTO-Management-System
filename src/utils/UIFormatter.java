package utils;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides consistent UI formatting for CLI applications.
 * Centralizes all formatting logic to follow OCP (Open/Closed Principle).
 */
public class UIFormatter {
    // ANSI color and style codes
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    
    // Semantic color types for different UI elements
    public enum ColorType {
        SUCCESS, ERROR, WARNING, INFO, HEADER, PROMPT, NORMAL
    }
    
    // Map color types to their ANSI codes
    private static final Map<ColorType, String> colorMap = new HashMap<>();
    
    // Flag to enable/disable colors
    private static boolean useColors = true;
    
    static {
        colorMap.put(ColorType.SUCCESS, ANSI_GREEN);
        colorMap.put(ColorType.ERROR, ANSI_RED);
        colorMap.put(ColorType.WARNING, ANSI_YELLOW);
        colorMap.put(ColorType.INFO, ANSI_BLUE);
        colorMap.put(ColorType.HEADER, ANSI_CYAN);
        colorMap.put(ColorType.PROMPT, ANSI_YELLOW);
        colorMap.put(ColorType.NORMAL, "");
    }
    
    /**
     * Enables or disables color output
     * @param enable true to enable colors, false to disable
     */
    public static void setColorEnabled(boolean enable) {
        useColors = enable;
    }
    
    /**
     * Formats a header with appropriate styling
     * @param title Header title
     * @return Formatted header string
     */
    public static String formatHeader(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(FileUtils.repeatChar('=', 70)).append("\n");
        
        // Center the title
        int padding = (70 - title.length()) / 2;
        sb.append(FileUtils.repeatChar(' ', padding));
        sb.append(colorize(title, ColorType.HEADER, true));
        sb.append("\n").append(FileUtils.repeatChar('=', 70));
        
        return sb.toString();
    }
    
    /**
     * Creates a horizontal divider line
     * @return Formatted divider string
     */
    public static String formatDivider() {
        return colorize(FileUtils.repeatChar('-', 70), ColorType.INFO, false);
    }
    
    /**
     * Formats a success message
     * @param message Success message
     * @return Formatted success message
     */
    public static String formatSuccess(String message) {
        return "\n" + colorize("✓ " + message, ColorType.SUCCESS, true);
    }
    
    /**
     * Formats an error message
     * @param message Error message
     * @return Formatted error message
     */
    public static String formatError(String message) {
        return "\n" + colorize("✗ " + message, ColorType.ERROR, true);
    }
    
    /**
     * Formats a warning message
     * @param message Warning message
     * @return Formatted warning message
     */
    public static String formatWarning(String message) {
        return "\n" + colorize("! " + message, ColorType.WARNING, true);
    }
    
    /**
     * Formats an information message
     * @param message Information message
     * @return Formatted information message
     */
    public static String formatInfo(String message) {
        return "\n" + colorize(message, ColorType.INFO, false);
    }
    
    /**
     * Formats a prompt string - only colors the prompt, not user input
     * @param prompt Prompt text
     * @return Formatted prompt string
     */
    public static String formatPrompt(String prompt) {
        return colorize(prompt, ColorType.PROMPT, false);
    }
    
    /**
     * Formats table headers
     * @param headers Array of header strings
     * @return Array of formatted header strings
     */
    public static String[] formatTableHeaders(String[] headers) {
        String[] formatted = new String[headers.length];
        for (int i = 0; i < headers.length; i++) {
            formatted[i] = colorize(headers[i], ColorType.HEADER, true);
        }
        return formatted;
    }
    
    /**
     * Formats a section header
     * @param title Section title
     * @return Formatted section header
     */
    public static String formatSectionHeader(String title) {
        return "\n" + colorize("=== " + title + " ===", ColorType.INFO, true);
    }
    
    /**
     * Highlights text with the header color
     * @param text Text to highlight
     * @return Highlighted text
     */
    public static String highlight(String text) {
        return colorize(text, ColorType.HEADER, false);
    }
    
    /**
     * Formats status text with appropriate color based on context
     * @param status Status text
     * @return Formatted status text
     */
    public static String formatStatus(String status) {
        if (status == null) return "";
        
        ColorType type;
        if (status.equalsIgnoreCase("SUCCESSFUL") || 
            status.equalsIgnoreCase("APPROVED") || 
            status.equalsIgnoreCase("BOOKED") || 
            status.equalsIgnoreCase("VISIBLE") || 
            status.equals("YES")) {
            type = ColorType.SUCCESS;
        } else if (status.equalsIgnoreCase("UNSUCCESSFUL") || 
                  status.equalsIgnoreCase("REJECTED") || 
                  status.equalsIgnoreCase("HIDDEN") || 
                  status.equals("NO")) {
            type = ColorType.ERROR;
        } else if (status.equalsIgnoreCase("PENDING") || 
                  status.equalsIgnoreCase("WAITING")) {
            type = ColorType.WARNING;
        } else {
            type = ColorType.NORMAL;
        }
        
        return colorize(status, type, false);
    }
    
    /**
     * Formats registration status with appropriate coloring
     * @param registrationStatus The registration status to format
     * @return Formatted registration status text
     */
    public static String formatRegistrationStatus(String registrationStatus) {
        if (registrationStatus == null) return "";
        
        if (registrationStatus.equals("Yes - Allow")) {
            return colorize(registrationStatus, ColorType.SUCCESS, false);
        } else if (registrationStatus.equals("No - Clash") || 
                   registrationStatus.equals("No - You Applied")) {
            return colorize(registrationStatus, ColorType.ERROR, false);
        } else {
            return registrationStatus;
        }
    }
    
    /**
     * Formats project status with appropriate coloring
     * @param projectStatus The project status to format
     * @return Formatted project status text
     */
    public static String formatProjectStatus(String projectStatus) {
        if (projectStatus == null) return "";
        
        if (projectStatus.equals("Active")) {
            return colorize(projectStatus, ColorType.SUCCESS, false);
        } else if (projectStatus.equals("Upcoming")) {
            return colorize(projectStatus, ColorType.WARNING, false);
        } else if (projectStatus.equals("Closed")) {
            return colorize(projectStatus, ColorType.ERROR, false);
        } else {
            return projectStatus;
        }
    }
    
    /**
     * Helper method to apply color to text
     * @param text Text to colorize
     * @param type Color type to apply
     * @param bold Whether to make text bold
     * @return Colorized string
     */
    private static String colorize(String text, ColorType type, boolean bold) {
        if (!useColors) return text;
        
        String colorCode = colorMap.get(type);
        String boldCode = bold ? ANSI_BOLD : "";
        
        if (colorCode.isEmpty() && !bold) {
            return text;
        }
        
        return boldCode + colorCode + text + ANSI_RESET;
    }
    
    /**
     * Check if terminal supports colors by checking environment variables
     * @return true if colors are supported
     */
    public static boolean supportsColors() {
        String noColor = System.getenv("NO_COLOR");
        String cliNoColor = System.getenv("CLI_NO_COLOR");
        
        // Disable colors if NO_COLOR or CLI_NO_COLOR is set
        if (noColor != null || cliNoColor != null) {
            return false;
        }
        
        // Check for terminal type
        String term = System.getenv("TERM");
        if (term != null && (term.contains("xterm") || term.contains("color"))) {
            return true;
        }
        
        // Check OS type
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac") || osName.contains("darwin")) {
            // macOS terminals generally support colors
            return true;
        } else if (osName.contains("windows")) {
            // Windows Terminal typically supports colors in newer versions
            String conEmuANSI = System.getenv("ConEmuANSI");
            if (conEmuANSI != null && conEmuANSI.equals("ON")) {
                return true;
            }
            
            // Windows 10 and above generally support ANSI codes in CMD/PowerShell
            String osVersion = System.getProperty("os.version");
            try {
                int version = Integer.parseInt(osVersion.split("\\.")[0]);
                if (version >= 10) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // If version parsing fails, default to no colors
                return false;
            }
        }
        
        return true; // Default to true for Unix-like systems
    }
}