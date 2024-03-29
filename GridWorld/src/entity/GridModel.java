package entity;

public class GridModel {

    private Cell[][] grid = null;

    public GridModel() {

        grid = new Cell[constants.NUM_COLS][constants.NUM_ROWS];
        buildGrid();
        duplicateGrid();
    }

    public GridModel(int scale)
    {
        grid = new Cell[constants.NUM_COLS*scale][constants.NUM_ROWS*scale];
        buildGrid(scale);
        duplicateGrid(scale);
    }

    // Returns the actual grid, i.e. a 2-D states array
    public Cell[][] getGrid() {
        return grid;
    }

    // Initialize the Grid Environment
    public void buildGrid() {

        // All grids (even walls) starts with reward of -0.040
        for(int row = 0 ; row < constants.NUM_ROWS ; row++) {
            for(int col = 0 ; col < constants.NUM_COLS ; col++) {

                grid[col][row] = new Cell(constants.WHITE_REWARD);
            }
        }

        // Set all the green squares (+1.000)
        String[] greenSquaresArr = constants.GREEN_SQUARES.split(constants.GRID_DELIM);
        for(String greenSquare : greenSquaresArr) {

            greenSquare = greenSquare.trim();
            String [] gridInfo = greenSquare.split(constants.COL_ROW_DELIM);
            int gridCol = Integer.parseInt(gridInfo[0]);
            int gridRow = Integer.parseInt(gridInfo[1]);

            grid[gridCol][gridRow].setReward(constants.GREEN_REWARD);
        }

        // Set all the brown squares (-1.000)
        String[] brownSquaresArr = constants.BROWN_SQUARES.split(constants.GRID_DELIM);
        for (String brownSquare : brownSquaresArr) {

            brownSquare = brownSquare.trim();
            String[] gridInfo = brownSquare.split(constants.COL_ROW_DELIM);
            int gridCol = Integer.parseInt(gridInfo[0]);
            int gridRow = Integer.parseInt(gridInfo[1]);

            grid[gridCol][gridRow].setReward(constants.BROWN_REWARD);
        }

        // Set all the walls (0.000 and unreachable, i.e. stays in the same place as before)
        String[] wallSquaresArr = constants.WALLS_SQUARES.split(constants.GRID_DELIM);
        for (String wallSquare : wallSquaresArr) {

            wallSquare = wallSquare.trim();
            String[] gridInfo = wallSquare.split(constants.COL_ROW_DELIM);
            int gridCol = Integer.parseInt(gridInfo[0]);
            int gridRow = Integer.parseInt(gridInfo[1]);

            grid[gridCol][gridRow].setReward(constants.WALL_REWARD);
            grid[gridCol][gridRow].setAsWall(true);
        }
    }


    public void duplicateGrid() {

        for(int row = 0 ; row < constants.NUM_ROWS ; row++) {
            for(int col = 0 ; col < constants.NUM_COLS ; col++) {

                if (row >= 6 || col >= 6) {
                    int trueRow = row % 6;
                    int trueCol = col % 6;

                    grid[col][row].setReward(grid[trueCol][trueRow].getReward());
                    grid[col][row].setAsWall(grid[trueCol][trueRow].isWall());
                }
            }
        }
    }

    private void buildGrid(int scale) {
        grid = new Cell[constants.NUM_ROWS*scale][constants.NUM_COLS*scale];
        for (int row=0; row<constants.NUM_ROWS*scale; row++) {
            for (int col=0; col<constants.NUM_COLS*scale; col++) {
                grid[row][col] = new Cell(constants.WHITE_REWARD);
            }
        }

        String[] greenSquaresArr = constants.GREEN_SQUARES.split(constants.GRID_DELIM);
        for(String greenSquare : greenSquaresArr) {

            greenSquare = greenSquare.trim();
            String [] gridInfo = greenSquare.split(constants.COL_ROW_DELIM);
            int gridCol = Integer.parseInt(gridInfo[0])*scale;
            int gridRow = Integer.parseInt(gridInfo[1])*scale;
            for(int i=gridRow;i<gridRow+scale;i++)
            {
                for(int j=gridCol;j<gridCol+scale;j++)
                {
                    grid[j][i].setReward(constants.GREEN_REWARD);
                }
            }


        }

        // Set all the brown squares (-1.000)
        String[] brownSquaresArr = constants.BROWN_SQUARES.split(constants.GRID_DELIM);
        for (String brownSquare : brownSquaresArr) {

            brownSquare = brownSquare.trim();
            String[] gridInfo = brownSquare.split(constants.COL_ROW_DELIM);
            int gridCol = Integer.parseInt(gridInfo[0])*scale;
            int gridRow = Integer.parseInt(gridInfo[1])*scale;

            for(int i=gridRow;i<gridRow+scale;i++)
            {
                for(int j=gridCol;j<gridCol+scale;j++)
                {
                    grid[j][i].setReward(constants.BROWN_REWARD);
                }
            }
        }

        // Set all the walls (0.000 and unreachable, i.e. stays in the same place as before)
        String[] wallSquaresArr = constants.WALLS_SQUARES.split(constants.GRID_DELIM);
        for (String wallSquare : wallSquaresArr) {

            wallSquare = wallSquare.trim();
            String[] gridInfo = wallSquare.split(constants.COL_ROW_DELIM);
            int gridCol = Integer.parseInt(gridInfo[0])*scale;
            int gridRow = Integer.parseInt(gridInfo[1])*scale;
            for(int i=gridRow;i<gridRow+scale;i++)
            {
                for(int j=gridCol;j<gridCol+scale;j++)
                {
                    grid[j][i].setReward(constants.WALL_REWARD);
                    grid[j][i].setAsWall(true);
                }
            }

        }


    }

    public void duplicateGrid(int scale) {

        for(int row = 0 ; row < constants.NUM_ROWS*scale ; row++) {
            for(int col = 0 ; col < constants.NUM_COLS*scale ; col++) {

                if (row >= 6*scale || col >= 6*scale) {
                    int trueRow = row % (6*scale);
                    int trueCol = col % (6*scale);

                    grid[col][row].setReward(grid[trueCol][trueRow].getReward());
                    grid[col][row].setAsWall(grid[trueCol][trueRow].isWall());
                }
            }
        }
    }

}
