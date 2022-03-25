package entity;

public class Utility_Action implements Comparable<Utility_Action> {

    private Action action = null;
    private double util = 0.000;

    public Utility_Action() {
        action = null;
        util = 0.000;
    }

    public Utility_Action(Action action, double util) {
        this.action = action;
        this.util = util;
    }

    public Action getAction() {
        return action;
    }

    public String getActionStr() {

        // No action at wall, otherwise return one of the 4 possible actions
        return action != null ? action.toString() : " Wall";
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public double getUtil() {
        return util;
    }

    public void setUtil(double util) {
        this.util = util;
    }

    @Override
    public int compareTo(Utility_Action other) {

        // Descending order based on utility values
        return ((Double) other.getUtil()).compareTo(getUtil());
    }

}