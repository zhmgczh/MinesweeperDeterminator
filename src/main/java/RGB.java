import java.util.ArrayList;
import java.util.Stack;

public class RGB {
    public static final double CIRCLE_RATIO = 1;
    private static final int[][] unit_vectors = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

    public static double distance(double x_1, double y_1, double x_2, double y_2) {
        double diff_x = x_1 - x_2;
        double diff_y = y_1 - y_2;
        return Math.sqrt(diff_x * diff_x + diff_y * diff_y);
    }

    public static boolean rgb_equal(int[] rgb_1, int[] rgb_2) {
        return rgb_1[0] == rgb_2[0] && rgb_1[1] == rgb_2[1] && rgb_1[2] == rgb_2[2];
    }

    public static double rgb_distance(int[] rgb_1, int[] rgb_2) {
        int R = rgb_1[0] - rgb_2[0];
        int G = rgb_1[1] - rgb_2[1];
        int B = rgb_1[2] - rgb_2[2];
        double rmean = (rgb_1[0] + rgb_2[0]) / 2.0;
        return Math.sqrt((2 + rmean / 256) * (R * R) + 4 * (G * G) + (2 + (255 - rmean) / 256) * (B * B));
    }

    public static double rgb_distance(double[] rgb_1, double[] rgb_2) {
        double R = rgb_1[0] - rgb_2[0];
        double G = rgb_1[1] - rgb_2[1];
        double B = rgb_1[2] - rgb_2[2];
        double rmean = (rgb_1[0] + rgb_2[0]) / 2.0;
        return Math.sqrt((2 + rmean / 256) * (R * R) + 4 * (G * G) + (2 + (255 - rmean) / 256) * (B * B));
    }

    public static double rgb_distance_direct(int[] rgb_1, int[] rgb_2) {
        int diff_r = rgb_1[0] - rgb_2[0];
        int diff_g = rgb_1[1] - rgb_2[1];
        int diff_b = rgb_1[2] - rgb_2[2];
        return Math.sqrt(diff_r * diff_r + diff_g * diff_g + diff_b * diff_b);
    }

    public static double rgb_distance_direct(double[] rgb_1, double[] rgb_2) {
        double diff_r = rgb_1[0] - rgb_2[0];
        double diff_g = rgb_1[1] - rgb_2[1];
        double diff_b = rgb_1[2] - rgb_2[2];
        return Math.sqrt(diff_r * diff_r + diff_g * diff_g + diff_b * diff_b);
    }

    public static double[] rgb_image_centroid(int[][][] image) {
        double[] rgb_mean = new double[3];
        for (int[][] ints : image) {
            for (int l = 0; l < image[0].length; ++l) {
                rgb_mean[0] += ints[l][0];
                rgb_mean[1] += ints[l][1];
                rgb_mean[2] += ints[l][2];
            }
        }
        int size = image.length * image[0].length;
        rgb_mean[0] /= size;
        rgb_mean[1] /= size;
        rgb_mean[2] /= size;
        return rgb_mean;
    }

    public static double[] rgb_image_centroid_circle(int[][][] image) {
        double[] rgb_mean = new double[3];
        double center_x = image.length / 2.0;
        double center_y = image[0].length / 2.0;
        double radius = Math.sqrt(image.length * image[0].length) / 2.0 * CIRCLE_RATIO;
        ArrayList<Pair<Integer, Integer>> include_pixels = new ArrayList<>();
        for (int i = 0; i < image.length; ++i) {
            for (int j = 0; j < image[0].length; ++j) {
                if (distance(center_x, center_y, i, j) <= radius) {
                    include_pixels.add(new Pair<>(i, j));
                }
            }
        }
        for (Pair<Integer, Integer> p : include_pixels) {
            rgb_mean[0] += image[p.getFirst()][p.getSecond()][0];
            rgb_mean[1] += image[p.getFirst()][p.getSecond()][1];
            rgb_mean[2] += image[p.getFirst()][p.getSecond()][2];
        }
        rgb_mean[0] /= include_pixels.size();
        rgb_mean[1] /= include_pixels.size();
        rgb_mean[2] /= include_pixels.size();
        return rgb_mean;
    }

    public static boolean rgb_similar(int[] rgb_1, int[] rgb_2, double threshold) {
        return rgb_distance(rgb_1, rgb_2) <= threshold;
    }

    public static int[] get_corresponding_position(int i, int j, int width, int height, int new_width, int new_height) {
        assert height > 0 && width > 0 && new_height > 0 && new_width > 0;
        int new_i = (int) Math.round(i * new_width / (double) width);
        if (new_i == new_width) {
            --new_i;
        }
        int new_j = (int) Math.round(j * new_height / (double) height);
        if (new_j == new_height) {
            --new_j;
        }
        return new int[]{new_i, new_j};
    }

