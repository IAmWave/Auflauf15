/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import model.ai.Exploration;

/**
 *
 * @author Václav
 */
public class RobotController implements Controller {
    NXTRegulatedMotor sensor = Motor.A;
    NXTRegulatedMotor left = Motor.B;
    NXTRegulatedMotor right = Motor.C;
    
    Exploration exp;
    
    public RobotController(Exploration exp){
        left.setSpeed(360);
        right.setSpeed(360);
        this.exp = exp;
    }
    
    public void move(int tiles){
        left.rotate(-720, true);
        right.rotate(-720, true);
        //v budoucnu: skenovat, výsledek nahlásit exp
    }
    
    public void turn(int times){
        left.rotate(-720, true);
        right.rotate(720, true);
    }
}
