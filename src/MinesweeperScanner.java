import java.util.ArrayList;
import java.util.Stack;

public class MinesweeperScanner {
    static final int neighborhood[][] = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};
    public static final int panel_background[] = {189, 189, 189};

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

    public static void dfs_equal_color(int map[][][], boolean past[][], ArrayList<int[]> block, int i, int j, int color[]) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{i, j});
        past[i][j] = true;
        while (!stack.isEmpty()) {
            int top[] = stack.pop();
            i = top[0];
            j = top[1];
            if (rgb_equal(color, map[i][j])) {
                block.add(new int[]{i, j});
                for (int[] vector : neighborhood) {
                    int new_i = i + vector[0];
                    int new_j = j + vector[1];
                    if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length && !past[new_i][new_j]) {
                        stack.push(new int[]{new_i, new_j});
                        past[i][j] = true;
                    }
                }
            }
        }
    }

    public static void dfs_similar_color(int map[][][], boolean past[][], ArrayList<int[]> block, int i, int j, int color[]) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{i, j});
        past[i][j] = true;
        while (!stack.isEmpty()) {
            int top[] = stack.pop();
            i = top[0];
            j = top[1];
            if (rgb_similar(color, map[i][j])) {
                block.add(new int[]{i, j});
                for (int[] vector : neighborhood) {
                    int new_i = i + vector[0];
                    int new_j = j + vector[1];
                    if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length && !past[new_i][new_j]) {
                        stack.push(new int[]{new_i, new_j});
                        past[i][j] = true;
                    }
                }
            }
        }
    }

    public static ArrayList<int[]> find_equal_color_block(int map[][][], boolean past[][], int i, int j, int color[]) {
        ArrayList<int[]> block = new ArrayList<>();
        dfs_equal_color(map, past, block, i, j, color);
        return block.isEmpty() ? null : block;
    }

    public static ArrayList<int[]> find_similar_color_block(int map[][][], boolean past[][], int i, int j, int color[]) {
        ArrayList<int[]> block = new ArrayList<>();
        dfs_similar_color(map, past, block, i, j, color);
        return block.isEmpty() ? null : block;
    }

    public static ArrayList<ArrayList<int[]>> find_equal_color_blocks(int map[][][], int width_l, int width_r, int height_l, int height_r, int color[]) {
        assert color != null && color.length == 3;
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0 && map[0][0] != null && map[0][0].length == 3;
        assert width_l >= 0 && width_r >= 0 && height_l >= 0 && height_r >= 0;
        assert width_l < map.length && width_r <= map.length && height_l < map[0].length && height_r <= map[0].length;
        assert width_r > width_l && height_r > height_l;
        boolean past[][] = new boolean[map.length][map[0].length];
        ArrayList<ArrayList<int[]>> blocks = new ArrayList<>();
        for (int i = width_l; i < width_r; ++i) {
            for (int j = height_l; j < height_r; ++j) {
                ArrayList<int[]> block = find_equal_color_block(map, past, i, j, panel_background);
                if (block != null) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static ArrayList<ArrayList<int[]>> find_similar_color_blocks(int map[][][], int width_l, int width_r, int height_l, int height_r, int color[]) {
        assert color != null && color.length == 3;
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0 && map[0][0] != null && map[0][0].length == 3;
        assert width_l >= 0 && width_r >= 0 && height_l >= 0 && height_r >= 0;
        assert width_l < map.length && width_r <= map.length && height_l < map[0].length && height_r <= map[0].length;
        assert width_r > width_l && height_r > height_l;
        boolean past[][] = new boolean[map.length][map[0].length];
        ArrayList<ArrayList<int[]>> blocks = new ArrayList<>();
        for (int i = width_l; i < width_r; ++i) {
            for (int j = height_l; j < height_r; ++j) {
                ArrayList<int[]> block = find_similar_color_block(map, past, i, j, panel_background);
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

    public static MinesweeperState scan(ScreenData screen) {

        return null;
    }

    public static void main(String[] args) {
        ScreenData screen = ScreenCapture.load_screen_from_file("/Users/caiyufei/Downloads/FireShot/踩地雷/高級1.png");
//        for (int i = 0; i < screen.width; ++i) {
//            for (int j = 0; j < screen.height; ++j) {
//                if (rgb_similar(panel_background, screen.rgb_array[i][j])) {
//                    screen.rgb_array[i][j] = new int[]{166, 0, 255};
//                }
//            }
//        }
//        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
        int coordinates[] = find_panel_coordinates(screen);
        for (int i = coordinates[0]; i < coordinates[1]; ++i) {
            for (int j = coordinates[2]; j < coordinates[3]; ++j) {
                screen.rgb_array[i][j] = new int[]{166, 0, 255};
            }
        }
        ScreenCapture.save_screen_to_file(screen, "screen.png", "png");
    }
}