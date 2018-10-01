package rw;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 * PaintingRobot - a sample robot that demonstrates the onPaint() and
 * getGraphics() methods.
 * Also demonstrate feature of debugging properties on RobotDialog
 * <p/>
 * Moves in a seesaw motion, and spins the gun around at each end.
 * When painting is enabled for this robot, a red circle will be painted
 * around this robot.
 *
 * @author Stefan Westen (original SGSample)
 * @author Pavel Savara (contributor)
 */
public class CreativeNameV2 extends AdvancedRobot {

    public int sameDirectionCounter = 0;
    public double eEnergyFirst = 100;
    public double eEnergySecond;
    public long moveTime = 1;
    public static int moveDirection = 1;
    public static double lastBulletSpeed = 15.0;
    public double wallStick = 120;
    public boolean hit = false;
    /**
     * PaintingRobot's run method - Seesaw
     */
    public void run() {

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        do {

            if (getRadarTurnRemaining() == 0.0)
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

            execute();
        } while (true) ;


    }

    /**
     * Fire when we see a robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // demonstrate feature of debugging properties on RobotDialog
        setDebugProperty("lastScannedRobot", e.getName() + " at " + e.getBearing() + " degrees at time " + getTime());
        eEnergySecond = e.getEnergy();
        double heading = getGunHeading();
        if (eEnergySecond < eEnergyFirst) {
            eEnergyFirst = eEnergySecond;
            if (!hit) {
                Graphics2D g = getGraphics();
                g.setColor(Color.RED);
                g.drawOval((int) (getX() - 55), (int) (getY() - 55), 110, 110);


                double angle = e.getHeading();
                double enemyY ((Int)(getX() + Math.sin(angle) * e.getDistance()));
                 double enemyX ((Int)(getX() + Math.cos(angle) * e.getDistance()));
                //double speed = 20 - 3 * (eEnergySecond - eEnergyFirst);

               //  if (e.getDistance() / speed > 5) {// Time it takes to reach me

                    dodge();
                //}
            }
            else
                hit = false;
        }



        double absBearing = e.getBearingRadians() + getHeadingRadians();
        double distance = e.getDistance() + (Math.random()-0.5)*5.0;
        double radarTurn = Utils.normalRelativeAngle(absBearing - getRadarHeadingRadians() );

        double baseScanSpan = (18.0 + 36.0*Math.random());
        double extraTurn = Math.min(Math.atan(baseScanSpan / distance), Math.PI/4.0);
        setTurnRadarRightRadians(radarTurn + (radarTurn < 0 ? -extraTurn : extraTurn));

        if (--moveTime <= 0) {
            distance = Math.max(distance, 100 + Math.random() * 50) * 1.25;
            moveTime = 50 + (long) (distance / lastBulletSpeed);

            ++sameDirectionCounter;

            if (Math.random() < 0.5 || sameDirectionCounter > 16) {
                moveDirection = -moveDirection;
                sameDirectionCounter = 0;
            }
        }
        double goalDirection = absBearing - Math.PI / 2.0 * moveDirection;
        double x = getX();
        double y = getY();
        double smooth = 0;
        Rectangle2D fieldRect = new Rectangle2D.Double(18, 18, getBattleFieldWidth() - 36, getBattleFieldHeight() - 36);

        while (!fieldRect.contains(x + Math.sin(goalDirection) * wallStick, y + Math.cos(goalDirection) * wallStick)) {
            goalDirection += moveDirection * 0.1;
            smooth += 0.1;
        }
        if (smooth > 0.5 + Math.random() * 0.125) {
            moveDirection = -moveDirection;
            sameDirectionCounter = 0;
        }

        double turn = Utils.normalRelativeAngle(goalDirection - getHeadingRadians());
            if (Math.abs(turn) > Math.PI / 2) {
                turn = Utils.normalRelativeAngle(turn + Math.PI);
                setBack(100);
            } else {
                setAhead(90);
            }


        setTurnRightRadians(turn);

        double bulletPower = 1.0 + Math.random() * 1.5;
        double bulletSpeed = 20 - 3 * bulletPower;

        double enemyLatVel = e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing);
        double escapeAngle = Math.asin(8.0 / bulletSpeed);

        double enemyDirection = Math.signum(enemyLatVel);
        double angleOffset = escapeAngle * enemyDirection * Math.random();
        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing + angleOffset - getGunHeadingRadians()));

        if (getEnergy() > bulletPower) {
            setFire(bulletPower);



        }
    }

    /**
     * We were hit!  Turn perpendicular to the bullet,
     * so our seesaw might avoid a future shot.
     * In addition, draw orange circles where we were hit.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // demonstrate feature of debugging properties on RobotDialog
        setDebugProperty("lastHitBy", e.getName() + " with power of bullet " + e.getPower() + " at time " + getTime());

        // show how to remove debugging property
        setDebugProperty("lastScannedRobot", null);

        // gebugging by painting to battle view



    }

    /**
     * Paint a red circle around our PaintingRobot
     */



    public void onBulletHit (BulletHitEvent e){
        hit = true;
    }


    public void dodge() {
        if (Math.random() + 1 > 0.5)
            ahead(30);
        else
            back(30);
    }




}

