/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import model.ai.AI;
import model.ai.GreedyAI;

/**
 *
 * @author Václav
 */
public class Exploration {

    public static final double MOVE_COST = 1.5;
    public static final double TURN_COST = 1;
    
    public static final double SCAN_FREE_COEF = 0.3;
    public static final double SCAN_WALL_COEF = 0.3;
    ExplorationTile[][] map = new ExplorationTile[Map.WIDTH][Map.HEIGHT];
    int x = Map.START_X;
    int y = Map.START_Y;
    Direction rot = Direction.UP;

    AI ai;

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

    public Move decide() {
        return ai.decide();
    }

    public Direction getDirection() {
        return rot;
    }

    public void setRotation(Direction to) {
        rot = to;
    }

    public void handleScan(int sx, int sy, Direction dir, int value) { //value = počet políček před zdí
        int cx = sx, cy = sy;
        for (int i = 0; i < value; i++) {
            cx += dir.deltaX();
            cy += dir.deltaY();
            if (!possiblyFree(cx, cy)) {
                return;
            }
        }
        cx = sx;
        cy = sy;
        for (int i = 0; i < value; i++) {
            cx += dir.deltaX();
            cy += dir.deltaY();
            map[cx][cy].wall = SCAN_FREE_COEF * map[cx][cy].wall;
        }
        cx += dir.deltaX();
        cy += dir.deltaY();
        if (inBounds(cx, cy)) {
            map[cx][cy].wall = 1 - (1 - map[cx][cy].wall) * SCAN_WALL_COEF;
        }
    }

    public ExplorationTile tileAt(int x, int y) {
        return map[x][y];
    }

    public void setTile(int x, int y, ExplorationTile tile) {
        if (!inBounds(x, y)) return;
        map[x][y] = tile;
    }

    public static boolean inBounds(int x, int y) {
        return !(x < 0 || y < 0 || x >= Map.WIDTH || y >= Map.HEIGHT);
    }

    public boolean shouldVisit(int x, int y) {
        if (!possiblyFree(x, y)) return false;
        if (map[x][y].visited) return false;
        if (map[x][y].wall <= 0.5) return true;
        return false;
    }

    public boolean possiblyFree(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }
        if (map[x][y].wall == 1) {
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
            System.out.println("|");
        }
        System.out.println(" ---------");
    }
}
