import java.util.ArrayList;
import java.util.Stack;

public class MinesweeperScanner {
    static final int neighborhood[][] = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    public static final int panel_background[][] = {{189, 189, 189}};
    public static final int digits_boards_background[][] = {{0, 0, 0}};
    public static final int board_border[][] = {{131, 131, 131}, {255, 255, 255}};

    public static boolean rgb_equal(int rgb_1[], int rgb_2[]) {
        return rgb_1[0] == rgb_2[0] && rgb_1[1] == rgb_2[1] && rgb_1[2] == rgb_2[2];
    }

    public static boolean rgb_similar(int rgb_1[], int rgb_2[]) {
        int R = rgb_1[0] - rgb_2[0];
        int G = rgb_1[1] - rgb_2[1];
        int B = rgb_1[2] - rgb_2[2];
        double rmean = (rgb_1[0] + rgb_2[0]) / 2.0;
        double distance = Math.sqrt((2 + rmean / 256) * (R * R) + 4 * (G * G) + (2 + (255 - rmean) / 256) * (B * B));
        return distance < 50.0;
    }

    public static boolean rgb_equal_to_any(int rgb[], int colors[][]) {
        for (int i = 0; i < colors.length; ++i) {
            if (rgb_equal(rgb, colors[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean rgb_similar_to_any(int rgb[], int colors[][]) {
        for (int i = 0; i < colors.length; ++i) {
            if (rgb_similar(rgb, colors[i])) {
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

    public static void dfs_similar_color(int map[][][], boolean past[][], ArrayList<int[]> block, int i, int j, int width_l, int width_r, int height_l, int height_r, int colors[][]) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{i, j});
        past[i][j] = true;
        while (!stack.isEmpty()) {
            int top[] = stack.pop();
            i = top[0];
            j = top[1];
            if (rgb_similar_to_any(map[i][j], colors)) {
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

    public static ArrayList<int[]> find_similar_colors_block(int map[][][], boolean past[][], int i, int j, int width_l, int width_r, int height_l, int height_r, int colors[][]) {
        ArrayList<int[]> block = new ArrayList<>();
        dfs_similar_color(map, past, block, i, j, width_l, width_r, height_l, height_r, colors);
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

    public static ArrayList<ArrayList<int[]>> find_similar_color_blocks(int map[][][], int width_l, int width_r, int height_l, int height_r, int colors[][]) {
        assert colors != null && colors[0] != null && colors[0].length == 3;
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0 && map[0][0] != null && map[0][0].length == 3;
        assert width_l >= 0 && width_r >= 0 && height_l >= 0 && height_r >= 0;
        assert width_l < map.length && width_r <= map.length && height_l < map[0].length && height_r <= map[0].length;
        assert width_r > width_l && height_r > height_l;
        boolean past[][] = new boolean[map.length][map[0].length];
        ArrayList<ArrayList<int[]>> blocks = new ArrayList<>();
        for (int i = width_l; i < width_r; ++i) {
            for (int j = height_l; j < height_r; ++j) {
                ArrayList<int[]> block = find_similar_colors_block(map, past, i, j, width_l, width_r, height_l, height_r, colors);
                if (block != null) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static int[] find_panel_coordinates(ScreenData screen) {
        assert screen != null;
        ArrayList<ArrayList<int[]>> blocks = find_similar_color_blocks(screen.rgb_array, 0, screen.width, 0, screen.height, panel_background);
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
        ArrayList<ArrayList<int[]>> blocks = find_similar_color_blocks(screen.rgb_array, panel_coordinates[0], panel_coordinates[1], panel_coordinates[2], panel_coordinates[3], digits_boards_background);
        int max_area_1 = 0, max_area_2 = 0;
        int width_l_1 = panel_coordinates[0], width_r_1 = panel_coordinates[1], height_l_1 = panel_coordinates[2], height_r_1 = panel_coordinates[3];
        int width_l_2 = panel_coordinates[0], width_r_2 = panel_coordinates[1], height_l_2 = panel_coordinates[2], height_r_2 = panel_coordinates[3];
//        ArrayList<Integer> block_areas = new ArrayList<>();
//        ArrayList<Integer> w_ls = new ArrayList<>();
//        ArrayList<Integer> w_rs = new ArrayList<>();
//        ArrayList<Integer> h_ls = new ArrayList<>();
//        ArrayList<Integer> h_rs = new ArrayList<>();
        for (int i = 0; i < blocks.size(); ++i) {
            int w_l = Integer.MAX_VALUE, w_r = 0, h_l = Integer.MAX_VALUE, h_r = 0;
            for (int j = 0; j < blocks.get(i).size(); ++j) {
                w_l = Math.min(w_l, blocks.get(i).get(j)[0]);
                w_r = Math.max(w_r, blocks.get(i).get(j)[0] + 1);
                h_l = Math.min(h_l, blocks.get(i).get(j)[1]);
                h_r = Math.max(h_r, blocks.get(i).get(j)[1] + 1);
            }
            int block_area = (w_r - w_l) * (h_r - h_l);
//            block_areas.add(block_area);
//            w_ls.add(w_l);
//            w_rs.add(w_r);
//            h_ls.add(h_l);
//            h_rs.add(h_r);
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
//        block_areas.sort((a, b) -> b - a);
//        for (int i = 1; i < block_areas.size(); ++i) {
//            System.out.println(block_areas.get(i));
//            if (block_areas.get(i).equals(block_areas.get(i - 1))) {
//                return new int[]{w_ls.get(i - 1), w_rs.get(i - 1), h_ls.get(i - 1), h_rs.get(i - 1), w_ls.get(i), w_rs.get(i), h_ls.get(i), h_rs.get(i)};
//            }
//        }
        return new int[]{width_l_1, width_r_1, height_l_1, height_r_1, width_l_2, width_r_2, height_l_2, height_r_2};
//        return null;
    }

    public static int[] find_board_coordinates(ScreenData screen, int panel_coordinates[]) {
        assert screen != null;
        ArrayList<ArrayList<int[]>> blocks = find_similar_color_blocks(screen.rgb_array, panel_coordinates[0], panel_coordinates[1], panel_coordinates[2], panel_coordinates[3], board_border);
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
        return new int[]{width_l, width_r, height_l, height_r};
    }

    public static MinesweeperState scan(ScreenData screen) {
        int panel_coordinates[] = find_panel_coordinates(screen);
//        for (int i = panel_coordinates[0]; i < panel_coordinates[1]; ++i) {
//            for (int j = panel_coordinates[2]; j < panel_coordinates[3]; ++j) {
//                screen.rgb_array[i][j] = new int[]{166, 0, 255};
//            }
//        }
        int remaining_mines_and_times_coordinates[] = find_remaining_mines_and_times_coordinates(screen, panel_coordinates);
//        assert remaining_mines_and_times_coordinates != null;
//        for (int i : remaining_mines_and_times_coordinates) {
//            System.out.println(i);
//        }
//        for (int i = remaining_mines_and_times_coordinates[0]; i < remaining_mines_and_times_coordinates[1]; ++i) {
//            for (int j = remaining_mines_and_times_coordinates[2]; j < remaining_mines_and_times_coordinates[3]; ++j) {
//                screen.rgb_array[i][j] = new int[]{166, 0, 255};
//            }
//        }
//        for (int i = remaining_mines_and_times_coordinates[4]; i < remaining_mines_and_times_coordinates[5]; ++i) {
//            for (int j = remaining_mines_and_times_coordinates[6]; j < remaining_mines_and_times_coordinates[7]; ++j) {
//                screen.rgb_array[i][j] = new int[]{166, 0, 255};
//            }
//        }
        int board_coordinates[] = find_board_coordinates(screen, panel_coordinates);
        for (int i = board_coordinates[0]; i < board_coordinates[1]; ++i) {
            for (int j = board_coordinates[2]; j < board_coordinates[3]; ++j) {
                screen.rgb_array[i][j] = new int[]{166, 0, 255};
            }
        }
        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
        return null;
    }

    public static void main(String[] args) {
        ScreenData screen = ScreenCapture.load_screen_from_file("/Users/caiyufei/Downloads/FireShot/踩地雷/進階1.png");
//        for (int i = 0; i < screen.width; ++i) {
//            for (int j = 0; j < screen.height; ++j) {
//                if (rgb_similar(panel_background, screen.rgb_array[i][j])) {
//                    screen.rgb_array[i][j] = new int[]{166, 0, 255};
//                }
//            }
//        }
//        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
        MinesweeperState state = scan(screen);
    }
}