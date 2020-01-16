import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;  
import java.util.Random;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;  

import java.io.IOException;

public class Emulator{
    //init singleton
    public static Emulator INSTANCE = new Emulator();
   
    public static final int SCHIP8_WIDTH = 128;
    public static final int SCHIP8_HEIGHT = 64;
    public static final int CHIP8_WIDTH = 64;
    public static final int CHIP8_HEIGHT = 32;

    public static final int SCHIP8_SPEED = 500;
    public static final int CHIP8_SPEED = 500;
   public static boolean hit  = false;
    Keyboard keyboardObserver;
    Screen screenController;

    private int opcode;

    private int[] memory;
    private int[] V;
    //used for RFL Flags
    private int[] RFL;
    private int pc;
    private int I;
    
    private boolean[] fb;
    private int fbx;
    private int fby;
    private int sound;
    private int delay;

    private boolean canBeep;
    private MediaPlayer beepSound;

    private int[] stack;
    private int sp;

    private boolean drawFlag;
    private boolean[] keyState;

    private boolean debugState;    
    private boolean canStep;

    public Emulator() {
        this.memory = new int[4096];
        this.fb = new boolean[CHIP8_WIDTH * CHIP8_HEIGHT];
        this.stack = new int[16];
        this.V = new int[16];
        this.keyState = new boolean[16];
        this.RFL = new int[8];
        this.fbx = CHIP8_WIDTH;
        this.fby = CHIP8_HEIGHT;
        this.beepSound = new MediaPlayer(new Media(new File("beep.wav").toURI().toString()));
    }
    
    public Emulator(Screen screen, Keyboard keyboard) {
        this();
        this.keyboardObserver = keyboard;
        this.screenController = screen;
    }


    public void load(String file) throws IOException{
        init();
        Path fileLocation = Paths.get(file);
        byte[] data = Files.readAllBytes(fileLocation);
        for (int i = 0; i < data.length; ++i) {
            memory[0x200 + i] = data[i];
        }
        System.out.println("LOADED " + file + " - " + data.length + "b");
    }

    public void init() {
        this.opcode = 0;
        this.I = 0;
        this.sp = -1;
        this.pc = 0x200;
        this.drawFlag = false;
        this.canBeep = false;
        this.fbx = CHIP8_WIDTH;
        this.fby = CHIP8_HEIGHT;
        //init fontset
        for (int i = 0; i < Fontset.FONT_SET_CHIP8.length; i++) {
            this.memory[i] = Fontset.FONT_SET_CHIP8[i];
        }
        for (int i = 0; i < Fontset.FONT_SET_SCHIP8.length; i++) {
            this.memory[i + 80] = Fontset.FONT_SET_SCHIP8[i];
        }
        //init keystate
        for (int i  = 0; i < 0x10; i++) {
            this.keyState[i] = false;
        }
        for (int i = 0; i < 8; i++) {
            this.RFL[i] = 0;
        }
        //init frame buffer
        for (int i = 0; i < this.fbx * this.fby; i++) {
            this.fb[i] = false;
        }
        if (this.screenController != null) this.screenController.init();
    }

    public void setScreen(Screen screen) {
        this.screenController = screen;
    }

    public void fetch() {
        this.opcode = ((this.memory[this.pc] << 8) & 0xff00) | (this.memory[this.pc + 1] & 0xff);
        // this.opcode = 0xDAA0;
    }

    public void decrementTimer() {
        if (this.sound == 0 && this.canBeep) {
            this.beepSound.stop();
            this.beepSound.play();
            this.canBeep = false;
        }
        if (this.sound == 1) this.canBeep = true;
        if (this.sound > 0) this.sound--;
        if (this.delay > 0) this.delay--;
    }

