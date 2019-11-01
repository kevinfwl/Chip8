import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.animation.*;
 
public class Main extends Application {

    //STATES

    private HBox hBox;
    private VBox vBox;
    private Screen screen;
    private Emulator emu;
    private Keyboard keyboard;

    private boolean debugState;

    public static void main(String[] args) {
        launch(args);
    }
    

    //automaticcally called in start function implicitly
    public void init() {
        // System.out.println("kasldj\t" + "okay");
        // System.out.println("as\t" + "okay");
        this.emu = Emulator.getInstance();
        this.screen = new Screen(this.emu);
        this.keyboard = new Keyboard(this.emu);
        this.screen.init();
        this.debugState = false;
        try {
            this.emu.init();
            // this.emu.load("D:\\side projects\\emulator\\Chip8\\roms\\TETRIS");
            this.emu.load("C:\\personal\\Chip8\\roms\\TETRIS");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chip8 emulator");
        AnimationTimer timer = new AnimationTimer(){
            @Override
            public void handle(long now) {
                try {
                    emu.CPUcycle();
                    if (emu.getDrawFlag()) {
                        screen.redraw();  
                        emu.setDrawFlag(false);              
                    }                    
                    // if (debugState) {
                    //     stop();
                    // }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        };
        
        StackPane root = new StackPane();
        root.getChildren().add(screen);
        
        Scene mainScene = new Scene(root, 640, 320);
        
        mainScene.setOnKeyPressed(this.keyboard);
        mainScene.setOnKeyReleased(this.keyboard);
        // mainScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            
        //     @Override
        //     public void handle(KeyEvent event) {
        //         switch (event.getCode()) {
        //             case ENTER:
        //                 timer.start();
        //         }
        //     }
        // });

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        timer.start();
    }

    //setup the keyboard 
}

// JavaFX Java GUI Tutorial - 8 - Embedding Layouts
