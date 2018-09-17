package rw;

import robocode.HitByBulletEvent;
import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
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


    public long moveTime = 1;


    public static int moveDirection = 1;


    public static double lastBulletSpeed = 15.0;

    public double wallStick = 120;
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
        double absBearing = e.getBearingRadians() + getHeadingRadians();
        double distance = e.getDistance() + (Math.random()-0.5)*5.0;
        double radarTurn = Utils.normalRelativeAngle(absBearing

                - getRadarHeadingRadians() );

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
            setAhead(100);
        }

        setTurnRightRadians(turn);

        double bulletPower = 1.0 + Math.random() * 2.0;
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
        Graphics2D g = getGraphics();

        g.setColor(Color.orange);
        g.drawOval((int) (getX() - 55), (int) (getY() - 55), 110, 110);
        g.drawOval((int) (getX() - 56), (int) (getY() - 56), 112, 112);
        g.drawOval((int) (getX() - 59), (int) (getY() - 59), 118, 118);
        g.drawOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);


    }

    /**
     * Paint a red circle around our PaintingRobot
     */
    public void onPaint(Graphics2D g) {
        g.setColor(Color.red);
        g.drawOval((int) (getX() - 50), (int) (getY() - 50), 100, 100);
        g.setColor(new Color(0, 0xFF, 0, 30));
        g.fillOval((int) (getX() - 60), (int) (getY() - 60), 120, 120);
    }
}

