import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

class Screen extends Canvas {

    public static Color ON_PIXEL_COLOR = Color.rgb(30, 39, 46);
    public static Color OFF_PIXEL_COLOR = Color.rgb(137, 143, 139);
    public int pixelSize;
    private Emulator emu;
    private GraphicsContext gc;

    Screen(Emulator emu) {
        super(640, 320);
        this.emu = emu;
        this.emu.setScreen(this);
        this.gc = getGraphicsContext2D();
        this.pixelSize = 10;
    }
    //initializes the screen and sets the fill color
    public void init() {
        if (this.emu == null) return;
        this.pixelSize = 640 / this.emu.getScreenWidth();
        gc.setFill(OFF_PIXEL_COLOR);
        gc.fillRect(0, 0, 640, 320);
    }

    public void resize() {
        if (this.emu == null) return;
        this.pixelSize = 640 / this.emu.getScreenWidth();
;
    }

    public void redraw() {
        for (int x = 0; x < this.emu.getScreenWidth(); x++) {
            for (int y = 0; y < this.emu.getScreenHeight(); y++) {
                if (this.emu.getPixel(x, y)) {
                    this.gc.setFill(ON_PIXEL_COLOR);
                }
                else {
                    this.gc.setFill(OFF_PIXEL_COLOR);
                }
                this.gc.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);                
            }
        }
    }
}