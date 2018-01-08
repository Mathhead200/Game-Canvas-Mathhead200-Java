import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;

import com.mathhead200.game.net.NetGameClient;


public class NetGameClientDemo
{
	public static void main(String[] args) throws IOException, InterruptedException {
		
		JFrame frame = new JFrame("NetGame Client Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		NetGameClient client = new NetGameClient("localhost");
		client.setSize(600, 450);
		client.setBackground(Color.BLACK);
		frame.add(client);
		
		client.setFrameDelay(8);
		client.play();
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		client.createBufferStrategy(2);
		
		while( frame.isShowing() ) {
			System.out.println( "FPS: " + client.getFrameRate() );
			Thread.sleep(333);
		}
	}
}
