/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pathfinding;

/**
 *
 * @author Václav
 */
public enum Move {

    UP(0), RIGHT(1), DOWN(2), LEFT(3);
    public final int x;
    private final static int ddx[] = {0, 1, 0, -1};
    private final static int ddy[] = {-1, 0, 1, 0};

    Move(int x) {
        this.x = x;
    }

    public static Move intToMove(final int i) {
        for (Move m : Move.values())
            if (m.x == i) return m;
        return null;
    }

    public Move turnLeft() {
        return intToMove((x + 3) % 4);
    }

    public Move turnRight() {
        return intToMove((x + 1) % 4);
    }

    public int deltaX() {
        return ddx[x];
    }

    public int deltaY() {
        return ddy[x];
    }
    
    public char toChar(){
        char[] res = {'^', '>', 'v', '<'};
        return res[x];
    }
}
