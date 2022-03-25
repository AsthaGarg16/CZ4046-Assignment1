package entity;

public class Cell {
    private double reward = 0.000;

    private boolean hasWall = false;

    public Cell(double reward) {
        this.reward = reward;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public boolean isWall() {
        return hasWall;
    }

    public void setAsWall(boolean hasWall) {
        this.hasWall = hasWall;
    }
}
