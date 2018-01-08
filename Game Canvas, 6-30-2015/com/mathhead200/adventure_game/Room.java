package com.mathhead200.adventure_game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.mathhead200.game.Behavior;
import com.mathhead200.game.GameState;
import com.mathhead200.game.Item;
import com.mathhead200.game.Sprite;
import com.mathhead200.game.Vector;


@SuppressWarnings("serial")
public class Room extends HashMap<Class<? extends Item>, Vector> implements Behavior
{
	private double width = 0;
	private double height = 0;
	private Vector spawn = Vector.ZERO;
	
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void setDimensions(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	public Vector getSpawn() {
		return spawn;
	}
	
	public void setSpawn(Vector spawn) {
		this.spawn = spawn;
	}

	
	public static Room load(File file) throws IOException {
		Room room = new Room();
		try( BufferedReader reader = new BufferedReader(new FileReader(file)) ) {
			String[] arr = reader.readLine().trim().split("\\s+");
			room.setDimensions( Integer.valueOf(arr[0]), Integer.valueOf(arr[1]) );
			arr = reader.readLine().trim().split("\\s+");
			room.setSpawn( new Vector( Integer.valueOf(arr[0]), Integer.valueOf(arr[1]) ) );
			
			String line;
			while( (line = reader.readLine()) != null ) {
				line = line.trim();
				if( line.isEmpty() )
					continue;
				arr = line.split("\\s+");
				Object item = (Item) Class.forName(arr[0]).newInstance();
				if( item instanceof Sprite )
					((Sprite) item).setPosition( new Vector( Integer.valueOf(arr[1]), Integer.valueOf(arr[2]) ) );
			}
		} catch(InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
			throw new IOException("room file is corrupt/invalid", e);
		}
		return room;
	}

	
	public void behave(GameState info) {
		// TODO Auto-generated method stub
		
	}
}
