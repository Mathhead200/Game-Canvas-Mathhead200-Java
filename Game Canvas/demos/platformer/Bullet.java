package demos.platformer;

import javax.sound.sampled.Clip;

import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.HitBox;
import com.mathhead200.games3d.Mob;
import com.mathhead200.games3d.Resources;
import com.mathhead200.games3d.Vector;


public class Bullet extends Mob
{
	private static final Resources resources = new Resources();
	static {
		resources.loadStaticImageFromFile("Alive Image", "rsc/bullet.dmi");
		resources.loadAnimationFromFile("Death Animation", "rsc/explosion-sprite-sheet.png", 64, 64, 20);
		resources.loadAudioClipFromFile("Explosion Clip", "rsc/explosion.wav");
	}


	private Protagonist target;


	public Bullet(Protagonist target) {
		this.target = target;
		mapState( "Dead", resources.getAnimation("Death Animation") );
		mapState( "Alive", resources.getStaticImage("Alive Image") );
		setState("Alive");
		setHitBox( new HitBox(0, 0, 24, 15) );
		setVelocity( new Vector(-0.30, 0.0) );
	}


	public void behave(GameState info) {
		if( isIntersecting(target) ) {
			Clip explosionClip = resources.getAudioClip("Explosion Clip");
			explosionClip.setFramePosition(0);
			explosionClip.start();
			target.hurt(2, this);
			setState("Dead");
			setHitBox(null);
		}
		if( getImage() == null || getPosition().x < -getImage().getWidth(null) )
			info.game.remove(this);
	}
}
