import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

class Screen extends Canvas {

    public static int SCREEN_SIZE_X = 640;
    public static int SCREEN_SIZE_Y = 320;

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
        gc.setFill(Color.BLACK);
        System.out.println("drawn");
        
        gc.fillRect(0, 0, 640, 320);
        gc.setFill(Color.BLACK);

        // gc.fillRect(0, 0, 10, 10);
        // gc.fillRect(0, 300, 10, 10);
        // gc.fillRect(0, 260, 10, 10);

        // gc.fillRect(0, 310, 10, 10);

        // gc.fillRect(630, 300, 10, 10);
        // gc.fillRect(630, 260, 10, 10);

        // gc.fillRect(630, 310, 10, 10);
    }

    public void redraw() {
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 32; j++) {
                if (this.emu.getPixel(i, j)) {
                    this.gc.setFill(Color.BLACK);
                }
                else {
                    this.gc.setFill(Color.BEIGE);
                }
                this.gc.fillRect(i* 10, j * 10, 10, 10);                
            }
        }
    }
}