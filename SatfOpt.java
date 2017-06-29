import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/*
Implementation of a shortest access time first approach, which calculates the optimal access time and orders accesses optimally in order.
*/
public class SatfOpt
{
	static String outFilePath = "tests/satfopt_out.txt";
	static String inFilePath = "tests/diskin_satfopt.txt";

	static int nPlatters;
	static int nCylinders; // same as number of tracks
	static int nRequests;
	static int blocksPerTrack;
	static int rotationalSpeed; //rpm
	static int constFactor0; // constant factor
	static int constFactor1; // constant factor
	static int startCylinder;
	static int startDirection; // 0 is outer to inner i.e. 0 to 99 and 1 is inner to outer i.e. 99 to 0
	static int startBlock;
	static int startHead;
	static double readTimePerBlock;
	static double writeTimePerBlock;

	static AccessRequest[] inRequests;
	static AccessRequest[] outRequests;

	/*
	Runner
	*/
	public static void runSatfOpt()
	{
		inputConfig();
		SATF();
		printConfig();
	}

	/*
	Algorithm Implementation
	*/
	public static void SATF()
	{
		double accessTimes[] = new double[nRequests];
		int i = 0;
		int count = 0;
		int cyl = startCylinder;
		int block = startBlock;
		int requestsRemaining = nRequests;
		int minLoc = 0;
		int newBlock;
		double min = 0;

		// here, head switch time is assumed to be zero
		while (count < nRequests)
		{
			i = 0;
			min = 100000;
			while (i < requestsRemaining)
			{
				newBlock = (int) (block + (blocksPerTrack * (constFactor1 * 0.001 * Utils.absval(cyl - inRequests[i].m_cylinderNumber))
					*(rotationalSpeed/60))) % blocksPerTrack;
				if (inRequests[i].m_startBlock >= newBlock)
				{
					accessTimes[i] = (double) constFactor1 * 0.001 * Utils.absval(cyl - inRequests[i].m_cylinderNumber) +
						((inRequests[i].m_startBlock - newBlock)/(blocksPerTrack * (rotationalSpeed/60)));
					if (cyl != inRequests[i].m_cylinderNumber)
						accessTimes[i] += constFactor0; // seek time if on different cylinders
					if (inRequests[i].m_readWrite == 0) // transfer time
						accessTimes[i] += inRequests[i].m_numOfBlocks * readTimePerBlock;
					else if (inRequests[i].m_readWrite == 1) // transfer time
						accessTimes[i] += inRequests[i].m_numOfBlocks * writeTimePerBlock;
				}
				else
				{
					accessTimes[i] = (double) constFactor1 * 0.001 * Utils.absval(cyl - inRequests[i].m_cylinderNumber) +
						((inRequests[i].m_startBlock - newBlock + blocksPerTrack)/(blocksPerTrack * (rotationalSpeed/60)));
					if (cyl != inRequests[i].m_cylinderNumber)
						accessTimes[i] += constFactor0; // seek time if on different cylinders
					if (inRequests[i].m_readWrite == 0) // transfer time
						accessTimes[i] += inRequests[i].m_numOfBlocks * readTimePerBlock;
					else if (inRequests[i].m_readWrite == 1) // transfer time
						accessTimes[i] += inRequests[i].m_numOfBlocks * writeTimePerBlock;
				}
				if (accessTimes[i] < min)
				{
					min = accessTimes[i];
					minLoc = i;
				}
				i++;
			}
			outRequests[count] = inRequests[minLoc];
			for (int j = minLoc; j < requestsRemaining - 1; j++)
			{
				inRequests[j] = inRequests[j+1];
			}
			requestsRemaining--;
			cyl = inRequests[minLoc].m_cylinderNumber;
			block = (inRequests[minLoc].m_startBlock + inRequests[minLoc].m_numOfBlocks) % blocksPerTrack;
			count++;
		}
	}

	/*
	Read the input configuration
	*/
	public static void inputConfig()
	{
		Scanner scanner;
		try
		{
			scanner = new Scanner(new File(inFilePath));
		}
		catch (Exception e)
		{
			// exit - file not found or unsupported encoding
			return;
		}

		//assuming all inputs are positive. not doing negative check
		nPlatters = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		nCylinders = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		blocksPerTrack = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		rotationalSpeed = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		constFactor0 = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		constFactor1 = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		nRequests = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		startCylinder = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		startDirection = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		startBlock = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		startHead = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		readTimePerBlock = (scanner.hasNextDouble()) ? scanner.nextDouble() : 0.0;
		writeTimePerBlock = (scanner.hasNextDouble()) ? scanner.nextDouble() : 0.0;

		int cylNum, headNum, startingBlock, numBlocks, rw;
		inRequests = new AccessRequest[nRequests];
		outRequests = new AccessRequest[nRequests];

		if (startBlock >= blocksPerTrack || startHead >= 2 * nPlatters)
		{
			System.out.println("Invalid accesses.");
			scanner.close();
			return;
		}

		for (int i = 0; i < nRequests; i++)
		{
			cylNum = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
			headNum = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
			startingBlock = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
			numBlocks = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
			rw = (scanner.hasNextInt()) ? scanner.nextInt() : 0;

			if (cylNum >= nCylinders ||
				headNum >= 2 * nPlatters ||
				startingBlock >= blocksPerTrack ||
				numBlocks >= blocksPerTrack ||
				(rw != 0 && rw != 1))
			{
				System.out.println("Invalid accesses.");
				scanner.close();
				return;
			}

			inRequests[i] = new AccessRequest();
			outRequests[i] = new AccessRequest();
			inRequests[i].addDetails(cylNum, headNum, startingBlock, numBlocks, rw);
		}

		scanner.close();
	}

  /*
  Print the output configuration
  */
  public static void printConfig()
  {
    PrintWriter writer;
    try
    {
      writer = new PrintWriter(outFilePath);
    }
    catch (Exception e)
    {
      // exit - file not found or unsupported encoding
      return;
    }

		writer.println("Disk configuration");
		writer.println("Platters: " + nPlatters + "\t\t\tCylinders: " + nCylinders + "\t\tBlocks per track: " + blocksPerTrack);
    writer.println("Rotational speed: " + rotationalSpeed + "\t\tBlock read time: " + readTimePerBlock + "\t\tBlock write time: " + writeTimePerBlock);
		writer.println("Starting cyl (head): " + startCylinder + "\t\tStart direction: " + startDirection);
		writer.println("Requests:");
		for (int i = 0; i < nRequests; i++)
		{
			writer.println("Cyl: " + outRequests[i].m_cylinderNumber + ", \tHead: " + outRequests[i].m_headNumber + ", \tStart: " + outRequests[i].m_startBlock + ",  \t Num blks: " + outRequests[i].m_numOfBlocks + ", \tR/W: " + outRequests[i].m_readWrite);
		}

		writer.close();
	}

}
