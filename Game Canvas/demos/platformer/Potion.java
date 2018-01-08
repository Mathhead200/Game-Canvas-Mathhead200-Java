package demos.platformer;

import com.mathhead200.games3d.HitBox;
import com.mathhead200.games3d.Resources;
import com.mathhead200.games3d.StaticImage;


public class Potion extends Bonus
{
	private static final StaticImage IMAGE = Resources.loadStaticImageFromFile("rsc/mpotion.dmi");


	public Potion(Protagonist target) {
		super(IMAGE, target, 15000.0, 1);
		setHitBox( new HitBox(0, 0, 19, 28) );
	}


	public void doBonus(Protagonist target) {
		target.healEnergy( Protagonist.MAX_ENERGY * 0.3 );
	}
}
