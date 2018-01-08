package com.mathhead200.game;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Allows multiple instructions to be run in sequence.
 * 
 * @author Christopher D'Angelo
 * @version June 27, 2015
 */
public class InstructionQueue<T extends Behavior> implements Instruction<T>
{
	private Queue<Instruction<T>> instructions = new ArrayDeque<>();
	
	@SafeVarargs
	public InstructionQueue(Instruction<T>... instructions) {
		for( Instruction<T> instruction : instructions )
			this.instructions.add(instruction);
	}
	
	public void add(Instruction<T> instruction) {
		instructions.add(instruction);
	}
	
	public boolean follow(T self, GameState info) {
		Instruction<T> instruction = instructions.peek();
		if( instruction == null )
			return false; // done
		if( !instruction.follow(self, info) )
			instructions.poll();
		return true; // continue
	}

}
