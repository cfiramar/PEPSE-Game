package pepse;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import pepse.util.ColorSupplier;
import pepse.world.*;
import pepse.world.daynight.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;


public class GraphicManager {
    public static final int WINDOW_WIDTH = 1400;
    public static final int WINDOW_HEIGHT = 700;
    public static final Vector2 WINDOW_DIMENSIONS = Vector2.of(WINDOW_WIDTH, WINDOW_HEIGHT);
    public static final float WINDOW_HEIGHT_TO_TERRAIN_FACTOR = 2f / 3f;
    public static final float DAY_CYCLE_LENGTH = 40;
    public static final int
            SKY_LAYER = Layer.BACKGROUND,
            SUNSET_LAYER = SKY_LAYER + 1,
            SUN_LAYER = SUNSET_LAYER + 1,
            CLOUDS_LAYER = SUN_LAYER + 1,
            TREE_LAYER = CLOUDS_LAYER + 1,
            HALO_LAYER = Layer.BACKGROUND + 10,
            TERRAIN_LAYER = Layer.STATIC_OBJECTS,
            GAME_OBJECTS_LAYER = 0,
            NIGHT_LAYER = Layer.FOREGROUND,
            UI_LAYER = Layer.UI - 1,
            LOADING_SCREEN_LAYER = Layer.UI;
    public static final int DESIRED_FPS = 70;
    private static final String LOADING_SCREEN_IMG = "assets/loadingScreen.png";
    private static final String BACKGROUND_MUSIC_PATH = "assets/backgroundMusic.wav";
    private static final Color
            SUN_HALO_COLOR = new Color(255, 255, 0, 20),
            MOON_HALO_COLOR = new Color(255, 255, 255, 20);

    private final ImageReader imageReader;
    private final SoundReader soundReader;
    private final UserInputListener inputListener;
    private final GameObjectCollection gameObjects;
    private final int seed;
    private GameObject loadingScreen;
    public Avatar avatar;
    public World leftWorld, centerWorld, rightWorld;
    public int worldWidth;


    /**
     * @param imageReader   image reader
     * @param soundReader   sound reader
     * @param inputListener input listener
     * @param gameObjects   collection of game objects
     * @param seed          the seed
     */
    public GraphicManager(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener,
                   GameObjectCollection gameObjects, int seed) {
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.gameObjects = gameObjects;
        this.seed = seed;
        var sound = soundReader.readSound(BACKGROUND_MUSIC_PATH);
        sound.playLooped();
        initializeGraphicObjects();

    }

    /**
     * @param leftBorder  left X coordinate
     * @param rightBorder right X coordinate
     * @return World object
     */
    public World createWorld(int leftBorder, int rightBorder) {
        return new World(leftBorder, rightBorder, gameObjects, seed);
    }

    /**
     * @param x         X coordinate
     * @param y         Y coordinate
     * @param depth     column's depth
     * @param baseColor base color
     * @return column of blocks
     */
    public static ArrayList<GameObject> createBlockCol(int x, int y, int depth, Color baseColor) {
        var col = new ArrayList<GameObject>();
        for (int i = 0; i < depth; i++) {
            int height = y + (i * Block.SIZE);
            var topLeftCorner = Vector2.of(x, height);
            var color = ColorSupplier.approximateColor(baseColor);
            var renderer = new RectangleRenderable(color);
            var block = new Block(topLeftCorner, renderer);
            col.add(block);
            block.setTag("block (" + x + ',' + y + ')');
        }
        return col;
    }

    /**
     * @param seed the seed
     * @return avatar initial location
     */
    public static Vector2 getAvatarInitialLocation(int seed) {
        int x = roundX(WINDOW_WIDTH / 2, 0);
        int y = (int) (Terrain.myGroundHeightAt(x, seed) - Avatar.DIMENSIONS.y());
        return Vector2.of(x, y);
    }

    /**
     * @param x               X coordinate
     * @param growthParameter growth parameter
     * @return rounded X by Block's size
     */
    public static int roundX(int x, int growthParameter) {
        int multiplier = x / Block.SIZE;
        return Block.SIZE * (multiplier + growthParameter);
    }

    /**
     * create loading screen
     */
    private void initLoadingScreen() {
        var image = imageReader.readImage(LOADING_SCREEN_IMG, false);
        loadingScreen = new GameObject(Vector2.ZERO, WINDOW_DIMENSIONS, image);
        loadingScreen.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(loadingScreen, LOADING_SCREEN_LAYER);
        new ScheduledTask(loadingScreen, 3, false, this::loadingScreenFadeOut);
    }

