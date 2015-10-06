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
public interface Controller {
    
    public void turn(int times); //po směru hodinových ručiček

    public void move(int tiles);
}
