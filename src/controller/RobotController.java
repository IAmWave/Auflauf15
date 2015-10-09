package controller;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.util.Delay;
import model.Exploration;
import model.ExplorationTile;

/**
 *
 * @author Václav
 */
public class RobotController implements Controller {

    TouchSensor touch = new TouchSensor(SensorPort.S2);
    NXTRegulatedMotor magnet = Motor.A;
    NXTRegulatedMotor left = Motor.B;
    NXTRegulatedMotor right = Motor.C;
    final int MAX_SPEED = 600;
    final int MAX_TURNING_SPEED = 350;
    final int MIN_SPEED = 100;
    final int DEG_TILE = 400;
    final int DEG_TURN_90 = 182;
    final int DEG_TURN_180 = 364;
    final int ACCELERATION = 20;
    final int DECCELERATION_TIME = 70;
    final int FROM_WALL = 55;
    final int FROM_WALL_SPEED = 100;

    Exploration exp;

    public RobotController(Exploration exp) {
        this.exp = exp;
        exp.print();
    }

    public void move(int tiles) {
        int x = exp.getX();
        int y = exp.getY();
        left.rotate(-DEG_TILE * tiles, true);
        right.rotate(-DEG_TILE * tiles, true);
        int deg = left.getTachoCount();
        int targetDeg = deg - DEG_TILE * tiles;
        Sound.beep();
        left.setSpeed(1);
        right.setSpeed(1);
        boolean accelerate = true;
        while (left.isMoving() && !touch.isPressed()) {
            if (accelerate && left.getSpeed() < this.MAX_SPEED) {
                right.setSpeed(left.getSpeed() + ACCELERATION);
                left.setSpeed(left.getSpeed() + ACCELERATION);
                if (left.getSpeed() >= MAX_SPEED) {
                    accelerate = false;
                }
            } else if (Math.abs(left.getTachoCount() - targetDeg) < 200 && left.getSpeed() > MIN_SPEED) {
                right.setSpeed(left.getSpeed() - ACCELERATION);
                left.setSpeed(left.getSpeed() - ACCELERATION);

            }
            Delay.msDelay(20);
        }
        left.flt(true);
        right.flt(true);

        int tilesFinished = Math.abs((int) Math.round((left.getTachoCount() - deg + 0.0) / DEG_TILE));
        for (int i = 0; i < tilesFinished; i++) {
            x += exp.getRotation().deltaX();
            y += exp.getRotation().deltaY();
            exp.setTile(x, y, new ExplorationTile(false));
        }
        exp.setX(x);
        exp.setY(y);
        if (touch.isPressed()) {
            x += exp.getRotation().deltaX();
            y += exp.getRotation().deltaY();
            exp.setTile(x, y, new ExplorationTile(true));
            Delay.msDelay(100);
            left.setSpeed(FROM_WALL_SPEED);
            right.setSpeed(FROM_WALL_SPEED);
            left.rotate(FROM_WALL, true);
            right.rotate(FROM_WALL, false);
        }

        //System.out.println("TILES FINISHED: " + tilesFinished);
    }

    public void turn(int times) {//times>0 => doprava
        exp.print();
        int targetDeg = 0;
        if (Math.abs(times) == 1) {
            targetDeg = left.getTachoCount() + DEG_TURN_90 * times;
            left.rotate(DEG_TURN_90 * times, true);
            right.rotate(-DEG_TURN_90 * times, true);
        } else {
            targetDeg = left.getTachoCount() + DEG_TURN_180 * times/2;
            left.rotate(DEG_TURN_180 * times/2, true);
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

    @Override
    public boolean shouldContinue() {
        return Button.readButtons() == 0;
    }

    public void onFinish() {
        exp.print();
        Delay.msDelay(1000);
        Button.waitForAnyPress();
    }
    
    public void moveFromStart(){
        move(1);
    }
}
