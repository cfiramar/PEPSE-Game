package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.util.List;
import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

public class Leaf extends Block {
    private static final Color BASE_COLOR = new Color(50, 200, 30);
    private static final float MAX_ROTATION_ANGLE = 5;
    private static final float MIN_ROTATION_ANGLE = -1 * MAX_ROTATION_ANGLE;
    private static final float MAX_GROWTH_FACTOR = 1.15f;
    private static final float MIN_GROWTH_FACTOR = 2 - MAX_GROWTH_FACTOR;
    private static final float WIND_CYCLE = 1.6f;
    private static final int MAX_WIND_DELAY_TIME = 2;
    private static final int MAX_LIFESPAN = 35;
    private static final int FALL_SPEED = 50;
    private static final Vector2 FALL_VECTOR = Vector2.DOWN.mult(FALL_SPEED);
    private static final Vector2 FALL_RIGHT = FALL_VECTOR.add(Vector2.RIGHT.mult(80));
    private static final Vector2 FALL_LEFT = FALL_VECTOR.add(Vector2.LEFT.mult(80));
    private static final float FADE_OUT_TIME = 7f;
    private static final int NO_WEIGHT = 0;
    private Transition<Vector2> FALL_TRANSITION;

    private final Random rand;
    private final Vector2 initialLocation;
    private final List<GameObject> trunk;
    private final Tree tree;

    /**
     * @param topLeftCorner top left corner
     * @param tree          the leaf's tree
     * @param trunk         the leaf's trunk
     * @param seed          the seed
     */
    Leaf(Vector2 topLeftCorner, Tree tree, List<GameObject> trunk, int seed) {
        super(topLeftCorner, null);
        this.initialLocation = new Vector2(topLeftCorner);
        physics().setMass(NO_WEIGHT);
        this.trunk = trunk;
        this.tree = tree;
        rand = new Random(Objects.hash(topLeftCorner.x(), topLeftCorner.y(), seed));
        var color = ColorSupplier.approximateColor(BASE_COLOR);
        this.renderer().setRenderable(new RectangleRenderable(color));
        addWind();
        addLifespan();
    }

    /**
     * @param other The other GameObject.
     * @return true if this should collide with other, otherwise false
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        super.shouldCollideWith(other);
        return !(other instanceof Leaf);
    }

    /**
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        removeComponent(FALL_TRANSITION);
        this.setVelocity(Vector2.ZERO);
        transform().setAccelerationY(FALL_SPEED * 2);
    }

    /**
     * @return initial location
     */
    Vector2 getInitialLocation() {
        return initialLocation;
    }

    /**
     * @return the leaf's trunk
     */
    List<GameObject> getTrunk() {
        return trunk;
    }

    /**
     * @return random time to delay
     */
    private float getWaitTime() {
        float res = 0f;
        for (int i = 0; i < Leaf.MAX_WIND_DELAY_TIME; i++) {
            res += rand.nextFloat();
        }
        return res;
    }

    /**
     * adding wind effect
     */
    private void addWind() {
        float waitTime = getWaitTime();
        new ScheduledTask(this, waitTime, false, this::createWind);
    }

    /**
     * creating the wind effect
     */
    private void createWind() {
        createRotation();
        createDimensionChanges();
    }

    /**
     * creating the rotation of the leaf
     */
    private void createRotation() {
        Consumer<Float> setValueCallback = angle -> this.renderer().setRenderableAngle(angle);
        var interpolator = Transition.LINEAR_INTERPOLATOR_FLOAT;
        var transitionType = Transition.TransitionType.TRANSITION_BACK_AND_FORTH;
        new Transition<>(this, setValueCallback, MIN_ROTATION_ANGLE, MAX_ROTATION_ANGLE,
                interpolator, WIND_CYCLE, transitionType, null);
    }

    /**
     * change the size of the leaf, as part of the wind effect
     */
    private void createDimensionChanges() {
        Consumer<Float> setValueCallback =
                growthFactor -> this.setDimensions(new Vector2(Block.SIZE * growthFactor, Block.SIZE));
        var interpolator = Transition.LINEAR_INTERPOLATOR_FLOAT;
        var transitionType = Transition.TransitionType.TRANSITION_BACK_AND_FORTH;
        new Transition<>(this, setValueCallback, MIN_GROWTH_FACTOR, MAX_GROWTH_FACTOR,
                interpolator, WIND_CYCLE, transitionType, null);
    }

    /**
     * adding life span
     */
    private void addLifespan() {
        float waitTime = rand.nextInt(MAX_LIFESPAN);
        new ScheduledTask(this, waitTime, false, this::createFall);

    }

    /**
     * creating the fall effect
     */
    private void createFall() {
        renderer().fadeOut(FADE_OUT_TIME);
        new ScheduledTask(this, FADE_OUT_TIME + getWaitTime(), false, this::reviveLeaf);
        Consumer<Vector2> setValueCallback = this::setVelocity;
        var interpolator = Transition.LINEAR_INTERPOLATOR_VECTOR;
        var transitionType = Transition.TransitionType.TRANSITION_BACK_AND_FORTH;
        FALL_TRANSITION = new Transition<>(this, setValueCallback, FALL_RIGHT, FALL_LEFT,
                interpolator, 1, transitionType, null);
    }

    /**
     * reviving the leaf after fall
     */
    private void reviveLeaf() {
        tree.resetLeaf(this);
    }
}
