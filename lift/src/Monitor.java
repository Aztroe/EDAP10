import lift.LiftView;
import lift.Passenger;


public class Monitor {
	
	LiftView view;
	Boolean enterLift;
	int CurrentFloor, maxPassengers, pplInLift, ppl_EnteringLeaving_Lift, enterPermissions;
	private int[] toEnter; // number of passengers waiting to enter the lift at each floor
	private int[] toExit; // number of passengers (in lift) waiting to exit at each floor
	
	
	
	public Monitor(LiftView view, int maxPassengers) {

		CurrentFloor = 0;
		toEnter = new int[7];
		toExit = new int[7];
		enterLift = false;
		pplInLift = 0;
		ppl_EnteringLeaving_Lift = 0;
		this.maxPassengers = maxPassengers; 
		this.view = view;
	}

	
	
	
	public synchronized void enterPassenger(Passenger pass,int fromFloor) throws InterruptedException {

		toEnter[fromFloor]++;
		notifyAll();
		while(CurrentFloor != fromFloor || maxPassengers == pplInLift || enterLift == false) {

			wait();
		}
		ppl_EnteringLeaving_Lift++;
		pplInLift++;
		notifyAll();
	}
	
	public synchronized void entered(Passenger pass) {
		ppl_EnteringLeaving_Lift--;
		toEnter[CurrentFloor]--;
		notifyAll();
	}
	
	public synchronized void exitPassenger(Passenger pass, int toFloor) throws InterruptedException{
		toExit[toFloor]++;
		notifyAll();
		while(CurrentFloor != toFloor || enterLift == false) {
			wait();
		}
		ppl_EnteringLeaving_Lift++;
		pplInLift--;
		toExit[toFloor]--;
		notifyAll();
	}
	
	public synchronized void exited(Passenger pass) {
		ppl_EnteringLeaving_Lift--;
		notifyAll();
	}
	

	public synchronized void tryOpen(int CurrentFloor) throws InterruptedException {
		this.CurrentFloor = CurrentFloor;
		
		

		if((toEnter[CurrentFloor] != 0 && maxPassengers > pplInLift) || toExit[CurrentFloor] != 0) {
			
			view.openDoors(this.CurrentFloor);
			enterLift = true;
			notifyAll();


			while(ppl_EnteringLeaving_Lift != 0 || (toEnter[CurrentFloor] != 0 && maxPassengers > pplInLift) || toExit[CurrentFloor] != 0) {

				wait();
			}
			
			view.closeDoors();
			this.enterLift = false;
			notifyAll();
		}	
	}
	
	public synchronized void ppl_waiting_or_inLift() throws InterruptedException {

		while(sumofWaiting() == 0 && pplInLift == 0) {
			wait();
		}
	}
	
	private synchronized int sumofWaiting() {
		int sumofWaiting = 0;
		for(int value : toEnter) {
			sumofWaiting = sumofWaiting + value;
		}
		return sumofWaiting;
	}
}
