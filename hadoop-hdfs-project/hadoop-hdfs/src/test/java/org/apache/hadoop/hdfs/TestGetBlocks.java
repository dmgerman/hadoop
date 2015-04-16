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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|util
operator|.
name|HashMap
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
name|Map
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
name|CommonConfigurationKeys
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
name|protocol
operator|.
name|Block
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
name|ClientProtocol
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
name|protocol
operator|.
name|HdfsConstantsClient
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
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|server
operator|.
name|datanode
operator|.
name|DataNodeTestUtils
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
name|namenode
operator|.
name|NameNode
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
name|protocol
operator|.
name|BlocksWithLocations
operator|.
name|BlockWithLocations
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
name|protocol
operator|.
name|NamenodeProtocol
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
name|ipc
operator|.
name|RemoteException
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

begin_comment
comment|/**  * This class tests if getblocks request works correctly.  */
end_comment

begin_class
DECL|class|TestGetBlocks
specifier|public
class|class
name|TestGetBlocks
block|{
DECL|field|blockSize
specifier|private
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|8192
decl_stmt|;
DECL|field|racks
specifier|private
specifier|static
specifier|final
name|String
name|racks
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"/d1/r1"
block|,
literal|"/d1/r1"
block|,
literal|"/d1/r2"
block|,
literal|"/d1/r2"
block|,
literal|"/d1/r2"
block|,
literal|"/d2/r3"
block|,
literal|"/d2/r3"
block|}
decl_stmt|;
DECL|field|numDatanodes
specifier|private
specifier|static
specifier|final
name|int
name|numDatanodes
init|=
name|racks
operator|.
name|length
decl_stmt|;
comment|/**    * Stop the heartbeat of a datanode in the MiniDFSCluster    *     * @param cluster    *          The MiniDFSCluster    * @param hostName    *          The hostName of the datanode to be stopped    * @return The DataNode whose heartbeat has been stopped    */
DECL|method|stopDataNodeHeartbeat (MiniDFSCluster cluster, String hostName)
specifier|private
name|DataNode
name|stopDataNodeHeartbeat
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|String
name|hostName
parameter_list|)
block|{
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
if|if
condition|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|hostName
argument_list|)
condition|)
block|{
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|dn
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|dn
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Test if the datanodes returned by    * {@link ClientProtocol#getBlockLocations(String, long, long)} is correct    * when stale nodes checking is enabled. Also test during the scenario when 1)    * stale nodes checking is enabled, 2) a writing is going on, 3) a datanode    * becomes stale happen simultaneously    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testReadSelectNonStaleDatanode ()
specifier|public
name|void
name|testReadSelectNonStaleDatanode
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AVOID_STALE_DATANODE_FOR_READ_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|long
name|staleInterval
init|=
literal|30
operator|*
literal|1000
operator|*
literal|60
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY
argument_list|,
name|staleInterval
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|numDatanodes
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|nodeInfoList
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanodeListForReport
argument_list|(
name|DatanodeReportType
operator|.
name|LIVE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of datanodes"
argument_list|,
name|numDatanodes
argument_list|,
name|nodeInfoList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|stm
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// do the writing but do not close the FSDataOutputStream
comment|// in order to mimic the ongoing writing
specifier|final
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
literal|"/file1"
argument_list|)
decl_stmt|;
name|stm
operator|=
name|fileSys
operator|.
name|create
argument_list|(
name|fileName
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[
operator|(
name|blockSize
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// We do not close the stream so that
comment|// the writing seems to be still ongoing
name|stm
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|LocatedBlocks
name|blocks
init|=
name|client
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|nodes
init|=
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|nodes
operator|.
name|length
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|DataNode
name|staleNode
init|=
literal|null
decl_stmt|;
name|DatanodeDescriptor
name|staleNodeInfo
init|=
literal|null
decl_stmt|;
comment|// stop the heartbeat of the first node
name|staleNode
operator|=
name|this
operator|.
name|stopDataNodeHeartbeat
argument_list|(
name|cluster
argument_list|,
name|nodes
index|[
literal|0
index|]
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|staleNode
argument_list|)
expr_stmt|;
comment|// set the first node as stale
name|staleNodeInfo
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|staleNode
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|staleNodeInfo
argument_list|,
operator|-
operator|(
name|staleInterval
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|blocksAfterStale
init|=
name|client
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|nodesAfterStale
init|=
name|blocksAfterStale
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|nodesAfterStale
operator|.
name|length
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nodesAfterStale
index|[
literal|2
index|]
operator|.
name|getHostName
argument_list|()
argument_list|,
name|nodes
index|[
literal|0
index|]
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
comment|// restart the staleNode's heartbeat
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|staleNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// reset the first node as non-stale, so as to avoid two stale nodes
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|staleNodeInfo
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|LocatedBlock
name|lastBlock
init|=
name|client
operator|.
name|getLocatedBlocks
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|nodes
operator|=
name|lastBlock
operator|.
name|getLocations
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|nodes
operator|.
name|length
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// stop the heartbeat of the first node for the last block
name|staleNode
operator|=
name|this
operator|.
name|stopDataNodeHeartbeat
argument_list|(
name|cluster
argument_list|,
name|nodes
index|[
literal|0
index|]
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|staleNode
argument_list|)
expr_stmt|;
comment|// set the node as stale
name|DatanodeDescriptor
name|dnDesc
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|staleNode
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|resetLastUpdatesWithOffset
argument_list|(
name|dnDesc
argument_list|,
operator|-
operator|(
name|staleInterval
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|LocatedBlock
name|lastBlockAfterStale
init|=
name|client
operator|.
name|getLocatedBlocks
argument_list|(
name|fileName
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|nodesAfterStale
operator|=
name|lastBlockAfterStale
operator|.
name|getLocations
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|nodesAfterStale
operator|.
name|length
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nodesAfterStale
index|[
literal|2
index|]
operator|.
name|getHostName
argument_list|()
argument_list|,
name|nodes
index|[
literal|0
index|]
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|stm
operator|!=
literal|null
condition|)
block|{
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** test getBlocks */
annotation|@
name|Test
DECL|method|testGetBlocks ()
specifier|public
name|void
name|testGetBlocks
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|short
name|REPLICATION_FACTOR
init|=
operator|(
name|short
operator|)
literal|2
decl_stmt|;
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPLICATION_FACTOR
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|long
name|fileLen
init|=
literal|2
operator|*
name|DEFAULT_BLOCK_SIZE
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/tmp.txt"
argument_list|)
argument_list|,
name|fileLen
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// get blocks& data nodes
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
decl_stmt|;
name|DatanodeInfo
index|[]
name|dataNodes
init|=
literal|null
decl_stmt|;
name|boolean
name|notWritten
decl_stmt|;
do|do
block|{
specifier|final
name|DFSClient
name|dfsclient
init|=
operator|new
name|DFSClient
argument_list|(
name|NameNode
operator|.
name|getAddress
argument_list|(
name|CONF
argument_list|)
argument_list|,
name|CONF
argument_list|)
decl_stmt|;
name|locatedBlocks
operator|=
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
literal|"/tmp.txt"
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|locatedBlocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|notWritten
operator|=
literal|false
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|dataNodes
operator|=
name|locatedBlocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getLocations
argument_list|()
expr_stmt|;
if|if
condition|(
name|dataNodes
operator|.
name|length
operator|!=
name|REPLICATION_FACTOR
condition|)
block|{
name|notWritten
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
break|break;
block|}
block|}
block|}
do|while
condition|(
name|notWritten
condition|)
do|;
comment|// get RPC client to namenode
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|NamenodeProtocol
name|namenode
init|=
name|NameNodeProxies
operator|.
name|createProxy
argument_list|(
name|CONF
argument_list|,
name|NameNode
operator|.
name|getUri
argument_list|(
name|addr
argument_list|)
argument_list|,
name|NamenodeProtocol
operator|.
name|class
argument_list|)
operator|.
name|getProxy
argument_list|()
decl_stmt|;
comment|// get blocks of size fileLen from dataNodes[0]
name|BlockWithLocations
index|[]
name|locs
decl_stmt|;
name|locs
operator|=
name|namenode
operator|.
name|getBlocks
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|fileLen
argument_list|)
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|locs
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|locs
index|[
literal|0
index|]
operator|.
name|getStorageIDs
argument_list|()
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|locs
index|[
literal|1
index|]
operator|.
name|getStorageIDs
argument_list|()
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// get blocks of size BlockSize from dataNodes[0]
name|locs
operator|=
name|namenode
operator|.
name|getBlocks
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|locs
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|locs
index|[
literal|0
index|]
operator|.
name|getStorageIDs
argument_list|()
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// get blocks of size 1 from dataNodes[0]
name|locs
operator|=
name|namenode
operator|.
name|getBlocks
argument_list|(
name|dataNodes
index|[
literal|0
index|]
argument_list|,
literal|1
argument_list|)
operator|.
name|getBlocks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|locs
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|locs
index|[
literal|0
index|]
operator|.
name|getStorageIDs
argument_list|()
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// get blocks of size 0 from dataNodes[0]
name|getBlocksWithException
argument_list|(
name|namenode
argument_list|,
name|dataNodes
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// get blocks of size -1 from dataNodes[0]
name|getBlocksWithException
argument_list|(
name|namenode
argument_list|,
name|dataNodes
index|[
literal|0
index|]
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// get blocks of size BlockSize from a non-existent datanode
name|DatanodeInfo
name|info
init|=
name|DFSTestUtil
operator|.
name|getDatanodeInfo
argument_list|(
literal|"1.2.3.4"
argument_list|)
decl_stmt|;
name|getBlocksWithException
argument_list|(
name|namenode
argument_list|,
name|info
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getBlocksWithException (NamenodeProtocol namenode, DatanodeInfo datanode, long size)
specifier|private
name|void
name|getBlocksWithException
parameter_list|(
name|NamenodeProtocol
name|namenode
parameter_list|,
name|DatanodeInfo
name|datanode
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|getException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|namenode
operator|.
name|getBlocks
argument_list|(
name|DFSTestUtil
operator|.
name|getLocalDatanodeInfo
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
name|getException
operator|=
literal|true
expr_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getClassName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"HadoopIllegalArgumentException"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|getException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockKey ()
specifier|public
name|void
name|testBlockKey
parameter_list|()
block|{
name|Map
argument_list|<
name|Block
argument_list|,
name|Long
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|Block
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Random
name|RAN
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|RAN
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"seed="
operator|+
name|seed
argument_list|)
expr_stmt|;
name|RAN
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|long
index|[]
name|blkids
init|=
operator|new
name|long
index|[
literal|10
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
name|blkids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blkids
index|[
name|i
index|]
operator|=
literal|1000L
operator|+
name|RAN
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
operator|new
name|Block
argument_list|(
name|blkids
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|blkids
index|[
name|i
index|]
argument_list|)
argument_list|,
name|blkids
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"map="
operator|+
name|map
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|","
argument_list|,
literal|"\n  "
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
name|blkids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|blkids
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|HdfsConstantsClient
operator|.
name|GRANDFATHER_GENERATION_STAMP
argument_list|)
decl_stmt|;
name|Long
name|v
init|=
name|map
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|b
operator|+
literal|" => "
operator|+
name|v
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blkids
index|[
name|i
index|]
argument_list|,
name|v
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

