import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.util.Delay;

public class HWController {
	
	private int direction = 0;
	private final int turn90 = 0; //otoceni o 90°
	private final int tileLength = 0; //delka pole

	public HWController(){
	}
	
	public void turn90(boolean direction){
			
	}

	public void turn(int rotations, boolean right){

	}
	
	public void move(int tiles, boolean forward){
		Motor.A.rotate(tiles*tileLength, forward);
	        Motor.B.rotate(tiles*tileLength, forward);		
	}
}
