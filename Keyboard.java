import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.Map;
import java.util.HashMap;

public class Keyboard implements EventHandler<KeyEvent> {
    /**
     * keyboard layout for the to f is in the following order
     * 
     * |  1(1) |  2(2)  |  3(3)  |  4(C)  |
     * |  Q(4) |  W(5)  |  E(6)  |  R(D)  |
     * |  A(7) |  S(8)  |  D(9)  |  F(E)  |
     * |  Z(A) |  X(0)  |  C(B)  |  V(F)  |
     */

    public static Map<KeyCode, Integer> keyMap;
    private Emulator emu;

    static {
        keyMap = new HashMap<KeyCode, Integer>(){
            //call initializer on anonymous subclass to init the keymap (Hashmaps doesn't have init functions)
            {
                put(KeyCode.DIGIT1, 1);
                put(KeyCode.DIGIT2, 2);
                put(KeyCode.DIGIT3, 3);
                put(KeyCode.Q, 4);
                put(KeyCode.W, 5);
                put(KeyCode.E, 6);
                put(KeyCode.A, 7);
                put(KeyCode.S, 8);
                put(KeyCode.D, 9);
                put(KeyCode.X, 0);
                put(KeyCode.Z, 0xA);
                put(KeyCode.C, 0xB);
                put(KeyCode.DIGIT4, 0xC);
                put(KeyCode.R, 0xD);
                put(KeyCode.F, 0xE);
                put(KeyCode.V, 0xF);
            }
        };
    }

    public Keyboard() {
        this.emu = null;
    }

    public Keyboard(Emulator emu) {
        this.emu = emu;
    }

    @Override
    public void handle(KeyEvent e) {
        if (this.emu == null) return;
        try  {
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                if (keyMap.get(e.getCode()) != null)
                    this.emu.setKeyOn(keyMap.get(e.getCode()));
            }
    
            if (e.getEventType() ==  KeyEvent.KEY_RELEASED) {
                if (keyMap.get(e.getCode()) != null)
                    this.emu.setKeyOff(keyMap.get(e.getCode()));
            }
        }
        catch (Exception exception) {
        }
    }
}