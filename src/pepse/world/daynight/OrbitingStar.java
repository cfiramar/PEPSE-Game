package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.GraphicManager;

import java.util.function.Consumer;

import static pepse.GraphicManager.WINDOW_HEIGHT;
import static pepse.GraphicManager.WINDOW_WIDTH;

public class OrbitingStar {
    private static final Vector2 DIMENSIONS = new Vector2(90, 90);
    private static final float
            WINDOW_HEIGHT_TO_TOP_POS_FACTOR = 1.07692308f,
            WINDOW_WIDTH_TO_LEFT_POS_FACTOR = 2.54545455f;

    /**
     * @param cycleLength half day length
     * @param gameObjects collection of game objects
     * @param renderable  renderable
     * @param initValue   begin value
     * @param finalValue  end value
     * @return OrbitingStar game object
     */
    public static GameObject create(GameObjectCollection gameObjects, float cycleLength,
                                    Renderable renderable, float initValue, float finalValue) {
        var orbitingStar = new GameObject(Vector2.ZERO, DIMENSIONS, renderable);
        orbitingStar.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(orbitingStar, GraphicManager.SUN_LAYER);
        createCycle(orbitingStar, cycleLength, initValue, finalValue);
        return orbitingStar;
    }

    /**
     * @param orbitingStar   game object
     * @param transitionTime the transition time
     * @param initValue      begin value
     * @param finalValue     end value
     */
    private static void createCycle(GameObject orbitingStar, float transitionTime, float initValue,
                                    float finalValue) {
        Consumer<Float> setValueCallback =
                angleInSky -> orbitingStar.setCenter(calcPositionEllipse(angleInSky));
        var interpolator = Transition.LINEAR_INTERPOLATOR_FLOAT;
        var transitionType = Transition.TransitionType.TRANSITION_LOOP;
        new Transition<>(orbitingStar, setValueCallback, initValue, finalValue, interpolator,
                transitionTime, transitionType, null);
    }

    /**
     * @param angleInSky the angle from center
     * @return vector of object's center, calculated based on the angle
     */
    private static Vector2 calcPositionEllipse(float angleInSky) {
        double widthRadius = WINDOW_WIDTH / WINDOW_WIDTH_TO_LEFT_POS_FACTOR;
        double heightRadius = WINDOW_HEIGHT / WINDOW_HEIGHT_TO_TOP_POS_FACTOR;
        var center = Vector2.of(WINDOW_WIDTH / 2f, WINDOW_HEIGHT);
        float x = (float) (center.x() + widthRadius * Math.cos(angleInSky * Math.PI / 180.0));
        float y = (float) (center.y() + heightRadius * Math.sin(angleInSky * Math.PI / 180.0));
        return Vector2.of(x, y);
    }
}
