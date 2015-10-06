import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.util.Delay;

public class Main {

    public static void main(String[] args) {
	//2 tily = 800
	//otoceni o 180 = +360 a -360
	System.out.println("Hello World");
        Motor.B.rotate(-360, true);
        Motor.C.rotate(360, true);

	//postupne zrychlovani
	for(int i = 1; i<300;i+=3){//LEJOS JE RETARDOVANEJ A NEFUNGUJE 0
	Motor.B.setSpeed(i);
        Motor.C.setSpeed(i);
	Delay.msDelay(20); 	
	}
        //Button.waitForAnyPress();
    }
}
