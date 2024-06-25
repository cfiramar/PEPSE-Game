package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;

import java.util.Random;

import static pepse.GraphicManager.*;

public class Bird {
    private static GameObject bird;

    private static final String[] BIRD_IMAGES = {"assets/bird/bird0.png",
                                                 "assets/bird/bird1.png",
                                                 "assets/bird/bird2.png",
                                                 "assets/bird/bird3.png",
                                                 "assets/bird/bird4.png",
                                                 "assets/bird/bird5.png",
                                                 "assets/bird/bird6.png"};
    private static final float TIME_BETWEEN_BIRD_IMAGES = 0.07f;
    private static final Vector2 BIRD_START_PLACE = Vector2.of(WINDOW_WIDTH / -1.5f, WINDOW_HEIGHT * 0.05f);

    /**
     *
     * @param gameObjects collection of game objects
     * @param imageReader image reader
     * @param seed the seed
     * @return the bird
     */
    public static GameObject create(GameObjectCollection gameObjects, ImageReader imageReader, int seed) {
        var birdSprite = new AnimationRenderable(BIRD_IMAGES, imageReader, false, TIME_BETWEEN_BIRD_IMAGES);
        var pos = BIRD_START_PLACE.add(Vector2.DOWN.mult(new Random(seed).nextInt(50)));
        bird = new GameObject(pos, Vector2.of(55,51.714f), birdSprite);
        bird.setVelocity(Vector2.RIGHT.mult(250));
        gameObjects.addGameObject(bird, CLOUDS_LAYER);
        return bird;
    }

    /**
     * reloacting the bird
     * @param x x coordinate
     */
    public static void respawn(float x) {
        bird.setCenter(Vector2.of(x, BIRD_START_PLACE.y()));
    }
}
