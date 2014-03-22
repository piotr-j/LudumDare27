package com.pj.ld.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.pj.ld.screens.Game;
import com.pj.ld.utils.Assets;

public class Chamber extends GameObject{
    public static final int OUTER_SENSOR = 0x0001;
    public static final int INNER_SENSOR = 0x0002;
    Body chamberBody;
    boolean used = false;
    boolean open = false;
    Rectangle pos;
    public Chamber(Assets assets, float x, float y, final Game game){
        super(assets, "chamber", x, y);
        anim.state.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void event(Event event) {
                Gdx.app.log("Event", event.getData().getName());
                if (event.getData().getName().equals("scan_start")){
                    game.scanStart();
                } else if (event.getData().getName().equals("scan_end")){
                    game.scanEnd();
                }
            }

            @Override
            public void complete(int loopCount) {

            }

            @Override
            public void start() {

            }

            @Override
            public void end() {

            }
        });
    }

    public void reset(){
        used = false;
        anim.state.setTime(0);
//        anim.playAnim("open", false);
//        anim.state.setTime(1.0f);
    }

    public void createBody(World world, Rectangle pos){
        this.pos = pos;
        pos.x = (pos.x + 50) * Game.WORLD_TO_BOX;
        pos.y = (pos.y + 70) * Game.WORLD_TO_BOX;
        PolygonShape rect = new PolygonShape();
        rect.setAsBox(0.3f, 0.7f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(pos.x, pos.y);
        chamberBody = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rect;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = INNER_SENSOR;
        chamberBody.createFixture(fixtureDef);
        rect.setAsBox(0.9f, 0.9f);
        fixtureDef.shape = rect;
        fixtureDef.filter.categoryBits = OUTER_SENSOR;
        chamberBody.createFixture(fixtureDef);
        chamberBody.setUserData(this);


        rect.dispose();
    }

    public Rectangle getBounds(){
        return pos;
    }

    public float getX(){
        return pos.x;
    }
    public float getY(){
        return pos.y;
    }

    public void open(){
        if(!open){
            open = true;
            anim.playAnim("open", false);
        }
    }

    public boolean isUsed(){
        return used;
    }

    public void scan(){
        if (!used && open){
            anim.playAnim("scan", false);
            used = true;
        }
    }

    public boolean isScanning(){
        return used && !anim.state.isComplete();
    }

    @Override
    public void update(float delta) {
        anim.update(delta);
    }

    @Override
    public void draw(SpriteBatch batch) {
        anim.draw(batch);
    }
}