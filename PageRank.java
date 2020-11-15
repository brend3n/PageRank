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
    
    // Stores the name of the file that we read the Adjacency matrix from
    String filename;

    // Stores the current iteration of the PageRank values of all of the N pages
    double [] R;

    // Stores the Nx1 vector of (1-d)/N, where d is the damping factor and N is the number of nodes/pages being observed
    double [] S;

    // Stores the NxN stochastic matrix that contains the 1/L(pj) if some page j links to page i , where L is the number of outgoing links from page j
    double [][] M;

    // Used for processing the output
    double [] pr1 = new double [N];

    // Initially stores a copy of the vector R, but then is sorted based on PageRank from highest to lowest
    double [] sortedPageRanks;
    
    // Stores the order of the of the nodes based on PageRank
    int [] ordering;

    // The number of iterations the algorithm ran for
    int iterations = 0;

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
				if(strScan.hasNextInt())
                    matrix[i][j] = strScan.nextInt();            
				else
                    strScan.skip(" ");
            }
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

    // Creates a Nx1 matrix of all the same entry
    private double [] create_s_matrix(double entry){
        double [] S_arr = new double [this.N];
        Arrays.fill(S_arr, entry);
        return S_arr;
    }

    // Stores the result of mulitplying the previous pageRanks by the stochastic matrix M.
    // See more about what makes up the stochastic matrix M in the top of this program by the declaration of it.
    private double [] matrix_mult_MxR(double [][] m1, double []m2){
        double [] res = new double [N];
        Arrays.fill(res, 0);
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
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
    
    // Returns the row sum of a single node. The row sum represents the number of outgoing links of a node i.
    private int get_outgoing_links(int node){
        int rowSum = 0;

        for(int i = 0; i < N; i++){
            if(matrix[node][i] == 1)
                rowSum++;
        }

        return rowSum;
    }

    // Returns an array of all of the values 1/L(pj) for all nodes j where L(x) is the number of outgoing links of a node x.
    private int[] L_arr(){
        int [] arr = new int [N];
        for(int i = 0; i < N; i++){
            arr[i] = get_outgoing_links(i);
        }
        return arr;
    }
    
    // Computes all of the entries of the M (NxN) matrix. Read more about the entries of M at the declaration of M.
    private double [][] create_m_matrix(){

        double [][] matrix_m = new double[N][N];
        int [] links = L_arr();

        for(int i = 0; i < N; i++)
            for(int j = 0; j < N; j++){
                if(matrix[i][j] == 1)
                    matrix_m[j][i] = (double)(1.0/links[i]);        
                else
                    matrix_m[j][i] = 0;        
            }
        return matrix_m;
    }

    // Returns the sum of all of the absolute differences of the current and the past iterations of PageRanks of each page. 
    private double computeConvergenceCriteria(double [] Rt_next, double [] Rt){
        double diffSum = 0.0;

        for(int i = 0; i < N; i++){
            diffSum += Math.abs(Rt_next[i] - Rt[i]);
        }

        return diffSum;
    }

    // Returns the computeConvergenceCriteria
    private double isConverged(double [] Rt_next, double [] Rt, double epsilon){
        double res = computeConvergenceCriteria(Rt_next, Rt);

        return res;
    }
    
    // Initializes the vectors R, M and S for the PageRank algorithm.
    private void init_for_runPageRank(double damping){

        // Initialize page ranks for all nodes
        Arrays.fill(R, (double)1/N);

        // Nx1 vector that consists of the entry (1-d)/N
        S = create_s_matrix(create_s_matrix_entry(damping));

        // Create M matrix
        M = create_m_matrix();
    
    }
    
    
    // Runs this implementation of the PageRank algorithm on a specified input matrix with N pages.
    // In this implementation, the algorithm continues to run until it converges upon a specified value of ε or ep.
    public void runPageRank(double damping, double ep){
        double [] G;
        double [] F;
        double [] R_next = new double [N];
        init_for_runPageRank(damping);
        
        // Loops until the results converge on ε
        do{
            iterations++;
            // Building up the result of a single iteration of PageRank algo
            G = matrix_mult_MxR(M, R);
            F = mult_constant_by_Nx1_matrix(damping, G);
            R_next = matrix_addition(S, F);

            // Check for convergence
            if(isConverged(R_next, R, ep) <  ep){
                R = R_next;
                break;
            }

            R = R_next;
        }while(true);

        // Used for processing the results
        pr1 =  terminate();

        return;
    }

    // Ends the execution of the program by obtaining the proper
    // ordering of the pages and writing these results to a file
    private double[] terminate() {
        // Convert R -> Map with key-value where key is the index and the value is the PageRank Value
        Map <Double, Integer> RankMap = new HashMap<Double, Integer>(N);

        // Used to store the order of the pages
        ordering = new int[N];

        for(int i = 0; i < N ; i++){
            //                key          value
            // Mapping : {index of array, PageRank }
            RankMap.put(R[i], i);
        }

        // Make a copy of R and the pageRanks of R
        sortedPageRanks = R;
        Arrays.sort(sortedPageRanks);

        // Ordering the pages based on PageRank
        for(int i = 0; i < N; i++){
            ordering[i] = RankMap.get(sortedPageRanks[i]);
        }                    

        // Print the results of the algorithm to output.txt in this directory
        print_to_file(R);    

        return sortedPageRanks;
    }

    // Writes the result of the PageRank algorithm to a file in the order of highest PageRank
    // to lowest PageRank with their respective page number.
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
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2 || args.length > 2 ){
			System.out.println("To run this program: ");
			System.out.println("\t java -jar PageRank.jar [filename of graph] [number of vertices in graph] ");

		}else{

			String fileName = args[0];
            int numberOfVertices = Integer.parseInt(args[1]);
            
            PageRank pr = new PageRank(fileName , numberOfVertices);
            // The damping factor d is equal to the score of the second exam divided by 100. ε is equal to 1e-5.
                // d = 89/100 = 0.89
                // ε = 1e-5 = 0.00001
            double damping = 0.89;
            double ep = 1e-5;
            pr.runPageRank(damping, ep);
		}
        return;
    }
}