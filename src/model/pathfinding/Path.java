/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pathfinding;

import java.util.ArrayList;
import model.Map;
import model.Map.Tile;

/**
 *
 * @author Václav
 */
public class Path {

    public ArrayList<Move> moves;

    public Path() {
        moves = new ArrayList<>();
    }

    public Path(ArrayList<Move> moves) {
        this.moves = moves;
    }

    public void append(Path p) {
        moves.addAll(p.moves);
    }

    public Move back() {
        return moves.get(moves.size() - 1);
    }

    public int length() {
        return moves.size();
    }

    public int rotations() {
        int res = 0;
        Move prev = Move.UP;
        for (Move cur : moves) {
            int delta = Math.abs(cur.x - prev.x);
            if (delta == 3) delta = 1;
            res += delta;
            prev = cur;
        }
        return res;
    }

    public boolean isValid(Map map) {
        Tile[][] tiles = map.getTiles();
        int cx = Map.START_X;
        int cy = Map.START_Y;
        for (Move cur : moves) {
            tiles[cx][cy] = Tile.GOOD;
            cx += cur.deltaX();
            cy += cur.deltaY();
            if (!map.walkable(cx, cy)) {
                //System.err.println("The path goes out of bounds.");
                return false;
            }
            tiles[cx][cy] = Tile.GOOD;
        }
        for (int x = 0; x < Map.WIDTH; x++) for (int y = 0; y < Map.HEIGHT; y++) {
                if (tiles[x][y] == Tile.BAD) {
                    //System.err.println("The path doesn't fix everything.");
                    return false;
                }
            }
        return true;
    }

    public void print() {
        for (Move m : moves) {
            System.out.print(m + " ");
        }
        System.out.println();
    }

    public void prettyPrint(Map map) {
        Tile[][] tiles = map.getTiles();
        int cx = Map.START_X;
        int cy = Map.START_Y;
        Move rot = Move.UP;
        for (int i = 0; i <= moves.size(); i++) {
            for (int y = 0; y < Map.HEIGHT; y++) {
                for (int x = 0; x < Map.WIDTH; x++) {
                    if (x == cx && y == cy) System.out.print(rot.toChar());
                    else System.out.print(tiles[x][y].toChar());
                }
                System.out.println();
            }
            System.out.println();
            tiles[cx][cy] = Tile.GOOD;
            if (i < moves.size()) {
                cx += moves.get(i).deltaX();
                cy += moves.get(i).deltaY();
                rot = moves.get(i);
            }
        }
    }
}
