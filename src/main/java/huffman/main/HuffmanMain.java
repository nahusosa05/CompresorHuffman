package huffman.main;

import huffman.gui.HuffmanGUI;
import javax.swing.SwingUtilities;

/* 
*	SwingUtilities.invokeLater(() -> new HuffmanGUI());
*	Garantiza que la GUI se cree y actualice correctamente en el hilo de eventos de Swing (evita problemas de concurrencia).
*	Es una buena práctica en Java Swing para mantener la aplicación responsiva
*/

public class HuffmanMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HuffmanGUI());
    }
}
