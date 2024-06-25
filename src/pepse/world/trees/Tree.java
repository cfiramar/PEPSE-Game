package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.GraphicManager;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.*;

public class Tree {
    private static final double TREE_CREATION_PERCENTAGE = 0.1;
    private static final double LEAF_CREATION_PERCENTAGE = 0.9f;
    private static final float SKY_TO_LENGTH_FACTOR = 0.2f;
    private static final int MIN_TRUNK_LENGTH = 4;
    private static final float TRUNK_LENGTH_TO_TREETOP_RADIUS_FACTOR = 1 / 3f;
    public static final int TREETOP_LAYER_SPACER = 1;

    private final HashMap<List<GameObject>, List<GameObject>> treetopByTrunk = new HashMap<>();
    private final GameObjectCollection gameObjects;
    private final int trunkLayer, treetopLayer;
    private final int seed;
    private Random rand;

    /**
     * @param gameObjects collection of game objects
     * @param treeLayer   layer of the tree
     * @param seed        the seed
     */
    public Tree(GameObjectCollection gameObjects, int treeLayer, int seed) {
        this.gameObjects = gameObjects;
        trunkLayer = treeLayer;
        treetopLayer = trunkLayer + TREETOP_LAYER_SPACER;
        this.seed = seed;
    }

    /**
     * @param minX min X to locate
     * @param maxX max X to locate
     */
    public void createInRange(int minX, int maxX) {
        for (int x = minX; x < maxX; x += Block.SIZE) {
            rand = new Random(Objects.hash(x, seed));
            double isTreeShouldBeCreated = rand.nextDouble();
            if (isTreeShouldBeCreated > TREE_CREATION_PERCENTAGE) {
                continue;
            }
            int trunkLength = getTrunkLength();
            var trunk = Trunk.create(x, trunkLength, seed);
            var treetop = createTreetop(x, trunkLength, trunk);
            treetopByTrunk.put(trunk, treetop);
            addTreeToGame();
        }
    }

    /**
     * delete tree
     */
    public void deleteTree() {
        for (var trunk : treetopByTrunk.keySet()) {
            for (var block : trunk) {
                gameObjects.removeGameObject(block, trunkLayer);
            }
            for (var leaf : treetopByTrunk.get(trunk)) {
                gameObjects.removeGameObject(leaf, treetopLayer);
            }
        }
    }

    /**
     * reseting leaf in the game
     *
     * @param leafToReset leaf
     */
    void resetLeaf(Leaf leafToReset) {
        var trunk = leafToReset.getTrunk();
        var leaves = treetopByTrunk.get(trunk);
        int i = leaves.indexOf(leafToReset);
        Leaf leaf = new Leaf(leafToReset.getInitialLocation(), this, trunk, seed);
        leaves.set(i, leaf);
        gameObjects.removeGameObject(leafToReset, treetopLayer);
        gameObjects.addGameObject(leaf, treetopLayer);
    }

    /**
     * @param trunkX      X coordinate
     * @param trunkLength length of trunk
     * @param trunk       the trunk
     * @return the leaves
     */
    private ArrayList<GameObject> createTreetop(int trunkX, int trunkLength, List<GameObject> trunk) {
        int radius = (int) (trunkLength * TRUNK_LENGTH_TO_TREETOP_RADIUS_FACTOR) * Block.SIZE;
        int minX = trunkX - radius;
        int minY = Terrain.myGroundHeightAt(trunkX, seed) - (trunkLength * Block.SIZE) - radius;
        int maxX = minX + 2 * radius, maxY = minY + 2 * radius;
        var leaves = new ArrayList<GameObject>();
        for (int x = minX; x <= maxX; x += Block.SIZE) {
            for (int y = minY; y <= maxY; y += Block.SIZE) {
                var leaf = createLeaf(x, y, trunk);
                if (leaf != null) {
                    leaves.add(leaf);
                }
            }
        }
        return leaves;
    }

    /**
     * @param x     x coordinate
     * @param y     y coordinate
     * @param trunk the leaf's trunk
     * @return leaf
     */
    private Leaf createLeaf(int x, int y, List<GameObject> trunk) {
        double isLeafShouldBeCreated = rand.nextDouble();
        if (isLeafShouldBeCreated > LEAF_CREATION_PERCENTAGE) {
            return null;
        }
        var topLeftCorner = new Vector2(x, y);
        return new Leaf(topLeftCorner, this, trunk, seed);
    }

    /**
     * @return trunk's length
     */
    private int getTrunkLength() {
        float SpaceAboveTerrain =
                GraphicManager.WINDOW_HEIGHT_TO_TERRAIN_FACTOR * GraphicManager.WINDOW_HEIGHT;
        float LeftoverSpace = SpaceAboveTerrain * SKY_TO_LENGTH_FACTOR;
        int maxLength = (int) (SpaceAboveTerrain - LeftoverSpace) / Block.SIZE;
        int randomLength = rand.nextInt(maxLength);
        return Math.max(randomLength, MIN_TRUNK_LENGTH);
    }

    /**
     * adding trees to the game
     */
    private void addTreeToGame() {
        for (var trunk : treetopByTrunk.keySet()) {
            addBlocksToGame(trunk, trunkLayer);
            addBlocksToGame(treetopByTrunk.get(trunk), treetopLayer);
        }
    }

    /**
     * @param blocks array of blocks
     * @param layer  layer of the blocks in the game
     */
    private void addBlocksToGame(Iterable<GameObject> blocks, int layer) {
        for (var block : blocks) {
            gameObjects.addGameObject(block, layer);
        }
    }
}
