package huffman.gui;

import javax.swing.*;
import java.util.List;
import huffman.def.Compresor;
import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import imple.Factory;
import java.awt.*;
import java.io.File;

public class HuffmanGUI extends JFrame {
    Compresor c = Factory.getCompresor();
    Descompresor d = Factory.getDescompresor();
    static JProgressBar bar = new JProgressBar(0, 100); // Barra de progreso

    public HuffmanGUI() {
        setTitle("Compresor de Archivos Huffman");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(400, 300)); 
        setBackground(new Color(117, 226, 97));
        startComponents();

        setVisible(true);
    }

    private void startComponents() {
        JPanel fondoPanel = new JPanel(new GridBagLayout());
        fondoPanel.setBackground(new Color(220, 220, 220)); 
        
        JPanel panelInterno = new JPanel(new GridBagLayout());
        panelInterno.setBackground(Color.WHITE); 
        panelInterno.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; 

        JLabel texto = new JLabel("Seleccione un archivo para comprimir o descomprimir");
        texto.setFont(new Font("Roboto", Font.BOLD, 18));

        JButton buscador = new JButton(" CLICK AQUÍ ");
        buscador.setFont(new Font("Roboto", Font.PLAIN, 18));
        buscador.setBackground(Color.GRAY);
        buscador.setForeground(Color.BLACK);
        buscador.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel info = new JLabel("📝 Solo archivos .huf pueden ser descomprimidos.");
        info.setFont(new Font("Roboto", Font.ITALIC, 14));

        buscador.addActionListener(e -> seleccionarArchivo());

        // Configurar barra de progreso
        bar.setStringPainted(true);  // Mostrar el porcentaje
        bar.setPreferredSize(new Dimension(300, 20));
        bar.setValue(0);

        // Agregar componentes
        panelInterno.add(texto, gbc);
        gbc.gridy++;
        panelInterno.add(buscador, gbc);
        gbc.gridy++;
        panelInterno.add(info, gbc);
        gbc.gridy++;
        panelInterno.add(bar, gbc); // Agregar la barra de progreso

        fondoPanel.add(panelInterno, gbc);
        add(fondoPanel);
    }

    private void seleccionarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            // Ejecutar en segundo plano para no bloquear la GUI
            new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() {
                    bar.setValue(0); // Iniciar barra de progreso
                    bar.setIndeterminate(true); // Modo de carga indefinido

                    if (filePath.endsWith(".huf")) {
                        descompresion(filePath);
                    } else {
                        compresion(filePath);
                    }

                    return null;
                }

                @Override
                protected void done() {
                    bar.setIndeterminate(false);
                    bar.setValue(100); // Completar la barra
                    JOptionPane.showMessageDialog(null, filePath.endsWith(".huf") 
                        ? "Descompresión exitosa." + "\n" + "Ruta del archivo nuevo: " + filePath.replace(".huf", "")
                        : "Compresión exitosa." + "\n" + "Ruta del archivo nuevo: " + filePath + ".huf"
                    );
                    bar.setValue(0);
                }
            }.execute();
        }
    }

    private static void compresion(String filename){
        Compresor comp = Factory.getCompresor();

        bar.setValue(14);

        HuffmanTable arr[] = comp.contarOcurrencias(filename);

        bar.setValue(28);

        List<HuffmanInfo> lista = comp.crearListaEnlazada(arr);

        bar.setValue(42);

        HuffmanInfo raiz = comp.convertirListaEnArbol(lista);

        bar.setValue(56);

        comp.generarCodigosHuffman(raiz, arr);

        bar.setValue(70);

        comp.escribirEncabezado(filename, arr);

        bar.setValue(84);

        comp.escribirContenido(filename, arr);
    }

    private static void descompresion(String filename){
        bar.setValue(20);

        Descompresor d = Factory.getDescompresor();

        bar.setValue(40);

        HuffmanInfo arbol = new HuffmanInfo('*', 0);

        bar.setValue(60);

        long n = d.recomponerArbol(filename, arbol);

        bar.setValue(80);

        d.descomprimirArchivo(arbol, n, filename);
    }
}
