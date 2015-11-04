package controller;

import java.util.ArrayList;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import model.Direction;
import model.Exploration;
import model.ExplorationTile;
import model.Map;
import util.GoogleSorter;
import util.UltrasonicPair;

/**
 *
 * @author Václav
 */
public class RobotController implements Controller {

    TouchSensor touchL = new TouchSensor(SensorPort.S1);
    TouchSensor touchR = new TouchSensor(SensorPort.S2);
    UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
    NXTRegulatedMotor magnet = Motor.A;
    NXTRegulatedMotor left = Motor.B;
    NXTRegulatedMotor right = Motor.C;
    final int MAX_SPEED = 600;
    final int MAX_TURNING_SPEED = 350;
    final int MIN_SPEED = 100;
    final int DEG_TILE = 400;
    final int DEG_TURN_90 = 182;
    final int DEG_TURN_180 = 366;
    final int ACCELERATION = 25;
    final int DECCELERATION_TIME = 70;
    final int FROM_WALL = 70;
    final int FROM_WALL_SPEED = 100;
    final int SLOW_DOWN_AT = 200;
    final int PANIC_TIME = 500;
    final int TO_WALL = 150;
    final int TO_WALL_SPEED = 300;
    final int ULTRA_OFFSET = 7;
    final int ULTRA_TILE = 27;
    //PANIC MODE
    final int PANIC_SPEED = 300;
    final int PANIC_ROTATION = 90;
    final int PANIC_TIME_DELTA = 200;

    Exploration exp;
    GoogleSorter sorter = new GoogleSorter();

    public RobotController(Exploration exp) {
        RConsole.openUSB(500);
        /*while (!touchL.isPressed()) {
         RConsole.println(sonic.getDistance() + "");
         }
         System.exit(0);*/
        this.exp = exp;
        exp.print();
        //KALIBROVACI CYKLUS
        /*for (int i = 0; i < 10; i++) {
         turn(2);
         }
         System.exit(0);*/
    }

