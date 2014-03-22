package com.pj.ld.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.pj.ld.Seamless;
import com.pj.ld.game.Chamber;
import com.pj.ld.game.Exit;
import com.pj.ld.game.Player;
import com.pj.ld.game.Spike;
import com.pj.ld.utils.WorldGenerator;

public class Game extends BaseScreen implements InputProcessor, ContactListener {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 100f;
    public static final float PIXELS_PER_METER = 20f;
    public static final float METERS_PER_PIXEL = 1/20f;

    protected final OrthographicCamera gameCam;

    OrthogonalTiledMapRenderer mapRenderer;
    private final TiledMap gameMap;
    private World world;
    private Vector2 gravity = new Vector2(0, -10);
    private Player player;
    private Array<Player> deadPlayers;
    private Array<Chamber> chambers;
    private Array<Spike> spikes;
    private Array<Exit> exits;
    private Chamber currentChamber;
    boolean playerMustDie = false;
    boolean playerSpawning = true;
    boolean playerWon = false;
    private float timer = 0.0f;
    private int deaths = 1;

    Box2DDebugRenderer debugRenderer;

    float dirX = 0;
    float dirY = 0;

    BitmapFont font;

    public Game(Seamless game){
        super(game);
        font = assets.skin.getFont("default");
        font.setColor(assets.skin.getColor("white"));
        deadPlayers = new Array<Player>();
        gameCam = new OrthographicCamera(8, 4.8f);
//        gameCam = new OrthographicCamera(800, 480);
        gameCam.position.set(4, 2.4f, 0);
        gameMap = assets.gameMap;

        float unitScale = 1 / 20f;
        mapRenderer = new OrthogonalTiledMapRenderer(gameMap, WORLD_TO_BOX);
//        gameCam.zoom = 2f;
        setUpWorld();
        createTriggers();
        spawnPlayer();
        Gdx.input.setInputProcessor(this);
    }

    public void setUpWorld(){
        world = new World(gravity, true);
        world.setContactListener(this);
        WorldGenerator.populateWorld(world, gameMap);
        debugRenderer = new Box2DDebugRenderer();
    }

    private void createTriggers(){
        MapLayer triggers = gameMap.getLayers().get("triggers");
        MapObjects triggerObjects = triggers.getObjects();
        Array<Rectangle> spikes = new Array<Rectangle>();
        Array<Rectangle> chambers = new Array<Rectangle>();
        Array<Rectangle> exits = new Array<Rectangle>();
        for (RectangleMapObject object: triggerObjects.getByType(RectangleMapObject.class)){
            if(object.getName().equals("instakill")){
                spikes.add(object.getRectangle());
            } else if(object.getName().equals("spawn")){
                chambers.add(object.getRectangle());
            } else if(object.getName().equals("exit")){
                 exits.add(object.getRectangle());
                Gdx.app.log("","exit");

            }
        }

        createSpikes(spikes);
        createChambers(chambers);
        createExits(exits);
    }

    private void createSpikes(Array<Rectangle> rects){
        spikes = new Array<Spike>();
        for (Rectangle r: rects){
            Spike s = new Spike(this);
            s.createBody(world, r);
            spikes.add(s);
        }
    }
    private void createExits(Array<Rectangle> rects){
        exits = new Array<Exit>();
        for (Rectangle r: rects){
            Exit e = new Exit(this);
            e.createBody(world, r);
            exits.add(e);
        }
    }
    private void createChambers(Array<Rectangle> rects){
        chambers = new Array<Chamber>();
        for (Rectangle r: rects){
            Chamber c = new Chamber(assets, r.x*WORLD_TO_BOX, r.y*WORLD_TO_BOX, this);
            c.createBody(world, r);
            chambers.add(c);
        }
        currentChamber = chambers.first();
    }

    public void spawnPlayer(){
        if (player != null){
            deadPlayers.add(player);
        }
        player = new Player(assets, currentChamber.getX(), currentChamber.getY(), this);
        player.createBody(world);
        player.setScan(true);
        font.setColor(assets.skin.getColor("black"));
        playerSpawning = true;
        playerMustDie = false;
        currentChamber.reset();
        currentChamber.scan();
        timer = 0.0f;
        scanStart();
    }

