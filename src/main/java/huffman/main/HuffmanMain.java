package huffman.main;

import huffman.gui.HuffmanGUI;
import javax.swing.SwingUtilities;

public class HuffmanMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HuffmanGUI());
    }
}
