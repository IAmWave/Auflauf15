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
    final int PANIC_MIN_SPEED = 150; //200
    final int PANIC_MAX_SPEED = 600; //750
    final int PANIC_ROTATION = 60;
    final int PANIC_FROM_WALL = 30;
    final int PANIC_TIME_DELTA = 150;
    final int PANIC_UNSTUCK_TIME = 3000; //jak dlouho se musi tocit na miste
    final int PANIC_TO_WALL_SPEED = 600;
    final int PANIC_AVG_SPEED = (PANIC_MIN_SPEED + PANIC_MAX_SPEED) / 2; //DEBUG

    final int DISTANCE_MIN = 5; //4
    final int DISTANCE_MAX = 11; //10

    final int CORNER_DETECTION_DIST = 25;
    final int CORNER_ANGLE_INIT = 150;
    final int CORNER_ANGLE_TURN = 240;
    final int CORNER_ANGLE_AFTER = 100;
    final boolean CORNER = true;
    
    RobotController c;
    NXTRegulatedMotor left, right;

    public PanicController(RobotController c) {
        this.c = c;
        left = c.left;
        right = c.right;
        panic();
    }

    private void delayAngle(int angle, NXTRegulatedMotor mot) {
        int beginAngle = mot.getTachoCount();
        while (Math.abs(mot.getTachoCount() - beginAngle) < angle && Button.readButtons() != Button.ESCAPE.getId()) {
            Delay.msDelay(10);
        }
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

            if (raw > CORNER_DETECTION_DIST && last > CORNER_DETECTION_DIST && CORNER) {
                Sound.beep();
                left.setSpeed(PANIC_AVG_SPEED);
                right.setSpeed(PANIC_AVG_SPEED);
                delayAngle(CORNER_ANGLE_INIT, right);
                left.setSpeed(PANIC_MIN_SPEED);
                right.setSpeed(PANIC_MAX_SPEED);
                left.forward();
                delayAngle(CORNER_ANGLE_TURN, right);
                left.backward();
                left.setSpeed(PANIC_AVG_SPEED);
                right.setSpeed(PANIC_AVG_SPEED);
                delayAngle(CORNER_ANGLE_AFTER, right);
                Sound.beep();
                left.setSpeed(PANIC_MIN_SPEED);
                right.setSpeed(PANIC_AVG_SPEED);
                while(c.sonic.getDistance() > CORNER_DETECTION_DIST){
                    Delay.msDelay(20);
                }
                Sound.beep();
                last = raw;
                raw = c.sonic.getDistance();
            }

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
                left.rotate(PANIC_FROM_WALL, true);
                right.rotate(PANIC_FROM_WALL, false);
                c.turn(-1);
                right.setSpeed(PANIC_MAX_SPEED);
                left.backward();
                right.backward();
            }
            int read = Math.min(DISTANCE_MAX, Math.max(DISTANCE_MIN, raw));
            double coef = ((double) read - DISTANCE_MIN) / ((double) DISTANCE_MAX - DISTANCE_MIN);
            left.setSpeed((int) (PANIC_MIN_SPEED + (PANIC_MAX_SPEED - PANIC_MIN_SPEED) * (1 - coef)));
            right.setSpeed((int) (PANIC_MIN_SPEED + (PANIC_MAX_SPEED - PANIC_MIN_SPEED) * (coef)));
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
