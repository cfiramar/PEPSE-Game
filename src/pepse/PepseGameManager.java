package pepse;

import danogl.GameManager;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;

import pepse.world.Avatar;
import pepse.world.Terrain;
import pepse.world.World;
import pepse.world.trees.Tree;

import java.util.Random;

public class PepseGameManager extends GameManager {

    private static final String WINDOW_NAME = "Pepse Max";
    private final int seed;
    private GraphicManager graphicManager;

    /**
     * constructor
     *
     * @param windowTitle      window's title
     * @param windowDimensions window dimensions
     */
    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        this(windowTitle, windowDimensions, null);
    }

    /**
     * constructor
     *
     * @param windowTitle      window's title
     * @param windowDimensions window dimensions
     * @param seed             the seed
     */
    public PepseGameManager(String windowTitle, Vector2 windowDimensions, String seed) {
        super(windowTitle, windowDimensions);
        this.seed = seed != null ? seed.hashCode() : new Random().nextInt();
    }

    /**
     * This method initializes a new game.
     * It creates all game objects,
     * sets their values and initial positions and allow the start of a game.
     *
     * @param imageReader      an object used to read images from the disc and render them
     * @param soundReader      an object used to read sound files from the disc and render them
     * @param inputListener    a listener capable of reading user keyboard inputs
     * @param windowController a controller used to control the window and its attributes
     */
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        graphicManager = new GraphicManager(imageReader, soundReader, inputListener,
                gameObjects(), seed);
        windowController.setTargetFramerate(GraphicManager.DESIRED_FPS);

        setCamera(new Camera(graphicManager.avatar, Vector2.ZERO.add(Vector2.of(0, -100)),
                GraphicManager.WINDOW_DIMENSIONS, GraphicManager.WINDOW_DIMENSIONS));
        var windowCenter = GraphicManager.WINDOW_DIMENSIONS.mult(0.5f);
        var avatarLocation = GraphicManager.getAvatarInitialLocation(seed);
        var pos = windowCenter.add(avatarLocation.mult(-1)).add(Vector2.UP.mult(Avatar.DIMENSIONS.x()));
        setCamera(new Camera(graphicManager.avatar, pos, GraphicManager.WINDOW_DIMENSIONS,
                GraphicManager.WINDOW_DIMENSIONS));
        gameObjects().layers().shouldLayersCollide(GraphicManager.GAME_OBJECTS_LAYER, GraphicManager.TREE_LAYER, true);
        gameObjects().layers().shouldLayersCollide(GraphicManager.TERRAIN_LAYER,
                GraphicManager.TREE_LAYER + Tree.TREETOP_LAYER_SPACER, true);
    }

    /**
     * This method overrides the GameManager update method.
     * It checks for game status, and triggers a new game popup.
     *
     * @param deltaTime used in the super's update method
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        handleWorldsPassing();
        fixAvatarUndedectedCollisions();

    }

    /**
     * preventing avatar from falling inside the terrain
     */
    private void fixAvatarUndedectedCollisions() {
        var avatar = graphicManager.avatar;
        var avatarBottomY = avatar.getTopLeftCorner().y() + Avatar.DIMENSIONS.y();
        var avatarX = avatar.getCenter().x();
        var terrainHeight = Terrain.myGroundHeightAt(GraphicManager.roundX((int) avatarX, 0), seed);
        if (avatarBottomY > terrainHeight + 5) {
            graphicManager.avatar.setCenter(Vector2.of(avatarX, terrainHeight - Avatar.DIMENSIONS.y() / 2));
        }
    }

    /**
     * handle avatar passing between worlds
     */
    private void handleWorldsPassing() {
        var avatarXLocation = graphicManager.avatar.getCenter().x();
        if (graphicManager.rightWorld.isAvatarAtThisWorld(avatarXLocation)) {
            var leftBorder = graphicManager.rightWorld.rightBorder;
            var rightBorder = graphicManager.rightWorld.rightBorder + graphicManager.worldWidth;
            World aWholeNewWorld = graphicManager.createWorld(leftBorder, rightBorder);

            graphicManager.leftWorld.removeWorld();
            graphicManager.leftWorld = graphicManager.centerWorld;
            graphicManager.centerWorld = graphicManager.rightWorld;
            graphicManager.rightWorld = aWholeNewWorld;
        } else if (graphicManager.leftWorld.isAvatarAtThisWorld(avatarXLocation)) {
            var leftBorder = graphicManager.leftWorld.leftBorder - graphicManager.worldWidth;
            var rightBorder = graphicManager.leftWorld.leftBorder;
            World aWholeNewWorld = graphicManager.createWorld(leftBorder, rightBorder);

            graphicManager.rightWorld.removeWorld();
            graphicManager.rightWorld = graphicManager.centerWorld;
            graphicManager.centerWorld = graphicManager.leftWorld;
            graphicManager.leftWorld = aWholeNewWorld;
        }
    }

    public static void main(String[] args) {
        String seed = args.length > 0 ? args[0] : null;
        new PepseGameManager(WINDOW_NAME, GraphicManager.WINDOW_DIMENSIONS, seed).run();
    }
}
