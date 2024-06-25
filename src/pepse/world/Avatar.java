package pepse.world;


import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    public static final Vector2 DIMENSIONS = Vector2.of(60, 90);
    private static final float WALKING_SPEED = 300;
    private static final float GRAVITY = 500;
    private static final int MAX_ENERGY = 100;
    private static final String
            JUMP_SOUND_PATH = "assets/avatar/jumpSFX.wav",
            LAND_SOUND_PATH = "assets/avatar/landSFX.wav",
            WING_FLAP_SOUND_PATH = "assets/avatar/wingFlap.wav";
    private static final String[]
            IDLE_SPRITE = {"assets/avatar/idle0.png",
            "assets/avatar/idle1.png",
            "assets/avatar/idle2.png",
            "assets/avatar/idle3.png"},
            RUNNING_SPRITE = {"assets/avatar/walking0.png",
                    "assets/avatar/walking1.png",
                    "assets/avatar/walking2.png",
                    "assets/avatar/walking3.png",
                    "assets/avatar/walking4.png",
                    "assets/avatar/walking5.png"},
            FALLING_SPRITE = {"assets/avatar/falling0.png",
                    "assets/avatar/falling1.png"},
            FLYING_SPRITES = {"assets/avatar/flying0.png",
                    "assets/avatar/flying1.png"};
    private static final double TIME_BETWEEN_IDLE_SPRITES = 0.4,
            TIME_BETWEEN_RUN_SPRITES = 0.12,
            TIME_BETWEEN_FLY_SPRITES = 0.15,
            TIME_BETWEEN_FALL_SPRITES = 0.1;
    private static final double ENERGY_INCREASE_FACTOR = 0.5;
    private static final double ENERGY_DECREASE_FACTOR = -ENERGY_INCREASE_FACTOR;

    private final UserInputListener inputListener;
    private final Renderable idleSprite, runSprite, flySprite, fallSprite;
    private final Sound jumpSound, landSound, wingFlapSound;

    private double flyingEnergy = MAX_ENERGY;
    private boolean isFlying = false, didLandAlready = true, isFlapSoundOn = false;
    private static Avatar avatar;

    /**
     * @param gameObjects   game objects
     * @param layer         layer
     * @param topLeftCorner location
     * @param inputListener input listener
     * @param imageReader   image reader
     * @return an avatar
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                  UserInputListener inputListener, ImageReader imageReader) {
        avatar = Avatar.myCreate(topLeftCorner, inputListener, imageReader, null);
        return avatar;
    }

    /**
     * @param topLeftCorner location
     * @param inputListener input listener
     * @param imageReader   image reader
     * @param soundReader   sound reader
     * @return an avatar
     */
    public static Avatar myCreate(Vector2 topLeftCorner, UserInputListener inputListener,
                                  ImageReader imageReader, SoundReader soundReader) {
        avatar = new Avatar(topLeftCorner, inputListener, imageReader, soundReader);
        return avatar;
    }

    /**
     * @param pos           location
     * @param inputListener input listener
     * @param imageReader   image reader
     * @param soundReader   sound reader
     */
    private Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader, SoundReader soundReader) {
        super(pos, DIMENSIONS, null);
        jumpSound = soundReader.readSound(JUMP_SOUND_PATH);
        landSound = soundReader.readSound(LAND_SOUND_PATH);
        wingFlapSound = soundReader.readSound(WING_FLAP_SOUND_PATH);
        idleSprite = new AnimationRenderable(IDLE_SPRITE, imageReader, false, TIME_BETWEEN_IDLE_SPRITES);
        runSprite = new AnimationRenderable(RUNNING_SPRITE, imageReader, false, TIME_BETWEEN_RUN_SPRITES);
        flySprite = new AnimationRenderable(FLYING_SPRITES, imageReader, false, TIME_BETWEEN_FLY_SPRITES);
        fallSprite = new AnimationRenderable(FALLING_SPRITE, imageReader, false,
                TIME_BETWEEN_FALL_SPRITES);
        renderer().setRenderable(idleSprite);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    /**
     * @param other     collided game object
     * @param collision collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (flyingEnergy == 0 && other.getTopLeftCorner().y() > getTopLeftCorner().y()) {
            renderer().setRenderable(idleSprite);
        }
    }

    /**
     * @param other     collided game object
     * @param collision collision
     */
    @Override
    public void onCollisionStay(GameObject other, Collision collision) {
        super.onCollisionStay(other, collision);
        if (getVelocity().y() != 0) {
            return;
        }
        isFlying = false;
        if (didLandAlready) {
            return;
        }
        landSound.play();
        didLandAlready = true;
    }

    /**
     * @param deltaTime delta time
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        walkIfNeeded();
        jumpIfNeeded();
        flyIfNeeded();
        if (flyingEnergy == 0) {
            renderer().setRenderable(fallSprite);
        }
        if (!isFlying) {
            flyingEnergy += flyingEnergy < MAX_ENERGY ? ENERGY_INCREASE_FACTOR : 0;
        }
    }

    /**
     * @return current flying energy
     */
    public double returnFlyingEnergy() {
        return flyingEnergy;
    }

    /**
     * make avatr walk
     */
    private void walkIfNeeded() {
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= WALKING_SPEED;
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += WALKING_SPEED;
        }
        transform().setVelocityX(xVel);
        updateSprite(xVel);
    }

    /**
     * changing the avater's animation
     *
     * @param xVel x coordinate
     */
    private void updateSprite(float xVel) {
        if (xVel != 0) {
            renderer().setIsFlippedHorizontally(false);
            renderer().setRenderable(runSprite);
            if (xVel < 0) {
                renderer().setIsFlippedHorizontally(true);
            }
        } else {
            renderer().setRenderable(idleSprite);
        }
    }

    /**
     * make the avatar jump
     */
    private void jumpIfNeeded() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            jumpSound.play();
            didLandAlready = false;
            transform().setVelocityY(-1 * WALKING_SPEED);
        }
    }

    /**
     * make the avatar fly
     */
    private void flyIfNeeded() {
        if (!inputListener.isKeyPressed(KeyEvent.VK_SPACE) || !inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
            wingFlapSound.stopAllOccurences();
            isFlapSoundOn = false;
            return;
        }
        isFlying = true;
        if (flyingEnergy <= 0) {
            wingFlapSound.stopAllOccurences();
            isFlapSoundOn = false;
            return;
        }
        renderer().setRenderable(flySprite);
        transform().setVelocityY(-1 * WALKING_SPEED);
        flyingEnergy += ENERGY_DECREASE_FACTOR;
        if (!isFlapSoundOn) {
            wingFlapSound.playLooped();
            isFlapSoundOn = true;
        }
    }
}