    public void step() throws Exception {
        if ((debugState && canStep) || !debugState) {
            //
            fetch();
            if (debugState) {
                printCurrentOpcode();
            }
            execute();
            decrementTimer();
            if (drawFlag) {
                screenController.redraw();
                drawFlag = false;
            }

            if (debugState) {
                printResultState();
            }
        }
        canStep = false;
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


    public void printCurrentOpcode() {
            System.out.print("0x" + Integer.toHexString(this.pc) + " - ");
            printOpcode(this.opcode);
    }

    public void printResultState() {
        String allRegs = "";
        String allStack = "";
        for (int i =0; i < 0x10; i++) {
            allRegs += "" + intToString(i) + ":0x" + String.format("%02x", V[i]) + "\t";
        }
        for (int i=0; i <= this.sp; i++) {
            allStack += "" + intToString(i) + ":0x" + String.format("%02x", stack[i]) + "\t";
        }
        // System.out.print("Executing: ");
        // printOpcode(this.opcode);
        System.out.println(allRegs);
        System.out.println(allStack);
        System.out.println("I: 0x" + Integer.toHexString(I) + "\tSP: " + this.sp  + "\tPC: 0x" + Integer.toHexString(this.pc));
        System.out.println("DT:" + this.delay + "\tST:" +  this.sound + "\tdrawflag:" + this.drawFlag);
        System.out.print("NEXT: 0x");
        printOpcode(((this.memory[this.pc] << 8) & 0xff00) | (this.memory[this.pc + 1] & 0xff));     
        System.out.println();
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
        return this.fb[this.fbx * y + x];
    }

    public boolean getDrawFlag() {
        return this.drawFlag;
    }

    public int getScreenWidth() {
        return this.fbx;
    }

    public int getScreenHeight() {
        return this.fby;
    }

    public void setDebugState(boolean debugState) {
        this.debugState = debugState;
    }

    public void setCanStep() {
        this.canStep = true;
    }

    public void setDrawFlag(boolean flag) {
        this.drawFlag = flag;
    }

    private char intToString(int hex){
        return "0123456789ABCDEF".toCharArray()[hex];
    }
    
    //Opcodes
    public void execute() throws Exception{
            try {
                switch ((this.opcode >> 12) & 0x000f) {
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }

    //private opcode functions
    private void zero() throws Exception{
        //0x0NNN instruction
        if  ((this.opcode >>> 4) != 0xE && (this.opcode >>> 4) != 0xF && (this.opcode >>> 4) != 0xC) {
            this.pc = (short) (this.opcode & 0x0fff);
            return;
        }

        switch (this.opcode) {
            case 0xE0:
                for (int i = 0; i < this.fb.length; i++) this.fb[i] = false;
                break;
            case 0xEE:
                this.pc = this.stack[this.sp];
                this.sp--;
                break;
            // 00FB: Scroll display 4 pixels right
            case 0xFB:
                for (int y = 0; y < this.fby; y++) {
                    for (int x = this.fbx - 5; x >= 4; x--) {
                        this.fb[y * this.fbx + x] = this.fb[y *  this.fbx + (x - 4)];
                    }
                    for (int x = 0; x < 4; x++) {
                        this.fb[y * this.fbx + x] = false;
                    }
                }
                break;
            //SCHIP8 OPCODES
            // 00FC: Scroll display 4 pixels left
            case 0xFC:
                for (int y = 0; y < this.fby; y++) {
                    for (int x = 0; x < this.fbx - 4; x++) {
                        this.fb[y * this.fbx + x] = this.fb[y *  this.fbx + (x + 4)];
                    }
                    for (int x = this.fbx - 4; x < this.fbx; x++) {
                        this.fb[y * this.fbx + x] = false;
                    }
                }
                break;
            // 00FD: Exit CHIP interpreter
            case 0xFD:
                System.exit(0);
                break;
            case 0xFE:
                // System.out.println("shrink");
                this.fbx = CHIP8_WIDTH;
                this.fby = CHIP8_HEIGHT;
                this.fb =  new boolean[CHIP8_HEIGHT *  CHIP8_WIDTH];
                if (screenController != null) screenController.init();
                break;
            case 0xFF:
                // System.out.println("expand");
                this.fbx = SCHIP8_WIDTH;
                this.fby = SCHIP8_HEIGHT;
                this.fb =  new boolean[SCHIP8_HEIGHT *  SCHIP8_WIDTH];
                if (screenController != null) screenController.init();
                break;
            // 00CN: Scroll display N lines down
            default:
                // System.out.println("scroll down");
                int n = this.opcode & 0x0001;   
                for (int x = 0; x < this.fbx ; x++) {
                    for (int y = this.fby - n - 1; y >= n; y-- ) {
                        this.fb[y * this.fbx + x] = this.fb[(y - n) * this.fbx + x];
                    }
                    for (int y = 0; y < n; y++ ) {
                        this.fb[y * this.fbx + x] = false;
                    }
                }
                ///DO THIS SHT
        }
        this.drawFlag = true;
        this.pc += 2;
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
        if (this.V[(this.opcode >>> 8) & 0xf] != this.V[(this.opcode >>> 4) & 0xf]) this.pc += 2;
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
        //need to implement DXY0
        
        int n = this.opcode & 0x000f; 
        // System.out.println(n);
        int y = this.V[(this.opcode >>> 4) & 0xf];
        int x = this.V[(this.opcode >>> 8) & 0xf];
        int spriteSize = 8;
        boolean turnedOff = false;
        boolean largePrint = false; 
        if  (n == 0)  {
            spriteSize = 16;
            n = 16;
        }

        for (int i = 0; i < n; i++) {
            int spriteRow = memory[I + i];
            if (n == 16) {
                spriteRow = ((memory[I + i * 2] << 8) & 0xff00) | (memory[I + i * 2 + 1] & 0xff);
            }

            for (int j = 0; j < spriteSize; j++) {
                int displayColor = (spriteRow >>> ((spriteSize - 1) - j)) & 0x0001;
                
                //turn VF to true 
                if (displayColor != 0) {
                    int index = ( x + j + (y + i) * this.fbx ) % this.fb.length;
                    if ((displayColor == 1) && this.fb[index]) turnedOff = true;
                    this.fb[index] ^= true;
                }
            }  
            // System.out.println("");   
        }
        // System.out.println("");   

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
        int x = (this.opcode >>> 8) & 0x0000000f;
        switch (this.opcode & 0x00ff) {
            case 0x07:
                this.V[x] = this.delay;
                break;

            case 0x0A:
                boolean keypressed =  false;
                for (int i = 0; i <= 0xf; i++) {
                    if (this.keyState[i]) {
                        this.V[x] = i;
                        keypressed = true;
                    }
                }
                if(keypressed) break;
                else return;
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
                for (int i = 0; i <= x; i++) 
                    this.memory[this.I + i] = this.V[i] & 0xff;
                break;

            case 0x65:
                for (int i = 0; i <= x; i++) 
                    this.V[i] = this.memory[this.I + i] & 0xff;            
                break;
            //SUPER CHIP 8 opcodes
            case 0x30:
                hit = true;
                this.I = this.V[x] * 10 + 80;
                break;
            case 0x75:
                if (x > 7) throw new Exception("Err: 0XF385 x is over 7 for RPL store");
                for (int i = 0; i <= x; i++)
                    this.RFL[i] = this.V[i];
                break;
            case 0x85:
                if (x > 7) throw new Exception("Err: 0XF385 x is over 7 for RPL read");
                for (int i = 0; i <= x; i++)
                    this.V[i] = this.RFL[i];
                break;
            default:
                throw new Exception("Err: F opcode option is not found");
        }
        // if (hit) System.exit(1);
        this.pc += 2;
    }

}