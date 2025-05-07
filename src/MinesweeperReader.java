import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Scanner;

public class MinesweeperReader {

    // --- 定義遊戲狀態的常量 ---
    // 您需要根據實際遊戲畫面的顏色來定義這些值
    // 例如：
    // STATE_HIDDEN: 隱藏格子通常是帶有陰影效果的灰色按鈕
    // STATE_FLAG: 旗子通常是紅色的
    // STATE_EMPTY_REVEALED: 翻開的空白格子是淺灰色
    // STATE_NUMBER_1: 數字1通常是藍色的
    // ... 以此類推
    public static final int STATE_UNKNOWN = -99; // 未知狀態
    public static final int STATE_HIDDEN = -1;    // 隱藏
    public static final int STATE_FLAG = -2;      // 旗子
    public static final int STATE_EMPTY_REVEALED = 0; // 翻開的空白
    public static final int STATE_NUMBER_1 = 1;   // 數字1
    public static final int STATE_NUMBER_2 = 2;   // 數字2
    public static final int STATE_NUMBER_3 = 3;   // 數字3
    public static final int STATE_NUMBER_4 = 4;   // 數字4
    public static final int STATE_NUMBER_5 = 5;   // 數字5
    public static final int STATE_NUMBER_6 = 6;   // 數字6
    public static final int STATE_NUMBER_7 = 7;   // 數字7
    public static final int STATE_NUMBER_8 = 8;   // 數字8
    // 可能還有地雷狀態 (遊戲結束時才會顯示)
    // public static final int STATE_MINE = -3;

    // --- 定義遊戲UI元素的相對位置和尺寸常量 ---
    // 這些值是相對於遊戲網格左上角的座標。
    // 您需要根據您的Windows版本和遊戲視窗佈局來測量這些值。
    // 例如，假設雷數顯示和時間顯示在網格正上方，並且尺寸固定。
    private static final int MINE_COUNTER_RELATIVE_X = 15; // 雷數顯示相對於網格左上角的X偏移
    private static final int MINE_COUNTER_RELATIVE_Y = -40; // 雷數顯示相對於網格左上角的Y偏移
    private static final int MINE_COUNTER_WIDTH = 40;      // 雷數顯示區域寬度 (通常是3個數字寬度)
    private static final int MINE_COUNTER_HEIGHT = 25;     // 雷數顯示區域高度

    private static final int TIMER_RELATIVE_X = 180;      // 時間顯示相對於網格左上角的X偏移
    private static final int TIMER_RELATIVE_Y = -40;      // 時間顯示相對於網格左上角的Y偏移
    private static final int TIMER_WIDTH = 40;           // 時間顯示區域寬度 (通常是3個數字寬度)
    private static final int TIMER_HEIGHT = 25;          // 時間顯示區域高度

    // --- 儲存遊戲狀態的變數 ---
    private int[][] gridState;
    private int remainingMines = -1; // -1 表示未識別或錯誤
    private int currentTime = -1;    // -1 表示未識別或錯誤

