package com.pj.ld.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.pj.ld.screens.Game;

public class WorldGenerator {
    public final static float UNITS = 0.01f;

    public static void populateWorld(World world, TiledMap map){
        MapLayer solids = map.getLayers().get("solids");
        MapObjects solidObjects = solids.getObjects();

        for (RectangleMapObject object: solidObjects.getByType(RectangleMapObject.class)){
            addRect(world, object.getRectangle(), BodyDef.BodyType.StaticBody);
        }
        for (PolygonMapObject object: solidObjects.getByType(PolygonMapObject.class)){
            addPoly(world, object.getPolygon(), BodyDef.BodyType.StaticBody);
        }

    }

    public static void addRect(World world, Rectangle rectangle, BodyDef.BodyType type){
        PolygonShape rect = new PolygonShape();
        Vector2 size = new Vector2(
                (rectangle.x + rectangle.width * 0.5f) * Game.WORLD_TO_BOX,
                (rectangle.y + rectangle.height * 0.5f ) * Game.WORLD_TO_BOX);

        rect.setAsBox(
                rectangle.width * 0.5f * Game.WORLD_TO_BOX,
                rectangle.height * 0.5f * Game.WORLD_TO_BOX,
                size,
                0.0f);

        addShape(world, rect, type);
        rect.dispose();
    }

    public static void addPoly(World world, Polygon polygon, BodyDef.BodyType type){
        PolygonShape poly = new PolygonShape();
        float[] vertices = polygon.getVertices();
        float[] worldVertices = new float[vertices.length];
        float x = polygon.getX();
        float y = polygon.getY();
        for (int i = 0; i < vertices.length; ++i) {
            if (i%2 == 0){
                worldVertices[i] = (vertices[i] + x) * Game.WORLD_TO_BOX;
            } else {
                worldVertices[i] = (vertices[i] + y) * Game.WORLD_TO_BOX;
            }
        }

        poly.set(worldVertices);
        addShape(world, poly, type);
        poly.dispose();
    }

    private static void addShape(World world, Shape shape, BodyDef.BodyType type){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;

        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.restitution = 0.2f;
        fixtureDef.friction = 1;
        body.createFixture(fixtureDef);
    }

}
