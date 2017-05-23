package mavonie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import net.dermetfan.gdx.graphics.g2d.Box2DSprite;

import box2dLight.Light;
import box2dLight.PointLight;

/**
 * Handle the bullet
 */
public class Bullet {

    private Vector2 tmp = new Vector2();
    private static Texture bulletTexture = new Texture("bulletSilverSilver.png");

    private Body body;
    private Light light;

    private boolean isDestroyed = false;

    public Bullet(World world, Body turret) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        CircleShape bulletShape = new CircleShape();
        bulletShape.setRadius(0.5f);

        FixtureDef fixdev = new FixtureDef();
        fixdev.shape = bulletShape;
        fixdev.density = 400;
        fixdev.restitution = 0;
        fixdev.friction = 1;

        bodyDef.position.set(turret.getWorldPoint(tmp.set(0, 3)));

        body = world.createBody(bodyDef);
        body.createFixture(fixdev).setUserData(new Box2DSprite(bulletTexture));
        body.setUserData(this);

        light = new PointLight(Game.rayHandler, 100, Color.WHITE, 10, body.getPosition().x, body.getPosition().y);
        light.attachToBody(body);
    }


    public Body getBody() {
        return body;
    }

    public void hit(Game game, Tank tank) {

        ParticleEffect p = new ParticleEffect();
        p.load(Gdx.files.internal("particles/bulletHit.p"), Gdx.files.internal("particles"));
        p.start();
        p.setPosition(tank.getChasis().getPosition().x, tank.getChasis().getPosition().y);
        p.scaleEffect(0.1f);
        game.effects.add(p);

        Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/bulletExplosion.mp3"));
        sound.play(0.6f);

        tank.damage(10);

        this.destroy(game);
    }

    private void destroy(Game game) {
        if (!isDestroyed) {
            light.remove();
            game.destroy(body);
        }

        isDestroyed = true;
    }
}
