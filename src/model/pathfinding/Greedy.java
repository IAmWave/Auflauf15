/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import model.Map;
import model.Map.Tile;

/**
 *
 * @author Václav
 */
public class Greedy extends PathAlgorithm {

    @Override
    public Path getPath(Map map) {
        return PathAlgorithm.orderToPath(map, getGreedyOrder(map));
    }

    public static ArrayList<Point> getGreedyOrder(Map map) {
        HashSet<Point> bad = new HashSet<>();
        for (int x = 0; x < Map.WIDTH; x++) for (int y = 0; y < Map.HEIGHT; y++) {
                if (map.tileAt(x, y) == Tile.BAD
                        && (x != Map.START_X || y != Map.START_Y))
                    bad.add(new Point(x, y));
            }
        ArrayList<Point> order = new ArrayList<>();
        order.add(new Point(Map.START_X, Map.START_Y));
        while (!bad.isEmpty()) {
            int best = 1000; //infinity
            Point bestP = null;
            for (Point p : bad) {
                int cur = map.getDistanceMatrix()[order.get(order.size() - 1).x][order.get(order.size() - 1).y][p.x][p.y];
                if (cur < best) {
                    bestP = p;
                    best = cur;
                }
            }
            order.add(bestP);
            bad.remove(bestP);
        }
        return order;
    }
}
