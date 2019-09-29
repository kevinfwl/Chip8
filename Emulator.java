import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class Emulator{
    public static Emulator INSTANCE = new Emulator();

    private short opcode;

    private byte[] memory;
    private int[] V;
    private short pc;
    private short I;

    
    private boolean[] fb;
    private byte sound;
    private byte delay;
    
    //stack
    private short[] stack;
    private byte sp;

    private boolean drawFlag;

    private Emulator() {
        this.memory = new byte[4096];
        this.fb = new boolean[64 *32];
        this.stack = new short[16];
        this.V = new int[16];
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
        this.drawFlag = false;
    }

    public void fetch() {
        this.opcode = (short) (this.memory[this.pc] << 8 | this.memory[this.pc + 1]);
    }

    public void execute() {
        switch ((this.opcode >> 12) & 0xf) {
            case 0x0:

                break;


            case 0x1:
                break;


            default:
                break;
        }
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



    //private opcode functions
    private void zero() {
        switch (this.opcode) {
            case 0x00E0:
                for ( boolean pixel : this.fb) {
                    pixel = false;
                }
                this.drawFlag = true;
                this.pc += 2;
                return;
            case 0x00EE:
                this.pc = this.stack[this.sp];
                this.sp--;
                this.pc += 2;
                this.drawFlag = true;
                return;
            default:
                this.pc = (short) (this.opcode & 0x0fff);
                return;
        }
    }

    private void one() {
        this.pc = (short) (this.opcode & 0x0fff);
    }

    private void two() {
        this.stack[++this.sp] = pc;
        this.pc = (short) (this.opcode & 0x0fff);
        this.drawFlag = true;
    }

    private void three() {
        if (this.V[(this.opcode >>> 8) & 0xf] == (this.opcode & 0x00ff)) pc += 2;
        pc += 2;
    }

    private void four() {
        if (this.V[(this.opcode >>> 8) & 0xf] != (this.opcode & 0x00ff)) pc += 2;
        pc += 2;
    }

    private void five() {
        if (this.V[(this.opcode >>> 8) & 0xf] == this.V[(this.opcode >>> 4) & 0xf]) pc += 2;
        pc += 2;
    }

    private void six() {
        this.V[(this.opcode >>> 8) & 0xf] = (byte) (this.opcode & 0x00ff);
        this.pc +=2;
    }

    private void seven() {
        this.V[(this.opcode >>> 8) & 0xf] += (byte) (this.opcode & 0x00ff);
        this.pc +=2;
    }

    private void eight() {
        byte Vx = this.V[(this.opcode >>> 8) & 0xf];
        byte Vy = this.V[(this.opcode >>> 4) & 0xf];
        switch (this.opcode & 0xf) {
            case 0x0:
                this.V[(this.opcode >>> 8) & 0xf] = this.V[(this.opcode >>> 4) & 0xf];
                break;
            case 0x1:
            this.V[(this.opcode >>> 8) & 0xf] this.V[(this.opcode >>> 8) & 0xf] | this.V[(this.opcode >>> 4) & 0xf];
            case 0x2:

                break;
            case 0x3:
                break;
            case 0x4:
                break;
            case 0x5:
                break;
            case 0x6:
                break;
            case 0x7:
                break;
            case 0xE:
                break;
            default:
        }
        this.pc += 2;
    }

    private void nine() {
        if (this.V[(this.opcode >>> 12) & 0xf] != this.V[(this.opcode >>> 8) & 0xf]) this.pc += 2;
        this.pc += 2;
    }

    private void A() {
        
    }
}