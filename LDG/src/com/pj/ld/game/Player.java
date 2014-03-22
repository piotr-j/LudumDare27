package com.pj.ld.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Event;
import com.pj.ld.screens.Game;
import com.pj.ld.utils.Assets;

public class Player extends GameObject {
    public final static int LEFT = -1;
    public final static int RIGHT = 1;
    private final static int NEUTRAL = 0;
    private final static float SPEED = 6;
    private final static float MAX_SPEED = 6;
    private int dirX = 0;
    private boolean jumping = false;
    private boolean grounded = true;
    private boolean inAir = false;
    public final Vector2 pos = new Vector2();
    public final Vector2 centre = new Vector2();
    public final Vector2 impulseJump = new Vector2(0, 150);

    private float jumpTimer = 0.0f;
    RevoluteJoint playerMotor;

    Body playerBody;
    Body playerFeet;
    World world;
    Vector2 raycastVL = new Vector2();
    Vector2 raycastVR = new Vector2();
    Vector2 raycastVD = new Vector2();
    RayCastCallback raycastLeft;
    RayCastCallback raycastRight;
    RayCastCallback raycastDown;
    boolean canGoLeft = true;
    boolean canGoRight = true;
    boolean isAlive = true;
    boolean scan = false;

    boolean updatePos = false;
    boolean nukeBodies = false;
    float newX = 0.0f;
    float newY = 0.0f;

    public Player(Assets assets, float x, float y, final Game game){
        super(assets, "dude", x, y);
        pos.set(x, y);
        centre.set(0f, 2.5f);
        anim.addMix("idle", "run", 0.1f);
        anim.addMix("idle", "jump", 0.1f);
        anim.addMix("jump", "run", 0.1f);
        anim.playAnim("idle", true);

        anim.state.addListener(new AnimationState.AnimationStateListener() {
            @Override
            public void event(Event event) {
            }

            @Override
            public void complete(int loopCount) {
                if(anim.state.getAnimation().getName().equals("die")){
                    game.spawnPlayer();

                }
            }

            @Override
            public void start() {
            }

            @Override
            public void end() {
            }
        });

    }

    public int getDir(){
        return dirX;
//        if (playerBody.getLinearVelocity().x > 0.1f){
//             return RIGHT;
//        } else if(playerBody.getLinearVelocity().x < -0.1f){
//             return LEFT;
//        }
//        return NEUTRAL;
    }

    public void createBody(World world){
        this.world = world;
        PolygonShape rect = new PolygonShape();
        rect.setAsBox(0.1f,0.45f);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
//        bodyDef.position.set(pos.x, pos.y);
        playerBody = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rect;
        fixtureDef.density = 100;
        fixtureDef.restitution = 0;
        fixtureDef.friction = 0.5f;
        playerBody.createFixture(fixtureDef);
        playerBody.setFixedRotation(true);
//        playerBody.setLinearDamping(1.0f);
        playerBody.setUserData("player_body");

        rect.dispose();

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.1f);

        bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos.x, pos.y);
        playerFeet = world.createBody(bodyDef);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 100;
        fixtureDef.restitution = 0;
        fixtureDef.friction = 20f;

        playerFeet.createFixture(fixtureDef);
        playerFeet.setUserData("player_feet");
        circleShape.dispose();


        RevoluteJointDef motor = new RevoluteJointDef();
        motor.enableMotor = false;
