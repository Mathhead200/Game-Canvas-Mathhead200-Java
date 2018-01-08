package com.mathhead200.game;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProgrammableMob extends Mob
{
	private List<Instruction<? super ProgrammableMob>> instructions = new ArrayList<>();
	
	
	public ProgrammableMob(Iterable<Image> animation) {
		super(animation);
	}

	public ProgrammableMob() {
		super();
	}
	
	
	/**
	 * Have the programmable mob follow some instruction.
	 * 	This instruction is run concurrently with other instructions.
	 * @param instruction - The instruction to follow.
	 */
	public void giveInstruction(Instruction<? super ProgrammableMob> instruction) {
		synchronized(instructions) {
			instructions.add(instruction);
		}
	}
	
	/**
	 * Have the programmable mob stop following and forget the given instruction.
	 * @param instruction - The instruction to stop following.
	 * @return <code>true</code> if the given instruction was being followed;
	 * 	otherwise <code>false</code>.
	 */
	public boolean stop(Instruction<? super ProgrammableMob> instruction) {
		synchronized(instructions) {
			return instructions.remove(instruction);
		}
	}
	
	/**
	 * Have the programmable mob stop following and forget all instructions
	 * 	pass to it up to this point.
	 */
	public void stopAll() {
		synchronized(instructions) {
			instructions.clear();
		}
	}
	
	
	public void behave(GameState info) {
		synchronized(instructions) {
			Iterator<Instruction<? super ProgrammableMob>> iter = instructions.iterator();
			while( iter.hasNext() ) {
				Instruction<? super ProgrammableMob> instruction = iter.next();
				if( !instruction.follow(this, info) )
					iter.remove();
			}
		}
	}
}
