import lift.LiftView;

public class Lift extends Thread {
	
	LiftView view;
	Monitor monitor;
	int CurrentFloor, maxFloors;
	boolean GoingUp;
	


	public Lift(LiftView view, Monitor monitor, int maxFloors) {
		this.view = view; 
		this.monitor = monitor;
		this.CurrentFloor = 0;
		this.maxFloors = maxFloors;
		this.GoingUp = true;
	}
	
	@Override
	public void run() {
		while(true) {
			if(CurrentFloor+1 == maxFloors) {
				GoingUp = false;
			}else if(CurrentFloor == 0) {
				GoingUp = true;
			}
			if(GoingUp) {

				try {
					monitor.ppl_waiting_or_inLift();
					monitor.tryOpen(CurrentFloor);
					view.moveLift(CurrentFloor, CurrentFloor+1);
					CurrentFloor++;
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				try {
					monitor.ppl_waiting_or_inLift();
					monitor.tryOpen(CurrentFloor);
					view.moveLift(CurrentFloor, CurrentFloor-1);
					CurrentFloor--;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}	
	}
	}
}
