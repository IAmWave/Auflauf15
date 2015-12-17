package model;

/**
 *
 * @author Václav
 */
public class ExplorationTile {

    private double wall = 0.5;
    public boolean visited = false;

    public ExplorationTile() {
    }

    public ExplorationTile(boolean isWall) {
        wall = isWall ? 1 : 0;
        visited = true;
    }

    public double getWall() {
        return wall;
    }

    public void setWall(double wall) {
        this.wall = wall;
        if (wall == 1) visited = true;
    }

    public char toChar() {
        if (wall == 1) return '#';
        if (visited) return ' ';
        if (wall == 0) return '.';
        if (wall == 0.5) return '?';
        if (wall < 0.5) return ',';
        return 'X';
    }
}
