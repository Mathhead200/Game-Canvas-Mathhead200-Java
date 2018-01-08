package com.mathhead200.battleground;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.mathhead200.game.Game;


/**
 * 
 * @author Christopher D'Angelo
 * @version Jun 4, 2014
 */
public class BattleGround
{

	public static void main(String[] args) {
		
		// Setup Window + Components
		JFrame frame = new JFrame("BattleGround - Alpha - (June 4, 2014)");
		JPanel panel = new JPanel();
		Game game = new Game(800, 400);
		
		game.setBackground(Color.WHITE);
		game.setMaximumSize( game.getSize() );
		
		panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) );
		panel.add( Box.createVerticalGlue() );
		panel.add(game);
		panel.add( Box.createVerticalGlue() );
		
		frame.add(panel);
		
		// Setup Menubar
		JMenuBar menubar = new JMenuBar();
		JMenu testMenu = new JMenu("Test");
		JMenuItem host = new JMenuItem("Host");
		JMenuItem connect = new JMenuItem("Connect");
		
		menubar.add(testMenu);
		testMenu.add(host);
		testMenu.add(connect);
		
		host.addActionListener( e -> {
			System.out.println("[Host] was selected.");
		});
		
		connect.addActionListener( e -> {
			System.out.println("[Connect] was selected.");
		});
		
		frame.setJMenuBar(menubar);
		
		// Display Window
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
	}

}
