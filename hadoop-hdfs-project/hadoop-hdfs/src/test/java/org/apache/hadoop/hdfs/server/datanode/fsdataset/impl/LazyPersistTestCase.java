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
name|commons
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|CreateFlag
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
name|permission
operator|.
name|FsPermission
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
name|DFSClient
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
name|DatanodeUtil
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
name|tools
operator|.
name|JMXGet
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
name|Arrays
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
name|HashSet
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
name|UUID
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
name|fs
operator|.
name|CreateFlag
operator|.
name|CREATE
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
name|fs
operator|.
name|CreateFlag
operator|.
name|LAZY_PERSIST
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
name|*
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
name|StorageType
operator|.
name|DEFAULT
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
name|StorageType
operator|.
name|RAM_DISK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertThat
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

begin_class
DECL|class|LazyPersistTestCase
specifier|public
specifier|abstract
class|class
name|LazyPersistTestCase
block|{
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|blockStateChangeLog
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|NameNode
operator|.
name|stateChangeLog
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|FsDatasetImpl
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|BLOCK_SIZE
specifier|protected
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|5
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|BUFFER_LENGTH
specifier|protected
specifier|static
specifier|final
name|int
name|BUFFER_LENGTH
init|=
literal|4096
decl_stmt|;
DECL|field|EVICTION_LOW_WATERMARK
specifier|protected
specifier|static
specifier|final
name|int
name|EVICTION_LOW_WATERMARK
init|=
literal|1
decl_stmt|;
DECL|field|HEARTBEAT_INTERVAL_SEC
specifier|private
specifier|static
specifier|final
name|long
name|HEARTBEAT_INTERVAL_SEC
init|=
literal|1
decl_stmt|;
DECL|field|HEARTBEAT_RECHECK_INTERVAL_MSEC
specifier|private
specifier|static
specifier|final
name|int
name|HEARTBEAT_RECHECK_INTERVAL_MSEC
init|=
literal|500
decl_stmt|;
DECL|field|JMX_RAM_DISK_METRICS_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|JMX_RAM_DISK_METRICS_PATTERN
init|=
literal|"^RamDisk"
decl_stmt|;
DECL|field|JMX_SERVICE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|JMX_SERVICE_NAME
init|=
literal|"DataNode"
decl_stmt|;
DECL|field|LAZY_WRITE_FILE_SCRUBBER_INTERVAL_SEC
specifier|protected
specifier|static
specifier|final
name|int
name|LAZY_WRITE_FILE_SCRUBBER_INTERVAL_SEC
init|=
literal|3
decl_stmt|;
DECL|field|LAZY_WRITER_INTERVAL_SEC
specifier|protected
specifier|static
specifier|final
name|int
name|LAZY_WRITER_INTERVAL_SEC
init|=
literal|1
decl_stmt|;
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LazyPersistTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REPL_FACTOR
specifier|protected
specifier|static
specifier|final
name|short
name|REPL_FACTOR
init|=
literal|1
decl_stmt|;
DECL|field|cluster
specifier|protected
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|protected
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|client
specifier|protected
name|DFSClient
name|client
decl_stmt|;
DECL|field|jmx
specifier|protected
name|JMXGet
name|jmx
decl_stmt|;
DECL|field|sockDir
specifier|protected
name|TemporarySocketDirectory
name|sockDir
decl_stmt|;
annotation|@
name|After
DECL|method|shutDownCluster ()
specifier|public
name|void
name|shutDownCluster
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Dump all RamDisk JMX metrics before shutdown the cluster
name|printRamDiskJMXMetrics
argument_list|()
expr_stmt|;
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
name|client
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
name|shutdownDataNodes
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|jmx
operator|!=
literal|null
condition|)
block|{
name|jmx
operator|=
literal|null
expr_stmt|;
block|}
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|sockDir
argument_list|)
expr_stmt|;
name|sockDir
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|method|ensureFileReplicasOnStorageType ( Path path, StorageType storageType)
specifier|protected
specifier|final
name|LocatedBlocks
name|ensureFileReplicasOnStorageType
parameter_list|(
name|Path
name|path
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Ensure that returned block locations returned are correct!
name|LOG
operator|.
name|info
argument_list|(
literal|"Ensure path: "
operator|+
name|path
operator|+
literal|" is on StorageType: "
operator|+
name|storageType
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
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
name|assertThat
argument_list|(
name|locatedBlock
operator|.
name|getStorageTypes
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|is
argument_list|(
name|storageType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|locatedBlocks
return|;
block|}
comment|/**    * Make sure at least one non-transient volume has a saved copy of the replica.    * An infinite loop is used to ensure the async lazy persist tasks are completely    * done before verification. Caller of ensureLazyPersistBlocksAreSaved expects    * either a successful pass or timeout failure.    */
DECL|method|ensureLazyPersistBlocksAreSaved ( LocatedBlocks locatedBlocks)
specifier|protected
specifier|final
name|void
name|ensureLazyPersistBlocksAreSaved
parameter_list|(
name|LocatedBlocks
name|locatedBlocks
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|FsVolumeSpi
argument_list|>
name|volumes
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
operator|.
name|getVolumes
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Long
argument_list|>
name|persistedBlockIds
init|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|persistedBlockIds
operator|.
name|size
argument_list|()
operator|<
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// Take 1 second sleep before each verification iteration
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
for|for
control|(
name|FsVolumeSpi
name|v
range|:
name|volumes
control|)
block|{
if|if
condition|(
name|v
operator|.
name|isTransientStorage
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|FsVolumeImpl
name|volume
init|=
operator|(
name|FsVolumeImpl
operator|)
name|v
decl_stmt|;
name|File
name|lazyPersistDir
init|=
name|volume
operator|.
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getLazypersistDir
argument_list|()
decl_stmt|;
name|long
name|blockId
init|=
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|File
name|targetDir
init|=
name|DatanodeUtil
operator|.
name|idToBlockDir
argument_list|(
name|lazyPersistDir
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
name|File
name|blockFile
init|=
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockFile
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// Found a persisted copy for this block and added to the Set
name|persistedBlockIds
operator|.
name|add
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// We should have found a persisted copy for each located block.
name|assertThat
argument_list|(
name|persistedBlockIds
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeRandomTestFile (Path path, long length, boolean isLazyPersist, long seed)
specifier|protected
specifier|final
name|void
name|makeRandomTestFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|isLazyPersist
parameter_list|,
name|long
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|isLazyPersist
argument_list|,
name|BUFFER_LENGTH
argument_list|,
name|length
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|REPL_FACTOR
argument_list|,
name|seed
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|makeTestFile (Path path, long length, boolean isLazyPersist)
specifier|protected
specifier|final
name|void
name|makeTestFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|isLazyPersist
parameter_list|)
throws|throws
name|IOException
block|{
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|createFlags
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|CREATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|isLazyPersist
condition|)
block|{
name|createFlags
operator|.
name|add
argument_list|(
name|LAZY_PERSIST
argument_list|)
expr_stmt|;
block|}
name|FSDataOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fos
operator|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|FsPermission
operator|.
name|getFileDefault
argument_list|()
argument_list|,
name|createFlags
argument_list|,
name|BUFFER_LENGTH
argument_list|,
name|REPL_FACTOR
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Allocate a block.
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|BUFFER_LENGTH
index|]
decl_stmt|;
for|for
control|(
name|int
name|bytesWritten
init|=
literal|0
init|;
name|bytesWritten
operator|<
name|length
condition|;
control|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|bytesWritten
operator|+=
name|buffer
operator|.
name|length
expr_stmt|;
block|}
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|fos
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * If ramDiskStorageLimit is>=0, then RAM_DISK capacity is artificially    * capped. If ramDiskStorageLimit< 0 then it is ignored.    */
DECL|method|startUpCluster (boolean hasTransientStorage, final int ramDiskReplicaCapacity, final boolean useSCR, final boolean useLegacyBlockReaderLocal)
specifier|protected
specifier|final
name|void
name|startUpCluster
parameter_list|(
name|boolean
name|hasTransientStorage
parameter_list|,
specifier|final
name|int
name|ramDiskReplicaCapacity
parameter_list|,
specifier|final
name|boolean
name|useSCR
parameter_list|,
specifier|final
name|boolean
name|useLegacyBlockReaderLocal
parameter_list|)
throws|throws
name|IOException
block|{
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
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_LAZY_PERSIST_FILE_SCRUB_INTERVAL_SEC
argument_list|,
name|LAZY_WRITE_FILE_SCRUBBER_INTERVAL_SEC
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|HEARTBEAT_INTERVAL_SEC
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
name|HEARTBEAT_RECHECK_INTERVAL_MSEC
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_DATANODE_LAZY_WRITER_INTERVAL_SEC
argument_list|,
name|LAZY_WRITER_INTERVAL_SEC
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_DATANODE_RAM_DISK_LOW_WATERMARK_BYTES
argument_list|,
name|EVICTION_LOW_WATERMARK
operator|*
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
if|if
condition|(
name|useSCR
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_READ_SHORTCIRCUIT_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Do not share a client context across tests.
name|conf
operator|.
name|set
argument_list|(
name|DFS_CLIENT_CONTEXT
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|useLegacyBlockReaderLocal
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_USE_LEGACY_BLOCKREADERLOCAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_BLOCK_LOCAL_PATH_ACCESS_USER_KEY
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sockDir
operator|=
operator|new
name|TemporarySocketDirectory
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"._PORT.sock"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|long
index|[]
name|capacities
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasTransientStorage
operator|&&
name|ramDiskReplicaCapacity
operator|>=
literal|0
condition|)
block|{
comment|// Convert replica count to byte count, add some delta for .meta and
comment|// VERSION files.
name|long
name|ramDiskStorageLimit
init|=
operator|(
operator|(
name|long
operator|)
name|ramDiskReplicaCapacity
operator|*
name|BLOCK_SIZE
operator|)
operator|+
operator|(
name|BLOCK_SIZE
operator|-
literal|1
operator|)
decl_stmt|;
name|capacities
operator|=
operator|new
name|long
index|[]
block|{
name|ramDiskStorageLimit
block|,
operator|-
literal|1
block|}
expr_stmt|;
block|}
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
name|REPL_FACTOR
argument_list|)
operator|.
name|storageCapacities
argument_list|(
name|capacities
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|hasTransientStorage
condition|?
operator|new
name|StorageType
index|[]
block|{
name|RAM_DISK
block|,
name|DEFAULT
block|}
else|:
literal|null
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|client
operator|=
name|fs
operator|.
name|getClient
argument_list|()
expr_stmt|;
try|try
block|{
name|jmx
operator|=
name|initJMX
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Failed initialize JMX for testing: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Cluster startup complete"
argument_list|)
expr_stmt|;
block|}
comment|/**    * If ramDiskStorageLimit is>=0, then RAM_DISK capacity is artificially    * capped. If ramDiskStorageLimit< 0 then it is ignored.    */
DECL|method|startUpCluster (final int numDataNodes, final StorageType[] storageTypes, final long ramDiskStorageLimit, final boolean useSCR)
specifier|protected
specifier|final
name|void
name|startUpCluster
parameter_list|(
specifier|final
name|int
name|numDataNodes
parameter_list|,
specifier|final
name|StorageType
index|[]
name|storageTypes
parameter_list|,
specifier|final
name|long
name|ramDiskStorageLimit
parameter_list|,
specifier|final
name|boolean
name|useSCR
parameter_list|)
throws|throws
name|IOException
block|{
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
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_LAZY_PERSIST_FILE_SCRUB_INTERVAL_SEC
argument_list|,
name|LAZY_WRITE_FILE_SCRUBBER_INTERVAL_SEC
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|HEARTBEAT_INTERVAL_SEC
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
name|HEARTBEAT_RECHECK_INTERVAL_MSEC
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_DATANODE_LAZY_WRITER_INTERVAL_SEC
argument_list|,
name|LAZY_WRITER_INTERVAL_SEC
argument_list|)
expr_stmt|;
if|if
condition|(
name|useSCR
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_CLIENT_READ_SHORTCIRCUIT_KEY
argument_list|,
name|useSCR
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_CLIENT_CONTEXT
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sockDir
operator|=
operator|new
name|TemporarySocketDirectory
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"._PORT.sock"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_BLOCK_LOCAL_PATH_ACCESS_USER_KEY
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|numDataNodes
argument_list|)
operator|.
name|storageTypes
argument_list|(
name|storageTypes
operator|!=
literal|null
condition|?
name|storageTypes
else|:
operator|new
name|StorageType
index|[]
block|{
name|DEFAULT
block|,
name|DEFAULT
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|client
operator|=
name|fs
operator|.
name|getClient
argument_list|()
expr_stmt|;
comment|// Artificially cap the storage capacity of the RAM_DISK volume.
if|if
condition|(
name|ramDiskStorageLimit
operator|>=
literal|0
condition|)
block|{
name|List
argument_list|<
name|?
extends|extends
name|FsVolumeSpi
argument_list|>
name|volumes
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
operator|.
name|getVolumes
argument_list|()
decl_stmt|;
for|for
control|(
name|FsVolumeSpi
name|volume
range|:
name|volumes
control|)
block|{
if|if
condition|(
name|volume
operator|.
name|getStorageType
argument_list|()
operator|==
name|RAM_DISK
condition|)
block|{
operator|(
operator|(
name|FsVolumeImpl
operator|)
name|volume
operator|)
operator|.
name|setCapacityForTesting
argument_list|(
name|ramDiskStorageLimit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Cluster startup complete"
argument_list|)
expr_stmt|;
block|}
DECL|method|startUpCluster (boolean hasTransientStorage, final int ramDiskReplicaCapacity)
specifier|protected
specifier|final
name|void
name|startUpCluster
parameter_list|(
name|boolean
name|hasTransientStorage
parameter_list|,
specifier|final
name|int
name|ramDiskReplicaCapacity
parameter_list|)
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|hasTransientStorage
argument_list|,
name|ramDiskReplicaCapacity
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|triggerBlockReport ()
specifier|protected
specifier|final
name|void
name|triggerBlockReport
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Trigger block report to NN
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyBlockDeletedFromDir (File dir, LocatedBlocks locatedBlocks)
specifier|protected
specifier|final
name|boolean
name|verifyBlockDeletedFromDir
parameter_list|(
name|File
name|dir
parameter_list|,
name|LocatedBlocks
name|locatedBlocks
parameter_list|)
block|{
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|locatedBlocks
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|File
name|targetDir
init|=
name|DatanodeUtil
operator|.
name|idToBlockDir
argument_list|(
name|dir
argument_list|,
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|blockFile
init|=
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"blockFile: "
operator|+
name|blockFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" exists after deletion."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|File
name|metaFile
init|=
operator|new
name|File
argument_list|(
name|targetDir
argument_list|,
name|DatanodeUtil
operator|.
name|getMetaName
argument_list|(
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockName
argument_list|()
argument_list|,
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"metaFile: "
operator|+
name|metaFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" exists after deletion."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|verifyDeletedBlocks (LocatedBlocks locatedBlocks)
specifier|protected
specifier|final
name|boolean
name|verifyDeletedBlocks
parameter_list|(
name|LocatedBlocks
name|locatedBlocks
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Verifying replica has no saved copy after deletion."
argument_list|)
expr_stmt|;
name|triggerBlockReport
argument_list|()
expr_stmt|;
while|while
condition|(
name|DataNodeTestUtils
operator|.
name|getPendingAsyncDeletions
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|>
literal|0L
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|bpid
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|FsVolumeSpi
argument_list|>
name|volumes
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
operator|.
name|getVolumes
argument_list|()
decl_stmt|;
comment|// Make sure deleted replica does not have a copy on either finalized dir of
comment|// transient volume or finalized dir of non-transient volume
for|for
control|(
name|FsVolumeSpi
name|v
range|:
name|volumes
control|)
block|{
name|FsVolumeImpl
name|volume
init|=
operator|(
name|FsVolumeImpl
operator|)
name|v
decl_stmt|;
name|File
name|targetDir
init|=
operator|(
name|v
operator|.
name|isTransientStorage
argument_list|()
operator|)
condition|?
name|volume
operator|.
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getFinalizedDir
argument_list|()
else|:
name|volume
operator|.
name|getBlockPoolSlice
argument_list|(
name|bpid
argument_list|)
operator|.
name|getLazypersistDir
argument_list|()
decl_stmt|;
if|if
condition|(
name|verifyBlockDeletedFromDir
argument_list|(
name|targetDir
argument_list|,
name|locatedBlocks
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|verifyRamDiskJMXMetric (String metricName, long expectedValue)
specifier|protected
specifier|final
name|void
name|verifyRamDiskJMXMetric
parameter_list|(
name|String
name|metricName
parameter_list|,
name|long
name|expectedValue
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|expectedValue
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|jmx
operator|.
name|getValue
argument_list|(
name|metricName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyReadRandomFile ( Path path, int fileLength, int seed)
specifier|protected
specifier|final
name|boolean
name|verifyReadRandomFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|int
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|contents
index|[]
init|=
name|DFSTestUtil
operator|.
name|readFileBuffer
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|byte
name|expected
index|[]
init|=
name|DFSTestUtil
operator|.
name|calculateFileContentsFromSeed
argument_list|(
name|seed
argument_list|,
name|fileLength
argument_list|)
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|contents
argument_list|,
name|expected
argument_list|)
return|;
block|}
DECL|method|initJMX ()
specifier|private
name|JMXGet
name|initJMX
parameter_list|()
throws|throws
name|Exception
block|{
name|JMXGet
name|jmx
init|=
operator|new
name|JMXGet
argument_list|()
decl_stmt|;
name|jmx
operator|.
name|setService
argument_list|(
name|JMX_SERVICE_NAME
argument_list|)
expr_stmt|;
name|jmx
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|jmx
return|;
block|}
DECL|method|printRamDiskJMXMetrics ()
specifier|private
name|void
name|printRamDiskJMXMetrics
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|jmx
operator|!=
literal|null
condition|)
block|{
name|jmx
operator|.
name|printAllMatchedAttributes
argument_list|(
name|JMX_RAM_DISK_METRICS_PATTERN
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