    @Override
    public void move(int tiles) {
        ArrayList<UltrasonicPair> sonicData = new ArrayList<>();
        int x = exp.getX();
        int y = exp.getY();

        boolean fromWall = false;
        if (isBumpable(x, y, exp.getDirection().turnLeft().turnLeft())) {
            //COUVANI PRED JIZDOU
            left.setSpeed(TO_WALL_SPEED);
            right.setSpeed(TO_WALL_SPEED);
            left.rotate(TO_WALL, true);
            right.rotate(TO_WALL);
            //Button.waitForAnyPress();
            fromWall = true;
        }
        boolean bump = false;
        int SLOW_DOWN = SLOW_DOWN_AT;
        if (isBumpable(x + tiles * exp.getDirection().deltaX(), y + tiles * exp.getDirection().deltaY(), exp.getDirection())) {
            bump = true;
            SLOW_DOWN -= TO_WALL;
        }
        //PRICTENI DEG POKUD JE U ZDI
        left.rotate(-DEG_TILE * tiles - (fromWall ? FROM_WALL : 0) - (bump ? TO_WALL : 0), true);
        right.rotate(-DEG_TILE * tiles - (fromWall ? FROM_WALL : 0) - (bump ? TO_WALL : 0), true);
        int deg = left.getTachoCount();
        int targetDeg = deg - DEG_TILE * tiles;
        deg -= (fromWall ? FROM_WALL : 0);
        if (fromWall) {
            targetDeg -= FROM_WALL;
        }
        left.setSpeed(1);
        right.setSpeed(1);
        boolean accelerate = true;
        boolean read = true;
        long singlePress = -1;
        while (left.isMoving() && !(touchL.isPressed() && touchR.isPressed())) {
            if (Button.readButtons() == Button.ESCAPE.getId()) {
                exp.print();
                Delay.msDelay(1000);
                Button.waitForAnyPress();
                System.exit(0);
            }
            if ((touchL.isPressed() || touchR.isPressed()) && singlePress == -1) {
                singlePress = System.currentTimeMillis();
            }
            if (singlePress != -1 && System.currentTimeMillis() - singlePress > PANIC_TIME) {
                panic();
            }
            if (read) {
                sonicData.add(new UltrasonicPair(Math.abs(targetDeg - left.getTachoCount()), sonic.getDistance()));
                //RConsole.println(sonicData.get(sonicData.size() - 1).getDeg() + " " + sonicData.get(sonicData.size() - 1).getValue());
                read = false;
            } else {
                read = true;
            }
            if (accelerate && left.getSpeed() < this.MAX_SPEED) {
                right.setSpeed(left.getSpeed() + ACCELERATION);
                left.setSpeed(left.getSpeed() + ACCELERATION);
                if (left.getSpeed() >= MAX_SPEED) {
                    accelerate = false;
                }
            } else if (Math.abs(left.getTachoCount() - targetDeg) < SLOW_DOWN && left.getSpeed() > MIN_SPEED /*&& !isBumpable(x + tiles * exp.getDirection().deltaX(), y + tiles * exp.getDirection().deltaY(), exp.getDirection())*/) {
                right.setSpeed(left.getSpeed() - ACCELERATION);
                left.setSpeed(left.getSpeed() - ACCELERATION);
            }
            Delay.msDelay(20);
        }
        /*if (isBumpable(x + tiles * exp.getDirection().deltaX(), y + tiles * exp.getDirection().deltaY(), exp.getDirection())) {
         System.out.println("WALL");
         left.setSpeed(MAX_SPEED);
         right.setSpeed(MAX_SPEED);
         left.rotate(-TO_WALL, true);
         right.rotate(-TO_WALL, false);
         }*/
        left.flt(true);
        right.flt(true);

        int tilesFinished = Math.abs((int) Math.round((left.getTachoCount() - deg + 0.0) / DEG_TILE));
        for (int i = 0; i < tilesFinished; i++) {
            x += exp.getDirection().deltaX();
            y += exp.getDirection().deltaY();
            exp.setTile(x, y, new ExplorationTile(false));
            //POCITANI ULTRASONIC DAT
            int centerDeg = i * DEG_TILE - DEG_TILE / 2;
            ArrayList<Integer> tileData = new ArrayList<>();
            for (int j = 0; j < sonicData.size(); j++) {
                if (sonicData.get(j).getDeg() >= centerDeg - (DEG_TILE * 2) / 3 && sonicData.get(j).getDeg() <= centerDeg - DEG_TILE / 4) {
                    if (sonicData.get(j).getValue() != 255) {
                        tileData.add(sonicData.get(j).getValue());
                    }
                }
            }

            if (tileData.size() > 0) {

                Integer[] ints = tileData.toArray(new Integer[tileData.size()]);
                int[] ar = new int[ints.length];
                for (int j = 0; j < ar.length; j++) {
                    ar[j] = ints[j];
                }
                //System.out.println(ar.length);
                sorter.sort(ar);
                //RConsole.println("ARRAY: " + ar.length);
                int medianTiles = (ar[ar.length / 2] - ULTRA_OFFSET) / ULTRA_TILE;
                exp.handleScan(x, y, exp.getDirection(), medianTiles);
                RConsole.println("MEDIAN " + " " + x + " " + y + " " + ar[ar.length / 2]);
            } else {
                RConsole.println("NO DATA MEASURED");
            }
        }
        exp.setX(x);
        exp.setY(y);
        if (!Exploration.inBounds(x, y)) {
            panic(); //Something, somewhere has gone horribly wrong.
        }
        if (touchL.isPressed() && touchR.isPressed()) { //konec dotykem
            x += exp.getDirection().deltaX();
            y += exp.getDirection().deltaY();
            exp.setTile(x, y, new ExplorationTile(true));
            Delay.msDelay(100);
            left.setSpeed(FROM_WALL_SPEED);
            right.setSpeed(FROM_WALL_SPEED);
            left.rotate(FROM_WALL, true);
            right.rotate(FROM_WALL, false);
        }
        System.out.println(sonicData.size());
        //VÝPIS EXTRÉMNĚ ZPOMALUJE
        /*for (int i = 0; i < sonicData.size(); i++) {
         System.out.println("DEG: " + sonicData.get(i).getDeg() + " DIST: " + sonicData.get(i).getValue());
         }*/
        //System.out.println("TILES FINISHED: " + tilesFinished);
    }

