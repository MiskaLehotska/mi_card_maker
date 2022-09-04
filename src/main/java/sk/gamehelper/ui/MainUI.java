package sk.gamehelper.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainUI {
	
	public static void main(String ...strings) {
		
		SwingUtilities.invokeLater(() -> {
			JFrame f = new JFrame();
			f.setSize(555, 680);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setLocationRelativeTo(null);
			f.add(new MagicItemCreatePanel());
			f.setVisible(true);
		});
	}

}
