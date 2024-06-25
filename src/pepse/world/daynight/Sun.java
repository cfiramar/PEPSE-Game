package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {
    private static final float initValue = 270, finalValue = -90;

    /**
     * @param windowDimensions window dimensions
     * @param cycleLength      length of day
     * @param gameObjects      collection of game objects
     * @param layer            layer of the sun
     * @return the sun object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength) {
        var renderable = new OvalRenderable(Color.YELLOW);
        var sun = OrbitingStar.create(gameObjects, cycleLength, renderable, initValue, finalValue);
        sun.setTag("sun");
        return sun;
    }
}
