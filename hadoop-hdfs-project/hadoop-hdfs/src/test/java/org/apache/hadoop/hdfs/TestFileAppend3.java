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
name|io
operator|.
name|RandomAccessFile
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
name|CommonConfigurationKeys
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
name|DatanodeInfo
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
name|LeaseManager
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
name|server
operator|.
name|protocol
operator|.
name|InterDatanodeProtocol
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
name|AfterClass
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

begin_comment
comment|/** This class implements some of tests posted in HADOOP-2658. */
end_comment

begin_class
DECL|class|TestFileAppend3
specifier|public
class|class
name|TestFileAppend3
block|{
block|{
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
name|LeaseManager
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
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
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
name|DataNode
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
name|DFSClient
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
name|InterDatanodeProtocol
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
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|REPLICATION
specifier|static
specifier|final
name|short
name|REPLICATION
init|=
literal|3
decl_stmt|;
DECL|field|DATANODE_NUM
specifier|static
specifier|final
name|int
name|DATANODE_NUM
init|=
literal|5
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|buffersize
specifier|private
specifier|static
name|int
name|buffersize
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|DistributedFileSystem
name|fs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
block|{
name|AppendTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"setUp()"
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|buffersize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
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
name|DATANODE_NUM
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|AppendTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"tearDown()"
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * TC1: Append on block boundary.    * @throws IOException an exception might be thrown    */
annotation|@
name|Test
DECL|method|testTC1 ()
specifier|public
name|void
name|testTC1
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/TC1/foo"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p="
operator|+
name|p
argument_list|)
expr_stmt|;
comment|//a. Create file and write one block of data. Close file.
specifier|final
name|int
name|len1
init|=
operator|(
name|int
operator|)
name|BLOCK_SIZE
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
name|buffersize
argument_list|,
name|REPLICATION
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|len1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//   Reopen file to append. Append half block of data. Close file.
specifier|final
name|int
name|len2
init|=
operator|(
name|int
operator|)
name|BLOCK_SIZE
operator|/
literal|2
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|len1
argument_list|,
name|len2
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//b. Reopen file and read 1.5 blocks worth of data. Close file.
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|len1
operator|+
name|len2
argument_list|)
expr_stmt|;
block|}
comment|/**    * TC2: Append on non-block boundary.    * @throws IOException an exception might be thrown    */
annotation|@
name|Test
DECL|method|testTC2 ()
specifier|public
name|void
name|testTC2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/TC2/foo"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p="
operator|+
name|p
argument_list|)
expr_stmt|;
comment|//a. Create file with one and a half block of data. Close file.
specifier|final
name|int
name|len1
init|=
call|(
name|int
call|)
argument_list|(
name|BLOCK_SIZE
operator|+
name|BLOCK_SIZE
operator|/
literal|2
argument_list|)
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
name|buffersize
argument_list|,
name|REPLICATION
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|len1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|len1
argument_list|)
expr_stmt|;
comment|//   Reopen file to append quarter block of data. Close file.
specifier|final
name|int
name|len2
init|=
operator|(
name|int
operator|)
name|BLOCK_SIZE
operator|/
literal|4
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|len1
argument_list|,
name|len2
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//b. Reopen file and read 1.75 blocks of data. Close file.
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|len1
operator|+
name|len2
argument_list|)
expr_stmt|;
block|}
comment|/**    * TC5: Only one simultaneous append.    * @throws IOException an exception might be thrown    */
annotation|@
name|Test
DECL|method|testTC5 ()
specifier|public
name|void
name|testTC5
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/TC5/foo"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p="
operator|+
name|p
argument_list|)
expr_stmt|;
comment|//a. Create file on Machine M1. Write half block to it. Close file.
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
name|buffersize
argument_list|,
name|REPLICATION
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
call|(
name|int
call|)
argument_list|(
name|BLOCK_SIZE
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//b. Reopen file in "append" mode on Machine M1.
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
comment|//c. On Machine M2, reopen file in "append" mode. This should fail.
try|try
block|{
name|AppendTestUtil
operator|.
name|createHdfsWithDifferentUsername
argument_list|(
name|conf
argument_list|)
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|AppendTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"GOOD: got an exception"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
comment|//d. On Machine M1, close file.
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * TC7: Corrupted replicas are present.    * @throws IOException an exception might be thrown    */
annotation|@
name|Test
DECL|method|testTC7 ()
specifier|public
name|void
name|testTC7
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|repl
init|=
literal|2
decl_stmt|;
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/TC7/foo"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p="
operator|+
name|p
argument_list|)
expr_stmt|;
comment|//a. Create file with replication factor of 2. Write half block of data. Close file.
specifier|final
name|int
name|len1
init|=
call|(
name|int
call|)
argument_list|(
name|BLOCK_SIZE
operator|/
literal|2
argument_list|)
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
name|buffersize
argument_list|,
name|repl
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|len1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|repl
argument_list|)
expr_stmt|;
comment|//b. Log into one datanode that has one replica of this block.
comment|//   Find the block file on this datanode and truncate it to zero size.
specifier|final
name|LocatedBlocks
name|locatedblocks
init|=
name|fs
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
literal|0L
argument_list|,
name|len1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|locatedblocks
operator|.
name|locatedBlockCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|LocatedBlock
name|lb
init|=
name|locatedblocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|ExtendedBlock
name|blk
init|=
name|lb
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|len1
argument_list|,
name|lb
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeInfo
index|[]
name|datanodeinfos
init|=
name|lb
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|repl
argument_list|,
name|datanodeinfos
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|datanodeinfos
index|[
literal|0
index|]
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|File
name|f
init|=
name|DataNodeTestUtils
operator|.
name|getBlockFile
argument_list|(
name|dn
argument_list|,
name|blk
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blk
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|f
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"dn="
operator|+
name|dn
operator|+
literal|", blk="
operator|+
name|blk
operator|+
literal|" (length="
operator|+
name|blk
operator|.
name|getNumBytes
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|len1
argument_list|,
name|raf
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|raf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//c. Open file in "append mode".  Append a new block worth of data. Close file.
specifier|final
name|int
name|len2
init|=
operator|(
name|int
operator|)
name|BLOCK_SIZE
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|len1
argument_list|,
name|len2
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//d. Reopen file and read two blocks worth of data.
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|len1
operator|+
name|len2
argument_list|)
expr_stmt|;
block|}
comment|/**    * TC11: Racing rename    * @throws IOException an exception might be thrown    */
annotation|@
name|Test
DECL|method|testTC11 ()
specifier|public
name|void
name|testTC11
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/TC11/foo"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p="
operator|+
name|p
argument_list|)
expr_stmt|;
comment|//a. Create file and write one block of data. Close file.
specifier|final
name|int
name|len1
init|=
operator|(
name|int
operator|)
name|BLOCK_SIZE
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
name|buffersize
argument_list|,
name|REPLICATION
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|len1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//b. Reopen file in "append" mode. Append half block of data.
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len2
init|=
operator|(
name|int
operator|)
name|BLOCK_SIZE
operator|/
literal|2
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|len1
argument_list|,
name|len2
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|//c. Rename file to file.new.
specifier|final
name|Path
name|pnew
init|=
operator|new
name|Path
argument_list|(
name|p
operator|+
literal|".new"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|rename
argument_list|(
name|p
argument_list|,
name|pnew
argument_list|)
argument_list|)
expr_stmt|;
comment|//d. Close file handle that was opened in (b).
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"close() should throw an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|AppendTestUtil
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"GOOD!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//wait for the lease recovery
name|cluster
operator|.
name|setLeasePeriod
argument_list|(
literal|1000
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|AppendTestUtil
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|//check block sizes
specifier|final
name|long
name|len
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|pnew
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
specifier|final
name|LocatedBlocks
name|locatedblocks
init|=
name|fs
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|pnew
operator|.
name|toString
argument_list|()
argument_list|,
literal|0L
argument_list|,
name|len
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numblock
init|=
name|locatedblocks
operator|.
name|locatedBlockCount
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
name|numblock
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|LocatedBlock
name|lb
init|=
name|locatedblocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|ExtendedBlock
name|blk
init|=
name|lb
operator|.
name|getBlock
argument_list|()
decl_stmt|;
specifier|final
name|long
name|size
init|=
name|lb
operator|.
name|getBlockSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|numblock
operator|-
literal|1
condition|)
block|{
name|assertEquals
argument_list|(
name|BLOCK_SIZE
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DatanodeInfo
name|datanodeinfo
range|:
name|lb
operator|.
name|getLocations
argument_list|()
control|)
block|{
specifier|final
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|datanodeinfo
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Block
name|metainfo
init|=
name|DataNodeTestUtils
operator|.
name|getFSDataset
argument_list|(
name|dn
argument_list|)
operator|.
name|getStoredBlock
argument_list|(
name|blk
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blk
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|metainfo
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**     * TC12: Append to partial CRC chunk    * @throws IOException an exception might be thrown    */
annotation|@
name|Test
DECL|method|testTC12 ()
specifier|public
name|void
name|testTC12
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/TC12/foo"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p="
operator|+
name|p
argument_list|)
expr_stmt|;
comment|//a. Create file with a block size of 64KB
comment|//   and a default io.bytes.per.checksum of 512 bytes.
comment|//   Write 25687 bytes of data. Close file.
specifier|final
name|int
name|len1
init|=
literal|25687
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
name|buffersize
argument_list|,
name|REPLICATION
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|len1
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//b. Reopen file in "append" mode. Append another 5877 bytes of data. Close file.
specifier|final
name|int
name|len2
init|=
literal|5877
decl_stmt|;
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|AppendTestUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|len1
argument_list|,
name|len2
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//c. Reopen file and read 25687+5877 bytes of data from file. Close file.
name|AppendTestUtil
operator|.
name|check
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|len1
operator|+
name|len2
argument_list|)
expr_stmt|;
block|}
comment|/** Append to a partial CRC chunk and     * the first write does not fill up the partial CRC trunk    * *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testAppendToPartialChunk ()
specifier|public
name|void
name|testAppendToPartialChunk
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/partialChunk/foo"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|fileLen
init|=
literal|513
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p="
operator|+
name|p
argument_list|)
expr_stmt|;
name|byte
index|[]
name|fileContents
init|=
name|AppendTestUtil
operator|.
name|initBuffer
argument_list|(
name|fileLen
argument_list|)
decl_stmt|;
comment|// create a new file.
name|FSDataOutputStream
name|stm
init|=
name|AppendTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// create 1 byte file
name|stm
operator|.
name|write
argument_list|(
name|fileContents
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wrote 1 byte and closed the file "
operator|+
name|p
argument_list|)
expr_stmt|;
comment|// append to file
name|stm
operator|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// Append to a partial CRC trunk
name|stm
operator|.
name|write
argument_list|(
name|fileContents
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|stm
operator|.
name|hflush
argument_list|()
expr_stmt|;
comment|// The partial CRC trunk is not full yet and close the file
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Append 1 byte and closed the file "
operator|+
name|p
argument_list|)
expr_stmt|;
comment|// write the remainder of the file
name|stm
operator|=
name|fs
operator|.
name|append
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// ensure getPos is set to reflect existing size of the file
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stm
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|// append to a partial CRC trunk
name|stm
operator|.
name|write
argument_list|(
name|fileContents
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// The partial chunk is not full yet, force to send a packet to DN
name|stm
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Append and flush 1 byte"
argument_list|)
expr_stmt|;
comment|// The partial chunk is not full yet, force to send another packet to DN
name|stm
operator|.
name|write
argument_list|(
name|fileContents
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|stm
operator|.
name|hflush
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Append and flush 2 byte"
argument_list|)
expr_stmt|;
comment|// fill up the partial chunk and close the file
name|stm
operator|.
name|write
argument_list|(
name|fileContents
argument_list|,
literal|5
argument_list|,
name|fileLen
operator|-
literal|5
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Flush 508 byte and closed the file "
operator|+
name|p
argument_list|)
expr_stmt|;
comment|// verify that entire file is good
name|AppendTestUtil
operator|.
name|checkFullFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|fileLen
argument_list|,
name|fileContents
argument_list|,
literal|"Failed to append to a partial chunk"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

