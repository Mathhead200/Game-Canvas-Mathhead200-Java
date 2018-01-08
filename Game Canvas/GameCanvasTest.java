
import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import com.mathhead200.GameCanvas;


@SuppressWarnings("serial")
public class GameCanvasTest extends GameCanvas
{
	public void drawStep(Graphics2D g) {
		g.clearRect( 0, 0, getWidth(), getHeight() );
		System.out.printf( "%d: dT = %.1f ms, T = %.1f ms%n", getLoopID(), getDeltaTime(), getElapsedTime() );
	}


	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame("Game Canvas Test");
		GameCanvasTest canvas = new GameCanvasTest();

		canvas.setSize(400, 300);
		canvas.setBackground(Color.WHITE);
		frame.add(canvas);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		canvas.createBufferStrategy(2);
		while(true) {
			canvas.play();
			Thread.sleep(1000);
			canvas.pause();
			Thread.sleep(1000);
			System.out.println("----------------------------------------");
		}
	}
}
