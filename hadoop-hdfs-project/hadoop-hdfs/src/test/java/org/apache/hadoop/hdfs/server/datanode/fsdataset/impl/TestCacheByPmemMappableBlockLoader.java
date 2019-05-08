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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|ExtendedBlockId
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
name|DataNodeFaultInjector
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_CACHE_PMEM_DIRS_KEY
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|assertNotNull
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|HdfsBlockLocation
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
name|BlockReaderTestUtil
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
name|CacheDirectiveInfo
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
name|CachePoolInfo
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
name|nativeio
operator|.
name|NativeIO
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
name|nativeio
operator|.
name|NativeIO
operator|.
name|POSIX
operator|.
name|CacheManipulator
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
name|nativeio
operator|.
name|NativeIO
operator|.
name|POSIX
operator|.
name|NoMlockCacheManipulator
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|MetricsAsserts
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
name|Before
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
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_FSDATASETCACHE_MAX_THREADS_PER_VOLUME_KEY
import|;
end_import

begin_comment
comment|/**  * Tests HDFS persistent memory cache by PmemMappableBlockLoader.  *  * Bogus persistent memory volume is used to cache blocks.  */
end_comment

begin_class
DECL|class|TestCacheByPmemMappableBlockLoader
specifier|public
class|class
name|TestCacheByPmemMappableBlockLoader
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|org
operator|.
name|slf4j
operator|.
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestCacheByPmemMappableBlockLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CACHE_CAPACITY
specifier|protected
specifier|static
specifier|final
name|long
name|CACHE_CAPACITY
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|protected
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|dn
specifier|private
specifier|static
name|DataNode
name|dn
decl_stmt|;
DECL|field|cacheManager
specifier|private
specifier|static
name|FsDatasetCache
name|cacheManager
decl_stmt|;
DECL|field|cacheLoader
specifier|private
specifier|static
name|PmemMappableBlockLoader
name|cacheLoader
decl_stmt|;
comment|/**    * Used to pause DN BPServiceActor threads. BPSA threads acquire the    * shared read lock. The test acquires the write lock for exclusive access.    */
DECL|field|lock
specifier|private
specifier|static
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|prevCacheManipulator
specifier|private
specifier|static
name|CacheManipulator
name|prevCacheManipulator
decl_stmt|;
DECL|field|oldInjector
specifier|private
specifier|static
name|DataNodeFaultInjector
name|oldInjector
decl_stmt|;
DECL|field|PMEM_DIR_0
specifier|private
specifier|static
specifier|final
name|String
name|PMEM_DIR_0
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"pmem0"
decl_stmt|;
DECL|field|PMEM_DIR_1
specifier|private
specifier|static
specifier|final
name|String
name|PMEM_DIR_1
init|=
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"pmem1"
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FsDatasetCache
operator|.
name|class
argument_list|)
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setUpClass ()
specifier|public
specifier|static
name|void
name|setUpClass
parameter_list|()
throws|throws
name|Exception
block|{
name|oldInjector
operator|=
name|DataNodeFaultInjector
operator|.
name|get
argument_list|()
expr_stmt|;
name|DataNodeFaultInjector
operator|.
name|set
argument_list|(
operator|new
name|DataNodeFaultInjector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|startOfferService
parameter_list|()
throws|throws
name|Exception
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endOfferService
parameter_list|()
throws|throws
name|Exception
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownClass ()
specifier|public
specifier|static
name|void
name|tearDownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|DataNodeFaultInjector
operator|.
name|set
argument_list|(
name|oldInjector
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
name|Exception
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
name|DFS_NAMENODE_PATH_BASED_CACHE_REFRESH_INTERVAL_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CACHEREPORT_INTERVAL_MSEC_KEY
argument_list|,
literal|500
argument_list|)
expr_stmt|;
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
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_MAX_LOCKED_MEMORY_KEY
argument_list|,
name|CACHE_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
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
name|DFS_DATANODE_FSDATASETCACHE_MAX_THREADS_PER_VOLUME_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Configuration for pmem cache
operator|new
name|File
argument_list|(
name|PMEM_DIR_0
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|mkdir
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|PMEM_DIR_1
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|mkdir
argument_list|()
expr_stmt|;
comment|// Configure two bogus pmem volumes
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_CACHE_PMEM_DIRS_KEY
argument_list|,
name|PMEM_DIR_0
operator|+
literal|","
operator|+
name|PMEM_DIR_1
argument_list|)
expr_stmt|;
name|PmemVolumeManager
operator|.
name|setMaxBytes
argument_list|(
call|(
name|long
call|)
argument_list|(
name|CACHE_CAPACITY
operator|*
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|prevCacheManipulator
operator|=
name|NativeIO
operator|.
name|POSIX
operator|.
name|getCacheManipulator
argument_list|()
expr_stmt|;
name|NativeIO
operator|.
name|POSIX
operator|.
name|setCacheManipulator
argument_list|(
operator|new
name|NoMlockCacheManipulator
argument_list|()
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
literal|1
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cacheManager
operator|=
operator|(
operator|(
name|FsDatasetImpl
operator|)
name|dn
operator|.
name|getFSDataset
argument_list|()
operator|)
operator|.
name|cacheManager
expr_stmt|;
name|cacheLoader
operator|=
operator|(
name|PmemMappableBlockLoader
operator|)
name|cacheManager
operator|.
name|getCacheLoader
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
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
name|NativeIO
operator|.
name|POSIX
operator|.
name|setCacheManipulator
argument_list|(
name|prevCacheManipulator
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdownCluster ()
specifier|protected
specifier|static
name|void
name|shutdownCluster
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
annotation|@
name|Test
DECL|method|testPmemVolumeManager ()
specifier|public
name|void
name|testPmemVolumeManager
parameter_list|()
throws|throws
name|IOException
block|{
name|PmemVolumeManager
name|pmemVolumeManager
init|=
name|PmemVolumeManager
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|pmemVolumeManager
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CACHE_CAPACITY
argument_list|,
name|pmemVolumeManager
operator|.
name|getCacheCapacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test round-robin selection policy
name|long
name|count1
init|=
literal|0
decl_stmt|,
name|count2
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Byte
name|index
init|=
name|pmemVolumeManager
operator|.
name|chooseVolume
argument_list|(
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|String
name|volume
init|=
name|pmemVolumeManager
operator|.
name|getVolumeByIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|volume
operator|.
name|equals
argument_list|(
name|PmemVolumeManager
operator|.
name|getRealPmemDir
argument_list|(
name|PMEM_DIR_0
argument_list|)
argument_list|)
condition|)
block|{
name|count1
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|volume
operator|.
name|equals
argument_list|(
name|PmemVolumeManager
operator|.
name|getRealPmemDir
argument_list|(
name|PMEM_DIR_1
argument_list|)
argument_list|)
condition|)
block|{
name|count2
operator|++
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected persistent storage location:"
operator|+
name|volume
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|count1
argument_list|,
name|count2
argument_list|)
expr_stmt|;
block|}
DECL|method|getExtendedBlockId (Path filePath, long fileLen)
specifier|public
name|List
argument_list|<
name|ExtendedBlockId
argument_list|>
name|getExtendedBlockId
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|long
name|fileLen
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ExtendedBlockId
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|HdfsBlockLocation
index|[]
name|locs
init|=
operator|(
name|HdfsBlockLocation
index|[]
operator|)
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|filePath
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
decl_stmt|;
for|for
control|(
name|HdfsBlockLocation
name|loc
range|:
name|locs
control|)
block|{
name|long
name|bkid
init|=
name|loc
operator|.
name|getLocatedBlock
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|String
name|bpid
init|=
name|loc
operator|.
name|getLocatedBlock
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
operator|new
name|ExtendedBlockId
argument_list|(
name|bkid
argument_list|,
name|bpid
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|keys
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCacheAndUncache ()
specifier|public
name|void
name|testCacheAndUncache
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|maxCacheBlocksNum
init|=
name|Ints
operator|.
name|checkedCast
argument_list|(
name|CACHE_CAPACITY
operator|/
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|BlockReaderTestUtil
operator|.
name|enableHdfsCachingTracing
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|CACHE_CAPACITY
operator|%
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CACHE_CAPACITY
argument_list|,
name|cacheManager
operator|.
name|getPmemCacheCapacity
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
literal|"/testFile"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|testFileLen
init|=
name|maxCacheBlocksNum
operator|*
name|BLOCK_SIZE
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|testFile
argument_list|,
name|testFileLen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xbeef
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ExtendedBlockId
argument_list|>
name|blockKeys
init|=
name|getExtendedBlockId
argument_list|(
name|testFile
argument_list|,
name|testFileLen
argument_list|)
decl_stmt|;
name|fs
operator|.
name|addCachePool
argument_list|(
operator|new
name|CachePoolInfo
argument_list|(
literal|"testPool"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|cacheDirectiveId
init|=
name|fs
operator|.
name|addCacheDirective
argument_list|(
operator|new
name|CacheDirectiveInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setPool
argument_list|(
literal|"testPool"
argument_list|)
operator|.
name|setPath
argument_list|(
name|testFile
argument_list|)
operator|.
name|setReplication
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|// wait for caching
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
name|MetricsRecordBuilder
name|dnMetrics
init|=
name|getMetrics
argument_list|(
name|dn
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|blocksCached
init|=
name|MetricsAsserts
operator|.
name|getLongCounter
argument_list|(
literal|"BlocksCached"
argument_list|,
name|dnMetrics
argument_list|)
decl_stmt|;
if|if
condition|(
name|blocksCached
operator|!=
name|maxCacheBlocksNum
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for "
operator|+
name|maxCacheBlocksNum
operator|+
literal|" blocks to "
operator|+
literal|"be cached. Right now "
operator|+
name|blocksCached
operator|+
literal|" blocks are cached."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|maxCacheBlocksNum
operator|+
literal|" blocks are now cached."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|// The pmem cache space is expected to have been used up.
name|assertEquals
argument_list|(
name|CACHE_CAPACITY
argument_list|,
name|cacheManager
operator|.
name|getPmemCacheUsed
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ExtendedBlockId
argument_list|,
name|Byte
argument_list|>
name|blockKeyToVolume
init|=
name|PmemVolumeManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getBlockKeyToVolume
argument_list|()
decl_stmt|;
comment|// All block keys should be kept in blockKeyToVolume
name|assertEquals
argument_list|(
name|blockKeyToVolume
operator|.
name|size
argument_list|()
argument_list|,
name|maxCacheBlocksNum
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blockKeyToVolume
operator|.
name|keySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|blockKeys
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test each replica's cache file path
for|for
control|(
name|ExtendedBlockId
name|key
range|:
name|blockKeys
control|)
block|{
name|String
name|cachePath
init|=
name|cacheManager
operator|.
name|getReplicaCachePath
argument_list|(
name|key
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|key
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
comment|// The cachePath shouldn't be null if the replica has been cached
comment|// to pmem.
name|assertNotNull
argument_list|(
name|cachePath
argument_list|)
expr_stmt|;
name|String
name|expectFileName
init|=
name|PmemVolumeManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getCacheFileName
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachePath
operator|.
name|startsWith
argument_list|(
name|PMEM_DIR_0
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|cachePath
operator|.
name|equals
argument_list|(
name|PmemVolumeManager
operator|.
name|getRealPmemDir
argument_list|(
name|PMEM_DIR_0
argument_list|)
operator|+
literal|"/"
operator|+
name|expectFileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cachePath
operator|.
name|startsWith
argument_list|(
name|PMEM_DIR_1
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|cachePath
operator|.
name|equals
argument_list|(
name|PmemVolumeManager
operator|.
name|getRealPmemDir
argument_list|(
name|PMEM_DIR_1
argument_list|)
operator|+
literal|"/"
operator|+
name|expectFileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"The cache path is not the expected one: "
operator|+
name|cachePath
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Try to cache another file. Caching this file should fail
comment|// due to lack of available cache space.
specifier|final
name|Path
name|smallTestFile
init|=
operator|new
name|Path
argument_list|(
literal|"/smallTestFile"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|smallTestFileLen
init|=
name|BLOCK_SIZE
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|smallTestFile
argument_list|,
name|smallTestFileLen
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xbeef
argument_list|)
expr_stmt|;
comment|// Try to cache more blocks when no cache space is available.
specifier|final
name|long
name|smallFileCacheDirectiveId
init|=
name|fs
operator|.
name|addCacheDirective
argument_list|(
operator|new
name|CacheDirectiveInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setPool
argument_list|(
literal|"testPool"
argument_list|)
operator|.
name|setPath
argument_list|(
name|smallTestFile
argument_list|)
operator|.
name|setReplication
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
comment|// Wait for enough time to verify smallTestFile could not be cached.
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|dnMetrics
init|=
name|getMetrics
argument_list|(
name|dn
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|blocksCached
init|=
name|MetricsAsserts
operator|.
name|getLongCounter
argument_list|(
literal|"BlocksCached"
argument_list|,
name|dnMetrics
argument_list|)
decl_stmt|;
comment|// The cached block num should not be increased.
name|assertTrue
argument_list|(
name|blocksCached
operator|==
name|maxCacheBlocksNum
argument_list|)
expr_stmt|;
comment|// The blockKeyToVolume should just keep the block keys for the testFile.
name|assertEquals
argument_list|(
name|blockKeyToVolume
operator|.
name|size
argument_list|()
argument_list|,
name|maxCacheBlocksNum
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blockKeyToVolume
operator|.
name|keySet
argument_list|()
operator|.
name|containsAll
argument_list|(
name|blockKeys
argument_list|)
argument_list|)
expr_stmt|;
comment|// Stop trying to cache smallTestFile to avoid interfering the
comment|// verification for uncache functionality.
name|fs
operator|.
name|removeCacheDirective
argument_list|(
name|smallFileCacheDirectiveId
argument_list|)
expr_stmt|;
comment|// Uncache the test file
name|fs
operator|.
name|removeCacheDirective
argument_list|(
name|cacheDirectiveId
argument_list|)
expr_stmt|;
comment|// Wait for uncaching
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
name|MetricsRecordBuilder
name|dnMetrics
init|=
name|getMetrics
argument_list|(
name|dn
operator|.
name|getMetrics
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|blocksUncached
init|=
name|MetricsAsserts
operator|.
name|getLongCounter
argument_list|(
literal|"BlocksUncached"
argument_list|,
name|dnMetrics
argument_list|)
decl_stmt|;
if|if
condition|(
name|blocksUncached
operator|!=
name|maxCacheBlocksNum
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for "
operator|+
name|maxCacheBlocksNum
operator|+
literal|" blocks to be "
operator|+
literal|"uncached. Right now "
operator|+
name|blocksUncached
operator|+
literal|" blocks are uncached."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|maxCacheBlocksNum
operator|+
literal|" blocks have been uncached."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
comment|// It is expected that no pmem cache space is used.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cacheManager
operator|.
name|getPmemCacheUsed
argument_list|()
argument_list|)
expr_stmt|;
comment|// No record should be kept by blockKeyToVolume after testFile is uncached.
name|assertEquals
argument_list|(
name|blockKeyToVolume
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

