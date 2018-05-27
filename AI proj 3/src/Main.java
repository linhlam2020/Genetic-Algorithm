//Name: Linh Lam - Duc Nguyen
//Project 3 - GENETIC ALGORITHMS
//Professor Scott Thede
//March 15th, 2018

//Main class: This project will involve writing a genetic algorithm 
//to find the maximum value of arbitrary functions. 

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
	public static int numVar;				//number of variables
	public static int numBit;				//each variable takes 8 bits, numBit = numVar*8
	public static int testPop;				//number of children each time reproducing
	public static int [][] coefficient;		//stores the given coefficients
	public static int [][] var;				//stores generated variables in reproducing
	public static int [][] flippedVar;		//stores the mutant of the best child of each generation
	public static ArrayList<String> children= new ArrayList<String>();		//stores the binary representation of each children
	public static ArrayList<String> flippedBit= new ArrayList<String>();	//stores the binary representation of each mutant
	public static String dad;				//binary representation of the 1st parent
	public static String mom;				//binary representation of the 2nd parent
	public static int col;					//total number of column of var[][]
	
	//sort the var array by descending result
	public static void sortbyColumn(int arr[][], int col)
    {
        // Using built-in sort function Arrays.sort
        Arrays.sort(arr, new Comparator<int[]>() {
           
          @Override             
          // Compare values according to columns
          public int compare(final int[] entry1, 
                             final int[] entry2) {
 
            // To sort in descending order revert 
            // the '>' Operator
            if (entry1[col] < entry2[col])
                return 1;
            else
                return -1;
          }
        });  // End of function call sort().
    }
	
	//main function: runs the whole program
	public static void main( String[] args ){		
		String file;
		
		Scanner key=new Scanner(System.in);
		System.out.println( "Enter a file name to read: " );
		file = key.nextLine();
		
		key.close();
		
		FileReader inFile = null;
		
		try
		{
			inFile = new FileReader( file );
		}
		catch( FileNotFoundException e )
		{
			System.out.println( "That file could not be found." );
			System.exit( 1 );
		}
		
		Scanner in = new Scanner( inFile );
		
		numVar = Integer.parseInt(in.nextLine());
		testPop = 8;
		coefficient = new int[numVar+1][numVar+1];
		col = numVar+2;
		
		String line;
        int k = 0;
        while ( in.hasNextLine( ) )
		{
        	line = in.nextLine().trim();
        	if (line.equals(""))
        		line = in.nextLine().trim();
			String[] num = line.split( "\\s+" );
			for (int i = 0; i<numVar+1; i++) {
				//System.out.println("/"+num[i]+"/" + i);			
				coefficient[k][i] = Integer.parseInt(num[i]);
			}
			k++;
		}
        //Generate the first population
		var = new int [testPop][numVar+2];
		
		for (int i = 0; i<testPop; i++) {
			var[i][0] = 1;
			for (int j = 1; j<numVar+1; j++) {
				Random r = new Random();
				int x = r.nextInt(256);
				var[i][j] = x; 
			}
		}
		flippedVar = new int [testPop][numVar+2];
		for (int i = 0; i<testPop; i++) {
			flippedVar[i][0] = 1;}
		//System.out.println("-------------------------");
		//System.out.println("FIRST GENERATION");
		//System.out.println("-------------------------");
		//System.out.println();
		
		calSum(var);
        
    	in.close();
    	System.out.println();
    	System.out.println("Calculating...");
	    
    	//Reproduce for n times
    	for (int times = 1 ; times<= numVar*500 ; times++){		 
				        
			selectedToBinary(); 								//select the best 2 individuals
			numBit = numVar*8;
			children= new ArrayList<String>();
			children = crossover(dad,mom, numBit); 				//crossover 
			binaryToVar(children, var); 						//convert back from binary to decimal 
			//System.out.println("-------------------------");
			//System.out.println("F"+times);
			//System.out.println("-------------------------");
			//System.out.println();
			calSum(var);										//calculate the value and sort from largest to smallest
			
			
		 	flippedBit= new ArrayList<String>();						
		 	flippedBit = children;								//mutate randomly by flipping
		 	
		 	for (int i = 0; i< flippedBit.size() ; i++){
		 		String element = flip(flippedBit.get(i),numVar*8);
		 		flippedBit.set(i, element);
		 	}
		 	binaryToVar(flippedBit, flippedVar);				//calculate the mutant
		 	calSum(flippedVar); 
	
			updateBetterMutant();								//if mutant produces a higher result, take the mutant as the parent

    	}
	    System.out.println();									//print the result
	    System.out.println("The given function is: ");
	    String s = "";
		for(int i = 0; i<=numVar; i++) 
			for(int j = i; j<=numVar; j++) 
					 s+= ("("+ coefficient[i][j] + "*" +var[0][i] +"*" +var[0][j] + ")" + "+");			
		System.out.println(s.substring(0, s.length() - 1));
	    System.out.println();
	    System.out.print("The best set of variables found:  ");
	    System.out.println();
	    for (int i=1;i<numVar+1;i++){
	    	System.out.println("x" + i + " = " + var[0][i]);
		}
	    System.out.println("The value of the function with those variables = " + var[0][numVar+1]);
	}
	//convert variables from binary to decimal
	public static int [][] binaryToVar(ArrayList<String> binaryList, int [][] arr){
		for (int t = 0; t<binaryList.size(); t++) {
			for(int l = 0; l<mom.length(); l = l+8) {
				String a = binaryList.get(t).substring(l,l+8);
				int currVar = Integer.parseInt(a, 2);
				arr[t][(l+8)/8] = currVar; }
		}
		return arr;
	}
	
	//calculate the function value of each children
	public static int [][] calSum(int [][] arr){		
		for (int a = 0; a<testPop; a++) {
			int sum = 0;
			for(int i = 0; i<=numVar; i++) {
				for(int j = i; j<=numVar; j++) {
					//System.out.print("("+ coefficient[i][j] + "*" +arr[a][i] +"*" +arr[a][j] + ")" + "+");
			    	sum+= coefficient[i][j]*arr[a][i]*arr[a][j];
			   		arr[a][numVar+1] = sum;}
			}
			//System.out.println();

			//for (int j = 0; j<numVar+2; j++) {			
			//		System.out.print(arr[a][j] + " ");
			//}
			//System.out.println();
			//System.out.println("-------------------------");
		}	
		sortbyColumn(arr, col - 1);
		return arr;
	}
	
	//transform the variables of 2 highest sums to become parents
	public static void selectedToBinary() {
		dad = "";
		mom = "";
		//System.out.println("SUM DAD = " + var[0][numVar+1]);
		//System.out.println("SUM MOM = " + var[1][numVar+1]);
		
		for (int i = 1; i < numVar+1; i++) {
			if (var[0][i] < 2)
				dad+= "0000000" + Integer.toBinaryString(var[0][i]);
		    else if (var[0][i] < 4)
		    		dad+= "000000" + Integer.toBinaryString(var[0][i]);
		    else if (var[0][i] < 8)
		    		dad+= "00000" + Integer.toBinaryString(var[0][i]);
		    else if (var[0][i] < 16)
		    		dad+= "0000" + Integer.toBinaryString(var[0][i]);
		    else if (var[0][i] < 32)
		    		dad+= "000" + Integer.toBinaryString(var[0][i]);
		    	else if (var[0][i] < 64)
		    		dad+= "00" + Integer.toBinaryString(var[0][i]);
		    	else if (var[0][i] < 128)
		    		dad+= "0" + Integer.toBinaryString(var[0][i]);
		    	else
		    		dad+= Integer.toBinaryString(var[0][i]);   
		}
		
		for (int i = 1; i < numVar+1; i++) {
			if (var[1][i] < 2)
				mom+= "0000000" + Integer.toBinaryString(var[1][i]);
			else if (var[1][i] < 4)
				mom+= "000000" + Integer.toBinaryString(var[1][i]);
			else if (var[1][i] < 8)
				mom+= "00000" + Integer.toBinaryString(var[1][i]);
			else if (var[1][i] < 16)
				mom+= "0000" + Integer.toBinaryString(var[1][i]);
			else if (var[1][i] < 32)
				mom+= "000" + Integer.toBinaryString(var[1][i]);
			else if (var[1][i] < 64)
				mom+= "00" + Integer.toBinaryString(var[1][i]);
			else if (var[1][i] < 128)
				mom+= "0" + Integer.toBinaryString(var[1][i]);
			else
				mom+= Integer.toBinaryString(var[1][i]);   
		}
	}
	
	//crossover method
    public static ArrayList<String> crossover(String dad,String mom,int numBit){
    	ArrayList<String> f1= new  ArrayList<String>();
    	Random rn = new Random();
    	int start = 1+ rn.nextInt(numBit/3);
    	int mid = rn.nextInt(numBit/3) + numBit/3;
    	int end = rn.nextInt(numBit/3) + 2*numBit/3 -1;


    	//System.out.println("start mid end" + start + mid + end);
    	//System.out.println("dad: " + dad);
    	//System.out.println("mom: " + mom);
    	String dad1 = dad.substring(0, start);
		String dad2 = dad.substring(start, mid);
		String dad3 = dad.substring(mid,end);
		String dad4 = dad.substring(end, dad.length());
		String mom1 = mom.substring(0, start);
		String mom2 = mom.substring(start, mid);  
		String mom3 = mom.substring(mid,end);
		String mom4 = mom.substring(end, mom.length());
		
		String child1 = mom1 + dad2 + dad3 + dad4;
		String child2 = dad1 + mom2 + mom3 + mom4;
		String child3 = dad1 + mom2 + dad3 + dad4;
		String child4 = mom1 + dad2 + mom3 + mom4;
		String child5 = dad1 + dad2 + mom3 + dad4;
		String child6 = mom1 + mom2 + dad3 + mom4;
		String child7 = dad1 + dad2 + dad3 + mom4;
		String child8 = mom1 + mom2 + mom3 + mom4;
	    	
		f1.add(child1);
		f1.add(child2);
		f1.add(child3);
		f1.add(child4);
		f1.add(child5);
		f1.add(child6);
		f1.add(child7);
		f1.add(child8);
		
		//System.out.println(f1);
		return f1;
			
	}	
    
    //flip bits to mutate randomly
    public static String flip(String dad, int numBit){
    	char [] c = dad.toCharArray();
    	Random rn = new Random();
    	int times = rn.nextInt(numBit)+1;
    	for (int i = 0; i< times; i++){
    		int m = rn.nextInt(numBit);
    		if(c[m]=='0')
    			c[m]='1';
    		else 
    			c[m]='0';
    	//	System.out.println("m: " + m);
    	}
    	String a = new String(c);
    	//System.out.println("times: " + times);
    	return a;
    }
    
    //if a mutant has a higher result than parents, the mutant becomes parent
    public static void updateBetterMutant() {
    	if (flippedVar[0][numVar+1] > var[0][numVar+1]){    			
    		//System.out.println("FLIPPED BETTER");
    		//System.out.println("old: " +var[0][numVar+1]);
    		//System.out.println("new: " +flippedVar[0][numVar+1]);
    		for (int i = 1; i<numVar+2; i++){
    			var[0][i] = flippedVar[0][i];
    		}
        	//System.out.println("FLIPPED DAD = " + var[0][numVar+1]);
    	}
    	if (flippedVar[1][numVar+1] > var[1][numVar+1]){
    		//System.out.println("FLIPPED BETTER");
   			//System.out.println("old: " +var[1][numVar+1]);
   			//System.out.println("new: " +flippedVar[1][numVar+1]);
    		for (int i = 1; i<numVar+2; i++){
    				var[1][i] = flippedVar[1][i];
    		}
    		//System.out.println("FLIPPED MOM = " + var[1][numVar+1]);
    	}
    }
}
