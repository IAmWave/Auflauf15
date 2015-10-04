/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import model.Map;

/**
 *
 * @author Václav
 */
public abstract class PathAlgorithm {

    public abstract Path getPath(Map map); //Java mě nenechá to udělat static...
    
    public static Path orderToPath(Map map, ArrayList<Point> order){ //pořadí bodů na kroky
        Path res = new Path();
        Move rot = Move.UP;
        for (int i = 1; i < order.size(); i++) {
            res.append(map.findPath(order.get(i-1).x, order.get(i-1).y, order.get(i).x, order.get(i).y, rot));
            rot = res.back();
        }
        return res;
    }
}
