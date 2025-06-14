package imple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import huffman.def.BitWriter;
import huffman.def.Compresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import huffman.util.HuffmanTree;

public class CompresorImple implements Compresor{
	// Cuenta las veces que aparece cada byte (carácter) en un archivo. Lo devuelve en un array
	@Override
	public HuffmanTable[] contarOcurrencias(String filename){
		//creamos la tabla para contar todas las ocurrencias de los chars
        HuffmanTable[] out = new HuffmanTable[256];
        
        try { 
        	// abrimos el archivo a leer 
            FileInputStream fis = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            int bait = bis.read();

            // Recorremos el archivo
            while (bait != -1){
            	// si en ese espacio no hay nada, creamos un nuevo huffman table
                if (out[bait] == null) { 
                    out[bait] = new HuffmanTable(); 
                    out[bait].increment(); // e incrementamos en uno su contador
                }
                else {	// si no, directamente incrementamos su contador
                    out[bait].increment();
                }
                bait = bis.read();
            }
            bis.close();
            fis.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return out;
    }
	
	// Convierte la tabla en una lista ordenada
	@Override
	public List<HuffmanInfo> crearListaEnlazada(HuffmanTable[] arr){
		List<HuffmanInfo> info = new ArrayList<>();
		
		for(int i = 0; i<arr.length ; i++) {
			HuffmanTable ht = arr[i];
			
			if(ht != null && ht.getN() > 0) {
				HuffmanInfo nodo = new HuffmanInfo(i,arr[i].getN());
				info.add(nodo);
			}
			Collections.sort(info, Comparator.comparingInt(HuffmanInfo::getN));
		}
		return info;
	}
	
	// Construcción del árbol
	@Override
    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista){
        while(lista.size()>1){
            HuffmanInfo nodo1 = lista.remove(0);
            HuffmanInfo nodo2 = lista.remove(0);

            HuffmanInfo nodoPadre = new HuffmanInfo('*',nodo2.getN()+ nodo1.getN());

            nodoPadre.setLeft(nodo2);
            nodoPadre.setRight(nodo1);

            lista.add(nodoPadre);

            Comparator<HuffmanInfo> cmp =(a, b) -> Integer.compare(a.getN(),b.getN());
            Collections.sort(lista,cmp);
        }
        return lista.get(0);
    }
	
	// Genera los códigos huffman según el orden (0 izq [mayor freq], 1 derecha [menor freq])
	@Override
    public void generarCodigosHuffman(HuffmanInfo root, HuffmanTable[] arr){
        HuffmanTree ht = new HuffmanTree(root);
        
        StringBuffer cod = new StringBuffer();
        
        HuffmanInfo hi = ht.next(cod);
        
        while (hi != null){
            arr[hi.getC()].setCod(cod.toString());
            
            hi = ht.next(cod);
        }
    }
	
	// encabezado con la estructura: (cant hojas, t registros, long original)
	@Override
	public long escribirEncabezado(String filename, HuffmanTable[] arr){
		long bytesEscritos = 0;
		try {
			// abrimos el archivo a escribir y lo bufereamos
            FileOutputStream fos = new FileOutputStream(filename + ".huf");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            // obtenemos bitWriter
            BitWriter bt = Factory.getBitWriter();
            bt.using(bos);
            
            // creamos un map para guardar los char con sus respectivos 
            // huffmantable (frecuencia y código)
            HashMap<Integer,HuffmanTable> hm = new HashMap<>();
            int longOriginal = 0;
            
            // iteramos el array para obtener los huffmantable distinto de null de arr y los
            // añadimos al map, contando cada frecuencia para la long original.
            for(int i = 0; i<arr.length; i++){
            	HuffmanTable ht = arr[i];
            	if(ht != null) {
            		hm.put(i,ht);
            		
            		longOriginal += ht.getN();
            	}
            }
            
            // escribimos las hojas de árbol con el size de los caracteres distintos
            byte hojas = (byte)hm.size();
            
            if(hojas == 256) {
            	bos.write(0);
            }
            else {
            	bos.write(hojas);
            }
            
            bytesEscritos += 1;
            
            //for para iterar según todas las claves del map y obtener su valor asociado(cod)
            for(int key : hm.keySet()) {
            	HuffmanTable ht = hm.get(key);
            	byte codLenght = (byte)ht.getCod().length();
            	
            	bos.write((byte)key);
            	bytesEscritos += 1;
            	bos.write(codLenght);
            	bytesEscritos += 1;
            	
            	
            	//while para escribir el código huffman bit por bit
            	int i = 0;
                while (i<codLenght){
                	int bit = ht.getCod().charAt(i) - '0';
                    bt.writeBit(bit);
                    if ((i+1)%8 == 0){
                        bytesEscritos++;
                    }
                    i++;
                }
                if (i%8 != 0){
                    bt.flush();
                    bytesEscritos++;
                }
            }
            
            /* 
             	Escribimos longOriginal del archivo, byte por byte
             	lo hacemos de esta manera por el orden Big-Endian. 
             	(escribir el byte más significativo primero y el menos significativo al final)
            */            
            
            bos.write((longOriginal >> 24) & 0xFF);
            bos.write((longOriginal >> 16) & 0xFF);
            bos.write((longOriginal >> 8)  & 0xFF);
            bos.write(longOriginal & 0xFF);
            bytesEscritos += 4;    
            
            bos.close();
            fos.close();
		}
		catch(IOException e){
            e.printStackTrace();
        }
		return bytesEscritos;
	}
	
	@Override
	public void escribirContenido(String filename, HuffmanTable[] arr) {
		try{
			// abrimos el archivo (copia archivo comprimido) a escribir con el append 
			// en true para escribir al final del archivo.
            FileOutputStream fos = new FileOutputStream(filename + ".huf",true);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            // abrimos archivo original
            FileInputStream fis = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            // obtenemos bitWriter
            BitWriter bt = Factory.getBitWriter();
            bt.using(bos);
            
            int byteLeido = bis.read();
            while(byteLeido != -1) {
            	HuffmanTable ht = arr[byteLeido];
            	String cod = ht.getCod();
            	
            	for(int i = 0; i<cod.length(); i++){
            		bt.writeBit(cod.charAt(i) - '0');
            	}
            	
            	byteLeido = bis.read();
            }
            bt.flush();
            
            bos.close();
            bis.close();
            fos.close();
            fis.close();
		}
		catch(IOException e){
            e.printStackTrace();
        }
	}
}
