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
    double time = 0;
    int done = 0;
    int doneAt90 = -1;
    final static double TIME_LIMIT = 90;

    public EmulatedController(Exploration exp) {
        map = new Map(new File("data/maps/22.map"));
        this.exp = exp;
        Tile[][] t = map.getTiles();
        boolean sym = true;
        for (int x = 0; x < Map.WIDTH; x++) {
            for (int y = 0; y < Map.HEIGHT; y++) {
                if(!t[x][y].equals(t[Map.WIDTH-1-x][y])) {
                    sym = false;
                    break;
                }
            }
        }
        exp.setSymmetry(sym);
    }

    @Override
    public void turn(int times) {
        exp.clearCache();
        time += Math.abs(Exploration.MOVE_COST * times);
        exp.print();
        exp.setRotation(Direction.fromInt((exp.getDirection().n + times + 4) % 4));
    }

    @Override
    public void move(int tiles) {
        if (time > 90 && doneAt90 == -1) doneAt90 = done;
        scan();
        if (map.tileAt(exp.getX(), exp.getY()) == Map.Tile.BAD) done++;
        map.setTileAt(exp.getX(), exp.getY(), Map.Tile.GOOD);
        if (tiles == 0) return;
        time += Exploration.MOVE_COST;
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
        exp.cacheDecision();
    }

    private void scan() {
        Direction dir = exp.getDirection().turnRight();
        int cx = exp.getX(), cy = exp.getY();
        int i = 0;
        while (map.walkable(cx, cy)) {
            cx += dir.deltaX();
            cy += dir.deltaY();
            i++;
        }
        exp.handleScan(exp.getX(), exp.getY(), dir, i - 1);
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
        return done < Map.OPEN;
        //return time < TIME_LIMIT;
    }

    @Override
    public void onStart() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onFinish() {
        exp.print();
        print();
        if (time > TIME_LIMIT)
            System.out.println("Unfinished at 90: " + (Map.OPEN - doneAt90));
        System.out.println("Unfinished: " + (Map.OPEN - done));
        System.out.println("Robot time: " + time);
        System.out.println("Computation time: " + (System.currentTimeMillis() - startTime) + " ms");
    }
}
