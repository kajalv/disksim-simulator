import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/*
Implementation of a cyclic cyndrical access, where accesses are ordered by disk cylinders.
*/
public class CycleCyl
{
	static String outFilePath = "tests/cyclecyl_out.txt";
	static String inFilePath = "tests/diskin_cyclecyl.txt";

	static int nPlatters;
	static int nCylinders; // same as number of tracks
	static int nRequests;
	static int blocksPerTrack;
	static int startCylinder;
	static int startDirection; // 0 is outer to inner i.e. 0 to 99 and 1 is inner to outer i.e. 99 to 0

	static AccessRequest[] inRequests;
	static AccessRequest[] outRequests;

	/*
	Runner
	*/
	public static void runCycleCyl()
	{
		inputConfig();
		CLOOK();
		printConfig();
	}

	/*
	Algorithm Implementation
	*/
	public static void CLOOK()
	{
		int i;
		int j;
		AccessRequest temp = new AccessRequest();

		for (i = 1; i < nRequests; i++)
		{
			j = i;
			while (j > 0 &&
				((startDirection == 0 && inRequests[j].m_cylinderNumber < inRequests[j-1].m_cylinderNumber) ||
				(startDirection == 1 && inRequests[j].m_cylinderNumber > inRequests[j-1].m_cylinderNumber))
			)
			{
				temp = inRequests[j];
				inRequests[j] = inRequests[j-1];
				inRequests[j-1] = temp;
				j--;
			}
		}

		int[] answer = search(startDirection);
		i = answer[0];
		j = answer[1];
		int count = 0;

		if (startDirection == 0)
		{
			count = 0;
			while (j < nRequests)
			{
				outRequests[count] = inRequests[j];
				++j;
				++count;
			}
			j = 0;
			while (j <= i)
			{
				outRequests[count] = inRequests[j];
				++j;
				++count;
			}
		}
		else
		{
			count = 0;
			while (j < nRequests)
			{
				outRequests[count] = inRequests[j];
				++j;
				++count;
			}
			j = 0;
			while (j <= i)
			{
				outRequests[count] = inRequests[j];
				++j;
				++count;
			}
		}
	}

	/*
	Search for indices based on startDirection
	*/
	static int[] search(int startDirection)
	{
		int[] indices = new int[2];
		int low = 0;
		int high = nRequests - 1;

		switch (startDirection)
		{
			case 0:
			if (inRequests[low].m_cylinderNumber >= startCylinder)
			{
				indices[0] = -1;
				indices[1] = 0;
			}
			else if (inRequests[high].m_cylinderNumber < startCylinder)
			{
				indices[0] = high;
				indices[1] = high + 1;
			}
			else
			{
				while (high - low > 1)
				{
					int mid = (low + high)/2;
					if ((inRequests[mid-1].m_cylinderNumber < startCylinder) && (inRequests[mid].m_cylinderNumber >= startCylinder))
					{
						indices[0] = mid - 1;
						indices[1] = mid;
						break;
					}
					else if (inRequests[mid].m_cylinderNumber < startCylinder)
					{
						low = mid;
						indices[0] = mid;
						indices[1] = mid + 1;
					}
					else if (inRequests[mid].m_cylinderNumber >= startCylinder)
					{
						high = mid;
						indices[0] = mid - 1;
						indices[1] = mid;
					}
				}
			}
			break;

			case 1:
			if (inRequests[low].m_cylinderNumber <= startCylinder)
			{
				indices[0] = -1;
				indices[1] = 0;
			}
			else if (inRequests[high].m_cylinderNumber > startCylinder)
			{
				indices[0] = high;
				indices[1] = high + 1;
			}
			else
			{
				while (high - low > 1)
				{
					int mid = (low + high)/2;
					if ((inRequests[mid-1].m_cylinderNumber > startCylinder) && (inRequests[mid].m_cylinderNumber <= startCylinder))
					{
						indices[0] = mid - 1;
						indices[1] = mid;
						break;
					}
					else if (inRequests[mid].m_cylinderNumber > startCylinder)
					{
						low = mid;
						indices[0] = mid;
						indices[1] = mid + 1;
					}
					else if (inRequests[mid].m_cylinderNumber <= startCylinder)
					{
						high = mid;
						indices[0] = mid - 1;
						indices[1] = mid;
					}
				}
			}
			break;

			default: break;
		}

		return indices;
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
		nRequests = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		startCylinder = (scanner.hasNextInt()) ? scanner.nextInt() : 0;
		startDirection = (scanner.hasNextInt()) ? scanner.nextInt() : 0;

		int cylNum, headNum, startingBlock, numBlocks, rw;
		inRequests = new AccessRequest[nRequests];
		outRequests = new AccessRequest[nRequests];

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
		writer.println("Starting cyl (head): " + startCylinder + "\t\tStart direction: " + startDirection);
		writer.println("Requests:");
		for (int i = 0; i < nRequests; i++)
		{
			writer.println("Cyl: " + outRequests[i].m_cylinderNumber + ", \tHead: " + outRequests[i].m_headNumber + ", \tStart: " + outRequests[i].m_startBlock + ",  \t Num blks: " + outRequests[i].m_numOfBlocks + ", \tR/W: " + outRequests[i].m_readWrite);
		}

		writer.close();
	}
}