//        motor.motorSpeed = 360 * MathUtils.degreesToRadians;
        motor.maxMotorTorque = 100;
        motor.bodyA = playerBody;
        motor.bodyB = playerFeet;
        motor.collideConnected = false;

        motor.localAnchorA.set(0,-0.45f);
        motor.localAnchorB.set(0,0);

        playerMotor = (RevoluteJoint) world.createJoint(motor);
        raycastLeft = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if(fixture.isSensor())
                    return -1;
                canGoLeft(false);
                stopLeft();

                return 0;
            }
        };
        raycastRight = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if(fixture.isSensor())
                    return -1;
                canGoRight(false);
                stopRight();
                return 0;
            }
        };
        raycastDown = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if(fixture.isSensor())
                    return -1;
                grounded();
                return 0;
            }
        };

    }

    public void setScan(boolean scan){
        this.scan = scan;
    }

    public void canGoRight(boolean goRight){
        canGoRight = goRight;
    }

    public void canGoLeft(boolean goLeft){
        canGoLeft = goLeft;
    }

    public Vector2 getPosition(){
        return playerBody.getWorldCenter();
    }

    public void goLeft(){
        if (!canGoLeft || !isAlive)
            return;
        dirX = LEFT;
        anim.setFlipped(true);
        anim.playAnim("run", true);
        canGoRight = true;
    }

    public void stopLeft(){
        if (dirX == LEFT && isAlive){
            dirX = NEUTRAL;
            if (jumping){
                anim.playAnim("jump", true);
            } else {
                anim.playAnim("idle", true);
            }
        }
    }

    public void goRight(){
        if (!canGoRight || !isAlive)
            return;
        dirX = RIGHT;
        anim.playAnim("run", true);
        anim.setFlipped(false);
        canGoLeft = true;
    }

    public void stopRight(){
        if (dirX == RIGHT && isAlive){
            dirX = NEUTRAL;
            if (jumping){
                anim.playAnim("jump", true);
            } else {
                anim.playAnim("idle", true);
            }
        }
    }

    public void jump(){
        if(!jumping && grounded && !scan && isAlive && jumpTimer < 0.5f){
            inAir = true;
            jumping = true;
            grounded = false;
            anim.playAnim("jump", true);
            playerBody.setLinearVelocity(playerBody.getLinearVelocity().x, 0);
            playerBody.applyLinearImpulse(impulseJump, playerBody.getWorldCenter(), true);
        }
    }

    public void grounded(){
        if (!isAlive)
            return;
        grounded = true;
        jumping = false;
        inAir = false;
        if (dirX == LEFT){
            anim.playAnim("run", true);
        } else if(dirX == RIGHT){
            anim.playAnim("run", true);
        } else {
            anim.playAnim("idle", true);
        }
    }

    public void shoot(){
//        Gdx.app.log("player", "shoot");
    }

    public void die(){
        anim.playAnim("die", false);
        isAlive = false;
        nukeBodies = true;
    }

    public void destroyBodies(){
        world.destroyJoint(playerMotor);
        world.destroyBody(playerBody);
        world.destroyBody(playerFeet);
    }

    public boolean isDead(){
        return !isAlive;
    }

    public void setPos(float x, float y){
        // do this at the end so world doesnt blow up
        updatePos = true;
        newX = x-0.2f;
        newY = y-0.9f;

    }

    private void dieLater(){

    }

    private void setPosLater(){
//        playerBody.setActive(false);
//        playerFeet.setActive(false);
        playerBody.setLinearVelocity(0, 0);
        playerBody.setTransform(newX, newY, 0);

        playerFeet.setTransform(newX, newY, 0);
        playerFeet.setLinearVelocity(0, 0);
        playerFeet.setAngularVelocity(0);
        playerMotor.enableMotor(false);
        anim.setPosition(newX, newY);

        stopRight();
        stopLeft();
        grounded();
//        playerBody.setActive(true);
//        playerFeet.setActive(true);
    }

    @Override
    public void update(float delta){
        anim.update(delta);

        if(updatePos){
            updatePos = false;
            setPosLater();
        }
        if(nukeBodies && !isAlive){
            destroyBodies();
            nukeBodies = false;
        }
        if (!isAlive || scan){

            return;
        }
        anim.setPosition(
            playerBody.getWorldCenter().x,
            playerBody.getWorldCenter().y-0.55f
        );
        // test stuff
        raycastVR.set(playerBody.getWorldCenter());
        raycastVR.x += 0.2f;
        world.rayCast(raycastRight, playerBody.getWorldCenter(), raycastVR);
        raycastVR.y -= 0.3f;
        world.rayCast(raycastRight, playerBody.getWorldCenter(), raycastVR);
        raycastVR.y += 0.6f;
        world.rayCast(raycastRight, playerBody.getWorldCenter(), raycastVR);

        raycastVL.set(playerBody.getWorldCenter());
        raycastVL.x -= 0.2f;
        world.rayCast(raycastLeft, playerBody.getWorldCenter(), raycastVL);
        raycastVL.y -= 0.3f;
        world.rayCast(raycastLeft, playerBody.getWorldCenter(), raycastVL);
        raycastVL.y += 0.6f;
        world.rayCast(raycastLeft, playerBody.getWorldCenter(), raycastVL);
        raycastVD.set(playerFeet.getWorldCenter());
        raycastVD.y -= 0.2f;
        world.rayCast(raycastDown, playerFeet.getWorldCenter(), raycastVD);

        float jumpScale = !grounded ? 0.5f : 1.0f;
        if (dirX == LEFT && canGoLeft){
            if(playerBody.getLinearVelocity().x > -MAX_SPEED){
                playerMotor.enableMotor(false);
                playerBody.applyLinearImpulse(
                    -SPEED*jumpScale,
                    0,
                    playerBody.getWorldCenter().x,
                    playerBody.getWorldCenter().y,
                    true
                );
            }
        } else if(dirX == RIGHT && canGoRight){
            if(playerBody.getLinearVelocity().x < MAX_SPEED){
                playerMotor.enableMotor(false);
                playerBody.applyLinearImpulse(
                    SPEED*jumpScale,
                    0,
                    playerBody.getWorldCenter().x,
                    playerBody.getWorldCenter().y,
                    true
            );
            }
        } else {
            if(Math.abs(playerBody.getLinearVelocity().x) > 1e-5){
                playerMotor.setMotorSpeed(playerMotor.getMotorSpeed()*0.99f);
                playerMotor.enableMotor(true);
            }
        }


        if (jumping){
            jumpTimer+=delta;
            if (jumpTimer > 0.5f){
                jumping = false;
                jumpTimer = 0.0f;
//                playerBody.applyLinearImpulse(impulseJump, playerBody.getWorldCenter(), true);
            }
        }
        if(grounded){
//            playerBody.setLinearVelocity(playerBody.getLinearVelocity().x, -1);
        }
//        if(!grounded){
//            playerBody.setLinearVelocity(
//                    playerBody.getLinearVelocity().x*0.97f,
//                    playerBody.getLinearVelocity().y);
//        } else {
//            playerBody.setLinearDamping(0);
//        }

        canGoRight = true;
        canGoLeft = true;

    }

    @Override
    public void draw(SpriteBatch batch){
        if(!scan){
            anim.draw(batch);

        }
    }


}
