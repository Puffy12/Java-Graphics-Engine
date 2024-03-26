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
        
        int my_count = 0;
        int my_row = rows, my_col = cols;

        int next_r = (my_row + 1) % NUM_ROWS, 
            next_c = (my_col + 1) % NUM_COLS,
            prev_r = (my_row - 1 + NUM_ROWS) % NUM_ROWS,
            prev_c = (my_col - 1 + NUM_COLS) % NUM_COLS;

        my_count += liveCellArray[prev_r][prev_c] ? 1 : 0;
        my_count += liveCellArray[prev_r][my_col] ? 1 : 0;
        my_count += liveCellArray[prev_r][next_c] ? 1 : 0;
        
        my_count += liveCellArray[my_row][prev_c] ? 1 : 0;
        my_count += liveCellArray[my_row][next_c] ? 1 : 0;
        
        my_count += liveCellArray[next_r][prev_c] ? 1 : 0;
        my_count += liveCellArray[next_r][my_col] ? 1 : 0;
        my_count += liveCellArray[next_r][next_c] ? 1 : 0;

        System.out.print(" ->");
        
        System.out.print(my_row + " " + my_col + " " +  liveCellArray[my_row][my_col]);

        System.out.print(" -> \n");

        System.out.print(liveCellArray[prev_r][prev_c] + " " + liveCellArray[prev_r][my_col] + " " + liveCellArray[prev_r][next_c] + "\n"); //above
        System.out.print(liveCellArray[my_row][prev_c] + " " + liveCellArray[my_row][my_col] + " " + liveCellArray[my_row][next_c] + "\n"); //same row 
        System.out.print(liveCellArray[next_r][prev_c] + " " + liveCellArray[next_r][my_col] + " " + liveCellArray[next_r][next_c] + "\n"); //below


        return my_count;
    }

    @Override
    protected int updateNextCellArray() {
        int retVal = 0;

        int live_count = 0;  // Number Live Neighbors
        boolean my_cell = true; // Current Cell Status
        for (int row = 0; row < NUM_ROWS; ++row){
            for (int col = 0; col < NUM_COLS; ++col) {
                my_cell = liveCellArray[row][col];
                live_count = countLiveTwoDegreeNeighbors(row, col);
                
                if (!my_cell && live_count == 3) {
                    nextCellArray[row][col] = true;
                    ++retVal;
                    continue;
                } else {
                    // Current Cell Status is true
                    if (live_count == 2 || live_count == 3) {
                        nextCellArray[row][col] = true;
                        ++retVal;
                        continue;
                        
                    } else {
                        //if(live_count < 2 || live_count > 3)
                        nextCellArray[row][col] = false;
                        continue;
                        
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

