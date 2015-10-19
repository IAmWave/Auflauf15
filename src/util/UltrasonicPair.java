package util;

/**
 *
 * @author colander
 */
public class UltrasonicPair {

    private int deg;
    private int value;
    
    public UltrasonicPair(int deg, int value) {
        this.deg = deg;
        this.value = value;
    }

    /**
     * @return the deg
     */
    public int getDeg() {
        return deg;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

}
