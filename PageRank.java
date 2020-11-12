import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class PageRank{


	// Stores the number of nodes in a the graph
	int N;

	// Stores the number of edges in the graph
	int graph_size;

	// 2D array to store the input file
    int [][] matrix;
    
    double [] R;
    double [] S;
    double [][] M;
    int [] pageOrder;




    double [] pr1 = new double [N];
    int [] order1 = new int [N];

    double [] pr2 = new double [N];
    int [] order2 = new int [N];

    int iterations = 0;
    int [] ordering;
    double [] sortedPageRanks;
    String filename;

    double currSum = 0.0;

	// Constuctor
	public PageRank (String fileName, int numVertices) throws Exception{

        
		// Opens the passed in file and allows the filed to be read
        Scanner scan = new Scanner(new File(fileName));
        this.filename = "output.txt";

		// Storing the number of vertices/nodes in the graph
		this.N = numVertices;

		// Declaring a 2-D  integer array to act as the adjacency matrix
        matrix = new int[numVertices][numVertices];
        

        // Array of page ranks for each node
        R = new double [N];

        // Array of the M matrix
        // M = new double[N][N];

		// Reading in the text file and creating the matrix
		for (int i = 0; i < N; i++){

			// Reading in the first charater of each row. This represents the number of the row starting at 0.
            int rowNum = scan.nextInt();
        


			// Ignoring the tab
			scan.skip("\t");

			// Capture the entire row as a String to be parsed
			String str = scan.nextLine();

			// Setting up the string for parsing.
			// Replacing deleting all commas
			str = str.replace(",", " ");

			// Begin to parse string
			Scanner strScan = new Scanner(str);

			// Reading each edge in the text file and adding it to the adjacency matrix
			for (int j = 0; j < N; j++){
				if(strScan.hasNextInt()){
                    matrix[i][j] = strScan.nextInt();
                }
				else{
                    strScan.skip(" ");
                }
            }
        }

        // printArr(matrix);
    }

    // Prints a 2D array for testing purposes
    private void printArr(int [][] arr){
        for(int [] x : arr){
            for(int y : x){
                System.out.print(y + " ");
            }
            System.out.println();
        }
    }

    // Writes the input string (input) into the first line of the output file (filename)
    public static void writeToFile(String filename, String input){

        try{
            FileWriter writer = new FileWriter(filename);
            writer.write(input + "\n");
            writer.close();

        }catch(IOException f){
            System.out.println("Error in: (" + filename +")\n Exception: " + f);
        }

    }

    // Appends the input string (input) to the output file (filename)

    public static void appendToFile(String filename, String input){
        try{
            FileWriter writer = new FileWriter(filename, true);
            writer.write(input);
            writer.write("\n");
            writer.close();
        }catch(IOException f){
            System.out.println("Error in: (" + filename +")\n Exception: " + f);
        }
    }

    // Returns (1-d)N => to be used for the creation of each entry of the S matrix
    private double create_s_matrix_entry(double damping){
        return ((1-damping)/N);
    }

    // Creates a Nx1 matrix of the same (entry := entry)
    private double [] create_s_matrix(double entry){
        double [] S_arr = new double [this.N];
        Arrays.fill(S_arr, entry);
        return S_arr;
    }

    // m1 is a NxN matrix and m2 is an Nx1 matrix
    // This should result in a Nx1 matrix
    private double [] matrix_mult_MxR(double [][] m1, double []m2){
        double [] res = new double [N];
        Arrays.fill(res, 0);
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                // System.out.printf("(m1[%d][%d]) x (m2[%d]) -> %d x %d\n", i,j,i, m1[i][j], m2[i]);
                res[i] += m1[i][j] * m2[j];
            }
        }
        return res;
    }

    // Multiples d by the Nx1 matrix that results from multiplying matrix M (NxN) by matrix R(t) (Nx1)
    // Returns the product
    private double [] mult_constant_by_Nx1_matrix(double d, double [] matrix){
        for(int i = 0; i < N; i++){
            matrix[i] = matrix[i] * d;
        }

        return matrix;
    }

    // Returns an Nx1 matrix that results from the sum of two Nx1 matrices m1 and m2
    private double [] matrix_addition(double[] m1, double [] m2){
        double [] m = new double [N];
        for(int i = 0; i < N; i++){
            m[i] = m1[i] + m2[i];
        }
        return m;
    }
    
    // Returns an Nx1 matrix from the subtraction of Rt from Rt_next (Rt_next - Rt) both of which are Nx1 matrices
    private double[] absolute_matrix_subtraction(double [] Rt_next, double [] Rt){

        double [] res = new double [N];
        for(int i = 0; i < N; i++){
            res[i] = Math.abs(Rt_next[i] - Rt[i]);
        }
        return res;
    }

    private int get_outgoing_links(int node){
        int rowSum = 0;

        for(int i = 0; i < N; i++){
            if(matrix[node][i] == 1)
                rowSum++;
        }

        return rowSum;
    }

    private int[] L_arr(){
        int [] arr = new int [N];
        for(int i = 0; i < N; i++){
            arr[i] = get_outgoing_links(i);
        }
        return arr;
    }
    
    private double [][] create_m_matrix(){
        double [][] matrix_m = new double[N][N];
        int [] links = L_arr();

        // System.out.println("L_arr: " + Arrays.toString(links));

        for(int i = 0; i < N; i++)
            for(int j = 0; j < N; j++){
                if(matrix[i][j] == 1){
                    // System.out.printf("%c->%c == 1\n", (i + 65), (j +65));
                    // System.out.printf("\t Setting matrix_m[%c][%c] = %lf\n",i+65, j+65, (double)(1.0/links[i]));
                    matrix_m[j][i] = (double)(1.0/links[i]);
                }
                else{
                    // System.out.println("ELSE");
                    // System.out.printf("%c->%c == 0\n", (i + 65), (j + 65));
                    // System.out.printf("\t Setting matrix_m[%c][%c] = %d\n",i+65, j+65, 0);
                    matrix_m[j][i] = 0;
                }
                // System.out.println();
            }

        return matrix_m;
    }

    private void print2d(int [][]arr){
        for(int i =0; i < N;i++)
            System.out.println(Arrays.toString(arr[i]));
        return;
    }
    private void print2d(double [][]arr){
        for(int i =0; i < N;i++)
            System.out.println(Arrays.toString(arr[i]));
        return;
    }

    private double computeConvergenceCriteria(double [] Rt_next, double [] Rt){
        // double summationDiff = 0;
        // double sum_t_next = 0;
        // double sum_t = 0;

        double diffSum = 0.0;

        // for(int i = 0; i < N; i++){
        //     sum_t_next += Rt_next[i];
        //     sum_t += Rt[i];
        // }

        // summationDiff = Math.abs(sum_t_next - sum_t);
        // System.out.println(summationDiff + " = |" + sum_t_next +" - " + sum_t+"|");
        // return summationDiff;

        



        for(int i = 0; i < N; i++){
            diffSum += Math.abs(Rt_next[i] - Rt[i]);
        }

        System.out.println("diffSum = " + diffSum);
        return diffSum;



    //    return 420.0;
    }



    // Not sure how this function should work yet.
    /*
        I need to ask Dr. Zhang about the convergence condition 
            -> do we check if any of the entries in the resultant Nx1 matrix is less than the value of epsilon or am I missing
    */
    private double isConverged(double [] Rt_next, double [] Rt, double epsilon, double currSum){
        double res = computeConvergenceCriteria(Rt_next, Rt);
        // boolean res = false;

        // System.out.printf("res: %lf\nepsilon: %lf\nDifference: %lf", res, epsilon, (res - epsilon));
        System.out.printf("Convergence reached: %s\n", (res < epsilon) ? "true":"false");


        // return true;
        return res;
    }
    
    private void init_for_runPageRank(double damping){

        // Initialize page ranks for all nodes
        Arrays.fill(R, (double)1/N);

        // Nx1 vector that consists of the entry (1-d)/N
        S = create_s_matrix(create_s_matrix_entry(damping));

        // Create M matrix
        M = create_m_matrix();

        // System.out.println("R: ");
        // System.out.println(Arrays.toString(R));

        // System.out.println("S: ");
        // System.out.println(Arrays.toString(S));
        // System.out.println("M: ");
        // print2d(M);

    }
    public void print(String str){
        System.out.print(str);
    }
    public void print(int str){
        System.out.print(str);
    }
    public void runPageRank(double damping, double ep){
        
        int i = 0;
        double [] G;
        double [] F;
        double [] R_next = new double [N];
        init_for_runPageRank(damping);
        // R_next = R;
        // Arrays.fill(R_next, 0.0);

        print("iterations: ");
        print(iterations);
        System.out.println(Arrays.toString(R));

        
        do{
            // print("iterations: ");
            iterations++;
            // System.out.println("\nR: "+Arrays.toString(R));
            G = matrix_mult_MxR(M, R);
            F = mult_constant_by_Nx1_matrix(damping, G);
            R_next = matrix_addition(S, F);

            if(isConverged(R_next, R, ep, currSum) <  ep){
                System.out.println("Converged at iterations: " + iterations);
                R = R_next;
                break;
            }

            R = R_next;
        }while(true);

       pr1 =  terminate();

        // }while(!isConverged(R_next,R,ep));

        while(i++<1e6){
            G = matrix_mult_MxR(M, R);
            F = mult_constant_by_Nx1_matrix(damping, G);
            R_next = matrix_addition(S, F);
            // iterations++;
            // if(iterations % 1000000 == 0)
                // System.out.println(iterations);
            // R_next = R;
            // print("iterations: ");
            // print(++iterations);
            // print("\n");
            // System.out.println("\nR: "+Arrays.toString(R));
            // System.out.println("\nR_next: "+Arrays.toString(R_next));
            // System.out.println("Math.abs(sum(R_next - R)) = "+ computeConvergenceCriteria(R_next, R));
            // print("\n");

            // if(isConverged(R_next,R,ep, currSum) < ep ){
            //     System.out.println("Converged at iterations: " + iterations);
            //     break;
            // }
            R = R_next;

        }
         pr2 = terminate();
        

        int flag = 1;
         for(i = 0; i < N; i++){
             if(Math.abs(pr1[i] - pr2[i]) > 1e-5){
                System.out.println("NOT CONVERGED ENOUGH");
                flag = 0;
                break;
             }
            System.out.println("Math.abs(" +pr1[i]+ "-" +pr2[i]+" = " + (Math.abs(pr1[i] - pr2[i])));
         }

         if(flag == 1)
            System.out.println("CONVERGED");

        // // while(!isConverged(R_next,R,ep)){
        //     G = matrix_mult_MxR(M, R);
        //     F = mult_constant_by_Nx1_matrix(damping, G);
        //     R_next = matrix_addition(S, F);
        //     R = R_next;
        //     print("iterations: ");
        //     print(++iterations);
        //     System.out.println("\nR: "+Arrays.toString(R));
        //     print("\n");
        // // }



        return;
    }

    private double[] terminate() {
        // Convert R -> Map with key-value where key is the index and the value is the PageRank Value
        Map <Double, Integer> RankMap = new HashMap<Double, Integer>(N);
        ordering = new int[N];
        double val;
        for(int i = 0; i < N ; i++){

//                            key          value
            // Mapping : {index of array, PageRank }
            RankMap.put(R[i], i);
            System.out.println(i +": " + R[i]);
        }

        sortedPageRanks = R;
        Arrays.sort(sortedPageRanks);
        System.out.println(RankMap.toString());

        for(int i = 0; i < N; i++){
            ordering[i] = RankMap.get(sortedPageRanks[i]);
        }
            print("\n\n|Ordering|\n\n");
            System.out.println(Arrays.toString(ordering));
            
        
        double max = -1e9;
        double min = 1e9; ;    
        
        for(int i = 0; i < N ;i++){
            val = sortedPageRanks[i];
            if(val < min){min = val; continue;}
            if(val > max){max = val; continue;}

        }
        System.out.printf("Min: "+min+"\t"+" Max:" +max+ "\n");

        print_to_file(R);
        
        return sortedPageRanks;

    }

    private void print_to_file(double [] R){
        String end = "", res_str = "";

        writeToFile(filename, "number of iterations: " + iterations );

        int j = N-1;
        for(int i = 0; i < N; i++){
            if(i == (N-1)){
                end = ".";
            }else{
                end = ", ";
            }
            res_str += ordering[i] + " (" + sortedPageRanks[j--]+")"+ end;    
        }
        appendToFile(filename, res_str);
        // writeToFile(filename, input);
        // appendToFile(filename, input);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2 || args.length > 2 ){
			System.out.println("To run this program: ");
			// System.out.println("\t javac FleuryPrimSolver [filename] [source] [number of vertices in graph] ");
			System.out.println("\t java -jar PageRank.jar [filename of graph] [number of vertices in graph] ");

		}else{

			String fileName = args[0];
            int numberOfVertices = Integer.parseInt(args[1]);
            
            PageRank pr = new PageRank(fileName , numberOfVertices);
            // The damping factor d is equal to the score of your second exam divided by 100. ε is equal to 1e-5.
                // d = 89/100 = 0.89
                // ε = 1e-5 = 0.00001
            double damping = 0.89;
            double ep = 1e-5;
            pr.runPageRank(damping, ep);
		}

        return;
    }
}