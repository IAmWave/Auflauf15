package model.ai;

import model.Exploration;
import model.Map;
import model.Move;

/**
 *
 * @author Václav
 */
public class GreedyAI extends AI {

    public GreedyAI(Exploration exp) {
        super(exp);
    }

    @Override
    public Move decide() {
        updateCostMatrix();
        double closest = INF;
        int bx = 0, by = 0;
        for (int x = 0; x < Map.WIDTH; x++) {
            for (int y = 0; y < Map.HEIGHT; y++) {
                double interest = exp.getInterest(x, y); //vyssi = lepsi
                if (interest != 0) {
                    interest = simpleMatrix[x][y] / interest; //nyni mensi = lepsi
                    if (interest < closest) {
                        closest = interest;
                        bx = x;
                        by = y;
                    }
                }
            }
        }
        Move res = moveTowards(bx, by);
        if (exp.getX() + res.dir.deltaX() * res.tiles == bx
                && exp.getY() + res.dir.deltaY() * res.tiles == by) {
            //posledni cast cesty
            int ct = res.tiles;
            while (Exploration.inBounds(bx, by)) {
                if (exp.getInterest(bx, by) > 0) res.tiles = ct;
                bx += res.dir.deltaX();
                by += res.dir.deltaY();
                ct++;
            }
            /*
             while (exp.shouldVisit(exp.getX() + res.dir.deltaX() * (res.tiles + 1),
             exp.getY() + res.dir.deltaY() * (res.tiles + 1))) {
             res.tiles++;
             }
             */

        }
        return res;
    }
}
