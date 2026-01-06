import java.awt.image.BufferedImage;
import java.util.*;

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
    static final char[] unfinished_operands = {BLANK, QUESTION_MARK};
    static final char[] lost_operands = {MINE_EXPLODED, MINE_UNFOUND, MINE_WRONGLY_FLAGGED};
    static final char[] operands = {BLANK, QUESTION_MARK, MINE_FLAG, MINE_EXPLODED, MINE_UNFOUND, MINE_WRONGLY_FLAGGED, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT};
    static final String[] image_names = {"blank", "question_mark", "mine_flag", "mine_exploded", "mine_unfound", "mine_wrongly_flagged", "mine_0", "mine_1", "mine_2", "mine_3", "mine_4", "mine_5", "mine_6", "mine_7", "mine_8"};
    static final String[] digit_names = {"digit_0", "digit_1", "digit_2", "digit_3", "digit_4", "digit_5", "digit_6", "digit_7", "digit_8", "digit_9"};
    static final int[][] neighborhood = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    static int[][][][] images;
    static int[][][][] digits;
    public int time_passed, remaining_mines, nrows, ncols;
    public char[][] map;

    static {
        load_images();
        load_digits();
    }

    private static void load_images() {
        if (null == images) {
            images = new int[operands.length][][][];
            for (int i = 0; i < operands.length; ++i) {
                BufferedImage image = ScreenCapture.load_image_from_file("images/" + image_names[i] + ".png");
                assert image != null;
                images[i] = ScreenCapture.convert_image_to_rgb_array(image);
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
        assert check_map_valid(map, remaining_mines, false);
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

    public boolean is_unfinished_operand(char c) {
        for (char operand : unfinished_operands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public boolean is_lost_operand(char c) {
        for (char operand : lost_operands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public char get_status() {
        boolean started = false;
        boolean unfinished = false;
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                if (is_lost_operand(map[i][j])) {
                    return 'L';
                }
                if (is_number(map[i][j])) {
                    started = true;
                } else if (is_unfinished_operand(map[i][j])) {
                    unfinished = true;
                }
            }
        }
        return started ? (unfinished ? 'P' : 'W') : 'S';
    }

    public boolean is_number(char c) {
        return c >= ZERO && c <= EIGHT;
    }

    public int to_number(char c) {
        assert is_number(c);
        return c - '0';
    }

    public boolean check_number_valid(char[][] map, int i, int j, boolean force_finished) {
        if (!is_number(map[i][j])) {
            return true;
        }
        int mines = 0;
        int blanks = 0;
        for (int[] vector : neighborhood) {
            int new_i = i + vector[0];
            int new_j = j + vector[1];
            if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length) {
                if (MINE_FLAG == map[new_i][new_j]) {
                    ++mines;
                } else if (BLANK == map[new_i][new_j] || QUESTION_MARK == map[new_i][new_j]) {
                    ++blanks;
                }
            }
        }
        if (force_finished && mines != to_number(map[i][j])) {
            return false;
        }
        if (mines > to_number(map[i][j]) || mines + blanks < to_number(map[i][j])) {
            return false;
        }
        return true;
    }

    public boolean check_number_valid(int i, int j, boolean force_finished) {
        return check_number_valid(map, i, j, force_finished);
    }

    public boolean check_map_valid(char map[][], int remaining_mines, boolean force_finished) {
        if (force_finished && 0 != remaining_mines) {
            return false;
        }
        int blanks = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (BLANK == map[i][j] || QUESTION_MARK == map[i][j]) {
                    ++blanks;
                }
                if (force_finished && is_unfinished_operand(map[i][j])) {
                    return false;
                }
                if (!is_valid_operand(map[i][j])) {
                    return false;
                }
                if (!check_number_valid(map, i, j, force_finished)) {
                    return false;
                }
            }
        }
        return remaining_mines <= blanks;
    }

    public int get_nrows() {
        return nrows;
    }

    public int get_ncols() {
        return ncols;
    }

    public char get_state(int i, int j) {
        if (i >= 0 && j >= 0 && i < nrows && j < map[0].length) {
            return map[i][j];
        }
        return ' ';
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Time passed: ").append(time_passed).append(" Remaining mines: ").append(remaining_mines).append("\nWidth: ").append(nrows).append(" Height: ").append(ncols).append('\n');
        for (int i = 0; i < ncols; ++i) {
            sb.append(map[0][i]);
            for (int j = 1; j < nrows; ++j) {
                sb.append(' ').append(map[j][i]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public int[][][] get_map_rgb_array(char[][] map) {
        int[][][] rgb = new int[nrows * images[0].length][ncols * images[0][0].length][3];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                for (int k = 0; k < operands.length; ++k) {
                    if (map[i][j] == operands[k]) {
                        int base_i = i * images[0].length;
                        int base_j = j * images[0][0].length;
                        for (int pos_i = 0; pos_i < images[k].length; ++pos_i) {
                            for (int pos_j = 0; pos_j < images[k][pos_i].length; ++pos_j) {
                                rgb[base_i + pos_i][base_j + pos_j][0] = images[k][pos_i][pos_j][0];
                                rgb[base_i + pos_i][base_j + pos_j][1] = images[k][pos_i][pos_j][1];
                                rgb[base_i + pos_i][base_j + pos_j][2] = images[k][pos_i][pos_j][2];
                            }
                        }
                        break;
                    }
                }
            }
        }
        return rgb;
    }

    public int[][][] get_map_rgb_array(char[][] map, int resize) {
        int[][][] rgb = get_map_rgb_array(map);
        return RGB.resize(rgb, rgb.length * resize, rgb[0].length * resize);
    }

    public int[][][] get_map_rgb_array(int resize) {
        return get_map_rgb_array(map, resize);
    }

    public int[][][] get_marked_rgb_array(Pair<int[], Character> mark, int resize) {
        char[][] new_map = new char[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                if (i == mark.getFirst()[0] && j == mark.getFirst()[1]) {
                    new_map[i][j] = mark.getSecond();
                } else {
                    new_map[i][j] = map[i][j];
                }
            }
        }
        return get_map_rgb_array(new_map, resize);
    }

    public int[][][] get_marked_rgb_array(ArrayList<Pair<int[], Character>> marks, int resize) {
        char[][] new_map = new char[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                new_map[i][j] = map[i][j];
            }
        }
        for (Pair<int[], Character> mark : marks) {
            new_map[mark.getFirst()[0]][mark.getFirst()[1]] = mark.getSecond();
        }
        return get_map_rgb_array(new_map, resize);
    }

    private static final int[][] unit_vectors = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private static char[][] temp_map;
    private static HashMap<int[], HashSet<Character>> possibility_map;
    private static ArrayList<int[]> all_points;
    private static long search_stop_before;
    private static boolean force_stopped = false;

    private boolean check_position_valid(int i, int j) {
        for (int[] unit_vector : unit_vectors) {
            int number_x = i + unit_vector[0];
            int number_y = j + unit_vector[1];
            if (number_x >= 0 && number_x < nrows && number_y >= 0 && number_y < ncols && is_number(map[number_x][number_y])) {
                if (!check_number_valid(temp_map, number_x, number_y, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean check_temp_map_valid(int remaining_mines, boolean force_finished) {
        if (force_finished && 0 != remaining_mines) {
            return false;
        }
        int blanks = 0;
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                if (BLANK == temp_map[i][j] || QUESTION_MARK == temp_map[i][j]) {
                    ++blanks;
                }
                if (force_finished && is_unfinished_operand(temp_map[i][j])) {
                    return false;
                }
                if (!is_valid_operand(map[i][j])) {
                    return false;
                }
                if (is_number(map[i][j]) && !check_number_valid(map, i, j, force_finished)) {
                    return false;
                }
            }
        }
        return remaining_mines <= blanks;
    }

    private void search(int point_index, int remaining_mines) {
        if (System.currentTimeMillis() > search_stop_before) {
            force_stopped = true;
            return;
        }
        if (remaining_mines >= 0) {
            if (point_index == all_points.size()) {
                if (check_temp_map_valid(remaining_mines, false)) {
                    for (int[] point : all_points) {
                        possibility_map.get(point).add(temp_map[point[0]][point[1]]);
                    }
                }
            } else {
                int x = all_points.get(point_index)[0];
                int y = all_points.get(point_index)[1];
                temp_map[x][y] = ZERO;
                if (check_position_valid(x, y)) {
                    search(point_index + 1, remaining_mines);
                }
                temp_map[x][y] = MINE_FLAG;
                if (check_position_valid(x, y)) {
                    search(point_index + 1, remaining_mines - 1);
                }
                temp_map[x][y] = BLANK;
            }
        }
    }

    public ArrayList<Pair<int[], Character>> get_predictions(int layers, long search_stop_before) {
        MinesweeperState.search_stop_before = search_stop_before;
        force_stopped = false;
        ArrayList<Pair<int[], Character>> predictions = new ArrayList<>();
        all_points = new ArrayList<>();
        HashMap<int[], Integer> point_layer = new HashMap<>();
        boolean[][] visited = new boolean[nrows][ncols];
        for (int layer = 0; layer < layers; ++layer) {
            for (int i = 0; i < nrows; ++i) {
                for (int j = 0; j < ncols; ++j) {
                    for (int[] unit_vector : unit_vectors) {
                        if (is_number(map[i][j])) {
                            int new_x = i + (layer + 1) * unit_vector[0];
                            int new_y = j + (layer + 1) * unit_vector[1];
                            if (new_x >= 0 && new_x < nrows && new_y >= 0 && new_y < ncols && !visited[new_x][new_y] && (BLANK == map[new_x][new_y] || QUESTION_MARK == map[new_x][new_y])) {
                                int[] point = new int[]{new_x, new_y};
                                all_points.add(point);
                                point_layer.put(point, layer);
                                visited[new_x][new_y] = true;
                            }
                        }
                    }
                }
            }
        }
        possibility_map = new HashMap<>();
        for (int[] point : all_points) {
            possibility_map.put(point, new HashSet<>());
        }
        if (0 == remaining_mines) {
            for (int[] point : all_points) {
                possibility_map.get(point).add(ZERO);
            }
        } else {
            temp_map = new char[nrows][ncols];
            for (int i = 0; i < nrows; ++i) {
                for (int j = 0; j < ncols; ++j) {
                    if (QUESTION_MARK == map[i][j]) {
                        temp_map[i][j] = BLANK;
                    } else {
                        temp_map[i][j] = map[i][j];
                    }
                }
            }
            try {
                Thread.ofVirtual().start(() -> {
                    search(0, remaining_mines);
                }).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (force_stopped) {
                return predictions;
            }
        }
        for (int[] point : all_points) {
            HashSet<Character> possibility_set = possibility_map.get(point);
            if (possibility_set.isEmpty()) {
                return null;
            } else if (1 == possibility_set.size()) {
                predictions.add(new Pair<>(point, (Character) possibility_set.toArray()[0]));
            }
        }
        predictions.sort(Comparator.comparingInt(o -> point_layer.get(o.getFirst())));
        return predictions;
    }
}