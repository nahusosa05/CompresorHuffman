package imple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import huffman.def.BitReader;
import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;

public class DescompresorImple implements Descompresor{
	@Override
	public long recomponerArbol(String filename, HuffmanInfo arbol){
		long bytesLeidos=0; // inicializo variable para devolverla
		
		try{
			// abrimos archivo para lectura
			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);

			BitReader br = Factory.getBitReader();
			br.using(bis);

			// itero el encabezado del archivo según las hojas (hs)
			int hs = bis.read();
			
			if(hs == 0) {
				hs = 256;
			}
			
			bytesLeidos++;

			for(int i = 0; i<hs; i++){
				int c = bis.read(); // leo el carácter
				int longi = bis.read(); // leo la longitud del código
				bytesLeidos += 2;

				HuffmanInfo hi = arbol; // creo buffer

				while(longi > 0) { // itero por bit el código huf
					int bit = br.readBit();
					if (bit == 1) { 
					    if (hi.getRight() == null) {
					        hi.setRight(new HuffmanInfo('*', 0)); 
					    }
					    hi = hi.getRight();
					} else { 
					    if (hi.getLeft() == null) {
					        hi.setLeft(new HuffmanInfo('*', 0)); 
					    }
					    hi = hi.getLeft();
					}

					// Verifico si tuve que leer mas de un byte para sumarlo al long
					if((longi-1) != 0 &&(longi-1) % 8 == 0){ 
						bytesLeidos++;
					}
					longi--;
				}
				hi.setC(c);
				bytesLeidos++;
				br.flush();
			}
			bis.close();
			fis.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return bytesLeidos;
	}

	@Override
	public void descomprimirArchivo(HuffmanInfo root, long n, String filename){
		try{
			// abrimos archivo para lectura
			FileInputStream fis=new FileInputStream(filename);
			BufferedInputStream bis=new BufferedInputStream(fis);

			String archivo = filename.replace(".huf", "");
			FileOutputStream fos = new FileOutputStream(archivo);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			BitReader br=Factory.getBitReader();
			br.using(bis);

			for(int i=0; i<n; i++)
			{
				bis.read();
			}
			
			// leo los 4 bytes que representan la longitud original del archivo
            int b1 = bis.read();
            int b2 = bis.read();
            int b3 = bis.read();
            int b4 = bis.read();
            
            // acomodo los 4 bytes en un int usando las operaciones de bit
            // lo que hace esto es ubicar cada byte en su lugar, según el orden de big-endian
            int longOriginal = (b1 << 24 | b2 << 16 | b3 << 8 | b4); 

			/*
			 * Nos posicionamos en la raíz del árbol Huffman. Leemos un bit del
			 * archivo comprimido: Si leímos un 0 descendemos un nivel del árbol
			 * y nos posicionamos en su hijo izquierdo. Si leímos un 1
			 * descendemos un nivel y nos posicionamos en su hijo derecho.
			 * Repetimos el paso 2 hasta llegar a una hoja del árbol, situación
			 * que nos permitirá determinar que logramos decodificar
			 * correctamente un byte. Grabamos el byte decodificado en el
			 * archivo que estamos restaurando.
			 */

			int bitL=br.readBit();
			HuffmanInfo hi=root;
			int longActual = 0;
			
			while(bitL!=-1 && longActual != longOriginal)
			{
				if(hi.getLeft()==null&&hi.getRight()==null)
				{
					int c=hi.getC(); // aca tiene que ser int si no, tira error
					hi=root;
					bos.write(c);
					longActual++;
				}

				if(bitL==0)
				{
					hi=hi.getLeft();
				}
				else
				{
					hi=hi.getRight();
				}

				bitL=br.readBit();
			}

			bos.close();
			fos.close();
			bis.close();
			fis.close();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
}
