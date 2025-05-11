import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class MinesweeperScanner {
    static final int neighborhood[][] = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    public static final int panel_background[][] = {{189, 189, 189}};
    public static final int digits_boards_background[][] = {{0, 0, 0}};
    public static final int digits_color[][] = {{255, 0, 0}};
    public static final int board_border[][] = {{131, 131, 131}, {255, 255, 255}};
    public static final int split_grid_line[][] = {{130, 130, 130}};
    public static final int number_to_compute_average_grid_size = 10;

    public static boolean rgb_equal(int rgb_1[], int rgb_2[]) {
        return rgb_1[0] == rgb_2[0] && rgb_1[1] == rgb_2[1] && rgb_1[2] == rgb_2[2];
    }

    public static boolean rgb_similar(int rgb_1[], int rgb_2[], double threshold) {
        int R = rgb_1[0] - rgb_2[0];
        int G = rgb_1[1] - rgb_2[1];
        int B = rgb_1[2] - rgb_2[2];
        double rmean = (rgb_1[0] + rgb_2[0]) / 2.0;
        double distance = Math.sqrt((2 + rmean / 256) * (R * R) + 4 * (G * G) + (2 + (255 - rmean) / 256) * (B * B));
        return distance <= threshold;
    }

    public static boolean rgb_equal_to_any(int rgb[], int colors[][]) {
        for (int i = 0; i < colors.length; ++i) {
            if (rgb_equal(rgb, colors[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean rgb_similar_to_any(int rgb[], int colors[][], double threshold) {
        for (int i = 0; i < colors.length; ++i) {
            if (rgb_similar(rgb, colors[i], threshold)) {
                return true;
            }
        }
        return false;
    }

    public static void dfs_equal_color(int map[][][], boolean past[][], ArrayList<int[]> block, int i, int j, int width_l, int width_r, int height_l, int height_r, int colors[][]) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{i, j});
        past[i][j] = true;
        while (!stack.isEmpty()) {
            int top[] = stack.pop();
            i = top[0];
            j = top[1];
            if (rgb_equal_to_any(map[i][j], colors)) {
                block.add(new int[]{i, j});
                for (int[] vector : neighborhood) {
                    int new_i = i + vector[0];
                    int new_j = j + vector[1];
                    if (new_i >= width_l && new_i < width_r && new_j >= height_l && new_j < height_r && !past[new_i][new_j]) {
                        stack.push(new int[]{new_i, new_j});
                        past[i][j] = true;
                    }
                }
            }
        }
    }

    public static void dfs_similar_color(int map[][][], boolean past[][], ArrayList<int[]> block, int i, int j, int width_l, int width_r, int height_l, int height_r, int colors[][], double threshold) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{i, j});
        past[i][j] = true;
        while (!stack.isEmpty()) {
            int top[] = stack.pop();
            i = top[0];
            j = top[1];
            if (rgb_similar_to_any(map[i][j], colors, threshold)) {
                block.add(new int[]{i, j});
                for (int[] vector : neighborhood) {
                    int new_i = i + vector[0];
                    int new_j = j + vector[1];
                    if (new_i >= width_l && new_i < width_r && new_j >= height_l && new_j < height_r && !past[new_i][new_j]) {
                        stack.push(new int[]{new_i, new_j});
                        past[i][j] = true;
                    }
                }
            }
        }
    }

    public static ArrayList<int[]> find_equal_colors_block(int map[][][], boolean past[][], int i, int j, int width_l, int width_r, int height_l, int height_r, int colors[][]) {
        ArrayList<int[]> block = new ArrayList<>();
        dfs_equal_color(map, past, block, i, j, width_l, width_r, height_l, height_r, colors);
        return block.isEmpty() ? null : block;
    }

    public static ArrayList<int[]> find_similar_colors_block(int map[][][], boolean past[][], int i, int j, int width_l, int width_r, int height_l, int height_r, int colors[][], double threshold) {
        ArrayList<int[]> block = new ArrayList<>();
        dfs_similar_color(map, past, block, i, j, width_l, width_r, height_l, height_r, colors, threshold);
        return block.isEmpty() ? null : block;
    }

    public static ArrayList<ArrayList<int[]>> find_equal_color_blocks(int map[][][], int width_l, int width_r, int height_l, int height_r, int colors[][]) {
        assert colors != null && colors[0] != null && colors[0].length == 3;
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0 && map[0][0] != null && map[0][0].length == 3;
        assert width_l >= 0 && width_r >= 0 && height_l >= 0 && height_r >= 0;
        assert width_l < map.length && width_r <= map.length && height_l < map[0].length && height_r <= map[0].length;
        assert width_r > width_l && height_r > height_l;
        boolean past[][] = new boolean[map.length][map[0].length];
        ArrayList<ArrayList<int[]>> blocks = new ArrayList<>();
        for (int i = width_l; i < width_r; ++i) {
            for (int j = height_l; j < height_r; ++j) {
                ArrayList<int[]> block = find_equal_colors_block(map, past, i, j, width_l, width_r, height_l, height_r, colors);
                if (block != null) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static ArrayList<ArrayList<int[]>> find_similar_color_blocks(int map[][][], int width_l, int width_r, int height_l, int height_r, int colors[][], double threshold) {
        assert colors != null && colors[0] != null && colors[0].length == 3;
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0 && map[0][0] != null && map[0][0].length == 3;
        assert width_l >= 0 && width_r >= 0 && height_l >= 0 && height_r >= 0;
        assert width_l < map.length && width_r <= map.length && height_l < map[0].length && height_r <= map[0].length;
        assert width_r > width_l && height_r > height_l;
        boolean past[][] = new boolean[map.length][map[0].length];
        ArrayList<ArrayList<int[]>> blocks = new ArrayList<>();
        for (int i = width_l; i < width_r; ++i) {
            for (int j = height_l; j < height_r; ++j) {
                ArrayList<int[]> block = find_similar_colors_block(map, past, i, j, width_l, width_r, height_l, height_r, colors, threshold);
                if (block != null) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static int[] find_panel_coordinates(ScreenData screen) {
        assert screen != null;
        ArrayList<ArrayList<int[]>> blocks = find_similar_color_blocks(screen.rgb_array, 0, screen.width, 0, screen.height, panel_background, 50);
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

    public static int[] find_remaining_mines_and_times_coordinates(ScreenData screen, int panel_coordinates[]) {
        assert screen != null && panel_coordinates != null && panel_coordinates.length == 4;
        assert panel_coordinates[0] < panel_coordinates[1] && panel_coordinates[2] < panel_coordinates[3];
        ArrayList<ArrayList<int[]>> blocks = find_similar_color_blocks(screen.rgb_array, panel_coordinates[0], panel_coordinates[1], panel_coordinates[2], panel_coordinates[3], digits_boards_background, 50);
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
        ArrayList<ArrayList<int[]>> blocks = find_similar_color_blocks(screen.rgb_array, panel_coordinates[0], panel_coordinates[1], panel_coordinates[2], panel_coordinates[3], board_border, 50);
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
                if (rgb_similar(screen.rgb_array[width_r - i][height_r - j], screen.rgb_array[width_r-1][height_r-1],50)) {
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
        ArrayList<ArrayList<int[]>> blocks = find_similar_color_blocks(screen.rgb_array, board_coordinates[0], board_coordinates[1], board_coordinates[2], board_coordinates[3], split_grid_line, 50);
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

    public static MinesweeperState scan(ScreenData screen) {
        int panel_coordinates[] = find_panel_coordinates(screen);
        int remaining_mines_and_times_coordinates[] = find_remaining_mines_and_times_coordinates(screen, panel_coordinates);
        int board_coordinates[] = find_board_coordinates(screen, panel_coordinates);
        int grid_size[] = find_grid_size_abandoned(screen, board_coordinates);
        System.out.print("Game Settings: Width: " + grid_size[0] + " Height: " + grid_size[1] + '\n');
        for (int i = 0; i < grid_size[0]; ++i) {
            for (int j = 0; j < grid_size[1]; ++j) {
                if ((i + j) % 2 == 1) {
                    int grid_coordinates[] = get_grid_coordinates(board_coordinates, grid_size, i, j);
                    for (int k = grid_coordinates[0]; k <= grid_coordinates[1]; ++k) {
                        for (int l = grid_coordinates[2]; l <= grid_coordinates[3]; ++l) {
                            screen.rgb_array[k][l] = new int[]{166, 0, 255};
                        }
                    }
                }
            }
        }
        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
        return null;
    }

    public static void main(String[] args) {
        ScreenData screen = ScreenCapture.load_screen_from_file("./test_images/empty.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("./test_images/empty_xp.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("./test_images/process.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("./test_images/process_xp.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("./test_images/final.png");
//        ScreenData screen = ScreenCapture.load_screen_from_file("./test_images/final_xp.png");
        MinesweeperState state = scan(screen);
    }
}