package MT;
import robocode.*;
import java.awt.Color;
import java.util.Random;
import java.awt.geom.Point2D;


	
public class MamiBot extends AdvancedRobot{

	private EnemyBot enemy = new EnemyBot();
	private byte radarDirection = 1;
	private byte moveDirection = 1;
	double previousEnergy = 100;
	
	public void run() 
	{
		//---------------------------------------------
		// Set our robot's color
		//---------------------------------------------
		setBodyColor(Color.red);
		setGunColor(Color.black);
		setRadarColor(Color.black);
		setBulletColor(Color.green);
		
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		enemy.reset();
		
		while(true){
			doRadar();
			doMove();
			execute(); 
		}
	}
	

	
	public void onScannedRobot(ScannedRobotEvent e) 
	{
	
		if ( enemy.none() || e.getDistance() < enemy.getDistance() - 100 || e.getName().equals(enemy.getName())){
			enemy.update(e);		// track the enemy
			
			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10){
				setFire(Math.min(400 / enemy.getDistance(), 3));	
				setTurnRight(90 + e.getBearing());
				setAhead(50);	
			}
			
			double turn = getHeading() - getGunHeading() + e.getBearing();		//calculate gun turn toward enemy
			setTurnGunRight(normalizeBearing(turn));
			
			double changeInEnergy = previousEnergy-e.getEnergy();
   			 if (changeInEnergy>0 && changeInEnergy <=3) {
         		moveDirection *= -1;
         		setAhead((e.getDistance()/4+25)*moveDirection); 		//dodge 
			}

		}
	}

	public void onRobotDeath(RobotDeathEvent e) 
	{
		if (e.getName().equals(enemy.getName())) {
			enemy.reset();
		}
	} 
	public void onHitWall(HitWallEvent e) 
	{
		if (getVelocity() == 0)
		moveDirection *= -1;
	}
	public void onHitRobt(HitRobotEvent e) 
	{
		turnRight(e.getBearing());
		back(180);
	}

	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++)
		{
			turnRight(50);
			turnLeft(50);
		}
	}

	void doRadar() 
	{
		if (enemy.none()) {
			setTurnRadarRight(360);
		} 
		else {
			double turn = getHeading() - getRadarHeading() + enemy.getBearing();
			turn += 30 * radarDirection;
			setTurnRadarRight(normalizeBearing(turn));
			radarDirection *= -1;
		}
	}
	public void doMove() {

		// cricle around the enemy
		setTurnRight(normalizeBearing(enemy.getBearing() + 90));
		setAhead(1000 * moveDirection);
	}





	// normalizes a bearing to between +180 and -180
	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
}