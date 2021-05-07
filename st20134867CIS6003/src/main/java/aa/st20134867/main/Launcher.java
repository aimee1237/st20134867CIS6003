package aa.st20134867.main;
import aa.st20134867.view.MainView;
import javax.swing.UIManager;

public class Launcher {

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch(Exception e) {
			System.out.println("Error occured while setting "
					+ "up the look and feel" + e.toString());
		}
		/*
		 * Initiate the UI, and place it in the Event Dispatch Thread
		 */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					new MainView();
                } catch (Exception e) {
					System.out.println("Error occured while initiating "
							+ "the Swing thread. Please try again later..." + e.toString());
				}
            }
        });
	}
}
