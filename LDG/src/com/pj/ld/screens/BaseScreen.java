package com.pj.ld.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pj.ld.Seamless;
import com.pj.ld.utils.Assets;

public abstract class BaseScreen implements Screen{
    protected final SpriteBatch batch;
    protected final OrthographicCamera guiCam;
    protected final Seamless game;
    protected final Assets assets;

    protected BaseScreen(Seamless game){
        this.game = game;
        assets = game.assets;
        batch = new SpriteBatch();
        guiCam = new OrthographicCamera(800, 480);
    }

    public abstract void update(float delta);
    public abstract void draw();

    @Override
    public void render(float delta) {
        draw();
        update(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
