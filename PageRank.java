import java.util.*;
import java.io.*;
import java.lang.Math;


class PageRank{


	// Stores the number of nodes in a the graph
	int N;

	// Stores the number of edges in the graph
	int graph_size;

	// 2D array to store the input file
	int [][] matrix;

	// Constuctor
	public PageRank (String fileName, int numVertices) throws Exception{


		// Opens the passed in file and allows the filed to be read
		Scanner scan = new Scanner(new File(fileName));

		// Storing the number of vertices/nodes in the graph
		this.N = numVertices;

		// Declaring a 2-D  integer array to act as the adjacency matrix
		matrix = new int[numVertices][numVertices];

		// Not sure what this is for
		int [] edgeCount = new int[N];


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
            
            // Closing scanner object
            strScan.close();
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
    private double [] mult_constant_by_Nx1_matrix(int d, double [] matrix){
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



    // Not sure how this function should work yet.
    /*
        I need to ask Dr. Zhang about the convergence condition 
            -> do we check if any of the entries in the resultant Nx1 matrix is less than the value of epsilon or am I missing
    */
    private boolean isConverged(double [] Rt_next, double [] Rt, double epsilon){
        double res[] = absolute_matrix_subtraction(Rt_next, Rt);

        // return ( < epsilon);
        return true;
        
    }
    
    private void create_m_matrix(){
        return;
    }
    
    public void runPageRank(double damping, double ep){

        // Nx1 vector that consists of the entry (1-d)/N
        double[] S = create_s_matrix(create_s_matrix_entry(damping));
        



        return;
    }

    public static void main(String[] args) throws Exception {

        // The damping factor d is equal to the score of your second exam divided by 100. ε is equal to 1e-5.
        // d = 89/100 = 0.89
        // ε = 1e-5 = 0.00001

        if (args.length < 2 || args.length > 2 ){
			System.out.println("To run this program: ");
			// System.out.println("\t javac FleuryPrimSolver [filename] [source] [number of vertices in graph] ");
			System.out.println("\t java -jar PageRank.jar [filename of graph] [number of vertices in graph] ");

		}else{

			String fileName = args[0];
			int numberOfVertices = Integer.parseInt(args[1]);

            System.out.printf("file name => %s \tnumber of vertices => %d\n", fileName, numberOfVertices);

			PageRank pr = new PageRank(fileName , numberOfVertices);
			pr.runPageRank(10.0, 24.0);
		}



        return;
    }
}