import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_calib3d;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_features2d.BFMatcher;
import org.bytedeco.opencv.opencv_features2d.ORB;
// import org.bytedeco.opencv.opencv_features2d.SIFT; // 如果想用SIFT，請替換ORB
// import org.bytedeco.opencv.opencv_features2d.SURF; // 如果想用SURF，請替換ORB
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_LINEAR;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.util.HashMap;
import java.util.ArrayList;


public class OpenCV {
    private static final int min_grid_side_length = 40;
    private static final int max_grid_side_length = 60;
    private static Mat images[];

    static {
        load_images();
    }

    private static void load_images() {
        images = new Mat[MinesweeperState.operands.length];
        for (int i = 0; i < MinesweeperState.operands.length; ++i) {
            images[i] = array_to_mat(MinesweeperState.images[i]);
        }
    }

    public static String display_positions(HashMap<Character, ArrayList<int[]>> results) {
        StringBuilder sb = new StringBuilder();
        for (Character operand : results.keySet()) {
            ArrayList<int[]> positions = results.get(operand);
            sb.append(operand).append(": ");
            for (int[] position : positions) {
                sb.append('(').append(position[0]).append(",").append(position[1]).append(") ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static HashMap<Character, ArrayList<int[]>> process(int board_rgb_array[][][]) {
        assert board_rgb_array != null && board_rgb_array.length > 0 && board_rgb_array[0] != null && board_rgb_array[0].length > 0;
        assert board_rgb_array[0][0] != null && 3 == board_rgb_array[0][0].length;
        int side_length = detect_grid_size(array_to_mat(board_rgb_array), board_rgb_array.length, board_rgb_array[0].length);
        return null;
    }

    private static int detect_grid_size(Mat board_mat, int width, int height) {
//        int max_side_length = Math.min(width, height);
        for (int side_length = min_grid_side_length; side_length < max_grid_side_length; ++side_length) {
            for (int k = 0; k < images.length; ++k) {
                for (int i = 0; i < width - side_length + 1; ++i) {
                    for (int j = 0; j < height - side_length + 1; ++j) {
                        Mat sub_mat = extract_sub_mat(board_mat, i, i + side_length, j, j + side_length);
                        Mat resized_sub_mat = resize_mat(sub_mat, images[k].arrayWidth(), images[k].arrayHeight());
                        double distance = compare_mat_mse(resized_sub_mat, images[k]);
                        System.out.println("i=" + i + ",j=" + j + ",side_length=" + side_length+" distance=" + distance);
                        if (distance < 1) {
                            System.out.println("i=" + i + ",j=" + j + ",side_length=" + side_length + ",distance=" + distance + ",char=" + MinesweeperState.operands[k]);
                            System.exit(0);
                        }
                    }
                }
            }
        }
        return 0;
    }

    private static Mat resize_mat(Mat original_mat, int new_width, int new_height) {
        if (original_mat == null || original_mat.empty()) {
            return null;
        }
        Mat resizedMat = new Mat();
        Size newSize = new Size(new_width, new_height);
        resize(original_mat, resizedMat, newSize, 0, 0, INTER_LINEAR);
        return resizedMat;
    }

    public static double compare_mat_mse(Mat mat_1, Mat mat_2) {
        assert mat_1 != null && mat_2 != null && !mat_1.empty() && !mat_2.empty();
        assert mat_1.cols() == mat_2.cols() && mat_1.rows() == mat_2.rows() && mat_1.type() == mat_2.type();
        double sumSqDiff = norm(mat_1, mat_2);
        double totalElements = (double) mat_1.cols() * mat_1.rows() * mat_1.channels();
        return sumSqDiff / totalElements;
    }

    public static Mat extract_sub_mat(Mat original_mat, int start_width, int end_width, int start_height, int end_height) {
        Rect roi = new Rect(start_width, start_height, end_width - start_width, end_height - start_height);
//        System.out.println("roi=" + start_width+", "+start_height+", "+end_width+", "+end_height+"\noriginal_mat.size="+original_mat.arrayWidth()+", "+original_mat.arrayHeight());
        return new Mat(original_mat, roi);
    }

    private static Mat array_to_mat(int[][][] arr) {
        int width = arr.length;
        int height = arr[0].length;
        Mat mat = new Mat(height, width, CV_8UC3);
        BytePointer ptr = new BytePointer(mat.data());
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                byte r = (byte) arr[x][y][0];
                byte g = (byte) arr[x][y][1];
                byte b = (byte) arr[x][y][2];
                ptr.position(y * mat.step() + x * 3L).put(b, g, r);
            }
        }
        return mat;
    }

    private static double compare_subimage_scale_abandoned(Mat img1, Mat img2, int start_x, int start_y, int end_x, int end_y) {
        try (Mat subImage = new Mat();
             Mat graySubImage = new Mat();
             Mat grayImg2 = new Mat();
             KeyPointVector keypoints1 = new KeyPointVector();
             Mat descriptors1 = new Mat();
             KeyPointVector keypoints2 = new KeyPointVector();
             Mat descriptors2 = new Mat();
             BFMatcher matcher = new BFMatcher(opencv_core.NORM_HAMMING, true); // 對ORB使用NORM_HAMMING，對SIFT/SURF使用NORM_L2
        ) {
            if (start_x < 0 || start_y < 0 || end_x > img1.cols() || end_y > img1.rows() || start_x >= end_x || start_y >= end_y) {
                System.err.println("錯誤：指定的子區域座標無效。");
                return 0;
            }
            Rect roi = new Rect(start_x, start_y, end_x - start_x, end_y - start_y);
            new Mat(img1, roi).copyTo(subImage); // 將子區域拷貝到 subImage 中

            if (subImage.empty() || img2.empty()) {
                System.err.println("錯誤：輸入圖片或子區域為空。");
                return 0;
            }
            if (subImage.channels() == 3) {
                opencv_imgproc.cvtColor(subImage, graySubImage, opencv_imgproc.COLOR_BGR2GRAY);
            } else {
                subImage.copyTo(graySubImage);
            }
            if (img2.channels() == 3) {
                opencv_imgproc.cvtColor(img2, grayImg2, opencv_imgproc.COLOR_BGR2GRAY);
            } else {
                img2.copyTo(grayImg2);
            }
            ORB detector = ORB.create();
            // SIFT detector = SIFT.create();
            // SURF detector = SURF.create();
            detector.detectAndCompute(graySubImage, new Mat(), keypoints1, descriptors1);
            detector.detectAndCompute(grayImg2, new Mat(), keypoints2, descriptors2);
            if (keypoints1.size() < 10 || keypoints2.size() < 10) {
                System.err.println("錯誤：未能檢測到足夠的特徵點進行匹配。");
                return 0;
            }
            DMatchVectorVector matches = new DMatchVectorVector();
            matcher.knnMatch(descriptors1, descriptors2, matches, 2);
            ArrayList<DMatch> goodMatchesList = new ArrayList<>();
            float ratioThresh = 0.75f;
            for (int i = 0; i < matches.size(); i++) {
                DMatchVector pair = matches.get(i);
                if (pair.size() > 1) {
                    DMatch m = pair.get(0);
                    DMatch n = pair.get(1);
                    if (m.distance() < n.distance() * ratioThresh) {
                        goodMatchesList.add(m);
                    }
                }
                pair.close();
            }
            if (goodMatchesList.size() < 4) {
                System.err.println("錯誤：篩選後好的匹配點不足4個。");
                return 0;
            }
            try (
                    Point2fVector pts1 = new Point2fVector(goodMatchesList.size());
                    Point2fVector pts2 = new Point2fVector(goodMatchesList.size());
                    Mat matPts1 = new Mat(goodMatchesList.size(), 1, opencv_core.CV_32FC2);
                    Mat matPts2 = new Mat(goodMatchesList.size(), 1, opencv_core.CV_32FC2);
                    Mat mask = new Mat();
            ) {
                for (int i = 0; i < goodMatchesList.size(); i++) {
                    DMatch match = goodMatchesList.get(i);
                    Point2f pt1 = keypoints1.get(match.queryIdx()).pt();
                    Point2f pt2 = keypoints2.get(match.trainIdx()).pt();
                    BytePointer ptr1 = matPts1.ptr(i, 0);
                    ptr1.putFloat(pt1.x());
                    ptr1.position(ptr1.position() + 4).putFloat(pt1.y());
                    BytePointer ptr2 = matPts2.ptr(i, 0);
                    ptr2.putFloat(pt2.x());
                    ptr2.position(ptr2.position() + 4).putFloat(pt2.y());
                    pt1.close();
                    pt2.close();
                    ptr1.close();
                    ptr2.close();
                }
                Mat H = opencv_calib3d.findHomography(matPts1, matPts2, mask, opencv_calib3d.RANSAC, 5.0);
                int inlierCount = 0;
                if (mask.size().area() > 0) {
                    for (int i = 0; i < mask.rows(); i++) {
                        if (mask.ptr(i).get(0) == 1) {
                            inlierCount++;
                        }
                    }
                }
                if (H != null) H.close();
                return inlierCount;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}