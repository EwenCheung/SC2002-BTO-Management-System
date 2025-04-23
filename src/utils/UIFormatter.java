package utils;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides consistent UI formatting for the BTO Management System command-line interface.
 * This utility class centralizes all text formatting, color management, and display 
 * standardization to ensure a consistent user experience throughout the application.
 * 
 * Features include:
 * - Semantic color coding for different message types (success, error, warning, etc.)
 * - Automatic detection of terminal color support
 * - Status-based color highlighting for application states
 * - Consistent header and divider formatting
 * - Support for enabling/disabling colors globally
 * 
 * Follows the Open/Closed Principle by providing extension points for new formatting
 * without modifying existing code.
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
    
    /**
     * Semantic color types for different UI elements.
     * Each type maps to a specific ANSI color code for consistent representation.
     */
    public enum ColorType {
        /** For successful operations and positive states */
        SUCCESS, 
        /** For errors, failures, and negative states */
        ERROR, 
        /** For warnings and cautionary messages */
        WARNING, 
        /** For informational messages and neutral states */
        INFO, 
        /** For section and page headers */
        HEADER, 
        /** For user input prompts */
        PROMPT, 
        /** For regular, unformatted text */
        NORMAL
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
     * Enables or disables color output globally.
     * Useful for environments where ANSI colors are not supported.
     * 
     * @param enable true to enable colors, false to disable
     */
    public static void setColorEnabled(boolean enable) {
        useColors = enable;
    }
    
    /**
     * Formats a main header with appropriate styling and centering.
     * Used for page titles and major section headers.
     * 
     * @param title Header title text
     * @return Formatted header string with border and centered text
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
     * Creates a horizontal divider line for separating content sections.
     * 
     * @return Formatted divider string
     */
    public static String formatDivider() {
        return colorize(FileUtils.repeatChar('-', 70), ColorType.INFO, false);
    }
    
    /**
     * Formats a success message with appropriate styling and icon.
     * Used for confirming successful operations to the user.
     * 
     * @param message Success message text
     * @return Formatted success message with checkmark prefix
     */
    public static String formatSuccess(String message) {
        return "\n" + colorize("✓ " + message, ColorType.SUCCESS, true);
    }
    
    /**
     * Formats an error message with appropriate styling and icon.
     * Used for indicating errors or failures to the user.
     * 
     * @param message Error message text
     * @return Formatted error message with X mark prefix
     */
    public static String formatError(String message) {
        return "\n" + colorize("✗ " + message, ColorType.ERROR, true);
    }
    
    /**
     * Formats a warning message with appropriate styling and icon.
     * Used for cautionary messages that require attention but aren't errors.
     * 
     * @param message Warning message text
     * @return Formatted warning message with exclamation mark prefix
     */
    public static String formatWarning(String message) {
        return "\n" + colorize("! " + message, ColorType.WARNING, true);
    }
    
    /**
     * Formats an information message with appropriate styling.
     * Used for neutral informational content.
     * 
     * @param message Information message text
     * @return Formatted information message
     */
    public static String formatInfo(String message) {
        return "\n" + colorize(message, ColorType.INFO, false);
    }
    
    /**
     * Formats a user input prompt with appropriate styling.
     * Only colors the prompt text, not the user's input.
     * 
     * @param prompt Prompt text
     * @return Formatted prompt string
     */
    public static String formatPrompt(String prompt) {
        return colorize(prompt, ColorType.PROMPT, false);
    }
    
    /**
     * Formats table headers with appropriate styling for better readability.
     * Used to make table column headers stand out from the data rows.
     * 
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
     * Formats a section header with appropriate styling.
     * Used for subsections within a page or form.
     * 
     * @param title Section title text
     * @return Formatted section header
     */
    public static String formatSectionHeader(String title) {
        return "\n" + colorize("=== " + title + " ===", ColorType.INFO, true);
    }
    
    /**
     * Highlights text with the header color for emphasis.
     * Used to make important information stand out.
     * 
     * @param text Text to highlight
     * @return Highlighted text
     */
    public static String highlight(String text) {
        return colorize(text, ColorType.HEADER, false);
    }
    
    /**
     * Formats status text with appropriate color based on semantic meaning.
     * Automatically determines the appropriate color based on common status keywords.
     * 
     * @param status Status text to format
     * @return Color-coded status text
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
     * Formats officer registration status with appropriate coloring.
     * Different statuses are color-coded to provide visual cues about the registration state.
     * 
     * @param registrationStatus The registration status to format
     * @return Color-coded registration status text
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
     * Formats project status with appropriate coloring.
     * Different project states are color-coded to provide visual cues about availability.
     * 
     * @param projectStatus The project status to format
     * @return Color-coded project status text
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
     * Helper method to apply color and styling to text.
     * Handles the actual ANSI code application and reset.
     * 
     * @param text Text to colorize
     * @param type Color type to apply
     * @param bold Whether to make text bold
     * @return Colorized string with appropriate ANSI codes
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
     * Determines if the current terminal environment supports ANSI colors.
     * Checks various environment variables and platform-specific features.
     * 
     * @return true if colors are supported by the terminal
     */
    public static boolean supportsColors() {
        // In JDK 8, we'll use a simpler approach that works cross-platform
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
        
        // Check OS type - simplified for Java 8 compatibility
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac") || osName.contains("darwin")) {
            return true;
        } else if (osName.contains("windows")) {
            String conEmuANSI = System.getenv("ConEmuANSI");
            if (conEmuANSI != null && conEmuANSI.equals("ON")) {
                return true;
            }
            
            // Simplified Windows version check
            try {
                String osVersion = System.getProperty("os.version");
                if (osVersion != null) {
                    String[] versionParts = osVersion.split("\\.");
                    if (versionParts.length > 0) {
                        int majorVersion = Integer.parseInt(versionParts[0]);
                        if (majorVersion >= 10) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                // If version parsing fails, default to no colors on Windows
                return false;
            }
            return false;
        }
        
        // Unix-like systems generally support colors
        return true;
    }
}