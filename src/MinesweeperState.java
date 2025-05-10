import java.util.ArrayList;

public class MinesweeperState {
    public static final char UNKNOWN = '*';
    public static final char MINE_FLAG = '.';
    public static final char BLANK = '0';
    public static final char ONE = '1';
    public static final char TWO = '2';
    public static final char THREE = '3';
    public static final char FOUR = '4';
    public static final char FIVE = '5';
    public static final char SIX = '6';
    public static final char SEVEN = '7';
    public static final char EIGHT = '8';
    static final char oprerands[] = {UNKNOWN, BLANK, MINE_FLAG, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT};
    static final int neighborhood[][] = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 0}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    public int time_passed, remaining_mines, nrows, ncols;
    public char map[][];

    public boolean is_valid_operand(char c) {
        for (char operand : oprerands) {
            if (c == operand) {
                return true;
            }
        }
        return false;
    }

    public boolean is_number(char c) {
        return c >= ONE && c <= EIGHT;
    }

    public int to_number(char c) {
        assert BLANK == c || is_number(c);
        return c - '0';
    }

    public boolean check_map_valid(char map[][]) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (!is_valid_operand(map[i][j])) {
                    return false;
                }
                if (is_number(map[i][j])) {
                    int mines = 0;
                    for (int k = 0; k < neighborhood.length; k++) {
                        int new_i = i + neighborhood[k][0];
                        int new_j = j + neighborhood[k][1];
                        if (new_i >= 0 && new_i < map.length && new_j >= 0 && new_j < map[0].length && MINE_FLAG == map[new_i][new_j]) {
                            ++mines;
                        }
                    }
                    if (mines > to_number(map[i][j])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public MinesweeperState(int time_passed, int remaining_mines, char map[][]) {
        assert time_passed >= 0 && time_passed <= 999 && remaining_mines >= 0;
        assert null != map && map.length > 0 && null != map[0] && map[0].length > 0;
        assert check_map_valid(map);
        this.time_passed = time_passed;
        this.remaining_mines = remaining_mines;
        this.map = map;
        this.nrows = map.length;
        this.ncols = map[0].length;
    }

    public void move(char operand, int i, int j) {
        assert i >= 0 && i < nrows && j >= 0 && j < ncols;
        assert is_valid_operand(operand);
        map[i][j] = operand;
    }

    public void change_map(char map[][]) {
        assert null != map && map.length == nrows && null != map[0] && map[0].length == ncols;
        assert check_map_valid(map);
        this.map = map;
    }
}