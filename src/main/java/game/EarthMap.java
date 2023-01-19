package game;

import ecs.GameObject;
import ecs.SpriteRenderer;
import graphics.Color;
import graphics.Sprite;
import graphics.Texture;
import graphics.Window;
import org.joml.Vector2f;
import tiles.SimplexNoise;
import util.MathUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class EarthMap {

    public GameObject mapImage = new GameObject(new Vector2f(0, 0), 2);

    public EarthMap (String path) {
        // Load image from file system to BufferedImage
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Extract ARGB pixel data from the image
        int[][][] pixels = Texture.getPixels(image);
        float[][] landMask = new float[pixels.length][pixels[0].length];
        for (int x = 0; x < pixels.length; x ++) {
            for (int y = 0; y < pixels[0].length; y ++) {
                landMask[x][y] = (pixels[x][y][0] + pixels[x][y][1] + pixels[x][y][2]) / 3;
                if (pixels[x][y][3] == 0) {
                    landMask[x][y] = 0;
                }
            }
        }

        // Add some noise to the heightmap
        float[][] terrain = generate(landMask);

        // Determine color values for each pixel based on heightmap and add some slight random variation for a pixelated effect
        int scale = 1;
        float deepWater = 4;
        float shallowWater = 10;
        float sand = 20;
        float grass = 100;
        float treeline = 255;

        for (int x = 0; x < terrain.length; x ++) {
            for (int y = 0; y < terrain[0].length; y ++) {
                Color blockColor = Color.randomColor();
                if (terrain[x][y] <= treeline) {
                    blockColor = new Color(34, 77, 19, MathUtils.random(240, 255)); // dark green grass
                } if (terrain[x][y] <= grass) {
                    blockColor = new Color(31, 97, 9, MathUtils.random(240, 255)); // light green grass
                } if (terrain[x][y] <= sand) {
                    blockColor = new Color(199, 174, 46, MathUtils.random(245, 255)); // sand
                } if (terrain[x][y] <= shallowWater) {
                    blockColor = new Color(37, 91, 148, MathUtils.random(247, 255)); // shallow water
                } if (terrain[x][y] <= deepWater) {
                    blockColor = new Color(35, 81, 128, MathUtils.random(247, 255)); // deep water
                }

                pixels[x][y] = new int[]{(int) blockColor.r, (int) blockColor.g, (int) blockColor.b, (int) blockColor.a};
            }
        }

        // Convert pixel data to a ByteBuffer and load into a Texture

        mapImage.addComponent(new SpriteRenderer(new Sprite(new Texture(Texture.createByteBuffer(pixels), pixels.length, pixels[0].length, 4)), new Vector2f(Window.getWidth(), Window.getHeight())));
    }

    static double increment = 0.006; // the fineness of the noise map
    static float[][] terrain;

    public static float[][] generate (float[][] mask) {
        terrain = new float[mask.length][mask[0].length]; // create an empty 2D array with the dimensions of

        float yoff = 0;
        for (int x = 0; x < mask.length; x++) {
            float xoff = 0;
            for (int y = 0; y < mask[0].length; y++) {
                terrain[x][y] = mask[x][y];
                if (terrain[x][y] > 10) {
                    terrain[x][y] -= MathUtils.map((float) SimplexNoise.noise(xoff, yoff), -1, 1, 0, 10);
                } if (terrain[x][y] > 60) {
                    terrain[x][y] -= MathUtils.map((float) SimplexNoise.noise(xoff, yoff), -1, 1, 0, 20);
                } if (terrain[x][y] < 11) {
                    terrain[x][y] += MathUtils.map((float) SimplexNoise.noise(xoff, yoff), -1, 1, 0, 10);
                }
                xoff += increment;
            }
            yoff += increment;
        }
        return terrain;
    }
}
