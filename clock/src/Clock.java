import java.util.concurrent.Semaphore;

import clock.io.ClockInput;
import clock.io.ClockOutput;


public class Clock{

	private int h,m,s;
	private Semaphore timeToken = new Semaphore(1);
	private int alarmH = 0, alarmM = 0, alarmS = 0;
	private boolean alarmON_OFF;
	private ClockOutput out;
	
	public Clock(ClockOutput out) {
		h = 0;
		m = 0;
		s = 0;
		this.out = out;
		this.alarmON_OFF = false;
	}

	
	void alarmSwitch() throws InterruptedException {
		timeToken.acquire();
		alarmON_OFF = !alarmON_OFF;
		timeToken.release();
	}
	
	boolean isAlarmOn() {
		
		return alarmON_OFF;
	}

	
	
	void incrementTime(){
		try {
			timeToken.acquire();
			s++;
			if(s ==60) {
				s = 0;
				m++;
				if(m == 60){
					m = 0;
					h++;
					if(h == 24) {
						h = 0;
					}
				}
			}
			timeToken.release();


		} catch (InterruptedException e) {
		}
	}
	
	
	
	void setTime(int H, int M, int S) {
		try {
			timeToken.acquire();
			h = H;
			m = M;
			s = S;
			timeToken.release();

		} catch (InterruptedException e) {
		}

	}
	
	void setAlarm(int H, int M, int S) throws InterruptedException {
		
		timeToken.acquire();
		alarmH = H;
		alarmM = M;
		alarmS = S;
		timeToken.release();
	}
	
	
	boolean alarmTrigger()throws InterruptedException {
		timeToken.acquire();
		
		if(alarmH == h && alarmM == m && alarmS == s && alarmON_OFF == true) {
			timeToken.release();
			return true;
		}else {
			timeToken.release();
			return false;
		}
		
	}
	
	void displayTime() throws InterruptedException {
		timeToken.acquire();
		out.displayTime(h, m, s);
		timeToken.release();
	}
	
	//SEMAPHORE
    /*
    Thread displayTime = new Thread(() -> {
    	try {
    		timeToken.acquire();
        	out.displayTime(h, m, s);
        	timeToken.release();
    	}catch(InterruptedException e) {
    		
    	}
        	
    	});
    */

}
