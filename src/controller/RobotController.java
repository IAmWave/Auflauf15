package controller;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Delay;
import model.Exploration;

/**
 *
 * @author Václav
 */
public class RobotController implements Controller {

    NXTRegulatedMotor sensor = Motor.A;
    NXTRegulatedMotor left = Motor.B;
    NXTRegulatedMotor right = Motor.C;
    final int maxSpeed = 600;
    final int minSpeed = 100;

    Exploration exp;

    public RobotController(Exploration exp) {
        //System.out.println("LEL");
        //for (int i = 0; i < 12; i++) {
        //turn(1);
        //}
        //Delay.msDelay(5000);

        this.exp = exp;
    }

    public void move(int tiles) {
        left.rotate(-400 * tiles, true);
        right.rotate(-400 * tiles, true);
        int deg = left.getTachoCount();
        int targetDeg = deg - 400 * tiles;
        for (int i = 1; i < this.maxSpeed; i += 20) {//LEJOS JE RETARDOVANEJ A NEFUNGUJE 0
            Motor.B.setSpeed(i);
            Motor.C.setSpeed(i);
            Delay.msDelay(20);
        }
        left.setSpeed(this.maxSpeed);
        right.setSpeed(this.maxSpeed);
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
        for (int i = 1; i < this.maxSpeed; i += 30) {//LEJOS JE RETARDOVANEJ A NEFUNGUJE 0
            Motor.B.setSpeed(this.maxSpeed - i);
            Motor.C.setSpeed(this.maxSpeed - i);
            System.out.println(left.getSpeed());
            if (this.maxSpeed - i <= this.minSpeed) {
                Motor.B.setSpeed(this.minSpeed);
                Motor.C.setSpeed(this.minSpeed);
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
        }
        //v budoucnu: skenovat, výsledek nahlásit exp
    }

    public void turn(int times) {//times>0 => doprava
        int targetDeg = left.getTachoCount() + 180 * times;
        left.rotate(180, true);
        right.rotate(-180, true);
        for (int i = 1; i < this.maxSpeed; i += 25) {
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
        
        for (int i = 1; i < this.maxSpeed; i += 50) {//LEJOS JE RETARDOVANEJ A NEFUNGUJE 0
            Motor.B.setSpeed(this.maxSpeed - i);
            Motor.C.setSpeed(this.maxSpeed - i);
            System.out.println(left.getSpeed());
            if (this.maxSpeed - i <= this.minSpeed) {
                Motor.B.setSpeed(this.minSpeed);
                Motor.C.setSpeed(this.minSpeed);
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
