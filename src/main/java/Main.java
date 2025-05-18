public class Main {
    public static void main(String[] args) {
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/empty.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/empty_xp.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/process.png");
        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/process_xp.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/final.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/final_xp.png");
        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
        MinesweeperState state = MinesweeperScanner.scan(screen);
        System.out.println(state);
    }
}