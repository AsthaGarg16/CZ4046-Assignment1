package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entity.Utility_Action;
import entity.Cell;
import entity.Action;
import entity.constants;

public class UtilityControl {

    //Calculates the utility for each possible action and returns the action with maximal utility
    public static Utility_Action getBestUtility(final int col, final int row, final Utility_Action[][] currUtilArr, final Cell[][] grid, int scale) {

        List<Utility_Action> utilities = new ArrayList<>();

        utilities.add(new Utility_Action(Action.UP, getActionUpUtility(col, row, currUtilArr, grid, scale)));
        utilities.add(new Utility_Action(Action.DOWN, getActionDownUtility(col, row, currUtilArr, grid, scale)));
        utilities.add(new Utility_Action(Action.LEFT, getActionLeftUtility(col, row, currUtilArr, grid, scale)));
        utilities.add(new Utility_Action(Action.RIGHT, getActionRightUtility(col, row, currUtilArr, grid, scale)));

        Collections.sort(utilities);
        return utilities.get(0);
    }


    //Calculates the utility for the given action
    public static Utility_Action getFixedUtility(final Action action, final int col,
                                          final int row, final Utility_Action[][] actionUtilArr, final Cell[][] grid, int scale) {

        return switch (action) {
            case UP -> new Utility_Action(Action.UP, UtilityControl.getActionUpUtility(col, row, actionUtilArr, grid, scale));
            case DOWN -> new Utility_Action(Action.DOWN, UtilityControl.getActionDownUtility(col, row, actionUtilArr, grid, scale));
            case LEFT -> new Utility_Action(Action.LEFT, UtilityControl.getActionLeftUtility(col, row, actionUtilArr, grid, scale));
            case RIGHT -> new Utility_Action(Action.RIGHT, UtilityControl.getActionRightUtility(col, row, actionUtilArr, grid, scale));
        };
    }

    // Simplified Bellman update to produce the next utility estimate
    public static Utility_Action[][] estimateNextUtilities(final Utility_Action[][] utilArr, final Cell[][] grid, int scale) {

        Utility_Action[][] currUtilArr = new Utility_Action[constants.NUM_COLS*scale][constants.NUM_ROWS*scale];
        for (int col = 0; col < constants.NUM_COLS*scale; col++) {
            for (int row = 0; row < constants.NUM_ROWS*scale; row++) {
                currUtilArr[col][row] = new Utility_Action();
            }
        }

        Utility_Action[][] newUtilArr = new Utility_Action[constants.NUM_COLS*scale][constants.NUM_ROWS*scale];
        for (int col = 0; col < constants.NUM_COLS*scale; col++) {
            for (int row = 0; row < constants.NUM_ROWS*scale; row++) {
                newUtilArr[col][row] = new Utility_Action(utilArr[col][row].getAction(), utilArr[col][row].getUtil());
            }
        }


        int k = 0;
        do {
            for (int i = 0; i < newUtilArr.length; i++) {
                System.arraycopy(newUtilArr[i], 0, currUtilArr[i], 0, newUtilArr[i].length);
            }

            // For each state
            for (int row = 0; row < constants.NUM_ROWS*scale; row++) {
                for (int col = 0; col < constants.NUM_COLS*scale; col++) {
                    if (!grid[col][row].isWall()) {
                        // Updates the utility based on the action stated in the policy
                        Action action = currUtilArr[col][row].getAction();
                        newUtilArr[col][row] = UtilityControl.getFixedUtility(action,
                                col, row, currUtilArr, grid, scale);
                    }
                }
            }
            k++;
        } while(k < constants.K);

        return newUtilArr;
    }


    //Calculates the utility for attempting to move up
    public static double getActionUpUtility(final int col, final int row,
                                            final Utility_Action[][] currUtilArr, final Cell[][] grid, int scale) {

        double actionUpUtility = 0.000;

        // Intends to move up
        actionUpUtility += constants.PROB_INTENT * moveUp(col, row, currUtilArr, grid);

        // Intends to move up, but moves right instead
        actionUpUtility += constants.PROB_RIGHT * moveRight(col, row, currUtilArr, grid, scale);

        // Intends to move up, but moves left instead
        actionUpUtility += constants.PROB_LEFT * moveLeft(col, row, currUtilArr, grid);

        // Final utility
        actionUpUtility = grid[col][row].getReward() + constants.DISCOUNT * actionUpUtility;

        return actionUpUtility;
    }

