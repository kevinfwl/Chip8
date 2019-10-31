import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.io.IOException;

public class Emulator{
    public static Emulator INSTANCE = new Emulator();

    private int opcode;

    private int[] memory;
    private int[] V;
    private int pc;
    private int I;
    
    private boolean[] fb;
    private int sound;
    private int delay;
    
    //stack
    private int[] stack;
    private int sp;

    private boolean drawFlag;
    private boolean[] keyState;

    private Emulator() {
        this.memory = new int[4096];
        this.fb = new boolean[64 *32];
        this.stack = new int[16];
        this.V = new int[16];
        this.keyState = new boolean[16];
    }

    public static Emulator getInstance() {
        return INSTANCE;
    }


    public void load(String file) throws IOException{
        Path fileLocation = Paths.get(file);
        byte[] data = Files.readAllBytes(fileLocation);
        for (int i = 0; i < data.length; ++i) {
            memory[0x200 + i] = data[i];
        }
        System.out.println(data.length);
    }

    public void init() {
        this.opcode = 0;
        this.I = 0;
        this.sp = -1;
        this.pc = 0x200;
        this.drawFlag = false;
        
        //init fonset
        for (int i = 0; i < 80; i++) {
            this.memory[i] = Fontset.FONT_SET[i];
        }
        for (int i  = 0; i < 0x10; i++) {
            this.keyState[i] = false;
        }
    }

    public void fetch() {
        this.opcode = (this.memory[this.pc] << 8 | (this.memory[this.pc + 1] & 0xff));
    }

    public void decrementTimer() {
        if (this.sound == 0) beep();
        if (this.sound > 0) this.sound--;
        if (this.delay > 0) this.delay--;
    }

    public void CPUcycle() throws Exception {
        fetch();
        execute();
        decrementTimer();
        // if (drawFlag) drawImages();
    }

    public void drawImages() { 
        System.out.println("\f");
        System.out.println("------------------------------------------------------------------");

        for (int i = 0; i < 32; ++i) {
            String line = "";
            for (int j = 0; j < 64 ; ++j) {
                if (this.fb[i * 64 + j]) 
                    line += "*";
                else 
                    line += " ";  
            }
            System.out.println("|"+ line + "|");
        }
        System.out.println("------------------------------------------------------------------");
        this.drawFlag = false;
    }

    public void printDebugger() {
        String allRegs = "";
        String allStack = "";
        for (int i =0; i < 0x10; i++) {
            allRegs += "0x" + intToString(i) + ": " + V[i] + "\t";
            allStack += "0x" + intToString(i) + ": " + stack[i] + "\t";
        }
        System.out.print("Executing: ");
        printOpcode(this.opcode);
        System.out.println(allRegs);
        System.out.println(allStack);
        System.out.println("I: 0x" + Integer.toHexString(I) + "\t" + "pc: 0x" + Integer.toHexString(this.pc) + "\t" + "timer:" + this.delay + "\t"  + "sound:" +  this.sound);
        System.out.println("sp: " + this.sp + "\t" + "drawflag: " + this.drawFlag);
        System.out.println();
    }

    //PRIVATE FUNCTIONS
    //prints bytes into the rom
    public void printMem() {
        for (int i = 0x200; i < 0x300; i+=2) {
            int code = ((this.memory[i]) << 8) | (this.memory[i + 1] & 0xff);
            // System.out.println("start: " + Integer.toBinaryString(code));
            if (code < 0) code = code & 0xFFFF;
            printOpcode(code);
        }
    }

    private void printOpcode(int code) {
            if (code < 0) code = code & 0xFFFF;
            System.out.print("0x");
            System.out.print(intToString(code >> 12));
            System.out.print(intToString((code >> 8) & 0xF));
            System.out.print(intToString((code >> 4) & 0xF));
            System.out.print(intToString(code & 0xF));
            System.out.println("");
    }


    public void setKeyOn(int num) {
        this.keyState[num] = true;
    }

    public void setKeyOff(int num) {
        this.keyState[num] = false;
    }

    public boolean getPixel(int x, int y) {
        return this.fb[64 * y + x];
    }

    public boolean getDrawFlag() {
        return this.drawFlag;
    }

    public void setDrawFlag(boolean flag) {
        this.drawFlag = flag;
    }

    private char intToString(int hex){
        return "0123456789ABCDEF".toCharArray()[hex];
    }

    private void beep() {
    }

    public boolean first =  false;

    public void execute() throws Exception{
        // if (this.delay >= -1) {
        //     System.out.print("Executing: ");
        //     printOpcode(this.opcode);
        //     System.out.println("0: " + this.V[0x0] + "           " + "1: " + this.V[0x1]);
        //     System.out.println("delay: " + this.delay);
        // }
        System.out.println("Current pc: 0x"  +  Integer.toHexString(this.pc));
        if (this.V[0xA] != 0) first = true;
        if (this.V[0xA] == 0 && first) System.exit(0);

        switch ((this.opcode >> 12) & 0xf) {
            case 0x0:
                zero();
                break;
            case 0x1:
                one();
                break;
            case 0x2:
                two();
                break;
            case 0x3:
                three();
                break;
            case 0x4:
                four();
                break;
            case 0x5:
                five();
                break;
            case 0x6:
                six();
                break;
            case 0x7:
                seven();
                break;
            case 0x8:
                eight();
                break;
            case 0x9:
                nine();
                break;
            case 0xA:
                A();
                break;
            case 0xB:
                B();
                break;
            case 0xC:
                C();
                break;
            case 0xD:
                D();
                break;
            case 0xE:
                E();
                break;
            case 0xF:
                F();
                break;
        }
        printDebugger();
    }

