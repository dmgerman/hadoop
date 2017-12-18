begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
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
name|primitives
operator|.
name|Longs
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|lang
operator|.
name|RandomStringUtils
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|CBlockTargetMetrics
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|ContainerCacheFlusher
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
operator|.
name|LogicalBlock
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
name|cblock
operator|.
name|jscsiHelper
operator|.
name|cache
operator|.
name|impl
operator|.
name|CBlockLocalCache
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
name|ozone
operator|.
name|MiniOzoneClassicCluster
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
name|MiniOzoneCluster
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
name|scm
operator|.
name|XceiverClientManager
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
name|scm
operator|.
name|XceiverClientSpi
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|scm
operator|.
name|storage
operator|.
name|ContainerProtocolCalls
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
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_DISK_CACHE_PATH_KEY
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_TRACE_IO
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_BLOCK_BUFFER_FLUSH_INTERVAL
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
name|cblock
operator|.
name|CBlockConfigKeys
operator|.
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE
import|;
end_import

begin_comment
comment|/**  * Tests for Cblock read write functionality.  */
end_comment

begin_class
DECL|class|TestCBlockReadWrite
specifier|public
class|class
name|TestCBlockReadWrite
block|{
DECL|field|GB
specifier|private
specifier|final
specifier|static
name|long
name|GB
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|KB
specifier|private
specifier|final
specifier|static
name|int
name|KB
init|=
literal|1024
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|config
specifier|private
specifier|static
name|OzoneConfiguration
name|config
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|static
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestCBlockReadWrite
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFS_CBLOCK_DISK_CACHE_PATH_KEY
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_TRACE_IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|config
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|createStorageContainerLocationClient
argument_list|()
expr_stmt|;
name|xceiverClientManager
operator|=
operator|new
name|XceiverClientManager
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|InterruptedException
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
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|storageContainerLocationClient
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
comment|/**    * getContainerPipelines creates a set of containers and returns the    * Pipelines that define those containers.    *    * @param count - Number of containers to create.    * @return - List of Pipelines.    * @throws IOException throws Exception    */
DECL|method|getContainerPipeline (int count)
specifier|private
name|List
argument_list|<
name|Pipeline
argument_list|>
name|getContainerPipeline
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Pipeline
argument_list|>
name|containerPipelines
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|count
condition|;
name|x
operator|++
control|)
block|{
name|String
name|traceID
init|=
literal|"trace"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|String
name|containerName
init|=
literal|"container"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerName
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client
init|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client
argument_list|,
name|traceID
argument_list|)
expr_stmt|;
comment|// This step is needed since we set private data on pipelines, when we
comment|// read the list from CBlockServer. So we mimic that action here.
name|pipeline
operator|.
name|setData
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|containerPipelines
operator|.
name|add
argument_list|(
name|pipeline
argument_list|)
expr_stmt|;
block|}
return|return
name|containerPipelines
return|;
block|}
comment|/**    * This test creates a cache and performs a simple write / read.    * The operations are done by bypassing the cache.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testDirectIO ()
specifier|public
name|void
name|testDirectIO
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|OzoneConfiguration
name|cConfig
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|cConfig
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cConfig
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_TRACE_IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|long
name|blockID
init|=
literal|0
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|String
name|data
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
decl_stmt|;
name|String
name|dataHash
init|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|CBlockTargetMetrics
name|metrics
init|=
name|CBlockTargetMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|ContainerCacheFlusher
name|flusher
init|=
operator|new
name|ContainerCacheFlusher
argument_list|(
name|cConfig
argument_list|,
name|xceiverClientManager
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|CBlockLocalCache
name|cache
init|=
name|CBlockLocalCache
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfiguration
argument_list|(
name|cConfig
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
operator|.
name|setPipelines
argument_list|(
name|getContainerPipeline
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|setClientManager
argument_list|(
name|xceiverClientManager
argument_list|)
operator|.
name|setBlockSize
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
operator|.
name|setVolumeSize
argument_list|(
literal|50
operator|*
name|GB
argument_list|)
operator|.
name|setFlusher
argument_list|(
name|flusher
argument_list|)
operator|.
name|setCBlockTargetMetrics
argument_list|(
name|metrics
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cache
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|cache
operator|.
name|isShortCircuitIOEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|blockID
argument_list|,
name|data
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metrics
operator|.
name|getNumDirectBlockWrites
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metrics
operator|.
name|getNumWriteOps
argument_list|()
argument_list|)
expr_stmt|;
comment|// Please note that this read is directly from remote container
name|LogicalBlock
name|block
init|=
name|cache
operator|.
name|get
argument_list|(
name|blockID
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metrics
operator|.
name|getNumReadOps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumReadCacheHits
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metrics
operator|.
name|getNumReadCacheMiss
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumReadLostBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumFailedDirectBlockWrites
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|blockID
operator|+
literal|1
argument_list|,
name|data
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metrics
operator|.
name|getNumDirectBlockWrites
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metrics
operator|.
name|getNumWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumFailedDirectBlockWrites
argument_list|()
argument_list|)
expr_stmt|;
comment|// Please note that this read is directly from remote container
name|block
operator|=
name|cache
operator|.
name|get
argument_list|(
name|blockID
operator|+
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metrics
operator|.
name|getNumReadOps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumReadCacheHits
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metrics
operator|.
name|getNumReadCacheMiss
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumReadLostBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|readHash
init|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|block
operator|.
name|getData
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"File content does not match."
argument_list|,
name|dataHash
argument_list|,
name|readHash
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
operator|!
name|cache
operator|.
name|isDirtyCache
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|20
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * This test writes some block to the cache and then shuts down the cache    * The cache is then restarted with "short.circuit.io" disable to check    * that the blocks are read correctly from the container.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testContainerWrites ()
specifier|public
name|void
name|testContainerWrites
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
comment|// Create a new config so that this tests write metafile to new location
name|OzoneConfiguration
name|flushTestConfig
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestCBlockReadWrite
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|flushTestConfig
operator|.
name|set
argument_list|(
name|DFS_CBLOCK_DISK_CACHE_PATH_KEY
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|flushTestConfig
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_TRACE_IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|flushTestConfig
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|flushTestConfig
operator|.
name|setTimeDuration
argument_list|(
name|DFS_CBLOCK_BLOCK_BUFFER_FLUSH_INTERVAL
argument_list|,
literal|3
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|XceiverClientManager
name|xcm
init|=
operator|new
name|XceiverClientManager
argument_list|(
name|flushTestConfig
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|int
name|numUniqueBlocks
init|=
literal|4
decl_stmt|;
name|String
index|[]
name|data
init|=
operator|new
name|String
index|[
name|numUniqueBlocks
index|]
decl_stmt|;
name|String
index|[]
name|dataHash
init|=
operator|new
name|String
index|[
name|numUniqueBlocks
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
name|numUniqueBlocks
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
expr_stmt|;
name|dataHash
index|[
name|i
index|]
operator|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|CBlockTargetMetrics
name|metrics
init|=
name|CBlockTargetMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|ContainerCacheFlusher
name|flusher
init|=
operator|new
name|ContainerCacheFlusher
argument_list|(
name|flushTestConfig
argument_list|,
name|xcm
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines
init|=
name|getContainerPipeline
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|CBlockLocalCache
name|cache
init|=
name|CBlockLocalCache
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfiguration
argument_list|(
name|flushTestConfig
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
operator|.
name|setPipelines
argument_list|(
name|pipelines
argument_list|)
operator|.
name|setClientManager
argument_list|(
name|xcm
argument_list|)
operator|.
name|setBlockSize
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
operator|.
name|setVolumeSize
argument_list|(
literal|50
operator|*
name|GB
argument_list|)
operator|.
name|setFlusher
argument_list|(
name|flusher
argument_list|)
operator|.
name|setCBlockTargetMetrics
argument_list|(
name|metrics
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cache
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|flushListenerThread
init|=
operator|new
name|Thread
argument_list|(
name|flusher
argument_list|)
decl_stmt|;
name|flushListenerThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|flushListenerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|cache
operator|.
name|isShortCircuitIOEnabled
argument_list|()
argument_list|)
expr_stmt|;
comment|// Write data to the cache
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|512
condition|;
name|i
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|data
index|[
name|i
operator|%
name|numUniqueBlocks
index|]
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Close the cache and flush the data to the containers
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumDirectBlockWrites
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|512
argument_list|,
name|metrics
operator|.
name|getNumWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|flusher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getNumBlockBufferFlushTriggered
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metrics
operator|.
name|getNumBlockBufferFlushCompleted
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumWriteIOExceptionRetryBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumWriteGenericExceptionRetryBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumFailedReleaseLevelDB
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now disable DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO and restart cache
name|flushTestConfig
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|CBlockTargetMetrics
name|newMetrics
init|=
name|CBlockTargetMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|ContainerCacheFlusher
name|newFlusher
init|=
operator|new
name|ContainerCacheFlusher
argument_list|(
name|flushTestConfig
argument_list|,
name|xcm
argument_list|,
name|newMetrics
argument_list|)
decl_stmt|;
name|CBlockLocalCache
name|newCache
init|=
name|CBlockLocalCache
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfiguration
argument_list|(
name|flushTestConfig
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
operator|.
name|setPipelines
argument_list|(
name|pipelines
argument_list|)
operator|.
name|setClientManager
argument_list|(
name|xcm
argument_list|)
operator|.
name|setBlockSize
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
operator|.
name|setVolumeSize
argument_list|(
literal|50
operator|*
name|GB
argument_list|)
operator|.
name|setFlusher
argument_list|(
name|newFlusher
argument_list|)
operator|.
name|setCBlockTargetMetrics
argument_list|(
name|newMetrics
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|newCache
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|newCache
operator|.
name|isShortCircuitIOEnabled
argument_list|()
argument_list|)
expr_stmt|;
comment|// this read will be from the container, also match the hash
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|512
condition|;
name|i
operator|++
control|)
block|{
name|LogicalBlock
name|block
init|=
name|newCache
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|readHash
init|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|block
operator|.
name|getData
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"File content does not match, for index:"
operator|+
name|i
argument_list|,
name|dataHash
index|[
name|i
operator|%
name|numUniqueBlocks
index|]
argument_list|,
name|readHash
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newMetrics
operator|.
name|getNumReadLostBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newMetrics
operator|.
name|getNumFailedReadBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|newCache
operator|.
name|close
argument_list|()
expr_stmt|;
name|newFlusher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetryLog ()
specifier|public
name|void
name|testRetryLog
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
comment|// Create a new config so that this tests write metafile to new location
name|OzoneConfiguration
name|flushTestConfig
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestCBlockReadWrite
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|flushTestConfig
operator|.
name|set
argument_list|(
name|DFS_CBLOCK_DISK_CACHE_PATH_KEY
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|flushTestConfig
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_TRACE_IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|flushTestConfig
operator|.
name|setBoolean
argument_list|(
name|DFS_CBLOCK_ENABLE_SHORT_CIRCUIT_IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|flushTestConfig
operator|.
name|setTimeDuration
argument_list|(
name|DFS_CBLOCK_BLOCK_BUFFER_FLUSH_INTERVAL
argument_list|,
literal|3
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|int
name|numblocks
init|=
literal|10
decl_stmt|;
name|flushTestConfig
operator|.
name|setInt
argument_list|(
name|DFS_CBLOCK_CACHE_BLOCK_BUFFER_SIZE
argument_list|,
name|numblocks
argument_list|)
expr_stmt|;
name|String
name|volumeName
init|=
literal|"volume"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|String
name|userName
init|=
literal|"user"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|String
name|data
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pipeline
argument_list|>
name|fakeContainerPipelines
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|Pipeline
name|fakePipeline
init|=
operator|new
name|Pipeline
argument_list|(
literal|"fake"
argument_list|)
decl_stmt|;
name|fakePipeline
operator|.
name|setData
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|fakeContainerPipelines
operator|.
name|add
argument_list|(
name|fakePipeline
argument_list|)
expr_stmt|;
name|CBlockTargetMetrics
name|metrics
init|=
name|CBlockTargetMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|ContainerCacheFlusher
name|flusher
init|=
operator|new
name|ContainerCacheFlusher
argument_list|(
name|flushTestConfig
argument_list|,
name|xceiverClientManager
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|CBlockLocalCache
name|cache
init|=
name|CBlockLocalCache
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfiguration
argument_list|(
name|flushTestConfig
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
operator|.
name|setPipelines
argument_list|(
name|fakeContainerPipelines
argument_list|)
operator|.
name|setClientManager
argument_list|(
name|xceiverClientManager
argument_list|)
operator|.
name|setBlockSize
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
operator|.
name|setVolumeSize
argument_list|(
literal|50
operator|*
name|GB
argument_list|)
operator|.
name|setFlusher
argument_list|(
name|flusher
argument_list|)
operator|.
name|setCBlockTargetMetrics
argument_list|(
name|metrics
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cache
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|flushListenerThread
init|=
operator|new
name|Thread
argument_list|(
name|flusher
argument_list|)
decl_stmt|;
name|flushListenerThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|flushListenerThread
operator|.
name|start
argument_list|()
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
name|numblocks
condition|;
name|i
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|data
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numblocks
argument_list|,
name|metrics
operator|.
name|getNumWriteOps
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
comment|// all the writes to the container will fail because of fake pipelines
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numblocks
argument_list|,
name|metrics
operator|.
name|getNumDirtyLogBlockRead
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|metrics
operator|.
name|getNumWriteGenericExceptionRetryBlocks
argument_list|()
operator|>=
name|numblocks
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumWriteIOExceptionRetryBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumFailedRetryLogFileWrites
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|metrics
operator|.
name|getNumFailedReleaseLevelDB
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|flusher
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// restart cache with correct pipelines, now blocks should be uploaded
comment|// correctly
name|CBlockTargetMetrics
name|newMetrics
init|=
name|CBlockTargetMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|ContainerCacheFlusher
name|newFlusher
init|=
operator|new
name|ContainerCacheFlusher
argument_list|(
name|flushTestConfig
argument_list|,
name|xceiverClientManager
argument_list|,
name|newMetrics
argument_list|)
decl_stmt|;
name|CBlockLocalCache
name|newCache
init|=
name|CBlockLocalCache
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConfiguration
argument_list|(
name|flushTestConfig
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setUserName
argument_list|(
name|userName
argument_list|)
operator|.
name|setPipelines
argument_list|(
name|getContainerPipeline
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|setClientManager
argument_list|(
name|xceiverClientManager
argument_list|)
operator|.
name|setBlockSize
argument_list|(
literal|4
operator|*
name|KB
argument_list|)
operator|.
name|setVolumeSize
argument_list|(
literal|50
operator|*
name|GB
argument_list|)
operator|.
name|setFlusher
argument_list|(
name|newFlusher
argument_list|)
operator|.
name|setCBlockTargetMetrics
argument_list|(
name|newMetrics
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|newCache
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|newFlushListenerThread
init|=
operator|new
name|Thread
argument_list|(
name|newFlusher
argument_list|)
decl_stmt|;
name|newFlushListenerThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|newFlushListenerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newMetrics
operator|.
name|getNumRetryLogBlockRead
argument_list|()
operator|>=
name|numblocks
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newMetrics
operator|.
name|getNumWriteGenericExceptionRetryBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newMetrics
operator|.
name|getNumWriteIOExceptionRetryBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|newMetrics
operator|.
name|getNumFailedReleaseLevelDB
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

