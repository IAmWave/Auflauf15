/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.ai;

import model.Direction;
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
                if(exp.shouldVisit(x, y)){
                    if(simpleMatrix[x][y]<closest){
                        closest = simpleMatrix[x][y];
                        bx = x;
                        by = y;
                    }
                }
            }
        }
        Move res = moveTowards(bx, by);
        if (!exp.shouldVisit(exp.getX() + res.dir.deltaX() * res.tiles,
                exp.getY() + res.dir.deltaY() * res.tiles)) return res;

        while (exp.shouldVisit(exp.getX() + res.dir.deltaX() * (res.tiles + 1),
                exp.getY() + res.dir.deltaY() * (res.tiles + 1))) {
            res.tiles++;
        }
        return res;
    }
}
