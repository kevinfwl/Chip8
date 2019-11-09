import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

class Screen extends Canvas {

    public static Color ON_PIXEL_COLOR = Color.rgb(30, 39, 46);
    public static Color OFF_PIXEL_COLOR = Color.rgb(137, 143, 139);

    private Emulator emu;
    private GraphicsContext gc;

    Screen(Emulator emu) {
        super(640, 320);
        this.emu = emu;
        this.gc = getGraphicsContext2D();
        // setHeight(320);
        // setWidth(640);
    }
    //initializes the screen and sets the fill color
    public void init() {
        gc.setFill(OFF_PIXEL_COLOR);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
        

        // gc.fillRect(0, 0, 10, 10);
        // gc.fillRect(0, 300, 10, 10);
        // gc.fillRect(0, 260, 10, 10);

        // gc.fillRect(0, 310, 10, 10);

        // gc.fillRect(630, 300, 10, 10);
        // gc.fillRect(630, 260, 10, 10);

        // gc.fillRect(630, 310, 10, 10);
    }

    public void resize(double height, double width) {
        this.setHeight(height);
        this.setWidth(width);
        this.init();
    }

    public void redraw() {
        for (int x = 0; x < this.getWidth() / 10; x++) {
            for (int y = 0; y < this.getHeight() / 10; y++) {
                if (this.emu.getPixel(x, y)) {
                    this.gc.setFill(ON_PIXEL_COLOR);
                }
                else {
                    this.gc.setFill(OFF_PIXEL_COLOR);
                }
                this.gc.fillRect(x* 10, y * 10, 10, 10);                
            }
        }
    }
}