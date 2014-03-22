package com.pj.ld.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.pj.ld.Seamless;

public class Tutorial extends BaseScreen implements InputProcessor{
    private final Texture tutorial;
    float tutorialTimer = 0.0f;
    public Tutorial(Seamless game){
        super(game);
        tutorial = new Texture(Gdx.files.internal("data/tutorial.png"));
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float delta) {
        tutorialTimer +=delta;
        if(tutorialTimer > 2){
            game.setScreen(new Game(game));
            tutorial.dispose();
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCam.update();
        batch.setProjectionMatrix(guiCam.combined);
        batch.begin();
        batch.draw(tutorial,-400,-240);
        batch.end();
    }

    @Override
    public boolean keyDown(int i) {
        if(tutorialTimer>0.5f) {
            game.setScreen(new Game(game));
            tutorial.dispose();
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyUp(int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean keyTyped(char c) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDown(int i, int i2, int i3, int i4) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchUp(int i, int i2, int i3, int i4) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean touchDragged(int i, int i2, int i3) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean mouseMoved(int i, int i2) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean scrolled(int i) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}