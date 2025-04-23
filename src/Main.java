/**
 * Main entry point for the BTO Management System application.
 * This class initializes the application and displays the main menu.
 */
import menu.MainMenu;

public class Main {
    /**
     * The main method that starts the BTO Management System.
     * Creates a MainMenu object and displays it.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting BTO Management System...");
        MainMenu mainMenu = new MainMenu();
        mainMenu.displayMainMenu();
    }
}
