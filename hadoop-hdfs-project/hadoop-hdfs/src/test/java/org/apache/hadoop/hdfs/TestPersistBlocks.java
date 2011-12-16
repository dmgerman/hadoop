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
name|log4j
operator|.
name|Level
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
name|org
operator|.
name|junit
operator|.
name|Test
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
operator|(
operator|(
name|Log4JLogger
operator|)
name|FSImage
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
operator|(
operator|(
name|Log4JLogger
operator|)
name|FSNamesystem
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
comment|/** check if DFS remains in proper condition after a restart */
annotation|@
name|Test
DECL|method|testRestartDfs ()
specifier|public
name|void
name|testRestartDfs
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERSIST_BLOCKS_KEY
argument_list|,
literal|true
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERSIST_BLOCKS_KEY
argument_list|,
literal|true
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
operator|!=
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERSIST_BLOCKS_KEY
argument_list|,
literal|true
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
name|NameNode
operator|.
name|getAddress
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PERSIST_BLOCKS_KEY
argument_list|,
literal|true
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
name|NameNode
operator|.
name|getAddress
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
block|}
end_class

end_unit