    public void killPlayer(){
        playerMustDie = true;
    }

    public void pause(){

    }

    public void scanStart(){
        player.setScan(true);
        timer = 0.0f;
        playerSpawning = true;
        font.setColor(assets.skin.getColor("black"));
    }

    public void scanEnd(){
        Gdx.app.log("","scanEdn");
        player.setScan(false);
        playerSpawning = false;
        playerMustDie = false;
        timer = 0.0f;
    }

    @Override
    public void update(float delta) {

        if (!playerSpawning && !player.isDead() && !playerWon)
            timer += delta;
//        else
//            Gdx.app.log("",String.valueOf(!/playerSpawning  +" "+ !player.isDead() +" "+!playerWon));
        mapRenderer.setView(gameCam);
//        world.step(1/45f, 6, 2);
        if (playerWon){
            return;
        }

            world.step(delta, 6, 2);
        for (Player p: deadPlayers){
            p.update(delta);
        }
        player.update(delta);
        for (Chamber c: chambers){
            c.update(delta);
        }
        if (timer > 8){
            font.setColor(assets.skin.getColor("red"));
        }
        if ((playerMustDie || timer >= 10) && !player.isDead()){
            playerMustDie = false;
            player.die();
            deaths +=1;
        }

//        if (timer >= 10){
//            playerMustDie = true;
//
//        }
        timer = MathUtils.clamp(timer, 0, 10);

    }

    public void playerWon(){
        playerWon = true;
        Gdx.app.log("","winrar");

    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!player.isDead()){
            Gdx.app.log("",String.valueOf(gameCam.position.x + " "+player.getPosition().x));
            Gdx.app.log("diff",String.valueOf(gameCam.position.x - player.getPosition().x));
            float playerX = player.getPosition().x;
            if (player.getDir() == Player.LEFT){
                Gdx.app.log("","left");
                float newPos = gameCam.position.x-0.15f;
                gameCam.position.x = MathUtils.clamp(
                        newPos,
                        playerX-2,
                        gameCam.position.x
                );
            } else if (player.getDir() == Player.RIGHT){
                Gdx.app.log("","right");
                float newPos = gameCam.position.x+0.15f;
                gameCam.position.x = MathUtils.clamp(
                        newPos,
                        gameCam.position.x,
                        playerX+2
                );
            } else {
//                gameCam.position.x*=0.95f;
                if ( gameCam.position.x > playerX+0.15f){
                    gameCam.position.x-=(gameCam.position.x-playerX)*0.05f;
                } else if ( gameCam.position.x < playerX-0.15f){
                    gameCam.position.x+=(playerX-gameCam.position.x)*0.05f;
                } else {
                    gameCam.position.x = playerX;
                }
            }
            gameCam.position.y = player.getPosition().y;
//            if (gameCam.position.x - player.getPosition().x > 2){
//                gameCam.position.x = player.getPosition().x - 2;
//            } else if (gameCam.position.x - player.getPosition().x < -2){
//                gameCam.position.x = player.getPosition().x + 2;
//            }
        }
//            gameCam.position.set(player.getPosition().x, player.getPosition().y, 0);
        if(gameCam.position.x < 4){
            gameCam.position.x = 4;
        } else if( gameCam.position.x > 72f){
            gameCam.position.x = 72f;
        }

        if(gameCam.position.y < 2.4f){
            gameCam.position.y = 2.4f;
        } else if( gameCam.position.y > 24-2.4f){
            gameCam.position.y = 24-2.4f;
        }

