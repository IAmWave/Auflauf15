
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
        int t = 40;

        /*Sound.playTone(523, t * 3);
         Delay.msDelay(t * 12);
         Sound.playTone(392, t * 3);
         Delay.msDelay(t * 12);
         Sound.playTone(330, t * 3);
         Delay.msDelay(t * 12);
         Sound.playTone(440, t * 3);
         Delay.msDelay(t * 8);
         Sound.playTone(494, t * 3);
         Delay.msDelay(t * 8);
         Sound.playTone(466, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(440, t * 3);
         Delay.msDelay(t * 8);
         Sound.playTone(392, t * 3);
         Delay.msDelay((long) (t * (16.0 / 3)));
         Sound.playTone(659, t * 3);
         Delay.msDelay((long) (t * (16.0 / 3)));
         Sound.playTone(784, t * 3);
         Delay.msDelay((long) (t * (16.0 / 3)));
         Sound.playTone(880, t * 3);
         Delay.msDelay(t * 8);
         Sound.playTone(698, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(784, t * 3);
         Delay.msDelay(t * 8);
         Sound.playTone(659, t * 3);
         Delay.msDelay(t * 8);
         Sound.playTone(523, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(587, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(494, t * 3);
         Delay.msDelay(t * 12);

         Sound.playTone(196, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(261, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(330, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(392, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(523, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(659, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(784, t * 7);
         Delay.msDelay(t * 8);
         Sound.playTone(659, t * 7);
         Delay.msDelay(t * 8);
         Sound.playTone(208, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(261, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(311, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(415, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(523, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(622, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(831, t * 7);
         Delay.msDelay(t * 8);
         Sound.playTone(622, t * 7);
         Delay.msDelay(t * 8);
         Sound.playTone(233, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(294, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(349, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(466, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(587, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(698, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(932, t * 7);
         Delay.msDelay(t * 8);
         Sound.playTone(932, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(932, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(932, t * 3);
         Delay.msDelay(t * 4);
         Sound.playTone(1047, t * 15);
         Delay.msDelay(t * 16);*/
        //System.exit(0);
        Exploration e = new Exploration();
        boolean panic = false;
        boolean fromStart = false;
        boolean sym = false;
        /*while(true){
         System.out.println(Button.readButtons()!=Button.ENTER.getId());
         }*/
        if (ROBOT) {
            Delay.msDelay(500);
            while (Button.readButtons() != Button.ENTER.getId()) {
                LCD.clear();
                LCD.drawString("< PANIC: " + panic, 0, 0);
                LCD.drawString("> START: " + fromStart, 0, 1);
                LCD.drawString("v SYM:   " + sym, 0, 2);
                if (Button.readButtons() == Button.RIGHT.getId()) {
                    fromStart = !fromStart;
                }
                if (Button.readButtons() == Button.LEFT.getId()) {
                    panic = !panic;
                }
                if (Button.readButtons() == Button.ESCAPE.getId()) {
                    sym = !sym;
                }
                Delay.msDelay(50);
            }
            e.setSymmetry(sym);
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
