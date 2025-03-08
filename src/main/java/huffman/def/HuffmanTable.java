package huffman.def;

public class HuffmanTable
{
	private int n; // frecuencia
	private String cod; // codigo para mas adelante

	public int getN()
	{
		return n;
	}
	public void setN(int n)
	{
		this.n=n;
	}
	
	public String getCod()
	{
		return cod;
	}
	public void setCod(String cod)
	{
		this.cod=cod;
	}
	
	public void increment()
	{
		n++;
	}
}
