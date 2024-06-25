package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.util.Vector2;
import pepse.GraphicManager;

import java.util.function.Consumer;

public class Sunset {
    /**
     * @param gameObjects collection of game objects
     * @param imageReader image reader
     * @return game object of sunset
     */
    public static GameObject create(GameObjectCollection gameObjects, ImageReader imageReader) {
        var image = imageReader.readImage("assets/sunset.png", false);
        var pos = Vector2.ZERO;
        var sunset = new GameObject(pos, GraphicManager.WINDOW_DIMENSIONS, image);
        sunset.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        createCycle(sunset);
        sunset.setTag("sunset");
        gameObjects.addGameObject(sunset, GraphicManager.SUNSET_LAYER);
        return sunset;
    }

    /**
     * create the cycle of the sunset
     *
     * @param sunset sunset game object
     */
    private static void createCycle(GameObject sunset) {
        Consumer<Float> setValueCallback = sunset.renderer()::setOpaqueness;
        float initValue = 0, finalValue = 1;
        var interpolator = Transition.CUBIC_INTERPOLATOR_FLOAT;
        interpolator = Transition.LINEAR_INTERPOLATOR_FLOAT;
        float transitionTime = GraphicManager.DAY_CYCLE_LENGTH / 4;
        var transitionType = Transition.TransitionType.TRANSITION_BACK_AND_FORTH;
        new Transition<>(sunset, setValueCallback, initValue, finalValue, interpolator,
                transitionTime, transitionType, null);
    }
}
