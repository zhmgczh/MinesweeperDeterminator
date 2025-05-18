import java.util.*;

public class MinesweeperScanner {
    public static final int panel_background[][] = {{189, 189, 189}};
    public static final int digits_boards_background[][] = {{0, 0, 0}};
    public static final int board_border[][] = {{131, 131, 131}, {255, 255, 255}};
    public static final int split_grid_line[][] = {{130, 130, 130}};
    public static final int number_to_compute_average_grid_size = 10;
    int grid_size[];

    public MinesweeperScanner(int width, int height) {
        assert width > 0 && height > 0;
        grid_size = new int[]{width, height};
    }

    public MinesweeperScanner(int grid_size[]) {
        assert grid_size != null && grid_size.length == 2 && grid_size[0] > 0 && grid_size[1] > 0;
        this.grid_size = grid_size;
    }

    public MinesweeperScanner() {
    }

    public static int[] find_panel_coordinates(ScreenData screen) {
        assert screen != null;
        ArrayList<ArrayList<int[]>> blocks = RGB.find_similar_color_blocks(screen.rgb_array, 0, screen.width, 0, screen.height, panel_background, 50);
        int max_area = 0;
        int width_l = 0, width_r = screen.width, height_l = 0, height_r = screen.height;
        for (int i = 0; i < blocks.size(); ++i) {
            int w_l = Integer.MAX_VALUE, w_r = 0, h_l = Integer.MAX_VALUE, h_r = 0;
            for (int j = 0; j < blocks.get(i).size(); ++j) {
                w_l = Math.min(w_l, blocks.get(i).get(j)[0]);
                w_r = Math.max(w_r, blocks.get(i).get(j)[0] + 1);
                h_l = Math.min(h_l, blocks.get(i).get(j)[1]);
                h_r = Math.max(h_r, blocks.get(i).get(j)[1] + 1);
            }
            int block_area = (w_r - w_l) * (h_r - h_l);
            if (max_area < block_area) {
                width_l = w_l;
                width_r = w_r;
                height_l = h_l;
                height_r = h_r;
                max_area = block_area;
            }
        }
        return new int[]{width_l, width_r, height_l, height_r};
    }

    public static int[] find_remaining_mines_and_time_coordinates(ScreenData screen, int panel_coordinates[]) {
        assert screen != null && panel_coordinates != null && panel_coordinates.length == 4;
        assert panel_coordinates[0] < panel_coordinates[1] && panel_coordinates[2] < panel_coordinates[3];
        ArrayList<ArrayList<int[]>> blocks = RGB.find_similar_color_blocks(screen.rgb_array, panel_coordinates[0], panel_coordinates[1], panel_coordinates[2], panel_coordinates[3], digits_boards_background, 50);
        int max_area_1 = 0, max_area_2 = 0;
        int width_l_1 = panel_coordinates[0], width_r_1 = panel_coordinates[1], height_l_1 = panel_coordinates[2], height_r_1 = panel_coordinates[3];
        int width_l_2 = panel_coordinates[0], width_r_2 = panel_coordinates[1], height_l_2 = panel_coordinates[2], height_r_2 = panel_coordinates[3];
        for (int i = 0; i < blocks.size(); ++i) {
            int w_l = Integer.MAX_VALUE, w_r = 0, h_l = Integer.MAX_VALUE, h_r = 0;
            for (int j = 0; j < blocks.get(i).size(); ++j) {
                w_l = Math.min(w_l, blocks.get(i).get(j)[0]);
                w_r = Math.max(w_r, blocks.get(i).get(j)[0] + 1);
                h_l = Math.min(h_l, blocks.get(i).get(j)[1]);
                h_r = Math.max(h_r, blocks.get(i).get(j)[1] + 1);
            }
            int block_area = (w_r - w_l) * (h_r - h_l);
            if (max_area_2 < block_area) {
                if (max_area_1 < block_area) {
                    width_l_2 = width_l_1;
                    width_r_2 = width_r_1;
                    height_l_2 = height_l_1;
                    height_r_2 = height_r_1;
                    max_area_2 = max_area_1;
                    width_l_1 = w_l;
                    width_r_1 = w_r;
                    height_l_1 = h_l;
                    height_r_1 = h_r;
                    max_area_1 = block_area;
                } else {
                    width_l_2 = w_l;
                    width_r_2 = w_r;
                    height_l_2 = h_l;
                    height_r_2 = h_r;
                    max_area_2 = block_area;
                }
            }
        }
        return new int[]{width_l_1, width_r_1, height_l_1, height_r_1, width_l_2, width_r_2, height_l_2, height_r_2};
    }

