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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|FileSystemTestHelper
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
name|common
operator|.
name|Storage
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
name|StorageInfo
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
name|DNConf
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
name|DataBlockScanner
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
name|DataStorage
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
name|StorageLocation
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
name|protocol
operator|.
name|NamespaceInfo
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
name|util
operator|.
name|DiskChecker
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
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|HashSet
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
name|Set
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyListOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doAnswer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|TestFsDatasetImpl
specifier|public
class|class
name|TestFsDatasetImpl
block|{
DECL|field|BASE_DIR
specifier|private
specifier|static
specifier|final
name|String
name|BASE_DIR
init|=
operator|new
name|FileSystemTestHelper
argument_list|()
operator|.
name|getTestRootDir
argument_list|()
decl_stmt|;
DECL|field|NUM_INIT_VOLUMES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_INIT_VOLUMES
init|=
literal|2
decl_stmt|;
DECL|field|BLOCK_POOL_IDS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|BLOCK_POOL_IDS
init|=
block|{
literal|"bpid-0"
block|,
literal|"bpid-1"
block|}
decl_stmt|;
comment|// Use to generate storageUuid
DECL|field|dsForStorageUuid
specifier|private
specifier|static
specifier|final
name|DataStorage
name|dsForStorageUuid
init|=
operator|new
name|DataStorage
argument_list|(
operator|new
name|StorageInfo
argument_list|(
name|HdfsServerConstants
operator|.
name|NodeType
operator|.
name|DATA_NODE
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|datanode
specifier|private
name|DataNode
name|datanode
decl_stmt|;
DECL|field|storage
specifier|private
name|DataStorage
name|storage
decl_stmt|;
DECL|field|scanner
specifier|private
name|DataBlockScanner
name|scanner
decl_stmt|;
DECL|field|dataset
specifier|private
name|FsDatasetImpl
name|dataset
decl_stmt|;
DECL|method|createStorageDirectory (File root)
specifier|private
specifier|static
name|Storage
operator|.
name|StorageDirectory
name|createStorageDirectory
parameter_list|(
name|File
name|root
parameter_list|)
block|{
name|Storage
operator|.
name|StorageDirectory
name|sd
init|=
operator|new
name|Storage
operator|.
name|StorageDirectory
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|dsForStorageUuid
operator|.
name|createStorageID
argument_list|(
name|sd
argument_list|)
expr_stmt|;
return|return
name|sd
return|;
block|}
DECL|method|createStorageDirs (DataStorage storage, Configuration conf, int numDirs)
specifier|private
specifier|static
name|void
name|createStorageDirs
parameter_list|(
name|DataStorage
name|storage
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|numDirs
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Storage
operator|.
name|StorageDirectory
argument_list|>
name|dirs
init|=
operator|new
name|ArrayList
argument_list|<
name|Storage
operator|.
name|StorageDirectory
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dirStrings
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|numDirs
condition|;
name|i
operator|++
control|)
block|{
name|File
name|loc
init|=
operator|new
name|File
argument_list|(
name|BASE_DIR
operator|+
literal|"/data"
operator|+
name|i
argument_list|)
decl_stmt|;
name|dirStrings
operator|.
name|add
argument_list|(
name|loc
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|loc
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dirs
operator|.
name|add
argument_list|(
name|createStorageDirectory
argument_list|(
name|loc
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|storage
operator|.
name|getStorageDir
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|dataDir
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|dirStrings
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|storage
operator|.
name|getNumStorageDirs
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|numDirs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|datanode
operator|=
name|mock
argument_list|(
name|DataNode
operator|.
name|class
argument_list|)
expr_stmt|;
name|storage
operator|=
name|mock
argument_list|(
name|DataStorage
operator|.
name|class
argument_list|)
expr_stmt|;
name|scanner
operator|=
name|mock
argument_list|(
name|DataBlockScanner
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
specifier|final
name|DNConf
name|dnConf
init|=
operator|new
name|DNConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|datanode
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|datanode
operator|.
name|getDnConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dnConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|datanode
operator|.
name|getBlockScanner
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scanner
argument_list|)
expr_stmt|;
name|createStorageDirs
argument_list|(
name|storage
argument_list|,
name|conf
argument_list|,
name|NUM_INIT_VOLUMES
argument_list|)
expr_stmt|;
name|dataset
operator|=
operator|new
name|FsDatasetImpl
argument_list|(
name|datanode
argument_list|,
name|storage
argument_list|,
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|bpid
range|:
name|BLOCK_POOL_IDS
control|)
block|{
name|dataset
operator|.
name|addBlockPool
argument_list|(
name|bpid
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|NUM_INIT_VOLUMES
argument_list|,
name|dataset
operator|.
name|getVolumes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dataset
operator|.
name|getNumFailedVolumes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddVolumes ()
specifier|public
name|void
name|testAddVolumes
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numNewVolumes
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|numExistingVolumes
init|=
name|dataset
operator|.
name|getVolumes
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|totalVolumes
init|=
name|numNewVolumes
operator|+
name|numExistingVolumes
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedVolumes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NamespaceInfo
argument_list|>
name|nsInfos
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|bpid
range|:
name|BLOCK_POOL_IDS
control|)
block|{
name|nsInfos
operator|.
name|add
argument_list|(
operator|new
name|NamespaceInfo
argument_list|(
literal|0
argument_list|,
literal|"cluster-id"
argument_list|,
name|bpid
argument_list|,
literal|1
argument_list|)
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
name|numNewVolumes
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
init|=
name|BASE_DIR
operator|+
literal|"/newData"
operator|+
name|i
decl_stmt|;
name|StorageLocation
name|loc
init|=
name|StorageLocation
operator|.
name|parse
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Storage
operator|.
name|StorageDirectory
name|sd
init|=
name|createStorageDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|DataStorage
operator|.
name|VolumeBuilder
name|builder
init|=
operator|new
name|DataStorage
operator|.
name|VolumeBuilder
argument_list|(
name|storage
argument_list|,
name|sd
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|storage
operator|.
name|prepareVolume
argument_list|(
name|eq
argument_list|(
name|datanode
argument_list|)
argument_list|,
name|eq
argument_list|(
name|loc
operator|.
name|getFile
argument_list|()
argument_list|)
argument_list|,
name|anyListOf
argument_list|(
name|NamespaceInfo
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|dataset
operator|.
name|addVolume
argument_list|(
name|loc
argument_list|,
name|nsInfos
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|totalVolumes
argument_list|,
name|dataset
operator|.
name|getVolumes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|totalVolumes
argument_list|,
name|dataset
operator|.
name|storageMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|actualVolumes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
name|numNewVolumes
condition|;
name|i
operator|++
control|)
block|{
name|dataset
operator|.
name|getVolumes
argument_list|()
operator|.
name|get
argument_list|(
name|numExistingVolumes
operator|+
name|i
argument_list|)
operator|.
name|getBasePath
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|actualVolumes
argument_list|,
name|expectedVolumes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveVolumes ()
specifier|public
name|void
name|testRemoveVolumes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Feed FsDataset with block metadata.
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|100
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
name|NUM_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|bpid
init|=
name|BLOCK_POOL_IDS
index|[
name|NUM_BLOCKS
operator|%
name|BLOCK_POOL_IDS
operator|.
name|length
index|]
decl_stmt|;
name|ExtendedBlock
name|eb
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|bpid
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|dataset
operator|.
name|createRbw
argument_list|(
name|StorageType
operator|.
name|DEFAULT
argument_list|,
name|eb
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
index|[]
name|dataDirs
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
specifier|final
name|String
name|volumePathToRemove
init|=
name|dataDirs
index|[
literal|0
index|]
decl_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|volumesToRemove
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageLocation
argument_list|>
argument_list|()
decl_stmt|;
name|volumesToRemove
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
name|volumePathToRemove
argument_list|)
argument_list|)
expr_stmt|;
name|dataset
operator|.
name|removeVolumes
argument_list|(
name|volumesToRemove
argument_list|)
expr_stmt|;
name|int
name|expectedNumVolumes
init|=
name|dataDirs
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The volume has been removed from the volumeList."
argument_list|,
name|expectedNumVolumes
argument_list|,
name|dataset
operator|.
name|getVolumes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The volume has been removed from the storageMap."
argument_list|,
name|expectedNumVolumes
argument_list|,
name|dataset
operator|.
name|storageMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|dataset
operator|.
name|asyncDiskService
operator|.
name|execute
argument_list|(
name|volumesToRemove
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{}
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expect RuntimeException: the volume has been removed from the "
operator|+
literal|"AsyncDiskService."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot find root"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|int
name|totalNumReplicas
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|bpid
range|:
name|dataset
operator|.
name|volumeMap
operator|.
name|getBlockPoolList
argument_list|()
control|)
block|{
name|totalNumReplicas
operator|+=
name|dataset
operator|.
name|volumeMap
operator|.
name|size
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"The replica infos on this volume has been removed from the "
operator|+
literal|"volumeMap."
argument_list|,
name|NUM_BLOCKS
operator|/
name|NUM_INIT_VOLUMES
argument_list|,
name|totalNumReplicas
argument_list|)
expr_stmt|;
comment|// Verify that every BlockPool deletes the removed blocks from the volume.
name|verify
argument_list|(
name|scanner
argument_list|,
name|times
argument_list|(
name|BLOCK_POOL_IDS
operator|.
name|length
argument_list|)
argument_list|)
operator|.
name|deleteBlocks
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|Block
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testChangeVolumeWithRunningCheckDirs ()
specifier|public
name|void
name|testChangeVolumeWithRunningCheckDirs
parameter_list|()
throws|throws
name|IOException
block|{
name|RoundRobinVolumeChoosingPolicy
argument_list|<
name|FsVolumeImpl
argument_list|>
name|blockChooser
init|=
operator|new
name|RoundRobinVolumeChoosingPolicy
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|FsVolumeList
name|volumeList
init|=
operator|new
name|FsVolumeList
argument_list|(
literal|0
argument_list|,
name|blockChooser
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FsVolumeImpl
argument_list|>
name|oldVolumes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Initialize FsVolumeList with 5 mock volumes.
specifier|final
name|int
name|NUM_VOLUMES
init|=
literal|5
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
name|NUM_VOLUMES
condition|;
name|i
operator|++
control|)
block|{
name|FsVolumeImpl
name|volume
init|=
name|mock
argument_list|(
name|FsVolumeImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|oldVolumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|volume
operator|.
name|getBasePath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"data"
operator|+
name|i
argument_list|)
expr_stmt|;
name|volumeList
operator|.
name|addVolume
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
comment|// When call checkDirs() on the 2nd volume, anther "thread" removes the 5th
comment|// volume and add another volume. It does not affect checkDirs() running.
specifier|final
name|FsVolumeImpl
name|newVolume
init|=
name|mock
argument_list|(
name|FsVolumeImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|FsVolumeImpl
name|blockedVolume
init|=
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocationOnMock
parameter_list|)
throws|throws
name|Throwable
block|{
name|volumeList
operator|.
name|removeVolume
argument_list|(
literal|"data4"
argument_list|)
expr_stmt|;
name|volumeList
operator|.
name|addVolume
argument_list|(
name|newVolume
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|blockedVolume
argument_list|)
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
name|FsVolumeImpl
name|brokenVolume
init|=
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|doThrow
argument_list|(
operator|new
name|DiskChecker
operator|.
name|DiskErrorException
argument_list|(
literal|"broken"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|brokenVolume
argument_list|)
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
name|volumeList
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
comment|// Since FsVolumeImpl#checkDirs() get a snapshot of the list of volumes
comment|// before running removeVolume(), it is supposed to run checkDirs() on all
comment|// the old volumes.
for|for
control|(
name|FsVolumeImpl
name|volume
range|:
name|oldVolumes
control|)
block|{
name|verify
argument_list|(
name|volume
argument_list|)
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
block|}
comment|// New volume is not visible to checkDirs() process.
name|verify
argument_list|(
name|newVolume
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|checkDirs
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|contains
argument_list|(
name|newVolume
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|contains
argument_list|(
name|brokenVolume
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_VOLUMES
operator|-
literal|1
argument_list|,
name|volumeList
operator|.
name|getVolumes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

