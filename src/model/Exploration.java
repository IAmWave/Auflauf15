package model;

import model.ai.AI;
import model.ai.GreedyAI;

/**
 *
 * @author Václav
 */
public class Exploration {

    public static final double MOVE_COST = 1.3;
    //vyssi = duveryhodnejsi
    public static final double[] SCAN_FREE_COEF = {1, 0.9, 0.8};
    public static final double[] SCAN_WALL_COEF = {1, 0, 0};
    public static final double SINGLE_INTEREST = 0.01;

    ExplorationTile[][] map = new ExplorationTile[Map.WIDTH][Map.HEIGHT];
    int x = Map.START_X;
    int y = Map.START_Y;
    Direction rot = Direction.UP;
    boolean symmetry = false;
    boolean skipSingles = true;
    boolean preferLeft = false;
    AI ai;
    Move decisionCache = null;

    public Exploration() {
        for (int x = 0; x < Map.WIDTH; x++) {
            for (int y = 0; y < Map.HEIGHT; y++) {
                map[x][y] = new ExplorationTile();
            }
        }
        map[Map.START_X - 1][Map.START_Y + 1] = new ExplorationTile(true);
        map[Map.START_X][Map.START_Y + 1] = new ExplorationTile(true);
        map[Map.START_X + 1][Map.START_Y + 1] = new ExplorationTile(true);
        map[Map.START_X][Map.START_Y] = new ExplorationTile(false);
        ai = new GreedyAI(this);
    }

    public void clearCache() {
        decisionCache = null;
    }

    public void cacheDecision() {
        decisionCache = ai.decide();
    }

    public Move decide() {
        if (decisionCache == null) {
            return ai.decide();
        } else {
            return decisionCache;
        }
    }

    public Direction getDirection() {
        return rot;
    }

    public void setRotation(Direction to) {
        rot = to;
    }

    public void handleScan(int sx, int sy, Direction dir, int value) { //value = počet políček před zdí
        if (value > 2) value = 2;
        int cx = sx, cy = sy;
        for (int i = 0; i < value; i++) {
            cx += dir.deltaX();
            cy += dir.deltaY();
            if (!possiblyFree(cx, cy)) {
                return;
            }
        }
        double freeCoef = value < SCAN_FREE_COEF.length ? SCAN_FREE_COEF[value]
                : SCAN_FREE_COEF[SCAN_FREE_COEF.length - 1];
        double wallCoef = value < SCAN_WALL_COEF.length ? SCAN_WALL_COEF[value]
                : SCAN_WALL_COEF[SCAN_WALL_COEF.length - 1];
        cx = sx;
        cy = sy;
        for (int i = 0; i < value; i++) {
            cx += dir.deltaX();
            cy += dir.deltaY();
            map[cx][cy].setWall((1 - freeCoef) * map[cx][cy].getWall());
            if (symmetry)
                map[Map.WIDTH - 1 - cx][cy].setWall(map[cx][cy].getWall());
        }
        cx += dir.deltaX();
        cy += dir.deltaY();
        if (inBounds(cx, cy)) {
            map[cx][cy].setWall(1 - (1 - map[cx][cy].getWall()) * (1 - wallCoef));
            if (symmetry)
                map[Map.WIDTH - 1 - cx][cy].setWall(map[cx][cy].getWall());
        }
    }

    public ExplorationTile tileAt(int x, int y) {
        return map[x][y];
    }

    public void setTile(int x, int y, ExplorationTile tile) {
        if (!inBounds(x, y)) {
            return;
        }
        if (map[x][y].visited) return; //vime, co vime, dalsi nas nezajima
        map[x][y] = tile;
        if (symmetry) map[Map.WIDTH - 1 - x][y].setWall(map[x][y].getWall());
    }

    public static boolean inBounds(int x, int y) {
        return !(x < 0 || y < 0 || x >= Map.WIDTH || y >= Map.HEIGHT);
    }

    public double getInterest(int x, int y) {
        if (!inBounds(x, y)) return 0;
        if (map[x][y].visited) return 0;
        if (skipSingles) {
            int walls = 0;
            for (int i = 0; i < 4; i++) {
                if (!possiblyFree(x + Direction.fromInt(i).deltaX(),
                        y + Direction.fromInt(i).deltaY())) {
                    walls++;
                }
            }
            if(walls >= 3) return SINGLE_INTEREST;
        }
        if(map[x][y].getWall() == 0.5) return 2;
        if (map[x][y].getWall() > 0.5) return 0.5;
        return 2 - map[x][y].getWall();
    }

    public boolean possiblyFree(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }
        if (map[x][y].getWall() == 1) {
            return false;
        }
        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void print() {
        System.out.println(" ---------");
        for (int y = 0; y < Map.HEIGHT; y++) {
            System.out.print("|");
            for (int x = 0; x < Map.WIDTH; x++) {
                if (x == getX() && y == getY()) {
                    System.out.print(getDirection().toChar());
                } else {
                    System.out.print(map[x][y].toChar());
                }
            }
            System.out.println("|" + (y == 0 ? " " + getX() + " " + getY() : ""));
        }
        System.out.println(" ---------");
    }

    public void setSymmetry(boolean to) {
        symmetry = to;
    }

    public boolean getSymmetry() {
        return symmetry;
    }
    
    public boolean getPreferLeft(){
        return preferLeft;
    }
}
