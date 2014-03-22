package com.pj.ld.utils;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.spine.*;
import com.pj.ld.screens.Game;

public class Animation {
    protected final SkeletonData data;
    protected final Skeleton skeleton;
    protected final AnimationStateData mixing;
    public final AnimationState state;
    public final String name;
    protected float x;
    protected float y;
    protected final SkeletonRenderer renderer;

    protected String current = "";
    public Animation(Assets assets, String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        data = assets.getSkeletonData(name);
        skeleton = new Skeleton(data);
        mixing = new AnimationStateData(data);
        state = new AnimationState(mixing);
        skeleton.setToSetupPose();
        // set skeletons position
        setPosition(x, y);

        renderer = new SkeletonRenderer();
        setScale(Game.WORLD_TO_BOX, Game.WORLD_TO_BOX);

    }

    public void setFlipped(boolean flipped){
        skeleton.setFlipX(flipped);

    }

    public void playAnim(String name, boolean loop){
        if (!current.equals(name)){
            state.setAnimation(name, loop);
            current = name;
        }
    }


    public void reset(){
        skeleton.setToSetupPose();
        setPosition();
    }

    public void setPosition(){
        setPosition(x, y);
    }

    public void setPosition(float x, float y){
        skeleton.setX(x);
        skeleton.setY(y);
    }

    public void setScale(float x, float y){
        final Bone root = skeleton.getRootBone();
        root.setScaleX(x);
        root.setScaleY(y);
    }

    public void rotate(float deg){
        final Bone root = skeleton.getRootBone();
        root.setRotation(deg);
    }

    public void addMix(String first, String second, float duration){
        mixing.setMix(first, second, duration);
        mixing.setMix(second, first, duration);
    }

    public void update(float delta){
        state.update(delta);

        state.apply(skeleton);
        skeleton.updateWorldTransform();
    }

    public void draw(SpriteBatch batch) {
        renderer.draw(batch, skeleton);
    }
}