    //Calculates the utility for attempting to move down
    public static double getActionDownUtility(final int col, final int row,
                                              final Utility_Action[][] currUtilArr, final Cell[][] grid, int scale) {

        double actionDownUtility = 0.000;

        // Intends to move down
        actionDownUtility += constants.PROB_INTENT * moveDown(col, row, currUtilArr, grid, scale);

        // Intends to move down, but moves left instead
        actionDownUtility += constants.PROB_LEFT * moveLeft(col, row, currUtilArr, grid);

        // Intends to move down, but moves right instead
        actionDownUtility += constants.PROB_RIGHT * moveRight(col, row, currUtilArr, grid, scale);

        // Final utility
        actionDownUtility = grid[col][row].getReward() + constants.DISCOUNT * actionDownUtility;

        return actionDownUtility;
    }

    //Calculates the utility for attempting to move left
    public static double getActionLeftUtility(final int col, final int row,
                                              final Utility_Action[][] currUtilArr, final Cell[][] grid, int scale) {

        double actionLeftUtility = 0.000;

        // Intends to move left
        actionLeftUtility += constants.PROB_INTENT * moveLeft(col, row, currUtilArr, grid);

        // Intends to move left, but moves up instead
        actionLeftUtility += constants.PROB_RIGHT * moveUp(col, row, currUtilArr, grid);

        // Intends to move left, but moves down instead
        actionLeftUtility += constants.PROB_LEFT * moveDown(col, row, currUtilArr, grid, scale);

        // Final utility
        actionLeftUtility = grid[col][row].getReward() + constants.DISCOUNT * actionLeftUtility;

        return actionLeftUtility;
    }

    // Calculates the utility for attempting to move right
    public static double getActionRightUtility(final int col, final int row,
                                               final Utility_Action[][] currUtilArr, final Cell[][] grid, int scale) {

        double actionRightUtility = 0.000;

        // Intends to move right
        actionRightUtility += constants.PROB_INTENT * moveRight(col, row, currUtilArr, grid, scale);

        // Intends to move right, but moves down instead
        actionRightUtility += constants.PROB_RIGHT * moveDown(col, row, currUtilArr, grid, scale);

        // Intends to move right, but moves up instead
        actionRightUtility += constants.PROB_LEFT * moveUp(col, row, currUtilArr, grid);

        // Final utility
        actionRightUtility = grid[col][row].getReward() + constants.DISCOUNT * actionRightUtility;

        return actionRightUtility;
    }

    // Attempts to move up
    public static double moveUp(final int col, final int row, final Utility_Action[][] currUtilArr, final Cell[][] grid) {

        if (row - 1 >= 0 && !grid[col][row - 1].isWall()) {
            return currUtilArr[col][row - 1].getUtil();
        }
        return currUtilArr[col][row].getUtil();
    }

    // Attempts to move down
    public static double moveDown(final int col, final int row, final Utility_Action[][] currUtilArr, final Cell[][] grid, int scale) {
        if (row + 1 < constants.NUM_ROWS*scale && !grid[col][row + 1].isWall()) {
            return currUtilArr[col][row + 1].getUtil();
        }
        return currUtilArr[col][row].getUtil();
    }

    // Attempts to move left
    public static double moveLeft(final int col, final int row, final Utility_Action[][] currUtilArr, final Cell[][] grid) {
        if (col - 1 >= 0 && !grid[col - 1][row].isWall()) {
            return currUtilArr[col - 1][row].getUtil();
        }
        return currUtilArr[col][row].getUtil();
    }

    // Attempts to move right
    public static double moveRight(final int col, final int row, final Utility_Action[][] currUtilArr, final Cell[][] grid, int scale) {
        if (col + 1 < constants.NUM_COLS*scale && !grid[col + 1][row].isWall()) {
            return currUtilArr[col + 1][row].getUtil();
        }
        return currUtilArr[col][row].getUtil();
    }

    // Copy the contents from the source array to the destination array
    public static void updateUtilities(Utility_Action[][] aSrc, Utility_Action[][] aDest) {
        for (int i = 0; i < aSrc.length; i++) {
            System.arraycopy(aSrc[i], 0, aDest[i], 0, aSrc[i].length);
        }
    }
}
