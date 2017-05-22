package mavonie.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * Tank Class
 */
public class Tank extends InputAdapter {

    private final RevoluteJoint joint;

    private Body turret, chasis;

    private float acceleration = 20000, leftAcceleration, rightAcceleration;
    private float width, height;

    public Tank(World world, float x, float y, float width, float height) {

        this.width = width;
        this.height = height;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixdev = new FixtureDef();
        fixdev.shape = shape;
        fixdev.density = (float) Math.pow(width, height);
        fixdev.restitution = .1f;
        fixdev.friction = .5f;

        chasis = world.createBody(bodyDef);
        chasis.createFixture(fixdev);

        shape.setAsBox(width / 2 / 5, height / 3);
        fixdev.density /= 500;
        turret = world.createBody(bodyDef);
        turret.createFixture(fixdev);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = chasis;
        jointDef.bodyB = turret;
        jointDef.localAnchorB.y = -height / 3; //This is where the cannon rotates from

        joint = (RevoluteJoint) world.createJoint(jointDef);
    }

    private Vector2 tmp = new Vector2(), tmp2 = new Vector2();

    /**
     * Apply the directional force on the tracks
     */
    public void update() {
        float rot = (float) (chasis.getTransform().getRotation() + Math.PI / 2);
        float x = MathUtils.cos(rot);
        float y = MathUtils.sin(rot);

        chasis.applyForce(tmp.set(leftAcceleration * x, leftAcceleration * y), chasis.getWorldPoint(tmp2.set(-width / 2, 0)), true);
        chasis.applyForce(tmp.set(rightAcceleration * x, rightAcceleration * y), chasis.getWorldPoint(tmp2.set(width / 2, 0)), true);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.Q:
                leftAcceleration = acceleration;
                break;
            case Input.Keys.A:
                leftAcceleration = -acceleration;
                break;
            case Input.Keys.E:
                rightAcceleration = acceleration;
                break;
            case Input.Keys.D:
                rightAcceleration = -acceleration;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.Q || keycode == Input.Keys.A) {
            leftAcceleration = 0;
        } else {
            rightAcceleration = 0;
        }

        return true;
    }

    public Body getChasis() {
        return chasis;
    }
}