    @Override
    public void turn(int times) {//times>0 => doprava
        exp.print();
        int targetDeg = 0;
        if (Math.abs(times) == 1) {
            targetDeg = left.getTachoCount() + DEG_TURN_90 * times;
            left.rotate(DEG_TURN_90 * times, true);
            right.rotate(-DEG_TURN_90 * times, true);
        } else {
            targetDeg = left.getTachoCount() + DEG_TURN_180 * times / 2;
            left.rotate(DEG_TURN_180 * times / 2, true);
            right.rotate(-DEG_TURN_180 * times / 2, true);
        }
        boolean accelerate = true;
        left.setSpeed(40);
        right.setSpeed(40);
        while (left.isMoving()) {
            //System.out.println(left.getSpeed());
            if (accelerate && left.getSpeed() < this.MAX_TURNING_SPEED) {
                right.setSpeed(left.getSpeed() + ACCELERATION);
                left.setSpeed(left.getSpeed() + ACCELERATION);
                if (left.getSpeed() >= MAX_TURNING_SPEED) {
                    accelerate = false;
                    //System.out.println("ACCELERATED AT " + Math.abs(left.getTachoCount() - targetDeg));
                }
            } else if (Math.abs(left.getTachoCount() - targetDeg) < DECCELERATION_TIME && left.getSpeed() > MIN_SPEED) {
                right.setSpeed(left.getSpeed() - ACCELERATION);
                left.setSpeed(left.getSpeed() - ACCELERATION);

            }
            Delay.msDelay(20);
        }
        left.flt(true);
        right.flt(true);
    }

    private boolean isBumpable(int x, int y, Direction dir) {
        int nx = x + dir.deltaX();
        int ny = y + dir.deltaY();
        if (exp.possiblyFree(nx, ny)) {
            return false;
        }
        if (dir == Direction.DOWN && x == Map.START_X && y == Map.START_Y) {
            return false; //jel by na start
        }
        return true;
    }

    private void panic() {
        Sound.beepSequenceUp();
        Button.waitForAnyPress();
        Delay.msDelay(500);
        left.setSpeed(PANIC_SPEED);
        right.setSpeed(PANIC_SPEED);
        long singlePress = -1;
        left.backward();
        right.backward();
        while (Button.readButtons() == 0) {
            if (touchL.isPressed() && touchR.isPressed()) {
                //otocit o 90
                pullBack();
                left.rotate(DEG_TURN_90, true);
                right.rotate(-DEG_TURN_90, true);
            }
            if (touchL.isPressed() || touchR.isPressed()) {
                if (singlePress == -1) singlePress = System.currentTimeMillis();
            } else singlePress = -1;
            if (singlePress != -1 && System.currentTimeMillis() - singlePress > PANIC_TIME_DELTA) {
                pullBack();
                int coef = touchL.isPressed() ? 1 : (-1);
                left.rotate(PANIC_ROTATION * coef, true);
                right.rotate(-PANIC_ROTATION * coef, false);
            }
            Delay.msDelay(25);
        }
        System.exit(0);
    }

    private void pullBack() {
        left.flt(true);
        right.flt(true);
        Delay.msDelay(100);
        left.setSpeed(FROM_WALL_SPEED);
        right.setSpeed(FROM_WALL_SPEED);
        left.rotate(FROM_WALL, true);
        right.rotate(FROM_WALL, false);
        left.setSpeed(PANIC_SPEED);
        right.setSpeed(PANIC_SPEED);
    }

    @Override
    public boolean shouldContinue() {
        return Button.readButtons() == 0;
    }

    @Override
    public void onStart() { //vyjede ze startu
        exp.setY(exp.getY() + 1); //jinak si mysli, ze je vys
        move(3);
    }

    @Override
    public void onFinish() {
        exp.print();
        Delay.msDelay(1000);
        Button.waitForAnyPress();
    }
}
