package controller;

import java.util.ArrayList;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import model.Direction;
import model.Exploration;
import model.ExplorationTile;
import model.Map;
import util.GoogleSorter;

/**
 *
 * @author Václav
 */
public class RobotController implements Controller {

    TouchSensor touchL = new TouchSensor(SensorPort.S1);
    TouchSensor touchR = new TouchSensor(SensorPort.S2);
    UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
    LightSensor light = new LightSensor(SensorPort.S3);
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
    final int DECCELERATION_TIME = 30; //70
    final int TURN_ACCELERATION = 40;
    final int FROM_WALL = 70;
    final int FROM_WALL_SPEED = 100;
    final int SLOW_DOWN_AT = 200;
    final int TO_WALL = 150;
    final int TO_WALL_SPEED = 300;
    final int BACKWARDS_TO_WALL = 150; //150
    final int BACKWARDS_TO_WALL_SPEED = 300; //300
    //SCAN
    final double SCAN_DISTANCE_FROM = -0.8; //v jakych uhlech je scan v jakych policcich
    final double SCAN_DISTANCE_TO = -0.2;
    final int SCAN_VALUE_OFFSET = -7; //-7
    final int SCAN_VALUE_TILE = 17;
    //LIGHT
    final int LIGHT_RED = 30; //soutez: >30, trenink: <47
    final int MAGNET_ANGLE = 40;
    //PANIC
    final int PANIC_TIME = 250; //za jak dlouho zacne panikarit
    final boolean BUMP;

    Exploration exp;
    GoogleSorter sorter = new GoogleSorter();
    
    public RobotController(Exploration exp, boolean panic, int limit, boolean fromStart, boolean bump) {
        this.BUMP = bump;
        magnet.setSpeed(magnet.getMaxSpeed() / 2);
        light.setFloodlight(false);
        Thread magnetThread = new Thread() {
            public void run() {
                magnet.rotate(-MAGNET_ANGLE);
                magnet.stop();
                while (Button.readButtons() != Button.ESCAPE.getId()) {
                    if (light.readValue() > LIGHT_RED && !magnet.isMoving()) {
                        magnet.rotate(MAGNET_ANGLE, true);
                        magnet.waitComplete();
                        magnet.rotate(-MAGNET_ANGLE, false);
                        magnet.stop();
                    }
                    Delay.msDelay(50);
                }
            }
        };
        magnetThread.start();

        this.exp = exp;
        exp.print();
        if (panic) {
            PanicController pc = new PanicController(this, limit);
            if (fromStart) {
                pc.goUntilWall();
            }
            pc.panic();
            System.exit(0);
        }
    }

    @Override
    public void move(int tiles) {
        ArrayList<Integer> tileData[] = new ArrayList[10];
        for (int i = 0; i < tileData.length; i++) {
            tileData[i] = new ArrayList<>();
        }
        int x = exp.getX();
        int y = exp.getY();
        boolean fromWall = false;
        if (isBumpable(x, y, exp.getDirection().turnLeft().turnLeft()) && BUMP) {
            //COUVANI PRED JIZDOU
            left.setSpeed(BACKWARDS_TO_WALL_SPEED);
            right.setSpeed(BACKWARDS_TO_WALL_SPEED);
            left.rotate(BACKWARDS_TO_WALL, true);
            right.rotate(BACKWARDS_TO_WALL);
            fromWall = true;
        }
        boolean bump = false;
        int SLOW_DOWN = SLOW_DOWN_AT;
        if (isBumpable(x + tiles * exp.getDirection().deltaX(),
                y + tiles * exp.getDirection().deltaY(), exp.getDirection())) {
            bump = true;
            SLOW_DOWN -= TO_WALL;
        }
        //pokud je u zdi, musi jet dal
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
        int minTile = 0;
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
                //zrejme je nakrivo
                new PanicController(this).panic();
            }
            if (read) {
                int dist = sonic.getDistance();
                if (dist == 255) {
                    continue;
                }
                int sDeg = Math.abs(deg - left.getTachoCount());
                int tile = -1;
                for (int i = minTile; i < 11; i++) { //docasne
                    int centerDeg = i * DEG_TILE + DEG_TILE / 2;
                    if (sDeg >= centerDeg + (DEG_TILE * SCAN_DISTANCE_FROM)
                            && sDeg <= centerDeg + DEG_TILE * SCAN_DISTANCE_TO) {
                        tile = i;
                        minTile = i;
                        break;
                    }
                }
                if (tile != -1) {
                    tileData[tile].add(dist);
                }
            }
            read = !read;
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
        left.flt(true);
        right.flt(true);
        if (!Exploration.inBounds(x, y)) {
            new PanicController(this).panic(); //Something, somewhere has gone horribly wrong.
        }

