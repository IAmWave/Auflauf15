
import controller.Controller;
import controller.EmulatedController;
import controller.RobotController;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Delay;
import model.Exploration;
import model.Move;

public class Main {

    static boolean ROBOT = true;

    public static void main(String[] args) {
        if (args != null && args.length != 0 && args[0].equals("emulator"))
            ROBOT = false;
        Exploration e = new Exploration();
        boolean panic = false;
        boolean fromStart = false;
        boolean sym = false;
        if (ROBOT) {
            LCD.drawString("< PANIC: " + panic + "   ", 0, 0);
            LCD.drawString("> START: " + fromStart + "   ", 0, 1);
            LCD.drawString("v SYM:   " + sym + "   ", 0, 2);
            Delay.msDelay(500);
            while (Button.readButtons() != Button.ENTER.getId()) {
                if (Button.readButtons() == 0) {
                    Delay.msDelay(50);
                    continue;
                }
                if (Button.readButtons() == Button.RIGHT.getId()) {
                    fromStart = !fromStart;
                }
                if (Button.readButtons() == Button.LEFT.getId()) {
                    panic = !panic;
                }
                if (Button.readButtons() == Button.ESCAPE.getId()) {
                    sym = !sym;
                }
                //LCD.clear();
                LCD.drawString("< PANIC: " + panic + "   ", 0, 0);
                LCD.drawString("> START: " + fromStart + "   ", 0, 1);
                LCD.drawString("v SYM:   " + sym + "   ", 0, 2);
                Delay.msDelay(400);
            }
            e.setSymmetry(sym);
        }
        Controller c = ROBOT ? (new RobotController(e, panic, fromStart))
                : (new EmulatedController(e, "data/maps/s10.map"));
        go(e, c);
    }

    public static void go(Exploration e, Controller c) { //hlavní metoda
        c.onStart();
        while (c.shouldContinue()) {
            if (ROBOT) Sound.beep();
            Move next = e.decide();
            if (ROBOT) Sound.beep();
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