        gameCam.update();
        batch.setProjectionMatrix(gameCam.combined);
        mapRenderer.render();
        batch.begin();
        for (Chamber c: chambers){
            c.draw(batch);
        }
        for (Player p: deadPlayers){
            p.draw(batch);
        }
        player.draw(batch);
        batch.end();
        debugRenderer.render(world, gameCam.combined);
        guiCam.update();
        batch.setProjectionMatrix(guiCam.combined);
        batch.begin();
        if (!playerWon){
            font.draw(batch, String.format("%.4f", 10 - timer), 200, 200);
        } else {
            font.setColor(assets.skin.getColor("black"));
            font.draw(batch, "YOU GOT AWAY!", -160, 10);
        }
        font.setColor(assets.skin.getColor("black"));
        font.draw(batch,  String.format("Deaths: %d", deaths), -360, 200);
        batch.end();

    }

    @Override
    public boolean keyDown(int key) {
        switch (key){
            case Input.Keys.A:
            case Input.Keys.LEFT:
                player.goLeft();
                dirX = -0.5f;
                return true;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                player.goRight();
                dirX = 0.5f;
                return true;
            case Input.Keys.W:
            case Input.Keys.SPACE:
            case Input.Keys.UP:
                player.jump();
                dirY = 0.5f;
                return true;
            case Input.Keys.SHIFT_RIGHT:
            case Input.Keys.SHIFT_LEFT:
                player.shoot();
                return true;
            case Input.Keys.ESCAPE:
                pause();
                return true;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                dirY = -0.5f;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int key) {
        switch (key){
            case Input.Keys.A:
            case Input.Keys.LEFT:
                player.stopLeft();
                dirX = 0;
                return true;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                player.stopRight();
                dirX = 0;
                return true;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                dirY = 0;
                killPlayer();
                return true;
            case Input.Keys.W:
            case Input.Keys.SPACE:
            case Input.Keys.UP:
                player.jump();
                dirY = 0;
                return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i2, int i3, int i4) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i2, int i3) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i2) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
        Object udata1 = contact.getFixtureA().getBody().getUserData();
        Object udata2 = contact.getFixtureB().getBody().getUserData();
        if (udata1 != null){
            if (udata1 instanceof Chamber){
                Chamber c = (Chamber)udata1;
                int filter = contact.getFixtureA().getFilterData().categoryBits;
                checkChamberCollision(c, filter);
            } else if (udata1 instanceof Spike){
                killPlayer();
            }  else if (udata1 instanceof Exit){
                playerWon();
            }
        } else if (udata2 != null){
            if (udata2 instanceof Chamber){
                Chamber c = (Chamber)udata2;
                int filter = contact.getFixtureB().getFilterData().categoryBits;
                checkChamberCollision(c, filter);
            } else if (udata2 instanceof Spike){
                killPlayer();
            } else if (udata2 instanceof Exit){
                playerWon();
            }
        }
    }

    private void checkChamberCollision(Chamber c, int filter){
//        if (c.equals(chambers.get(chambers.size-1))){
//            playerWon();
//            return;
//        }
        if (filter == Chamber.INNER_SENSOR){
            if(!c.isUsed()){
                c.scan();
                scanStart();
                currentChamber = c;
                player.setPos(c.getX()+0.2f, c.getY()+0.2f);
            }
        } else if (filter == Chamber.OUTER_SENSOR){
            c.open();
        }
    }

    @Override
    public void endContact(Contact contact) {
//        Object udata1 = contact.getFixtureA().getBody().getUserData();
//        Object udata2 = contact.getFixtureB().getBody().getUserData();
//        if (udata1 != null){
//            if (udata1 instanceof Chamber){
//                Chamber c = (Chamber)udata1;
//                c.enter();
//                if (c.isScanning()){
//                    player.setScan(true);
//                }
//            }
//        } else if (udata2 != null){
//            if (udata2 instanceof Chamber){
//                Chamber c = (Chamber)udata2;
//                c.enter();
//                if (c.isScanning()){
//                    player.setScan(true);
//                }
//            }
//        }
    }

    private void checkPlayerX(Vector2 normal){
//        Gdx.app.log("x",String.valueOf(normal.x));
//        if (normal.x > 0.9f){
//            Gdx.app.log("x","cant go right");
//            player.canGoLeft(false);
//            player.canGoRight(true);
//        } else if (normal.x < -0.9f){
//            Gdx.app.log("x","cant go left");
//            player.canGoRight(false);
//            player.canGoLeft(true);
//        } else {
//            player.canGoRight(true);
//            player.canGoLeft(true);
//        }
    }
    private void checkPlayerY(Vector2 normal){
//        if (normal.y > 0.8f){
////            player.grounded();
//        } else {
//            player.cancelJump();
//        }
    }


    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
