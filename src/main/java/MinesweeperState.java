import java.awt.image.BufferedImage;

public class MinesweeperState {
    public static final char BLANK = '*';
    public static final char QUESTION_MARK = '?';
    public static final char MINE_FLAG = 'F';
    public static final char MINE_EXPLODED = 'X';
    public static final char MINE_UNFOUND = 'U';
    public static final char MINE_WRONGLY_FLAGGED = 'W';
    public static final char ZERO = '0';
    public static final char ONE = '1';
    public static final char TWO = '2';
    public static final char THREE = '3';
    public static final char FOUR = '4';
    public static final char FIVE = '5';
    public static final char SIX = '6';
    public static final char SEVEN = '7';
    public static final char EIGHT = '8';
    static final char changeable_operands[] = {BLANK, QUESTION_MARK, MINE_FLAG};
    static final char unfinished_operands[] = {BLANK, QUESTION_MARK};
    static final char operands[] = {BLANK, QUESTION_MARK, MINE_FLAG, MINE_EXPLODED, MINE_UNFOUND, MINE_WRONGLY_FLAGGED, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT};
    static final String image_names[] = {"blank", "question_mark", "mine_flag", "mine_exploded", "mine_unfound", "mine_wrongly_flagged", "mine_0", "mine_1", "mine_2", "mine_3", "mine_4", "mine_5", "mine_6", "mine_7", "mine_8"};
    static final String digit_names[] = {"digit_0", "digit_1", "digit_2", "digit_3", "digit_4", "digit_5", "digit_6", "digit_7", "digit_8", "digit_9"};
    static final int neighborhood[][] = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    static int images[][][][];
    static double image_rgb_centroids[][];
    static int digits[][][][];
    public int time_passed, remaining_mines, nrows, ncols;
    public char map[][];

    static {
        load_images();
        load_digits();
    }

    private static void load_images() {
        if (null == images || null == image_rgb_centroids) {
            images = new int[operands.length][][][];
            image_rgb_centroids = new double[operands.length][];
            for (int i = 0; i < operands.length; ++i) {
                BufferedImage image = ScreenCapture.load_image_from_file("images/" + image_names[i] + ".png");
                assert image != null;
                images[i] = ScreenCapture.convert_image_to_rgb_array(image);
            }
        }
    }

    private static void load_images_abandoned() {
        if (null == images || null == image_rgb_centroids) {
            images = new int[operands.length][][][];
            image_rgb_centroids = new double[operands.length][];
            for (int i = 0; i < operands.length; ++i) {
                BufferedImage image = ScreenCapture.load_image_from_file("images/" + image_names[i] + ".png");
                assert image != null;
                images[i] = ScreenCapture.convert_image_to_rgb_array(image);
                image_rgb_centroids[i] = RGB.rgb_image_centroid_circle(images[i]);
            }
        }
    }

    private static void load_digits() {
        if (null == digits) {
            digits = new int[digit_names.length][][][];
            for (int i = 0; i < digit_names.length; ++i) {
                BufferedImage digit = ScreenCapture.load_image_from_file("images/" + digit_names[i] + ".png");
                assert digit != null;
                digits[i] = ScreenCapture.convert_image_to_rgb_array(digit);
            }
        }
    }

    public MinesweeperState(int time_passed, int remaining_mines, char map[][]) {
        assert time_passed >= 0 && time_passed <= 999 && remaining_mines >= 0;
        assert null != map && map.length > 0 && null != map[0] && map[0].length > 0;
        assert check_map_valid(map, false);
        this.time_passed = time_passed;
        this.remaining_mines = remaining_mines;
        this.map = map;
        this.nrows = map.length;
        this.ncols = map[0].length;
    }

    public boolean is_valid_operand(char c) {
        for (char operand : operands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public boolean is_changeable_operand(char c) {
        for (char operand : changeable_operands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public boolean is_unfinished_operand(char c) {
        for (char operand : unfinished_operands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public boolean is_number(char c) {
        return c >= ZERO && c <= EIGHT;
    }

    public int to_number(char c) {
        assert is_number(c);
        return c - '0';
    }

    public boolean check_number_valid(int i, int j, boolean force_finished) {
        if (!is_number(map[i][j])) {
            return true;
        }
        int mines = 0;
        for (int k = 0; k < neighborhood.length; k++) {
            int new_i = i + neighborhood[k][0];
            int new_j = j + neighborhood[k][1];
            if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length && MINE_FLAG == map[new_i][new_j]) {
                ++mines;
            }
        }
        if (force_finished && mines != to_number(map[i][j])) {
            return false;
        }
        if (mines > to_number(map[i][j])) {
            return false;
        }
        return true;
    }

    public boolean check_map_valid(char map[][], boolean force_finished) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (force_finished && is_unfinished_operand(map[i][j])) {
                    return false;
                }
                if (!is_valid_operand(map[i][j])) {
                    return false;
                }
                if (!check_number_valid(i, j, force_finished)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void move(char operand, int i, int j) {
        assert i >= 0 && i < nrows && j >= 0 && j < ncols;
        assert is_changeable_operand(operand);
        map[i][j] = operand;
    }

    public void change_map(char map[][]) {
        assert null != map && map.length == nrows && null != map[0] && map[0].length == ncols;
        assert check_map_valid(map, false);
        this.map = map;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time passed: ").append(time_passed).append(" Remaining mines: ").append(remaining_mines).
                append("\nWidth: ").append(nrows).append(" Height: ").append(ncols).append('\n');
        for (int i = 0; i < ncols; ++i) {
            sb.append(map[0][i]);
            for (int j = 1; j < nrows; ++j) {
                sb.append(' ').append(map[j][i]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}