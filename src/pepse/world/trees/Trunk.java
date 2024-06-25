package pepse.world.trees;

import danogl.GameObject;
import pepse.world.Block;
import pepse.world.Terrain;
import pepse.GraphicManager;
import java.awt.*;
import java.util.ArrayList;

public class Trunk {
    private static final Color BASE_COLOR = new Color(100, 50, 20);

    /**
     * @param x      x coordinate
     * @param length trunk's length
     * @param seed   the seed
     * @return trunk
     */
    public static ArrayList<GameObject> create(int x, int length, int seed) {
        int y = Terrain.myGroundHeightAt(x, seed) - length * Block.SIZE;
        return GraphicManager.createBlockCol(x, y, length, BASE_COLOR);
    }

}
