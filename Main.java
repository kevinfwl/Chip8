import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

// public class Main extends JFrame  {
    // public static void main(String[] args) {
    //     boolean runProgram = true;
        
    //     try {
    //         Emulator emu = Emulator.getInstance();
    //         emu.init();
    //         emu.load("D:\\emulator\\chip8Github\\Chip8\\roms\\pong.ch8");
    //         emu.printMem();
    //         while(runProgram) {
    //             emu.CPUcycle();
    //         }
    //     }
    //     catch(Exception e) {
    //         e.printStackTrace();
    //     }
    // }


// }

//D:\\emulator\\chip8Github\\Chip8\\roms\\ibm.ch8
//C:\personal\Chip8\roms\ibm.ch8

// import javax.swing.*;
// class Main {
//     public static void main(String args[]){
//        JFrame frame = new JFrame("My First GUI");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1280,640);
//        JButton button = new JButton("Press");
//        frame.getContentPane().add(button); // Adds Button to content pane of frame



//        frame.setVisible(true);
//     }
// }



public class Main extends JPanel {
    
    private Emulator emu;

    public Main(Emulator emu) {
        super();
        this.emu = emu;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
  
  
      for (int x = 0; x < 64; x++) {
          for (int y = 0; y < 32; y++) {
              if(this.emu.getPixel(x, y)) {
                g2d.setColor(Color.BLACK);
              }
              else {
                g2d.setColor(Color.WHITE);
              }
              g2d.drawRect(x * 10, y * 10, 10, 10);]
              repaint();
          }
      }
  
    }
  
    public static void main(String[] args) {


        try {
            Emulator emu = Emulator.getInstance();
            emu.init();
            emu.load("D:\\emulator\\chip8Github\\Chip8\\roms\\pong.ch8");
            emu.printMem();

            Main rects = new Main(emu);
            JFrame frame = new JFrame("Rectangles");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(rects);
            frame.setSize(640, 320);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            while(true) {
                emu.CPUcycle();
                if (emu.getDrawFlag()) {
                    frame.setVisible(false);
                    frame.revalidate();
                    frame.setVisible(true);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
  }