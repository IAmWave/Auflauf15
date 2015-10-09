package controller;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.util.Delay;
import model.Exploration;

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
    final int MIN_SPEED = 100;
    final int DEG_TILE = 400;
    final int DEG_TURN = 180;

    Exploration exp;

    public RobotController(Exploration exp) {
        //System.out.println("LEL");
        //for (int i = 0; i < 12; i++) {
        move(3);
        //}
        //Delay.msDelay(5000);

        this.exp = exp;
    }

    public void move(int tiles) {
        left.rotate(-DEG_TILE * tiles, true);
        right.rotate(-DEG_TILE * tiles, true);
        int deg = left.getTachoCount();
        int targetDeg = deg - DEG_TILE * tiles;
        left.setSpeed(MAX_SPEED);
        right.setSpeed(MAX_SPEED);
        while (left.isMoving()&& !touch.isPressed()) {
            Delay.msDelay(20);
        }
        left.flt();
        right.flt();
        System.out.println("ADWADAWD");
        /*
         for (int i = 1; i < this.MAX_SPEED; i += 20) {//LEJOS JE RETARDOVANEJ A NEFUNGUJE 0
         Motor.B.setSpeed(i);
         Motor.C.setSpeed(i);
         Delay.msDelay(20);
         }
         left.setSpeed(this.MAX_SPEED);
         right.setSpeed(this.MAX_SPEED);
         //Delay.msDelay(1000);
         int l = 0;
         System.out.println(targetDeg - left.getTachoCount());
         while (Math.abs(targetDeg - left.getTachoCount()) > 200) {
         System.out.println(targetDeg - left.getTachoCount());
         Delay.msDelay(20);
         l++;
         if (l > 1000) {
         break;
         }
         }
         for (int i = 1; i < this.MAX_SPEED; i += 30) {//LEJOS JE RETARDOVANEJ A NEFUNGUJE 0
         Motor.B.setSpeed(this.MAX_SPEED - i);
         Motor.C.setSpeed(this.MAX_SPEED - i);
         System.out.println(left.getSpeed());
         if (this.MAX_SPEED - i <= this.MIN_SPEED) {
         Motor.B.setSpeed(this.MIN_SPEED);
         Motor.C.setSpeed(this.MIN_SPEED);
         break;
         }
         Delay.msDelay(20);
         }
         long time = System.currentTimeMillis();
         while (left.isMoving()) {
         Delay.msDelay(10);
         if (System.currentTimeMillis() - time > 15000) {
         break;
         }
         }*/
        //v budoucnu: skenovat, výsledek nahlásit exp
    }

    public void turn(int times) {//times>0 => doprava
        int targetDeg = left.getTachoCount() + DEG_TURN * times;
        left.rotate(DEG_TURN, true);
        right.rotate(-DEG_TURN, true);
        for (int i = 1; i < this.MAX_SPEED; i += 25) {
            left.setSpeed(i);
            right.setSpeed(i);
            Delay.msDelay(10);
        }
        long time = System.currentTimeMillis();
        System.out.println("ACCELERATED " + Math.abs(targetDeg - left.getTachoCount()));

        while (Math.abs(targetDeg - left.getTachoCount()) > 100) {
            System.out.println(targetDeg - left.getTachoCount());
            Delay.msDelay(20);
            if (System.currentTimeMillis() - time > 15000) {
                break;
            }
        }

        for (int i = 1; i < this.MAX_SPEED; i += 50) {//LEJOS JE RETARDOVANEJ A NEFUNGUJE 0
            Motor.B.setSpeed(this.MAX_SPEED - i);
            Motor.C.setSpeed(this.MAX_SPEED - i);
            System.out.println(left.getSpeed());
            if (this.MAX_SPEED - i <= this.MIN_SPEED) {
                Motor.B.setSpeed(this.MIN_SPEED);
                Motor.C.setSpeed(this.MIN_SPEED);
                break;
            }
            Delay.msDelay(20);
        }

        time = System.currentTimeMillis();
        while (left.isMoving()) {
            Delay.msDelay(10);
            if (System.currentTimeMillis() - time > 15000) {
                break;
            }
        }
    }
    
    @Override
    public boolean shouldContinue(){
        return Button.readButtons() == 0;
    }
    
    public void onFinish(){
        
    }
}
