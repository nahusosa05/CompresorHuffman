package huffman.def;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

import imple.CompresorImple;
import imple.DescompresorImple;
import imple.Factory;

public class CompresorTest
{
	//@Test
	public void testContarOcurrencias()
	{
		try(FileInputStream fis = new FileInputStream("beegees.txt"))
		{
			Compresor c = Factory.getCompresor();
			HuffmanTable arr[] = c.contarOcurrencias("beegees.txt");
			
			assertEquals(4,arr['A'].getN());
			assertEquals(3,arr['B'].getN());
			assertEquals(2,arr['C'].getN());
			assertEquals(1,arr['D'].getN());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	//@Test
	public void testCrearListaEnlazada(){
		// Simulación de datos de entrada. En un test, generalmente se simula un
		// array
		HuffmanTable[] arr=new HuffmanTable[256];

		// Suponiendo que ya tienes datos en arr con caracteres y sus
		// frecuencias
		arr['A']=new HuffmanTable();
		arr['A'].setN(4);
		arr['B']=new HuffmanTable();
		arr['B'].setN(3);
		arr['C']=new HuffmanTable();
		arr['C'].setN(2);
		arr['D']=new HuffmanTable();
		arr['D'].setN(1);

		// Creamos el compresor y obtenemos la lista ordenada
		Compresor c = Factory.getCompresor();
		List<HuffmanInfo> lista=c.crearListaEnlazada(arr);

		// Verificamos que la lista tenga los elementos agregados
		assertEquals(4,lista.size()); // Debería tener 4 elementos

		// Verificamos que la lista esté ordenada correctamente (por frecuencia
		// de menor a mayor)
		assertEquals('D',lista.get(0).getC()); // D (1)
		assertEquals('C',lista.get(1).getC()); // C (2)
		assertEquals('B',lista.get(2).getC()); // B (3)
		assertEquals('A',lista.get(3).getC()); // A (4)

		// Verificamos las frecuencias
		assertEquals(1,lista.get(0).getN()); // D
		assertEquals(2,lista.get(1).getN()); // C
		assertEquals(3,lista.get(2).getN()); // B
		assertEquals(4,lista.get(3).getN()); // A
	}
	
	//@Test
    public void testConvertirListaEnArbol() {
        // Crear un compresor
        CompresorImple compresor = new CompresorImple();

        // Simular la tabla de frecuencias
        HuffmanTable[] tabla = new HuffmanTable[256];
        tabla['A'] = new HuffmanTable();
        tabla['A'].setN(4);
        tabla['B'] = new HuffmanTable();
        tabla['B'].setN(3);
        tabla['C'] = new HuffmanTable();
        tabla['C'].setN(2);
        tabla['D'] = new HuffmanTable();
        tabla['D'].setN(1);

        // Crear la lista enlazada desde la tabla
        List<HuffmanInfo> lista = compresor.crearListaEnlazada(tabla);

        // Convertir la lista en un árbol de Huffman
        HuffmanInfo raiz = compresor.convertirListaEnArbol(lista);

        // La raíz del árbol debe contener la suma total de las frecuencias
        assertEquals(10, raiz.getN()); // 4+3+2+1 = 10
    }
	
	public void Prueba(){
		try(FileInputStream fis = new FileInputStream("prueba.txt")){
			Compresor c = Factory.getCompresor();
			
			HuffmanTable arr[] = c.contarOcurrencias("prueba.txt");
			
			List<HuffmanInfo> lista = c.crearListaEnlazada(arr);
			
			HuffmanInfo raiz = c.convertirListaEnArbol(lista);
			
			assertEquals(39, raiz.getN());
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void Prueba2(){
		try(FileInputStream fis = new FileInputStream("prueba2.txt")){
			Compresor c = Factory.getCompresor();
			
			HuffmanTable arr[] = c.contarOcurrencias("prueba2.txt");
			
			List<HuffmanInfo> lista = c.crearListaEnlazada(arr);
			
			HuffmanInfo raiz = c.convertirListaEnArbol(lista);
			
			c.generarCodigosHuffman(raiz,arr);
			
			assertEquals("1", arr['A'].getCod());    // A debería tener código "0"
            assertEquals("01", arr['B'].getCod());   // B debería tener código "10"
            assertEquals("000", arr['C'].getCod());  // C debería tener código "110"
            assertEquals("001", arr['D'].getCod());  // D debería tener código "111"
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	
    void testEscribirEncabezado() throws IOException {
		try(FileInputStream fis = new FileInputStream("prueba.txt.huf")){
			Compresor c = Factory.getCompresor();
			
			HuffmanTable arr[] = c.contarOcurrencias("prueba.txt");
			
			List<HuffmanInfo> lista = c.crearListaEnlazada(arr);
			
			HuffmanInfo raiz = c.convertirListaEnArbol(lista);
			
			c.generarCodigosHuffman(raiz,arr);

	        long x = c.escribirEncabezado("prueba.txt", arr);
	        assertEquals(41, x);
			
	        // 📖 Leer los bytes del archivo "prueba.txt"
	        byte[] fileContent = fis.readAllBytes();
	        
	        // iteramos el array
	        for(int i = 0; i<fileContent.length ; i++) {
	        	// buscamos el valor de ascii N para la prueba que estabamos buscando
	        	if(fileContent[i] == 78) {
	        		// ✅ Verificacion de las hojas
	    	        assertEquals(12, fileContent[0]); 
	    	      
	    	        assertEquals(5, fileContent[i+1]);   // ✅ Longitud del código Huffman
	    	        assertEquals(0b10110000, fileContent[i+2]& 0xFF); // ✅ Código Huffman 	    	      
	    	        
	    	        // agregarle el 0xFF hace que los valores que escribes 
	    	        // son mayores que 127, no se interpreten como números negativos 
	    	        // cuando se leen. (10110000 = 176 que es mayor que 127)
	        	}
	        }
	        fis.close();
		}
	}
		
	@Test
	void testCompresion() throws IOException{
		Compresor c = Factory.getCompresor();

		HuffmanTable arr[] = c.contarOcurrencias("prueba.wav");

		List<HuffmanInfo> lista = c.crearListaEnlazada(arr);

		HuffmanInfo raiz = c.convertirListaEnArbol(lista);

		c.generarCodigosHuffman(raiz,arr);

		c.escribirEncabezado("prueba.wav",arr);

		c.escribirContenido("prueba.wav",arr);
		
		File compressed = new File("prueba.wav.huf");
		assertTrue(compressed.exists());
	}
	
	@Test
	void testDescomprecion() throws IOException {
	    Descompresor d = Factory.getDescompresor();
	    String filename = "prueba.wav.huf";

	    File compressed = new File(filename);
	    assertTrue(compressed.exists()); // Asegura que el archivo existe antes de continuar

	    HuffmanInfo arbol = new HuffmanInfo('*', 0);
	    long n = d.recomponerArbol(filename, arbol);
	    d.descomprimirArchivo(arbol, n, filename);

	    File decompressed = new File("prueba.wav"); // Asegúrate del nombre correcto del archivo descomprimido
	    assertTrue(decompressed.exists()); // Verifica que la descompresión se realizó correctamente
	}
	
	 //@Test
	 void testRecomponerArbol() throws IOException {
	        // Creamos la instancia del descompresor
	        DescompresorImple descompresor = new DescompresorImple();
	        HuffmanInfo arbol = new HuffmanInfo('*', 0);

	        // Ejecutamos recomponerArbol
	        long bytesLeidos = descompresor.recomponerArbol("prueba.txt.huf", arbol);

	        // 🔹 Verificamos que se leyeron los bytes esperados
	        //prueba "AAAABBBCCD"
	        assertEquals(13, bytesLeidos, "La cantidad de bytes leídos no es la esperada");
	    }
}
