begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
package|;
end_package

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
name|EnumSet
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|FSDataInputStream
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
name|ReadOption
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
name|net
operator|.
name|unix
operator|.
name|DomainSocket
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
name|unix
operator|.
name|TemporarySocketDirectory
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
name|NativeCodeLoader
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

begin_class
DECL|class|TestFsDatasetCacheRevocation
specifier|public
class|class
name|TestFsDatasetCacheRevocation
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
name|TestFsDatasetCacheRevocation
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|prevCacheManipulator
specifier|private
specifier|static
name|CacheManipulator
name|prevCacheManipulator
decl_stmt|;
DECL|field|sockDir
specifier|private
specifier|static
name|TemporarySocketDirectory
name|sockDir
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
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
name|DomainSocket
operator|.
name|disableBindPathValidation
argument_list|()
expr_stmt|;
name|sockDir
operator|=
operator|new
name|TemporarySocketDirectory
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
comment|// Restore the original CacheManipulator
name|NativeIO
operator|.
name|POSIX
operator|.
name|setCacheManipulator
argument_list|(
name|prevCacheManipulator
argument_list|)
expr_stmt|;
name|sockDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getDefaultConf ()
specifier|private
specifier|static
name|Configuration
name|getDefaultConf
parameter_list|()
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
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_PATH_BASED_CACHE_REFRESH_INTERVAL_MS
argument_list|,
literal|50
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
literal|250
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
name|TestFsDatasetCache
operator|.
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
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_SHORTCIRCUIT_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DOMAIN_SOCKET_PATH_KEY
argument_list|,
operator|new
name|File
argument_list|(
name|sockDir
operator|.
name|getDir
argument_list|()
argument_list|,
literal|"sock"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Test that when a client has a replica mmapped, we will not un-mlock that    * replica for a reasonable amount of time, even if an uncache request    * occurs.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testPinning ()
specifier|public
name|void
name|testPinning
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
operator|&&
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|getDefaultConf
argument_list|()
decl_stmt|;
comment|// Set a really long revocation timeout, so that we won't reach it during
comment|// this test.
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_CACHE_REVOCATION_TIMEOUT_MS
argument_list|,
literal|1800000L
argument_list|)
expr_stmt|;
comment|// Poll very often
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_CACHE_REVOCATION_POLLING_MS
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create and cache a file.
specifier|final
name|String
name|TEST_FILE
init|=
literal|"/test_file"
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xcafe
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|addCachePool
argument_list|(
operator|new
name|CachePoolInfo
argument_list|(
literal|"pool"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|cacheDirectiveId
init|=
name|dfs
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
literal|"pool"
argument_list|)
operator|.
name|setPath
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
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
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFSDataset
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|verifyExpectedCacheUsage
argument_list|(
name|BLOCK_SIZE
argument_list|,
literal|1
argument_list|,
name|fsd
argument_list|)
expr_stmt|;
comment|// Mmap the file.
name|FSDataInputStream
name|in
init|=
name|dfs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|in
operator|.
name|read
argument_list|(
literal|null
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ReadOption
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
comment|// Attempt to uncache file.  The file should still be cached.
name|dfs
operator|.
name|removeCacheDirective
argument_list|(
name|cacheDirectiveId
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyExpectedCacheUsage
argument_list|(
name|BLOCK_SIZE
argument_list|,
literal|1
argument_list|,
name|fsd
argument_list|)
expr_stmt|;
comment|// Un-mmap the file.  The file should be uncached after this.
name|in
operator|.
name|releaseBuffer
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyExpectedCacheUsage
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|fsd
argument_list|)
expr_stmt|;
comment|// Cleanup
name|in
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
comment|/**    * Test that when we have an uncache request, and the client refuses to release    * the replica for a long time, we will un-mlock it.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testRevocation ()
specifier|public
name|void
name|testRevocation
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
operator|&&
operator|!
name|Path
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|BlockReaderTestUtil
operator|.
name|enableHdfsCachingTracing
argument_list|()
expr_stmt|;
name|BlockReaderTestUtil
operator|.
name|enableShortCircuitShmTracing
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|getDefaultConf
argument_list|()
decl_stmt|;
comment|// Set a really short revocation timeout.
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_CACHE_REVOCATION_TIMEOUT_MS
argument_list|,
literal|250L
argument_list|)
expr_stmt|;
comment|// Poll very often
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_CACHE_REVOCATION_POLLING_MS
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create and cache a file.
specifier|final
name|String
name|TEST_FILE
init|=
literal|"/test_file2"
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xcafe
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|addCachePool
argument_list|(
operator|new
name|CachePoolInfo
argument_list|(
literal|"pool"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|cacheDirectiveId
init|=
name|dfs
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
literal|"pool"
argument_list|)
operator|.
name|setPath
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
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
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|fsd
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFSDataset
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|verifyExpectedCacheUsage
argument_list|(
name|BLOCK_SIZE
argument_list|,
literal|1
argument_list|,
name|fsd
argument_list|)
expr_stmt|;
comment|// Mmap the file.
name|FSDataInputStream
name|in
init|=
name|dfs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|in
operator|.
name|read
argument_list|(
literal|null
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ReadOption
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
comment|// Attempt to uncache file.  The file should get uncached.
name|LOG
operator|.
name|info
argument_list|(
literal|"removing cache directive {}"
argument_list|,
name|cacheDirectiveId
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|removeCacheDirective
argument_list|(
name|cacheDirectiveId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"finished removing cache directive {}"
argument_list|,
name|cacheDirectiveId
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyExpectedCacheUsage
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|fsd
argument_list|)
expr_stmt|;
comment|// Cleanup
name|in
operator|.
name|releaseBuffer
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|in
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
end_class

end_unit