    /**
     * 讀取指定位置的Windows踩地雷遊戲狀態。
     *
     * @param gridX 遊戲網格左上角的螢幕X座標。
     * @param gridY 遊戲網格左上角的螢幕Y座標。
     * @param cellWidth 單個格子（Cell）的寬度（像素）。
     * @param cellHeight 單個格子（Cell）的高度（像素）。
     * @param numRows 網格的行數。
     * @param numCols 網格的列數。
     * @return 如果成功讀取狀態，返回 true；否則返回 false。
     */
    public boolean readGameState(int gridX, int gridY, int cellWidth, int cellHeight, int numRows, int numCols) {
        try {
            // 1. 建立 Robot 物件用於螢幕截圖
            Robot robot = new Robot();

            // 2. 截取整個遊戲區域（包括網格、雷數和時間）
            // 需要計算出整個區域的包圍盒。這需要根據您測量的相對位置來調整。
            // 簡單起見，我們可以只截取網格和上方區域。
            int totalAreaX = gridX + Math.min(0, MINE_COUNTER_RELATIVE_X);
            int totalAreaY = gridY + Math.min(0, MINE_COUNTER_RELATIVE_Y);
            int totalAreaWidth = numCols * cellWidth + Math.max(0, MINE_COUNTER_RELATIVE_X + MINE_COUNTER_WIDTH - numCols * cellWidth); // 簡易計算
            int totalAreaHeight = numRows * cellHeight + Math.max(0, MINE_COUNTER_RELATIVE_Y + MINE_COUNTER_HEIGHT - numRows * cellHeight); // 簡易計算

            Rectangle captureArea = new Rectangle(totalAreaX, totalAreaY, totalAreaWidth, totalAreaHeight);
            BufferedImage gameScreenshot = robot.createScreenCapture(captureArea);

            // 計算網格在截圖中的相對座標
            int gridRelativeX = gridX - totalAreaX;
            int gridRelativeY = gridY - totalAreaY;


            // 3. 分析網格狀態
            gridState = new int[numRows][numCols];
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    // 獲取單個格子的子圖片區域
                    int cellX = gridRelativeX + col * cellWidth;
                    int cellY = gridRelativeY + row * cellHeight;
                    Rectangle cellRect = new Rectangle(cellX, cellY, cellWidth, cellHeight);

                    // 確保子圖片區域在截圖範圍內
                    if (cellRect.x < 0 || cellRect.y < 0 ||
                            cellRect.x + cellRect.width > gameScreenshot.getWidth() ||
                            cellRect.y + cellRect.height > gameScreenshot.getHeight()) {
                        System.err.println("警告: 格子區域超出截圖範圍，跳過。");
                        gridState[row][col] = STATE_UNKNOWN; // 標記為未知
                        continue;
                    }

                    BufferedImage cellImage = gameScreenshot.getSubimage(cellRect.x, cellRect.y, cellRect.width, cellRect.height);

                    // 識別格子的狀態
                    gridState[row][col] = getCellState(cellImage);
                }
            }

            // 4. 分析雷數和時間顯示
            // 計算雷數顯示區域在截圖中的相對座標
            int mineCounterRelativeX = gridRelativeX + MINE_COUNTER_RELATIVE_X;
            int mineCounterRelativeY = gridRelativeY + MINE_COUNTER_RELATIVE_Y;
            Rectangle mineCounterRect = new Rectangle(mineCounterRelativeX, mineCounterRelativeY, MINE_COUNTER_WIDTH, MINE_COUNTER_HEIGHT);

            // 確保雷數區域在截圖範圍內
            if (mineCounterRect.x >= 0 && mineCounterRect.y >= 0 &&
                    mineCounterRect.x + mineCounterRect.width <= gameScreenshot.getWidth() &&
                    mineCounterRect.y + mineCounterRect.height <= gameScreenshot.getHeight()) {

                BufferedImage mineCounterImage = gameScreenshot.getSubimage(mineCounterRect.x, mineCounterRect.y, mineCounterRect.width, mineCounterRect.height);
                remainingMines = analyzeDigitDisplay(mineCounterImage);

            } else {
                System.err.println("警告: 雷數顯示區域超出截圖範圍，無法識別。");
            }


            // 計算時間顯示區域在截圖中的相對座標
            int timerRelativeX = gridRelativeX + TIMER_RELATIVE_X;
            int timerRelativeY = gridRelativeY + TIMER_RELATIVE_Y;
            Rectangle timerRect = new Rectangle(timerRelativeX, timerRelativeY, TIMER_WIDTH, TIMER_HEIGHT);

            // 確保時間區域在截圖範圍內
            if (timerRect.x >= 0 && timerRect.y >= 0 &&
                    timerRect.x + timerRect.width <= gameScreenshot.getWidth() &&
                    timerRect.y + timerRect.height <= gameScreenshot.getHeight()) {

                BufferedImage timerImage = gameScreenshot.getSubimage(timerRect.x, timerRect.y, timerRect.width, timerRect.height);
                currentTime = analyzeDigitDisplay(timerImage);

            } else {
                System.err.println("警告: 時間顯示區域超出截圖範圍，無法識別。");
            }


