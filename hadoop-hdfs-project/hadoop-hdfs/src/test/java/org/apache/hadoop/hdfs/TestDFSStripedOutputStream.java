begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
operator|.
name|impl
operator|.
name|DfsClientConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
operator|.
name|Peer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
operator|.
name|TcpPeerServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|LocatedBlock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|LocatedBlocks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|LocatedStripedBlock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|CachingStrategy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
operator|.
name|StripedBlockUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RSRawEncoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RawErasureEncoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
operator|.
name|NetUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|TestDFSStripedOutputStream
specifier|public
class|class
name|TestDFSStripedOutputStream
block|{
DECL|field|dataBlocks
specifier|private
name|int
name|dataBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlocks
specifier|private
name|int
name|parityBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|stripesPerBlock
specifier|private
specifier|final
name|int
name|stripesPerBlock
init|=
literal|4
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
name|stripesPerBlock
decl_stmt|;
DECL|field|encoder
specifier|private
specifier|final
name|RawErasureEncoder
name|encoder
init|=
operator|new
name|RSRawEncoder
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDNs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|encoder
operator|.
name|initialize
argument_list|(
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|TestFileEmpty ()
specifier|public
name|void
name|TestFileEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/EmptyFile"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileSmallerThanOneCell1 ()
specifier|public
name|void
name|TestFileSmallerThanOneCell1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileSmallerThanOneCell2 ()
specifier|public
name|void
name|TestFileSmallerThanOneCell2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileEqualsWithOneCell ()
specifier|public
name|void
name|TestFileEqualsWithOneCell
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/EqualsWithOneCell"
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileSmallerThanOneStripe1 ()
specifier|public
name|void
name|TestFileSmallerThanOneStripe1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileSmallerThanOneStripe2 ()
specifier|public
name|void
name|TestFileSmallerThanOneStripe2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileEqualsWithOneStripe ()
specifier|public
name|void
name|TestFileEqualsWithOneStripe
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/EqualsWithOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileMoreThanOneStripe1 ()
specifier|public
name|void
name|TestFileMoreThanOneStripe1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanOneStripe1"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileMoreThanOneStripe2 ()
specifier|public
name|void
name|TestFileMoreThanOneStripe2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileFullBlockGroup ()
specifier|public
name|void
name|TestFileFullBlockGroup
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/FullBlockGroup"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileMoreThanABlockGroup1 ()
specifier|public
name|void
name|TestFileMoreThanABlockGroup1
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup1"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileMoreThanABlockGroup2 ()
specifier|public
name|void
name|TestFileMoreThanABlockGroup2
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup2"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestFileMoreThanABlockGroup3 ()
specifier|public
name|void
name|TestFileMoreThanABlockGroup3
parameter_list|()
throws|throws
name|IOException
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup3"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|3
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
DECL|method|stripeDataSize ()
specifier|private
name|int
name|stripeDataSize
parameter_list|()
block|{
return|return
name|cellSize
operator|*
name|dataBlocks
return|;
block|}
DECL|method|generateBytes (int cnt)
specifier|private
name|byte
index|[]
name|generateBytes
parameter_list|(
name|int
name|cnt
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|cnt
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
name|getByte
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
DECL|method|getByte (long pos)
specifier|private
name|byte
name|getByte
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|int
name|mod
init|=
literal|29
decl_stmt|;
return|return
call|(
name|byte
call|)
argument_list|(
name|pos
operator|%
name|mod
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|testOneFile (String src, int writeBytes)
specifier|private
name|void
name|testOneFile
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|writeBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|generateBytes
argument_list|(
name|writeBytes
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
comment|// check file length
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|long
name|fileLength
init|=
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|writeBytes
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|>
name|blockGroupList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|src
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|firstBlock
range|:
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|firstBlock
operator|instanceof
name|LocatedStripedBlock
argument_list|)
expr_stmt|;
name|LocatedBlock
index|[]
name|blocks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
operator|(
name|LocatedStripedBlock
operator|)
name|firstBlock
argument_list|,
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|oneGroup
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|blocks
argument_list|)
decl_stmt|;
name|blockGroupList
operator|.
name|add
argument_list|(
name|oneGroup
argument_list|)
expr_stmt|;
block|}
comment|// test each block group
for|for
control|(
name|int
name|group
init|=
literal|0
init|;
name|group
operator|<
name|blockGroupList
operator|.
name|size
argument_list|()
condition|;
name|group
operator|++
control|)
block|{
comment|//get the data of this block
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blockList
init|=
name|blockGroupList
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|dataBlockBytes
init|=
operator|new
name|byte
index|[
name|dataBlocks
index|]
index|[]
decl_stmt|;
name|byte
index|[]
index|[]
name|parityBlockBytes
init|=
operator|new
name|byte
index|[
name|parityBlocks
index|]
index|[]
decl_stmt|;
comment|// for each block, use BlockReader to read data
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LocatedBlock
name|lblock
init|=
name|blockList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|lblock
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|DatanodeInfo
index|[]
name|nodes
init|=
name|lblock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|block
init|=
name|lblock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|targetAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|nodes
index|[
literal|0
index|]
operator|.
name|getXferAddr
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|blockBytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|block
operator|.
name|getNumBytes
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|dataBlocks
condition|)
block|{
name|dataBlockBytes
index|[
name|i
index|]
operator|=
name|blockBytes
expr_stmt|;
block|}
else|else
block|{
name|parityBlockBytes
index|[
name|i
operator|-
name|dataBlocks
index|]
operator|=
name|blockBytes
expr_stmt|;
block|}
if|if
condition|(
name|block
operator|.
name|getNumBytes
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|BlockReader
name|blockReader
init|=
operator|new
name|BlockReaderFactory
argument_list|(
operator|new
name|DfsClientConf
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|setFileName
argument_list|(
name|src
argument_list|)
operator|.
name|setBlock
argument_list|(
name|block
argument_list|)
operator|.
name|setBlockToken
argument_list|(
name|lblock
operator|.
name|getBlockToken
argument_list|()
argument_list|)
operator|.
name|setInetSocketAddress
argument_list|(
name|targetAddr
argument_list|)
operator|.
name|setStartOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|setLength
argument_list|(
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
operator|.
name|setVerifyChecksum
argument_list|(
literal|true
argument_list|)
operator|.
name|setClientName
argument_list|(
literal|"TestStripeLayoutWrite"
argument_list|)
operator|.
name|setDatanodeInfo
argument_list|(
name|nodes
index|[
literal|0
index|]
argument_list|)
operator|.
name|setCachingStrategy
argument_list|(
name|CachingStrategy
operator|.
name|newDefaultStrategy
argument_list|()
argument_list|)
operator|.
name|setClientCacheContext
argument_list|(
name|ClientContext
operator|.
name|getFromConf
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
operator|.
name|setRemotePeerFactory
argument_list|(
operator|new
name|RemotePeerFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Peer
name|newConnectedPeer
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|DatanodeID
name|datanodeId
parameter_list|)
throws|throws
name|IOException
block|{
name|Peer
name|peer
init|=
literal|null
decl_stmt|;
name|Socket
name|sock
init|=
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|createSocket
argument_list|()
decl_stmt|;
try|try
block|{
name|sock
operator|.
name|connect
argument_list|(
name|addr
argument_list|,
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|sock
operator|.
name|setSoTimeout
argument_list|(
name|HdfsServerConstants
operator|.
name|READ_TIMEOUT
argument_list|)
expr_stmt|;
name|peer
operator|=
name|TcpPeerServer
operator|.
name|peerFromSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|peer
operator|==
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|peer
return|;
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|blockReader
operator|.
name|readAll
argument_list|(
name|blockBytes
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|blockReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// check if we write the data correctly
for|for
control|(
name|int
name|blkIdxInGroup
init|=
literal|0
init|;
name|blkIdxInGroup
operator|<
name|dataBlockBytes
operator|.
name|length
condition|;
name|blkIdxInGroup
operator|++
control|)
block|{
specifier|final
name|byte
index|[]
name|actualBlkBytes
init|=
name|dataBlockBytes
index|[
name|blkIdxInGroup
index|]
decl_stmt|;
if|if
condition|(
name|actualBlkBytes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|int
name|posInBlk
init|=
literal|0
init|;
name|posInBlk
operator|<
name|actualBlkBytes
operator|.
name|length
condition|;
name|posInBlk
operator|++
control|)
block|{
name|byte
name|expected
decl_stmt|;
comment|// calculate the position of this byte in the file
name|long
name|posInFile
init|=
name|StripedBlockUtil
operator|.
name|offsetInBlkToOffsetInBG
argument_list|(
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|posInBlk
argument_list|,
name|blkIdxInGroup
argument_list|)
operator|+
name|group
operator|*
name|blockSize
operator|*
name|dataBlocks
decl_stmt|;
if|if
condition|(
name|posInFile
operator|>=
name|writeBytes
condition|)
block|{
name|expected
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|expected
operator|=
name|getByte
argument_list|(
name|posInFile
argument_list|)
expr_stmt|;
block|}
name|String
name|s
init|=
literal|"Unexpected byte "
operator|+
name|actualBlkBytes
index|[
name|posInBlk
index|]
operator|+
literal|", expect "
operator|+
name|expected
operator|+
literal|". Block group index is "
operator|+
name|group
operator|+
literal|", stripe index is "
operator|+
name|posInBlk
operator|/
name|cellSize
operator|+
literal|", cell index is "
operator|+
name|blkIdxInGroup
operator|+
literal|", byte index is "
operator|+
name|posInBlk
operator|%
name|cellSize
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
argument_list|,
name|expected
argument_list|,
name|actualBlkBytes
index|[
name|posInBlk
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// verify the parity blocks
specifier|final
name|ByteBuffer
index|[]
name|parityBuffers
init|=
operator|new
name|ByteBuffer
index|[
name|parityBlocks
index|]
decl_stmt|;
specifier|final
name|long
name|groupSize
init|=
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|get
argument_list|(
name|group
argument_list|)
operator|.
name|getBlockSize
argument_list|()
decl_stmt|;
name|int
name|parityBlkSize
init|=
operator|(
name|int
operator|)
name|StripedBlockUtil
operator|.
name|getInternalBlockLength
argument_list|(
name|groupSize
argument_list|,
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|dataBlocks
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parityBlocks
condition|;
name|i
operator|++
control|)
block|{
name|parityBuffers
index|[
name|i
index|]
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|parityBlkSize
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numStripes
init|=
call|(
name|int
call|)
argument_list|(
name|groupSize
operator|-
literal|1
argument_list|)
operator|/
name|stripeDataSize
argument_list|()
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numStripes
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|parityCellSize
init|=
name|i
operator|<
name|numStripes
operator|-
literal|1
operator|||
name|parityBlkSize
operator|%
name|cellSize
operator|==
literal|0
condition|?
name|cellSize
else|:
name|parityBlkSize
operator|%
name|cellSize
decl_stmt|;
name|ByteBuffer
index|[]
name|stripeBuf
init|=
operator|new
name|ByteBuffer
index|[
name|dataBlocks
index|]
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|stripeBuf
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|stripeBuf
index|[
name|k
index|]
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|cellSize
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|dataBlocks
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|dataBlockBytes
index|[
name|j
index|]
operator|!=
literal|null
condition|)
block|{
name|int
name|length
init|=
name|Math
operator|.
name|min
argument_list|(
name|cellSize
argument_list|,
name|dataBlockBytes
index|[
name|j
index|]
operator|.
name|length
operator|-
name|cellSize
operator|*
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|stripeBuf
index|[
name|j
index|]
operator|.
name|put
argument_list|(
name|dataBlockBytes
index|[
name|j
index|]
argument_list|,
name|cellSize
operator|*
name|i
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|long
name|pos
init|=
name|stripeBuf
index|[
name|j
index|]
operator|.
name|position
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|parityCellSize
operator|-
name|pos
condition|;
name|k
operator|++
control|)
block|{
name|stripeBuf
index|[
name|j
index|]
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
name|stripeBuf
index|[
name|j
index|]
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
name|ByteBuffer
index|[]
name|parityBuf
init|=
operator|new
name|ByteBuffer
index|[
name|parityBlocks
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|parityBlocks
condition|;
name|j
operator|++
control|)
block|{
name|parityBuf
index|[
name|j
index|]
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|cellSize
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|parityCellSize
condition|;
name|k
operator|++
control|)
block|{
name|parityBuf
index|[
name|j
index|]
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
name|parityBuf
index|[
name|j
index|]
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
name|encoder
operator|.
name|encode
argument_list|(
name|stripeBuf
argument_list|,
name|parityBuf
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|parityBlocks
condition|;
name|j
operator|++
control|)
block|{
name|parityBuffers
index|[
name|j
index|]
operator|.
name|put
argument_list|(
name|parityBuf
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parityBlocks
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|parityBuffers
index|[
name|i
index|]
operator|.
name|array
argument_list|()
argument_list|,
name|parityBlockBytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testReadWriteOneFile (String src, int writeBytes)
specifier|private
name|void
name|testReadWriteOneFile
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|writeBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|TestPath
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|generateBytes
argument_list|(
name|writeBytes
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|TestPath
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
comment|//check file length
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TestPath
argument_list|)
decl_stmt|;
name|long
name|fileLength
init|=
name|status
operator|.
name|getLen
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileLength
operator|!=
name|writeBytes
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"File Length error: expect="
operator|+
name|writeBytes
operator|+
literal|", actual="
operator|+
name|fileLength
argument_list|)
expr_stmt|;
block|}
name|DFSStripedInputStream
name|dis
init|=
operator|new
name|DFSStripedInputStream
argument_list|(
name|fs
operator|.
name|getClient
argument_list|()
argument_list|,
name|src
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|writeBytes
operator|+
literal|100
index|]
decl_stmt|;
name|int
name|readLen
init|=
name|dis
operator|.
name|read
argument_list|(
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
name|readLen
operator|=
name|readLen
operator|>=
literal|0
condition|?
name|readLen
else|:
literal|0
expr_stmt|;
if|if
condition|(
name|readLen
operator|!=
name|writeBytes
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"The length of file is not correct."
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|writeBytes
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|getByte
argument_list|(
name|i
argument_list|)
operator|!=
name|buf
index|[
name|i
index|]
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Byte at i = "
operator|+
name|i
operator|+
literal|" is wrongly written."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

