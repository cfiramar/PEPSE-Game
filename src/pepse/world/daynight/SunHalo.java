package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

import static pepse.GraphicManager.HALO_LAYER;

public class SunHalo {
    private static final float DEFAULT_SUN_TO_HALO_FACTOR = 1.3f;

    /**
     * @param gameObjects collection of game objects
     * @param sun         sun game object
     * @param color       color of the halo
     * @param layer       layer of the followed object
     * @return sun halo game object
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun, Color color) {
        return myCreate(gameObjects, sun, color, DEFAULT_SUN_TO_HALO_FACTOR);
    }

    public static GameObject myCreate(GameObjectCollection gameObjects, GameObject star,
                                      Color color, float sunToHaloFactor) {
        var dimensions = star.getDimensions().mult(sunToHaloFactor);
        var renderable = new OvalRenderable(color);
        var sunHalo = new GameObject(Vector2.ZERO, dimensions, renderable);
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sunHalo, HALO_LAYER);
        sunHalo.setTag("halo: sizeFactor=" + sunToHaloFactor + ", color=" + color);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(star.getCenter()));
        return sunHalo;
    }
}