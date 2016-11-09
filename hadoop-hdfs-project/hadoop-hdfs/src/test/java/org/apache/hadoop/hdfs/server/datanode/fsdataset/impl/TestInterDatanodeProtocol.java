begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
package|package
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
name|fsdataset
operator|.
name|impl
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
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|SocketTimeoutException
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
name|DFSClientAdapter
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
name|DFSConfigKeys
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
name|DFSTestUtil
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
name|DistributedFileSystem
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|DatanodeInfo
operator|.
name|DatanodeInfoBuilder
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
name|RecoveryInProgressException
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
operator|.
name|ReplicaState
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
name|datanode
operator|.
name|FinalizedReplica
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
name|Replica
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
name|ReplicaInfo
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
name|ReplicaUnderRecovery
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|BlockRecoveryCommand
operator|.
name|RecoveringBlock
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
name|InterDatanodeProtocol
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
name|ReplicaRecoveryInfo
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
name|LongWritable
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
name|Writable
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
name|RPC
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
name|Server
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
name|util
operator|.
name|AutoCloseableLock
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * This tests InterDataNodeProtocol for block handling.   */
end_comment

begin_class
DECL|class|TestInterDatanodeProtocol
specifier|public
class|class
name|TestInterDatanodeProtocol
block|{
DECL|field|ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|ADDRESS
init|=
literal|"0.0.0.0"
decl_stmt|;
DECL|field|PING_INTERVAL
specifier|final
specifier|static
specifier|private
name|int
name|PING_INTERVAL
init|=
literal|1000
decl_stmt|;
DECL|field|MIN_SLEEP_TIME
specifier|final
specifier|static
specifier|private
name|int
name|MIN_SLEEP_TIME
init|=
literal|1000
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|class|TestServer
specifier|private
specifier|static
class|class
name|TestServer
extends|extends
name|Server
block|{
DECL|field|sleep
specifier|private
name|boolean
name|sleep
decl_stmt|;
DECL|field|responseClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Writable
argument_list|>
name|responseClass
decl_stmt|;
DECL|method|TestServer (int handlerCount, boolean sleep)
specifier|public
name|TestServer
parameter_list|(
name|int
name|handlerCount
parameter_list|,
name|boolean
name|sleep
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|handlerCount
argument_list|,
name|sleep
argument_list|,
name|LongWritable
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TestServer (int handlerCount, boolean sleep, Class<? extends Writable> paramClass, Class<? extends Writable> responseClass)
specifier|public
name|TestServer
parameter_list|(
name|int
name|handlerCount
parameter_list|,
name|boolean
name|sleep
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Writable
argument_list|>
name|paramClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Writable
argument_list|>
name|responseClass
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|ADDRESS
argument_list|,
literal|0
argument_list|,
name|paramClass
argument_list|,
name|handlerCount
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|sleep
operator|=
name|sleep
expr_stmt|;
name|this
operator|.
name|responseClass
operator|=
name|responseClass
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call (RPC.RpcKind rpcKind, String protocol, Writable param, long receiveTime)
specifier|public
name|Writable
name|call
parameter_list|(
name|RPC
operator|.
name|RpcKind
name|rpcKind
parameter_list|,
name|String
name|protocol
parameter_list|,
name|Writable
name|param
parameter_list|,
name|long
name|receiveTime
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|sleep
condition|)
block|{
comment|// sleep a bit
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|PING_INTERVAL
operator|+
name|MIN_SLEEP_TIME
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
if|if
condition|(
name|responseClass
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|responseClass
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|param
return|;
comment|// echo param as result
block|}
block|}
block|}
DECL|method|checkMetaInfo (ExtendedBlock b, DataNode dn)
specifier|public
specifier|static
name|void
name|checkMetaInfo
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|DataNode
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
name|Block
name|metainfo
init|=
name|DataNodeTestUtils
operator|.
name|getFSDataset
argument_list|(
name|dn
argument_list|)
operator|.
name|getStoredBlock
argument_list|(
name|b
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|metainfo
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|metainfo
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getLastLocatedBlock ( ClientProtocol namenode, String src)
specifier|public
specifier|static
name|LocatedBlock
name|getLastLocatedBlock
parameter_list|(
name|ClientProtocol
name|namenode
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
comment|//get block info for the last block
name|LocatedBlocks
name|locations
init|=
name|namenode
operator|.
name|getBlockLocations
argument_list|(
name|src
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks
init|=
name|locations
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
name|DataNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"blocks.size()="
operator|+
name|blocks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blocks
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
name|blocks
operator|.
name|get
argument_list|(
name|blocks
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** Test block MD access via a DN */
annotation|@
name|Test
DECL|method|testBlockMetaDataInfo ()
specifier|public
name|void
name|testBlockMetaDataInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|checkBlockMetaDataInfo
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** The same as above, but use hostnames for DN<->DN communication */
annotation|@
name|Test
DECL|method|testBlockMetaDataInfoWithHostname ()
specifier|public
name|void
name|testBlockMetaDataInfoWithHostname
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Linux"
argument_list|)
argument_list|)
expr_stmt|;
name|checkBlockMetaDataInfo
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * The following test first creates a file.    * It verifies the block information from a datanode.    * Then, it updates the block with new information and verifies again.    * @param useDnHostname whether DNs should connect to other DNs by hostname    */
DECL|method|checkBlockMetaDataInfo (boolean useDnHostname)
specifier|private
name|void
name|checkBlockMetaDataInfo
parameter_list|(
name|boolean
name|useDnHostname
parameter_list|)
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_USE_DN_HOSTNAME
argument_list|,
name|useDnHostname
argument_list|)
expr_stmt|;
if|if
condition|(
name|useDnHostname
condition|)
block|{
comment|// Since the mini cluster only listens on the loopback we have to
comment|// ensure the hostname used to access DNs maps to the loopback. We
comment|// do this by telling the DN to advertise localhost as its hostname
comment|// instead of the default hostname.
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
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
literal|3
argument_list|)
operator|.
name|checkDataNodeHostConfig
argument_list|(
literal|true
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
comment|//create a file
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|String
name|filestr
init|=
literal|"/foo"
decl_stmt|;
name|Path
name|filepath
init|=
operator|new
name|Path
argument_list|(
name|filestr
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|filepath
argument_list|,
literal|1024L
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|filepath
argument_list|)
argument_list|)
expr_stmt|;
comment|//get block info
name|LocatedBlock
name|locatedblock
init|=
name|getLastLocatedBlock
argument_list|(
name|DFSClientAdapter
operator|.
name|getDFSClient
argument_list|(
name|dfs
argument_list|)
operator|.
name|getNamenode
argument_list|()
argument_list|,
name|filestr
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|datanodeinfo
init|=
name|locatedblock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|datanodeinfo
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|//connect to a data node
name|DataNode
name|datanode
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|datanodeinfo
index|[
literal|0
index|]
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
name|InterDatanodeProtocol
name|idp
init|=
name|DataNodeTestUtils
operator|.
name|createInterDatanodeProtocolProxy
argument_list|(
name|datanode
argument_list|,
name|datanodeinfo
index|[
literal|0
index|]
argument_list|,
name|conf
argument_list|,
name|useDnHostname
argument_list|)
decl_stmt|;
comment|// Stop the block scanners.
name|datanode
operator|.
name|getBlockScanner
argument_list|()
operator|.
name|removeAllVolumeScanners
argument_list|()
expr_stmt|;
comment|//verify BlockMetaDataInfo
name|ExtendedBlock
name|b
init|=
name|locatedblock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|InterDatanodeProtocol
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"b="
operator|+
name|b
operator|+
literal|", "
operator|+
name|b
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|checkMetaInfo
argument_list|(
name|b
argument_list|,
name|datanode
argument_list|)
expr_stmt|;
name|long
name|recoveryId
init|=
name|b
operator|.
name|getGenerationStamp
argument_list|()
operator|+
literal|1
decl_stmt|;
name|idp
operator|.
name|initReplicaRecovery
argument_list|(
operator|new
name|RecoveringBlock
argument_list|(
name|b
argument_list|,
name|locatedblock
operator|.
name|getLocations
argument_list|()
argument_list|,
name|recoveryId
argument_list|)
argument_list|)
expr_stmt|;
comment|//verify updateBlock
name|ExtendedBlock
name|newblock
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|b
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|b
operator|.
name|getNumBytes
argument_list|()
operator|/
literal|2
argument_list|,
name|b
operator|.
name|getGenerationStamp
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|idp
operator|.
name|updateReplicaUnderRecovery
argument_list|(
name|b
argument_list|,
name|recoveryId
argument_list|,
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|newblock
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|checkMetaInfo
argument_list|(
name|newblock
argument_list|,
name|datanode
argument_list|)
expr_stmt|;
comment|// Verify correct null response trying to init recovery for a missing block
name|ExtendedBlock
name|badBlock
init|=
operator|new
name|ExtendedBlock
argument_list|(
literal|"fake-pool"
argument_list|,
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|idp
operator|.
name|initReplicaRecovery
argument_list|(
operator|new
name|RecoveringBlock
argument_list|(
name|badBlock
argument_list|,
name|locatedblock
operator|.
name|getLocations
argument_list|()
argument_list|,
name|recoveryId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
DECL|method|createReplicaInfo (Block b)
specifier|private
specifier|static
name|ReplicaInfo
name|createReplicaInfo
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
return|return
operator|new
name|FinalizedReplica
argument_list|(
name|b
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|assertEquals (ReplicaInfo originalInfo, ReplicaRecoveryInfo recoveryInfo)
specifier|private
specifier|static
name|void
name|assertEquals
parameter_list|(
name|ReplicaInfo
name|originalInfo
parameter_list|,
name|ReplicaRecoveryInfo
name|recoveryInfo
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalInfo
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|recoveryInfo
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalInfo
operator|.
name|getGenerationStamp
argument_list|()
argument_list|,
name|recoveryInfo
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalInfo
operator|.
name|getBytesOnDisk
argument_list|()
argument_list|,
name|recoveryInfo
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalInfo
operator|.
name|getState
argument_list|()
argument_list|,
name|recoveryInfo
operator|.
name|getOriginalReplicaState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test     * {@link FsDatasetImpl#initReplicaRecovery(String, ReplicaMap, Block, long, long)}    */
annotation|@
name|Test
DECL|method|testInitReplicaRecovery ()
specifier|public
name|void
name|testInitReplicaRecovery
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|firstblockid
init|=
literal|10000L
decl_stmt|;
specifier|final
name|long
name|gs
init|=
literal|7777L
decl_stmt|;
specifier|final
name|long
name|length
init|=
literal|22L
decl_stmt|;
specifier|final
name|ReplicaMap
name|map
init|=
operator|new
name|ReplicaMap
argument_list|(
operator|new
name|AutoCloseableLock
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|bpid
init|=
literal|"BP-TEST"
decl_stmt|;
specifier|final
name|Block
index|[]
name|blocks
init|=
operator|new
name|Block
index|[
literal|5
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
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|Block
argument_list|(
name|firstblockid
operator|+
name|i
argument_list|,
name|length
argument_list|,
name|gs
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|bpid
argument_list|,
name|createReplicaInfo
argument_list|(
name|blocks
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
comment|//normal case
specifier|final
name|Block
name|b
init|=
name|blocks
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|ReplicaInfo
name|originalInfo
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
decl_stmt|;
specifier|final
name|long
name|recoveryid
init|=
name|gs
operator|+
literal|1
decl_stmt|;
specifier|final
name|ReplicaRecoveryInfo
name|recoveryInfo
init|=
name|FsDatasetImpl
operator|.
name|initReplicaRecovery
argument_list|(
name|bpid
argument_list|,
name|map
argument_list|,
name|blocks
index|[
literal|0
index|]
argument_list|,
name|recoveryid
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|originalInfo
argument_list|,
name|recoveryInfo
argument_list|)
expr_stmt|;
specifier|final
name|ReplicaUnderRecovery
name|updatedInfo
init|=
operator|(
name|ReplicaUnderRecovery
operator|)
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalInfo
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|updatedInfo
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|recoveryid
argument_list|,
name|updatedInfo
operator|.
name|getRecoveryID
argument_list|()
argument_list|)
expr_stmt|;
comment|//recover one more time
specifier|final
name|long
name|recoveryid2
init|=
name|gs
operator|+
literal|2
decl_stmt|;
specifier|final
name|ReplicaRecoveryInfo
name|recoveryInfo2
init|=
name|FsDatasetImpl
operator|.
name|initReplicaRecovery
argument_list|(
name|bpid
argument_list|,
name|map
argument_list|,
name|blocks
index|[
literal|0
index|]
argument_list|,
name|recoveryid2
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|originalInfo
argument_list|,
name|recoveryInfo2
argument_list|)
expr_stmt|;
specifier|final
name|ReplicaUnderRecovery
name|updatedInfo2
init|=
operator|(
name|ReplicaUnderRecovery
operator|)
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|originalInfo
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|updatedInfo2
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|recoveryid2
argument_list|,
name|updatedInfo2
operator|.
name|getRecoveryID
argument_list|()
argument_list|)
expr_stmt|;
comment|//case RecoveryInProgressException
try|try
block|{
name|FsDatasetImpl
operator|.
name|initReplicaRecovery
argument_list|(
name|bpid
argument_list|,
name|map
argument_list|,
name|b
argument_list|,
name|recoveryid
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RecoveryInProgressException
name|ripe
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"GOOD: getting "
operator|+
name|ripe
argument_list|)
expr_stmt|;
block|}
block|}
block|{
comment|// BlockRecoveryFI_01: replica not found
specifier|final
name|long
name|recoveryid
init|=
name|gs
operator|+
literal|1
decl_stmt|;
specifier|final
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|firstblockid
operator|-
literal|1
argument_list|,
name|length
argument_list|,
name|gs
argument_list|)
decl_stmt|;
name|ReplicaRecoveryInfo
name|r
init|=
name|FsDatasetImpl
operator|.
name|initReplicaRecovery
argument_list|(
name|bpid
argument_list|,
name|map
argument_list|,
name|b
argument_list|,
name|recoveryid
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"Data-node should not have this replica."
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|{
comment|// BlockRecoveryFI_02: "THIS IS NOT SUPPOSED TO HAPPEN" with recovery id< gs
specifier|final
name|long
name|recoveryid
init|=
name|gs
operator|-
literal|1
decl_stmt|;
specifier|final
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|firstblockid
operator|+
literal|1
argument_list|,
name|length
argument_list|,
name|gs
argument_list|)
decl_stmt|;
try|try
block|{
name|FsDatasetImpl
operator|.
name|initReplicaRecovery
argument_list|(
name|bpid
argument_list|,
name|map
argument_list|,
name|b
argument_list|,
name|recoveryid
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"GOOD: getting "
operator|+
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
comment|// BlockRecoveryFI_03: Replica's gs is less than the block's gs
block|{
specifier|final
name|long
name|recoveryid
init|=
name|gs
operator|+
literal|1
decl_stmt|;
specifier|final
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
name|firstblockid
argument_list|,
name|length
argument_list|,
name|gs
operator|+
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|FsDatasetImpl
operator|.
name|initReplicaRecovery
argument_list|(
name|bpid
argument_list|,
name|map
argument_list|,
name|b
argument_list|,
name|recoveryid
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_XCEIVER_STOP_TIMEOUT_MILLIS_DEFAULT
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"InitReplicaRecovery should fail because replica's "
operator|+
literal|"gs is less than the block's gs"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"replica.getGenerationStamp()< block.getGenerationStamp(), block="
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**     * Test  for    * {@link FsDatasetImpl#updateReplicaUnderRecovery(ExtendedBlock, long, long)}     * */
annotation|@
name|Test
DECL|method|testUpdateReplicaUnderRecovery ()
specifier|public
name|void
name|testUpdateReplicaUnderRecovery
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
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
literal|3
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
comment|//create a file
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|String
name|filestr
init|=
literal|"/foo"
decl_stmt|;
name|Path
name|filepath
init|=
operator|new
name|Path
argument_list|(
name|filestr
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|filepath
argument_list|,
literal|1024L
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|//get block info
specifier|final
name|LocatedBlock
name|locatedblock
init|=
name|getLastLocatedBlock
argument_list|(
name|DFSClientAdapter
operator|.
name|getDFSClient
argument_list|(
name|dfs
argument_list|)
operator|.
name|getNamenode
argument_list|()
argument_list|,
name|filestr
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeInfo
index|[]
name|datanodeinfo
init|=
name|locatedblock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|datanodeinfo
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|//get DataNode and FSDataset objects
specifier|final
name|DataNode
name|datanode
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|datanodeinfo
index|[
literal|0
index|]
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|datanode
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//initReplicaRecovery
specifier|final
name|ExtendedBlock
name|b
init|=
name|locatedblock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
specifier|final
name|long
name|recoveryid
init|=
name|b
operator|.
name|getGenerationStamp
argument_list|()
operator|+
literal|1
decl_stmt|;
specifier|final
name|long
name|newlength
init|=
name|b
operator|.
name|getNumBytes
argument_list|()
operator|-
literal|1
decl_stmt|;
specifier|final
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsdataset
init|=
name|DataNodeTestUtils
operator|.
name|getFSDataset
argument_list|(
name|datanode
argument_list|)
decl_stmt|;
specifier|final
name|ReplicaRecoveryInfo
name|rri
init|=
name|fsdataset
operator|.
name|initReplicaRecovery
argument_list|(
operator|new
name|RecoveringBlock
argument_list|(
name|b
argument_list|,
literal|null
argument_list|,
name|recoveryid
argument_list|)
argument_list|)
decl_stmt|;
comment|//check replica
specifier|final
name|Replica
name|replica
init|=
name|cluster
operator|.
name|getFsDatasetTestUtils
argument_list|(
name|datanode
argument_list|)
operator|.
name|fetchReplica
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ReplicaState
operator|.
name|RUR
argument_list|,
name|replica
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|//check meta data before update
name|cluster
operator|.
name|getFsDatasetTestUtils
argument_list|(
name|datanode
argument_list|)
operator|.
name|checkStoredReplica
argument_list|(
name|replica
argument_list|)
expr_stmt|;
comment|//case "THIS IS NOT SUPPOSED TO HAPPEN"
comment|//with (block length) != (stored replica's on disk length).
block|{
comment|//create a block with same id and gs but different length.
specifier|final
name|ExtendedBlock
name|tmp
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|b
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|rri
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|rri
operator|.
name|getNumBytes
argument_list|()
operator|-
literal|1
argument_list|,
name|rri
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
comment|//update should fail
name|fsdataset
operator|.
name|updateReplicaUnderRecovery
argument_list|(
name|tmp
argument_list|,
name|recoveryid
argument_list|,
name|tmp
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|newlength
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"GOOD: getting "
operator|+
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
comment|//update
specifier|final
name|Replica
name|r
init|=
name|fsdataset
operator|.
name|updateReplicaUnderRecovery
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|b
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|rri
argument_list|)
argument_list|,
name|recoveryid
argument_list|,
name|rri
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|newlength
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getStorageUuid
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Test to verify that InterDatanode RPC timesout as expected when    *  the server DN does not respond.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SocketTimeoutException
operator|.
name|class
argument_list|)
DECL|method|testInterDNProtocolTimeout ()
specifier|public
name|void
name|testInterDNProtocolTimeout
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Server
name|server
init|=
operator|new
name|TestServer
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|DatanodeID
name|fakeDnId
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeID
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
name|DatanodeInfo
name|dInfo
init|=
operator|new
name|DatanodeInfoBuilder
argument_list|()
operator|.
name|setNodeID
argument_list|(
name|fakeDnId
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|InterDatanodeProtocol
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proxy
operator|=
name|DataNode
operator|.
name|createInterDataNodeProtocolProxy
argument_list|(
name|dInfo
argument_list|,
name|conf
argument_list|,
literal|500
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|initReplicaRecovery
argument_list|(
operator|new
name|RecoveringBlock
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
literal|"bpid"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected SocketTimeoutException exception, but did not get."
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

