import java.awt.image.BufferedImage;
import java.util.*;

class IllegalMapException extends IllegalArgumentException {
    public IllegalMapException(String message) {
        super(message);
    }
}

public class MinesweeperState {
    static final char BLANK = '*';
    static final char QUESTION_MARK = '?';
    static final char MINE_FLAG = 'F';
    static final char MINE_EXPLODED = 'X';
    static final char MINE_UNFOUND = 'U';
    static final char MINE_WRONGLY_FLAGGED = 'W';
    static final char ZERO = '0';
    static final char ONE = '1';
    static final char TWO = '2';
    static final char THREE = '3';
    static final char FOUR = '4';
    static final char FIVE = '5';
    static final char SIX = '6';
    static final char SEVEN = '7';
    static final char EIGHT = '8';
    static final char[] unfinished_operands = {BLANK, QUESTION_MARK};
    static final char[] lost_operands = {MINE_EXPLODED, MINE_UNFOUND, MINE_WRONGLY_FLAGGED};
    static final char[] operands = {BLANK, QUESTION_MARK, MINE_FLAG, MINE_EXPLODED, MINE_UNFOUND, MINE_WRONGLY_FLAGGED, ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT};
    static final String[] image_names = {"blank", "question_mark", "mine_flag", "mine_exploded", "mine_unfound", "mine_wrongly_flagged", "mine_0", "mine_1", "mine_2", "mine_3", "mine_4", "mine_5", "mine_6", "mine_7", "mine_8"};
    static final String[] digit_names = {"digit_0", "digit_1", "digit_2", "digit_3", "digit_4", "digit_5", "digit_6", "digit_7", "digit_8", "digit_9"};
    static final int[][] neighborhood = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    static final int[][] unit_vectors = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    static int[][][][] images;
    static int[][][][] digits;
    private final int time_passed, remaining_mines, nrows, ncols;
    private final char[][] map;

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

    public MinesweeperState(int time_passed, int remaining_mines, char[][] map) throws IllegalMapException {
        assert time_passed >= 0 && time_passed <= 999 && remaining_mines >= 0;
        assert null != map && map.length > 0 && null != map[0] && map[0].length > 0;
        if (!check_map_valid(map, remaining_mines, false)) {
            throw new IllegalMapException("The map is invalid.");
        }
        this.time_passed = time_passed;
        this.remaining_mines = remaining_mines;
        this.map = map;
        this.nrows = map.length;
        this.ncols = map[0].length;
    }

