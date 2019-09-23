public class Main {
    public static void main(String[] args) {
        try {
            Emulator emu = Emulator.getInstance();
            emu.init();
            emu.load();
            emu.printMem();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}