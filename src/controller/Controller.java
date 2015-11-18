package controller;

/**
 *
 * @author Václav
 */
public interface Controller {

    public void turn(int times); //po směru hodinových ručiček

    public void move(int tiles);

    public boolean shouldContinue();

    public void onStart();

    public void onFinish();
}
