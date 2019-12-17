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
name|EnumSet
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
name|Map
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
name|concurrent
operator|.
name|ThreadLocalRandom
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
name|atomic
operator|.
name|AtomicBoolean
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
name|BatchedRemoteIterator
operator|.
name|BatchedEntries
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
name|HAUtil
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
name|MiniDFSNNTopology
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
name|OpenFileEntry
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
name|OpenFilesIterator
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
name|OpenFilesIterator
operator|.
name|OpenFilesType
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
name|ha
operator|.
name|HATestUtil
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
name|NamenodeProtocols
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
name|tools
operator|.
name|DFSAdmin
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
name|ToolRunner
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

begin_comment
comment|/**  * Verify open files listing.  */
end_comment

begin_class
DECL|class|TestListOpenFiles
specifier|public
class|class
name|TestListOpenFiles
block|{
DECL|field|NUM_DATA_NODES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DATA_NODES
init|=
literal|3
decl_stmt|;
DECL|field|BATCH_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BATCH_SIZE
init|=
literal|5
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
init|=
literal|null
decl_stmt|;
DECL|field|nnRpc
specifier|private
specifier|static
name|NamenodeProtocols
name|nnRpc
init|=
literal|null
decl_stmt|;
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
name|TestListOpenFiles
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|Configuration
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
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LIST_OPENFILES_NUM_RESPONSES
argument_list|,
name|BATCH_SIZE
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
name|NUM_DATA_NODES
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
name|nnRpc
operator|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|IOException
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
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000L
argument_list|)
DECL|method|testListOpenFilesViaNameNodeRPC ()
specifier|public
name|void
name|testListOpenFilesViaNameNodeRPC
parameter_list|()
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|Path
argument_list|,
name|FSDataOutputStream
argument_list|>
name|openFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|createFiles
argument_list|(
name|fs
argument_list|,
literal|"closed"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
name|BatchedEntries
argument_list|<
name|OpenFileEntry
argument_list|>
name|openFileEntryBatchedEntries
init|=
name|nnRpc
operator|.
name|listOpenFiles
argument_list|(
literal|0
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|OpenFilesType
operator|.
name|ALL_OPEN_FILES
argument_list|)
argument_list|,
name|OpenFilesIterator
operator|.
name|FILTER_PATH_DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Open files list should be empty!"
argument_list|,
name|openFileEntryBatchedEntries
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|BatchedEntries
argument_list|<
name|OpenFileEntry
argument_list|>
name|openFilesBlockingDecomEntries
init|=
name|nnRpc
operator|.
name|listOpenFiles
argument_list|(
literal|0
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|OpenFilesType
operator|.
name|BLOCKING_DECOMMISSION
argument_list|)
argument_list|,
name|OpenFilesIterator
operator|.
name|FILTER_PATH_DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Open files list blocking decommission should be empty!"
argument_list|,
name|openFilesBlockingDecomEntries
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|putAll
argument_list|(
name|DFSTestUtil
operator|.
name|createOpenFiles
argument_list|(
name|fs
argument_list|,
literal|"open-1"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|putAll
argument_list|(
name|DFSTestUtil
operator|.
name|createOpenFiles
argument_list|(
name|fs
argument_list|,
literal|"open-2"
argument_list|,
operator|(
name|BATCH_SIZE
operator|*
literal|2
operator|+
name|BATCH_SIZE
operator|/
literal|2
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|closeOpenFiles
argument_list|(
name|openFiles
argument_list|,
name|openFiles
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|putAll
argument_list|(
name|DFSTestUtil
operator|.
name|createOpenFiles
argument_list|(
name|fs
argument_list|,
literal|"open-3"
argument_list|,
operator|(
name|BATCH_SIZE
operator|*
literal|5
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
while|while
condition|(
name|openFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DFSTestUtil
operator|.
name|closeOpenFiles
argument_list|(
name|openFiles
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyOpenFiles (Map<Path, FSDataOutputStream> openFiles, EnumSet<OpenFilesType> openFilesTypes, String path)
specifier|private
name|void
name|verifyOpenFiles
parameter_list|(
name|Map
argument_list|<
name|Path
argument_list|,
name|FSDataOutputStream
argument_list|>
name|openFiles
parameter_list|,
name|EnumSet
argument_list|<
name|OpenFilesType
argument_list|>
name|openFilesTypes
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
argument_list|<
name|Path
argument_list|>
name|remainingFiles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|openFiles
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|OpenFileEntry
name|lastEntry
init|=
literal|null
decl_stmt|;
name|BatchedEntries
argument_list|<
name|OpenFileEntry
argument_list|>
name|batchedEntries
decl_stmt|;
do|do
block|{
if|if
condition|(
name|lastEntry
operator|==
literal|null
condition|)
block|{
name|batchedEntries
operator|=
name|nnRpc
operator|.
name|listOpenFiles
argument_list|(
literal|0
argument_list|,
name|openFilesTypes
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|batchedEntries
operator|=
name|nnRpc
operator|.
name|listOpenFiles
argument_list|(
name|lastEntry
operator|.
name|getId
argument_list|()
argument_list|,
name|openFilesTypes
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Incorrect open files list size!"
argument_list|,
name|batchedEntries
operator|.
name|size
argument_list|()
operator|<=
name|BATCH_SIZE
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
name|batchedEntries
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|lastEntry
operator|=
name|batchedEntries
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|String
name|filePath
init|=
name|lastEntry
operator|.
name|getFilePath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"OpenFile: "
operator|+
name|filePath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected open file: "
operator|+
name|filePath
argument_list|,
name|remainingFiles
operator|.
name|remove
argument_list|(
operator|new
name|Path
argument_list|(
name|filePath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|batchedEntries
operator|.
name|hasMore
argument_list|()
condition|)
do|;
name|assertTrue
argument_list|(
name|remainingFiles
operator|.
name|size
argument_list|()
operator|+
literal|" open files not listed!"
argument_list|,
name|remainingFiles
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify all open files.    */
DECL|method|verifyOpenFiles (Map<Path, FSDataOutputStream> openFiles)
specifier|private
name|void
name|verifyOpenFiles
parameter_list|(
name|Map
argument_list|<
name|Path
argument_list|,
name|FSDataOutputStream
argument_list|>
name|openFiles
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|,
name|OpenFilesIterator
operator|.
name|FILTER_PATH_DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify open files with specified filter path.    */
DECL|method|verifyOpenFiles (Map<Path, FSDataOutputStream> openFiles, String path)
specifier|private
name|void
name|verifyOpenFiles
parameter_list|(
name|Map
argument_list|<
name|Path
argument_list|,
name|FSDataOutputStream
argument_list|>
name|openFiles
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|OpenFilesType
operator|.
name|ALL_OPEN_FILES
argument_list|)
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|OpenFilesType
operator|.
name|BLOCKING_DECOMMISSION
argument_list|)
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|createFiles (FileSystem fileSystem, String fileNamePrefix, int numFilesToCreate)
specifier|private
name|Set
argument_list|<
name|Path
argument_list|>
name|createFiles
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|String
name|fileNamePrefix
parameter_list|,
name|int
name|numFilesToCreate
parameter_list|)
throws|throws
name|IOException
block|{
name|HashSet
argument_list|<
name|Path
argument_list|>
name|files
init|=
operator|new
name|HashSet
argument_list|<>
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
name|numFilesToCreate
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileNamePrefix
operator|+
literal|"-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fileSystem
argument_list|,
name|filePath
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|files
return|;
block|}
comment|/**    * Verify dfsadmin -listOpenFiles command in HA mode.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testListOpenFilesInHA ()
specifier|public
name|void
name|testListOpenFilesInHA
parameter_list|()
throws|throws
name|Exception
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|haConf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|haConf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LIST_OPENFILES_NUM_RESPONSES
argument_list|,
name|BATCH_SIZE
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|haCluster
init|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|haConf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|HATestUtil
operator|.
name|setFailoverConfigurations
argument_list|(
name|haCluster
argument_list|,
name|haConf
argument_list|)
expr_stmt|;
name|FileSystem
name|fileSystem
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|haCluster
argument_list|,
name|haConf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ClientProtocol
argument_list|>
name|namenodes
init|=
name|HAUtil
operator|.
name|getProxiesForAllNameNodesInNameservice
argument_list|(
name|haConf
argument_list|,
name|HATestUtil
operator|.
name|getLogicalHostname
argument_list|(
name|haCluster
argument_list|)
argument_list|)
decl_stmt|;
name|haCluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|HAUtil
operator|.
name|isAtLeastOneActive
argument_list|(
name|namenodes
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createOpenFiles
argument_list|(
name|fileSystem
argument_list|,
literal|"ha-open-file"
argument_list|,
operator|(
operator|(
name|BATCH_SIZE
operator|*
literal|4
operator|)
operator|+
operator|(
name|BATCH_SIZE
operator|/
literal|2
operator|)
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|DFSAdmin
name|dfsAdmin
init|=
operator|new
name|DFSAdmin
argument_list|(
name|haConf
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failoverCompleted
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|listOpenFilesError
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|int
name|listingIntervalMsec
init|=
literal|250
decl_stmt|;
name|Thread
name|clientThread
init|=
operator|new
name|Thread
argument_list|(
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
block|{
while|while
condition|(
operator|!
name|failoverCompleted
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ToolRunner
operator|.
name|run
argument_list|(
name|dfsAdmin
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-listOpenFiles"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ToolRunner
operator|.
name|run
argument_list|(
name|dfsAdmin
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-listOpenFiles"
block|,
literal|"-blockingDecommission"
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// Sleep for some time to avoid
comment|// flooding logs with listing.
name|Thread
operator|.
name|sleep
argument_list|(
name|listingIntervalMsec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listOpenFilesError
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Error listing open files: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|clientThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Let client list open files for few
comment|// times before the NN failover.
name|Thread
operator|.
name|sleep
argument_list|(
name|listingIntervalMsec
operator|*
literal|2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down Active NN0!"
argument_list|)
expr_stmt|;
name|haCluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Transitioning NN1 to Active!"
argument_list|)
expr_stmt|;
name|haCluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|failoverCompleted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ToolRunner
operator|.
name|run
argument_list|(
name|dfsAdmin
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-listOpenFiles"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ToolRunner
operator|.
name|run
argument_list|(
name|dfsAdmin
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-listOpenFiles"
block|,
literal|"-blockingDecommission"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Client Error!"
argument_list|,
name|listOpenFilesError
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|clientThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|haCluster
operator|!=
literal|null
condition|)
block|{
name|haCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testListOpenFilesWithFilterPath ()
specifier|public
name|void
name|testListOpenFilesWithFilterPath
parameter_list|()
throws|throws
name|IOException
block|{
name|HashMap
argument_list|<
name|Path
argument_list|,
name|FSDataOutputStream
argument_list|>
name|openFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|createFiles
argument_list|(
name|fs
argument_list|,
literal|"closed"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|,
name|OpenFilesIterator
operator|.
name|FILTER_PATH_DEFAULT
argument_list|)
expr_stmt|;
name|BatchedEntries
argument_list|<
name|OpenFileEntry
argument_list|>
name|openFileEntryBatchedEntries
init|=
name|nnRpc
operator|.
name|listOpenFiles
argument_list|(
literal|0
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|OpenFilesType
operator|.
name|ALL_OPEN_FILES
argument_list|)
argument_list|,
name|OpenFilesIterator
operator|.
name|FILTER_PATH_DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Open files list should be empty!"
argument_list|,
name|openFileEntryBatchedEntries
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|BatchedEntries
argument_list|<
name|OpenFileEntry
argument_list|>
name|openFilesBlockingDecomEntries
init|=
name|nnRpc
operator|.
name|listOpenFiles
argument_list|(
literal|0
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|OpenFilesType
operator|.
name|BLOCKING_DECOMMISSION
argument_list|)
argument_list|,
name|OpenFilesIterator
operator|.
name|FILTER_PATH_DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Open files list blocking decommission should be empty!"
argument_list|,
name|openFilesBlockingDecomEntries
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|putAll
argument_list|(
name|DFSTestUtil
operator|.
name|createOpenFiles
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/base"
argument_list|)
argument_list|,
literal|"open-1"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Path
argument_list|,
name|FSDataOutputStream
argument_list|>
name|baseOpen
init|=
name|DFSTestUtil
operator|.
name|createOpenFiles
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/base-open"
argument_list|)
argument_list|,
literal|"open-1"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|,
literal|"/base"
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|,
literal|"/base/"
argument_list|)
expr_stmt|;
name|openFiles
operator|.
name|putAll
argument_list|(
name|baseOpen
argument_list|)
expr_stmt|;
while|while
condition|(
name|openFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DFSTestUtil
operator|.
name|closeOpenFiles
argument_list|(
name|openFiles
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|verifyOpenFiles
argument_list|(
name|openFiles
argument_list|,
name|OpenFilesIterator
operator|.
name|FILTER_PATH_DEFAULT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

