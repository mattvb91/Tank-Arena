package mavonie.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

/**
 * Tank Class
 */
public class Tank extends InputAdapter {

    private final RevoluteJoint joint;

    private Body turret, chasis;

    private float acceleration = 100000, leftAcceleration, rightAcceleration;
    private float width, height;

    private BodyDef bulletBodyDef;
    private FixtureDef bulletFixtureDev;

    private Texture texture, turretTexture, bulletTexture;

    public Tank(World world, float x, float y, float width, float height) {

        texture = new Texture("tankBeige.png");
        turretTexture = new Texture("barrelBeige.png");
        bulletTexture = new Texture("bulletSilverSilver.png");

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

        //Turret
        shape.setAsBox(width / 2 / 5, height / 3);
        fixdev.density /= 500;

        fixdev.density /= 500;
        turret = world.createBody(bodyDef);
        turret.createFixture(fixdev);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = chasis;
        jointDef.bodyB = turret;
        jointDef.localAnchorB.y = -height / 3; //This is where the cannon rotates from

        jointDef.enableLimit = true;
        jointDef.upperAngle = 20 * MathUtils.degreesToRadians;
        jointDef.lowerAngle = -20 * MathUtils.degreesToRadians;
        jointDef.maxMotorTorque = 1;

        joint = (RevoluteJoint) world.createJoint(jointDef);

        //Bullet
        bodyDef.position.set(0, 0);
        bulletBodyDef = bodyDef;

        CircleShape bulletShape = new CircleShape();
        bulletShape.setRadius(width / 2 / 6);

        fixdev.shape = bulletShape;
        fixdev.density = (float) Math.pow(bulletShape.getRadius(), 25);
        fixdev.restitution = 0;
        fixdev.friction = 1;

        bulletFixtureDev = fixdev;

        chasis.setUserData(new Box2DSprite(texture));
        turret.setUserData(new Box2DSprite(turretTexture));
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

    public void render(SpriteBatch batch) {
        Box2DSprite.draw(batch, chasis.getWorld());
    }

    /**
     * Create bullet
     */
    private void shoot() {
        bulletBodyDef.position.set(turret.getWorldPoint(tmp.set(0, height / 3)));
        Body bullet = chasis.getWorld().createBody(bulletBodyDef);
        bullet.createFixture(bulletFixtureDev);
        bullet.setUserData(new Box2DSprite(bulletTexture));

        float rot = (float) (turret.getTransform().getRotation() + Math.PI / 2);
        float x = MathUtils.cos(rot);
        float y = MathUtils.sin(rot);

        bullet.setLinearVelocity(x * 500, y * 500);
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
                break;

            //Turret Controls
            case Input.Keys.W:
                joint.enableLimit(false);
                joint.enableMotor(true);
                joint.setMotorSpeed(-20);
                break;
            case Input.Keys.S:
                joint.enableLimit(false);
                joint.enableMotor(true);
                joint.setMotorSpeed(20);
                break;
            case Input.Keys.SPACE:
                shoot();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.Q || keycode == Input.Keys.A) {
            leftAcceleration = 0;
        } else if (keycode == Input.Keys.E || keycode == Input.Keys.D) {
            rightAcceleration = 0;
        } else if (keycode == Input.Keys.W || keycode == Input.Keys.S) {
            joint.enableLimit(true);
            joint.enableMotor(false);
            joint.setLimits(joint.getJointAngle() - 20 * MathUtils.degRad, joint.getJointAngle() + 20 * MathUtils.degRad);
        }

        return true;
    }

    public Body getChasis() {
        return chasis;
    }

    public Body getTurret() {
        return turret;
    }
}
