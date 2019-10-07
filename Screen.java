import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

class Screen extends Canvas {

    public static int SCREEN_SIZE_X = 640;
    public static int SCREEN_SIZE_Y = 320;

    private Emulator emu;
    private GraphicsContext gc;
    

    Screen(Emulator emu) {
        super();
        this.emu = emu;
        this.gc = getGraphicsContext2D();
    }
    //initializes the screen and sets the fill color
    public void init() {
        setHeight(320);
        setWidth(640);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_SIZE_X, SCREEN_SIZE_Y);
    }

    public void redraw() {
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 32; j++) {
                if (this.emu.getPixel(i, j)) {
                    this.gc.setFill(Color.WHITE);
                }
                else {
                    this.gc.setFill(Color.BLACK);
                }
                this.gc.fillRect(i* 10, j * 10, SCREEN_SIZE_X / 64, SCREEN_SIZE_Y / 32);

            }
        }
    }
}