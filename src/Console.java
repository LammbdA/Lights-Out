import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {
    private static Field field;
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static Scanner sc = new Scanner(System.in);
    private static final Pattern INPUT_PATTERN = Pattern.compile("([A-Z])([1-9][0-9]*)");
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";


    public static void main(String[] args) {
        run();
    }
    private static void run(){
        askPlayerField();
        while (field.getGameState() == GameState.PLAYING) {
            printField();
            processInput();
        }
    }

    public static void printField(){
        printFieldBody();
        printFieldHeader();
    }

    private static void printFieldHeader() {
        System.out.print(" ");
        for (int column = 0; column < field.getColumnCount(); column++) {
            System.out.printf(" %2d", column + 1);
        }
        System.out.println();
    }

    private static void printFieldBody() {
        for (int row = 0; row < field.getRowCount(); row++) {
            System.out.print((char) (row + 'A'));
            for (int column = 0; column < field.getColumnCount(); column++) {
                System.out.print(" ");
                switch (field.getBricks(row, column).getBrickState()) {
                    case ON:
                        System.out.print(ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET);
                        break;
                    case OFF:
                        System.out.print(ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET);
                        break;
                }
            }
            System.out.println();
            System.out.println();
        }
    }

    private static void askPlayerField() {
        System.out.println("Zadajte rozmery pol'a:");
        System.out.print("Rows=");
        String string = sc.next();
        if (!string.matches("([1-9][0-9]*)")) {
            System.out.println("Zadali ste zly vstup");
            askPlayerField();
            return;
        }
        int row = Integer.parseInt(string);
        System.out.print("Columns=");
        string = sc.next();
        if (!string.matches("([1-9][0-9]*)")) {
            System.out.println("Zadali ste zly vstup");
            askPlayerField();
            return;
        }
        int column = Integer.parseInt(string);
        if (column > 1 && row > 1) {
            field = new Field(row, column);
            field.generateField();
        } else {
            System.out.println("Zadajte pole aspon' 2x2");
            askPlayerField();
        }
    }

    private static void processInput() {
        System.out.println("Napis vstup (napr. a1, exit, DFS, bot5x5, bot2x3):");
        String line = readLine();
        line = line.toUpperCase();
        if (line.equals("EXIT")) {
            System.exit(0);
        }
        if (line.equals("BOT5X5")) {
            field.bot5x5();
            return;
        }
        if (line.equals("DFS")) {
            field.DFS();
            return;
        }
        if (line.equals("BOT2X3")) {
            field.bot2x3();
            return;
        }
        Matcher m = INPUT_PATTERN.matcher(line);
        if (m.matches()) {
            int row = m.group(1).charAt(0) - 65;
            int column = Integer.parseInt(m.group(2)) - 1;
            if (row < field.getRowCount() && column < field.getColumnCount()) {
                field.openBrick(row, column);
                if (field.isSolved()) {
                    field.setGameState(GameState.SOLVED);
                    System.out.println("You won!");
                }
            }
            else {
                System.out.println("To nie je spravny move.");
                processInput();
            }
        } else {
            System.out.println("Nezadal si dobry vstup, skus znova.");
            processInput();
        }
    }

    private static String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            System.err.println("Nepodarilo sa nacitat vstup, skus znova");
            return "";
        }
    }
}
