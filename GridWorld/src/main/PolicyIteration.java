package main;

import java.util.ArrayList;
import java.util.List;

import entity.*;

import control.UtilityControl;
import control.FileIOHandler;
import control.DisplayControl;

public class PolicyIteration {

    public static GridModel GridModel = null;
    private static List<Utility_Action[][]> utilityList;
    private static Cell[][] grid;
    private static int iterations = 0;
    private static boolean isValueIteration = false;
    private static int SCALE = 1;

    public static void main(String[] args) {


        GridModel = new GridModel();
        grid = GridModel.getGrid();
        String config_and_results;

        runPolicyIteration(grid);

        config_and_results = displayResults(); //for saving to txt
        //write the utilities to the csv file
        FileIOHandler.writeToFile(utilityList, "policy_iteration_utilities", 1);
        //write the data to txt
        FileIOHandler.writeToTxt(config_and_results, isValueIteration, SCALE);
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


    private static String displayResults() {
        // Final item in the list is the optimal policy derived by policy iteration

        final Utility_Action[][] optimalPolicy =
                utilityList.get(utilityList.size() - 1);
        StringBuilder s = new StringBuilder();

        // Displays the Grid Environment
        s.append(DisplayControl.displayGrid(grid, SCALE));

        // Displays the experiment setup
        s.append(DisplayControl.displayExperimentSetup(isValueIteration, 0));

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
