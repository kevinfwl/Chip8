public class Main {
    public static void main(String[] args) {
        boolean runProgram = true;
        
        try {
            Emulator emu = Emulator.getInstance();
            emu.init();
            emu.load();
            emu.printMem();
            while(runProgram) {
                // emu.executeLoop();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        
    }
}