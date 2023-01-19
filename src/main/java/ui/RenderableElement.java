package ui;
 
import graphics.Color;
import graphics.Sprite;
import graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Assets;
import util.MathUtils;

import static graphics.Color.WHITE;

/**
 * UIComponentRenderer is the renderable part of a UI component
 * it can contain a solid color, a semi-transparent color, or a texture.
 * Sprites can be tinted by the color value.
 *
 * @author Asher Haun
 * @version 11.9.2021
 * @since 11.9.2021
 */

public class RenderableElement extends Element {


    protected Vector4f color = new Color(255, 0, 0, 255).toNormalizedVec4f();
    protected Vector4f defaultColor = color;
    public Vector4f hoverColor;
    public Vector4f tintColor;

    private Sprite sprite;

    private Frame renderFrame;

//    private Vector2f size;

    /**
     * Create the spriteRenderer using a color vector, no sprite.
     *
     * @param color of type JOML Vector4f, range from 0-1
     */
    public RenderableElement(Vector4f color, Frame frame) {
        this.setColor(color);
        this.defaultColor = color;
        this.sprite = new Sprite(null);
        this.frame = frame;
        this.renderFrame = frame;
    }

    /**
     * Create the spriteRenderer using a Color object, no sprite.
     *
     * @param color of type Color, range from 0-255
     */
    public RenderableElement(Color color, Frame frame) {
        // Note that type Color is normalized below in setColor()
        this.setColor(color.toNormalizedVec4f());
        this.defaultColor = color.toNormalizedVec4f();
        this.sprite = new Sprite(null);
        this.frame = frame;
        this.renderFrame = frame;
    }

    /**
     * Create a spriteRenderer using a sprite that is already loaded.
     * Default tint color is white (no tinting visible).
     *
     * @param sprite
     */
    public RenderableElement(Sprite sprite, Frame frame) {
        this.sprite = sprite;
        this.color = WHITE.toNormalizedVec4f();
        this.defaultColor = this.color;
        this.frame = frame;
        this.renderFrame = frame;
    }

    /**
     * Create a spriteRenderer using an image from the fileSystem.
     *
     * @param path to the image (ie. "src/assets/images/pepper.png")
     */
    public RenderableElement(String path, Frame frame) {
        this.sprite = new Sprite(Assets.getTexture(path));
        this.color = WHITE.toNormalizedVec4f();
        this.defaultColor = this.color;
        this.frame = frame;
        this.renderFrame = frame;
    }

    public void setRenderFrame(Frame frame) {
        this.renderFrame = frame;
    }

    public Frame getRenderFrame() {
        return this.renderFrame;
    }

    /**
     * Initialize the Element, called once after creation.
     */
    public void start() {

    }


    /**
     * Update method called every frame by parent
     *
     * @param dt Engine.deltaTime
     */
    public void update(float dt) {

    }

    /**
     * @return type Texture of the sprite if applicable.
     */
    public Texture getTexture() {
        return sprite.getTexture();
    }

//    public Vector2f getSize() {
//        return size;
//    }

    public void setSize(Vector2f size) {
//        this.size = size;
    }

    /**
     * Set the texture of the Sprite if required.
     *
     * @param texture the new texture of this sprite
     */
    public void setTexture(Texture texture) {
        if (sprite.getTexture() != texture) {
            sprite.setTexture(texture);
        }
    }

    /**
     * @return Vector2f array of the UV coordinates of the sprite if applicable.
     */
    public Vector2f[] getTexCoords() {
        return sprite.getTextureCoordinates();
    }

    /**
     * @return a Vector4f containing the normalized (0-1) color values (R, G, B, and A)
     */
    public Vector4f getColorVector() {
        return color;
    }

    /**
     * @return type Color in standard RGBA form in the range 0-255
     */
    public Color getColor() {
        return new Color(color.x, color.y, color.z, color.w).fromNormalized();
    }

    /**
     * Change the color by passing a Vector4f
     *
     * @param color vector, values should be in the range of 0-1
     */
    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color = color;
        }
    }

    /**
     * Change the color by passing a Color object, converting it to a normalized Vector4f.
     *
     * @param color should be in range of 0-255
     */
    public void setColor(Color color) {
        if (!this.color.equals(color.toNormalizedVec4f())) {
            this.color = color.toNormalizedVec4f();
        }
    }

    /**
     * Change the alpha/opacity of the sprite and/or color
     *
     * @param a alpha/opacity
     */
    public void setAlpha(float a) {
        color.w = MathUtils.map(a, 0, 255, 0, 1);
    }

    /**
     * Change the sprite contained in the SpriteRenderer Component.
     *
     * @param sprite
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
