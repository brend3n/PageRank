### PageRank

## Author: Brenden Morton
## Course: Network Optimization
## Professor: Dr. Wei Zhang



#   This is an implementation of the PageRank algorithm to find the highest rated page among a number of N pages. In this implementation, the algorithm continues
#   to run until it converges upon some specifed value ε.


# To run this program on the command line, do the following:

#	1. Navigate to the directory where the FleuryPrimSolver.jar file is located using 'cd' commands

#	2. Run the following command:
			
#               java -jar PageRank.jar [filename of graph] [number of vertices in graph]

# The input file should be a square adjancency matrix of integers separated by commas. A '0' represents no edge between two nodes.
# Additionally, the leftmost character of each row should be an integer representing the row. After, this character should be a '\t' tab character.
# Below is a simple example of what the input file should look like:

#				0   0, 3, 0, 6
#				1   5, 9, 0, 0
#				2   0, 8, 0, 0
#				3   8, 1, 3, 0



# The program outputs the nodes in order of highest PageRank to lowest PageRank to a file called "output.txt"
# The format of the output file is:

# number of iterations: 20
# ranking: 36 (0.0108), 87 (0.0102), 12 (0.00854), ..., 56 (0.00278).


# Thank you for using this program!!

# Brenden Morton
