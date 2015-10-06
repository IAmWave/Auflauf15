/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Václav
 */
public class ExplorationTile {

    public double wall = 0.5;
    public boolean visited = false;

    public ExplorationTile() {
    }

    public ExplorationTile(boolean isWall) {
        wall = isWall ? 1 : 0;
        visited = true;
    }
}
