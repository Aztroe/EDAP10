import lift.LiftView;
import lift.Passenger;

public class Person extends Thread{
	
	LiftView view;
	Passenger pass;
	Monitor monitor;
	int fromFloor, toFloor;
	
	public Person(LiftView view, Monitor monitor) {
		this.view = view;
		this.monitor = monitor;
		this.pass = view.createPassenger();
		this.fromFloor = pass.getStartFloor();
		this.toFloor = pass.getDestinationFloor();
	}
	
	@Override
	public void run() {
		

		pass.begin();

		
			try {
				monitor.enterPassenger(pass,fromFloor);
				pass.enterLift();
				monitor.entered(pass);
				
				monitor.exitPassenger(pass,toFloor);
				pass.exitLift();
				monitor.exited(pass);
				
				pass.end();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}
	
}



