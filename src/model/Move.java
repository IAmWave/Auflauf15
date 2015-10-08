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
//Reprezentuje naplánovaný tah - směr a počet políček.
public class Move {

    public Direction dir;
    public int tiles;

    public Move(Direction dir, int tiles) {
        this.dir = dir;
        this.tiles = tiles;
    }
}
