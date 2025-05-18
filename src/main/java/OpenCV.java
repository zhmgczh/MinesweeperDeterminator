import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_core;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;

public class OpenCV {
    public static String display_positions(HashMap<Character, ArrayList<int[]>> results) {
        StringBuilder sb = new StringBuilder();
        for (Character operand : results.keySet()) {
            ArrayList<int[]> positions = results.get(operand);
            sb.append(operand).append(": ");
            for (int[] position : positions) {
                sb.append('(').append(position[0]).append(",").append(position[1]).append(") ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static HashMap<Character, ArrayList<int[]>> process(int[][][] board, char operands[], int[][][][] items) {
        HashMap<Character, ArrayList<int[]>> results = new HashMap<>();
        Mat boardMat = arrayToMat(board);
        Size cellSize = detectCellSize(boardMat);
        int cellWidth = cellSize.width();
        int cellHeight = cellSize.height();
        for (int itemId = 0; itemId < items.length; itemId++) {
            Mat template = arrayToMat(items[itemId]);
            ArrayList<Point> positions_points = matchTemplateMultiScale(boardMat, template, cellWidth, cellHeight);
            ArrayList<int[]> positions = new ArrayList<>();
            for (Point position_point : positions_points) {
                positions.add(new int[]{position_point.x(), position_point.y()});
            }
            results.put(operands[itemId], positions);
        }
        return results;
    }

    private static Mat arrayToMat(int[][][] arr) {
        int width = arr.length;
        int height = arr[0].length;
        Mat mat = new Mat(height, width, CV_8UC3);
        BytePointer ptr = new BytePointer(mat.data());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = (arr[x][y][0] >> 16) & 0xFF;
                int g = (arr[x][y][1] >> 8) & 0xFF;
                int b = arr[x][y][2] & 0xFF;
                ptr.position(y * mat.step() + x * 3)
                        .put((byte) b, (byte) g, (byte) r);
            }
        }
        return mat;
    }

    private static Size detectCellSize(Mat boardMat) {
        Mat gray = new Mat();
        Mat edges = new Mat();
        Mat binary = new Mat();
        opencv_imgproc.cvtColor(boardMat, gray, COLOR_BGR2GRAY);
        opencv_imgproc.GaussianBlur(gray, gray, new Size(3, 3), 0);
        opencv_imgproc.Canny(gray, edges, 50, 150);
        opencv_imgproc.threshold(edges, binary, 128, 255, THRESH_BINARY);

        // 水平投影分析
        Mat horizontal = new Mat();
        opencv_core.reduce(binary, horizontal, 1, REDUCE_AVG);
        ArrayList<Integer> hGaps = analyzeProjection(horizontal, true);

        // 垂直投影分析
        Mat vertical = new Mat();
        opencv_core.reduce(binary, vertical, 0, REDUCE_AVG);
        ArrayList<Integer> vGaps = analyzeProjection(vertical, false);

        return new Size(findMode(vGaps), findMode(hGaps));
    }

    private static ArrayList<Integer> analyzeProjection(Mat projection, boolean isHorizontal) {
        ArrayList<Integer> gaps = new ArrayList<>();
        int prev = -1;
        int length = isHorizontal ? projection.rows() : projection.cols();

        for (int i = 0; i < length; i++) {
            double val = isHorizontal ? projection.ptr(i).get() & 0xFF
                    : projection.ptr(0, i).get() & 0xFF;
            if (val > 128) {
                if (prev != -1) gaps.add(i - prev);
                prev = i;
            }
        }
        return gaps;
    }

    private static ArrayList<Point> matchTemplateMultiScale(Mat board, Mat template, int baseW, int baseH) {
        ArrayList<Point> matches = new ArrayList<>();
        Mat result = new Mat();
        double scaleStep = 0.1;
        for (double scale = 0.8; scale <= 1.2; scale += scaleStep) {
            int w = (int) (baseW * scale);
            int h = (int) (baseH * scale);
            if (w < 5 || h < 5) continue;
            Mat resized = new Mat();
            opencv_imgproc.resize(template, resized, new Size(w, h));
            opencv_imgproc.matchTemplate(board, resized, result, TM_CCOEFF_NORMED);
            DoublePointer minVal = new DoublePointer();
            DoublePointer maxVal = new DoublePointer();
            Point minLoc = new Point();
            Point maxLoc = new Point();
            opencv_core.minMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null);
            if (maxVal.get() > 0.8) {
                int gridX = maxLoc.x() / baseW;
                int gridY = maxLoc.y() / baseH;
                matches.add(new Point(gridX, gridY));
            }
        }
        return nms(matches, baseW, baseH);
    }

    private static ArrayList<Point> nms(ArrayList<Point> points, int w, int h) {
        ArrayList<Point> filtered = new ArrayList<>();
        boolean[] suppressed = new boolean[points.size()];

        for (int i = 0; i < points.size(); i++) {
            if (suppressed[i]) continue;
            Point p1 = points.get(i);
            filtered.add(p1);
            for (int j = i + 1; j < points.size(); j++) {
                Point p2 = points.get(j);
                if (Math.abs(p1.x() - p2.x()) < w / 2 &&
                        Math.abs(p1.y() - p2.y()) < h / 2) {
                    suppressed[j] = true;
                }
            }
        }
        return filtered;
    }

    private static int findMode(ArrayList<Integer> list) {
        HashMap<Integer, Integer> freq = new HashMap<>();
        for (int num : list) freq.put(num, freq.getOrDefault(num, 0) + 1);
        return Collections.max(freq.entrySet(), HashMap.Entry.comparingByValue()).getKey();
    }
}