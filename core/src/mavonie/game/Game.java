package mavonie.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class Game extends ApplicationAdapter {

    private SpriteBatch batch;
    private World world;
    private OrthographicCamera camera;

    private Box2DDebugRenderer debugRenderer;

    private Tank tank;
    private Texture background;

    private float timestep = 1 / 60f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        world = new World(new Vector2(), true);
        camera = new OrthographicCamera();
        debugRenderer = new Box2DDebugRenderer();
        tank = new Tank(world, 0, 0, 4, 5);
        tank.getChasis().setLinearDamping(3);
        tank.getChasis().setAngularDamping(3);
        tank.getTurret().setLinearDamping(3);
        tank.getTurret().setAngularDamping(3);

        background = new Texture("dirt.png");

        Gdx.input.setInputProcessor(tank);
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

        world.step(timestep, 8, 3);
        camera.position.set(tank.getChasis().getPosition().x, tank.getChasis().getPosition().y, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.end();

        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
