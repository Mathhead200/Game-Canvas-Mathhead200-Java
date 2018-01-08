package demos.snake;

import java.awt.Color;

import javax.swing.JFrame;

import com.mathhead200.games3d.Game;


public class SnakeMain
{
	public static void main(String[] args) {
		Game game = new Game(600, 600);
		JFrame frame = Game.frame(game);
		game.add( new Snake(game) );
		game.setBackground(Color.WHITE);
		frame.setTitle("Snake");
		game.play();
	}
}
