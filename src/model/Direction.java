/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Václav
 */
public enum Direction {

    UP(0), RIGHT(1), DOWN(2), LEFT(3);
    public final int n;
    private final static int ddx[] = {0, 1, 0, -1};
    private final static int ddy[] = {-1, 0, 1, 0};

    Direction(int x) {
        this.n = x;
    }

    public static Direction intToMove(final int i) {
        for (Direction m : Direction.values())
            if (m.n == i) return m;
        return null;
    }

    public Direction turnLeft() {
        return intToMove((n + 3) % 4);
    }

    public Direction turnRight() {
        return intToMove((n + 1) % 4);
    }

    public int deltaX() {
        return ddx[n];
    }

    public int deltaY() {
        return ddy[n];
    }

    public char toChar() {
        char[] res = {'^', '>', 'v', '<'};
        return res[n];
    }

    public int rotationTo(Direction b) {
        int val = b.n - n;
        if (val == 3) val = -1;
        if (val == -3) val = 1;
        return val;
    }
}
