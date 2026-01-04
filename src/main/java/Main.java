import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class Main {
    private static boolean debug = true;

    private static ScreenData capture_screen(JFrame frame) {
        BufferedImage image = ScreenCapture.get_captured_screen();
        if (null == image) {
            JOptionPane.showMessageDialog(frame, "The screen capture failed.", "Error", JOptionPane.ERROR_MESSAGE);
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
                ((Graphics2D) g).setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR
                );
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
            p.getClass(); // no-op, keep minimal without extra objects
            try {
                java.lang.reflect.Field fld = p.getClass().getDeclaredField("which"); // avoid adding fields outside
                fld.setAccessible(true);
                fld.setInt(p, 1 - fld.getInt(p));
            } catch (Exception ex) {
                // fallback: repaint only (shouldn't happen)
            }
            p.repaint();
        }).start();
        return f;
    }

    private static Pair<Integer, Integer> get_width_height(JFrame frame, JTextField width_field, JTextField height_field) {
        try {
            int w = Integer.parseInt(width_field.getText());
            int h = Integer.parseInt(height_field.getText());
            return new Pair<>(w, h);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Please enter valid width and height.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return null;
    }

    private static void get_and_show_predictions(JFrame frame, boolean all, int width, int height) {
        ScreenData screen = capture_screen(frame);
        if (screen != null) {
            if (debug) {
                ScreenCapture.save_screen_to_file(screen, "Debug/captured_screen.png", "png");
            }
            MinesweeperScanner minesweeperScanner = new MinesweeperScanner(width, height);
            MinesweeperState state;
            try {
                state = minesweeperScanner.scan(screen, debug);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Didn't find your minesweeper board.", "Warning", JOptionPane.WARNING_MESSAGE);
                ex.printStackTrace();
                return;
            }
            int[][][] scanned_rgb_array = state.get_map_rgb_array(2);
            if (debug) {
                System.out.println(state);
                ScreenCapture.save_array_to_file(scanned_rgb_array, "Debug/scanned.png", "png");
            }
            char status = state.get_status();
            if ('S' == status) {
                JOptionPane.showMessageDialog(frame, "You haven't started the game. Please click at least once.", "Warning", JOptionPane.WARNING_MESSAGE);
            } else if ('L' == status) {
                JOptionPane.showMessageDialog(frame, "The game has been lost.", "Warning", JOptionPane.WARNING_MESSAGE);
            } else if ('W' == status) {
                JOptionPane.showMessageDialog(frame, "You won. Congratulations!", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                ArrayList<Pair<int[], Character>> predictions = state.get_predictions();
                int[][][] marked_rgb_array;
                if (!predictions.isEmpty()) {
                    if (all) {
                        marked_rgb_array = state.get_marked_rgb_array(predictions, 2);
                        if (debug) {
                            deleteRecursively(new File("Debug/predictions/"));
                            int index = 0;
                            for (Pair<int[], Character> prediction : predictions) {
                                ScreenCapture.save_array_to_file(state.get_marked_rgb_array(prediction, 2), "Debug/predictions/prediction_" + index + ".png", "png");
                                ++index;
                            }
                            ScreenCapture.save_array_to_file(marked_rgb_array, "Debug/predictions/all_predictions.png", "png");
                        }
                    } else {
                        Random random = new Random();
                        int random_index = random.nextInt(predictions.size());
                        marked_rgb_array = state.get_marked_rgb_array(predictions.get(random_index), 2);
                        if (debug) {
                            deleteRecursively(new File("Debug/predictions/"));
                            ScreenCapture.save_array_to_file(marked_rgb_array, "Debug/predictions/prediction_" + random_index + ".png", "png");
                        }
                    }
                    BufferedImage scanned = ScreenCapture.create_image_from_array(scanned_rgb_array);
                    BufferedImage marked = ScreenCapture.create_image_from_array(marked_rgb_array);
                    makeFlipFrame(frame, scanned, marked);
                } else {
                    JOptionPane.showMessageDialog(frame, "Cannot find any move. You have to guess.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
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
        try {
            BufferedImage icon = ScreenCapture.load_image_from_file("/images/icon.png");
            frame.setIconImage(icon);
            Taskbar.getTaskbar().setIconImage(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setSize(300, 580);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        Font bigFont = new Font("Arial", Font.BOLD, 32);
        Font smallFont = new Font("Arial", Font.PLAIN, 20);
        JLabel label_1 = new JLabel("Minesweeper");
        label_1.setFont(bigFont);
        JLabel label_2 = new JLabel("Determinator");
        label_2.setFont(bigFont);
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
        random_move_button.addActionListener(e -> {
            Pair<Integer, Integer> width_height = get_width_height(frame, width_textField, height_textField);
            if (width_height != null) {
                get_and_show_predictions(frame, false, width_height.getFirst(), width_height.getSecond());
            }
        });
        JButton all_moves_button = new JButton("<html><center>Show all possible moves</center></html>");
        all_moves_button.setFont(smallFont);
        all_moves_button.setMaximumSize(new Dimension(200, all_moves_button.getPreferredSize().height));
        all_moves_button.addActionListener(e -> {
            Pair<Integer, Integer> width_height = get_width_height(frame, width_textField, height_textField);
            if (width_height != null) {
                get_and_show_predictions(frame, true, width_height.getFirst(), width_height.getSecond());
            }
        });
        JPanel interval_inputPanel = new JPanel();
        JLabel interval_inputLabel = new JLabel("Interval: ");
        interval_inputLabel.setFont(smallFont);
        JTextField interval_textField = new JTextField(2);
        interval_textField.setText("0.2");
        interval_textField.setFont(smallFont);
        JLabel interval_unitLabel = new JLabel("s");
        interval_unitLabel.setFont(smallFont);
        interval_inputPanel.add(interval_inputLabel);
        interval_inputPanel.add(interval_textField);
        interval_inputPanel.add(interval_unitLabel);
        JButton auto_play_button = new JButton("<html><center>Start autoplay</center></html>");
        auto_play_button.setFont(smallFont);
        auto_play_button.setMaximumSize(new Dimension(200, auto_play_button.getPreferredSize().height));
        auto_play_button.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "3按鈕已被點擊！");
        });
        interval_inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, interval_inputPanel.getPreferredSize().height));
        JComponent[] components = {label_1, label_2, radioGroupPanel, width_inputPanel, height_inputPanel, random_move_button, all_moves_button, interval_inputPanel, auto_play_button};
        for (JComponent component : components) {
            centerComponent(component);
            frame.add(component);
        }
        frame.add(Box.createVerticalGlue());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int) screenSize.getWidth() - frame.getWidth(), 0);
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}