package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class EnergyCounter extends GameObject {
    private static final int LOW_ENERGY = 25;
    private static final Color LOW_ENERGY_COLOR = new Color(235, 80, 30);
    private final TextRenderable textRenderable;
    private final Avatar avatar;

    /**
     * @param avatar        avatar object
     * @param topLeftCorner top left corner
     * @param dimensions    dimensions of the object
     */
    public EnergyCounter(Avatar avatar, Vector2 topLeftCorner, Vector2 dimensions) {
        super(topLeftCorner, dimensions, null);
        this.avatar = avatar;

        textRenderable = new TextRenderable("Flying Energy: " + (int) avatar.returnFlyingEnergy());
        this.renderer().setRenderable(textRenderable);
        textRenderable.setColor(Color.BLACK);
    }

    /**
     * This method is overwritten from GameObject. It sets the string value of the text object to the
     * number of current lives left.
     *
     * @param deltaTime used in the super's update method
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        textRenderable.setString("Flying Energy: " + (int) avatar.returnFlyingEnergy());
        Color color = Color.BLACK;
        if (avatar.returnFlyingEnergy() <= LOW_ENERGY) {
            color = LOW_ENERGY_COLOR;
        }
        textRenderable.setColor(color);
    }

}
