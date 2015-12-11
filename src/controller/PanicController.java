package controller;

import lejos.nxt.Button;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.util.Delay;

/**
 *
 * @author Václav
 */
public class PanicController {

    final int PANIC_FROM_WALL = 40;
    final int PANIC_UNSTUCK_TIME = 3000; //jak dlouho se musi tocit na miste
    final int PANIC_TO_WALL_SPEED = 600;

    final int DISTANCE_MIN = 5; //4
    final int DISTANCE_MAX = 11; //10
    
    final int FROM_WALL_SPEED = 350;
    final int SPEED = 450; //450
    final int DELTA_MAX = 260; //280
    final double FLAT_DECREASE = 65; //65
    final double ACCEL = FLAT_DECREASE + 105; //105

    double delta = 0;

    RobotController c;
    NXTRegulatedMotor left, right;

    public PanicController(RobotController c) {
        this.c = c;
        left = c.left;
        right = c.right;
        panic();
    }

    public static double interpolate(double x){
        x-= 0.5;
        int sign = x>0?1:(x<0?-1:0);
        return Math.sqrt(Math.abs(x))/Math.sqrt(2)*sign;
    }
    
    public void panic() {
        left.backward();
        right.backward();
        long overTime = -1;
        int last = 10;
        int raw = 0;
        while (Button.readButtons() != Button.ESCAPE.getId()) {
            boolean doRotate = false;
            last = raw;
            raw = c.sonic.getDistance();

            if (raw > DISTANCE_MAX) {
                if (overTime == -1) {
                    overTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - overTime > PANIC_UNSTUCK_TIME) {
                        overTime = -1;
                        goUntilWall();
                        doRotate = true;
                    }
                }
            } else {
                overTime = -1;
            }
            if (c.touchR.isPressed() || doRotate) {
                left.setSpeed(FROM_WALL_SPEED);
                right.setSpeed(FROM_WALL_SPEED);
                left.rotate(PANIC_FROM_WALL, true);
                right.rotate(PANIC_FROM_WALL, false);
                c.turn(-1);
                right.setSpeed(SPEED);
                left.backward();
                right.backward();
                delta = 0;
            }
            int read = Math.min(DISTANCE_MAX, Math.max(DISTANCE_MIN, raw));
            double coef = ((double) read - DISTANCE_MIN) / ((double) DISTANCE_MAX - DISTANCE_MIN);
            coef = interpolate(coef);
            if (delta < 0) {
                delta = Math.min(0, delta + FLAT_DECREASE);
            }
            if (delta > 0) {
                delta = Math.max(0, delta - FLAT_DECREASE);
            }
            delta = delta + ACCEL * coef;
            //delta = delta * KEPT + (1 - KEPT) * ACCEL * (coef - 0.5);

            if (Math.abs(delta) > DELTA_MAX) {
                Sound.playTone(2000, 100);
            }
            delta = Math.min(DELTA_MAX, Math.max(-DELTA_MAX, delta));
            left.setSpeed(SPEED - (int) delta);
            right.setSpeed(SPEED + (int) delta);
        }
    }

    public void goUntilWall() {
        left.setSpeed(PANIC_TO_WALL_SPEED);
        right.setSpeed(PANIC_TO_WALL_SPEED);
        left.backward();
        right.backward();
        while (!c.touchL.isPressed() && !c.touchR.isPressed()) {
            Delay.msDelay(20);
        }
        left.flt();
        right.flt();
    }
}
