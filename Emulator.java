import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class Emulator{
    public static Emulator INSTANCE = new Emulator();

    private short opcode;

    private byte[] memory;
    private short pc;
    private short I;

    
    private boolean[] fb;
    private byte sound;
    private byte delay;
    
    //stack
    private short[] stack;
    private byte sp;

    private Emulator() {
        this.memory = new byte[4096];
        this.fb = new boolean[64 *32];
        this.stack = new short[16];
    }

    public static Emulator getInstance() {
        return INSTANCE;
    }


    public void load() throws IOException{
        Path fileLocation = Paths.get("D:\\emulator\\chip8Github\\Chip8\\roms\\pong.ch8");
        byte[] data = Files.readAllBytes(fileLocation);
        for (int i = 0; i < data.length; ++i) {
            memory[0x200 + i] = data[i];
            // System.out.print(intToString((int) data[i] >> 4));
            // System.out.print(intToString((int) data[i] & 0xF));
            // System.out.println("");
        }
        System.out.println(data.length);
    }

    public void init() {
        this.opcode = 0;
        this.I = 0;
        this.sp = 0;
        this.pc = 0x200;
    }

    public void fetch() {
        this.opcode = (short) (this.memory[this.pc] << 8 | this.memory[this.pc + 1]);
        this.sp += 2;
        System.out.print("0x");
        System.out.print(intToString(opcode >> 12));
        System.out.print(intToString((opcode >> 8) & 0xF));
        System.out.print(intToString((opcode >> 4) & 0xF));
        System.out.print(intToString(opcode & 0xF));
        System.out.println("");
    }

    public void execute() {
        
    }

    public void decrementTimer() {
        if (this.sound == 0) beep();
        if (this.sound >= 0) this.sound--;
        if (this.delay >= 0) this.delay--;
    }

    public void CPUcycle() {
        fetch();
        execute();
        decrementTimer();
    }

    public void drawImages() { 
        System.out.flush();
        for (int i = 0; i < 32; ++i) {
            String line = "";
            for (int j = 0; j < 64 ; ++i) {
                if (this.fb[i * 32 + j]) 
                    line += "*";
                else 
                    line += " ";  
            }
            System.out.println(line);
        }

    }

    //PRIVATE FUNCTIONS
    //prints bytes into the rom
    public void printMem() {
        for (int i = 0x200; i < 0x300; i+=2) {
            int code = (((short) this.memory[i]) << 8) | ((short) this.memory[i + 1] & 0xff);
            // System.out.println("start: " + Integer.toBinaryString(code));
            if (code < 0) code = code & 0xFFFF;
            // System.out.println(this.memory[i] + " " + this.memory[i + 1]);
            System.out.println(code);
            System.out.print("0x");
            System.out.print(intToString(code >> 12));
            System.out.print(intToString((code >> 8) & 0xF));
            System.out.print(intToString((code >> 4) & 0xF));
            System.out.print(intToString(code & 0xF));
            System.out.println("");
            System.out.println(i - 512);
        }
    }

    private char intToString(int hex){
        return "0123456789ABCDEF".toCharArray()[hex];
    }

    private void beep() {
        
    }
}