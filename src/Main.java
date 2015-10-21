
import controller.Controller;
import controller.EmulatedController;
import controller.RobotController;
import model.Exploration;
import model.Move;

public class Main {

    static final boolean ROBOT = true;

    public static void main(String[] args) {
        Exploration e = new Exploration();
        Controller c = ROBOT ? (new RobotController(e)) : (new EmulatedController(e));
        go(e, c);
    }

    public static void go(Exploration e, Controller c) { //hlavní metoda
        c.onStart();
        while (c.shouldContinue()) {
            Move next = e.decide();
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