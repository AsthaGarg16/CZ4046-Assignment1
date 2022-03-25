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

    public static void main(String[] args) {


        GridModel = new GridModel();
        grid = GridModel.getGrid();
        String config_and_results;

        runPolicyIteration(grid);

        config_and_results = displayResults();

        FileIOHandler.writeToFile(utilityList, "policy_iteration_utilities");
        FileIOHandler.writeToTxt(config_and_results, isValueIteration);
    }


    public static void runPolicyIteration(final Cell[][] grid) {

        Utility_Action[][] currUtilArr = new Utility_Action[constants.NUM_COLS][constants.NUM_ROWS];
        Utility_Action[][] newUtilArr = new Utility_Action[constants.NUM_COLS][constants.NUM_ROWS];
        newUtilArr = initializeUtilitiesPolicies();


        utilityList = new ArrayList<>();

        boolean unchanged;

        do {

            UtilityControl.updateUtilities(newUtilArr, currUtilArr);
            Utility_Action[][] currUtilArrCopy =
                    new Utility_Action[constants.NUM_COLS][constants.NUM_ROWS];
            UtilityControl.updateUtilities(currUtilArr, currUtilArrCopy);
            utilityList.add(currUtilArrCopy);


            newUtilArr = UtilityControl.estimateNextUtilities(currUtilArr, grid);

            unchanged = true;


            for (int row = 0; row < constants.NUM_ROWS; row++) {
                for (int col = 0; col < constants.NUM_COLS; col++) {


                    if (!grid[col][row].isWall()) {
                        Utility_Action bestActionUtil =
                                UtilityControl.getBestUtility(col, row, newUtilArr, grid);

                        Action policyAction = newUtilArr[col][row].getAction();
                        Utility_Action policyActionUtil = UtilityControl.getFixedUtility(policyAction, col, row, newUtilArr, grid);

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

    private static Utility_Action[][] initializeUtilitiesPolicies()
    {
        Utility_Action[][] newArr = new Utility_Action[constants.NUM_COLS][constants.NUM_ROWS];
        for (int col = 0; col < constants.NUM_COLS; col++) {
            for (int row = 0; row < constants.NUM_ROWS; row++) {
                newArr[col][row] = new Utility_Action();
                if (!grid[col][row].isWall()) {
                    Action randomAction = Action.getRandomAction();
                    newArr[col][row].setAction(randomAction);
                }
            }
        }
        return newArr;
    }




    private static String displayResults() {
        // Final item in the list is the optimal policy derived by policy iteration
        int latestUtilities = utilityList.size() - 1;
        final Utility_Action[][] optimalPolicy =
                utilityList.get(latestUtilities);
        StringBuilder s = new StringBuilder();

        // Displays the Grid Environment
        s.append(DisplayControl.displayGrid(grid));

        // Displays the experiment setup
        s.append(DisplayControl.displayExperimentSetup(isValueIteration, 0));

        // Display total number of iterations required for convergence
        s.append(DisplayControl.displayIterationsCount(iterations));

        // Display the utilities of all the (non-wall) Cells
        s.append(DisplayControl.displayUtilities(grid, optimalPolicy));

        // Display the optimal policy
        s.append(DisplayControl.displayPolicy(optimalPolicy));

        // Display the utilities of all Cells
        s.append(DisplayControl.displayUtilitiesGrid(optimalPolicy));
        return s.toString();
    }
}
