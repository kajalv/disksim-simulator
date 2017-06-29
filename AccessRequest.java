/*
Data structure to hold a disk access request.
*/

public class AccessRequest
{
  public AccessRequest()
  {
    m_cylinderNumber = 0;
    m_headNumber = 0;
    m_startBlock = 0;
    m_numOfBlocks = 0;
    m_readWrite = 0;
  }

	public void addDetails(int cylNum, int headNum, int startBlock, int numOfBlocks, int readWrite)
  {
		m_cylinderNumber = cylNum;
		m_headNumber = headNum;
		m_startBlock = startBlock;
		m_numOfBlocks = numOfBlocks;
		m_readWrite = readWrite;
	}

  // same as track number
  int m_cylinderNumber;

  // head numbers range from 0 to 2*num_of_platters - 1
  //surfaces = 2*num_of_platters
  int m_headNumber;

  // starting block of the access
  int m_startBlock;

  // number of blocks to be accesses
  int m_numOfBlocks;

  // access type. 0 is read access, 1 is write access
  int m_readWrite;
}
