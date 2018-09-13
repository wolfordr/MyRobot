package rw;

import robocode.HitByBulletEvent;
import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import java.awt.*;


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

        double ate = getHeadingRadians() + e.getBearingRadians ();
        double rturn = Utils.normalRelativeAngle(ate - getRadarHeadingRadians());
        double eturn = Math.min(Math.atan(36 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS);
        setTurnGunRightRadians(rturn);
        if (rturn < 0)
            rturn -= eturn;
        else
            rturn += eturn;

        setTurnRadarRightRadians(rturn);
        fire(2);
        ahead(100);
        back(100);
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

        turnLeft(90 - e.getBearing());
        back(50);
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

