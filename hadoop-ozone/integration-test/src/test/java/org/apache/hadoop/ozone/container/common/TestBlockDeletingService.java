begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|hdds
operator|.
name|client
operator|.
name|BlockID
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
name|hdds
operator|.
name|scm
operator|.
name|TestUtils
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
name|DFSUtil
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|container
operator|.
name|ContainerTestHelper
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
name|ozone
operator|.
name|container
operator|.
name|testutils
operator|.
name|BlockDeletingServiceTestImpl
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|KeyData
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|KeyUtils
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerManagerImpl
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|RandomContainerDeletionChoosingPolicy
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|background
operator|.
name|BlockDeletingService
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|GenericTestUtils
operator|.
name|LogCapturer
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
name|utils
operator|.
name|BackgroundService
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
name|utils
operator|.
name|MetadataKeyFilters
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
name|utils
operator|.
name|MetadataStore
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
name|BeforeClass
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
name|After
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|Map
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
import|;
end_import

begin_comment
comment|/**  * Tests to test block deleting service.  */
end_comment

begin_class
DECL|class|TestBlockDeletingService
specifier|public
class|class
name|TestBlockDeletingService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestBlockDeletingService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|testRoot
specifier|private
specifier|static
name|File
name|testRoot
decl_stmt|;
DECL|field|containersDir
specifier|private
specifier|static
name|File
name|containersDir
decl_stmt|;
DECL|field|chunksDir
specifier|private
specifier|static
name|File
name|chunksDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
name|testRoot
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestBlockDeletingService
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|chunksDir
operator|=
operator|new
name|File
argument_list|(
name|testRoot
argument_list|,
literal|"chunks"
argument_list|)
expr_stmt|;
name|containersDir
operator|=
operator|new
name|File
argument_list|(
name|testRoot
argument_list|,
literal|"containers"
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|chunksDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|chunksDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|chunksDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|containersDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
block|}
DECL|method|createContainerManager (Configuration conf)
specifier|private
name|ContainerManager
name|createContainerManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// use random container choosing policy for testing
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_DELETION_CHOOSING_POLICY
argument_list|,
name|RandomContainerDeletionChoosingPolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_LOCALSTORAGE_ROOT
argument_list|,
name|containersDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|containersDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|containersDir
argument_list|)
expr_stmt|;
block|}
name|ContainerManager
name|containerManager
init|=
operator|new
name|ContainerManagerImpl
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StorageLocation
argument_list|>
name|pathLists
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|pathLists
operator|.
name|add
argument_list|(
name|StorageLocation
operator|.
name|parse
argument_list|(
name|containersDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|init
argument_list|(
name|conf
argument_list|,
name|pathLists
argument_list|,
name|TestUtils
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|containerManager
return|;
block|}
comment|/**    * A helper method to create some blocks and put them under deletion    * state for testing. This method directly updates container.db and    * creates some fake chunk files for testing.    */
DECL|method|createToDeleteBlocks (ContainerManager mgr, Configuration conf, int numOfContainers, int numOfBlocksPerContainer, int numOfChunksPerBlock, File chunkDir)
specifier|private
name|void
name|createToDeleteBlocks
parameter_list|(
name|ContainerManager
name|mgr
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|numOfContainers
parameter_list|,
name|int
name|numOfBlocksPerContainer
parameter_list|,
name|int
name|numOfChunksPerBlock
parameter_list|,
name|File
name|chunkDir
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|numOfContainers
condition|;
name|x
operator|++
control|)
block|{
name|long
name|containerID
init|=
name|ContainerTestHelper
operator|.
name|getTestContainerID
argument_list|()
decl_stmt|;
name|ContainerData
name|data
init|=
operator|new
name|ContainerData
argument_list|(
name|containerID
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|createContainer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|data
operator|=
name|mgr
operator|.
name|readContainer
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|MetadataStore
name|metadata
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|data
argument_list|,
name|conf
argument_list|)
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
name|numOfBlocksPerContainer
condition|;
name|j
operator|++
control|)
block|{
name|BlockID
name|blockID
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|String
name|deleteStateName
init|=
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
operator|+
name|blockID
operator|.
name|getLocalID
argument_list|()
decl_stmt|;
name|KeyData
name|kd
init|=
operator|new
name|KeyData
argument_list|(
name|blockID
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunks
init|=
name|Lists
operator|.
name|newArrayList
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
name|numOfChunksPerBlock
condition|;
name|k
operator|++
control|)
block|{
comment|// offset doesn't matter here
name|String
name|chunkName
init|=
name|blockID
operator|.
name|getLocalID
argument_list|()
operator|+
literal|"_chunk_"
operator|+
name|k
decl_stmt|;
name|File
name|chunk
init|=
operator|new
name|File
argument_list|(
name|chunkDir
argument_list|,
name|chunkName
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
name|chunk
argument_list|,
literal|"a chunk"
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating file {}"
argument_list|,
name|chunk
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure file exists
name|Assert
operator|.
name|assertTrue
argument_list|(
name|chunk
operator|.
name|isFile
argument_list|()
operator|&&
name|chunk
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|ChunkInfo
name|info
init|=
name|ContainerProtos
operator|.
name|ChunkInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChunkName
argument_list|(
name|chunk
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|setLen
argument_list|(
literal|0
argument_list|)
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|setChecksum
argument_list|(
literal|""
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|chunks
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|kd
operator|.
name|setChunks
argument_list|(
name|chunks
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|deleteStateName
argument_list|)
argument_list|,
name|kd
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    *  Run service runDeletingTasks and wait for it's been processed.    */
DECL|method|deleteAndWait (BlockDeletingServiceTestImpl service, int timesOfProcessed)
specifier|private
name|void
name|deleteAndWait
parameter_list|(
name|BlockDeletingServiceTestImpl
name|service
parameter_list|,
name|int
name|timesOfProcessed
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|service
operator|.
name|runDeletingTasks
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|service
operator|.
name|getTimesOfProcessed
argument_list|()
operator|==
name|timesOfProcessed
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get under deletion blocks count from DB,    * note this info is parsed from container.db.    */
DECL|method|getUnderDeletionBlocksCount (MetadataStore meta)
specifier|private
name|int
name|getUnderDeletionBlocksCount
parameter_list|(
name|MetadataStore
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|underDeletionBlocks
init|=
name|meta
operator|.
name|getRangeKVs
argument_list|(
literal|null
argument_list|,
literal|100
argument_list|,
operator|new
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
argument_list|()
operator|.
name|addFilter
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|underDeletionBlocks
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getDeletedBlocksCount (MetadataStore db)
specifier|private
name|int
name|getDeletedBlocksCount
parameter_list|(
name|MetadataStore
name|db
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|underDeletionBlocks
init|=
name|db
operator|.
name|getRangeKVs
argument_list|(
literal|null
argument_list|,
literal|100
argument_list|,
operator|new
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
argument_list|()
operator|.
name|addFilter
argument_list|(
name|OzoneConsts
operator|.
name|DELETED_KEY_PREFIX
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|underDeletionBlocks
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testBlockDeletion ()
specifier|public
name|void
name|testBlockDeletion
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ContainerManager
name|containerManager
init|=
name|createContainerManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|createToDeleteBlocks
argument_list|(
name|containerManager
argument_list|,
name|conf
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|chunksDir
argument_list|)
expr_stmt|;
name|BlockDeletingServiceTestImpl
name|svc
init|=
operator|new
name|BlockDeletingServiceTestImpl
argument_list|(
name|containerManager
argument_list|,
literal|1000
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|svc
operator|.
name|start
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|svc
operator|.
name|isStarted
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
comment|// Ensure 1 container was created
name|List
argument_list|<
name|ContainerData
argument_list|>
name|containerData
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|containerManager
operator|.
name|listContainer
argument_list|(
literal|0L
argument_list|,
literal|1
argument_list|,
name|containerData
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containerData
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|MetadataStore
name|meta
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|containerData
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|ContainerData
argument_list|>
name|containerMap
init|=
operator|(
operator|(
name|ContainerManagerImpl
operator|)
name|containerManager
operator|)
operator|.
name|getContainerMap
argument_list|()
decl_stmt|;
name|long
name|transactionId
init|=
name|containerMap
operator|.
name|get
argument_list|(
name|containerData
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|getDeleteTransactionId
argument_list|()
decl_stmt|;
comment|// Number of deleted blocks in container should be equal to 0 before
comment|// block delete
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
comment|// Ensure there are 3 blocks under deletion and 0 deleted blocks
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getUnderDeletionBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getDeletedBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
comment|// An interval will delete 1 * 2 blocks
name|deleteAndWait
argument_list|(
name|svc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getUnderDeletionBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getDeletedBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|deleteAndWait
argument_list|(
name|svc
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getUnderDeletionBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getDeletedBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|deleteAndWait
argument_list|(
name|svc
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getUnderDeletionBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getDeletedBlocksCount
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|svc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|shutdownContainerMangaer
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShutdownService ()
specifier|public
name|void
name|testShutdownService
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|OZONE_BLOCK_DELETING_SERVICE_INTERVAL
argument_list|,
literal|500
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|ContainerManager
name|containerManager
init|=
name|createContainerManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Create 1 container with 100 blocks
name|createToDeleteBlocks
argument_list|(
name|containerManager
argument_list|,
name|conf
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|,
name|chunksDir
argument_list|)
expr_stmt|;
name|BlockDeletingServiceTestImpl
name|service
init|=
operator|new
name|BlockDeletingServiceTestImpl
argument_list|(
name|containerManager
argument_list|,
literal|1000
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|service
operator|.
name|isStarted
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
comment|// Run some deleting tasks and verify there are threads running
name|service
operator|.
name|runDeletingTasks
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|service
operator|.
name|getThreadCount
argument_list|()
operator|>
literal|0
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// Wait for 1 or 2 intervals
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// Shutdown service and verify all threads are stopped
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|service
operator|.
name|getThreadCount
argument_list|()
operator|==
literal|0
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|shutdownContainerMangaer
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockDeletionTimeout ()
specifier|public
name|void
name|testBlockDeletionTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ContainerManager
name|containerManager
init|=
name|createContainerManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|createToDeleteBlocks
argument_list|(
name|containerManager
argument_list|,
name|conf
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|chunksDir
argument_list|)
expr_stmt|;
comment|// set timeout value as 1ns to trigger timeout behavior
name|long
name|timeout
init|=
literal|1
decl_stmt|;
name|BlockDeletingService
name|svc
init|=
operator|new
name|BlockDeletingService
argument_list|(
name|containerManager
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|svc
operator|.
name|start
argument_list|()
expr_stmt|;
name|LogCapturer
name|log
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|BackgroundService
operator|.
name|LOG
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
if|if
condition|(
name|log
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Background task executes timed out, retrying in next interval"
argument_list|)
condition|)
block|{
name|log
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
argument_list|,
literal|1000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
name|log
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
name|svc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// test for normal case that doesn't have timeout limitation
name|timeout
operator|=
literal|0
expr_stmt|;
name|createToDeleteBlocks
argument_list|(
name|containerManager
argument_list|,
name|conf
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|chunksDir
argument_list|)
expr_stmt|;
name|svc
operator|=
operator|new
name|BlockDeletingService
argument_list|(
name|containerManager
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|svc
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// get container meta data
name|List
argument_list|<
name|ContainerData
argument_list|>
name|containerData
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|containerManager
operator|.
name|listContainer
argument_list|(
literal|0L
argument_list|,
literal|1
argument_list|,
name|containerData
argument_list|)
expr_stmt|;
name|MetadataStore
name|meta
init|=
name|KeyUtils
operator|.
name|getDB
argument_list|(
name|containerData
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|LogCapturer
name|newLog
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|BackgroundService
operator|.
name|LOG
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
if|if
condition|(
name|getUnderDeletionBlocksCount
argument_list|(
name|meta
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{       }
return|return
literal|false
return|;
block|}
argument_list|,
literal|1000
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
name|newLog
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
comment|// The block deleting successfully and shouldn't catch timed
comment|// out warning log.
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|newLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Background task executes timed out, retrying in next interval"
argument_list|)
argument_list|)
expr_stmt|;
name|svc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|shutdownContainerMangaer
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testContainerThrottle ()
specifier|public
name|void
name|testContainerThrottle
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Properties :
comment|//  - Number of containers : 2
comment|//  - Number of blocks per container : 1
comment|//  - Number of chunks per block : 10
comment|//  - Container limit per interval : 1
comment|//  - Block limit per container : 1
comment|//
comment|// Each time only 1 container can be processed, so each time
comment|// 1 block from 1 container can be deleted.
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
comment|// Process 1 container per interval
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ContainerManager
name|containerManager
init|=
name|createContainerManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|createToDeleteBlocks
argument_list|(
name|containerManager
argument_list|,
name|conf
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
name|chunksDir
argument_list|)
expr_stmt|;
name|BlockDeletingServiceTestImpl
name|service
init|=
operator|new
name|BlockDeletingServiceTestImpl
argument_list|(
name|containerManager
argument_list|,
literal|1000
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|service
operator|.
name|isStarted
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
comment|// 1st interval processes 1 container 1 block and 10 chunks
name|deleteAndWait
argument_list|(
name|service
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|chunksDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|shutdownContainerMangaer
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testBlockThrottle ()
specifier|public
name|void
name|testBlockThrottle
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Properties :
comment|//  - Number of containers : 5
comment|//  - Number of blocks per container : 3
comment|//  - Number of chunks per block : 1
comment|//  - Container limit per interval : 10
comment|//  - Block limit per container : 2
comment|//
comment|// Each time containers can be all scanned, but only 2 blocks
comment|// per container can be actually deleted. So it requires 2 waves
comment|// to cleanup all blocks.
name|Configuration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_CONTAINER_LIMIT_PER_INTERVAL
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_BLOCK_DELETING_LIMIT_PER_CONTAINER
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|ContainerManager
name|containerManager
init|=
name|createContainerManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|createToDeleteBlocks
argument_list|(
name|containerManager
argument_list|,
name|conf
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|chunksDir
argument_list|)
expr_stmt|;
comment|// Make sure chunks are created
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|chunksDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|BlockDeletingServiceTestImpl
name|service
init|=
operator|new
name|BlockDeletingServiceTestImpl
argument_list|(
name|containerManager
argument_list|,
literal|1000
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|service
operator|.
name|isStarted
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
comment|// Total blocks = 3 * 5 = 15
comment|// block per task = 2
comment|// number of containers = 5
comment|// each interval will at most runDeletingTasks 5 * 2 = 10 blocks
name|deleteAndWait
argument_list|(
name|service
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|chunksDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// There is only 5 blocks left to runDeletingTasks
name|deleteAndWait
argument_list|(
name|service
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|chunksDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|shutdownContainerMangaer
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutdownContainerMangaer (ContainerManager mgr)
specifier|private
name|void
name|shutdownContainerMangaer
parameter_list|(
name|ContainerManager
name|mgr
parameter_list|)
throws|throws
name|IOException
block|{
name|mgr
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|mgr
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

