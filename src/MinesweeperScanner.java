public class MinesweeperScanner {
    public static final int panel_background[] = {189, 189, 189};

    public static boolean rgb_equal(int rgb_1[], int rgb_2[]) {
        return rgb_1[0] == rgb_2[0] && rgb_1[1] == rgb_2[1] && rgb_1[2] == rgb_2[2];
    }

    public static boolean rgb_similar(int rgb_1[], int rgb_2[], boolean tight) {
        int R = rgb_1[0] - rgb_2[0];
        int G = rgb_1[1] - rgb_2[1];
        int B = rgb_1[2] - rgb_2[2];
        double rmean = (rgb_1[0] + rgb_2[0]) / 2.0;
        double distance = Math.sqrt((2 + rmean / 256) * (R * R) + 4 * (G * G) + (2 + (255 - rmean) / 256) * (B * B));
        if (tight) {
            return distance < 50;
        }
        return distance < 300.0;
    }

    public static MinesweeperState scan(ScreenData screen) {

        return null;
    }

    public static void main(String[] args) {
        ScreenData screen = ScreenCapture.load_screen_from_file("/Users/caiyufei/Downloads/FireShot/踩地雷/高級1.png");
        for (int i = 0; i < screen.width; ++i) {
            for (int j = 0; j < screen.height; ++j) {
                if (rgb_similar(panel_background, screen.rgb_array[i][j], true)) {
                    screen.rgb_array[i][j] = new int[]{166, 0, 255};
                }
            }
        }
        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
    }
}