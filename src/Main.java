
import controller.Controller;
import controller.EmulatedController;
import model.Exploration;
import model.Move;

public class Main {

    public static void main(String[] args) {
        Exploration e = new Exploration();
        Controller c = new EmulatedController(e);
        /*if (c.getClass() == RobotController.class) {
         new Thread() {
         public void run() {
         while (true) {
         if (Button.readButtons() != 0) {
         System.exit(0);
         }
         Thread.yield();
         }
         }
         }.start();
         }*/
        go(e, c);
    }

    public static void go(Exploration e, Controller c) { //hlavní metoda
        c.onStart();
        while (c.shouldContinue()) {
            Move next = e.decide();
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
