package mavonie.game.Utils;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import mavonie.game.Bullet;
import mavonie.game.Game;
import mavonie.game.Tank;


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
        Body a = contact.getFixtureA().getBody();
        Body b = contact.getFixtureB().getBody();

        if (b.getUserData() instanceof Bullet && a.getUserData() instanceof Tank) {
            Bullet bullet = (Bullet) b.getUserData();
            Tank tank = (Tank) a.getUserData();
            bullet.hit(game, tank);
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
