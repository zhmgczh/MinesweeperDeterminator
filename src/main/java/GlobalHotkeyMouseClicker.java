import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public final class GlobalHotkeyMouseClicker implements NativeKeyListener, AutoCloseable {
    public enum MouseButton {
        LEFT(1),
        RIGHT(3);
        private final int awtButtonNumber;

        MouseButton(int awtButtonNumber) {
            this.awtButtonNumber = awtButtonNumber;
        }

        public int mask() {
            return InputEvent.getMaskForButton(awtButtonNumber);
        }
    }

    private static final class Binding {
        final Set<Integer> keys;
        final Runnable task;
        final AtomicBoolean fired = new AtomicBoolean(false);

        Binding(Set<Integer> keys, Runnable task) {
            this.keys = Collections.unmodifiableSet(new HashSet<>(keys));
            this.task = task;
        }
    }

    private final Robot robot;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "hotkey-task");
        t.setDaemon(true);
        return t;
    });
    private final Set<Integer> pressed = Collections.synchronizedSet(new HashSet<>());
    private final List<Binding> bindings = new ArrayList<>();
    private volatile boolean started = false;

    public GlobalHotkeyMouseClicker() throws AWTException {
        this.robot = new Robot();
        this.robot.setAutoDelay(5);
    }

    public GlobalHotkeyMouseClicker bindHotkey(Set<Integer> hotkeyKeys, Runnable task) {
        if (hotkeyKeys == null || hotkeyKeys.isEmpty()) {
            throw new IllegalArgumentException("'hotkeyKeys' cannot be null or empty");
        }
        if (task == null) {
            throw new IllegalArgumentException("'task' cannot be null");
        }
        bindings.add(new Binding(hotkeyKeys, task));
        return this;
    }

    public static Set<Integer> hotkey(int... nativeKeyCodes) {
        Set<Integer> s = new HashSet<>();
        for (int code : nativeKeyCodes) s.add(code);
        return s;
    }

    public void start() throws NativeHookException {
        if (started) return;
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);
        started = true;
    }

    @Override
    public void close() {
        if (!started) return;
        try {
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ignored) {
            // ignore
        } finally {
            executor.shutdownNow();
            started = false;
        }
    }

    public void clickAt(int x, int y, MouseButton button) {
        if (button == null) throw new IllegalArgumentException("'button' cannot be null");
        robot.mouseMove(x, y);
        int mask = button.mask();
        robot.mousePress(mask);
        robot.mouseRelease(mask);
    }

    public void clickAt(int x, int y, MouseButton button, int clickCount, int intervalMs) {
        if (clickCount <= 0) throw new IllegalArgumentException("'clickCount' must > 0");
        if (intervalMs < 0) throw new IllegalArgumentException("'intervalMs' must >= 0");
        for (int i = 0; i < clickCount; i++) {
            clickAt(x, y, button);
            if (i + 1 < clickCount && intervalMs > 0) robot.delay(intervalMs);
        }
    }

    public static Point currentMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        pressed.add(e.getKeyCode());
        evaluateHotkeys();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        pressed.remove(e.getKeyCode());
        synchronized (pressed) {
            for (Binding b : bindings) {
                if (!pressed.containsAll(b.keys)) {
                    b.fired.set(false);
                }
            }
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // 不用 typed（組合鍵通常看 pressed/released）
    }

    private void evaluateHotkeys() {
        synchronized (pressed) {
            for (Binding b : bindings) {
                if (pressed.containsAll(b.keys)) {
                    if (b.fired.compareAndSet(false, true)) {
                        executor.submit(() -> {
                            try {
                                b.task.run();
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try (GlobalHotkeyMouseClicker app = new GlobalHotkeyMouseClicker()
                .bindHotkey(
                        hotkey(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_F8),
                        () -> {
                            System.out.println("Hotkey: Ctrl+Alt+F8 -> LEFT click (500,500)");
                            appClickSafe(500, 500, MouseButton.LEFT);
                        })
                .bindHotkey(
                        hotkey(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_F9),
                        () -> {
                            Point p = currentMousePosition();
                            System.out.println("Hotkey: Ctrl+Alt+F9 -> RIGHT click at " + p);
                            appClickSafe(p.x, p.y, MouseButton.RIGHT);
                        })
        ) {
            app.start();
            System.out.println("Started. Press Ctrl+Alt+F8 or Ctrl+Alt+F9. Press ESC to quit.");
            app.bindHotkey(hotkey(NativeKeyEvent.VC_ESCAPE), () -> {
                System.out.println("ESC pressed. Exiting...");
                System.exit(0);
            });
            Thread.currentThread().join();
        }
    }

    private static volatile GlobalHotkeyMouseClicker instanceForDemo;

    private static void appClickSafe(int x, int y, MouseButton button) {
        GlobalHotkeyMouseClicker inst = instanceForDemo;
        if (inst != null) {
            inst.clickAt(x, y, button);
        } else {
        }
    }
}