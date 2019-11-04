import java.io.File;

import javax.swing.Action;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
 
public class Main extends Application {

    //STATES
    private Screen screen;
    private Emulator emu;
    private Keyboard keyboard;
    private boolean debugState;
    private FileChooser fileChooser;
    private File rom;
    public static void main(String[] args) {
        launch(args);
    }
    
    //automaticcally called in start function implicitly
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
        }
        catch (Exception e) {
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
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        //add debugger
        //add load rom
        //fix the pixels
        //add the commandline incrementor and current machine state
        //set timer to 60 fps
        //file selection not working

        primaryStage.setTitle("kevinfwl's CHIP8");

        //new implementation of keyframe
        KeyFrame frame = new KeyFrame(Duration.millis(4), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (rom == null) return;
                try {
                    emu.CPUcycle();
                    if (emu.getDrawFlag()) {
                        screen.redraw();  
                        emu.setDrawFlag(false);              
                    }

                    if (debugState) {
                        stop();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        Timeline clockCycler = new Timeline(frame);
        clockCycler.setCycleCount(Animation.INDEFINITE);


        //build the menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Menu options = new Menu("Options");
        MenuItem loadRom =  new MenuItem("Load Rom");
        loadRom.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
                clockCycler.stop();
                File newRom =  fileChooser.showOpenDialog(primaryStage);
                if (newRom != null) {
                    rom = newRom;
                    reset();
                    clockCycler.play();
                }
			}
        });

        menuBar.getMenus().add(options);
        options.getItems().add(loadRom);


        VBox root = new VBox(menuBar, screen);
        
        Scene mainScene = new Scene(root);
        // Scene mainScene = new Scene(root);
        // primaryStage.setHeight(this.screen.getHeight() + menuBar.getHeight());
        // primaryStage.setWidth(this.screen.getWidth());
        System.out.println(this.screen.getWidth());
        System.out.println(this.screen.getHeight());
        System.out.println(primaryStage.getWidth());
        System.out.println(primaryStage.getHeight());
        primaryStage.setMaxHeight(this.screen.getHeight() + menuBar.getHeight());
        primaryStage.setMaxWidth(this.screen.getWidth());

        mainScene.setOnKeyPressed(this.keyboard);
        mainScene.setOnKeyReleased(this.keyboard);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();

        clockCycler.play();
    }

    //setup the keyboard 
}

// JavaFX Java GUI Tutorial - 8 - Embedding Layouts
