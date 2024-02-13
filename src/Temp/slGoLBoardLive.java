package Temp;

import java.io.*;
import java.util.*;

class slGoLBoardLive extends slGoLBoard{


    protected slGoLBoardLive(int numRows, int numCols) {
        super(numRows, numCols);
        //TODO Auto-generated constructor stub
    }

    private int boolToInt(boolean b) {
        return Boolean.compare(b, false);
    }

    @Override
    protected int countLiveTwoDegreeNeighbors(int rows, int cols){
        Vector<Boolean> nums = new Vector<Boolean>();
        int my_count = 0;
        int my_row = rows, my_col = cols;

        int next_r = my_row + 1, 
            next_c = my_col + 1,
            prev_r = my_row - 1,
            prev_c = my_col - 1;

        
        // Adjust next_r, next_c, prev_r, and prev_c to wrap around if out of bounds
        if (next_r < 0) {
            next_r = NUM_ROWS - 1;
        }
        if (next_r >= NUM_ROWS) {
            next_r = 0;
        }
        if (next_c < 0) {
            next_c = NUM_COLS - 1;
        }
        if (next_c >= NUM_COLS) {
            next_c = 0;
        }
        if (prev_r < 0) {
            prev_r = NUM_ROWS - 1;
        }
        if (prev_r >= NUM_ROWS) {
            prev_r = 0;
        }
        if (prev_c < 0) {
            prev_c = NUM_COLS - 1;
        }
        if (prev_c >= NUM_COLS) {
            prev_c = 0;
        }
       

        System.out.print(" ->");
            
        System.out.print(my_row + " " + my_col + " " +  liveCellArray[my_row][my_col]);

        System.out.print(" -> ");   

        nums.add(liveCellArray[my_row][next_c]);     // Right
        nums.add(liveCellArray[prev_r][next_c]);     // Below right
        nums.add(liveCellArray[prev_r][my_col]);     // Below
        nums.add(liveCellArray[prev_r][prev_c]);     // Below left
        nums.add(liveCellArray[my_row][prev_c]);     // Left
        nums.add(liveCellArray[next_r][prev_c]);     // Above left
        nums.add(liveCellArray[next_r][my_col]);     // Above
        nums.add(liveCellArray[next_r][next_c]);     // Above right


        for (int i = 0; i < nums.size(); i++){
            System.out.print(boolToInt(nums.get(i)) + " "); 
            my_count += boolToInt(nums.get(i));
        }
            


        return my_count;
    }

    @Override
    protected int updateNextCellArray() {
        int retVal = 0;

        int nln = 0;  // Number Live Neighbors
        boolean ccs = true; // Current Cell Status
        for (int row = 0; row < NUM_ROWS; ++row){
            for (int col = 0; col < NUM_COLS; ++col) {
                ccs = liveCellArray[row][col];
                nln = countLiveTwoDegreeNeighbors(row, col);
                if (!ccs && nln == 3) {
                    nextCellArray[row][col] = true;
                    ++retVal;
                } else {
                    // Current Cell Status is true
                    if (nln < 2 || nln > 3) {
                        nextCellArray[row][col] = false;
                    } else {
                        // nln == 2 || nln == 3
                        nextCellArray[row][col] = true;
                        ++retVal;
                    }
                }
            }  // for (int row = 0; ...)
        }  //  for (int col = 0; ...)

        boolean[][] tmp = liveCellArray;
        liveCellArray = nextCellArray;
        nextCellArray = tmp;

        return retVal;
    }  //  int updateNextCellArray()



    
}

