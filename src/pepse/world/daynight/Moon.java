package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;

public class Moon {
    private static final String[] PHASES =
            {"assets/moon/moon0.png",
            "assets/moon/moon1.png",
            "assets/moon/moon2.png",
            "assets/moon/moon3.png",
            "assets/moon/moon4.png",
            "assets/moon/moon5.png",
            "assets/moon/moon6.png",
            "assets/moon/moon7.png"};
    private static final float initValue = 90, finalValue = -270;

    /**
     * @param cycleLength length of night
     * @param gameObjects collection of game objects
     * @param imageReader image reader
     * @return the moon object
     */
    public static GameObject create(float cycleLength, GameObjectCollection gameObjects, ImageReader imageReader) {
        var renderable = new AnimationRenderable(PHASES, imageReader, false, cycleLength);
        var moon = OrbitingStar.create(gameObjects, cycleLength, renderable, initValue, finalValue);
        moon.setTag("moon");
        return moon;
    }
}
