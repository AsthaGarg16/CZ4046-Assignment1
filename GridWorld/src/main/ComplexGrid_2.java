package main;
import java.util.ArrayList;
import java.util.List;

import entity.*;
import control.DisplayControl;
import control.FileIOHandler;
import control.UtilityControl;

public class ComplexGrid_2 {

    public static GridModel gridEnvironment;
    private static List<Utility_Action[][]> utilityList;
    private static Cell[][] grid;
    private static int iterations = 0;
    private static int SCALE = 6;

    public static void main(String[] args) {

        gridEnvironment = new GridModel(SCALE);
        grid = gridEnvironment.getGrid();

        runValueIteration(grid);

        String configInfo = displayValueResults();
        FileIOHandler.writeToTxt(configInfo, true, SCALE);
        FileIOHandler.writeToFile(utilityList, "value_iteration_utilities_scale_"+SCALE, SCALE);

        initializeVals();

        gridEnvironment = new GridModel(SCALE);
        grid = gridEnvironment.getGrid();
        String config_and_results;

        runPolicyIteration(grid);

        config_and_results = displayPolicyResults();

        FileIOHandler.writeToFile(utilityList, "policy_iteration_utilities_scale_"+SCALE, SCALE);
        FileIOHandler.writeToTxt(config_and_results, false, SCALE);


    }

    public static void initializeVals()
    {
        gridEnvironment = null;
        utilityList = null;
        grid = null;
        iterations = 0;
        SCALE = 6;
    }


    public static void runValueIteration(final Cell[][] grid) {

        Utility_Action[][] currUtilArr = new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];

        Utility_Action[][] newUtilArr = new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];
        for (int col = 0; col < constants.NUM_COLS*SCALE; col++) {
            for (int row = 0; row < constants.NUM_ROWS*SCALE; row++) {
                newUtilArr[col][row] = new Utility_Action();
            }
        }


        utilityList = new ArrayList<>();

        double delta;

        do {
            iterations++;

            for (int i = 0; i < newUtilArr.length; i++) {
                System.arraycopy(newUtilArr[i], 0, currUtilArr[i], 0, newUtilArr[i].length);
            }


            delta = Double.MIN_VALUE;

            Utility_Action[][] currUtilArrCopy =
                    new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];


            for (int i = 0; i < currUtilArr.length; i++) {
                System.arraycopy(currUtilArr[i], 0, currUtilArrCopy[i], 0, currUtilArr[i].length);
            }

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

    private static String displayValueResults() {
        // Final item in the list is the optimal policy derived by value iteration

        final Utility_Action[][] optimalPolicy =
                utilityList.get(utilityList.size() - 1);

        StringBuilder sb = new StringBuilder();

        // Displays the Grid Environment
        sb.append(DisplayControl.displayGrid(grid, SCALE));

        // Displays the experiment setup
        sb.append(DisplayControl.displayExperimentSetup(true, constants.CONVERGENCE_THRESHOLD));

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

    public static void runPolicyIteration(final Cell[][] grid) {

        Utility_Action[][] currUtilArr = new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];
        Utility_Action[][] newUtilArr = new Utility_Action[constants.NUM_COLS][constants.NUM_ROWS];
        for (int col = 0; col < constants.NUM_COLS*SCALE; col++) {
            for (int row = 0; row < constants.NUM_ROWS*SCALE; row++) {
                newUtilArr[col][row] = new Utility_Action();
                if (!grid[col][row].isWall()) {
                    Action randomAction = Action.getRandomAction();
                    newUtilArr[col][row].setAction(randomAction);
                }
            }
        }

        utilityList = new ArrayList<>();

        boolean unchanged;

        do {

            for (int i = 0; i < newUtilArr.length; i++) {
                System.arraycopy(newUtilArr[i], 0, currUtilArr[i], 0, newUtilArr[i].length);
            }

            Utility_Action[][] currUtilArrCopy =
                    new Utility_Action[constants.NUM_COLS*SCALE][constants.NUM_ROWS*SCALE];

            for (int i = 0; i < currUtilArr.length; i++) {
                System.arraycopy(currUtilArr[i], 0, currUtilArrCopy[i], 0, currUtilArr[i].length);
            }

            utilityList.add(currUtilArrCopy);


            newUtilArr = UtilityControl.estimateNextUtilities(currUtilArr, grid, SCALE);

            unchanged = true;


            for (int row = 0; row < constants.NUM_ROWS*SCALE; row++) {
                for (int col = 0; col < constants.NUM_COLS*SCALE; col++) {


                    if (!grid[col][row].isWall()) {
                        Utility_Action bestActionUtil =
                                UtilityControl.getBestUtility(col, row, newUtilArr, grid, SCALE);

                        Action policyAction = newUtilArr[col][row].getAction();

                        Utility_Action policyActionUtil = switch (policyAction) {
                            case UP -> new Utility_Action(Action.UP, UtilityControl.getActionUpUtility(col, row, newUtilArr, grid, SCALE));
                            case DOWN -> new Utility_Action(Action.DOWN, UtilityControl.getActionDownUtility(col, row, newUtilArr, grid, SCALE));
                            case LEFT -> new Utility_Action(Action.LEFT, UtilityControl.getActionLeftUtility(col, row, newUtilArr, grid, SCALE));
                            case RIGHT -> new Utility_Action(Action.RIGHT, UtilityControl.getActionRightUtility(col, row, newUtilArr, grid, SCALE));
                        };


                        if((bestActionUtil.getUtil() > policyActionUtil.getUtil())) {
                            newUtilArr[col][row].setAction(bestActionUtil.getAction());
                            unchanged = false;
                        }
                    }
                }
            }
            iterations++;

        } while (!unchanged);
    }


    private static String displayPolicyResults() {
        // Final item in the list is the optimal policy derived by policy iteration

        final Utility_Action[][] optimalPolicy =
                utilityList.get(utilityList.size() - 1);
        StringBuilder s = new StringBuilder();

        // Displays the Grid Environment
        s.append(DisplayControl.displayGrid(grid, SCALE));

        // Displays the experiment setup
        s.append(DisplayControl.displayExperimentSetup(false, 0));

        // Display total number of iterations required for convergence
        s.append(DisplayControl.displayIterationsCount(iterations));

        // Display the utilities of all the (non-wall) Cells
        s.append(DisplayControl.displayUtilities(grid, optimalPolicy, SCALE));

        // Display the optimal policy
        s.append(DisplayControl.displayPolicy(optimalPolicy, SCALE));

        // Display the utilities of all Cells
        s.append(DisplayControl.displayUtilitiesGrid(optimalPolicy, SCALE));
        return s.toString();
    }


}
