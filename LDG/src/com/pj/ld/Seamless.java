package com.pj.ld;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.pj.ld.screens.Loading;
import com.pj.ld.utils.Assets;

public class Seamless extends Game {
    public final Assets assets;

    public Seamless(){
        assets = new Assets();

    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_NONE);
        setScreen(new Loading(this));
    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();

    }
}
