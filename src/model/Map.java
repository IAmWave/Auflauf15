/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

        WALL('#'), GOOD('.'), BAD('*');

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

    public Map(Tile[][] values) {
        this.values = values;
    }

    public Tile tileAt(int x, int y) {
        return values[x][y];
    }

    public void setTileAt(int x, int y, Tile to) {
        values[x][y] = to;
    }

    public Tile[][] getTiles() {
        Tile[][] res = new Tile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) for (int y = 0; y < HEIGHT; y++) {
                res[x][y] = values[x][y];
            }
        return res;
    }

    public Map(File src) {
        int walls = 0;
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
            r.readLine(); //prvni radek se uz nepouziva
            for (int y = 0; y < HEIGHT; y++) {
                String line = r.readLine();
                for (int x = 0; x < WIDTH; x++) {
                    values[x][y] = Tile.charToTile(line.charAt(x));
                    if (values[x][y] == Tile.WALL) walls++;
                }
            }
            if (walls != WIDTH * HEIGHT - OPEN) {
                System.err.println("Warning: Invalid map!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean walkable(int x, int y) {
        return (x >= 0) && (y >= 0) && (x < WIDTH) && (y < HEIGHT)
                && (values[x][y] != Tile.WALL);
    }
}