    //private opcode functions
    private void zero() throws Exception{
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
                throw new Exception("err");
        }
    }


    //opcode decode and execute 
    private void one() {
        this.pc = (this.opcode & 0x0fff);
    }

    private void two() {
        this.stack[++this.sp] = pc;
        this.pc = (this.opcode & 0x0fff);
        // this.drawFlag = true;
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
        this.V[(this.opcode >>> 8) & 0xf] = (this.opcode & 0x00ff);
        this.pc +=2;
    }

    private void seven() {
        this.V[(this.opcode >>> 8) & 0xf] += (this.opcode & 0x00ff);
        if (this.V[(this.opcode >>> 8) & 0xf] >= 256) {
            this.V[(this.opcode >>> 8) & 0xf] -= 256;
        }
        this.pc +=2;
    }

    private void eight() throws Exception {
        int x = (this.opcode >>> 8) & 0xf;
        int y = (this.opcode >>> 4) & 0xf;
        switch (this.opcode & 0x000f) {
            case 0x0:
                this.V[x] = this.V[y];
                break;

            case 0x1:
                this.V[x] = this.V[x] | this.V[y];
                break;

            case 0x2:

                this.V[x] = this.V[x]  & this.V[y];
                break;

            case 0x3:
                this.V[x] = this.V[x] ^ this.V[y];
                break;

            case 0x4:
                int addedValue = this.V[x] + this.V[y];
                if (addedValue > 255) this.V[0xf] = 1;
                else this.V[0xf] = 0;
                this.V[x] = addedValue & 0xff;
                break;

            case 0x5:
                int xsuby = this.V[x] - this.V[y];
                if (xsuby > 0) this.V[0xf] = 1;
                else this.V[0xf] = 0;
                this.V[x] = xsuby & 0xff;
                break;

            case 0x6:
                if ((this.V[x] & 0x1) == 1) this.V[0xf] = 1;
                else this.V[0xf] = 0;
                this.V[x] = this.V[x] >>> 1;
                break;

            case 0x7:
                int ysubx = this.V[y] - this.V[x];
                if (ysubx > 0) this.V[0xf] = 1;
                else this.V[0xf] = 0;
                this.V[x] = ysubx & 0xff;

            case 0xE:
                if (((this.V[x] >>> 15) & 0x1) != 0) this.V[0xf] = 1;
                else this.V[0xf] = 0;
                this.V[x] = (this.V[x] << 1) & 0xff;
                break;
            default:
                throw new Exception("err");
        }
        this.pc += 2;
    }

    private void nine() {
        if (this.V[(this.opcode >>> 12) & 0xf] != this.V[(this.opcode >>> 8) & 0xf]) this.pc += 2;
        this.pc += 2;
    }

    private void A() {
        this.I = (this.opcode & 0x0fff);
        this.pc += 2;
    }

    private void B() {
        this.pc = (((this.opcode & 0x0fff) + this.V[0]) & 0xff);
    }

    private void C() {
        this.V[(this.opcode >>> 8 ) & 0xf] = (new Random().nextInt(256) & (this.opcode & 0xff)) & 0xff;
        this.pc += 2;
    }

    private void D() {
        int n = this.opcode & 0xf; 
        int y = this.V[(this.opcode >>> 4) & 0xf];
        int x = this.V[(this.opcode >>> 8) & 0xf];
        boolean turnedOff = false;

        for (int i = 0; i < n; i++) {
            int spriteRow = memory[I + i];
            
            for (int j = 0; j < 8; j++) {
                int displayColor = (spriteRow >>> (7-j)) & 0x0001;
                System.out.print(displayColor);
                
                //turn VF to true 
                if (displayColor != 0) {
                    int index = ( x + j + (y + i) * 64 ) % 2048;
                    if ((displayColor == 1) && this.fb[index]) turnedOff = true;
                    this.fb[index] ^= true;
                }
            }  
            System.out.println("");        
        }
        this.V[0xf] = turnedOff ? 1 : 0;
        this.drawFlag = true;
        this.pc += 2;
    }

    private void E() {
        int x = (this.opcode >>> 8) & 0xf; 
        switch (this.opcode & 0xff) {
            case 0x9E:
                if (this.keyState[this.V[x]]) pc += 2; 
                break;

            case 0xA1:
                if (!this.keyState[this.V[x]]) pc += 2;
                break;
        }
        pc += 2;
    }

    private void F() throws Exception {
        int x = (this.opcode >>> 8) & 0x000f;
        switch (this.opcode & 0x00ff) {
            case 0x07:
                this.V[x] = this.delay;
                break;

            case 0x0A:
                boolean keypressed =  false;
                for (int i = 0; i < 0xf; i++) {
                    if (this.keyState[i]) {
                        this.V[x] = i;
                        keypressed  = true;
                    }
                }
                if(keypressed) break;
                return;

            case 0x15:
                this.delay = this.V[x];
                break;

            case 0x18:
                this.sound = this.V[x];
                break;

            case 0x1E:
                this.I += this.V[x];
                if(I > 0xFFF) {
                    V[0xF] = 1;
                } else {
                    V[0xF] = 0;
                }
                
                I &= 0xFFF;
                break;

            case 0x29:
                this.I = this.V[x] * 5;
                this.drawFlag = true;
                break;

            case 0x33:
                int num = this.V[x];
                this.memory[this.I + 2] = num % 10;
                num /= 10;
                this.memory[this.I + 1] = num % 10;
                num /= 10;
                this.memory[this.I] = num % 10;
                break;
                
            case 0x55:
                for (int i = 0; i < x; i++) 
                    this.memory[this.I + i] = this.V[i];
                break;

            case 0x65:
                for (int i = 0; i < x; i++) 
                    this.V[i] = this.memory[this.I + i];            
                break;
            default:
                throw new Exception("Err: F");
        }
        this.pc += 2;
    }

}