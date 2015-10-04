package model.emulation;

import java.io.File;
import java.io.PrintWriter;
import model.Map;
import model.pathfinding.Bruteforce;
import model.pathfinding.Greedy;
import model.pathfinding.Path;
import model.pathfinding.PathAlgorithm;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Václav
 */
public class TestLauncher {

    public static final int SEEDS = 10;

    public static void main(String[] args) {
        Map m = new Map(new File("data/maps/12.map"), 0);
        m.print();
        benchmark(m, new Greedy());
        benchmark(m, new Bruteforce());
    }

    private static void benchmark(Map m, PathAlgorithm algo) {
        System.out.println("Algorithm: " + algo.getClass().getSimpleName());
        long t = System.currentTimeMillis();
        Path p = algo.getPath(m);
        if (!p.isValid(m)) {
            System.err.println("\tInvalid path!");
            return;
        }
        System.out.println("\tElapsed time: " + (System.currentTimeMillis() - t) + " ms");
        System.out.println("\tPath length: " + p.length());
        System.out.println("\tRotations: " + p.rotations());
        System.out.println();
    }

    private static void generateAllOptimal() {
        File folder = new File("data/maps");
        for (File mapFile : folder.listFiles()) {
            generateOptimal(mapFile);
        }
    }

    private static void generateOptimal(File f) {
        System.out.println("Generating solutions for " + f.getName());
        try {
            String resPath = "data/solutions/" + f.getName().replaceFirst("[.][^.]+$", "") + ".sol";
            PrintWriter writer = new PrintWriter(resPath, "UTF-8");
            for (int i = 0; i < SEEDS; i++) {
                Map m = new Map(f, i);
                Path p = new Bruteforce().getPath(m);
                writer.println(i + " " + p.length() + " " + p.rotations());
                System.out.println("\tVariant " + i + ": " + p.length() + " steps, " + p.rotations() + " rotations");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
