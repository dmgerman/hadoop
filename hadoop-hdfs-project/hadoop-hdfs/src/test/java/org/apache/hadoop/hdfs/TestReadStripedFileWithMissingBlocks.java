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
name|BlockLocation
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|DatanodeReportType
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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

begin_comment
comment|/**  * Test reading a striped file when some of its blocks are missing (not included  * in the block locations returned by the NameNode).  */
end_comment

begin_class
DECL|class|TestReadStripedFileWithMissingBlocks
specifier|public
class|class
name|TestReadStripedFileWithMissingBlocks
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
name|TestReadStripedFileWithMissingBlocks
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|dfsClient
specifier|private
name|DFSClient
name|dfsClient
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
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
name|short
name|dataBlocks
init|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|final
name|short
name|parityBlocks
init|=
operator|(
name|short
operator|)
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
DECL|field|stripPerBlock
specifier|private
specifier|final
name|int
name|stripPerBlock
init|=
literal|4
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
init|=
name|stripPerBlock
operator|*
name|cellSize
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
comment|// Starting with two more datanodes, minimum 9 should be up for
comment|// test to pass.
DECL|field|numDNs
specifier|private
specifier|final
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
DECL|field|fileLength
specifier|private
specifier|final
name|int
name|fileLength
init|=
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
decl_stmt|;
annotation|@
name|Rule
DECL|field|globalTimeout
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
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
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY
argument_list|,
literal|0
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
name|setErasureCodingPolicy
argument_list|(
literal|"/"
argument_list|,
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|fs
operator|.
name|enableErasureCodingPolicy
argument_list|(
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|dfsClient
operator|=
operator|new
name|DFSClient
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadFileWithMissingBlocks ()
specifier|public
name|void
name|testReadFileWithMissingBlocks
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|setup
argument_list|()
expr_stmt|;
name|Path
name|srcPath
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|expected
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
operator|new
name|String
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|waitBlockGroupsReported
argument_list|(
name|fs
argument_list|,
name|srcPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyLength
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|missingData
init|=
literal|1
init|;
name|missingData
operator|<=
name|dataBlocks
condition|;
name|missingData
operator|++
control|)
block|{
for|for
control|(
name|int
name|missingParity
init|=
literal|0
init|;
name|missingParity
operator|<=
name|parityBlocks
operator|-
name|missingData
condition|;
name|missingParity
operator|++
control|)
block|{
name|readFileWithMissingBlocks
argument_list|(
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|missingData
argument_list|,
name|missingParity
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readFileWithMissingBlocks (Path srcPath, int fileLength, int missingDataNum, int missingParityNum, byte[] expected)
specifier|private
name|void
name|readFileWithMissingBlocks
parameter_list|(
name|Path
name|srcPath
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|int
name|missingDataNum
parameter_list|,
name|int
name|missingParityNum
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"readFileWithMissingBlocks: ("
operator|+
name|missingDataNum
operator|+
literal|","
operator|+
name|missingParityNum
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|int
name|dataBlocks
init|=
operator|(
name|fileLength
operator|-
literal|1
operator|)
operator|/
name|cellSize
operator|+
literal|1
decl_stmt|;
name|BlockLocation
index|[]
name|locs
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|srcPath
argument_list|,
literal|0
argument_list|,
name|cellSize
argument_list|)
decl_stmt|;
name|int
index|[]
name|missingDataNodes
init|=
operator|new
name|int
index|[
name|missingDataNum
operator|+
name|missingParityNum
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
name|missingDataNum
condition|;
name|i
operator|++
control|)
block|{
name|missingDataNodes
index|[
name|i
index|]
operator|=
name|i
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
name|missingParityNum
condition|;
name|i
operator|++
control|)
block|{
name|missingDataNodes
index|[
name|i
operator|+
name|missingDataNum
index|]
operator|=
name|i
operator|+
name|Math
operator|.
name|min
argument_list|(
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
argument_list|,
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
name|stopDataNodes
argument_list|(
name|locs
argument_list|,
name|missingDataNodes
argument_list|)
expr_stmt|;
comment|// make sure there are missing block locations
name|BlockLocation
index|[]
name|newLocs
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|srcPath
argument_list|,
literal|0
argument_list|,
name|cellSize
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newLocs
index|[
literal|0
index|]
operator|.
name|getNames
argument_list|()
operator|.
name|length
operator|<
name|locs
index|[
literal|0
index|]
operator|.
name|getNames
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|byte
index|[]
name|smallBuf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|byte
index|[]
name|largeBuf
init|=
operator|new
name|byte
index|[
name|fileLength
operator|+
literal|100
index|]
decl_stmt|;
name|StripedFileTestUtil
operator|.
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|ecPolicy
argument_list|,
name|blockGroupSize
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|smallBuf
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyPread
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|restartDeadDataNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|restartDeadDataNodes ()
specifier|private
name|void
name|restartDeadDataNodes
parameter_list|()
throws|throws
name|IOException
block|{
name|DatanodeInfo
index|[]
name|deadNodes
init|=
name|dfsClient
operator|.
name|datanodeReport
argument_list|(
name|DatanodeReportType
operator|.
name|DEAD
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|dnInfo
range|:
name|deadNodes
control|)
block|{
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnInfo
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
block|}
DECL|method|stopDataNodes (BlockLocation[] locs, int[] datanodes)
specifier|private
name|void
name|stopDataNodes
parameter_list|(
name|BlockLocation
index|[]
name|locs
parameter_list|,
name|int
index|[]
name|datanodes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|locs
operator|!=
literal|null
operator|&&
name|locs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|failedDNIdx
range|:
name|datanodes
control|)
block|{
name|String
name|name
init|=
operator|(
name|locs
index|[
literal|0
index|]
operator|.
name|getNames
argument_list|()
operator|)
index|[
name|failedDNIdx
index|]
decl_stmt|;
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|int
name|port
init|=
name|dn
operator|.
name|getXferPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|port
argument_list|)
argument_list|)
condition|)
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|setDataNodeDead
argument_list|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stop datanode "
operator|+
name|failedDNIdx
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

