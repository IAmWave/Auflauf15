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
