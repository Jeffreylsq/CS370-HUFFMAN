package tianyuwei;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class PHuffmanTree {
    public static void main(String[] args) throws IOException, InterruptedException {

        String txt = new String(Files.readAllBytes(Paths.get("/Users/pc/eclipse-workspace/CS370Huffman/src/constitution.txt")));

        PriorityQueue<HuffNode> Tree = new PriorityQueue<>();

        generateHuffmanTree(Tree, txt);
    }
    public static void generateHuffmanTree(PriorityQueue<HuffNode> Tree, String constitution) throws InterruptedException {
        

        long start = System.nanoTime();
        int[] char1 = new int[256];
        for (char c : constitution.toCharArray()) {
            char1[c] += 1;
        }

        for (int i = 0; i < char1.length; i++) {
            if (char1[i] > 0) {
                Tree.offer(new Node(char1[i], (char) i));
            }
        }
        Thread r1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < char1.length / 2; i++) {
                    if (char1[i] > 0) {
                        Tree.offer(new Node(char1[i], (char) i));
                    }
                }
            }
        });

        Thread r2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = char1.length / 2; i < char1.length; i++) {
                    if (char1[i] > 0) {
                        Tree.offer(new Node(char1[i], (char) i));
                    }
                }
            }
        });
        r1.start();r2.start();
        
        r1.join();r2.join();
        
        // Wait ----------------------------------------------------------------
        Thread.sleep(5000);

        while (Tree.size() > 1) {           
            HuffNode n1 = Tree.poll();       
            HuffNode n2 = Tree.poll();
    
            assert n2 != null;
            Tree.offer(new HuffNode(n1, n2));
        }
        long end = System.nanoTime();

        System.out.println("Time for setting up Parallel Thread tree: " + (((end-start) * (1e-6))-5000) + " ms");
    }
}