        int tilesFinished = Math.abs((int) Math.round((left.getTachoCount() - deg + 0.0) / DEG_TILE));
        handleMove(tileData, tilesFinished, x, y);
        int nx = exp.getX() + exp.getDirection().deltaX();
        int ny = exp.getY() + exp.getDirection().deltaY();
        if ((touchL.isPressed() && touchR.isPressed()) || !Exploration.inBounds(nx, ny)) {
            exp.setTile(nx, ny, new ExplorationTile(true));
            Delay.msDelay(100);
            left.setSpeed(FROM_WALL_SPEED);
            right.setSpeed(FROM_WALL_SPEED);
            left.rotate(FROM_WALL, true);
            right.rotate(FROM_WALL, true);
            exp.cacheDecision();
        }
        left.waitComplete();
    }

    @Override
    public void turn(int times) {//times>0 => doprava
        exp.clearCache();
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
            if (accelerate && left.getSpeed() < this.MAX_TURNING_SPEED) {
                right.setSpeed(left.getSpeed() + TURN_ACCELERATION);
                left.setSpeed(left.getSpeed() + TURN_ACCELERATION);
                if (left.getSpeed() >= MAX_TURNING_SPEED) {
                    accelerate = false;
                }
            } else if (Math.abs(left.getTachoCount() - targetDeg) < DECCELERATION_TIME && left.getSpeed() > MIN_SPEED) {
                right.setSpeed(left.getSpeed() - TURN_ACCELERATION);
                left.setSpeed(left.getSpeed() - TURN_ACCELERATION);

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

    private void handleMove(ArrayList<Integer>[] tileData, int tilesFinished, int x, int y) {
        for (int i = 0; i <= tilesFinished; i++) {
            if (tileData[i].size() > 0) {
                Integer[] ints = tileData[i].toArray(new Integer[tileData[i].size()]);
                int[] ar = new int[ints.length];
                for (int j = 0; j < ar.length; j++) {
                    ar[j] = ints[j];
                }
                sorter.sort(ar);
                int medianTiles = (ar[ar.length / 2] + SCAN_VALUE_OFFSET) / SCAN_VALUE_TILE;
                RConsole.println("MEDIAN " + ar[ar.length / 2]);
                RConsole.println("Handling: " + x + ", " + y
                        + " " + exp.getDirection().turnRight() + " " + medianTiles);
                exp.handleScan(x, y, exp.getDirection().turnRight(), medianTiles);
            }
            if (i < tilesFinished) {
                x += exp.getDirection().deltaX();
                y += exp.getDirection().deltaY();
                exp.setTile(x, y, new ExplorationTile(false));
            }
        }
        exp.setX(x);
        exp.setY(y);
    }

    @Override
    public boolean shouldContinue() {
        return Button.readButtons() == 0;
    }

    @Override
    public void onStart() { //Nastavení, start
        exp.setY(exp.getY() + 1); //jinak si mysli, ze je vys
        move(3);
    }

    @Override
    public void onFinish() {
        exp.print();
        new PanicController(this).panic();
    }
}
