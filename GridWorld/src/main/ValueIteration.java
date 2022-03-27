package main;
import java.util.ArrayList;
import java.util.List;

import entity.Utility_Action;
import entity.constants;
import control.DisplayControl;
import control.FileIOHandler;
import control.UtilityControl;

import entity.GridModel;
import entity.Cell;

public class ValueIteration {

    public static GridModel gridEnvironment;
    private static List<Utility_Action[][]> utilityList;
    private static Cell[][] grid;
    private static int iterations = 0;
    private static boolean isValueIteration = true;
    private static int SCALE = 1;

    public static void main(String[] args) {

        gridEnvironment = new GridModel();
        grid = gridEnvironment.getGrid();

        runValueIteration(grid);

        String configInfo = displayResults();
        FileIOHandler.writeToTxt(configInfo, isValueIteration, SCALE);
        FileIOHandler.writeToFile(utilityList, "value_iteration_utilities");
    }

    public static void runValueIteration(final Cell[][] grid) {

        Utility_Action[][] currUtilArr = new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];
        Utility_Action[][] newUtilArr = new Utility_Action[constants.NUM_COLS][constants.NUM_ROWS];

        newUtilArr = initializeUtilities();

        utilityList = new ArrayList<>();

        double delta;

        do {
            iterations++;
            UtilityControl.updateUtilities(newUtilArr, currUtilArr);

            delta = Double.MIN_VALUE;

            Utility_Action[][] currUtilArrCopy =
                    new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];
            UtilityControl.updateUtilities(currUtilArr, currUtilArrCopy);
            utilityList.add(currUtilArrCopy);

            // For each state
            for(int row = 0 ; row < constants.NUM_ROWS*SCALE ; row++) {
                for(int col = 0 ; col < constants.NUM_COLS*SCALE ; col++) {

                    // Calculate the utility for each state, not necessary to calculate for walls
                    if (!grid[col][row].isWall()) {
                        newUtilArr[col][row] =
                                UtilityControl.getBestUtility(col, row, currUtilArr, grid, SCALE);

                        double updatedUtil = newUtilArr[col][row].getUtil();
                        double currentUtil = currUtilArr[col][row].getUtil();
                        double updatedDelta = Math.abs(updatedUtil - currentUtil);

                        // Update delta, if the updated delta value is larger than the current one
                        delta = Math.max(delta, updatedDelta);
                    }
                }
            }


            //the iteration will cease when the delta is less than the convergence threshold
        } while ((delta) >= constants.CONVERGENCE_THRESHOLD);
    }

    private static Utility_Action[][] initializeUtilities()
    {

        Utility_Action[][] newArr = new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];
        for (int col = 0; col < constants.NUM_COLS*SCALE; col++) {
            for (int row = 0; row < constants.NUM_ROWS*SCALE; row++) {
                newArr[col][row] = new Utility_Action();
            }
        }
        return newArr;

    }

    private static String displayResults() {
        // Final item in the list is the optimal policy derived by value iteration
        int lastIteration = utilityList.size() - 1;
        final Utility_Action[][] optimalPolicy =
                utilityList.get(lastIteration);

        StringBuilder sb = new StringBuilder();

        // Displays the Grid Environment
        sb.append(DisplayControl.displayGrid(grid, SCALE));

        // Displays the experiment setup
        sb.append(DisplayControl.displayExperimentSetup(isValueIteration, constants.CONVERGENCE_THRESHOLD));

        // Display total number of iterations required for convergence
        sb.append(DisplayControl.displayIterationsCount(iterations));

        // Display the final utilities of all the (non-wall) states
        sb.append(DisplayControl.displayUtilities(grid, optimalPolicy, SCALE));

        // Display the optimal policy
        sb.append(DisplayControl.displayPolicy(optimalPolicy, SCALE));

        // Display the final utilities of all states
        sb.append(DisplayControl.displayUtilitiesGrid(optimalPolicy, SCALE));
        return sb.toString();
    }


}
