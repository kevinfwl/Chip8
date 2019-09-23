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

    
    private byte[] fb;
    private byte sound;
    private byte delay;
    
    //stack
    private short[] stack;
    private byte sp;


    private Emulator() {
        this.memory = new byte[4096];
        this.fb = new byte[64 *32];
        this.stack = new short[16];
    }

    public static Emulator getInstance() {
        return INSTANCE;
    }

    public void load() throws IOException{
        Path fileLocation = Paths.get("E:\\Kevin\\emulator\\pong.rom");
        byte[] data = Files.readAllBytes(fileLocation);
        for (int i = 0; i < data.length; ++i) {
            memory[0x200 + i] = data[i];
        }
        System.out.println(data.length);
    }

    public void init() {
        this.opcode = 0;
        this.I = 0;
        this.sp = 0;
        this.pc = 0x200;
    }

    //prints bytes into the rom
    public void printMem() {
        for (int i = 0x200; i < 0x300; i+=2) {
            short opcode = (short) (this.memory[i] << 8 | this.memory[i + 1]);
            if (opcode < 0) opcode ^= 0xFFFF;
            // System.out.println(opcode);
            System.out.print("0x");
            System.out.print(intToString(opcode >> 12));
            System.out.print(intToString((opcode >> 8) & 0xF));
            System.out.print(intToString((opcode >> 4) & 0xF));
            System.out.print(intToString(opcode & 0xF));
            System.out.println("");
            // System.out.println("");
        }

    }

    public char intToString(int hex){
        return "0123456789ABCDEF".toCharArray()[hex];
    }
}