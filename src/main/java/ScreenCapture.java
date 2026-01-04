import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ScreenCapture {
    public static BufferedImage load_image_from_file(String file_path) {
        if (file_path.charAt(0) != '/') {
            file_path = '/' + file_path;
        }
        BufferedImage image = null;
        try (InputStream is = ScreenCapture.class.getResourceAsStream(file_path)) {
            assert is != null;
            image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }

    public static int[][][] convert_image_to_rgb_array(BufferedImage image) {
        int packed_pixels[] = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), packed_pixels, 0, image.getWidth());
        int rgb_array[][][] = new int[image.getWidth()][image.getHeight()][3];
        for (int i = 0; i < image.getWidth(); ++i) {
            for (int j = 0; j < image.getHeight(); ++j) {
                int pixel = packed_pixels[j * image.getWidth() + i];
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                rgb_array[i][j][0] = red;
                rgb_array[i][j][1] = green;
                rgb_array[i][j][2] = blue;
            }
        }
        return rgb_array;
    }

    public static ScreenData convert_image_to_screen(BufferedImage image) {
        return new ScreenData(image.getWidth(), image.getHeight(), convert_image_to_rgb_array(image));
    }

    public static ScreenData load_screen_from_file(String file_path) {
        BufferedImage image = load_image_from_file(file_path);
        assert image != null;
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
        return create_image_from_array(screen.rgb_array);
    }

    public static BufferedImage create_image_from_array(int rgb_array[][][]) {
        assert rgb_array != null && rgb_array.length > 0 && rgb_array[0] != null && rgb_array[0].length > 0;
        assert rgb_array[0][0] != null && 3 == rgb_array[0][0].length;
        BufferedImage image = new BufferedImage(rgb_array.length, rgb_array[0].length, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < rgb_array.length; ++x) {
            for (int y = 0; y < rgb_array[0].length; ++y) {
                int r = rgb_array[x][y][0];
                int g = rgb_array[x][y][1];
                int b = rgb_array[x][y][2];
                int pixel = (0xFF << 24) |
                        (r << 16) |
                        (g << 8) |
                        b;
                image.setRGB(x, y, pixel);
            }
        }
        return image;
    }

    public static void save_array_to_file(int rgb_array[][][], String file_path, String format_name) {
        assert rgb_array != null && rgb_array.length > 0 && rgb_array[0] != null && rgb_array[0].length > 0;
        assert rgb_array[0][0] != null && 3 == rgb_array[0][0].length;
        BufferedImage image = create_image_from_array(rgb_array);
        save_image_to_file(image, file_path, format_name);
    }

    public static void save_image_to_file(BufferedImage image, String fiile_path, String format_name) {
        if (image == null || fiile_path == null || format_name == null || format_name.isEmpty()) {
            return;
        }
        try {
            File outputFile = new File(fiile_path);
            if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            ImageIO.write(image, format_name, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save_screen_to_file(ScreenData screen, String fiile_path, String format_name) {
        assert screen != null;
        BufferedImage image = create_image_from_screen(screen);
        save_image_to_file(image, fiile_path, format_name);
    }

    public static BufferedImage get_captured_screen() {
        ScreenData screenRGB = capture_screen();
        if (screenRGB != null) {
            return create_image_from_screen(screenRGB);
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Capturing screen...");
        ScreenData screenRGB = capture_screen();
        if (screenRGB != null) {
            BufferedImage image = create_image_from_screen(screenRGB);
            save_image_to_file(image, "Debug/captured_screen.png", "png");
        } else {
            System.out.println("Failed to capture screen.");
        }
    }
}