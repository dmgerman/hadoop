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
name|*
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
name|EnumSet
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
name|test
operator|.
name|GenericTestUtils
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
name|Test
import|;
end_import

begin_class
DECL|class|TestLazyPersistFiles
specifier|public
class|class
name|TestLazyPersistFiles
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestLazyPersistFiles
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|REPL_FACTOR
specifier|private
specifier|static
name|short
name|REPL_FACTOR
init|=
literal|1
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|10485760
decl_stmt|;
comment|// 10 MB
DECL|field|LAZY_WRITE_FILE_SCRUBBER_INTERVAL_SEC
specifier|private
specifier|static
specifier|final
name|int
name|LAZY_WRITE_FILE_SCRUBBER_INTERVAL_SEC
init|=
literal|3
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
DECL|field|LAZY_WRITER_INTERVAL_SEC
specifier|private
specifier|static
specifier|final
name|int
name|LAZY_WRITER_INTERVAL_SEC
init|=
literal|1
decl_stmt|;
DECL|field|BUFFER_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_LENGTH
init|=
literal|4096
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|client
specifier|private
name|DFSClient
name|client
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|After
DECL|method|shutDownCluster ()
specifier|public
name|void
name|shutDownCluster
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
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testFlagNotSetByDefault ()
specifier|public
name|void
name|testFlagNotSetByDefault
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Stat the file and check that the lazyPersist flag is returned back.
name|HdfsFileStatus
name|status
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
decl_stmt|;
name|assertThat
argument_list|(
name|status
operator|.
name|isLazyPersist
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testFlagPropagation ()
specifier|public
name|void
name|testFlagPropagation
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Stat the file and check that the lazyPersist flag is returned back.
name|HdfsFileStatus
name|status
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
decl_stmt|;
name|assertThat
argument_list|(
name|status
operator|.
name|isLazyPersist
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testFlagPersistenceInEditLog ()
specifier|public
name|void
name|testFlagPersistenceInEditLog
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Stat the file and check that the lazyPersist flag is returned back.
name|HdfsFileStatus
name|status
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
decl_stmt|;
name|assertThat
argument_list|(
name|status
operator|.
name|isLazyPersist
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testFlagPersistenceInFsImage ()
specifier|public
name|void
name|testFlagPersistenceInFsImage
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fos
init|=
literal|null
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// checkpoint
name|fs
operator|.
name|setSafeMode
argument_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|fs
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|fs
operator|.
name|setSafeMode
argument_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Stat the file and check that the lazyPersist flag is returned back.
name|HdfsFileStatus
name|status
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
decl_stmt|;
name|assertThat
argument_list|(
name|status
operator|.
name|isLazyPersist
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testPlacementOnRamDisk ()
specifier|public
name|void
name|testPlacementOnRamDisk
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|DEFAULT
block|,
name|RAM_DISK
block|}
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testFallbackToDisk ()
specifier|public
name|void
name|testFallbackToDisk
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path
argument_list|,
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * If the only available storage is RAM_DISK and the LAZY_PERSIST flag is not    * specified, then block placement should fail.    *    * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testRamDiskNotChosenByDefault ()
specifier|public
name|void
name|testRamDiskNotChosenByDefault
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|RAM_DISK
block|,
name|RAM_DISK
block|}
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
try|try
block|{
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Block placement to RAM_DISK should have failed without lazyPersist flag"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception "
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testAppendIsDenied ()
specifier|public
name|void
name|testAppendIsDenied
parameter_list|()
throws|throws
name|IOException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|RAM_DISK
block|,
name|DEFAULT
block|}
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|append
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|BUFFER_LENGTH
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Append to LazyPersist file did not fail as expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception "
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * If one or more replicas of a lazyPersist file are lost, then the file    * must be discarded by the NN, instead of being kept around as a    * 'corrupt' file.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testLazyPersistFilesAreDiscarded ()
specifier|public
name|void
name|testLazyPersistFilesAreDiscarded
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|RAM_DISK
block|,
name|DEFAULT
block|}
argument_list|,
operator|(
literal|2
operator|*
name|BLOCK_SIZE
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
comment|// 1 replica + delta.
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".01.dat"
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".02.dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path1
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|makeTestFile
argument_list|(
name|path2
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path2
argument_list|,
name|DEFAULT
argument_list|)
expr_stmt|;
comment|// Stop the DataNode and sleep for the time it takes the NN to
comment|// detect the DN as being dead.
name|cluster
operator|.
name|shutdownDataNodes
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|30000L
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getNumDeadDataNodes
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Next, wait for the replication monitor to mark the file as
comment|// corrupt, plus some delta.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2
operator|*
name|DFS_NAMENODE_REPLICATION_INTERVAL_DEFAULT
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Wait for the LazyPersistFileScrubber to run, plus some delta.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2
operator|*
name|LAZY_WRITE_FILE_SCRUBBER_INTERVAL_SEC
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Ensure that path1 does not exist anymore, whereas path2 does.
assert|assert
operator|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|path1
argument_list|)
operator|)
assert|;
assert|assert
operator|(
name|fs
operator|.
name|exists
argument_list|(
name|path2
argument_list|)
operator|)
assert|;
comment|// We should have only one block that needs replication i.e. the one
comment|// belonging to path2.
name|assertThat
argument_list|(
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
name|getUnderReplicatedBlocksCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testLazyPersistBlocksAreSaved ()
specifier|public
name|void
name|testLazyPersistBlocksAreSaved
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|RAM_DISK
block|,
name|DEFAULT
block|}
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
comment|// Create a test file
name|makeTestFile
argument_list|(
name|path
argument_list|,
name|BLOCK_SIZE
operator|*
literal|10
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|locatedBlocks
init|=
name|ensureFileReplicasOnStorageType
argument_list|(
name|path
argument_list|,
name|RAM_DISK
argument_list|)
decl_stmt|;
comment|// Sleep for a short time to allow the lazy writer thread to do its job
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
name|LAZY_WRITER_INTERVAL_SEC
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// Make sure that there is a saved copy of the replica on persistent
comment|// storage.
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
comment|// Make sure at least one non-transient volume has a saved copy of
comment|// the replica.
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
name|lazyPersistDir
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
comment|// Found a persisted copy for this block!
name|boolean
name|added
init|=
name|persistedBlockIds
operator|.
name|add
argument_list|(
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|added
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testRamDiskEviction ()
specifier|public
name|void
name|testRamDiskEviction
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|RAM_DISK
block|,
name|DEFAULT
block|}
argument_list|,
operator|(
literal|2
operator|*
name|BLOCK_SIZE
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
comment|// 1 replica + delta.
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".01.dat"
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".02.dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path1
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
comment|// Sleep for a short time to allow the lazy writer thread to do its job.
comment|// However the block replica should not be evicted from RAM_DISK yet.
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
name|LAZY_WRITER_INTERVAL_SEC
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
comment|// Create another file with a replica on RAM_DISK.
name|makeTestFile
argument_list|(
name|path2
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
comment|// Make sure that the second file's block replica is on RAM_DISK, whereas
comment|// the original file's block replica is now on disk.
name|ensureFileReplicasOnStorageType
argument_list|(
name|path2
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * TODO: Stub test, to be completed.    * Verify that checksum computation is skipped for files written to memory.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testChecksumIsSkipped ()
specifier|public
name|void
name|testChecksumIsSkipped
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|startUpCluster
argument_list|(
name|REPL_FACTOR
argument_list|,
operator|new
name|StorageType
index|[]
block|{
name|RAM_DISK
block|,
name|DEFAULT
block|}
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|METHOD_NAME
init|=
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|METHOD_NAME
operator|+
literal|".01.dat"
argument_list|)
decl_stmt|;
name|makeTestFile
argument_list|(
name|path1
argument_list|,
name|BLOCK_SIZE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ensureFileReplicasOnStorageType
argument_list|(
name|path1
argument_list|,
name|RAM_DISK
argument_list|)
expr_stmt|;
comment|// Verify checksum was not computed.
block|}
comment|// ---- Utility functions for all test cases -------------------------------
comment|/**    * If ramDiskStorageLimit is>=0, then RAM_DISK capacity is artificially    * capped. If tmpfsStorageLimit< 0 then it is ignored.    */
DECL|method|startUpCluster (final int numDataNodes, final StorageType[] storageTypes, final long ramDiskStorageLimit)
specifier|private
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
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
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
name|REPL_FACTOR
operator|=
literal|1
expr_stmt|;
comment|//Reset if case a test has modified the value
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
comment|// Artifically cap the storage capacity of the tmpfs volume.
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
name|FsTransientVolumeImpl
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
DECL|method|makeTestFile (Path path, long length, final boolean isLazyPersist)
specifier|private
name|void
name|makeTestFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|long
name|length
parameter_list|,
specifier|final
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
DECL|method|ensureFileReplicasOnStorageType ( Path path, StorageType storageType)
specifier|private
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
block|}
end_class

end_unit

