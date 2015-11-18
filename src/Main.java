
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

    static final boolean ROBOT = true;

    public static void main(String[] args) {
        Exploration e = new Exploration();
        boolean panic = false;
        boolean fromStart = false;
        if (ROBOT) {
            while (Button.readButtons() != Button.ESCAPE.getId()) {
                LCD.clear();
                System.out.println("PANIC: " + panic);
                System.out.println("FROMSTART: " + fromStart);
                if (Button.readButtons() == Button.RIGHT.getId()) {
                    fromStart = !fromStart;
                }
                if (Button.readButtons() == Button.LEFT.getId()) {
                    panic = !panic;
                }
                Delay.msDelay(20);
            }
        }
        Controller c = ROBOT ? (new RobotController(e, panic, fromStart)) : (new EmulatedController(e));
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
            //move bude zaroven prubezne hlasit Exploreru data ze skenu a pripadne kde narazil.
            //Hašení se přenechává výhradně RobotControlleru.
            c.move(next.tiles);
            //s aktualizovanymi daty zacne cyklus znovu
        }
        c.onFinish();
    }
}