    public static boolean is_valid_operand(char c) {
        for (char operand : operands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public static boolean is_unfinished_operand(char c) {
        for (char operand : unfinished_operands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public static boolean is_lost_operand(char c) {
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

    public static boolean is_number(char c) {
        return c >= ZERO && c <= EIGHT;
    }

    public static int to_number(char c) {
        assert is_number(c);
        return c - '0';
    }

    public static boolean check_number_valid(char[][] map, int i, int j, boolean force_finished) {
        if (!is_number(map[i][j])) {
            return true;
        }
        int mines = 0;
        int blanks = 0;
        for (int[] vector : unit_vectors) {
            int new_i = i + vector[0];
            int new_j = j + vector[1];
            if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length) {
                if (MINE_FLAG == map[new_i][new_j]) {
                    ++mines;
                } else if (is_unfinished_operand(map[new_i][new_j])) {
                    ++blanks;
                }
            }
        }
        if (force_finished && mines != to_number(map[i][j])) {
            return false;
        }
        return mines <= to_number(map[i][j]) && mines + blanks >= to_number(map[i][j]);
    }

    public boolean check_number_valid(int i, int j, boolean force_finished) {
        return check_number_valid(map, i, j, force_finished);
    }

    public static boolean check_map_valid(char[][] map, int remaining_mines, boolean force_finished) {
        if (force_finished && 0 != remaining_mines) {
            return false;
        }
        int blanks = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (is_unfinished_operand(map[i][j])) {
                    if (force_finished) {
                        return false;
                    }
                    ++blanks;
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

    public boolean check_map_valid(boolean force_finished) {
        return check_map_valid(map, remaining_mines, force_finished);
    }

    public static ArrayList<Pair<Integer, Integer>> get_numbers_in_domain(char[][] map, int i, int j) {
        ArrayList<Pair<Integer, Integer>> numbers = new ArrayList<>();
        for (int[] unit_vector : unit_vectors) {
            int new_i = i + unit_vector[0];
            int new_j = j + unit_vector[1];
            if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length && is_number(map[new_i][new_j])) {
                numbers.add(new Pair<>(new_i, new_j));
            }
        }
        return numbers;
    }

    public ArrayList<Pair<Integer, Integer>> get_numbers_in_domain(int i, int j) {
        return get_numbers_in_domain(map, i, j);
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

    public static String get_map_as_string(char[][] map) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < map[0].length; ++i) {
            sb.append(map[0][i]);
            for (int j = 1; j < map.length; ++j) {
                sb.append(' ').append(map[j][i]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String toString() {
        return "Time passed: " + time_passed + " Remaining mines: " + remaining_mines + "\nWidth: " + nrows + " Height: " + ncols + '\n' + get_map_as_string(map);
    }

    public static int[][][] get_map_rgb_array(char[][] map) {
        int[][][] rgb = new int[map.length * images[0].length][map[0].length * images[0][0].length][3];
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[0].length; ++j) {
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

    public static int[][][] get_map_rgb_array(char[][] map, int resize) {
        int[][][] rgb = get_map_rgb_array(map);
        return RGB.resize(rgb, rgb.length * resize, rgb[0].length * resize);
    }

    public int[][][] get_map_rgb_array(int resize) {
        return get_map_rgb_array(map, resize);
    }

    public int[][][] get_map_rgb_array() {
        return get_map_rgb_array(map);
    }

    public int[][][] get_marked_rgb_array(Pair<Pair<Integer, Integer>, Character> mark, int resize) {
        char[][] new_map = new char[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                if (i == mark.getFirst().getFirst() && j == mark.getFirst().getSecond()) {
                    new_map[i][j] = mark.getSecond();
                } else {
                    new_map[i][j] = map[i][j];
                }
            }
        }
        return get_map_rgb_array(new_map, resize);
    }

    public int[][][] get_marked_rgb_array(Pair<Pair<Integer, Integer>, Character> mark) {
        char[][] new_map = new char[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                if (i == mark.getFirst().getFirst() && j == mark.getFirst().getSecond()) {
                    new_map[i][j] = mark.getSecond();
                } else {
                    new_map[i][j] = map[i][j];
                }
            }
        }
        return get_map_rgb_array(new_map);
    }

    public int[][][] get_marked_rgb_array(ArrayList<Pair<Pair<Integer, Integer>, Character>> marks, int resize) {
        char[][] new_map = new char[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                new_map[i][j] = map[i][j];
            }
        }
        for (Pair<Pair<Integer, Integer>, Character> mark : marks) {
            new_map[mark.getFirst().getFirst()][mark.getFirst().getSecond()] = mark.getSecond();
        }
        return get_map_rgb_array(new_map, resize);
    }

    public int[][][] get_marked_rgb_array(ArrayList<Pair<Pair<Integer, Integer>, Character>> marks) {
        char[][] new_map = new char[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                new_map[i][j] = map[i][j];
            }
        }
        for (Pair<Pair<Integer, Integer>, Character> mark : marks) {
            new_map[mark.getFirst().getFirst()][mark.getFirst().getSecond()] = mark.getSecond();
        }
        return get_map_rgb_array(new_map);
    }

    private char[][] temp_map;
    private HashMap<Pair<Integer, Integer>, HashSet<Character>> possibility_map;
    private ArrayList<Pair<Integer, Integer>> all_points;
    private ArrayList<Pair<Integer, Integer>> all_blanks;
    private long search_stop_before;
    private boolean force_stopped = false;

    private boolean check_temp_map_position_valid(int i, int j, boolean force_finished) {
        for (int[] unit_vector : unit_vectors) {
            int number_x = i + unit_vector[0];
            int number_y = j + unit_vector[1];
            if (number_x >= 0 && number_x < nrows && number_y >= 0 && number_y < ncols && is_number(map[number_x][number_y])) {
                if (!check_number_valid(temp_map, number_x, number_y, force_finished)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean check_temp_map_positions_valid(ArrayList<Pair<Integer, Integer>> all_points, int remaining_mines, boolean force_finished) {
        if (force_finished && 0 != remaining_mines) {
            return false;
        }
        for (Pair<Integer, Integer> point : all_points) {
            if (!check_temp_map_position_valid(point.getFirst(), point.getSecond(), force_finished)) {
                return false;
            }
        }
        int blanks = 0;
        for (char[] chars : temp_map) {
            for (char state : chars) {
                if (is_unfinished_operand(state)) {
                    if (force_finished) {
                        return false;
                    }
                    ++blanks;
                }
            }
        }
        return 0 <= remaining_mines && remaining_mines <= blanks;
    }

    private void quick_set_and_check_valid(int point_index, ArrayList<Pair<Integer, Integer>> all_points, char state) {
        for (int i = point_index; i < all_points.size(); ++i) {
            Pair<Integer, Integer> point = all_points.get(i);
            temp_map[point.getFirst()][point.getSecond()] = state;
        }
    }

    private void quick_reset(ArrayList<Pair<Integer, Integer>> all_points, int point_index) {
        for (int i = point_index; i < all_points.size(); ++i) {
            Pair<Integer, Integer> point = all_points.get(i);
            temp_map[point.getFirst()][point.getSecond()] = BLANK;
        }
    }

    private void search(ArrayList<Pair<Integer, Integer>> all_points, int point_index, int remaining_mines, int number_of_blanks, boolean force_finished) {
        if (force_stopped) {
            return;
        }
        if (System.currentTimeMillis() > search_stop_before) {
            force_stopped = true;
            return;
        }
        if (point_index == all_points.size()) {
            if (check_temp_map_positions_valid(all_points, remaining_mines, force_finished)) {
                for (Pair<Integer, Integer> point : all_points) {
                    possibility_map.get(point).add(temp_map[point.getFirst()][point.getSecond()]);
                }
            }
        } else if (0 == remaining_mines) {
            quick_set_and_check_valid(point_index, all_points, ZERO);
            search(all_points, all_points.size(), 0, number_of_blanks, force_finished);
            quick_reset(all_points, point_index);
        } else if (number_of_blanks - point_index == remaining_mines) {
            quick_set_and_check_valid(point_index, all_points, MINE_FLAG);
            search(all_points, all_points.size(), 0, number_of_blanks, force_finished);
            quick_reset(all_points, point_index);
        } else {
            int x = all_points.get(point_index).getFirst();
            int y = all_points.get(point_index).getSecond();
            temp_map[x][y] = ZERO;
            if (check_temp_map_position_valid(x, y, false)) {
                search(all_points, point_index + 1, remaining_mines, number_of_blanks, force_finished);
            }
            temp_map[x][y] = MINE_FLAG;
            if (check_temp_map_position_valid(x, y, false)) {
                search(all_points, point_index + 1, remaining_mines - 1, number_of_blanks, force_finished);
            }
            temp_map[x][y] = BLANK;
        }
    }

    private boolean[][] prediction_tag;

    private ArrayList<Pair<Integer, Integer>> get_prediction_points_in_domain(int i, int j) {
        ArrayList<Pair<Integer, Integer>> points = new ArrayList<>();
        for (int[] unit_vector : unit_vectors) {
            int new_i = i + unit_vector[0];
            int new_j = j + unit_vector[1];
            if (new_i >= 0 && new_i < nrows && new_j >= 0 && new_j < ncols && prediction_tag[new_i][new_j]) {
                points.add(new Pair<>(new_i, new_j));
            }
        }
        return points;
    }

    private ArrayList<ArrayList<Pair<Integer, Integer>>> get_blocks() {
        final HashSet<Pair<Integer, Integer>> all_points_hashset = new HashSet<>(all_points);
        UnionFindSet<Pair<Integer, Integer>> set = new UnionFindSet<>(all_points_hashset);
        Graph<Pair<Integer, Integer>> graph = new Graph<>(all_points_hashset);
        for (Pair<Integer, Integer> point : all_points) {
            ArrayList<Pair<Integer, Integer>> numbers_in_domain = get_numbers_in_domain(temp_map, point.getFirst(), point.getSecond());
            for (Pair<Integer, Integer> number_point : numbers_in_domain) {
                ArrayList<Pair<Integer, Integer>> prediction_points = get_prediction_points_in_domain(number_point.getFirst(), number_point.getSecond());
                for (Pair<Integer, Integer> prediction_point : prediction_points) {
                    set.union(point, prediction_point);
                    graph.add_edge(point, prediction_point, 0);
                    graph.add_edge(prediction_point, point, 0);
                }
            }
        }
        ArrayList<ArrayList<Pair<Integer, Integer>>> blocks = new ArrayList<>();
        HashSet<Pair<Integer, Integer>> visited = new HashSet<>();
        for (Pair<Integer, Integer> point : all_points) {
            Pair<Integer, Integer> root = set.find(point);
            if (!visited.contains(root)) {
                blocks.add(graph.get_bfs_order(root));
                visited.add(root);
            }
        }
        all_points.clear();
        for (ArrayList<Pair<Integer, Integer>> block : blocks) {
            all_points.addAll(block);
        }
        return blocks;
    }

    private boolean search_unfinished(ArrayList<Pair<Integer, Integer>> target_points, int remaining_mines, int number_of_blanks, boolean force_finished) {
        if (!force_stopped) {
            try {
                Thread.ofVirtual().start(() -> {
                    search(target_points, 0, remaining_mines, number_of_blanks, force_finished);
                }).join();
            } catch (InterruptedException e) {
                force_stopped = true;
            }
        }
        return force_stopped;
    }

    private void initialize_temp_map() {
        temp_map = new char[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                if (is_unfinished_operand(map[i][j])) {
                    temp_map[i][j] = BLANK;
                } else {
                    temp_map[i][j] = map[i][j];
                }
            }
        }
    }

    private boolean has_found_predictions() {
        boolean found = false;
        for (Pair<Integer, Integer> point : all_points) {
            if (1 == possibility_map.get(point).size()) {
                found = true;
                break;
            }
        }
        return found;
    }

    private void initialize_get_predictions(long search_stop_before) {
        this.search_stop_before = search_stop_before;
        force_stopped = false;
        all_points = new ArrayList<>();
        all_blanks = new ArrayList<>();
        prediction_tag = new boolean[nrows][ncols];
        boolean[][] visited = new boolean[nrows][ncols];
        for (int i = 0; i < nrows; ++i) {
            for (int j = 0; j < ncols; ++j) {
                if (is_unfinished_operand(map[i][j])) {
                    all_blanks.add(new Pair<>(i, j));
                } else {
                    visited[i][j] = true;
                }
                if (is_number(map[i][j])) {
                    int min_i = Math.max(0, i - 1);
                    int max_i = Math.min(nrows - 1, i + 1);
                    int min_j = Math.max(0, j - 1);
                    int max_j = Math.min(ncols - 1, j + 1);
                    for (int new_i = min_i; new_i <= max_i; ++new_i) {
                        for (int new_j = min_j; new_j <= max_j; ++new_j) {
                            if (!visited[new_i][new_j] && is_unfinished_operand(map[new_i][new_j])) {
                                all_points.add(new Pair<>(new_i, new_j));
                                prediction_tag[new_i][new_j] = true;
                            }
                            visited[new_i][new_j] = true;
                        }
                    }
                }
            }
        }
    }

    private void initialize_possibility_map(ArrayList<Pair<Integer, Integer>> target_points) {
        possibility_map = new HashMap<>();
        for (Pair<Integer, Integer> point : target_points) {
            possibility_map.put(point, new HashSet<>());
        }
    }

    public ArrayList<Pair<Pair<Integer, Integer>, Character>> get_predictions(long search_stop_before) {
        initialize_get_predictions(search_stop_before);
        ArrayList<Pair<Pair<Integer, Integer>, Character>> predictions = new ArrayList<>();
        if (0 == remaining_mines) {
            for (Pair<Integer, Integer> point : all_blanks) {
                predictions.add(new Pair<>(point, ZERO));
            }
        } else if (remaining_mines == all_blanks.size()) {
            for (Pair<Integer, Integer> point : all_blanks) {
                predictions.add(new Pair<>(point, MINE_FLAG));
            }
        } else {
            boolean all_blanks_included = all_points.size() == all_blanks.size();
            ArrayList<Pair<Integer, Integer>> target_points = all_points;
            initialize_temp_map();
            initialize_possibility_map(target_points);
            ArrayList<ArrayList<Pair<Integer, Integer>>> blocks = get_blocks();
            for (ArrayList<Pair<Integer, Integer>> block : blocks) {
                if (search_unfinished(block, remaining_mines, all_blanks.size(), all_blanks_included && 1 == blocks.size())) {
                    return predictions;
                }
            }
            if (blocks.size() != 1 && !has_found_predictions()) {
                initialize_possibility_map(target_points);
                if (search_unfinished(target_points, remaining_mines, all_blanks.size(), all_blanks_included)) {
                    return predictions;
                }
            }
            if (!all_blanks_included && !has_found_predictions()) {
                target_points = all_blanks;
                initialize_possibility_map(target_points);
                if (search_unfinished(target_points, remaining_mines, all_blanks.size(), true)) {
                    return predictions;
                }
            }
            for (Pair<Integer, Integer> point : target_points) {
                HashSet<Character> possibility_set = possibility_map.get(point);
                if (possibility_set.isEmpty()) {
                    return null;
                } else if (1 == possibility_set.size()) {
                    predictions.add(new Pair<>(point, (Character) possibility_set.toArray()[0]));
                }
            }
        }
        return predictions;
    }

    public ArrayList<Pair<Pair<Integer, Integer>, Character>> limit_time_get_prediction(int time_upper_limit) {
        return get_predictions(System.currentTimeMillis() + time_upper_limit);
    }

    public static void main(String[] args) {
        char[][] maps = new char[30][16];
        for (int row = 0; row < 30; ++row) {
            for (int col = 0; col < 16; ++col) {
                maps[row][col] = BLANK;
            }
        }
        maps[14][3] = THREE;
        maps[15][3] = THREE;
        maps[15][2] = ONE;
        System.out.println(get_map_as_string(maps));
        int remaining_mines = 99;
        MinesweeperState state = new MinesweeperState(0, remaining_mines, maps);
        long start_time = System.currentTimeMillis();
        System.out.println(state.get_predictions(start_time + 10000));
    }
}