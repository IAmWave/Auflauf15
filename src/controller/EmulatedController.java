/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import model.Direction;
import model.Exploration;
import model.ExplorationTile;
import model.Map;
import static model.Map.HEIGHT;
import model.Map.Tile;
import static model.Map.WIDTH;

/**
 *
 * @author Václav
 */
public class EmulatedController implements Controller {
    
    long startTime = 0;
    Map map;
    Exploration exp;
    double time = 90;

    public EmulatedController(Exploration exp) {
        map = new Map(new File("data/maps/03.map"), 0);
        this.exp = exp;
    }

    @Override
    public void turn(int times) {
        time -= Math.abs(Exploration.TURN_COST * times);
        exp.print();
        exp.setRotation(Direction.fromInt((exp.getDirection().n + times + 4) % 4));
    }

    @Override
    public void move(int tiles) {
        map.setTileAt(exp.getX(), exp.getY(), Map.Tile.GOOD);
        if (tiles == 0) return;
        time -= Exploration.MOVE_COST;
        int nx = exp.getX() + exp.getDirection().deltaX();
        int ny = exp.getY() + exp.getDirection().deltaY();
        if (map.walkable(nx, ny)) {
            exp.setTile(nx, ny, new ExplorationTile(false));
            exp.setX(nx);
            exp.setY(ny);
            move(tiles - 1);
        } else {
            exp.setTile(nx, ny, new ExplorationTile(true));
        }

    }

    private void print() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (x == exp.getX() && y == exp.getY()) {
                    System.out.print(exp.getDirection().toChar());
                } else {
                    System.out.print(map.tileAt(x, y).toChar());
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public boolean shouldContinue() {
        return time > 0;
    }

    @Override
    public void onStart() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onFinish() {
        int count = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (map.tileAt(x, y) == Tile.BAD) count++;
            }
        }
        exp.print();
        print();
        System.out.println("Unfinished: " + count);
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
