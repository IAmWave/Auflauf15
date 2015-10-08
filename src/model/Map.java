/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Václav
 */
public class Map {

    public static final int WIDTH = 9;
    public static final int HEIGHT = 6;
    public static final int N = WIDTH * HEIGHT;
    public static final int OPEN = 40; //tiles minus walls
    public static final int BAD_COUNT = 20;
    public static final int START_X = 4;
    public static final int START_Y = 2;

    //<editor-fold desc="Tile" defaultstate="collapsed">
    public enum Tile {

        WALL('#'), GOOD('\''), BAD('*');

        private final char type;

        Tile(char type) {
            this.type = type;
        }

        public char toChar() {
            return type;
        }

        public static Tile charToTile(final char c) {
            for (Tile t : Tile.values())
                if (t.type == c) return t;

            return null;
        }
    }
    //</editor-fold>

    private Tile[][] values = new Tile[WIDTH][HEIGHT];
    private int[][][][] matrix = new int[WIDTH][HEIGHT][WIDTH][HEIGHT];

    public Map(Tile[][] values) {
        this.values = values;
        calculateDistanceMatrix();
    }

    public Tile tileAt(int x, int y) {
        return values[x][y];
    }

    public Tile[][] getTiles() {
        Tile[][] res = new Tile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) for (int y = 0; y < HEIGHT; y++) {
                res[x][y] = values[x][y];
            }
        return res;
    }

    public Map(File src, int seed) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
            String type = r.readLine();

            for (int y = 0; y < HEIGHT; y++) {
                String line = r.readLine();
                for (int x = 0; x < WIDTH; x++) {
                    values[x][y] = Tile.charToTile(line.charAt(x));
                }
            }
            if (type.equals("r")) {
                Random random = new Random(seed);
                boolean[] good = new boolean[OPEN];
                for (int i = 0; i < (OPEN - BAD_COUNT); i++) {
                    int pos;
                    do {
                        pos = random.nextInt(OPEN);
                    } while (good[pos]);
                    good[pos] = true;
                }
                int i = 0;

                for (int x = 0; x < WIDTH; x++) {
                    for (int y = 0; y < HEIGHT; y++) {
                        if (values[x][y] == Tile.BAD) {
                            if (i == OPEN) {
                                System.err.println("Not enough walls!");
                                System.exit(1);
                            }

                            if (good[i]) values[x][y] = Tile.GOOD;
                            i++;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        calculateDistanceMatrix();
    }

    public void print() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                System.out.print(values[x][y].toChar());
            }
            System.out.println();
        }
    }

    public int[][][][] getDistanceMatrix() {
        return matrix;
    }

    public final void calculateDistanceMatrix() {
        final int INF = 1000;
        for (int a = 0; a < N; a++) for (int b = 0; b < N; b++) {
                matrix[a % WIDTH][a / WIDTH][b % WIDTH][b / WIDTH] = (a == b) ? 0 : (INF);
            }

        for (int x = 0; x < WIDTH; x++) for (int y = 0; y < HEIGHT; y++) {
                if (values[x][y] == Tile.WALL) continue;

                if (x > 0 && values[x - 1][y] != Tile.WALL)
                    matrix[x][y][x - 1][y] = 1;
                if (x < WIDTH - 1 && values[x + 1][y] != Tile.WALL)
                    matrix[x][y][x + 1][y] = 1;
                if (y > 0 && values[x][y - 1] != Tile.WALL)
                    matrix[x][y][x][y - 1] = 1;
                if (y < HEIGHT - 1 && values[x][y + 1] != Tile.WALL)
                    matrix[x][y][x][y + 1] = 1;
            }

        for (int k = 0; k < N; k++) for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) {
                    if (values[k % WIDTH][k / WIDTH] == Tile.WALL) continue;
                    if (values[i % WIDTH][i / WIDTH] == Tile.WALL) continue;
                    if (values[j % WIDTH][j / WIDTH] == Tile.WALL) continue;
                    matrix[i % WIDTH][i / WIDTH][j % WIDTH][j / WIDTH]
                            = Math.min(matrix[i % WIDTH][i / WIDTH][j % WIDTH][j / WIDTH],
                                    matrix[i % WIDTH][i / WIDTH][k % WIDTH][k / WIDTH]
                                    + matrix[k % WIDTH][k / WIDTH][j % WIDTH][j / WIDTH]);
                }
    }

    public boolean walkable(int x, int y) {
        return (x >= 0) && (y >= 0) && (x < WIDTH) && (y < HEIGHT)
                && (values[x][y] != Tile.WALL);
    }
}
