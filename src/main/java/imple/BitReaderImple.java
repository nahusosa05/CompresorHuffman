package imple;

import java.io.IOException;
import java.io.InputStream;

import huffman.def.BitReader;

public class BitReaderImple implements BitReader
{
    private InputStream mIs;
    private int count;
    private String bait;
    private boolean eof = false;
    
    @Override
    public void using(InputStream is) {
        mIs = is;
        count = 8;
    }

    @Override
    public int readBit() {
        int out;
        if(count==8) {
            leerMIs();
            if (eof) {
                out = -1;
            }
            else {
                out=bait.charAt(count)-'0';
                count++;
            }
        }
        else {
            out=bait.charAt(count)-'0';
            count++;
        }
        return out;
    }
    
    @Override
    public void flush()
    {
        count=8;
    }
    
    private void leerMIs() {
        int r = 0;
        try {
            r=mIs.read();
            if (r == -1) {
                eof = true;
            }
            else {
                bait = Integer.toString(r,2);
                bait = String.format("%8s",bait).replace(' ','0');
                count = 0;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }        
    }
}