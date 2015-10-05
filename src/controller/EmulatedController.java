/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author Václav
 */
public class EmulatedController implements Controller {
    //ukázka toho, jak se chování robota dá emulovat.
    //todo: implementovat.
    @Override
    public void turn(int times) {
        System.err.println("turn called");
    }

    @Override
    public void move(int tiles) {
        System.err.println("move called");
    }
    
}
