package Temp;



public class Main {
    private static slGoLBoard my_board;
    private static final int ROWS = 7, COLS = 9;

    public static void main(String[] args) {

        //test_1();
        //test_2();
        //test_3();
        //test_4();
        test_5();
    }

    // print TwoDegreeNeighbors(0,0)
    private static void test_1() {
        my_board = new slGoLBoardLive(ROWS, COLS);
        my_board.printGoLBoard();
        int my_row = 1, my_col = 1;
        System.out.println("TwoDegreeNeighbors(" + my_row +", " + my_col + ") --> " +
                ((slGoLBoardLive) my_board).countLiveTwoDegreeNeighbors(my_row, my_col));
        System.out.println();
        return;
        
    }

    // print TwoDegreeNeighbors(ROWS-1, COLS-1)
    private static void test_2() {
        my_board = new slGoLBoardLive(ROWS, COLS);
        my_board.printGoLBoard();
        int my_row = ROWS-1, my_col = COLS-1;
        System.out.println("TwoDegreeNeighbors(" + my_row +", " + my_col + ") --> " +
                ((slGoLBoardLive) my_board).countLiveTwoDegreeNeighbors(my_row, my_col));
        System.out.println();
        return;
    }

    // print TwoDegreeNeighbors((int)(ROWS/2), (int)(ROWS/2))
    private static void test_3() {
        my_board = new slGoLBoardLive(ROWS, COLS);
        my_board.printGoLBoard();
        int my_row = 4, my_col = 6;
        System.out.println("TwoDegreeNeighbors(" + my_row +", " + my_col + ") --> " +
                ((slGoLBoardLive) my_board).countLiveTwoDegreeNeighbors(my_row, my_col));
        System.out.println();
        return;
    }

    // print TwoDegreeNeighbors(0, (int)(ROWS/2))
    private static void test_4() {
        my_board = new slGoLBoardLive(ROWS, COLS);
        my_board.printGoLBoard();
        int my_row = 0, my_col = 6;
        System.out.println("TwoDegreeNeighbors(" + my_row +", " + my_col + ") --> " +
                ((slGoLBoardLive) my_board).countLiveTwoDegreeNeighbors(my_row, my_col));
        System.out.println();
        return;
    }

    // print the board and the updated board
    private static void test_5() {
        my_board = new slGoLBoardLive(ROWS, COLS);
        my_board.printGoLBoard();
        int my_row = 5, my_col = 5;
        System.out.println("TwoDegreeNeighbors(" + my_row +", " + my_col + ") --> " +
                ((slGoLBoardLive) my_board).countLiveTwoDegreeNeighbors(my_row, my_col));
        System.out.println();
        return;
    }

}








