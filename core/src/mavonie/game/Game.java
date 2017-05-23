package mavonie.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import mavonie.game.Utils.ContactsListener;
import mavonie.game.Utils.FrameRate;

public class Game extends ApplicationAdapter {

    private SpriteBatch batch;
    private World world;
    private OrthographicCamera camera;

    private Array<Body> destroyBodies = new Array<Body>();

    private Box2DDebugRenderer debugRenderer;

    private Tank tank;
    private Texture background;

    private FrameRate fps;
    private float timestep = 1 / 60f;

    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();

        world = new World(new Vector2(), true);
        world.setContactListener(new ContactsListener(this));

        camera = new OrthographicCamera();
        debugRenderer = new Box2DDebugRenderer();
        tank = new Tank(world, 0, 0, 4, 5);

        player = new Player(world, 10, 10);

        background = new Texture("dirt.png");
        fps = new FrameRate();

        Gdx.input.setInputProcessor(player);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width / 20;
        camera.viewportHeight = height / 20;
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        batch.draw(background, 0, 0);

        tank.update();
        tank.render(batch);

        player.update();
        player.render(batch);

        world.step(timestep, 8, 3);

        for (Body body : destroyBodies) {
            world.destroyBody(body);
        }
        destroyBodies.clear();

        camera.position.set(player.tank.getChasis().getPosition().x, player.tank.getChasis().getPosition().y, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.end();

        fps.render();
        fps.update();
        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }

    /**
     * Destroy any bodies passed in
     * @param body
     */
    public void destroy(Body body) {
        if (!destroyBodies.contains(body, false)) {
            destroyBodies.add(body);
        }
    }
}
