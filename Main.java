public class Main {
    public static void main(String[] args) {
        boolean runProgram = true;
        
        try {
            Emulator emu = Emulator.getInstance();
            emu.init();
            emu.load();
            emu.printMem();
            while(runProgram) {
                emu.CPUcycle();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        
    }
}

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