package mavonie.game;

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

    protected RevoluteJoint joint;

    private Body turret, chasis;

    protected float acceleration = 100000, leftAcceleration, rightAcceleration;
    private float width, height;

    private BodyDef bulletBodyDef;
    private FixtureDef bulletFixtureDev;

    private Texture texture, turretTexture, bulletTexture, healthBackground, healthForeground, healthBorder;

    private float startingHealth = 100, health = 100;

    public Tank(World world, float x, float y, float width, float height) {

        texture = new Texture("tankBeige.png");
        turretTexture = new Texture("barrelBeige.png");
        bulletTexture = new Texture("bulletSilverSilver.png");
        healthBackground = new Texture("healthBackground.png");
        healthForeground = new Texture("healthForeground.png");
        healthBorder = new Texture("healthBorder.png");

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
        chasis.createFixture(fixdev).setUserData(new Box2DSprite(texture));

        //Turret
        shape.setAsBox(width / 2 / 5, height / 3);
        fixdev.density /= 500;

        fixdev.density /= 500;
        turret = world.createBody(bodyDef);
        turret.createFixture(fixdev).setUserData(new Box2DSprite(turretTexture));

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
        fixdev.density = 400;
        fixdev.restitution = 0;
        fixdev.friction = 1;

        bulletFixtureDev = fixdev;

        chasis.setUserData(this);
        turret.setUserData(this);

        chasis.setLinearDamping(3);
        chasis.setAngularDamping(3);
        turret.setLinearDamping(3);
        turret.setAngularDamping(3);
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

        float x = chasis.getPosition().x - width;
        float y = chasis.getPosition().y + height;

        float healthPercentage = health / startingHealth;

        batch.draw(healthBackground, x, y, 7f, 1f);
        batch.draw(healthForeground, x, y, Math.max(7f * healthPercentage, 0), 1f);
        batch.draw(healthBorder, x, y, 7f, 1f);
    }

    /**
     * Create bullet
     */
    public void shoot() {
        bulletBodyDef.position.set(turret.getWorldPoint(tmp.set(0, height / 2)));
        Body bullet = chasis.getWorld().createBody(bulletBodyDef);
        bullet.createFixture(bulletFixtureDev).setUserData(new Box2DSprite(bulletTexture));
        bullet.setUserData("bullet");

        float rot = (float) (turret.getTransform().getRotation() + Math.PI / 2);
        float x = MathUtils.cos(rot);
        float y = MathUtils.sin(rot);

        bullet.setLinearVelocity(x * 500, y * 500);
    }

    public Body getChasis() {
        return chasis;
    }

    public Body getTurret() {
        return turret;
    }

    public void damage(float i) {
        health -= i;

        if (health <= 0 && joint != null) {
            Game.destroyJoints.add(joint);
            turret.applyForce(10f, 10f, 10f, 10f, true);
            joint = null;
        }
    }
}
