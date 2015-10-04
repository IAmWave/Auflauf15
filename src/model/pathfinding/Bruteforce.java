/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pathfinding;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import model.Map;

/**
 *
 * @author Václav
 */
public class Bruteforce extends PathAlgorithm {

    private class State {

        byte pos;
        int mask;

        private State(byte pos, int done) {
            this.pos = pos;
            this.mask = done;
        }
    }

    @Override
    public Path getPath(Map map) {
        ArrayList<Point> bad = new ArrayList<>();
        for (int x = 0; x < Map.WIDTH; x++) for (int y = 0; y < Map.HEIGHT; y++) {
                if (map.tileAt(x, y) == Map.Tile.BAD
                        && (x!=Map.START_X || y!=Map.START_Y))
                    bad.add(new Point(x, y));
            }

        int[][] dp = new int[bad.size()][1 << bad.size()];
        byte[][] from = new byte[bad.size()][1 << bad.size()];
        for (int i = 0; i < bad.size(); i++) for (int j = 0; j < (1 << bad.size()); j++) {
                dp[i][j] = 1000; //inf
                from[i][j] = -1;
            }
        Queue<State> q = new ArrayDeque<>();
        for (int i = 0; i < bad.size(); i++) {
            dp[i][1 << i] = map.getDistanceMatrix()[Map.START_X][Map.START_Y][bad.get(i).x][bad.get(i).y];
            q.add(new State((byte) i, 1 << i));
        }
        while (!q.isEmpty()) {
            State s = q.poll();
            for (byte i = 0; i < bad.size(); i++) {
                if ((s.mask & 1 << i) == 0) {
                    int next = s.mask | (1 << i);
                    int val = dp[s.pos][s.mask]
                            + map.getDistanceMatrix()[bad.get(s.pos).x][bad.get(s.pos).y][bad.get(i).x][bad.get(i).y];
                    if (from[i][next] == -1) q.add(new State(i, next));
                    if (dp[i][next] > val) {
                        from[i][next] = s.pos;
                        dp[i][next] = val;
                    }
                }
            }
        }
        int best = 0;
        for (int i = 1; i < bad.size(); i++) {
            if (dp[best][(1 << bad.size()) - 1] > dp[i][(1 << bad.size()) - 1]) {
                best = i;
            }
        }
        ArrayList<Point> path = new ArrayList<>();
        int pos = best;
        int mask = (1 << bad.size()) - 1;
        while (mask != 0) {
            path.add(new Point(bad.get(pos).x, bad.get(pos).y));
            int temp = pos;
            pos = from[pos][mask];
            mask = mask - (1 << temp);
        }
        path.add(new Point(Map.START_X, Map.START_Y));
        Collections.reverse(path);
        return PathAlgorithm.orderToPath(map, path);
        /*
        Path res = new Path();
        Move rot = Move.UP;
        for (int i = 1; i < path.size(); i++) {
            res.append(map.findPath(path.get(i-1).x, path.get(i-1).y, path.get(i).x, path.get(i).y, rot));
            rot = res.back();
        }
        return res;*/
    }
}
