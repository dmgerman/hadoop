begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|conf
operator|.
name|ReconfigurationException
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
name|fs
operator|.
name|StorageType
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
name|*
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
name|*
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
name|*
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
name|FsVolumeSpi
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
name|RoundRobinVolumeChoosingPolicy
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
name|VolumeChoosingPolicy
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
name|Node
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
name|util
operator|.
name|*
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
name|TimeUnit
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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

begin_comment
comment|/**  * Test to ensure that the StorageType and StorageID sent from Namenode  * to DFSClient are respected.  */
end_comment

begin_class
DECL|class|TestNamenodeStorageDirectives
specifier|public
class|class
name|TestNamenodeStorageDirectives
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
name|TestNamenodeStorageDirectives
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|512
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|startDFSCluster (int numNameNodes, int numDataNodes, int storagePerDataNode, StorageType[][] storageTypes)
specifier|private
name|void
name|startDFSCluster
parameter_list|(
name|int
name|numNameNodes
parameter_list|,
name|int
name|numDataNodes
parameter_list|,
name|int
name|storagePerDataNode
parameter_list|,
name|StorageType
index|[]
index|[]
name|storageTypes
parameter_list|)
throws|throws
name|IOException
block|{
name|startDFSCluster
argument_list|(
name|numNameNodes
argument_list|,
name|numDataNodes
argument_list|,
name|storagePerDataNode
argument_list|,
name|storageTypes
argument_list|,
name|RoundRobinVolumeChoosingPolicy
operator|.
name|class
argument_list|,
name|BlockPlacementPolicyDefault
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|startDFSCluster (int numNameNodes, int numDataNodes, int storagePerDataNode, StorageType[][] storageTypes, Class<? extends VolumeChoosingPolicy> volumeChoosingPolicy, Class<? extends BlockPlacementPolicy> blockPlacementPolicy)
specifier|private
name|void
name|startDFSCluster
parameter_list|(
name|int
name|numNameNodes
parameter_list|,
name|int
name|numDataNodes
parameter_list|,
name|int
name|storagePerDataNode
parameter_list|,
name|StorageType
index|[]
index|[]
name|storageTypes
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|VolumeChoosingPolicy
argument_list|>
name|volumeChoosingPolicy
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|BlockPlacementPolicy
argument_list|>
name|blockPlacementPolicy
parameter_list|)
throws|throws
name|IOException
block|{
name|shutdown
argument_list|()
expr_stmt|;
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
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
comment|/*      * Lower the DN heartbeat, DF rate, and recheck interval to one second      * so state about failures and datanode death propagates faster.      */
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
name|DFS_DF_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|/* Allow 1 volume failure */
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FSDATASET_VOLUME_CHOOSING_POLICY_KEY
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|VolumeChoosingPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|blockPlacementPolicy
argument_list|,
name|BlockPlacementPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|MiniDFSNNTopology
name|nnTopology
init|=
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
name|numNameNodes
argument_list|)
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
name|nnTopology
argument_list|(
name|nnTopology
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDataNodes
argument_list|)
operator|.
name|storagesPerDatanode
argument_list|(
name|storagePerDataNode
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|storageTypes
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
block|}
DECL|method|shutdown ()
specifier|private
name|void
name|shutdown
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|createFile (Path path, int numBlocks, short replicateFactor)
specifier|private
name|void
name|createFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|short
name|replicateFactor
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|createFile
argument_list|(
literal|0
argument_list|,
name|path
argument_list|,
name|numBlocks
argument_list|,
name|replicateFactor
argument_list|)
expr_stmt|;
block|}
DECL|method|createFile (int fsIdx, Path path, int numBlocks, short replicateFactor)
specifier|private
name|void
name|createFile
parameter_list|(
name|int
name|fsIdx
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|short
name|replicateFactor
parameter_list|)
throws|throws
name|IOException
throws|,
name|TimeoutException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|seed
init|=
literal|0
decl_stmt|;
specifier|final
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
name|fsIdx
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|BLOCK_SIZE
operator|*
name|numBlocks
argument_list|,
name|replicateFactor
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|replicateFactor
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyFileReplicasOnStorageType (Path path, int numBlocks, StorageType storageType)
specifier|private
name|boolean
name|verifyFileReplicasOnStorageType
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
throws|throws
name|IOException
block|{
name|MiniDFSCluster
operator|.
name|NameNodeInfo
name|info
init|=
name|cluster
operator|.
name|getNameNodeInfos
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|info
operator|.
name|nameNode
operator|.
name|getServiceRpcAddress
argument_list|()
decl_stmt|;
assert|assert
name|addr
operator|.
name|getPort
argument_list|()
operator|!=
literal|0
assert|;
name|DFSClient
name|client
init|=
operator|new
name|DFSClient
argument_list|(
name|addr
argument_list|,
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"verifyFileReplicasOnStorageType: file {} does not exist"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|long
name|fileLength
init|=
name|client
operator|.
name|getFileInfo
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|int
name|foundBlocks
init|=
literal|0
decl_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|client
operator|.
name|getLocatedBlocks
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|fileLength
argument_list|)
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|locatedBlock
range|:
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
for|for
control|(
name|StorageType
name|st
range|:
name|locatedBlock
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|st
operator|==
name|storageType
condition|)
block|{
name|foundBlocks
operator|++
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Found {}/{} blocks on StorageType {}"
argument_list|,
name|foundBlocks
argument_list|,
name|numBlocks
argument_list|,
name|storageType
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|isValid
init|=
name|foundBlocks
operator|>=
name|numBlocks
decl_stmt|;
return|return
name|isValid
return|;
block|}
DECL|method|testStorageTypes (StorageType[][] storageTypes, String storagePolicy, StorageType[] expectedStorageTypes, StorageType[] unexpectedStorageTypes)
specifier|private
name|void
name|testStorageTypes
parameter_list|(
name|StorageType
index|[]
index|[]
name|storageTypes
parameter_list|,
name|String
name|storagePolicy
parameter_list|,
name|StorageType
index|[]
name|expectedStorageTypes
parameter_list|,
name|StorageType
index|[]
name|unexpectedStorageTypes
parameter_list|)
throws|throws
name|ReconfigurationException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|IOException
block|{
specifier|final
name|int
name|numDataNodes
init|=
name|storageTypes
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|storagePerDataNode
init|=
name|storageTypes
index|[
literal|0
index|]
operator|.
name|length
decl_stmt|;
name|startDFSCluster
argument_list|(
literal|1
argument_list|,
name|numDataNodes
argument_list|,
name|storagePerDataNode
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|storagePolicy
argument_list|)
expr_stmt|;
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
specifier|final
name|short
name|replFactor
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|numBlocks
init|=
literal|10
decl_stmt|;
name|createFile
argument_list|(
name|testFile
argument_list|,
name|numBlocks
argument_list|,
name|replFactor
argument_list|)
expr_stmt|;
for|for
control|(
name|StorageType
name|storageType
range|:
name|expectedStorageTypes
control|)
block|{
name|assertTrue
argument_list|(
name|verifyFileReplicasOnStorageType
argument_list|(
name|testFile
argument_list|,
name|numBlocks
argument_list|,
name|storageType
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|StorageType
name|storageType
range|:
name|unexpectedStorageTypes
control|)
block|{
name|assertFalse
argument_list|(
name|verifyFileReplicasOnStorageType
argument_list|(
name|testFile
argument_list|,
name|numBlocks
argument_list|,
name|storageType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Verify that writing to SSD and DISK will write to the correct Storage    * Types.    * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testTargetStorageTypes ()
specifier|public
name|void
name|testTargetStorageTypes
parameter_list|()
throws|throws
name|ReconfigurationException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|IOException
block|{
comment|// DISK and not anything else.
name|testStorageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
argument_list|,
literal|"ONE_SSD"
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
argument_list|)
expr_stmt|;
comment|// only on SSD.
name|testStorageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
argument_list|,
literal|"ALL_SSD"
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|SSD
block|}
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
argument_list|)
expr_stmt|;
comment|// only on SSD.
name|testStorageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
argument_list|,
literal|"ALL_SSD"
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|SSD
block|}
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
argument_list|)
expr_stmt|;
comment|// DISK and not anything else.
name|testStorageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
argument_list|,
literal|"HOT"
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|DISK
block|}
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
argument_list|)
expr_stmt|;
name|testStorageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|}
argument_list|,
literal|"WARM"
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|}
argument_list|)
expr_stmt|;
name|testStorageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|}
argument_list|,
literal|"COLD"
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|ARCHIVE
block|}
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
argument_list|)
expr_stmt|;
comment|// We wait for Lasy Persist to write to disk.
name|testStorageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|DISK
block|}
block|}
argument_list|,
literal|"LAZY_PERSIST"
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|DISK
block|}
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * A VolumeChoosingPolicy test stub used to verify that the storageId passed    * in is indeed in the list of volumes.    * @param<V>    */
DECL|class|TestVolumeChoosingPolicy
specifier|private
specifier|static
class|class
name|TestVolumeChoosingPolicy
parameter_list|<
name|V
extends|extends
name|FsVolumeSpi
parameter_list|>
extends|extends
name|RoundRobinVolumeChoosingPolicy
argument_list|<
name|V
argument_list|>
block|{
DECL|field|expectedStorageId
specifier|static
name|String
name|expectedStorageId
decl_stmt|;
annotation|@
name|Override
DECL|method|chooseVolume (List<V> volumes, long replicaSize, String storageId)
specifier|public
name|V
name|chooseVolume
parameter_list|(
name|List
argument_list|<
name|V
argument_list|>
name|volumes
parameter_list|,
name|long
name|replicaSize
parameter_list|,
name|String
name|storageId
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|expectedStorageId
argument_list|,
name|storageId
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
name|replicaSize
argument_list|,
name|storageId
argument_list|)
return|;
block|}
block|}
DECL|class|TestBlockPlacementPolicy
specifier|private
specifier|static
class|class
name|TestBlockPlacementPolicy
extends|extends
name|BlockPlacementPolicyDefault
block|{
DECL|field|dnStorageInfosToReturn
specifier|static
name|DatanodeStorageInfo
index|[]
name|dnStorageInfosToReturn
decl_stmt|;
annotation|@
name|Override
DECL|method|chooseTarget (String srcPath, int numOfReplicas, Node writer, List<DatanodeStorageInfo> chosenNodes, boolean returnChosenNodes, Set<Node> excludedNodes, long blocksize, final BlockStoragePolicy storagePolicy, EnumSet<AddBlockFlag> flags)
specifier|public
name|DatanodeStorageInfo
index|[]
name|chooseTarget
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|int
name|numOfReplicas
parameter_list|,
name|Node
name|writer
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|chosenNodes
parameter_list|,
name|boolean
name|returnChosenNodes
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
specifier|final
name|BlockStoragePolicy
name|storagePolicy
parameter_list|,
name|EnumSet
argument_list|<
name|AddBlockFlag
argument_list|>
name|flags
parameter_list|)
block|{
return|return
name|dnStorageInfosToReturn
return|;
block|}
block|}
DECL|method|getDatanodeStorageInfo (int dnIndex)
specifier|private
name|DatanodeStorageInfo
name|getDatanodeStorageInfo
parameter_list|(
name|int
name|dnIndex
parameter_list|)
throws|throws
name|UnregisteredNodeException
block|{
if|if
condition|(
name|cluster
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DatanodeID
name|dnId
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
decl_stmt|;
name|DatanodeManager
name|dnManager
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
return|return
name|dnManager
operator|.
name|getDatanode
argument_list|(
name|dnId
argument_list|)
operator|.
name|getStorageInfos
argument_list|()
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testStorageIDBlockPlacementSpecific ()
specifier|public
name|void
name|testStorageIDBlockPlacementSpecific
parameter_list|()
throws|throws
name|ReconfigurationException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|IOException
block|{
specifier|final
name|StorageType
index|[]
index|[]
name|storageTypes
init|=
block|{
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|DISK
block|}
block|,     }
decl_stmt|;
specifier|final
name|int
name|numDataNodes
init|=
name|storageTypes
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|storagePerDataNode
init|=
name|storageTypes
index|[
literal|0
index|]
operator|.
name|length
decl_stmt|;
name|startDFSCluster
argument_list|(
literal|1
argument_list|,
name|numDataNodes
argument_list|,
name|storagePerDataNode
argument_list|,
name|storageTypes
argument_list|,
name|TestVolumeChoosingPolicy
operator|.
name|class
argument_list|,
name|TestBlockPlacementPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
specifier|final
name|short
name|replFactor
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|numBlocks
init|=
literal|10
decl_stmt|;
name|DatanodeStorageInfo
name|dnInfoToUse
init|=
name|getDatanodeStorageInfo
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TestBlockPlacementPolicy
operator|.
name|dnStorageInfosToReturn
operator|=
operator|new
name|DatanodeStorageInfo
index|[]
block|{
name|dnInfoToUse
block|}
expr_stmt|;
name|TestVolumeChoosingPolicy
operator|.
name|expectedStorageId
operator|=
name|dnInfoToUse
operator|.
name|getStorageID
argument_list|()
expr_stmt|;
comment|//file creation invokes both BlockPlacementPolicy and VolumeChoosingPolicy,
comment|//and will test that the storage ids match
name|createFile
argument_list|(
name|testFile
argument_list|,
name|numBlocks
argument_list|,
name|replFactor
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

