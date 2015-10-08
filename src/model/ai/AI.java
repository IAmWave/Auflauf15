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
public abstract class AI {

    Exploration exp;

    public AI(Exploration exp) {
        this.exp = exp;
    }

    public abstract Move decide();
}
