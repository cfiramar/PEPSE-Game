package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

public class Night {
    private static final Float MIDNIGHT_OPACITY = 0.7f;
    private static final Color DARK_COLOR = new Color(18, 22, 44);

    /**
     * @param windowDimensions window dimensions
     * @param cycleLength      length of night
     * @param gameObjects      collection of game objects
     * @param layer            layer of the sky
     * @return night sky object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength) {
        var renderable = new RectangleRenderable(DARK_COLOR);
        var night = new GameObject(Vector2.ZERO, windowDimensions, renderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);
        night.setTag("night");
        createCycle(night, cycleLength);
        return night;
    }

    /**
     * @param night       night sky object
     * @param cycleLength length of the night
     */
    private static void createCycle(GameObject night, float cycleLength) {
        Consumer<Float> setValueCallback = night.renderer()::setOpaqueness;
        float initValue = 0, finalValue = MIDNIGHT_OPACITY;
        var interpolator = Transition.CUBIC_INTERPOLATOR_FLOAT;
        float transitionTime = cycleLength / 2f;
        var transitionType = Transition.TransitionType.TRANSITION_BACK_AND_FORTH;
        new Transition<>(night, setValueCallback, initValue, finalValue, interpolator,
                transitionTime, transitionType, null);
    }
}
