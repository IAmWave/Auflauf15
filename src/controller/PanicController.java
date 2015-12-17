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

    final int PANIC_FROM_WALL = 45;
    final int PANIC_UNSTUCK_TIME = 3000; //jak dlouho se musi tocit na miste
    final int PANIC_TO_WALL_SPEED = 600;

    final int DISTANCE_MIN = 4; //4
    final int DISTANCE_MAX = 11; //10

    final int FROM_WALL_SPEED = 350;
    final int SPEED = 450; //450
    final int DELTA_MAX = 280; //280
    final double FLAT_DECREASE = 65; //65
    final double ACCEL = FLAT_DECREASE + 110; //105
    final static double INTERPOLATION_EXP = 1; //vyzkouset

    double delta = 0;

    long lastTurn = 0;
    final int MIN_TURN_DELAY = 500;

    RobotController c;
    NXTRegulatedMotor left, right;

    int turnsDone = 0;
    int turnsLimit = -1;

    private void init() {
        left = c.left;
        right = c.right;
    }

    public PanicController(RobotController c) {
        this.c = c;
        init();
    }

    public PanicController(RobotController c, int turnsLimit) {
        this.c = c;
        this.turnsLimit = turnsLimit;
        init();
    }

    public static double interpolate(double x) { //na -0.5 az 0.5
        x = (x - 0.5) * 2;
        int sign = x > 0 ? 1 : (x < 0 ? -1 : 0);
        return Math.pow(Math.abs(x), INTERPOLATION_EXP) * sign / 2;
    }

    public void panic() {
        left.backward();
        right.backward();
        long overTime = -1;
        int last = 10;
        int raw = 0;
        boolean lastMax = false;
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
                        makeTurn();
                    }
                }
            } else {
                overTime = -1;
            }
            //naraz
            if (c.touchL.isPressed() && c.touchR.isPressed()) {
                makeTurn();
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
                if (!lastMax && lastTurn + MIN_TURN_DELAY < System.currentTimeMillis()) {
                    Sound.playTone(2000, 100);
                    turnsDone++;
                    lastTurn = System.currentTimeMillis();
                }
                lastMax = true;
            } else lastMax = false;
            delta = Math.min(DELTA_MAX, Math.max(-DELTA_MAX, delta));
            left.setSpeed(SPEED - (int) delta);
            right.setSpeed(SPEED + (int) delta);
        }
    }

    private void makeTurn() {
        left.setSpeed(FROM_WALL_SPEED);
        right.setSpeed(FROM_WALL_SPEED);
        left.rotate(PANIC_FROM_WALL, true);
        right.rotate(PANIC_FROM_WALL, false);
        c.turn(-1);
        delta = 0;
        turnsDone++;
        if (turnsDone >= turnsLimit && turnsLimit >= 0) {
            turnsDone = -10000;
            goUntilWall();
            makeTurn();
        } else {
            right.setSpeed(SPEED);
            left.backward();
            right.backward();
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
