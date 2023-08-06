

import lift.LiftView;


public class mainProgram {
	
	
    public static void main(String[] args){
    	
		final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;
		
		LiftView view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
		
		Monitor monitor = new Monitor(view, MAX_PASSENGERS);
		
		Thread lift = new Lift(view, monitor, NBR_FLOORS);
		lift.start();
		
		for(int i = 0; i < 40; i++) {
			Thread passenger = new Person(view, monitor);
			passenger.start();
		}


}
		

}

