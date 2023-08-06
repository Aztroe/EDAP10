import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;
    private ExecutorService threadpool;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();
        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        threadpool = Executors.newFixedThreadPool(2);
		w.enableErrorChecks();

    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) throws InterruptedException {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         *
         * That's what SwingUtilities.invokeLater is for.
         */
       
        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
           
            new Sniffer(codeBreaker).start();
        });
       

    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
    	
    	SwingUtilities.invokeLater(() -> {
    		WorklistItem work = new WorklistItem(n, message);
    		JButton button = new JButton("Break");
    		workList.add(button);
    		workList.add(work);
    		
    		button.addActionListener(e -> {
    			workList.remove(work);
    			workList.remove(button);
    			
    			mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
    			
    			ProgressItem workExecuting = new ProgressItem(n, message);
    			
    			Tracker tracker = new Tracker(workExecuting.getProgressBar(), mainProgressBar);
    			crackIt(message, n, tracker, workExecuting);
    			
    			progressList.add(workExecuting);

    			
    		});
    	});

    };
    
    
    private void crackIt(String message, BigInteger n, ProgressTracker tracker, ProgressItem item) {
    	
    	
    	JButton cancelbutton = new JButton("Cancel");
    	
    	Runnable runTask = () -> {

			try {
				


				String cracked = Factorizer.crack(message, n, tracker);
				
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	
				    	
				    	item.getTextArea().setText(cracked);
				    	item.remove(cancelbutton);
				    	
				    	JButton button = new JButton("Remove");
						button.addActionListener(e -> {
							progressList.remove(item);
						    mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
						    mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
						  });
						item.add(button);
				        }
			      });
			} catch (InterruptedException e1) {
				/*JButton button = new JButton("Remove");
				button.addActionListener(e -> {
					progressList.remove(item);
				    mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
				    mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
				  });
				item.add(button);*/
			}
			  	
    	};
    	
    	Future task = threadpool.submit(runTask);
    	


		
		cancelbutton.addActionListener(e -> {
			item.getTextArea().setText("Cancelled");
			item.remove(cancelbutton);
			tracker.onProgress(1000000 - item.getProgressBar().getValue());
			task.cancel(true);
			
			JButton button = new JButton("Remove");
			
			
			button.addActionListener(f -> {
				progressList.remove(item);
			    mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
			    mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
			  });
			item.add(button);
			
		});
		item.add(cancelbutton);
		    

		
	
					
		//};

    }

    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;
        private JProgressBar bar, mainbar;
       
        public Tracker(JProgressBar progressBar, JProgressBar mainBar) {
	        this.bar = progressBar;
	        this.mainbar = mainBar;
        }

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         *
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += Math.min(ppmDelta, 1000000 - bar.getValue());

	        SwingUtilities.invokeLater(new Runnable() {
	        	public void run() {
	        		bar.setValue(totalProgress);
	        		mainbar.setValue(mainbar.getValue() + ppmDelta);
	        	}
	        	});

        }


    }
}
