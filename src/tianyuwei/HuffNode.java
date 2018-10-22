package tianyuwei;

public class HuffNode implements Comparable<HuffNode> {

	public int total;
	public  HuffNode leftN;
	public  HuffNode rightN;
	
	public HuffNode()
	{
		
	}
	
	public HuffNode(HuffNode left, HuffNode right)
	{
		this.total = left.total + right.total;
		this.leftN = left;
		this.rightN = right;
	}
	
	@Override
	public int compareTo(HuffNode N)
	{
		return total - N.total;
	}
	
	
	
}
