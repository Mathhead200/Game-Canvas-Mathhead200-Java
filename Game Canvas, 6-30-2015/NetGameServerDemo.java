
import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;

import com.mathhead200.game.Game;
import com.mathhead200.game.GameState;
import com.mathhead200.game.Mob;
import com.mathhead200.game.Resources;
import com.mathhead200.game.Vector;
import com.mathhead200.game.net.NetGameServer;


public class NetGameServerDemo
{
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		
		Game game = new Game(600, 450);
		JFrame frame = Game.frame(game);
		NetGameServer server = new NetGameServer(game);
		
		frame.setTitle("NetGame Server Demo");
		game.setBackground(Color.WHITE);
		game.add( new Mob(Resources.loadStaticImageFromFile("rsc/dog.dmi")) {
			public void behave(GameState info) {
				Vector mouse = new Vector(info.mouseLocation.x, info.mouseLocation.y);
				setVelocity( mouse.subtract(getPosition()).multiply(.001) );
			}
		});
		
		server.startAccepting();
		game.setFrameDelay(8);
		game.play();
		
	}
}
