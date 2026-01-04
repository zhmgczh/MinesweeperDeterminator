import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class Main {
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
            JOptionPane.showMessageDialog(frame, "1按鈕已被點擊！");
        });
        JButton all_moves_button = new JButton("<html><center>Show all possible moves</center></html>");
        all_moves_button.setFont(smallFont);
        all_moves_button.setMaximumSize(new Dimension(200, all_moves_button.getPreferredSize().height));
        all_moves_button.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "2按鈕已被點擊！");
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