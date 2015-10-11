/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import model.Direction;
import model.Exploration;
import model.Map;
import model.Move;

/**
 *
 * @author Václav
 */
public abstract class AI {

    protected static final double INF = 1e2;
    Exploration exp;

    public AI(Exploration exp) {
        this.exp = exp;
    }

    public abstract Move decide();

    final protected double[][][] matrix = new double[Map.WIDTH][Map.HEIGHT][4];
    final protected double[][] simpleMatrix = new double[Map.WIDTH][Map.HEIGHT];
    final protected State[][][] fromMatrix = new State[Map.WIDTH][Map.HEIGHT][4];

    public Move moveTowards(int x, int y) {
        Point[] p = pathTo(x, y);
        int[] len = new int[4];
        int best = 0;
        for (int i = 0; i < 4; i++) {
            len[i] = 0;
            int cx = exp.getX();
            int cy = exp.getY();
            for (int j = 1; j < p.length; j++) {
                cx += Direction.fromInt(i).deltaX();
                cy += Direction.fromInt(i).deltaY();
                if (!p[j].equals(new Point(cx, cy))) break;
                len[i]++;
            }
            if (len[i] > len[best]) best = i;
        }
        return new Move(Direction.fromInt(best), len[best]);
    }

    public Point[] pathTo(int x, int y) {
        int z = 0;
        for (int i = 0; i < 4; i++) {
            if (simpleMatrix[x][y] == matrix[x][y][i]) z = i;
        }
        ArrayList<Point> path = new ArrayList<>();
        while (fromMatrix[x][y][z] != null) {
            if (path.isEmpty() || !path.get(path.size() - 1).equals(new Point(x, y)))
                path.add(new Point(x, y));
            State prev = fromMatrix[x][y][z];
            x = prev.x;
            y = prev.y;
            z = prev.z;
        }
        if (path.isEmpty() || !path.get(path.size() - 1).equals(new Point(x, y)))
            path.add(new Point(x, y));
        Collections.reverse(path);
        return path.toArray(new Point[0]);
    }

    public void printMatrix() {
        for (int y = 0; y < Map.HEIGHT; y++) {
            for (int x = 0; x < Map.WIDTH; x++) {
                System.out.printf("%.1f\t", matrix[x][y]);
            }
            System.out.println();
        }
    }

    public double[][] updateCostMatrix() {
        for (int x = 0; x < Map.WIDTH; x++) {
            for (int y = 0; y < Map.HEIGHT; y++) {
                for (int z = 0; z < 4; z++) {
                    matrix[x][y][z] = INF;
                }
            }
        }
        matrix[exp.getX()][exp.getY()][exp.getDirection().n] = 0;
        fromMatrix[exp.getX()][exp.getY()][exp.getDirection().n] = null;
        PriorityQueue<State> q = new PriorityQueue(11, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                State a = (State) o1;
                State b = (State) o2;
                return Double.compare(a.dist, b.dist);
            }
        });

        q.add(new State(exp.getX(), exp.getY(), exp.getDirection().n, 0));

        while (!q.isEmpty()) {
            State s = q.poll();
            //System.out.println(s.x + ", " + s.y + ", " + s.z);
            if (exp.shouldVisit(s.x, s.y)) continue;
            for (int dir = 0; dir < 4; dir++) {
                int turns = Math.abs(Direction.fromInt(dir).rotationTo(Direction.fromInt(s.z)));
                if (matrix[s.x][s.y][dir] > matrix[s.x][s.y][s.z] + Exploration.TURN_COST * turns) {
                    if (matrix[s.x][s.y][dir] != INF) {
                        q.remove(new State(s.x, s.y, dir, matrix[s.x][s.y][dir]));
                    }
                    fromMatrix[s.x][s.y][dir] = s;
                    matrix[s.x][s.y][dir] = matrix[s.x][s.y][s.z] + Exploration.TURN_COST * turns;
                    q.add(new State(s.x, s.y, dir, matrix[s.x][s.y][dir]));
                }
            }
            int x2 = s.x + Direction.fromInt(s.z).deltaX();
            int y2 = s.y + Direction.fromInt(s.z).deltaY();
            if (exp.possiblyFree(x2, y2)) {
                if (matrix[x2][y2][s.z] > matrix[s.x][s.y][s.z] + Exploration.MOVE_COST) {
                    if (matrix[x2][y2][s.z] != INF) {
                        q.remove(new State(x2, y2, s.z, matrix[x2][y2][s.z]));
                    }
                    fromMatrix[x2][y2][s.z] = s;
                    matrix[x2][y2][s.z] = matrix[s.x][s.y][s.z] + Exploration.MOVE_COST;
                    q.add(new State(x2, y2, s.z, matrix[x2][y2][s.z]));
                }
            }
        }
        for (int x = 0; x < Map.WIDTH; x++) {
            for (int y = 0; y < Map.HEIGHT; y++) {
                simpleMatrix[x][y] = INF;
                for (int z = 0; z < 4; z++) {
                    simpleMatrix[x][y] = Math.min(simpleMatrix[x][y], matrix[x][y][z]);
                }
            }
        }
        return simpleMatrix;
    }

    protected class State {

        int x, y, z;
        double dist;

        private State(int x, int y, int z, double dist) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dist = dist;
        }
    }
}
