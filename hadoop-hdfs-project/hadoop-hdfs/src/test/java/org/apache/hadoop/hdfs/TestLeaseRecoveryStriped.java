begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|ToStringBuilder
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
name|FSDataOutputStream
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
name|FileSystem
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
name|HdfsClientConfigKeys
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
name|ErasureCodingPolicy
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
name|BlockRecoveryWorker
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
name|DataNode
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
name|security
operator|.
name|UserGroupInformation
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
name|test
operator|.
name|GenericTestUtils
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
name|test
operator|.
name|Whitebox
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadLocalRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_class
DECL|class|TestLeaseRecoveryStriped
specifier|public
class|class
name|TestLeaseRecoveryStriped
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestLeaseRecoveryStriped
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
decl_stmt|;
DECL|field|dataBlocks
specifier|private
specifier|final
name|int
name|dataBlocks
init|=
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|final
name|int
name|parityBlocks
init|=
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
init|=
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
decl_stmt|;
DECL|field|stripeSize
specifier|private
specifier|final
name|int
name|stripeSize
init|=
name|dataBlocks
operator|*
name|cellSize
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
DECL|field|blockGroupSize
specifier|private
specifier|final
name|int
name|blockGroupSize
init|=
name|blockSize
operator|*
name|dataBlocks
decl_stmt|;
DECL|field|bytesPerChecksum
specifier|private
specifier|static
specifier|final
name|int
name|bytesPerChecksum
init|=
literal|512
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DataNode
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DFSStripedOutputStream
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|BlockRecoveryWorker
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DataStreamer
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
DECL|field|fakeUsername
specifier|static
specifier|private
specifier|final
name|String
name|fakeUsername
init|=
literal|"fakeUser1"
decl_stmt|;
DECL|field|fakeGroup
specifier|static
specifier|private
specifier|final
name|String
name|fakeGroup
init|=
literal|"supergroup"
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|p
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"testfile"
argument_list|)
decl_stmt|;
DECL|field|testFileLength
specifier|private
specifier|final
name|int
name|testFileLength
init|=
operator|(
name|stripesPerBlock
operator|-
literal|1
operator|)
operator|*
name|stripeSize
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
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
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
name|conf
operator|.
name|setLong
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CLIENT_SOCKET_TIMEOUT_KEY
argument_list|,
literal|60000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
decl_stmt|;
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
name|waitActive
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|enableErasureCodingPolicy
argument_list|(
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|dir
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
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
DECL|class|BlockLengths
specifier|private
specifier|static
class|class
name|BlockLengths
block|{
DECL|field|blockLengths
specifier|private
specifier|final
name|int
index|[]
name|blockLengths
decl_stmt|;
DECL|field|safeLength
specifier|private
specifier|final
name|long
name|safeLength
decl_stmt|;
DECL|method|BlockLengths (ErasureCodingPolicy policy, int[] blockLengths)
name|BlockLengths
parameter_list|(
name|ErasureCodingPolicy
name|policy
parameter_list|,
name|int
index|[]
name|blockLengths
parameter_list|)
block|{
name|this
operator|.
name|blockLengths
operator|=
name|blockLengths
expr_stmt|;
name|long
index|[]
name|longArray
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|blockLengths
argument_list|)
operator|.
name|asLongStream
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|this
operator|.
name|safeLength
operator|=
name|StripedBlockUtil
operator|.
name|getSafeLength
argument_list|(
name|policy
argument_list|,
name|longArray
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|ToStringBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|append
argument_list|(
literal|"blockLengths"
argument_list|,
name|getBlockLengths
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"safeLength"
argument_list|,
name|getSafeLength
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Length of each block in a block group.      */
DECL|method|getBlockLengths ()
specifier|public
name|int
index|[]
name|getBlockLengths
parameter_list|()
block|{
return|return
name|blockLengths
return|;
block|}
comment|/**      * Safe length, calculated by the block lengths.      */
DECL|method|getSafeLength ()
specifier|public
name|long
name|getSafeLength
parameter_list|()
block|{
return|return
name|safeLength
return|;
block|}
block|}
DECL|method|getBlockLengthsSuite ()
specifier|private
name|BlockLengths
index|[]
name|getBlockLengthsSuite
parameter_list|()
block|{
specifier|final
name|int
name|groups
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|minNumCell
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|maxNumCell
init|=
name|stripesPerBlock
decl_stmt|;
specifier|final
name|int
name|minNumDelta
init|=
operator|-
literal|4
decl_stmt|;
specifier|final
name|int
name|maxNumDelta
init|=
literal|2
decl_stmt|;
name|BlockLengths
index|[]
name|suite
init|=
operator|new
name|BlockLengths
index|[
name|groups
index|]
decl_stmt|;
name|Random
name|random
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
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
name|groups
condition|;
name|i
operator|++
control|)
block|{
name|int
index|[]
name|blockLengths
init|=
operator|new
name|int
index|[
name|dataBlocks
operator|+
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
name|blockLengths
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// Choose a random number of cells for the block
name|int
name|numCell
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxNumCell
operator|-
name|minNumCell
operator|+
literal|1
argument_list|)
operator|+
name|minNumCell
decl_stmt|;
comment|// For data blocks, jitter the length a bit
name|int
name|numDelta
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|groups
operator|-
literal|1
operator|&&
name|j
operator|<
name|dataBlocks
condition|)
block|{
name|numDelta
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxNumDelta
operator|-
name|minNumDelta
operator|+
literal|1
argument_list|)
operator|+
name|minNumDelta
expr_stmt|;
block|}
name|blockLengths
index|[
name|j
index|]
operator|=
operator|(
name|cellSize
operator|*
name|numCell
operator|)
operator|+
operator|(
name|bytesPerChecksum
operator|*
name|numDelta
operator|)
expr_stmt|;
block|}
name|suite
index|[
name|i
index|]
operator|=
operator|new
name|BlockLengths
argument_list|(
name|ecPolicy
argument_list|,
name|blockLengths
argument_list|)
expr_stmt|;
block|}
return|return
name|suite
return|;
block|}
DECL|field|blockLengthsSuite
specifier|private
specifier|final
name|BlockLengths
index|[]
name|blockLengthsSuite
init|=
name|getBlockLengthsSuite
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testLeaseRecovery ()
specifier|public
name|void
name|testLeaseRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"blockLengthsSuite: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|blockLengthsSuite
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockLengthsSuite
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BlockLengths
name|blockLengths
init|=
name|blockLengthsSuite
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|runTest
argument_list|(
name|blockLengths
operator|.
name|getBlockLengths
argument_list|()
argument_list|,
name|blockLengths
operator|.
name|getSafeLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"failed testCase at i="
operator|+
name|i
operator|+
literal|", blockLengths="
operator|+
name|blockLengths
operator|+
literal|"\n"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|runTest (int[] blockLengths, long safeLength)
specifier|private
name|void
name|runTest
parameter_list|(
name|int
index|[]
name|blockLengths
parameter_list|,
name|long
name|safeLength
parameter_list|)
throws|throws
name|Exception
block|{
name|writePartialBlocks
argument_list|(
name|blockLengths
argument_list|)
expr_stmt|;
name|int
name|checkDataLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|testFileLength
argument_list|,
operator|(
name|int
operator|)
name|safeLength
argument_list|)
decl_stmt|;
name|recoverLease
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|oldGS
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|oldGS
operator|.
name|add
argument_list|(
literal|1001L
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|checkData
argument_list|(
name|dfs
argument_list|,
name|p
argument_list|,
name|checkDataLength
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
argument_list|()
argument_list|,
name|oldGS
argument_list|,
name|blockGroupSize
argument_list|)
expr_stmt|;
comment|// After recovery, storages are reported by primary DN. we should verify
comment|// storages reported by blockReport.
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitFirstBRCompleted
argument_list|(
literal|0
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|checkData
argument_list|(
name|dfs
argument_list|,
name|p
argument_list|,
name|checkDataLength
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|DatanodeInfo
argument_list|>
argument_list|()
argument_list|,
name|oldGS
argument_list|,
name|blockGroupSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write a file with blocks of different lengths.    *    * This method depends on completing before the DFS socket timeout.    * Otherwise, the client will mark timed-out streamers as failed, and the    * write will fail if there are too many failed streamers.    *    * @param blockLengths lengths of blocks to write    * @throws Exception    */
DECL|method|writePartialBlocks (int[] blockLengths)
specifier|private
name|void
name|writePartialBlocks
parameter_list|(
name|int
index|[]
name|blockLengths
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|FSDataOutputStream
name|out
init|=
name|dfs
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|DFSStripedOutputStream
name|stripedOut
init|=
operator|(
name|DFSStripedOutputStream
operator|)
name|out
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|int
index|[]
name|posToKill
init|=
name|getPosToKill
argument_list|(
name|blockLengths
argument_list|)
decl_stmt|;
name|int
name|checkingPos
init|=
name|nextCheckingPos
argument_list|(
name|posToKill
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|stoppedStreamerIndexes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|testFileLength
condition|;
name|pos
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|StripedFileTestUtil
operator|.
name|getByte
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|==
name|checkingPos
condition|)
block|{
for|for
control|(
name|int
name|index
range|:
name|getIndexToStop
argument_list|(
name|posToKill
argument_list|,
name|pos
argument_list|)
control|)
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|stripedOut
operator|.
name|enqueueAllCurrentPackets
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping block stream idx {} at file offset {} block "
operator|+
literal|"length {}"
argument_list|,
name|index
argument_list|,
name|pos
argument_list|,
name|blockLengths
index|[
name|index
index|]
argument_list|)
expr_stmt|;
name|StripedDataStreamer
name|s
init|=
name|stripedOut
operator|.
name|getStripedDataStreamer
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|waitStreamerAllAcked
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|waitByteSent
argument_list|(
name|s
argument_list|,
name|blockLengths
index|[
name|index
index|]
argument_list|)
expr_stmt|;
name|stopBlockStream
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|stoppedStreamerIndexes
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|checkingPos
operator|=
name|nextCheckingPos
argument_list|(
name|posToKill
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
comment|// Flush everything
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|stripedOut
operator|.
name|enqueueAllCurrentPackets
argument_list|()
expr_stmt|;
comment|// Wait for streamers that weren't killed above to be written out
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockLengths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|stoppedStreamerIndexes
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|StripedDataStreamer
name|s
init|=
name|stripedOut
operator|.
name|getStripedDataStreamer
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for block stream idx {} to reach length {}"
argument_list|,
name|i
argument_list|,
name|blockLengths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|waitStreamerAllAcked
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|DFSTestUtil
operator|.
name|abortStream
argument_list|(
name|stripedOut
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|nextCheckingPos (int[] posToKill, int curPos)
specifier|private
name|int
name|nextCheckingPos
parameter_list|(
name|int
index|[]
name|posToKill
parameter_list|,
name|int
name|curPos
parameter_list|)
block|{
name|int
name|checkingPos
init|=
name|Integer
operator|.
name|MAX_VALUE
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
name|posToKill
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|posToKill
index|[
name|i
index|]
operator|>
name|curPos
condition|)
block|{
name|checkingPos
operator|=
name|Math
operator|.
name|min
argument_list|(
name|checkingPos
argument_list|,
name|posToKill
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|checkingPos
return|;
block|}
DECL|method|getPosToKill (int[] blockLengths)
specifier|private
name|int
index|[]
name|getPosToKill
parameter_list|(
name|int
index|[]
name|blockLengths
parameter_list|)
block|{
name|int
index|[]
name|posToKill
init|=
operator|new
name|int
index|[
name|dataBlocks
operator|+
name|parityBlocks
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
name|dataBlocks
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numStripe
init|=
operator|(
name|blockLengths
index|[
name|i
index|]
operator|-
literal|1
operator|)
operator|/
name|cellSize
decl_stmt|;
name|posToKill
index|[
name|i
index|]
operator|=
name|numStripe
operator|*
name|stripeSize
operator|+
name|i
operator|*
name|cellSize
operator|+
name|blockLengths
index|[
name|i
index|]
operator|%
name|cellSize
expr_stmt|;
if|if
condition|(
name|blockLengths
index|[
name|i
index|]
operator|%
name|cellSize
operator|==
literal|0
condition|)
block|{
name|posToKill
index|[
name|i
index|]
operator|+=
name|cellSize
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|dataBlocks
init|;
name|i
operator|<
name|dataBlocks
operator|+
name|parityBlocks
condition|;
name|i
operator|++
control|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|blockLengths
index|[
name|i
index|]
operator|%
name|cellSize
operator|==
literal|0
argument_list|)
expr_stmt|;
name|int
name|numStripe
init|=
operator|(
name|blockLengths
index|[
name|i
index|]
operator|)
operator|/
name|cellSize
decl_stmt|;
name|posToKill
index|[
name|i
index|]
operator|=
name|numStripe
operator|*
name|stripeSize
expr_stmt|;
block|}
return|return
name|posToKill
return|;
block|}
DECL|method|getIndexToStop (int[] posToKill, int pos)
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|getIndexToStop
parameter_list|(
name|int
index|[]
name|posToKill
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|indices
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
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
name|posToKill
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|pos
operator|==
name|posToKill
index|[
name|i
index|]
condition|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|indices
return|;
block|}
DECL|method|waitByteSent (final StripedDataStreamer s, final long byteSent)
specifier|private
name|void
name|waitByteSent
parameter_list|(
specifier|final
name|StripedDataStreamer
name|s
parameter_list|,
specifier|final
name|long
name|byteSent
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|s
operator|.
name|bytesSent
operator|>=
name|byteSent
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timeout waiting for streamer "
operator|+
name|s
operator|+
literal|". Sent="
operator|+
name|s
operator|.
name|bytesSent
operator|+
literal|", expected="
operator|+
name|byteSent
argument_list|)
throw|;
block|}
block|}
comment|/**    * Stop the block stream without immediately inducing a hard failure.    * Packets can continue to be queued until the streamer hits a socket timeout.    *    * @param s    * @throws Exception    */
DECL|method|stopBlockStream (StripedDataStreamer s)
specifier|private
name|void
name|stopBlockStream
parameter_list|(
name|StripedDataStreamer
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|NullOutputStream
name|nullOutputStream
init|=
operator|new
name|IOUtils
operator|.
name|NullOutputStream
argument_list|()
decl_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|s
argument_list|,
literal|"blockStream"
argument_list|,
operator|new
name|DataOutputStream
argument_list|(
name|nullOutputStream
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|recoverLease ()
specifier|private
name|void
name|recoverLease
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DistributedFileSystem
name|dfs2
init|=
operator|(
name|DistributedFileSystem
operator|)
name|getFSAsAnotherUser
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
return|return
name|dfs2
operator|.
name|recoverLease
argument_list|(
name|p
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|5000
argument_list|,
literal|24000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timeout waiting for recoverLease()"
argument_list|)
throw|;
block|}
block|}
DECL|method|getFSAsAnotherUser (final Configuration c)
specifier|private
name|FileSystem
name|getFSAsAnotherUser
parameter_list|(
specifier|final
name|Configuration
name|c
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|c
argument_list|)
argument_list|,
name|c
argument_list|,
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|fakeUsername
argument_list|,
operator|new
name|String
index|[]
block|{
name|fakeGroup
block|}
argument_list|)
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|waitStreamerAllAcked (DataStreamer s)
specifier|public
specifier|static
name|void
name|waitStreamerAllAcked
parameter_list|(
name|DataStreamer
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|toWaitFor
init|=
name|s
operator|.
name|getLastQueuedSeqno
argument_list|()
decl_stmt|;
name|s
operator|.
name|waitForAckedSeqno
argument_list|(
name|toWaitFor
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

