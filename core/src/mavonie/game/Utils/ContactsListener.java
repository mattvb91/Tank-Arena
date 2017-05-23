package mavonie.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import mavonie.game.Game;


/**
 * Contacts listener class
 */
public class ContactsListener implements ContactListener {

    private Game game;

    public ContactsListener(Game game) {
        this.game = game;
    }

    @Override
    public void beginContact(Contact contact) {
        Body b = contact.getFixtureB().getBody();

        if (b.getUserData() == "bullet") {

            ParticleEffect p = new ParticleEffect();
            p.load(Gdx.files.internal("particles/bulletHit.p"), Gdx.files.internal("particles"));
            p.start();
            p.setPosition(b.getPosition().x, b.getPosition().y);
            p.scaleEffect(0.1f);
            Game.effects.add(p);

            game.destroy(b);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
