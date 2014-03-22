package com.pj.ld.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.pj.ld.screens.Game;
import com.pj.ld.utils.Assets;

public class Spike {
    public static final int SPIKE_SENSOR = 0x0001;
    Body spike;
    public Spike(Game game){
    }

    public void createBody(World world, Rectangle pos){
        pos.x = (pos.x + pos.width*0.5f) * Game.WORLD_TO_BOX;
        pos.y = (pos.y +  pos.height*0.5f) * Game.WORLD_TO_BOX;
        PolygonShape rect = new PolygonShape();
        rect.setAsBox(
                pos.width* Game.WORLD_TO_BOX *0.5f,
                pos.height* Game.WORLD_TO_BOX *0.5f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(pos.x, pos.y);
        spike = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rect;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = SPIKE_SENSOR;
        spike.createFixture(fixtureDef);
        spike.setUserData(this);
        rect.dispose();
    }


}
