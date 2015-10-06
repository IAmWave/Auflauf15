
import controller.Controller;
import controller.RobotController;
import lejos.nxt.Button;
import model.ai.Exploration;
import model.ai.Move;

public class Main {

    public static void main(String[] args) {
        go();
    }

    public static void go() { //hlavní metoda
        Exploration e = new Exploration();
        Controller c = new RobotController(e);

        //todo: vyjet ze startu před cyklem
        
        while (Button.readButtons() == 0) {
            Move next = e.decide();
            c.turn(e.getRotation().rotationTo(next.dir));
            e.setRotation(next.dir);
            //move bude zároveň průběžně hlásit Explorationu data ze skeneru a případně to, kde narazil.
            //Hašení se přenechává výhradně RobotControlleru.
            c.move(next.tiles);
            //s aktualizovanými daty začne cyklus znovu
        }
    }
}
