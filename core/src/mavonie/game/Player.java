package mavonie.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Player Class
 */
public class Player extends InputAdapter {

    Tank tank;

    public Player(World world, float x, float y) {
        tank = new Tank(world, x, y, 4, 5);
    }

    public void render(SpriteBatch batch) {
        tank.render(batch);
    }

    public void update() {
        tank.update();
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.Q:
                tank.leftAcceleration = tank.acceleration;
                break;
            case Input.Keys.A:
                tank.leftAcceleration = -tank.acceleration;
                break;
            case Input.Keys.E:
                tank.rightAcceleration = tank.acceleration;
                break;
            case Input.Keys.D:
                tank.rightAcceleration = -tank.acceleration;
                break;

            //Turret Controls
            case Input.Keys.W:
                tank.joint.enableLimit(false);
                tank.joint.enableMotor(true);
                tank.joint.setMotorSpeed(-20);
                break;
            case Input.Keys.S:
                tank.joint.enableLimit(false);
                tank.joint.enableMotor(true);
                tank.joint.setMotorSpeed(20);
                break;
            case Input.Keys.SPACE:
                tank.shoot();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.Q || keycode == Input.Keys.A) {
            tank.leftAcceleration = 0;
        } else if (keycode == Input.Keys.E || keycode == Input.Keys.D) {
            tank.rightAcceleration = 0;
        } else if (keycode == Input.Keys.W || keycode == Input.Keys.S) {
            tank.joint.enableLimit(true);
            tank.joint.enableMotor(false);
            tank.joint.setLimits(tank.joint.getJointAngle() - 20 * MathUtils.degRad, tank.joint.getJointAngle() + 20 * MathUtils.degRad);
        }

        return true;
    }
}
