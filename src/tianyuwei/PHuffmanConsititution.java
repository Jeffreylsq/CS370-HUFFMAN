package tianyuwei;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.Phaser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;



public class PHuffmanConsititution {
    public static void main(String[] args) throws IOException, InterruptedException {

        String txt = new String(Files.readAllBytes(Paths.get("/Users/pc/eclipse-workspace/CS370Huffman/src/constitution.txt")));

        
        PriorityQueue<HuffNode> Tree = new PriorityQueue<>();

        long start = System.nanoTime();
        setHuffmanTree(Tree, txt);
        long end = System.nanoTime();

        HashMap<Character, String> compressedChar = new HashMap<>();


        FileOutputStream compress = new FileOutputStream("/Users/pc/eclipse-workspace/CS370Huffman/src/compressed.txt");

       setCompressedTxt(compressedChar, new StringBuffer(), Tree.peek());


        // Parallel Huffman Encoding begins-------------------------------------------------------------------------------------
        long start2 = System.nanoTime();
        int num = 3;
        Phaser pha = new Phaser(num);

        StringBuffer[] encoded = new StringBuffer[num];

        for (int i = 0, j = 0, k = txt.length() / num; i < encoded.length; i++, j += k) {
            encoded[i] = new StringBuffer();

            if (j + k > txt.length()) {
                new PHuffmanConsititution().compressString(pha, compressedChar, encoded[i], txt.substring(j));
            }
            else {
                new PHuffmanConsititution().compressString(pha, compressedChar, encoded[i], txt.substring(j, j + k));
            }
        }

        // Wait---------------------------------------------------------------------------------------------
        Thread.sleep(5000);

        StringBuffer encode = new StringBuffer();

        for (StringBuffer s : encoded) {
            encode.append(s);
        }
        compress.write(encode.toString().getBytes());
        compress.close();

        long end2 = System.nanoTime() ;

        System.out.println("Time for Parallel Thread : " + (((((end-start) + (end2-start2))*1e-6)-5000 ) ) + " ms");
    }

    public static void setHuffmanTree(PriorityQueue<HuffNode> huffTree, String constitution) throws InterruptedException {

        
        int[] charFreq = new int[256];
        for (char c : constitution.toCharArray()) {
            charFreq[c] += 1;
        }

        Thread r1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < charFreq.length / 2; i++) {
                    if (charFreq[i] > 0) {
                        huffTree.offer(new Node(charFreq[i], (char) i));
                    }
                }
            }
        });

        Thread r2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = charFreq.length / 2; i < charFreq.length; i++) {
                    if (charFreq[i] > 0) {
                        huffTree.offer(new Node(charFreq[i], (char) i));
                    }
                }
            }
        });

        r1.start();
        r2.start();
        r1.join();
        r2.join();
        Thread.sleep(5000);

        while (huffTree.size() > 1) {
            
            HuffNode n1 = huffTree.poll();
            HuffNode n2 = huffTree.poll();
            assert n2 != null;
            huffTree.offer(new HuffNode(n1, n2));
        }

    }

    
    

    private void compressString(Phaser p, HashMap<Character, String> hp, StringBuffer encoded, String str) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < str.length(); i++) {
                    encoded.append(hp.get(str.charAt(i)));
                }
                p.arrive();
            }
        }).start();
    }
    
    public static void setCompressedTxt(HashMap<Character, String> hm, StringBuffer compress, HuffNode current) {
        if (current instanceof Node) {
            hm.put(((Node) current).letter, compress.toString());

        }
        else if (current != null) {
            
            compress.append('0');
            setCompressedTxt(hm, compress, current.leftN);
            compress.deleteCharAt(compress.length() - 1);
            compress.append('1');
            setCompressedTxt(hm, compress, current.rightN);
            compress.deleteCharAt(compress.length() - 1);
        }
    }
    
    
    
}