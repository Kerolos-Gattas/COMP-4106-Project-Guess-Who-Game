package com.kerolos.game;

import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.kerolos.resources.CommonResources;

public class GUI extends JFrame {

	public static void main(String[] args) {

		JFrame frame = new JFrame(CommonResources.APP_TITLE);
		ImageIcon image = new ImageIcon("Characters.jpg");
		JLabel imageLabel = new JLabel(image);
		imageLabel.setBounds(0, 0, 600, 600);
		imageLabel.setVisible(true);
		frame.add(imageLabel);
		frame.setSize(600, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		Game game = new Game();
		game.StartGame();
	}

}
