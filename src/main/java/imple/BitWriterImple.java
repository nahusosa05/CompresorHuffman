package imple;

import java.io.IOException;
import java.io.OutputStream;

import huffman.def.BitWriter;

public class BitWriterImple implements BitWriter {
    private OutputStream mOs;
    private int count = 0;
    private String bait = "";
    
    @Override
    public void using(OutputStream os)
    {
        mOs = os;
    }

    @Override
    public void writeBit(int bit) {
        bait+=bit;
        count++;
        if (count == 8) {
            int b = Integer.parseInt(bait,2);
            try {
                mOs.write(b);
                count = 0;
                bait = "";
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
        
    @Override
    public void flush() {
        if(count>0) {
            bait=String.format("%-8s",bait).replace(' ','0');
            int b=Integer.parseInt(bait,2);
            try {
                mOs.write(b);
                count=0;
                bait="";
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}