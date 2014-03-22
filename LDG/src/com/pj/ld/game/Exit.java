package com.pj.ld.game;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.pj.ld.screens.Game;

public class Exit {
    Body spike;
    public Exit(Game game){
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
        spike.createFixture(fixtureDef);
        spike.setUserData(this);
        rect.dispose();
    }
}

