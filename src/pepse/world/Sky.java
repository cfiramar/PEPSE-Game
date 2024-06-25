package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.GraphicManager;

import java.awt.*;

import static pepse.GraphicManager.*;

public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final Vector2 CLOUD_DIMENSIONS = Vector2.of(150, 150);
    private static final Vector2
            CLOUD_0_POS = Vector2.of(-150, WINDOW_HEIGHT * 0.01f),
            CLOUD_1_POS = Vector2.of(-400, WINDOW_HEIGHT * 0.1f),
            CLOUD_2_POS = Vector2.of(-800, WINDOW_HEIGHT * 0.06f);
    private static final Vector2
            CLOUD_0_VELOCITY = Vector2.of(70, 0),
            CLOUD_1_VELOCITY = Vector2.of(75, 0),
            CLOUD_2_VELOCITY = Vector2.of(80, 0);
    private static final String
            CLOUD_0_IMG = "assets/cloud0.png",
            CLOUD_1_IMG = "assets/cloud1.png",
            CLOUD_2_IMG = "assets/cloud2.png";
    private static ImageRenderable image0, image2;
    private static ImageReader imageReader;
    private static GameObjectCollection gameObjects;
    private static GameObject sky;
    private static GameObject cloud01, cloud11, cloud21, cloud02, cloud12;


    /**
     * @param gameObjects      collection of game objects
     * @param windowDimensions window dimensions
     * @param skyLayer         sky's layer
     * @return sky object
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions,
                                      int skyLayer) {
        return myCreate(gameObjects, null);
    }

    /**
     * @param gameObjects collection of game objects
     * @param imageReader image reader
     * @return sky object
     */
    public static GameObject myCreate(GameObjectCollection gameObjects, ImageReader imageReader) {
        Sky.imageReader = imageReader;
        Sky.gameObjects = gameObjects;
        sky = new GameObject(Vector2.ZERO, GraphicManager.WINDOW_DIMENSIONS,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky, SKY_LAYER);
        sky.setTag("sky");
        if (imageReader != null) {
            initClouds();
        }
        return sky;
    }

    /**
     * adding group of clouds to the game
     */
    private static void initClouds() {
        image0 = imageReader.readImage(CLOUD_0_IMG, true);
        ImageRenderable image1 = imageReader.readImage(CLOUD_1_IMG, true);
        image2 = imageReader.readImage(CLOUD_2_IMG, true);
        cloud01 = new GameObject(CLOUD_0_POS, CLOUD_DIMENSIONS, image0);
        cloud11 = new GameObject(CLOUD_1_POS, CLOUD_DIMENSIONS, image1);
        cloud21 = new GameObject(CLOUD_2_POS, CLOUD_DIMENSIONS, image2);
        cloud01.setVelocity(CLOUD_0_VELOCITY);
        cloud11.setVelocity(CLOUD_1_VELOCITY);
        cloud21.setVelocity(CLOUD_2_VELOCITY);
        cloud01.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        cloud11.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        cloud21.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(cloud01, CLOUDS_LAYER);
        gameObjects.addGameObject(cloud11, CLOUDS_LAYER);
        gameObjects.addGameObject(cloud21, CLOUDS_LAYER);
        new ScheduledTask(sky, 30, true, Sky::respawnFirstClouds);

        new ScheduledTask(sky, 16, false, Sky::initSecondClouds);
    }

    /**
     * adding second group of clouds to the game
     */
    private static void initSecondClouds() {
        cloud02 = new GameObject(CLOUD_0_POS, CLOUD_DIMENSIONS, image0);
        cloud12 = new GameObject(CLOUD_1_POS, CLOUD_DIMENSIONS, image2);
        cloud02.setVelocity(CLOUD_2_VELOCITY);
        cloud12.setVelocity(CLOUD_1_VELOCITY);
        cloud02.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        cloud12.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(cloud02, CLOUDS_LAYER);
        gameObjects.addGameObject(cloud12, CLOUDS_LAYER);
        new ScheduledTask(sky, 30, true, Sky::respawnSecondClouds);
    }

    /**
     * respawn the first group of clouds
     */
    private static void respawnFirstClouds() {
        cloud01.setTopLeftCorner(CLOUD_0_POS);
        cloud11.setTopLeftCorner(CLOUD_1_POS);
        cloud21.setTopLeftCorner(CLOUD_2_POS);
    }

    /**
     * respawn the second group of clouds
     */
    private static void respawnSecondClouds() {
        cloud02.setTopLeftCorner(CLOUD_0_POS);
        cloud12.setTopLeftCorner(CLOUD_1_POS);
    }
}


