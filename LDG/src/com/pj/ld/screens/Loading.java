package com.pj.ld.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.pj.ld.Seamless;

public class Loading extends BaseScreen {
    private final Texture splash;
    float splashTimer = 0.0f;
    public Loading(Seamless game){
        super(game);
        splash = new Texture(Gdx.files.internal("data/loading.png"));
    }

    @Override
    public void update(float delta) {
        splashTimer+=delta;
        if(assets.update() && splashTimer > 1){
            assets.finishLoading();
            game.setScreen(new MainMenu(game));
            splash.dispose();
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCam.update();
        batch.setProjectionMatrix(guiCam.combined);
        batch.begin();
        batch.draw(splash,-400,-240);
        batch.end();
    }

}
