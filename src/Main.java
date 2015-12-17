
import controller.Controller;
import controller.EmulatedController;
import controller.PanicController;
import controller.RobotController;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.util.Delay;
import model.Exploration;
import model.Move;

public class Main {

    static boolean ROBOT = true;

    public static void main(String[] args) {
        if (args != null && args.length != 0 && args[0].equals("emulator")) {
            ROBOT = false;
        }
        Exploration e = new Exploration();
        boolean panic = false;
        boolean fromStart = false;
        int limit = -1;
        boolean sym = false;
        boolean bump = false;
        if (ROBOT) {
            LCD.drawString("< PANIC", 0, 0);
            LCD.drawString("> NORMAL", 0, 1);
            while (true) {
                if (Button.readButtons() == Button.LEFT.getId()) {
                    panic = true;
                    break;
                } else if (Button.readButtons() == Button.RIGHT.getId()) {
                    panic = false;
                    break;
                }
                Delay.msDelay(100);
            }

            if (panic) {
                LCD.drawString("<> LIMIT: " + limit + "   ", 0, 0);
                LCD.drawString("v START: " + fromStart + "   ", 0, 1);
                Delay.msDelay(500);
                while (Button.readButtons() != Button.ENTER.getId()) {
                    if (Button.readButtons() == 0) {
                        Delay.msDelay(20);
                    }
                    if (Button.readButtons() == Button.LEFT.getId()) {
                        limit--;
                        if (limit < -1) limit = -1;
                    }
                    if (Button.readButtons() == Button.RIGHT.getId()) {
                        limit++;
                    }
                    if (Button.readButtons() == Button.ESCAPE.getId()) {
                        fromStart = !fromStart;
                    }
                    LCD.drawString("<> LIMIT: " + limit + "   ", 0, 0);
                    LCD.drawString("v START: " + fromStart + "   ", 0, 1);
                    Delay.msDelay(400);
                }
            } else {
                Delay.msDelay(500);
                LCD.drawString("< SYM: " + sym + "   ", 0, 0);
                LCD.drawString("> BUMP: " + bump + "   ", 0, 1);
                while (Button.readButtons() != Button.ENTER.getId()) {
                    if (Button.readButtons() == 0) {
                        Delay.msDelay(20);
                    }
                    if (Button.readButtons() == Button.LEFT.getId()) {
                        sym = !sym;
                    }
                    if (Button.readButtons() == Button.RIGHT.getId()) {
                        bump = !bump;
                    }
                    LCD.drawString("< SYM: " + sym + "   ", 0, 0);
                    LCD.drawString("> BUMP: " + bump + "   ", 0, 1);
                    Delay.msDelay(400);
                }
            }
            e.setSymmetry(sym);
        }
        Controller c = ROBOT ? (new RobotController(e, panic, limit, fromStart, bump))
                : (new EmulatedController(e, "data/maps/26.map"));
        go(e, c);
    }

    public static void go(Exploration e, Controller c) { //hlavní metoda
        c.onStart();
        while (c.shouldContinue()) {
            if (ROBOT) {
                Sound.beep();
            }
            Move next = e.decide();
            if (ROBOT) {
                Sound.beep();
            }
            if (next.tiles == 0) {
                System.out.println("Invalid move!");
                break;
            }
            c.turn(e.getDirection().rotationTo(next.dir));
            e.setRotation(next.dir);
            c.move(next.tiles);
            //s aktualizovanymi daty zacne cyklus znovu
        }
        c.onFinish();
    }
}
