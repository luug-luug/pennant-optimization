package PennantOptimization;

import java.util.Scanner;

/**
 * This class provides textual user interfaces.
 * <p>
 *     Its methods contain different interfaces with which users can control
 *     processes and make entries using keystrokes.
 * </p>
 */
public class UserInterface {
    // Attributes
    private final Scanner scanner = new Scanner(System.in);
    private String userInput = "";
    private final String inputFieldText = "Input: ";
    private final String invalidInputText = "Not a valid input.\n";

    // Methods
    /**
     * This method contains a decision interface.
     * <p>
     *     A textual user interface is loaded, in which users can decide
     *     by key input between Yes and No.
     * </p>
     * @param text The instructive text that is displayed to the user.
     * @return The decision is returned ("y"/"Y"=true, "n"/"N"=false)
     */
    public boolean decisionInterface(String text) {
        userInput = "";
        System.out.print(text);
        while (true) {
            System.out.print(inputFieldText);
            userInput = scanner.nextLine().toLowerCase();
            switch (userInput) {
                case "y" -> {return true;}
                case "n" -> {return false;}
                default -> System.out.print(invalidInputText);
            }
        }
    }
    /**
     * This method contains an action interface.
     * <p>
     *     A textual user interface is loaded, in which users can <i>start</i> an action by
     *     pressing a key. Strictly speaking the program just waits in the interface until
     *     the keystroke commands to continue. The <i>action</i> is everything that follows
     *     after the interface.
     * </p>
     * @param text The instructive text that is displayed to the user.
     */
    public void actionInterface(String text) {
        userInput = "";
        System.out.print(text);
        while (true) {
            System.out.print(inputFieldText);
            userInput = scanner.nextLine().toLowerCase();
            if ("x".equals(userInput)) {
                return;
            } else {
                System.out.print(invalidInputText);
            }
        }
    }
    /**
     * This method contains a text input user interface.
     * <p>
     *     A textual user interface is loaded, in which users can put in a text by typing it in.
     *     Empty inputs are excluded.
     * </p>
     * @param text The instructive text that is displayed to the user.
     * @return The text that has been typed in is returned.
     */
    public String textInterface(String text) {
        userInput = "";
        System.out.print(text);
        while(true) {
            System.out.print(inputFieldText);
            userInput = scanner.nextLine();
            if (userInput.equals("")) {
                System.out.print(invalidInputText);
            } else {
                return userInput;
            }
        }
    }
    /**
     * This method contains a quantity input interface.
     * <p>
     *     A textual user interface is loaded, in which users can type in a quantity, meaning
     *     a positive integer within certain predefined limits.
     * </p>
     * @param text The instructive text that is displayed to the user.
     * @param minimum The inclusive minimum for the values that can be typed in as a quantity.
     *                It has to be higher than 0.
     * @param maximum The inclusive maximum for the values that can be typed in as a quantity.
     * @throws IllegalArgumentException Minimum and/or maximum has been set incorrectly.
     * @return The quantity that has been typed in is returned.
     */
    public int quantityInterface(String text, int minimum, int maximum) throws IllegalArgumentException {
        if (minimum < 0 || maximum <= minimum) {
            throw new IllegalArgumentException("Minimum/maximum set incorrectly");
        }
        userInput = "";
        System.out.print(text);
        int anzahl;
        while (true) {
            System.out.print(inputFieldText);
            userInput = scanner.nextLine();
            try {   // test whether input is integer
                anzahl = Integer.parseInt(userInput);
                if (minimum <= anzahl && anzahl <= maximum) {
                    return anzahl;
                } else {
                    System.out.print(invalidInputText);
                }
            }catch(NumberFormatException e) {
                System.out.print(invalidInputText);
            }
        }
    }
}