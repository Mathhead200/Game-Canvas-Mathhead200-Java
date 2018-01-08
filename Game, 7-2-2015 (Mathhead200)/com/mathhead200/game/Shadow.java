package com.mathhead200.game;

import java.awt.Image;
import java.util.ArrayDeque;
import java.util.Queue;


public class Shadow implements Behavior, Drawable
{
	private static final class SubjectInfo
	{
		Image image = null;
		Vector position = Vector.ZERO;
		boolean isHUD = true;
	}
	
	
	private Sprite subject;
	private Queue<SubjectInfo> queue;
	private SubjectInfo info = null;
	
	
	public Shadow(Sprite subject, int delay) {
		this.subject = subject;
		this.queue = new ArrayDeque<SubjectInfo>(delay);
		while( delay --> 0 )
			queue.add( new SubjectInfo() );
	}
	

	public void behave(GameState state) {
		if( state.game.getItems().contains(subject) ) {
			SubjectInfo info = new SubjectInfo();
			info.image = subject.getCachedImage();
			info.position = subject.getPosition();
			info.isHUD = subject.isHUD();
			queue.add(info);
		}
		this.info = queue.poll();
		if( info == null )
			state.game.remove(this);
	}
	
	public Image getImage() {
		return info.image;
	}

	public Vector getPosition() {
		return info.position;
	}

	public boolean isHUD() {
		return info.isHUD;
	}
}
