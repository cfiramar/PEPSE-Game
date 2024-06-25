package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.GraphicManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private static final int MAX_AMPLITUDE = 4;
    private static float groundHeightAtX0;

    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final int seed;

    int counter = 0;
    ArrayList<ArrayList<GameObject>> terrain = new ArrayList<>();

    /**
     * @param gameObjects      collection of game objects
     * @param groundLayer      the ground layer
     * @param windowDimensions window dimensions
     * @param seed             the seed
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.seed = seed;
        groundHeightAtX0 =
                GraphicManager.WINDOW_DIMENSIONS.y() * GraphicManager.WINDOW_HEIGHT_TO_TERRAIN_FACTOR;
    }

    /**
     * @param x X coordinate
     * @return the ground height at X
     */
    public float groundHeightAt(float x) {
        return myGroundHeightAt(x, new Random().nextInt());
    }

    /**
     * @param x    X coordinate
     * @param seed the seed
     * @return the ground height at X
     */
    public static int myGroundHeightAt(float x, int seed) {
        float blockAmount = noise(x, seed) * Block.SIZE;
        float y = blockAmount + groundHeightAtX0;
        return ((int) (y / Block.SIZE)) * Block.SIZE;
    }

    /**
     * creating range of blocks
     *
     * @param minX min X coordinate
     * @param maxX max X coordinate
     */
    public void createInRange(int minX, int maxX) {
        for (int x = minX; x < maxX; x += Block.SIZE) {
            int y = myGroundHeightAt(x, seed);
            var col = GraphicManager.createBlockCol(x, y, TERRAIN_DEPTH, BASE_GROUND_COLOR);
            terrain.add(col);
            addColToGame(col);
            counter++;
        }
    }

    /**
     * deleting terrain
     */
    public void deleteTerrain() {
        for (var col : terrain) {
            for (int i = 0; i < col.size(); i++) {
                var layer = i < MAX_AMPLITUDE ? groundLayer : groundLayer + 1;
                gameObjects.removeGameObject(col.get(i), layer);
            }
        }
    }

    /**
     * adding a column to the game
     *
     * @param col a column of blocks
     */
    private void addColToGame(ArrayList<GameObject> col) {
        for (int i = 0; i < col.size(); i++) {
            var layer = i < MAX_AMPLITUDE ? groundLayer : groundLayer + 1;
            gameObjects.addGameObject(col.get(i), layer);
        }
    }

    /**
     * @param x    X coordinate
     * @param seed the seed
     * @return noised height for the column
     */
    private static float noise(float x, int seed) {
        Random rand = new Random(Objects.hash(x, seed));
        var amplitude = rand.nextInt(MAX_AMPLITUDE);
        return (float) (Math.sin(80 * x) + Math.sin(Math.PI * x)) * amplitude;
    }
}
