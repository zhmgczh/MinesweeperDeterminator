import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class Main {
    private static boolean debug = false;

    private static void debug_captured_screen(ScreenData screen) {
        if (debug) {
            ScreenCapture.save_screen_to_file(screen, "Debug/captured_screen.png", "png");
        }
    }

    private static void debug_scanned_board(MinesweeperState state, int[][][] scanned_rgb_array) {
        if (debug) {
            System.out.println(state);
            ScreenCapture.save_array_to_file(scanned_rgb_array, "Debug/scanned.png", "png");
        }
    }

    private static void debug_all_predictions(ArrayList<Pair<Pair<Integer, Integer>, Character>> predictions, MinesweeperState state, int[][][] marked_rgb_array) {
        if (debug) {
            deleteRecursively(new File("Debug/predictions/"));
            int index = 0;
            for (Pair<Pair<Integer, Integer>, Character> prediction : predictions) {
                ScreenCapture.save_array_to_file(state.get_marked_rgb_array(prediction, 2), "Debug/predictions/prediction_" + index + ".png", "png");
                ++index;
            }
            ScreenCapture.save_array_to_file(marked_rgb_array, "Debug/predictions/all_predictions.png", "png");
        }
    }

    private static void debug_random_prediction(int[][][] marked_rgb_array, int random_index) {
        if (debug) {
            deleteRecursively(new File("Debug/predictions/"));
            ScreenCapture.save_array_to_file(marked_rgb_array, "Debug/predictions/prediction_" + random_index + ".png", "png");
        }
    }

    private static void initialize_autoplay_button(JButton autoplay_button) {
        autoplay_button.setText("<html><center>Start autoplay</center></html>");
    }

    private static void change_autoplay_button(JButton autoplay_button) {
        autoplay_button.setText("<html><center>Stop (Press Esc)</center></html>");
    }

    private static boolean check_status(MinesweeperState state, JFrame frame) {
        char status = state.get_status();
        if ('S' == status) {
            JOptionPane.showMessageDialog(frame, "You haven't started the game. Please click at least once.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if ('L' == status) {
            JOptionPane.showMessageDialog(frame, "The game has been lost.", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if ('W' == status) {
            JOptionPane.showMessageDialog(frame, "You won. Congratulations!", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            return true;
        }
        return false;
    }

    private static void scan_error_warning(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "Didn't find your minesweeper board.", "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private static void illegal_board_warning(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "The board is illegal. You have to fix it.", "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private static void prediction_not_found_warning(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "Cannot find any move. You have to guess.", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void computation_stopped_manually_warning(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "You stopped computation manually.", "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private static void screen_capture_failed_warning(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "The screen capture failed.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void positive_integer_invalid_warning(JFrame frame, String name) {
        JOptionPane.showMessageDialog(frame, "Please enter " + name + " as a positive integer.", "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private static void seconds_invalid_warning(JFrame frame, String name) {
        JOptionPane.showMessageDialog(frame, "Please enter a valid " + name + ".", "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private static void esc_key_cannot_register_warning(JFrame frame) {
        JOptionPane.showMessageDialog(frame, "Cannot register Esc key.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static ScreenData capture_screen(JFrame frame) {
        BufferedImage image = ScreenCapture.get_captured_screen();
        if (null == image) {
            screen_capture_failed_warning(frame);
            return null;
        }
        return ScreenCapture.convert_image_to_screen(image);
    }

    public static JFrame makeFlipFrame(JFrame frame, final BufferedImage img1, final BufferedImage img2) {
        frame.setVisible(false);
        final JFrame f = new JFrame();
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                frame.setVisible(true);
                f.dispose();
            }
        });
        final JPanel p = new JPanel() {
            private int which = 0;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage src = (which == 0 ? img1 : img2);
                int pw = getWidth(), ph = getHeight();
                int iw = src.getWidth(), ih = src.getHeight();
                double s = Math.min((double) pw / iw, (double) ph / ih);
                int dw = (int) Math.round(iw * s);
                int dh = (int) Math.round(ih * s);
                int x = (pw - dw) / 2;
                int y = (ph - dh) / 2;
                g.drawImage(src, x, y, dw, dh, null);
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        };
        f.setContentPane(p);
        f.pack();
        f.setLocationRelativeTo(null);
        frame.setResizable(false);
        f.setVisible(true);
        Dimension d = f.getContentPane().getSize();
        int w = d.width;
        int h = d.height;
        final BufferedImage s1 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g1 = s1.createGraphics();
        g1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g1.drawImage(img1, 0, 0, w, h, null);
        g1.dispose();
        final BufferedImage s2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = s2.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img2, 0, 0, w, h, null);
        g2.dispose();
        new javax.swing.Timer(1000, e -> {
            p.getClass();
            try {
                java.lang.reflect.Field fld = p.getClass().getDeclaredField("which");
                fld.setAccessible(true);
                fld.setInt(p, 1 - fld.getInt(p));
            } catch (Exception ex) {
            }
            p.repaint();
        }).start();
        return f;
    }

    private static int get_positive_integer(JFrame frame, JTextField field, String name) {
        try {
            int result = Integer.parseInt(field.getText());
            if (result < 1) {
                positive_integer_invalid_warning(frame, name);
            } else {
                return result;
            }
        } catch (Exception e) {
            positive_integer_invalid_warning(frame, name);
        }
        return 0;
    }

    private static int get_milliseconds(JFrame frame, JTextField interval_field, String name) {
        try {
            double seconds = Double.parseDouble(interval_field.getText());
            if (seconds <= 0) {
                seconds_invalid_warning(frame, name);
            } else {
                return (int) (seconds * 1000 + 0.5);
            }
        } catch (NumberFormatException ex) {
            seconds_invalid_warning(frame, name);
        }
        return 0;
    }

    private static void before_process_button(JButton[] buttons) {
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
    }

    private static void after_process_button(JFrame frame, JButton[] buttons) {
        for (JButton button : buttons) {
            button.setEnabled(true);
        }
        if (computation_stopped_manually) {
            computation_stopped_manually_warning(frame);
        }
    }

    static volatile boolean continue_computation;
    static volatile boolean computation_stopped_manually;
    static AutoCloseable register;

    private static void get_and_show_predictions_iteration(JFrame frame, boolean all, int width, int height, int time_upper_limit) {
        ScreenData screen = capture_screen(frame);
        if (screen != null) {
            debug_captured_screen(screen);
            MinesweeperScanner minesweeperScanner = new MinesweeperScanner(width, height);
            MinesweeperState state;
            try {
                state = minesweeperScanner.scan(screen, debug);
            } catch (Exception ex) {
                if (ex instanceof IllegalMapException) {
                    illegal_board_warning(frame);
                } else {
                    scan_error_warning(frame);
                }
                ex.printStackTrace();
                return;
            }
            int[][][] scanned_rgb_array = state.get_map_rgb_array();
            debug_scanned_board(state, scanned_rgb_array);
            if (check_status(state, frame)) {
                ArrayList<Pair<Pair<Integer, Integer>, Character>> predictions = state.limit_time_get_prediction(time_upper_limit);
                int[][][] marked_rgb_array;
                if (null == predictions) {
                    illegal_board_warning(frame);
                } else if (!predictions.isEmpty()) {
                    if (all) {
                        marked_rgb_array = state.get_marked_rgb_array(predictions);
                        debug_all_predictions(predictions, state, marked_rgb_array);
                    } else {
                        Random random = new Random();
                        int random_index = random.nextInt(predictions.size());
                        marked_rgb_array = state.get_marked_rgb_array(predictions.get(random_index));
                        debug_random_prediction(marked_rgb_array, random_index);
                    }
                    BufferedImage scanned = ScreenCapture.create_image_from_array(scanned_rgb_array);
                    BufferedImage marked = ScreenCapture.create_image_from_array(marked_rgb_array);
                    makeFlipFrame(frame, scanned, marked);
                } else if (!computation_stopped_manually) {
                    prediction_not_found_warning(frame);
                }
            }
        }
    }

    private static void get_and_show_predictions_thread(JFrame frame, boolean all, int width, int height, int time_upper_limit) {
        get_and_show_predictions_iteration(frame, all, width, height, time_upper_limit);
        continue_computation = false;
    }

    private static void get_and_show_predictions(JFrame frame, boolean all, int width, int height, int time_upper_limit, JButton[] buttons) {
        before_process_button(buttons);
        if (null == register) {
            esc_key_cannot_register_warning(frame);
            after_process_button(frame, buttons);
        } else {
            continue_computation = true;
            computation_stopped_manually = false;
            Thread workerThread = new Thread(() -> get_and_show_predictions_thread(frame, all, width, height, time_upper_limit));
            new Thread(() -> {
                while (continue_computation) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                workerThread.interrupt();
                SwingUtilities.invokeLater(() -> after_process_button(frame, buttons));
            }).start();
            workerThread.start();
        }
    }

    private static boolean autoplay_iteration(MinesweeperScanner minesweeperScanner, JFrame frame, int interval, int time_upper_limit) {
        ScreenData screen = capture_screen(frame);
        if (screen != null) {
            debug_captured_screen(screen);
            MinesweeperState state;
            try {
                state = minesweeperScanner.scan(screen, debug);
            } catch (Exception ex) {
                if (ex instanceof IllegalMapException) {
                    illegal_board_warning(frame);
                } else {
                    scan_error_warning(frame);
                }
                ex.printStackTrace();
                return false;
            }
            if (check_status(state, frame)) {
                ArrayList<Pair<Pair<Integer, Integer>, Character>> predictions = state.limit_time_get_prediction(time_upper_limit);
                if (null == predictions) {
                    illegal_board_warning(frame);
                } else if (!predictions.isEmpty()) {
                    return MinesweeperAutoplay.iteration(predictions, interval, minesweeperScanner, state);
                } else if (!computation_stopped_manually) {
                    prediction_not_found_warning(frame);
                }
            }
        }
        return false;
    }

    private static void prepare_autoplay(JButton[] buttons, JButton autoplay_button) {
        change_autoplay_button(autoplay_button);
        before_process_button(buttons);
    }

    private static void after_autoplay(JFrame frame, JButton[] buttons, JButton autoplay_button) {
        initialize_autoplay_button(autoplay_button);
        after_process_button(frame, buttons);
    }

    private static void autoplay_thread(JFrame frame, int width, int height, int interval, int time_upper_limit) {
        MinesweeperScanner minesweeperScanner = new MinesweeperScanner(width, height);
        while (continue_computation && !Thread.currentThread().isInterrupted()) {
            if (!autoplay_iteration(minesweeperScanner, frame, interval, time_upper_limit)) {
                continue_computation = false;
                break;
            }
        }
    }

    private static void autoplay(JFrame frame, int width, int height, int interval, int time_upper_limit, JButton[] buttons, JButton autoplay_button) {
        prepare_autoplay(buttons, autoplay_button);
        if (null == register) {
            esc_key_cannot_register_warning(frame);
            after_autoplay(frame, buttons, autoplay_button);
        } else {
            continue_computation = true;
            computation_stopped_manually = false;
            Thread workerThread = new Thread(() -> autoplay_thread(frame, width, height, interval, time_upper_limit));
            new Thread(() -> {
                while (continue_computation) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                workerThread.interrupt();
                SwingUtilities.invokeLater(() -> after_autoplay(frame, buttons, autoplay_button));
            }).start();
            workerThread.start();
        }
    }

    public static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }

    private static void centerComponent(JComponent c) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Minesweeper Determinator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        register = MinesweeperAutoplay.register_exit_key(() -> {
        }, () -> {
            if (continue_computation) {
                computation_stopped_manually = true;
                continue_computation = false;
            }
        });
        try {
            BufferedImage icon = ScreenCapture.load_image_from_file("/images/icon.png");
            frame.setIconImage(icon);
            Taskbar.getTaskbar().setIconImage(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        Font bigFont = new Font("Arial", Font.BOLD, 32);
        Font smallFont = new Font("Arial", Font.PLAIN, 20);
        JLabel label_1 = new JLabel("Minesweeper");
        label_1.setFont(bigFont);
        JLabel label_2 = new JLabel("Determinator");
        label_2.setFont(bigFont);
        JPanel time_inputPanel = new JPanel();
        JLabel time_inputLabel = new JLabel("Search time upper limit: ");
        time_inputLabel.setFont(smallFont);
        JTextField time_textField = new JTextField(2);
        time_textField.setText("10");
        time_textField.setFont(smallFont);
        JLabel time_unitLabel1 = new JLabel("s");
        time_unitLabel1.setFont(smallFont);
        time_inputPanel.add(time_inputLabel);
        time_inputPanel.add(time_textField);
        time_inputPanel.add(time_unitLabel1);
        time_inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, time_inputPanel.getPreferredSize().height));
        JPanel width_inputPanel = new JPanel();
        JLabel width_inputLabel = new JLabel("Width: ");
        width_inputLabel.setFont(smallFont);
        JTextField width_textField = new JTextField(3);
        width_textField.setEnabled(false);
        width_textField.setText("30");
        width_textField.setFont(smallFont);
        width_inputPanel.add(width_inputLabel);
        width_inputPanel.add(width_textField);
        width_inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, width_inputPanel.getPreferredSize().height));
        JPanel height_inputPanel = new JPanel();
        JLabel height_inputLabel = new JLabel("Height: ");
        height_inputLabel.setFont(smallFont);
        JTextField height_textField = new JTextField(3);
        height_textField.setEnabled(false);
        height_textField.setText("16");
        height_textField.setFont(smallFont);
        height_inputPanel.add(height_inputLabel);
        height_inputPanel.add(height_textField);
        height_inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height_inputPanel.getPreferredSize().height));
        JRadioButton radio1 = new JRadioButton("Beginner");
        radio1.setFont(smallFont);
        JRadioButton radio2 = new JRadioButton("Intermediate");
        radio2.setFont(smallFont);
        JRadioButton radio3 = new JRadioButton("Expert", true);
        radio3.setFont(smallFont);
        JRadioButton radio4 = new JRadioButton("Superhuman");
        radio4.setFont(smallFont);
        JRadioButton radio5 = new JRadioButton("Extraterrestrial");
        radio5.setFont(smallFont);
        JRadioButton radio6 = new JRadioButton("Custom");
        radio6.setFont(smallFont);
        ButtonGroup group = new ButtonGroup();
        group.add(radio1);
        group.add(radio2);
        group.add(radio3);
        group.add(radio4);
        group.add(radio5);
        group.add(radio6);
        radio1.addActionListener(_ -> {
            width_textField.setEnabled(false);
            height_textField.setEnabled(false);
            width_textField.setText("9");
            height_textField.setText("9");
        });
        radio2.addActionListener(_ -> {
            width_textField.setEnabled(false);
            height_textField.setEnabled(false);
            width_textField.setText("16");
            height_textField.setText("16");
        });
        radio3.addActionListener(_ -> {
            width_textField.setEnabled(false);
            height_textField.setEnabled(false);
            width_textField.setText("30");
            height_textField.setText("16");
        });
        radio4.addActionListener(_ -> {
            width_textField.setEnabled(false);
            height_textField.setEnabled(false);
            width_textField.setText("50");
            height_textField.setText("50");
        });
        radio5.addActionListener(_ -> {
            width_textField.setEnabled(false);
            height_textField.setEnabled(false);
            width_textField.setText("100");
            height_textField.setText("100");
        });
        radio6.addActionListener(_ -> {
            width_textField.setEnabled(true);
            height_textField.setEnabled(true);
        });
        JPanel radioGroupPanel = new JPanel();
        radioGroupPanel.setLayout(new BoxLayout(radioGroupPanel, BoxLayout.Y_AXIS));
        radioGroupPanel.setOpaque(false);
        JRadioButton[] radios = {radio1, radio2, radio3, radio4, radio5, radio6};
        for (JRadioButton rb : radios) {
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            radioGroupPanel.add(rb);
        }
        JButton random_move_button = new JButton("<html><center>Show one possible move randomly</center></html>");
        random_move_button.setFont(smallFont);
        random_move_button.setMaximumSize(new Dimension(200, random_move_button.getPreferredSize().height));
        JButton all_moves_button = new JButton("<html><center>Show all possible moves</center></html>");
        all_moves_button.setFont(smallFont);
        all_moves_button.setMaximumSize(new Dimension(200, all_moves_button.getPreferredSize().height));
        JPanel interval_inputPanel = new JPanel();
        JLabel interval_inputLabel = new JLabel("Interval: ");
        interval_inputLabel.setFont(smallFont);
        JTextField interval_textField = new JTextField(3);
        interval_textField.setText("0.1");
        interval_textField.setFont(smallFont);
        JLabel time_unitLabel = new JLabel("s");
        time_unitLabel.setFont(smallFont);
        interval_inputPanel.add(interval_inputLabel);
        interval_inputPanel.add(interval_textField);
        interval_inputPanel.add(time_unitLabel);
        interval_inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, interval_inputPanel.getPreferredSize().height));
        JButton autoplay_button = new JButton();
        initialize_autoplay_button(autoplay_button);
        autoplay_button.setFont(smallFont);
        autoplay_button.setMaximumSize(new Dimension(200, autoplay_button.getPreferredSize().height));
        JButton[] all_buttons = new JButton[]{random_move_button, all_moves_button, autoplay_button};
        random_move_button.addActionListener(e -> {
            int time_upper_limit = get_milliseconds(frame, time_textField, "search time upper limit");
            int width = get_positive_integer(frame, width_textField, "width");
            int height = get_positive_integer(frame, height_textField, "height");
            if (0 != time_upper_limit && 0 != width && 0 != height) {
                get_and_show_predictions(frame, false, width, height, time_upper_limit, all_buttons);
            }
        });
        all_moves_button.addActionListener(e -> {
            int time_upper_limit = get_milliseconds(frame, time_textField, "search time upper limit");
            int width = get_positive_integer(frame, width_textField, "width");
            int height = get_positive_integer(frame, height_textField, "height");
            if (0 != time_upper_limit && 0 != width && 0 != height) {
                get_and_show_predictions(frame, true, width, height, time_upper_limit, all_buttons);
            }
        });
        autoplay_button.addActionListener(e -> {
            int time_upper_limit = get_milliseconds(frame, time_textField, "search time upper limit");
            int width = get_positive_integer(frame, width_textField, "width");
            int height = get_positive_integer(frame, height_textField, "height");
            int interval = get_milliseconds(frame, interval_textField, "interval");
            if (0 != time_upper_limit && 0 != width && 0 != height && 0 != interval) {
                autoplay(frame, width, height, interval, time_upper_limit, all_buttons, autoplay_button);
            }
        });
        JLabel label_3 = new JLabel("(Press Esc to stop computation)");
        label_3.setFont(smallFont);
        interval_inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, interval_inputPanel.getPreferredSize().height));
        JComponent[] components = {label_1, label_2, time_inputPanel, radioGroupPanel, width_inputPanel, height_inputPanel, random_move_button, all_moves_button, interval_inputPanel, autoplay_button, label_3};
        for (JComponent component : components) {
            centerComponent(component);
            mainPanel.add(component);
        }
        mainPanel.add(Box.createVerticalGlue());
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frame.setContentPane(scrollPane);
        frame.pack();
        frame.setSize(frame.getWidth() + 20, frame.getHeight());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int) screenSize.getWidth() - frame.getWidth(), 0);
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.getContentPane().setFocusable(true);
        frame.getContentPane().requestFocusInWindow();
    }
}