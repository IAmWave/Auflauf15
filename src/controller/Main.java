package controller;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.util.Delay;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World");
        Motor.B.rotate(-720, true);
        Motor.A.setSpeed(360);
        Motor.A.forward();
        Delay.msDelay(1500);
        Motor.A.flt();
        Button.waitForAnyPress();
    }
}
