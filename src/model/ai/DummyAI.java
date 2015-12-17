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