            return true; // 成功
        } catch (AWTException e) {
            e.printStackTrace();
            System.err.println("無法建立 java.awt.Robot，可能缺少權限或系統問題。");
            return false; // 失敗
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("讀取遊戲狀態時發生未知錯誤。");
            return false; // 失敗
        }
    }

    /**
     * 分析單個格子的圖片，判斷其狀態 (隱藏, 旗子, 數字等)。
     * 這部分需要根據實際遊戲畫面精確實現。
     * 可以檢查中心像素的顏色，或者檢查多個關鍵像素。
     *
     * @param cellImage 單個格子的 BufferedImage。
     * @return 格子的狀態常量。
     */
    private int getCellState(BufferedImage cellImage) {
        // TODO: 實現精確的圖像識別邏輯

        int width = cellImage.getWidth();
        int height = cellImage.getHeight();
        // 檢查中心像素顏色是一個簡單的開始
        int centerPixelColor = cellImage.getRGB(width / 2, height / 2);

        // 獲取 RGB 分量 (Alpha, Red, Green, Blue)
        // 注意：getRGB 返回的是一個 packed int，最高位是 Alpha，然後是 Red, Green, Blue。
        int alpha = (centerPixelColor >> 24) & 0xff;
        int red = (centerPixelColor >> 16) & 0xff;
        int green = (centerPixelColor >> 8) & 0xff;
        int blue = (centerPixelColor) & 0xff;

        // --- 以下是示例判斷邏輯，需要根據您的遊戲畫面調整精確的 RGB 值 ---
        // 您可以使用畫圖或其他工具查看遊戲中不同狀態格子的像素顏色。

        // 判斷隱藏狀態 (通常是中等灰色，帶有3D效果的顏色差異，可以檢查邊角像素)
        // 簡單檢查中心顏色是否在某個灰色範圍內
        if (red > 150 && red < 200 && green > 150 && green < 200 && blue > 150 && blue < 200) {
            // 這是非常粗略的判斷，實際隱藏格子需要更精確的識別
            // 例如，檢查左上角是否有亮色邊緣，右下角是否有暗色陰影
            int topLeftColor = cellImage.getRGB(1, 1); // 檢查左上角像素
            int bottomRightColor = cellImage.getRGB(width - 2, height - 2); // 檢查右下角像素
            int topLeftRed = (topLeftColor >> 16) & 0xff;
            int bottomRightRed = (bottomRightColor >> 16) & 0xff;
            // 如果左上角顏色較亮，右下角顏色較暗，很可能是隱藏格子
            if (topLeftRed > red && bottomRightRed < red) {
                return STATE_HIDDEN;
            }
        }

        // 判斷旗子狀態 (通常是紅色)
        // 檢查中心或特定位置是否有顯著的紅色
        if (red > 200 && green < 100 && blue < 100) {
            // 這裏判斷旗子的紅色可能與其他紅色重合，需要更精確的判斷或檢查旗子圖案
            // 更穩健的方式是檢查旗子圖片的特定模式或顏色分佈
            return STATE_FLAG;
        }

        // 判斷翻開的空白狀態 (通常是淺灰色)
        if (red > 200 && green > 200 && blue > 200) {
            // 檢查是否沒有數字、旗子或地雷的圖案
            // 這裏需要確保不是背景的其他淺色區域
            return STATE_EMPTY_REVEALED;
        }

        // 判斷數字狀態 (根據數字的顏色)
        // 數字1: 藍色
        if (blue > 150 && red < 100 && green < 100) {
            return STATE_NUMBER_1;
        }
        // 數字2: 綠色
        if (green > 150 && red < 100 && blue < 100) {
            return STATE_NUMBER_2;
        }
        // 數字3: 紅色 (與旗子紅色區分開，可能需要檢查更深的紅色或位置)
        if (red > 150 && green < 100 && blue < 100) { // 與旗子判斷類似，需要更精確
            // 例如，檢查中心文字區域的顏色，而不是圖標顏色
            // 您可能需要檢查中心周圍一塊區域的顏色來區分旗子和數字3
            int textColor = cellImage.getRGB(width/2, height/2); // 再次檢查中心，假設數字在中心
            int textRed = (textColor >> 16) & 0xff;
            if (textRed > 150 && (green < 50 && blue < 50) ) { // 檢查更純的紅色
                return STATE_NUMBER_3;
            }
        }
        // 數字4: 深藍色/紫色
        if (blue > 100 && red < 100 && green < 100) { // 粗略判斷
            return STATE_NUMBER_4;
        }
        // 數字5: 棕色/栗色
        if (red > 100 && green < 50 && blue < 50) { // 粗略判斷
            return STATE_NUMBER_5;
        }
        // 數字6: 青色/藍綠色
        if (green > 100 && blue > 100 && red < 100) { // 粗略判斷
            return STATE_NUMBER_6;
        }
        // 數字7: 黑色
        if (red < 50 && green < 50 && blue < 50) { // 粗略判斷
            return STATE_NUMBER_7;
        }
        // 數字8: 灰色
        if (red > 50 && red < 150 && green > 50 && green < 150 && blue > 50 && blue < 150) { // 粗略判斷
            return STATE_NUMBER_8;
        }


        // 如果以上都不匹配，可能是未知狀態或地雷 (遊戲結束時)
        return STATE_UNKNOWN;
    }

    /**
     * 分析雷數或時間顯示區域的圖片，識別其中的數字。
     * 這部分需要根據實際遊戲畫面精確實現。
     * 數字通常是類似七段顯示器的樣式。
     *
     * @param displayImage 雷數或時間顯示區域的 BufferedImage。
     * @return 識別出的數字值。如果無法識別，返回 -1。
     */
    private int analyzeDigitDisplay(BufferedImage displayImage) {
        // TODO: 實現精確的數字識別邏輯

        // 假設顯示區域包含 3 個數字，並且間隔固定
        int displayWidth = displayImage.getWidth();
        int displayHeight = displayImage.getHeight();

        // 假設每個數字佔用的寬度大約是 displayWidth / 3
        int digitWidth = displayWidth / 3;

        StringBuilder recognizedNumber = new StringBuilder();

        for (int i = 0; i < 3; i++) { // 通常有3個數字
            // 獲取單個數字的子圖片區域
            int digitX = i * digitWidth;
            Rectangle digitRect = new Rectangle(digitX, 0, digitWidth, displayHeight);

            if (digitRect.x + digitRect.width > displayImage.getWidth()) {
                digitRect = new Rectangle(digitX, 0, displayImage.getWidth() - digitX, displayHeight); // 調整最後一個數字的寬度
            }


            BufferedImage digitImage = displayImage.getSubimage(digitRect.x, digitRect.y, digitRect.width, digitRect.height);

            // 識別單個數字
            char digitChar = recognizeDigit(digitImage);
            if (digitChar == '?') { // 無法識別的數字
                // 如果是雷數，可能是負號
                if (i == 0) { // 第一個數字位置
                    // 檢查是否是負號 (通常是中間一條橫線)
                    // 您需要根據實際畫面的像素顏色和佈局來檢查
                    int minusSignColor = digitImage.getRGB(digitWidth / 2, displayHeight / 2); // 檢查中心點顏色
                    int red = (minusSignColor >> 16) & 0xff;
                    int green = (minusSignColor >> 8) & 0xff;
                    int blue = (minusSignColor) & 0xff;
                    if (red < 50 && green < 50 && blue < 50) { // 假設負號是黑色
                        digitChar = '-';
                    } else {
                        System.err.println("警告: 無法識別第 " + (i+1) + " 個數字或負號");
                        recognizedNumber.append('?');
                        continue;
                    }
                } else {
                    System.err.println("警告: 無法識別第 " + (i+1) + " 個數字");
                    recognizedNumber.append('?');
                    continue;
                }
            }
            recognizedNumber.append(digitChar);
        }

        try {
            return Integer.parseInt(recognizedNumber.toString());
        } catch (NumberFormatException e) {
            System.err.println("警告: 識別出的數字串無法轉換為整數: " + recognizedNumber.toString());
            return -1; // 轉換失敗
        }
    }

    /**
     * 分析單個數字圖片，判斷是哪個數字 (0-9) 或負號 (-)。
     * 這部分需要根據實際遊戲畫面精確實現。
     * 可以檢查七段顯示器的各個段是否亮起。
     *
     * @param digitImage 單個數字的 BufferedImage。
     * @return 識別出的數字字元 ('0'-'9') 或負號 ('-')，如果無法識別返回 '?'.
     */
    private char recognizeDigit(BufferedImage digitImage) {
        // TODO: 實現精確的數字字元識別邏輯

        int width = digitImage.getWidth();
        int height = digitImage.getHeight();

        // 假設數字是黑色或紅色 (負號) 在暗灰色背景上
        // 您需要確定數字像素的顏色範圍和背景像素的顏色範圍

        // 檢查幾個關鍵點來判斷是哪個數字（模擬七段顯示器）
        // 這些點的位置需要根據實際數字的尺寸和位置來精確測量
        int topSegmentX = width / 2;
        int topSegmentY = height / 4; // 頂部線段
        int middleSegmentX = width / 2;
        int middleSegmentY = height / 2; // 中間線段
        int bottomSegmentX = width / 2;
        int bottomSegmentY = height * 3 / 4; // 底部線段

        int topLeftSegmentX = width / 4;
        int topLeftSegmentY = height / 2 - height / 8; // 左上線段
        int bottomLeftSegmentX = width / 4;
        int bottomLeftSegmentY = height / 2 + height / 8; // 左下線段

        int topRightSegmentX = width * 3 / 4;
        int topRightSegmentY = height / 2 - height / 8; // 右上線段
        int bottomRightSegmentX = width * 3 / 4;
        int bottomRightSegmentY = height / 2 + height / 8; // 右下線段


        // 檢查特定點的顏色是否是數字顏色 (非背景色)
        // isDigitColor(int x, int y, BufferedImage image) 這個輔助方法會很有用
        boolean segTop = isDigitColor(topSegmentX, topSegmentY, digitImage);
        boolean segMid = isDigitColor(middleSegmentX, middleSegmentY, digitImage);
        boolean segBot = isDigitColor(bottomSegmentX, bottomSegmentY, digitImage);
        boolean segTL = isDigitColor(topLeftSegmentX, topLeftSegmentY, digitImage);
        boolean segBL = isDigitColor(bottomLeftSegmentX, bottomLeftSegmentY, digitImage);
        boolean segTR = isDigitColor(topRightSegmentX, topRightSegmentY, digitImage);
        boolean segBR = isDigitColor(bottomRightSegmentX, bottomRightSegmentY, digitImage);


        // 根據亮起的線段組合來判斷數字
        if (segTop && segTL && segTR && segBL && segBR && segBot && !segMid) return '0';
        if (!segTop && !segTL && segTR && !segMid && !segBL && segBR && !segBot) return '1'; // 1
        if (segTop && !segTL && segTR && segMid && segBL && !segBR && segBot) return '2'; // 2
        if (segTop && !segTL && segTR && segMid && !segBL && segBR && segBot) return '3'; // 3
        if (!segTop && segTL && segTR && segMid && !segBL && segBR && !segBot) return '4'; // 4
        if (segTop && segTL && !segTR && segMid && !segBL && segBR && segBot) return '5'; // 5
        if (segTop && segTL && !segTR && segMid && segBL && segBR && segBot) return '6'; // 6
        if (segTop && !segTL && segTR && !segMid && !segBL && !segBR && !segBot) return '7'; // 7 - 注意，有些字體7只有頂部和右邊兩個斜線，可能需要調整判斷邏輯
        if (segTop && segTL && segTR && segMid && segBL && segBR && segBot) return '8'; // 8
        if (segTop && segTL && segTR && segMid && !segBL && segBR && segBot) return '9'; // 9

        // 判斷負號
        if (!segTop && !segTL && !segTR && segMid && !segBL && !segBR && !segBot) return '-'; // 負號

        return '?'; // 無法識別
    }

    /**
     * 輔助方法：判斷指定座標的像素顏色是否是數字顏色。
     * 需要根據實際遊戲畫面中數字的顏色和背景顏色來實現。
     *
     * @param x 像素的X座標 (相對於 digitImage)。
     * @param y 像素的Y座標 (相對於 digitImage)。
     * @param image 數字的 BufferedImage。
     * @return 如果是數字顏色，返回 true；否則返回 false。
     */
    private boolean isDigitColor(int x, int y, BufferedImage image) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return false; // 座標超出範圍
        }
        int color = image.getRGB(x, y);
        int red = (color >> 16) & 0xff;
        int green = (color >> 8) & 0xff;
        int blue = (color) & 0xff;

        // TODO: 根據實際遊戲畫面中數字和背景的顏色範圍來定義判斷邏輯
        // 假設數字是黑色或紅色，背景是暗灰色
        // 這裏的閾值需要精確調整
        int digitColorThreshold = 50; // 數字顏色（黑色或紅色）的RGB分量應該小於這個閾值（對於黑色）
        // 或者紅色分量大於一個閾值，綠色和藍色小於一個閾值（對於紅色負號）
        int backgroundColorMin = 80; // 背景顏色（暗灰色）的RGB分量應該大於這個閾值

        // 簡單判斷：如果顏色比較深，可能是數字顏色
        if (red < backgroundColorMin && green < backgroundColorMin && blue < backgroundColorMin) {
            return true; // 可能是黑色數字
        }
        // 判斷紅色負號 (假設是鮮紅色)
        if (red > 150 && green < 50 && blue < 50) {
            return true; // 可能是紅色負號
        }


        return false; // 可能是背景色或其他顏色
    }


    /**
     * 獲取遊戲網格的狀態。
     *
     * @return 儲存遊戲網格狀態的二維整數陣列。
     */
    public int[][] getGridState() {
        return gridState;
    }

    /**
     * 獲取剩餘地雷數。
     *
     * @return 剩餘地雷數。
     */
    public int getRemainingMines() {
        return remainingMines;
    }

    /**
     * 獲取當前時間。
     *
     * @return 當前時間（秒）。
     */
    public int getCurrentTime() {
        return currentTime;
    }

    // --- 主程式入口 ---
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("請確保 Windows 踩地雷遊戲正在執行，並且位於螢幕可見區域。");
        System.out.println("遊戲視窗不能被其他視窗遮擋。");

        System.out.println("\n請輸入遊戲網格的左上角螢幕座標 (X Y):");
        int gridX = scanner.nextInt();
        int gridY = scanner.nextInt();

        System.out.println("請輸入單個格子（Cell）的寬度和高度 (Width Height):");
        int cellWidth = scanner.nextInt();
        int cellHeight = scanner.nextInt();

        System.out.println("請輸入網格的行數和列數 (Rows Cols):");
        int numRows = scanner.nextInt();
        int numCols = scanner.nextInt();

        MinesweeperReader reader = new MinesweeperReader();

        // 連續讀取狀態，直到遊戲結束或使用者停止 (可選)
        // while (true) { // 您可以根據需要 loop
        System.out.println("\n正在讀取遊戲狀態...");
        boolean success = reader.readGameState(gridX, gridY, cellWidth, cellHeight, numRows, numCols);

        if (success) {
            System.out.println("\n--- 遊戲狀態 ---");
            System.out.println("剩餘地雷數: " + reader.getRemainingMines());
            System.out.println("當前時間 (秒): " + reader.getCurrentTime());

            System.out.println("\n網格狀態:");
            int[][] state = reader.getGridState();
            if (state != null) {
                for (int i = 0; i < state.length; i++) {
                    for (int j = 0; j < state[i].length; j++) {
                        // 根據狀態常量列印不同的標識
                        switch (state[i][j]) {
                            case STATE_HIDDEN: System.out.print("H "); break; // Hidden
                            case STATE_FLAG: System.out.print("F "); break; // Flag
                            case STATE_EMPTY_REVEALED: System.out.print("  "); break; // Empty
                            case STATE_UNKNOWN: System.out.print("? "); break; // Unknown
                            default:
                                if (state[i][j] >= STATE_NUMBER_1 && state[i][j] <= STATE_NUMBER_8) {
                                    System.out.print(state[i][j] + " "); // Number
                                } else {
                                    System.out.print("? "); // Should not happen if states are covered
                                }
                        }
                    }
                    System.out.println(); // 換行
                }
            } else {
                System.out.println("無法獲取網格狀態。");
            }

        } else {
            System.out.println("讀取遊戲狀態失敗。");
        }

        // 可以在這裏添加延遲 Thread.sleep(1000);

        // } // End while loop (如果啟用)

        scanner.close();
    }
}