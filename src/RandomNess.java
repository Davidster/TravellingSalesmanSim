import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Collections2;


public class RandomNess {
    public static void main(String[] args){
        ArrayList<Integer> costList = new ArrayList<Integer>();
        int total = 0;
        int[] costArray = {24, 6, 73, 23, 7, 1, 3, 6, 500, 24};
        for (int i = 0; i < costArray.length; i++)
            costArray[i] = 1000 / costArray[i];

        for (int i : costArray)
            costList.add(i);
        for (int i = 1; i < costList.size(); i++) {
            total += costList.get(i);
        }
        for(int b = 0; b < 10*666; b++) {


            Random choose = new Random();
            int rand = choose.nextInt(total);
            //System.out.print("Rand: " + rand+"... ");
            int temp = 0;
            for (int i = 1; i < costList.size(); i++) {
                int cost = costList.get(i);
                if (temp <= rand && rand < temp + cost) {
                    //System.out.print("temp = " + temp + ", chosen index: " + i + " ");
                    if(i == 8)
                        System.out.print("BOOM ");
                }
                temp += cost;
            }
            //System.out.println();
            //try{Thread.sleep(100);}catch(InterruptedException ex){}
        }
        System.out.println(costList.toString());
    }
    public static ArrayList< ArrayList<Integer>> allPaths(ArrayList<Integer> nodes){
        ArrayList< ArrayList<Integer>> paths = new ArrayList< ArrayList<Integer>>();
        ArrayList<Integer> otherNodes = nodes;
        for(int i = 0; i < nodes.size(); i++){
            otherNodes.remove(i);
            for(int w = 0; w < otherNodes.size(); w++){
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                tmp.add(nodes.get(i));
            }
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.add(nodes.get(i));
            otherNodes.remove(i);
            if(otherNodes.size() == 1)
                temp.add(otherNodes.get(0));
            else
                paths = allPaths(otherNodes);
        }
        return paths;
    }
    public static boolean isPrime(int a){
        boolean b = true;
        if(a == 2)
            return true;
        if(a == 1 || a == 0)
            return false;
        for(int t = 2; t<a; t++){
            if(a%t == 0){
                b = false;
            }
        }
        return b;
    }
    //return true if all rotations are prime.
    public static boolean checkRotations(int a){
        boolean b = true;
        String originalWord = String.valueOf(a);
        char[] word = originalWord.toCharArray();
        ArrayList<Integer> allRotations = new ArrayList<Integer>();
        for(int t = 0; t < word.length; t++){
            char[] tempWord = new char[word.length];
            for(int z = 0; z < word.length; z++){
                if(z == word.length-1)
                    tempWord[z] = word[0];
                else
                    tempWord[z] = word[z+1];
            }
            if(!isPrime(Integer.parseInt(String.valueOf(tempWord))))
                b = false;
            //allRotations.add(Integer.parseInt(String.valueOf(tempWord)));
            //System.out.println(allRotations.toString());
            word = tempWord;
        }
/*		for(Integer numb : allRotations){
			if(!isPrime(numb))
				b = false;
			else
				System.out.print(numb+"; ");
		}*/
        return b;
    }
}