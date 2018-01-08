package com.mathhead200.game;

public class KeyConditionalInstruction<T extends Behavior> implements Instruction<T>
{
	private int keyCode;
	private Instruction<T> instruction;
	private boolean stopped = false;
	
	public KeyConditionalInstruction(int keyCode, Instruction<T> instruction) {
		this.keyCode = keyCode;
		this.instruction = instruction;
	}
	
	public void stop() {
		stopped = true;
	}
	
	public boolean follow(T self, GameState info) {
		return !stopped && !( info.heldKeys.contains(keyCode) && !instruction.follow(self, info) );
	}
	
}
