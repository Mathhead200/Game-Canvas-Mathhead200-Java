package demos.platformer;

import com.mathhead200.games3d.HitBox;
import com.mathhead200.games3d.Resources;
import com.mathhead200.games3d.StaticImage;


public class Heart extends Bonus
{
	private static final StaticImage IMAGE = Resources.loadStaticImageFromFile("rsc/potion.dmi");


	public Heart(Protagonist target) {
		super(IMAGE, target, 15000.0, 1);
		setHitBox( new HitBox(0, 0, 15, 26) );
	}


	public void doBonus(Protagonist target) {
		target.heal(3);
	}
}
