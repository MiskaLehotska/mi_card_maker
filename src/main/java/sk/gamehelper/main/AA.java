package sk.gamehelper.main;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AA {

	public AA() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(450, 380);
		frame.add(new P());

		frame.setResizable(false);
		frame.setVisible(true);
	}

}