    public static double picture_average_distance_sorted(int[][][] big_picture, int[][][] small_picture) {
        assert big_picture != null && small_picture != null && big_picture.length > 0 && small_picture.length > 0;
        assert big_picture[0].length > 0 && small_picture[0].length > 0;
        assert 3 == big_picture[0][0].length && 3 == small_picture[0][0].length;
        assert big_picture.length * big_picture[0].length >= small_picture.length * small_picture[0].length;
        double sum = 0;
        for (int i = 0; i < big_picture.length; ++i) {
            for (int j = 0; j < big_picture[0].length; ++j) {
                int[] corresponding_position = get_corresponding_position(i, j, big_picture.length, big_picture[0].length, small_picture.length, small_picture[0].length);
                sum += rgb_distance(big_picture[i][j], small_picture[corresponding_position[0]][corresponding_position[1]]);
            }
        }
        return sum / (big_picture.length * big_picture[0].length);
    }

    public static double picture_average_distance_sorted_circle(int[][][] big_picture, int[][][] small_picture) {
        assert big_picture != null && small_picture != null && big_picture.length > 0 && small_picture.length > 0;
        assert big_picture[0].length > 0 && small_picture[0].length > 0;
        assert 3 == big_picture[0][0].length && 3 == small_picture[0][0].length;
        assert big_picture.length * big_picture[0].length >= small_picture.length * small_picture[0].length;
        double sum = 0;
        double center_x = big_picture.length / 2.0;
        double center_y = big_picture[0].length / 2.0;
        double radius = Math.sqrt(big_picture.length * big_picture[0].length) / 2.0;
        ArrayList<Pair<Integer, Integer>> include_pixels = new ArrayList<>();
        for (int i = 0; i < big_picture.length; ++i) {
            for (int j = 0; j < big_picture[0].length; ++j) {
                if (distance(center_x, center_y, i, j) <= radius) {
                    include_pixels.add(new Pair<>(i, j));
                }
            }
        }
        for (Pair<Integer, Integer> p : include_pixels) {
            int i = p.getFirst();
            int j = p.getSecond();
            int[] corresponding_position = get_corresponding_position(i, j, big_picture.length, big_picture[0].length, small_picture.length, small_picture[0].length);
            sum += rgb_distance(big_picture[i][j], small_picture[corresponding_position[0]][corresponding_position[1]]);
        }
        return sum / include_pixels.size();
    }

    public static double picture_average_distance(int[][][] picture_1, int[][][] picture_2) {
        assert picture_1 != null && picture_2 != null && picture_1.length > 0 && picture_2.length > 0;
        assert picture_1[0].length > 0 && picture_2[0].length > 0;
        assert 3 == picture_1[0][0].length && 3 == picture_2[0][0].length;
        if (picture_1.length * picture_1[0].length >= picture_2.length * picture_2[0].length) {
            return picture_average_distance_sorted(picture_1, picture_2);
        } else {
            return picture_average_distance_sorted(picture_2, picture_1);
        }
    }

    public static double picture_average_distance_circle(int[][][] picture_1, int[][][] picture_2) {
        assert picture_1 != null && picture_2 != null && picture_1.length > 0 && picture_2.length > 0;
        assert picture_1[0].length > 0 && picture_2[0].length > 0;
        assert 3 == picture_1[0][0].length && 3 == picture_2[0][0].length;
        if (picture_1.length * picture_1[0].length >= picture_2.length * picture_2[0].length) {
            return picture_average_distance_sorted_circle(picture_1, picture_2);
        } else {
            return picture_average_distance_sorted_circle(picture_2, picture_1);
        }
    }

    public static boolean rgb_equal_to_any(int[] rgb, int[][] colors) {
        for (int[] color : colors) {
            if (rgb_equal(rgb, color)) {
                return true;
            }
        }
        return false;
    }

    public static boolean rgb_similar_to_any(int[] rgb, int[][] colors, double threshold) {
        for (int[] color : colors) {
            if (rgb_similar(rgb, color, threshold)) {
                return true;
            }
        }
        return false;
    }

    private static int[] stack_x;
    private static int[] stack_y;

    private static void initialize_stack(int area) {
        if (null == stack_x) {
            stack_x = new int[area];
            stack_y = new int[area];
        } else if (stack_x.length < area) {
            int length = stack_x.length;
            while (length < area) {
                length <<= 1;
            }
            stack_x = new int[length];
            stack_y = new int[length];
        }
    }

    public static void dfs_equal_color(int[][][] map, boolean[][] past, ArrayList<int[]> block, int i, int j, int width_l, int width_r, int height_l, int height_r, int[][] colors) {
        initialize_stack((width_r - width_l) * (height_r - height_l));
        int stack_pointer = 0;
        stack_x[stack_pointer] = i;
        stack_y[stack_pointer] = j;
        past[i][j] = true;
        ++stack_pointer;
        while (stack_pointer != 0) {
            --stack_pointer;
            i = stack_x[stack_pointer];
            j = stack_y[stack_pointer];
            if (rgb_equal_to_any(map[i][j], colors)) {
                block.add(new int[]{i, j});
                for (int[] vector : unit_vectors) {
                    int new_i = i + vector[0];
                    int new_j = j + vector[1];
                    if (new_i >= width_l && new_i < width_r && new_j >= height_l && new_j < height_r && !past[new_i][new_j]) {
                        stack_x[stack_pointer] = new_i;
                        stack_y[stack_pointer] = new_j;
                        past[new_i][new_j] = true;
                        ++stack_pointer;
                    }
                }
            }
        }
    }

