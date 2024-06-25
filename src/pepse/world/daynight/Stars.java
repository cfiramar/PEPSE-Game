package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.util.Vector2;
import pepse.GraphicManager;


import java.util.function.Consumer;

public class Stars {
    private static final String STARS_IMG_PATH = "assets/stars.png";
    private static boolean isNight = false;
    private static GameObject stars;

    /**
     * @param cycleLength length of night
     * @param gameObjects collection of game objects
     * @param imageReader image reader
     */
    public static void create(float cycleLength, GameObjectCollection gameObjects, ImageReader imageReader) {
        var renderable = imageReader.readImage(STARS_IMG_PATH, false);
        stars = new GameObject(Vector2.ZERO, GraphicManager.WINDOW_DIMENSIONS, renderable);
        stars.renderer().fadeOut(0);
        stars.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        stars.setTag("stars");
        createCycle(cycleLength);
        gameObjects.addGameObject(stars, GraphicManager.SKY_LAYER);
    }

    /**
     * @param cycleLength length of night
     */
    private static void createCycle(float cycleLength) {
        Consumer<Float> setValueCallback = time -> fade(time, cycleLength);
        var interpolator = Transition.LINEAR_INTERPOLATOR_FLOAT;
        float transitionTime = cycleLength / 2f;
        var transitionType = Transition.TransitionType.TRANSITION_BACK_AND_FORTH;
        new Transition<>(stars, setValueCallback, 0f, cycleLength, interpolator,
                transitionTime, transitionType, null);
    }

    private static void fade(float time, float cycleLength) {
        if (time > cycleLength * 0.75 && !isNight) {
            stars.renderer().fadeIn(3);
            isNight = true;
        }
        if (time < cycleLength * 0.75 && isNight) {
            stars.renderer().fadeOut(1);
            isNight = false;
        }
    }
}