    public static int[] find_board_coordinates(ScreenData screen, int panel_coordinates[]) {
        assert screen != null;
        ArrayList<ArrayList<int[]>> blocks = RGB.find_similar_color_blocks(screen.rgb_array, panel_coordinates[0], panel_coordinates[1], panel_coordinates[2], panel_coordinates[3], board_border, 50);
        int max_area = 0;
        int width_l = 0, width_r = screen.width, height_l = 0, height_r = screen.height;
        for (int i = 0; i < blocks.size(); ++i) {
            int w_l = Integer.MAX_VALUE, w_r = 0, h_l = Integer.MAX_VALUE, h_r = 0;
            for (int j = 0; j < blocks.get(i).size(); ++j) {
                w_l = Math.min(w_l, blocks.get(i).get(j)[0]);
                w_r = Math.max(w_r, blocks.get(i).get(j)[0] + 1);
                h_l = Math.min(h_l, blocks.get(i).get(j)[1]);
                h_r = Math.max(h_r, blocks.get(i).get(j)[1] + 1);
            }
            int block_area = (w_r - w_l) * (h_r - h_l);
            if (block_area == (panel_coordinates[1] - panel_coordinates[0]) * (panel_coordinates[3] - panel_coordinates[2])) {
                continue;
            }
            if (max_area < block_area) {
                width_l = w_l;
                width_r = w_r;
                height_l = h_l;
                height_r = h_r;
                max_area = block_area;
            }
        }
        boolean map[][] = new boolean[width_r - width_l + 1][height_r - height_l + 1];
        for (int i = 0; i < map.length; ++i) {
            Arrays.fill(map[i], true);
        }
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[0].length; ++j) {
                if (RGB.rgb_similar(screen.rgb_array[width_r - i][height_r - j], screen.rgb_array[width_r - 1][height_r - 1], 50)) {
                    map[i][j] = false;
                }
            }
        }
        int max_side_lengths[][] = find_max_side_lengths(map, 0, 2, 0, 2);
        width_l += max_side_lengths[1][1];
        width_r -= max_side_lengths[1][1];
        height_l += max_side_lengths[1][1];
        height_r -= max_side_lengths[1][1];
        return new int[]{width_l, width_r, height_l, height_r};
    }

    public static boolean side_length_augmentable(boolean map[][], int i, int j, int side_length) {
        if (i + side_length > map.length || j + side_length > map[0].length) {
            return false;
        }
        for (int k = i; k < i + side_length; ++k) {
            if (map[k][j + side_length - 1]) {
                return false;
            }
        }
        for (int k = j; k < j + side_length - 1; ++k) {
            if (map[i + side_length - 1][k]) {
                return false;
            }
        }
        return true;
    }

    public static int[][] find_max_side_lengths(boolean map[][], int start_i, int end_i, int start_j, int end_j) {
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0;
        assert start_i >= 0 && start_i < map.length;
        assert end_i >= start_i && end_i <= map.length;
        assert start_j >= 0 && start_j < map[0].length;
        assert end_j >= start_j && end_j <= map[0].length;
        int max_side_lengths[][] = new int[map.length][map[0].length];
        for (int i = start_i; i < end_i; ++i) {
            for (int j = start_j; j < end_j; ++j) {
                if (!map[i][j]) {
                    int side_length = Math.max(1, max_side_lengths[i][j]);
                    while (side_length_augmentable(map, i, j, side_length + 1)) {
                        ++side_length;
                    }
                    for (int k = i; k < i + side_length; ++k) {
                        for (int l = j; l < j + side_length; ++l) {
                            max_side_lengths[k][l] = Math.max(max_side_lengths[k][l], side_length - Math.max(k - i, l - j));
                        }
                    }
                }
            }
        }
        return max_side_lengths;
    }

    public static int[] find_grid_size_abandoned(ScreenData screen, int board_coordinates[]) {
        assert screen != null;
        ArrayList<ArrayList<int[]>> blocks = RGB.find_similar_color_blocks(screen.rgb_array, board_coordinates[0], board_coordinates[1], board_coordinates[2], board_coordinates[3], split_grid_line, 50);
        boolean map[][] = new boolean[board_coordinates[1] - board_coordinates[0] + 1][board_coordinates[3] - board_coordinates[2] + 1];
        for (int i = 0; i < blocks.size(); ++i) {
            for (int j = 0; j < blocks.get(i).size(); ++j) {
                map[blocks.get(i).get(j)[0] - board_coordinates[0]][blocks.get(i).get(j)[1] - board_coordinates[2]] = true;
            }
        }
        int max_side_lengths[][] = find_max_side_lengths(map, 0, board_coordinates[1] - board_coordinates[0] + 1, 0, board_coordinates[3] - board_coordinates[2] + 1);
        ArrayList<Integer> side_lengths = new ArrayList<>();
        for (int i = 0; i < max_side_lengths.length; ++i) {
            for (int j = 0; j < max_side_lengths[0].length; ++j) {
                if (max_side_lengths[i][j] > 0) {
                    side_lengths.add(max_side_lengths[i][j]);
                }
            }
        }
        side_lengths.sort((a, b) -> b - a);
        int side_lengths_sum = 0;
        for (int i = 0; i < number_to_compute_average_grid_size; ++i) {
            side_lengths_sum += side_lengths.get(i);
            System.out.println("Side Length: " + side_lengths.get(i));
        }
        int avg_side_length = side_lengths_sum / number_to_compute_average_grid_size;
        int width_n = (int) Math.round((board_coordinates[1] - board_coordinates[0]) / (double) avg_side_length);
        int height_n = (int) Math.round((board_coordinates[3] - board_coordinates[2]) / (double) avg_side_length);
        return new int[]{width_n, height_n};
    }

    public static int[][][] extract_picture_slice(int[][][] original_array, int row_start, int row_end, int col_start, int col_end) {
        assert original_array != null && original_array.length > 0;
        assert original_array[0] != null && original_array[0].length > 0;
        assert original_array[0][0] != null && 3 == original_array[0][0].length;
        int new_rows = row_end - row_start;
        int new_cols = col_end - col_start;
        int[][][] sub_array = new int[new_rows][new_cols][3];
        for (int i = row_start; i < row_end; i++) {
            System.arraycopy(original_array[i], col_start, sub_array[i - row_start], 0, new_cols);
        }
        return sub_array;
    }

    public static int[] find_grid_size(ScreenData screen, int board_coordinates[]) {
        assert screen != null && MinesweeperState.images != null;
        int least_side_length = Math.min(board_coordinates[1] - board_coordinates[0] + 1, board_coordinates[3] - board_coordinates[2] + 1);
        Set<Integer> side_lengths = new HashSet<>();
        for (int i = 1; i <= least_side_length; ++i) {
            int side_length = (int) Math.round(least_side_length / (double) i);
            if (side_length > 10) {
                side_lengths.add(side_length);
            }
        }
        int optimal_side_length = 1;
        double min_average_distance = Double.MAX_VALUE;
        for (int side_length : side_lengths) {
            int width = (board_coordinates[1] - board_coordinates[0]) / side_length;
            int height = (board_coordinates[3] - board_coordinates[2]) / side_length;
            double distance_sum = 0;
            for (int k = 0; k < width; ++k) {
                for (int l = 0; l < height; ++l) {
                    double min_distance = Double.MAX_VALUE;
                    for (int i = 0; i < MinesweeperState.images.length; ++i) {
                        int slice[][][] = extract_picture_slice(screen.rgb_array, board_coordinates[0] + side_length * k, board_coordinates[0] + side_length * (k + 1), board_coordinates[2] + side_length * l, board_coordinates[2] + side_length * (l + 1));
                        double distance = RGB.picture_average_distance(slice, MinesweeperState.images[i]);
                        if (distance < min_distance) {
                            min_distance = distance;
                        }
                    }
                    distance_sum += min_distance;
                }
            }
            double average_distance = distance_sum / (width * height);
            if (average_distance < min_average_distance) {
                optimal_side_length = side_length;
                min_average_distance = average_distance;
            }
        }
        int width_n = (int) Math.round((board_coordinates[1] - board_coordinates[0]) / (double) optimal_side_length);
        int height_n = (int) Math.round((board_coordinates[3] - board_coordinates[2]) / (double) optimal_side_length);
        return new int[]{width_n, height_n};
    }

    public static char[][] get_map_abandoned(ScreenData screen, int board_coordinates[], int grid_size[]) {
        assert screen != null && MinesweeperState.images != null && grid_size != null;
        assert 4 == board_coordinates.length && 2 == grid_size.length;
        char map[][] = new char[grid_size[0]][grid_size[1]];
        for (int k = 0; k < grid_size[0]; ++k) {
            for (int l = 0; l < grid_size[1]; ++l) {
                double min_distance = Double.MAX_VALUE;
                for (int i = 0; i < MinesweeperState.images.length; ++i) {
                    int grid_coordinates[] = get_grid_coordinates(board_coordinates, grid_size, k, l);
                    int slice[][][] = extract_picture_slice(screen.rgb_array,
                            grid_coordinates[0], grid_coordinates[1], grid_coordinates[2], grid_coordinates[3]);
                    double distance = RGB.picture_average_distance(slice, MinesweeperState.images[i]);
                    if (distance < min_distance) {
                        min_distance = distance;
                        map[k][l] = MinesweeperState.operands[i];
                    }
                }
            }
        }
        return map;
    }

    public static char[][] get_map_abandoned_2(ScreenData screen, int board_coordinates[], int grid_size[]) {
        assert screen != null && MinesweeperState.images != null && grid_size != null;
        assert 4 == board_coordinates.length && 2 == grid_size.length;
        char map[][] = new char[grid_size[0]][grid_size[1]];
        for (int k = 0; k < grid_size[0]; ++k) {
            for (int l = 0; l < grid_size[1]; ++l) {
                double min_distance = Double.MAX_VALUE;
                for (int i = 0; i < MinesweeperState.images.length; ++i) {
                    int grid_coordinates[] = get_grid_coordinates(board_coordinates, grid_size, k, l);
                    int slice[][][] = extract_picture_slice(screen.rgb_array,
                            grid_coordinates[0], grid_coordinates[1], grid_coordinates[2], grid_coordinates[3]);
                    double image_centroid[] = RGB.rgb_image_centroid_circle(slice);
                    double distance = RGB.rgb_distance(image_centroid, MinesweeperState.image_rgb_centroids[i]);
                    if (distance < min_distance) {
                        min_distance = distance;
                        map[k][l] = MinesweeperState.operands[i];
                    }
                }
            }
        }
        return map;
    }

    public static char[][] get_map(ScreenData screen, int board_coordinates[]) {
        assert screen != null && MinesweeperState.images != null;
        assert 4 == board_coordinates.length;
        int board_rgb_array[][][] = extract_picture_slice(screen.rgb_array, board_coordinates[0], board_coordinates[1], board_coordinates[2], board_coordinates[3]);
        HashMap<Character, ArrayList<int[]>> positions = OpenCV.process(board_rgb_array, MinesweeperState.operands, MinesweeperState.images);
        System.out.println("Positions: " + OpenCV.display_positions(positions));
        return null;
    }

    public static int[] get_digits(ScreenData screen, int width_l, int width_r, int height_l, int height_r, int number_of_digits) {
        assert MinesweeperState.digits != null && screen != null;
        int digits[] = new int[3];
        int side_length = (width_r - width_l) / number_of_digits;
        for (int i = 0; i < number_of_digits; ++i) {
            int slice[][][] = extract_picture_slice(screen.rgb_array, width_l + i * side_length, width_l + (i + 1) * side_length, height_l, height_r);
            double min_distance = Double.MAX_VALUE;
            for (int j = 0; j < MinesweeperState.digits.length; ++j) {
                double distance = RGB.picture_average_distance(slice, MinesweeperState.digits[j]);
                if (distance < min_distance) {
                    min_distance = distance;
                    digits[i] = j;
                }
            }
        }
        return digits;
    }

    public static int convert_digits_to_integer(int digits[]) {
        assert digits != null;
        int integer = 0;
        for (int i = 0; i < digits.length; ++i) {
            integer *= 10;
            integer += digits[i];
        }
        return integer;
    }

    public static int[] get_grid_coordinates(int board_coordinates[], int grid_size[], int i, int j) {
        int width_l = (int) Math.round(board_coordinates[0] + i * (board_coordinates[1] - board_coordinates[0]) / (double) grid_size[0]);
        int width_r = (int) Math.round(board_coordinates[0] + (i + 1) * (board_coordinates[1] - board_coordinates[0]) / (double) grid_size[0]);
        int height_l = (int) Math.round(board_coordinates[2] + j * (board_coordinates[3] - board_coordinates[2]) / (double) grid_size[1]);
        int height_r = (int) Math.round(board_coordinates[2] + (j + 1) * (board_coordinates[3] - board_coordinates[2]) / (double) grid_size[1]);
        if (width_r >= board_coordinates[1]) {
            --width_r;
        }
        if (height_r >= board_coordinates[3]) {
            --height_r;
        }
        return new int[]{width_l, width_r, height_l, height_r};
    }

    public MinesweeperState scan_abandoned(ScreenData screen) {
        assert screen != null;
        int panel_coordinates[] = find_panel_coordinates(screen);
        int remaining_mines_and_times_coordinates[] = find_remaining_mines_and_time_coordinates(screen, panel_coordinates);
        int remaining_mines = convert_digits_to_integer(get_digits(screen,
                remaining_mines_and_times_coordinates[0],
                remaining_mines_and_times_coordinates[1],
                remaining_mines_and_times_coordinates[2],
                remaining_mines_and_times_coordinates[3],
                3));
        int time_passed = convert_digits_to_integer(get_digits(screen,
                remaining_mines_and_times_coordinates[4],
                remaining_mines_and_times_coordinates[5],
                remaining_mines_and_times_coordinates[6],
                remaining_mines_and_times_coordinates[7],
                3));
        int board_coordinates[] = find_board_coordinates(screen, panel_coordinates);
        if (null == grid_size) {
            grid_size = find_grid_size(screen, board_coordinates);
            System.out.print("Game Settings: Width: " + grid_size[0] + " Height: " + grid_size[1] + '\n');
        }
        char map[][] = get_map_abandoned(screen, board_coordinates, grid_size);
        return new MinesweeperState(time_passed, remaining_mines, map);
    }

    public static MinesweeperState scan(ScreenData screen) {
        assert screen != null;
        int panel_coordinates[] = find_panel_coordinates(screen);
        int remaining_mines_and_times_coordinates[] = find_remaining_mines_and_time_coordinates(screen, panel_coordinates);
        int remaining_mines = convert_digits_to_integer(get_digits(screen,
                remaining_mines_and_times_coordinates[0],
                remaining_mines_and_times_coordinates[1],
                remaining_mines_and_times_coordinates[2],
                remaining_mines_and_times_coordinates[3],
                3));
        int time_passed = convert_digits_to_integer(get_digits(screen,
                remaining_mines_and_times_coordinates[4],
                remaining_mines_and_times_coordinates[5],
                remaining_mines_and_times_coordinates[6],
                remaining_mines_and_times_coordinates[7],
                3));
        int board_coordinates[] = find_board_coordinates(screen, panel_coordinates);
        char map[][] = get_map(screen, board_coordinates);
        return new MinesweeperState(time_passed, remaining_mines, map);
    }

    public static void main(String[] args) {
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/empty.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/empty_xp.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/process.png");
        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/process_xp.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/final.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("test_images/final_xp.png");
        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
        MinesweeperState state = scan(screen);
        System.out.println(state);
    }
}