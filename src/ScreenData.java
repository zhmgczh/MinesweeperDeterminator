public class ScreenData {
    public int width;
    public int height;
    public int rgb_array[][][];

    public ScreenData(int width, int height, int rgb_array[][][]) {
        assert width > 0 && height > 0;
        assert null != rgb_array && rgb_array.length == width && null != rgb_array[0] && rgb_array[0].length == height && rgb_array[0][0].length == 3;
        this.width = width;
        this.height = height;
        this.rgb_array = rgb_array;
    }
}