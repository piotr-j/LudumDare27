package com.pj.ld.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pj.ld.Seamless;

public class MainMenu extends BaseScreen implements InputProcessor {
    public final Stage stage;
    public final Skin skin;
    float quitTimer = 0;
    int lastTime = 10;
    TextButton startGame;
    private final Texture splash;

    public MainMenu(Seamless game){
        super(game);
        stage = new Stage(800, 480, false, batch);
        skin = assets.skin;
        splash = new Texture(Gdx.files.internal("data/loading.png"));

        setUpStage();
        InputMultiplexer im = new InputMultiplexer(this, stage);
        Gdx.input.setInputProcessor(im);

    }

    private void setUpStage(){
        Table root = new Table();
        root.setFillParent(true);
        root.debug();
        startGame = new TextButton("10 seconds, go!", skin);
        startGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!startGame.isDisabled())
                    game.setScreen(new Tutorial(game));
            }
        });
        root.add(startGame).size(320,80);
        stage.addActor(root);

    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(800, 480, false);
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
        quitTimer+=delta;
        if (quitTimer > 13){
            Gdx.app.exit();
        }
        int timeLeft = (int)(11-quitTimer);
        if (timeLeft < lastTime){
            if(timeLeft > 2)
                startGame.setText(String.valueOf(timeLeft)+ " seconds, go!");
            else if(timeLeft == 2)
                startGame.setText(String.valueOf(timeLeft)+ " seconds, GO!");
            else if(timeLeft == 1)
                startGame.setText(String.valueOf(timeLeft)+ " second, GO!!");
            else if(timeLeft == 0)
                startGame.setText("Just GO!!!");
            else if(!startGame.isDisabled()){
                startGame.setText("Times up");
                startGame.setDisabled(true);
            }
            lastTime = timeLeft;
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiCam.update();
        batch.setProjectionMatrix(guiCam.combined);
        batch.begin();
        batch.draw(splash, -400,-240);
        batch.end();
        stage.draw();
//        Table.drawDebug(stage);
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    @Override
    public boolean keyDown(int i) {

        if (i == Input.Keys.ENTER){
            if(!startGame.isDisabled())
                game.setScreen(new Game(game));
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
