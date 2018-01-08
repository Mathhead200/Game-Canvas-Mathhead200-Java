package demos.platformer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.mathhead200.games3d.DenseSprite;
import com.mathhead200.games3d.Drawable;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.HitBox;
import com.mathhead200.games3d.Mob;
import com.mathhead200.games3d.Resources;
import com.mathhead200.games3d.StaticImage;
import com.mathhead200.games3d.Vector;


public class Protagonist extends Mob
{
	private static final double BONUS = Math.sqrt(2);
	private static final Vector GRAVITY = new Vector(0, 0.0015);
	private static final Vector JUMP = new Vector(0, -0.75).multiply(1 / BONUS);
	private static final double BASE_SPEED = 0.225;

	private static final Resources resources = new Resources();
	static {
		resources.loadStaticImageFromFile("Image Left", "rsc/bandit3.dmi");
		resources.loadStaticImageFromFile("Image Right", "rsc/bandit3-r.dmi");
	}

	public static final int MAX_HEALTH = 10;
	public static final double MAX_ENERGY = 3750.0;


	private double height;
	private boolean isJumping = false;
	private boolean isFacingForward = true;
	private boolean isAlive = true;
	private boolean isFast = false;
	private double hurtTimer = 0.0; //0 or less -> is not hurting
	private int health = MAX_HEALTH;
	private double energy = MAX_ENERGY;
	private int points = 0;


	private double ground() {
		return height - getHitBox().y2();
	}

	public Protagonist(double height) {
		super( (StaticImage)resources.get("Image Left") );
		this.height = height;
		setHitBox( new HitBox(3, 6, 25, 30) );
		setPosition( new Vector(0, ground()) );
		StaticImage imageRight = resources.getStaticImage("Image Right");
		StaticImage imageLeft = resources.getStaticImage("Image Left");
		mapState( "Jumping Right", imageRight );
		mapState( "Jumping Left", imageLeft );
		mapState( "Standing Right", imageRight );
		mapState( "Standing Left", imageLeft );
	}


	public void behave(GameState info) {
		//Handle Gravity
		if( getY() >= ground() ) {
			setVelocity( new Vector(getVelocity().x, 0) );
			setPosition( new Vector(getX(), ground()) );
			isJumping = false;
		} else {
			applyImpulse( GRAVITY.multiply(info.deltaTime) );
		}

		if( !isAlive )
			return;

		//Handle Bonuses
		if( info.heldKeys.contains(KeyEvent.VK_SPACE) && energy > 0 ) {
			isFast = true;
		}
		if( energy <= 0 || info.releasedKeys.contains(KeyEvent.VK_SPACE) ) {
			isFast = false;
		}

		if( isFast ) {
			energy -= info.deltaTime;
			if( energy < 0.0 )
				energy = 0.0;
		} else {
			energy += info.deltaTime / 7;
			if( energy > MAX_ENERGY )
				energy = MAX_ENERGY;
		}

		double k = 0.5 + 0.5 * energy / MAX_ENERGY;

		//Handle Movement (via Input)
		if( hurtTimer <= 0.0 ) {
			//Handle Jumping
			if( !isJumping && info.heldKeys.contains(KeyEvent.VK_UP) ) {
				Vector jump = JUMP.multiply(Math.sqrt(k));
				if( isFast ) {
					jump = jump.multiply(BONUS);
					double deltaE = MAX_ENERGY / 6;
					if( (energy -= deltaE) < 0 ) {
						energy += deltaE;
						jump = jump.multiply(1 / BONUS);
					}
				}
				applyImpulse(jump);
				isJumping = true;
			}

			//Handle Left-Right Movement
			double xVel = 0.0;
			if( info.heldKeys.contains(KeyEvent.VK_LEFT) ) {
				xVel -= k * BASE_SPEED;
				isFacingForward = false;
			}
			if( info.heldKeys.contains(KeyEvent.VK_RIGHT) ) {
				xVel += k * BASE_SPEED;
				isFacingForward = true;
			}
			if( isFast )
				xVel *= BONUS;
			setVelocity( new Vector(xVel, getVelocity().y) );

		} else
			hurtTimer -= info.deltaTime;

		//Fix State
		setState( (isJumping ? "Jumping " : "Standing ") + (isFacingForward ? "Right" : "Left") );

		//Change Perspective
		info.game.setPerspective( new Vector(getX(), getY() - 200, info.game.getPerspective().z) );
	}

	public void hurt(int damage, DenseSprite source) {
		health -= damage;
		if( health <= 0 ) {
			isAlive = false;
			setState("Dead");
			setVelocity(Vector.ZERO);
		} else if( source != null && source.getHitBox() != null ) {
			hurtTimer = 400.0;
			applyImpulse(
					centerOfMass().subtract( source.centerOfMass() ).normalize().multiply(0.3) );
		}
	}

	public void heal(int amount) {
		health += amount;
		if( health > MAX_HEALTH )
			health = MAX_HEALTH;
	}

	public void healEnergy(double amount) {
		energy += amount;
		if( energy > MAX_ENERGY )
			energy = MAX_ENERGY;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public int getHealth() {
		return health;
	}

	public double getEnergy() {
		return energy;
	}

	public int getPoints() {
		return points;
	}

	public void givePoints(int points) {
		this.points += points;
	}

	public Drawable createHUD(final Vector pos) {
		return new Drawable() {
			BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
			final int border = 4;
			final int height = (img.getHeight() - 3 * border) / 2;
			final int max_width = img.getWidth() - 2 * border;
			final int x = border;
			final int y1 = border;
			final int y2 = height + 2 * border;
			final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
			final int baselineX = x + 3;
			final int baselineY1 = y1 + (height + font.getSize()) / 2;
			final int baselineY2 = y2 + (height + font.getSize()) / 2 ;

			public Vector getPosition() {
				return pos;
			}

			public Image getImage() {
				Graphics2D g = img.createGraphics();

				//background
				g.setBackground( new Color(0xFFFFFF) );
				g.clearRect( 0, 0, img.getWidth(), img.getHeight() );

				g.setColor( new Color(0x666666) );
				g.fillRect( x, y1, max_width, height );
				g.fillRect( x, y2, max_width, height );

				//health & energy
				g.setColor( new Color(0xBB0000) );
				double percentHealth = (double)health / MAX_HEALTH;
				g.fillRect( x, y1, (int)(max_width * percentHealth), height );

				g.setColor( new Color(0x00BB00) );
				double percentEnergy = energy / MAX_ENERGY;
				g.fillRect( x, y2, (int)(max_width * percentEnergy), height );

				g.setFont(font);
				g.setColor(Color.WHITE);
				g.drawString( "Health " + Math.round(100 * percentHealth) + "%",
						baselineX, baselineY1 );
				g.drawString( "Energy " + Math.round(100 * percentEnergy) + "%",
						baselineX, baselineY2 );

				g.dispose();
				return img;
			}

			public boolean isHUD() {
				return true;
			}
		};
	}
}
