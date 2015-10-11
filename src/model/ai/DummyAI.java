/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.ai;

import model.Exploration;
import model.Move;

/**
 *
 * @author Václav
 */
public class DummyAI extends AI {

    public DummyAI(Exploration exp){
        super(exp);
    }
    
    @Override
    public Move decide() {
        return new Move(exp.getDirection().turnRight(), 10);
    }
    
}
