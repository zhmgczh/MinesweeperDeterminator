import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenCapture {
    public static BufferedImage load_image_from_file(String file_path) {
        BufferedImage image = null;
        try {
            File file = new File(file_path);
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static ScreenData convert_image_to_screen(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int packed_pixels[] = new int[width * height];
        image.getRGB(0, 0, width, height, packed_pixels, 0, width);
        int rgb_array[][][] = new int[width][height][3];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int pixel = packed_pixels[j * width + i];
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                rgb_array[i][j][0] = red;
                rgb_array[i][j][1] = green;
                rgb_array[i][j][2] = blue;
            }
        }
        return new ScreenData(width, height, rgb_array);
    }

    public static ScreenData load_screen_from_file(String file_path) {
        BufferedImage image = load_image_from_file(file_path);
        return convert_image_to_screen(image);
    }

    public static ScreenData capture_screen() {
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            return convert_image_to_screen(image);
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage create_image_from_screen(ScreenData screen) {
        BufferedImage image = new BufferedImage(screen.width, screen.height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < screen.width; ++x) {
            for (int y = 0; y < screen.height; ++y) {
                int r = screen.rgb_array[x][y][0];
                int g = screen.rgb_array[x][y][1];
                int b = screen.rgb_array[x][y][2];
                int pixel = (0xFF << 24) |
                        (r << 16) |
                        (g << 8) |
                        b;
                image.setRGB(x, y, pixel);
            }
        }
        return image;
    }

    public static boolean save_image_to_file(BufferedImage image, String fiile_path, String format_name) {
        if (image == null || fiile_path == null || format_name == null || format_name.isEmpty()) {
            return false;
        }
        try {
            File outputFile = new File(fiile_path);
            if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            ImageIO.write(image, format_name, outputFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean save_screen_to_file(ScreenData screen, String fiile_path, String format_name) {
        BufferedImage image = create_image_from_screen(screen);
        return save_image_to_file(image, fiile_path, format_name);
    }

    public static void main(String[] args) {
        System.out.println("Capturing screen...");
        ScreenData screenRGB = capture_screen();
        if (screenRGB != null) {
            BufferedImage image = create_image_from_screen(screenRGB);
            save_image_to_file(image, "screen.png", "png");
        } else {
            System.out.println("Failed to capture screen.");
        }
    }
}