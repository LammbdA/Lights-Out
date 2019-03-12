import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Field {
    private GameState gameState = GameState.PLAYING;
    private int rowCount;
    private int columnCount;
    private final Brick[][] bricks;
    private int numberOfLightsOn;
    private int topNeighborLights;
    private int rowPos;
    private int columnPos;
    private ArrayList<int[][]> list;
    private int[][] field;
    private boolean flag;

    public Field(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.bricks = new Brick[rowCount][columnCount];
        this.field = new int[rowCount][columnCount];
        this.topNeighborLights = 0;
        this.rowPos = 0;
        this.columnPos = 0;
        this.list = new ArrayList<>();
        this.flag = false;
    }

    //Вигенерує поле
    public void generateField() {
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                bricks[row][column] = new Brick(BrickState.ON);
            }
        }
        Random random = new Random();
        int randomNumber = random.nextInt(20);
        while (randomNumber != 0) {
            int row = random.nextInt(getRowCount());
            int column = random.nextInt(getColumnCount());
            openBrick(row, column);
            randomNumber--;
        }
        if (isSolved())
            generateField();
    }

    //Включить світло
    public void openBrick(int row, int column) {
        if (gameState == GameState.PLAYING) {
            toggleWithCheck(row, column);
        }
    }

    private void toggleWithCheck(int row, int column) {
        if (row >= 0 && column >= 0 && row < getRowCount() && column < getColumnCount()) {
            if (row == 0 && column == 0) {
                toggle(row, column);
                toggle(row + 1, column);
                toggle(row, column + 1);
            } else {
                if (row == getRowCount() - 1 && column == getColumnCount() - 1) {
                    toggle(row, column);
                    toggle(row - 1, column);
                    toggle(row, column - 1);
                } else {
                    if (row == 0 && column == getColumnCount() - 1) {
                        toggle(row, column);
                        toggle(row + 1, column);
                        toggle(row, column - 1);
                    } else {
                        if (column == 0 && row == getRowCount() - 1) {
                            toggle(row, column);
                            toggle(row - 1, column);
                            toggle(row, column + 1);
                        } else {
                            if (row == 0 && column != getColumnCount() - 1) {
                                toggle(row, column);
                                toggle(row + 1, column);
                                toggle(row, column + 1);
                                toggle(row, column - 1);
                            } else {
                                if (column == 0 && row != getRowCount() - 1) {
                                    toggle(row, column);
                                    toggle(row, column + 1);
                                    toggle(row - 1, column);
                                    toggle(row + 1, column);
                                } else {
                                    if (row == getRowCount() - 1 && column != getColumnCount() - 1) {
                                        toggle(row, column);
                                        toggle(row - 1, column);
                                        toggle(row, column + 1);
                                        toggle(row, column - 1);
                                    } else {
                                        if (column == getColumnCount() - 1 && row != getRowCount() - 1) {
                                            toggle(row, column);
                                            toggle(row + 1, column);
                                            toggle(row - 1, column);
                                            toggle(row, column - 1);
                                        } else {
                                            toggle(row, column);
                                            toggle(row + 1, column);
                                            toggle(row - 1, column);
                                            toggle(row, column + 1);
                                            toggle(row, column - 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void toggle(int row, int column) {
        if (bricks[row][column].getBrickState() == BrickState.ON)
            bricks[row][column].setBrickState(BrickState.OFF);
        else
            bricks[row][column].setBrickState(BrickState.ON);
    }

    //Перевірка чи гра виграна
    public boolean isSolved() {
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                if (bricks[row][column].getBrickState() != BrickState.OFF)
                    return false;
            }
        }
        return true;
    }

///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

    //DFS алгоритм
    public void DFS() {
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                addFieldToList();
                if (flag)
                    DFS();
                else {
                    openBrick(row, column);
                    Console.printField();
                    System.out.println(list.size());
                }
                if (isSolved()) {
                    setGameState(GameState.SOLVED);
                    System.out.println("You won!");
                    System.exit(0);
                }
            }
        }
    }

    //Перевірить чи є таке поле в лісті
    private boolean checkFieldFromList() {
        if (list.isEmpty()) {
            list.add(field);
            flag = true;
            return false;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (Arrays.deepEquals(list.get(i), field)) {
                    flag = false;
                    return false;
                }
            }
        }
        flag = true;
        return true;
    }

    //Добавить поле в ліст, якщо такого поля нема або якщо ліст пустий
    private void addFieldToList() {
        field = new int[rowCount][columnCount];
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                if (bricks[row][column].getBrickState() == BrickState.ON)
                    field[row][column] = 1;
                else
                    field[row][column] = 0;
            }
        }
        if (checkFieldFromList())
            list.add(field);
    }
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

    //Алгоритм рішення поля 5х5
    public void bot5x5() {
        botMoveLightsDown();
        botCheck();
        botMoveLightsDown();
        if (isSolved()) {
            setGameState(GameState.SOLVED);
            System.out.println("You won!");
        } else {
            bot5x5();
        }
    }

    //Патерн на проходження 5х5
    private void botCheck() {
        if (bricks[getRowCount() - 1][0].getBrickState() == BrickState.OFF && bricks[getRowCount() - 1][1].getBrickState() == BrickState.OFF &&
                bricks[getRowCount() - 1][2].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][3].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][4].getBrickState() == BrickState.ON) {
            openBrick(0, 3);
        }
        if (bricks[getRowCount() - 1][0].getBrickState() == BrickState.OFF && bricks[getRowCount() - 1][1].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][2].getBrickState() == BrickState.OFF && bricks[getRowCount() - 1][3].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][4].getBrickState() == BrickState.OFF) {
            openBrick(0, 1);
            openBrick(0, 4);
        }
        if (bricks[getRowCount() - 1][0].getBrickState() == BrickState.OFF && bricks[getRowCount() - 1][1].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][2].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][3].getBrickState() == BrickState.OFF &&
                bricks[getRowCount() - 1][4].getBrickState() == BrickState.ON) {
            openBrick(0, 0);
        }
        if (bricks[getRowCount() - 1][0].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][1].getBrickState() == BrickState.OFF &&
                bricks[getRowCount() - 1][2].getBrickState() == BrickState.OFF && bricks[getRowCount() - 1][3].getBrickState() == BrickState.OFF &&
                bricks[getRowCount() - 1][4].getBrickState() == BrickState.ON) {
            openBrick(0, 3);
            openBrick(0, 4);
        }
        if (bricks[getRowCount() - 1][0].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][1].getBrickState() == BrickState.OFF &&
                bricks[getRowCount() - 1][2].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][3].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][4].getBrickState() == BrickState.OFF) {
            openBrick(0, 4);
        }
        if (bricks[getRowCount() - 1][0].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][1].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][2].getBrickState() == BrickState.OFF && bricks[getRowCount() - 1][3].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][4].getBrickState() == BrickState.ON) {
            openBrick(0, 2);
        }
        if (bricks[getRowCount() - 1][0].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][1].getBrickState() == BrickState.ON &&
                bricks[getRowCount() - 1][2].getBrickState() == BrickState.ON && bricks[getRowCount() - 1][3].getBrickState() == BrickState.OFF &&
                bricks[getRowCount() - 1][4].getBrickState() == BrickState.OFF) {
            openBrick(0, 1);
        }
    }

    //Змістить все світло вниз
    private void botMoveLightsDown() {
        for (int row = 0; row < getRowCount() - 1; row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                if (bricks[row][column].getBrickState() == BrickState.ON) {
                    openBrick(row + 1, column);
                    Console.printField();
                }
            }
        }
    }

    //Алгоритм рішення поля 2х3
    public void bot2x3() {
        searchAndOpenNeighborLights();
        if (isSolved()) {
            setGameState(GameState.SOLVED);
            System.out.println("You won!");
        } else
            bot2x3();
    }

    //Виключить світло з найбільшою кількістю включених сусідніх ламп
    private void searchAndOpenNeighborLights() {
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                if (bricks[row][column].getBrickState() == BrickState.ON) {
                    numberOfLightsOn = 0;
                    checkNeighborLights(row, column);
                    if (topNeighborLights <= numberOfLightsOn) {
                        topNeighborLights = numberOfLightsOn;
                        rowPos = row;
                        columnPos = column;
                    }
                }
            }
        }
        openBrick(rowPos, columnPos);
        Console.printField();
        topNeighborLights = 0;
        rowPos = 0;
        columnPos = 0;
    }


    //Знайде кількість сусідніх включених ламп
    private void checkNeighborLights(int row, int column) {
        if (row >= 0 && column >= 0 && row < getRowCount() && column < getColumnCount()) {
            if (row == 0 && column == 0) {
                checkRight(row, column);
                checkDown(row, column);
            } else {
                if (row == getRowCount() - 1 && column == getColumnCount() - 1) {
                    checkUp(row, column);
                    checkLeft(row, column);
                } else {
                    if (row == 0 && column == getColumnCount() - 1) {
                        checkLeft(row, column);
                        checkDown(row, column);
                    } else {
                        if (column == 0 && row == getRowCount() - 1) {
                            checkUp(row, column);
                            checkRight(row, column);
                        } else {
                            if (row == 0 && column != getColumnCount() - 1) {
                                checkRight(row, column);
                                checkLeft(row, column);
                                checkDown(row, column);
                            } else {
                                if (column == 0 && row != getRowCount() - 1) {
                                    checkDown(row, column);
                                    checkUp(row, column);
                                    checkRight(row, column);
                                } else {
                                    if (row == getRowCount() - 1 && column != getColumnCount() - 1) {
                                        checkUp(row, column);
                                        checkRight(row, column);
                                        checkLeft(row, column);
                                    } else {
                                        if (column == getColumnCount() - 1 && row != getRowCount() - 1) {
                                            checkDown(row, column);
                                            checkUp(row, column);
                                            checkLeft(row, column);
                                        } else {
                                            checkLeft(row, column);
                                            checkUp(row, column);
                                            checkDown(row, column);
                                            checkRight(row, column);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkRight(int row, int column) {
        if (bricks[row][column + 1].getBrickState() == BrickState.ON)
            numberOfLightsOn++;
    }

    private void checkLeft(int row, int column) {
        if (bricks[row][column - 1].getBrickState() == BrickState.ON)
            numberOfLightsOn++;
    }

    private void checkUp(int row, int column) {
        if (bricks[row - 1][column].getBrickState() == BrickState.ON)
            numberOfLightsOn++;
    }

    private void checkDown(int row, int column) {
        if (bricks[row + 1][column].getBrickState() == BrickState.ON)
            numberOfLightsOn++;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Brick getBricks(int row, int column) {
        return bricks[row][column];
    }

    public ArrayList<int[][]> getList() {
        return list;
    }
}