import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.util.ArrayList;

public class MinesweeperAutoplay {
    private static GlobalHotkeyMouseClicker robot;

    static {
        try {
            robot = new GlobalHotkeyMouseClicker();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean single_move(Pair<int[], Character> prediction, MinesweeperScanner scanner, MinesweeperState state) {
        int i = prediction.getFirst()[0];
        int j = prediction.getFirst()[1];
        char current = state.get_state(i, j);
        int[] grid_coordinates = scanner.get_grid_coordinates(i, j);
        if (grid_coordinates != null) {
            int x = (int) ((grid_coordinates[0] + grid_coordinates[1] + 0.5) / 2.0);
            int y = (int) ((grid_coordinates[2] + grid_coordinates[3] + 0.5) / 2.0);
            char target = prediction.getSecond();
            if (MinesweeperState.QUESTION_MARK == current) {
                robot.clickAt(x, y, GlobalHotkeyMouseClicker.MouseButton.RIGHT);
                try {
                    robot.wait(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (MinesweeperState.ZERO == target) {
                robot.clickAt(x, y, GlobalHotkeyMouseClicker.MouseButton.LEFT);
            } else {
                robot.clickAt(x, y, GlobalHotkeyMouseClicker.MouseButton.RIGHT);
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean iteration(ArrayList<Pair<int[], Character>> predictions, int interval, MinesweeperScanner scanner, MinesweeperState state) {
        boolean successful = true;
        for (Pair<int[], Character> prediction : predictions) {
            successful = single_move(prediction, scanner, state);
            try {
                robot.wait(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!successful) {
                break;
            }
        }
        return successful;
    }

    public static boolean register_exit_key(Runnable on_start, Runnable on_exit) {
        try (GlobalHotkeyMouseClicker app = new GlobalHotkeyMouseClicker()) {
            app.start();
            on_start.run();
            app.bindHotkey(GlobalHotkeyMouseClicker.hotkey(NativeKeyEvent.VC_ESCAPE), on_exit);
            Thread.currentThread().join();
            return true;
        } catch (InterruptedException | NativeHookException | AWTException e) {
            e.printStackTrace();
            return false;
        }
    }
}
