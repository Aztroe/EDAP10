import java.util.concurrent.Semaphore;


import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {
	

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();
        
        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        
        Semaphore inputTrue = in.getSemaphore();

        
        Clock clock = new Clock(out);
        
        Runnable alarmSetoff = (() ->  {
        	try {
	        	for (int i = 0; i <= 20; i++) {
	        		long t0 = System.currentTimeMillis();
	        		if(clock.isAlarmOn()) {
		        		out.alarm();
		        		long t1 = System.currentTimeMillis(); 	
						Thread.sleep(1000 - (t1-t0));
	        		}else {
	        			break;
	        		}
	        	}
        	}catch (InterruptedException e) {
			}
        });
        
        Thread progressTime = new Thread(() -> {
        	try {
	        	while(true) {
	        		long t0 = System.currentTimeMillis();
	        		clock.incrementTime();
	        		clock.displayTime();
	        		long t1 = System.currentTimeMillis();
	        		if(clock.alarmTrigger()) {
	        			new Thread(alarmSetoff).start();
	        		}
		        	Thread.sleep(1000 - (t1-t0));
		        	
	        	}
        	} catch(InterruptedException e) {
        		e.printStackTrace();
        	}	
        });
        
        
       
        
        
 /*       
        Thread displayTime = new Thread(() -> {
        	try {
				while(true){
					clockDisplayToken.acquire();
					testing.acquire();
	        		out.displayTime(clock.getH(), clock.getM(), clock.getS());
	        		testing.release();
	        		clockDisplayToken.release();

				}
			} catch (InterruptedException e) {
			}

        });
        
 */      
        
        
        progressTime.start();

        
        while (true) {
        	
        	//FRÅGA: clock.setTime(userInput.getHours(), userInput.getMinutes(), userInput.getSeconds()); ändrade
        	//efter drygt 20 sekunder?? Utan h, m, s.
        	inputTrue.acquire();
            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();
            
			if(choice == 1) {
				clock.setTime(h, m, s);
			}
			
			if(choice == 2) {
				clock.setAlarm(h, m, s);
			}
			
			if(choice == 3) {
				clock.alarmSwitch();
				out.setAlarmIndicator(clock.isAlarmOn());
			}
        }
        
        
    }
}

