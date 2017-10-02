begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|assertArrayEquals
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
name|assertTrue
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
name|Random
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
name|CommonConfigurationKeysPublic
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
name|FileStatus
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
name|FileUtil
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|FSImage
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
name|FSNamesystem
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
name|PathUtils
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

begin_comment
comment|/**  * A JUnit test for checking if restarting DFS preserves the  * blocks that are part of an unclosed file.  */
end_comment

begin_class
DECL|class|TestPersistBlocks
specifier|public
class|class
name|TestPersistBlocks
block|{
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSImage
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSNamesystem
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
block|}
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|NUM_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|5
decl_stmt|;
DECL|field|FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"/data"
decl_stmt|;
DECL|field|FILE_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|FILE_PATH
init|=
operator|new
name|Path
argument_list|(
name|FILE_NAME
argument_list|)
decl_stmt|;
DECL|field|DATA_BEFORE_RESTART
specifier|static
specifier|final
name|byte
index|[]
name|DATA_BEFORE_RESTART
init|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
index|]
decl_stmt|;
DECL|field|DATA_AFTER_RESTART
specifier|static
specifier|final
name|byte
index|[]
name|DATA_AFTER_RESTART
init|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
index|]
decl_stmt|;
DECL|field|HADOOP_1_0_MULTIBLOCK_TGZ
specifier|private
specifier|static
specifier|final
name|String
name|HADOOP_1_0_MULTIBLOCK_TGZ
init|=
literal|"hadoop-1.0-multiblock-file.tgz"
decl_stmt|;
static|static
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|)
expr_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|DATA_AFTER_RESTART
argument_list|)
expr_stmt|;
block|}
comment|/** check if DFS remains in proper condition after a restart     **/
annotation|@
name|Test
DECL|method|TestRestartDfsWithFlush ()
specifier|public
name|void
name|TestRestartDfsWithFlush
parameter_list|()
throws|throws
name|Exception
block|{
name|testRestartDfs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** check if DFS remains in proper condition after a restart     **/
DECL|method|TestRestartDfsWithSync ()
specifier|public
name|void
name|TestRestartDfsWithSync
parameter_list|()
throws|throws
name|Exception
block|{
name|testRestartDfs
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** check if DFS remains in proper condition after a restart    * @param useFlush - if true then flush is used instead of sync (ie hflush)    */
DECL|method|testRestartDfs (boolean useFlush)
name|void
name|testRestartDfs
parameter_list|(
name|boolean
name|useFlush
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Turn off persistent IPC, so that the DFSClient can survive NN restart
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|long
name|len
init|=
literal|0
decl_stmt|;
name|FSDataOutputStream
name|stream
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Creating a file with 4096 blockSize to write multiple blocks
name|stream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|FILE_PATH
argument_list|,
literal|true
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|)
expr_stmt|;
if|if
condition|(
name|useFlush
condition|)
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
else|else
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// Wait for at least a few blocks to get through
while|while
condition|(
name|len
operator|<=
name|BLOCK_SIZE
condition|)
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
name|len
operator|=
name|status
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// explicitly do NOT close the file.
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
comment|// Check that the file has no less bytes than before the restart
comment|// This would mean that blocks were successfully persisted to the log
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Length too short: "
operator|+
name|status
operator|.
name|getLen
argument_list|()
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
operator|>=
name|len
argument_list|)
expr_stmt|;
comment|// And keep writing (ensures that leases are also persisted correctly)
name|stream
operator|.
name|write
argument_list|(
name|DATA_AFTER_RESTART
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Verify that the data showed up, both from before and after the restart.
name|FSDataInputStream
name|readStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|verifyBuf
init|=
operator|new
name|byte
index|[
name|DATA_BEFORE_RESTART
operator|.
name|length
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|readStream
argument_list|,
name|verifyBuf
argument_list|,
literal|0
argument_list|,
name|verifyBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|,
name|verifyBuf
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|readStream
argument_list|,
name|verifyBuf
argument_list|,
literal|0
argument_list|,
name|verifyBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|DATA_AFTER_RESTART
argument_list|,
name|verifyBuf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|readStream
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
DECL|method|testRestartDfsWithAbandonedBlock ()
specifier|public
name|void
name|testRestartDfsWithAbandonedBlock
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Turn off persistent IPC, so that the DFSClient can survive NN restart
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|long
name|len
init|=
literal|0
decl_stmt|;
name|FSDataOutputStream
name|stream
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Creating a file with 4096 blockSize to write multiple blocks
name|stream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|FILE_PATH
argument_list|,
literal|true
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|)
expr_stmt|;
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// Wait for all of the blocks to get through
while|while
condition|(
name|len
operator|<
name|BLOCK_SIZE
operator|*
operator|(
name|NUM_BLOCKS
operator|-
literal|1
operator|)
condition|)
block|{
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
name|len
operator|=
name|status
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// Abandon the last block
name|DFSClient
name|dfsclient
init|=
name|DFSClientAdapter
operator|.
name|getDFSClient
argument_list|(
operator|(
name|DistributedFileSystem
operator|)
name|fs
argument_list|)
decl_stmt|;
name|HdfsFileStatus
name|fileStatus
init|=
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|FILE_NAME
argument_list|)
decl_stmt|;
name|LocatedBlocks
name|blocks
init|=
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|FILE_NAME
argument_list|,
literal|0
argument_list|,
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_BLOCKS
argument_list|,
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LocatedBlock
name|b
init|=
name|blocks
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|dfsclient
operator|.
name|getNamenode
argument_list|()
operator|.
name|abandonBlock
argument_list|(
name|b
operator|.
name|getBlock
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getFileId
argument_list|()
argument_list|,
name|FILE_NAME
argument_list|,
name|dfsclient
operator|.
name|clientName
argument_list|)
expr_stmt|;
comment|// explicitly do NOT close the file.
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
comment|// Check that the file has no less bytes than before the restart
comment|// This would mean that blocks were successfully persisted to the log
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Length incorrect: "
operator|+
name|status
operator|.
name|getLen
argument_list|()
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
operator|==
name|len
operator|-
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
comment|// Verify the data showed up from before restart, sans abandoned block.
name|FSDataInputStream
name|readStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|verifyBuf
init|=
operator|new
name|byte
index|[
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|-
name|BLOCK_SIZE
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|readStream
argument_list|,
name|verifyBuf
argument_list|,
literal|0
argument_list|,
name|verifyBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|byte
index|[]
name|expectedBuf
init|=
operator|new
name|byte
index|[
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|-
name|BLOCK_SIZE
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|,
literal|0
argument_list|,
name|expectedBuf
argument_list|,
literal|0
argument_list|,
name|expectedBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedBuf
argument_list|,
name|verifyBuf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|readStream
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
DECL|method|testRestartWithPartialBlockHflushed ()
specifier|public
name|void
name|testRestartWithPartialBlockHflushed
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Turn off persistent IPC, so that the DFSClient can survive NN restart
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FSDataOutputStream
name|stream
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSUtilClient
operator|.
name|getNNAddress
argument_list|(
name|conf
argument_list|)
operator|.
name|getPort
argument_list|()
expr_stmt|;
comment|// Creating a file with 4096 blockSize to write multiple blocks
name|stream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|FILE_PATH
argument_list|,
literal|true
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// explicitly do NOT close the file before restarting the NN.
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
comment|// this will fail if the final block of the file is prematurely COMPLETEd
name|stream
operator|.
name|write
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
name|stream
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|+
literal|2
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|FILE_PATH
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|readStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|verifyBuf
init|=
operator|new
name|byte
index|[
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|+
literal|2
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|readStream
argument_list|,
name|verifyBuf
argument_list|,
literal|0
argument_list|,
name|verifyBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|byte
index|[]
name|expectedBuf
init|=
operator|new
name|byte
index|[
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|+
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|,
literal|0
argument_list|,
name|expectedBuf
argument_list|,
literal|0
argument_list|,
name|DATA_BEFORE_RESTART
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|,
literal|0
argument_list|,
name|expectedBuf
argument_list|,
name|DATA_BEFORE_RESTART
operator|.
name|length
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedBuf
argument_list|,
name|verifyBuf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|readStream
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
DECL|method|testRestartWithAppend ()
specifier|public
name|void
name|testRestartWithAppend
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Turn off persistent IPC, so that the DFSClient can survive NN restart
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FSDataOutputStream
name|stream
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSUtilClient
operator|.
name|getNNAddress
argument_list|(
name|conf
argument_list|)
operator|.
name|getPort
argument_list|()
expr_stmt|;
comment|// Creating a file with 4096 blockSize to write multiple blocks
name|stream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|FILE_PATH
argument_list|,
literal|true
argument_list|,
name|BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|,
literal|0
argument_list|,
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|/
literal|2
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|stream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|FILE_PATH
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|,
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|/
literal|2
argument_list|,
name|DATA_BEFORE_RESTART
operator|.
name|length
operator|/
literal|2
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|DATA_BEFORE_RESTART
operator|.
name|length
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|FILE_PATH
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|DATA_BEFORE_RESTART
operator|.
name|length
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|FILE_PATH
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|readStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|FILE_PATH
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|verifyBuf
init|=
operator|new
name|byte
index|[
name|DATA_BEFORE_RESTART
operator|.
name|length
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|readStream
argument_list|,
name|verifyBuf
argument_list|,
literal|0
argument_list|,
name|verifyBuf
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|DATA_BEFORE_RESTART
argument_list|,
name|verifyBuf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|readStream
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Earlier versions of HDFS didn't persist block allocation to the edit log.    * This makes sure that we can still load an edit log when the OP_CLOSE    * is the opcode which adds all of the blocks. This is a regression    * test for HDFS-2773.    * This test uses a tarred pseudo-distributed cluster from Hadoop 1.0    * which has a multi-block file. This is similar to the tests in    * {@link TestDFSUpgradeFromImage} but none of those images include    * a multi-block file.    */
annotation|@
name|Test
DECL|method|testEarlierVersionEditLog ()
specifier|public
name|void
name|testEarlierVersionEditLog
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|String
name|tarFile
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
literal|"build/test/cache"
argument_list|)
operator|+
literal|"/"
operator|+
name|HADOOP_1_0_MULTIBLOCK_TGZ
decl_stmt|;
name|String
name|testDir
init|=
name|PathUtils
operator|.
name|getTestDirName
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|dfsDir
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"image-1.0"
argument_list|)
decl_stmt|;
if|if
condition|(
name|dfsDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dfsDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete dfs directory '"
operator|+
name|dfsDir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|FileUtil
operator|.
name|unTar
argument_list|(
operator|new
name|File
argument_list|(
name|tarFile
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|testDir
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|nameDir
init|=
operator|new
name|File
argument_list|(
name|dfsDir
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|nameDir
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|dfsDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|assertExists
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|nameDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
literal|0
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|manageDataDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|startupOption
argument_list|(
name|StartupOption
operator|.
name|UPGRADE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
literal|"/user/todd/4blocks"
argument_list|)
decl_stmt|;
comment|// Read it without caring about the actual data within - we just need
comment|// to make sure that the block states and locations are OK.
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
comment|// Ensure that we can append to it - if the blocks were in some funny
comment|// state we'd get some kind of issue here.
name|FSDataOutputStream
name|stm
init|=
name|fs
operator|.
name|append
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
try|try
block|{
name|stm
operator|.
name|write
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|stm
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

