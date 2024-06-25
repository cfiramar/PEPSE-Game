package pepse.world;

import danogl.collisions.GameObjectCollection;
import pepse.world.trees.Tree;
import static pepse.GraphicManager.*;

public class World {
    public final int leftBorder;
    public final int rightBorder;
    private final Terrain terrain;
    private final Tree tree;


    /**
     * @param leftBorder  left X border
     * @param rightBorder right X border
     * @param gameObjects collection of game objects
     * @param seed        the seed
     */
    public World(int leftBorder, int rightBorder, GameObjectCollection gameObjects, int seed) {
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        terrain = new Terrain(gameObjects, TERRAIN_LAYER, WINDOW_DIMENSIONS, seed);
        tree = new Tree(gameObjects, TREE_LAYER, seed);
        initWorld();
    }

    /**
     * @param x X coordinate
     * @return true if the avatar is in this world
     */
    public boolean isAvatarAtThisWorld(float x) {
        return x >= leftBorder && x <= rightBorder;
    }

    /**
     * remove all this world's objects from the game
     */
    public void removeWorld() {
        terrain.deleteTerrain();
        tree.deleteTree();
    }

    /**
     * creating all the world's object
     */
    private void initWorld() {
        terrain.createInRange(leftBorder, rightBorder);
        tree.createInRange(leftBorder, rightBorder);
    }
}