    /**
     * fade out loading screen
     */
    private void loadingScreenFadeOut() {
        Consumer<Float> setValueCallback = loadingScreen.renderer()::setOpaqueness;
        float initValue = Color.OPAQUE, finalValue = 0;
        var interpolator = Transition.CUBIC_INTERPOLATOR_FLOAT;
        var transitionType = Transition.TransitionType.TRANSITION_ONCE;
        new Transition<>(loadingScreen, setValueCallback, initValue, finalValue, interpolator,
                1, transitionType, this::removeLoadingScreen);
    }

    /**
     * removing loading screen
     */
    private void removeLoadingScreen() {
        gameObjects.removeGameObject(loadingScreen, UI_LAYER);
    }

    /**
     * creating graphical objects
     */
    private void initializeGraphicObjects() {
        initLoadingScreen();
        initSky();
        initOpeningWorld();
        initAvatar();
        initBird();
    }

    /**
     * creating game's sky
     */
    private void initSky() {
        Sky.myCreate(gameObjects, imageReader);
        Night.create(gameObjects, NIGHT_LAYER, WINDOW_DIMENSIONS, DAY_CYCLE_LENGTH);
        var sun = Sun.create(gameObjects, SUN_LAYER, WINDOW_DIMENSIONS, DAY_CYCLE_LENGTH);
        SunHalo.myCreate(gameObjects, sun, SUN_HALO_COLOR, 1.3f);
        SunHalo.myCreate(gameObjects, sun, SUN_HALO_COLOR, 1.3f);
        SunHalo.myCreate(gameObjects, sun, SUN_HALO_COLOR, 1.6f);
        SunHalo.myCreate(gameObjects, sun, SUN_HALO_COLOR, 2f);
        Sunset.create(gameObjects, imageReader);
        var moon = Moon.create(DAY_CYCLE_LENGTH, gameObjects, imageReader);
        SunHalo.myCreate(gameObjects, moon, MOON_HALO_COLOR, 1.2f);
        SunHalo.myCreate(gameObjects, moon, MOON_HALO_COLOR, 1.3f);
        SunHalo.myCreate(gameObjects, moon, MOON_HALO_COLOR, 1.4f);
        SunHalo.myCreate(gameObjects, moon, MOON_HALO_COLOR, 1.5f);
        Stars.create(DAY_CYCLE_LENGTH, gameObjects, imageReader);
    }

    /**
     * creating 3 worlds for the beginning of the game
     * center for the avatar
     * and left, right an extra worlds to continue the game
     */
    private void initOpeningWorld() {
        var centerWorldLeftBorder = roundX(0, 0);
        var centerWorldRightBorder = roundX(WINDOW_WIDTH, 1);
        worldWidth = centerWorldRightBorder;
        leftWorld = new World(-1 * centerWorldRightBorder, centerWorldLeftBorder, gameObjects, seed);
        centerWorld = new World(centerWorldLeftBorder, centerWorldRightBorder, gameObjects, seed);
        rightWorld = new World(centerWorldRightBorder, 2 * centerWorldRightBorder, gameObjects, seed);
    }

    /**
     * creating an avatar
     */
    private void initAvatar() {
        avatar = Avatar.myCreate(getAvatarInitialLocation(seed), inputListener, imageReader, soundReader);
        gameObjects.addGameObject(avatar, GAME_OBJECTS_LAYER);
        initEnergyCounter();
    }

    /**
     * creating energy counter
     */
    private void initEnergyCounter() {
        var counter = new EnergyCounter(avatar, Vector2.ONES.mult(20), Vector2.ONES.mult(20));
        gameObjects.addGameObject(counter, UI_LAYER);
        counter.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * creating bird
     */
    private void initBird() {
        var bird = Bird.create(gameObjects, imageReader, seed);
        bird.addComponent(deltaTime -> respawnBird(bird));
    }

    /**
     * respawning the bird
     *
     * @param bird bird object
     */
    private void respawnBird(GameObject bird) {
        var distance = Math.abs(avatar.getCenter().x() - bird.getCenter().x());
        if (distance < worldWidth + 150) {
            return;
        }
        switchFlightDirection(bird);
        if (bird.renderer().isFlippedHorizontally()) {
            Bird.respawn(avatar.getCenter().x() + worldWidth);
        } else {
            Bird.respawn(avatar.getCenter().x() - worldWidth);
        }
    }

    /**
     * switching the bird's flying direction
     *
     * @param bird bird object
     */
    private void switchFlightDirection(GameObject bird) {
        var curVelocity = bird.getVelocity();
        bird.setVelocity(curVelocity.mult(-1));
        var direction = bird.renderer().isFlippedHorizontally();
        bird.renderer().setIsFlippedHorizontally(!direction);
    }
}
