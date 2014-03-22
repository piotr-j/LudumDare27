package com.pj.ld.game;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pj.ld.utils.Animation;
import com.pj.ld.utils.Assets;

public class GameObject {
    protected Animation anim;

    public GameObject(Assets assets, String name, float x, float y){
        anim = new Animation(assets, name, x, y);
    }

    public void update(float delta){

    }

    public void draw(SpriteBatch batch){

    }
}
