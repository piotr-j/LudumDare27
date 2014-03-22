package com.pj.ld.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;

public class Assets {
    private final static String ATLAS_PATH = "data/assets.atlas";
    private final static String MAP_PATH = "data/testmap.tmx";
    private final static String DUDE_ANIM = "data/dude.json";
    private final static String CHAMBER_ANIM = "data/chamber.json";
    private final AssetManager assetManager;
    private TextureAtlas atlas;
    public Skin skin;
    private ObjectMap<String, Sprite> spriteMap;
    private ObjectMap<String, SkeletonData> skeletonDataMap;

    public TiledMap gameMap;

    public Assets(){
        spriteMap = new ObjectMap<String, Sprite>();
        skeletonDataMap = new ObjectMap<String, SkeletonData>();
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(ATLAS_PATH, TextureAtlas.class);
        assetManager.load(MAP_PATH, TiledMap.class);
        skin = new Skin();
    }


    public boolean update(){
        return assetManager.update();
    }

    public void finishLoading(){
        atlas = assetManager.get(ATLAS_PATH);
        for (Texture t: atlas.getTextures()){
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        gameMap = assetManager.get(MAP_PATH);
        setUpSkin();
        loadAnim(Gdx.files.internal(DUDE_ANIM), atlas);
        loadAnim(Gdx.files.internal(CHAMBER_ANIM), atlas);
    }

    public Sprite getSprite(String name){
        if (!spriteMap.containsKey(name)){
            spriteMap.put(name, atlas.createSprite(name));
        }
        return spriteMap.get(name);
    }

    private void setUpSkin(){
        skin = new Skin(atlas);
        // font
        final BitmapFont defaultFont = new BitmapFont(
                Gdx.files.internal("data/roboto42.fnt"),
                atlas.createSprite("roboto42"), false);
        skin.add("default", defaultFont);
        // 9 patches for buttons
        skin.add("btn", atlas.createPatch("btn"));
        skin.add("btn-down", atlas.createPatch("btn-down"));
        skin.add("btn-toggle", atlas.createPatch("btn-down"));
        // colors
        skin.add("white", new Color(1, 1, 1, 1));
        skin.add("red", new Color(1, 0, 0, 1));
        skin.add("green", new Color(0, 1, 0, 1));
        skin.add("blue", new Color(0, 0, 1, 1));
        skin.add("black", new Color(0, 0, 0, 1));
        // styles
        TextButton.TextButtonStyle defaultTextBtnStyle = new TextButton.TextButtonStyle();
        defaultTextBtnStyle.up = skin.getDrawable("btn");
        defaultTextBtnStyle.down = skin.getDrawable("btn-down");
        defaultTextBtnStyle.font = skin.getFont("default");
        defaultTextBtnStyle.fontColor = skin.getColor("white");
        defaultTextBtnStyle.downFontColor = skin.getColor("red");
        skin.add("default", defaultTextBtnStyle);

    }

    private void loadAnim(FileHandle file, TextureAtlas atlas){
        SkeletonJson mSkeletonJson = new SkeletonJson(atlas);
        final String name = file.nameWithoutExtension();
        final SkeletonData data = mSkeletonJson.readSkeletonData(file);
        skeletonDataMap.put(name, data);
    }

    public SkeletonData getSkeletonData(String name) {
        return skeletonDataMap.get(name);
    }

    public void dispose(){
        assetManager.dispose();
        atlas.dispose();
    }


}
