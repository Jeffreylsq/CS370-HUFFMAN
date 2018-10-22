package tianyuwei;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.Phaser;


public class PHuffmanCoding {
    public static void main(String[] args) throws IOException, InterruptedException {

        String txt = new String(Files.readAllBytes(Paths.get("/Users/pc/eclipse-workspace/CS370Huffman/src/constitution.txt")));

        
        PriorityQueue<HuffNode> huffTree = new PriorityQueue<>();

        setHuffmanTree(huffTree, txt);

        HashMap<Character, String> compressedChar = new HashMap<>();


        FileOutputStream compressedText = new FileOutputStream("/Users/pc/eclipse-workspace/CS370Huffman/src/compressed.txt");

        setCompressedTxt(compressedChar, new StringBuffer(), huffTree.peek());


         // coding begins--------------------------------------------------------------
        long start = System.nanoTime();
        int num = 3;
        Phaser ph = new Phaser(num);

        StringBuffer[] encoded = new StringBuffer[num];

        for (int i = 0, j = 0, k = txt.length() / num; i < encoded.length; i++, j += k) {
            encoded[i] = new StringBuffer();

            if ((j + k) > txt.length()) {
                new PHuffmanCoding().compressString(ph, compressedChar, encoded[i], txt.substring(j));
            }
            else {
                new PHuffmanCoding().compressString(ph, compressedChar, encoded[i], txt.substring(j, j + k));
            }
        }

        // Wait----------------------------------------------------------------
        Thread.sleep(5000);

        StringBuffer encode = new StringBuffer();

        for (StringBuffer s : encoded) {
            encode.append(s);
        }
        compressedText.write(encode.toString().getBytes());
        compressedText.close();

        long end = System.nanoTime();

        System.out.println("Time for Parallel Thread Encoding : " + ((end-start)*1e-6  - 5000) + " ms");
    }

    public static void setHuffmanTree(PriorityQueue<HuffNode> Tree, String constitution) {
        
        int[] char1 = new int[256];
        for (char i : constitution.toCharArray()) {
            char1[i] += 1;
        }

        for (int i = 0; i < char1.length; i++) {
            if (char1[i] > 0) {
                Tree.offer(new Node(char1[i], (char) i));
            }
        }

        
        while (Tree.size() > 1) {

            
            HuffNode n1 = Tree.poll();
            HuffNode n2 = Tree.poll();
            assert n2 != null;
            Tree.offer(new HuffNode(n1, n2));
        }
    }

  
    public static void setCompressedTxt(HashMap<Character, String> hm, StringBuffer compressedStr, HuffNode current) {
        if (current instanceof Node) {
            hm.put(((Node) current).letter, compressedStr.toString());


        }
        else if (current != null) {
           
            compressedStr.append('0');
            setCompressedTxt(hm, compressedStr, current.leftN);
            compressedStr.deleteCharAt(compressedStr.length() - 1);
            compressedStr.append('1');
            setCompressedTxt(hm, compressedStr, current.rightN);

          
            compressedStr.deleteCharAt(compressedStr.length() - 1);
        }
    }

    private void compressString(Phaser p, HashMap<Character, String> hm, StringBuffer encodedStr, String str) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < str.length(); i++) {
                    encodedStr.append(hm.get(str.charAt(i)));
                }
                p.arriveAndAwaitAdvance();
            }
        }).start();
    }
}