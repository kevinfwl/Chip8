import java.io.File;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    // STATES
    private Screen screen;
    private Emulator emu;
    private Keyboard keyboard;
    private boolean debugState;
    private FileChooser fileChooser;
    private File rom;

    public static void main(String[] args) {
        launch(args);
    }

    // automaticcally called in start function implicitly
    public void init() {
        this.emu = Emulator.getInstance();
        this.screen = new Screen(this.emu);
        this.keyboard = new Keyboard(this.emu);
        this.screen.init();
        this.fileChooser = new FileChooser();
        this.fileChooser.setInitialDirectory(new File("./roms"));
        this.debugState = false;
        this.rom = null;
        try {
            this.emu.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void reset() {
        System.out.println("drawn rest");
        try {
            this.emu.init();
            this.emu.load(this.rom.getAbsolutePath());
            this.screen.init();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // add debugger
        // add load rom
        // fix the pixels
        // add the commandline incrementor and current machine state
        // set timer to 60 fps
        // file selection not working

        primaryStage.setTitle("kevinfwl's CHIP8");

        // new implementation of keyframe
        KeyFrame frame = new KeyFrame(Duration.millis(2), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (rom == null)
                    return;
                try {
                    emu.CPUcycle();
                    if (emu.getDrawFlag()) {
                        screen.redraw();
                        emu.setDrawFlag(false);
                    }

                    if (debugState) {
                        stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        Timeline clockCycler = new Timeline(frame);
        clockCycler.setCycleCount(Animation.INDEFINITE);

        // build the menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Menu options = new Menu("Options");
        MenuItem loadRom = new MenuItem("Load Rom");
        loadRom.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                clockCycler.stop();
                File newRom = fileChooser.showOpenDialog(primaryStage);
                if (newRom != null) {
                    rom = newRom;
                    reset();
                    clockCycler.play();
                }
            }
        });

        menuBar.getMenus().add(options);
        options.getItems().add(loadRom);

        Pane root = new StackPane(screen);
        // root.setStyle("-fx-border-width: 25px; -fx-border-style: solid;
        // -fx-border-color: #545454;");

        BorderPane root2 = new BorderPane();
        root2.setTop(menuBar);
        root2.setCenter(screen);
        Canvas bottom = new Canvas(100, 500);
        GraphicsContext graphics = bottom.getGraphicsContext2D();
        graphics.setFill(Color.BLACK);
        graphics.fillRect(0, 0, 100, 500);

        Scene mainScene = new Scene(root2);

        mainScene.setOnKeyPressed(this.keyboard);
        mainScene.setOnKeyReleased(this.keyboard);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        //numbers are just hardcoded for now, from current jdk the window does not seem to wrap around canvas properly
        //if resizeable is set to false

        primaryStage.show();
        // System.out.println(primaryStage.getWidth());
        // System.out.println(menuBar.getHeight());

        // System.out.println(primaryStage.getWidth());
        System.out.println(root2.getHeight());
        System.out.println(root2.getWidth());
        System.out.println(root.getHeight());
        System.out.println(root.getWidth());
        clockCycler.play();
    }

    //setup the keyboard 
}

// JavaFX Java GUI Tutorial - 8 - Embedding Layouts