    public static void dfs_similar_color(int[][][] map, boolean[][] past, ArrayList<int[]> block, int i, int j, int width_l, int width_r, int height_l, int height_r, int[][] colors, double threshold) {
        initialize_stack((width_r - width_l) * (height_r - height_l));
        int stack_pointer = 0;
        stack_x[stack_pointer] = i;
        stack_y[stack_pointer] = j;
        past[i][j] = true;
        ++stack_pointer;
        while (stack_pointer != 0) {
            --stack_pointer;
            i = stack_x[stack_pointer];
            j = stack_y[stack_pointer];
            if (rgb_similar_to_any(map[i][j], colors, threshold)) {
                block.add(new int[]{i, j});
                for (int[] vector : unit_vectors) {
                    int new_i = i + vector[0];
                    int new_j = j + vector[1];
                    if (new_i >= width_l && new_i < width_r && new_j >= height_l && new_j < height_r && !past[new_i][new_j]) {
                        stack_x[stack_pointer] = new_i;
                        stack_y[stack_pointer] = new_j;
                        past[new_i][new_j] = true;
                        ++stack_pointer;
                    }
                }
            }
        }
    }

    public static ArrayList<int[]> find_equal_colors_block(int[][][] map, boolean[][] past, int i, int j, int width_l, int width_r, int height_l, int height_r, int[][] colors) {
        ArrayList<int[]> block = new ArrayList<>();
        dfs_equal_color(map, past, block, i, j, width_l, width_r, height_l, height_r, colors);
        return block.isEmpty() ? null : block;
    }

    public static ArrayList<int[]> find_similar_colors_block(int[][][] map, boolean[][] past, int i, int j, int width_l, int width_r, int height_l, int height_r, int[][] colors, double threshold) {
        ArrayList<int[]> block = new ArrayList<>();
        dfs_similar_color(map, past, block, i, j, width_l, width_r, height_l, height_r, colors, threshold);
        return block.isEmpty() ? null : block;
    }

    public static ArrayList<ArrayList<int[]>> find_equal_color_blocks(int[][][] map, int width_l, int width_r, int height_l, int height_r, int[][] colors) {
        assert colors != null && colors[0] != null && colors[0].length == 3;
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0 && map[0][0] != null && map[0][0].length == 3;
        assert width_l >= 0 && width_r >= 0 && height_l >= 0 && height_r >= 0;
        assert width_l < map.length && width_r <= map.length && height_l < map[0].length && height_r <= map[0].length;
        assert width_r > width_l && height_r > height_l;
        boolean[][] past = new boolean[map.length][map[0].length];
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

    public static ArrayList<ArrayList<int[]>> find_similar_color_blocks(int[][][] map, int width_l, int width_r, int height_l, int height_r, int[][] colors, double threshold) {
        assert colors != null && colors[0] != null && colors[0].length == 3;
        assert map != null && map.length > 0 && map[0] != null && map[0].length > 0 && map[0][0] != null && map[0][0].length == 3;
        assert width_l >= 0 && width_r >= 0 && height_l >= 0 && height_r >= 0;
        assert width_l < map.length && width_r <= map.length && height_l < map[0].length && height_r <= map[0].length;
        assert width_r > width_l && height_r > height_l;
        boolean[][] past = new boolean[map.length][map[0].length];
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

    public static int[][][] resize(int[][][] original, int newW, int newH) {
        int oldW = original.length;
        int oldH = original[0].length;
        int[][][] resized = new int[newW][newH][3];
        double widthRatio = (double) (oldW - 1) / (newW - 1);
        double heightRatio = (double) (oldH - 1) / (newH - 1);
        for (int i = 0; i < newW; i++) {
            for (int j = 0; j < newH; j++) {
                double x = i * widthRatio;
                double y = j * heightRatio;
                int x1 = (int) Math.floor(x);
                int y1 = (int) Math.floor(y);
                int x2 = Math.min(x1 + 1, oldW - 1);
                int y2 = Math.min(y1 + 1, oldH - 1);
                double dx = x - x1;
                double dy = y - y1;
                for (int c = 0; c < 3; c++) {
                    double val = original[x1][y1][c] * (1 - dx) * (1 - dy) + original[x2][y1][c] * dx * (1 - dy) + original[x1][y2][c] * (1 - dx) * dy + original[x2][y2][c] * dx * dy;
                    resized[i][j][c] = (int) Math.round(val);
                }
            }
        }
        return resized;
    }
}