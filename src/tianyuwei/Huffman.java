package tianyuwei;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.PriorityQueue;

public class Huffman {

	public static void main(String[] args) throws IOException {
		
		// read constitution and calculate time for setting up the Huffman tree-------------------------------------------------------------------------------------
		String txt = new String( Files.readAllBytes(Paths.get("/Users/pc/eclipse-workspace/CS370Huffman/src/constitution.txt")));
		
		PriorityQueue<HuffNode> Tree = new PriorityQueue<>();
		
		long start = System.nanoTime();
		
		setHuffmanTree(Tree, txt);
		
		long end = System.nanoTime();
		
		System.out.println("Time for Thread set up a huffman tree: " + (end- start)+" ns");
		
		HashMap<Character, String> compress = new HashMap<>();
		
		//Get the compressed file from the local address, and put them into FileOutputStream------------------------------------------------------------------------
		
		FileOutputStream compressText = new FileOutputStream("/Users/pc/eclipse-workspace/CS370Huffman/src/Compressed.txt");
		
	    start = System.nanoTime();
		setCompressedTxt(compress, new StringBuffer(), Tree.peek());
		
		//set up a string builder
		
		StringBuffer encodeConst = new StringBuffer();
		
		for(int i=0; i < txt.length(); i++)
		{
			encodeConst.append(compress.get(txt.charAt(i)));
		}
		compressText.write(encodeConst.toString().getBytes());
		compressText.close();  // close FileOutputStream
		
		end = System.nanoTime();
		// print out the encode of time
		System.out.println("Time for Thread Encoding of file: " + (end-start)+ " ns");     
		System.out.println("Original Size: " + txt.length()+ " bytes ");              //The answer for question a
        System.out.println("After compressed Size: "+ encodeConst.toString().length()/8.0  + " bytes");  //
        System.out.printf("The percentage of compressed: %.2f%%. " , 100- (encodeConst.toString().length()/8.0)/txt.length()*100);  
	}

	public static void setHuffmanTree(PriorityQueue<HuffNode> tree, String constitution)
	{
		int [] charHuff = new int[256];
		for (char i : constitution.toCharArray()) {
            charHuff[i] += 1;
        }
		
		for(int i =0; i<charHuff.length;i++)
		{
			if(charHuff[i] >0)
			{
			tree.offer(new Node(charHuff[i],(char)i));	
			}
		}
		
	   while(tree.size()>1) {
		   
		   HuffNode n1 = tree.poll();
		   HuffNode n2 = tree.poll();
		   assert n2 !=null;
		    tree.offer(new HuffNode(n1,n2));
		   
	   }
		
		
	}
   	
	
	public static void setCompressedTxt(HashMap<Character, String> mp, StringBuffer compressed, HuffNode current) {
        if (current instanceof Node) {
            mp.put(((Node) current).letter, compressed.toString());

        }   
        else if (current != null) {
            
            compressed.append('0');
            setCompressedTxt(mp, compressed, current.leftN);

            
            compressed.deleteCharAt(compressed.length() - 1);

            
            compressed.append('1');
            setCompressedTxt(mp, compressed, current.rightN);

            
            compressed.deleteCharAt(compressed.length() - 1);
        }
    }

}

