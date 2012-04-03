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
name|BlockLocation
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
name|ChecksumException
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
name|util
operator|.
name|StringUtils
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * This class tests the cases of a concurrent reads/writes to a file;  * ie, one writer and one or more readers can see unfinsihed blocks  */
end_comment

begin_class
DECL|class|TestFileConcurrentReader
specifier|public
class|class
name|TestFileConcurrentReader
extends|extends
name|junit
operator|.
name|framework
operator|.
name|TestCase
block|{
DECL|enum|SyncType
specifier|private
enum|enum
name|SyncType
block|{
DECL|enumConstant|SYNC
name|SYNC
block|,
DECL|enumConstant|APPEND
name|APPEND
block|,   }
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TestFileConcurrentReader
operator|.
name|class
argument_list|)
decl_stmt|;
block|{
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
block|}
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|8192
decl_stmt|;
DECL|field|DEFAULT_WRITE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_WRITE_SIZE
init|=
literal|1024
operator|+
literal|1
decl_stmt|;
DECL|field|SMALL_WRITE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SMALL_WRITE_SIZE
init|=
literal|61
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fileSystem
specifier|private
name|FileSystem
name|fileSystem
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|init (Configuration conf)
specifier|private
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
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
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|fileSystem
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
DECL|method|writeFileAndSync (FSDataOutputStream stm, int size)
specifier|private
name|void
name|writeFileAndSync
parameter_list|(
name|FSDataOutputStream
name|stm
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
name|DFSTestUtil
operator|.
name|generateSequentialBytes
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|stm
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
DECL|method|checkCanRead (FileSystem fileSys, Path path, int numBytes)
specifier|private
name|void
name|checkCanRead
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|waitForBlocks
argument_list|(
name|fileSys
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|assertBytesAvailable
argument_list|(
name|fileSys
argument_list|,
name|path
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
comment|// make sure bytes are available and match expected
DECL|method|assertBytesAvailable ( FileSystem fileSystem, Path path, int numBytes )
specifier|private
name|void
name|assertBytesAvailable
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|,
name|Path
name|path
parameter_list|,
name|int
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|fileSystem
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|inputStream
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"unable to validate bytes"
argument_list|,
name|validateSequentialBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForBlocks (FileSystem fileSys, Path name)
specifier|private
name|void
name|waitForBlocks
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// wait until we have at least one block in the file to read.
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
name|done
operator|=
literal|true
expr_stmt|;
name|BlockLocation
index|[]
name|locations
init|=
name|fileSys
operator|.
name|getFileBlockLocations
argument_list|(
name|fileSys
operator|.
name|getFileStatus
argument_list|(
name|name
argument_list|)
argument_list|,
literal|0
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|locations
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|done
operator|=
literal|false
expr_stmt|;
continue|continue;
block|}
block|}
block|}
comment|/**    * Test that that writes to an incomplete block are available to a reader    */
DECL|method|testUnfinishedBlockRead ()
specifier|public
name|void
name|testUnfinishedBlockRead
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create a new file in the root, write data, do no close
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/unfinished-block"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm
init|=
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|fileSystem
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// write partial block and sync
name|int
name|partialBlockSize
init|=
name|blockSize
operator|/
literal|2
decl_stmt|;
name|writeFileAndSync
argument_list|(
name|stm
argument_list|,
name|partialBlockSize
argument_list|)
expr_stmt|;
comment|// Make sure a client can read it before it is closed
name|checkCanRead
argument_list|(
name|fileSystem
argument_list|,
name|file1
argument_list|,
name|partialBlockSize
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * test case: if the BlockSender decides there is only one packet to send,    * the previous computation of the pktSize based on transferToAllowed    * would result in too small a buffer to do the buffer-copy needed    * for partial chunks.    */
DECL|method|testUnfinishedBlockPacketBufferOverrun ()
specifier|public
name|void
name|testUnfinishedBlockPacketBufferOverrun
parameter_list|()
throws|throws
name|IOException
block|{
comment|// check that / exists
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Path : \""
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
comment|// create a new file in the root, write data, do no close
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/unfinished-block"
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|stm
init|=
name|TestFileCreation
operator|.
name|createFile
argument_list|(
name|fileSystem
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// write partial block and sync
specifier|final
name|int
name|bytesPerChecksum
init|=
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.bytes.per.checksum"
argument_list|,
literal|512
argument_list|)
decl_stmt|;
specifier|final
name|int
name|partialBlockSize
init|=
name|bytesPerChecksum
operator|-
literal|1
decl_stmt|;
name|writeFileAndSync
argument_list|(
name|stm
argument_list|,
name|partialBlockSize
argument_list|)
expr_stmt|;
comment|// Make sure a client can read it before it is closed
name|checkCanRead
argument_list|(
name|fileSystem
argument_list|,
name|file1
argument_list|,
name|partialBlockSize
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// use a small block size and a large write so that DN is busy creating
comment|// new blocks.  This makes it almost 100% sure we can reproduce
comment|// case of client getting a DN that hasn't yet created the blocks
DECL|method|testImmediateReadOfNewFile ()
specifier|public
name|void
name|testImmediateReadOfNewFile
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|blockSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|writeSize
init|=
literal|10
operator|*
name|blockSize
decl_stmt|;
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
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|int
name|requiredSuccessfulOpens
init|=
literal|100
decl_stmt|;
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/file1"
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|openerDone
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|errorMessage
init|=
operator|new
name|AtomicReference
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|fileSystem
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|writer
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
try|try
block|{
while|while
condition|(
operator|!
name|openerDone
operator|.
name|get
argument_list|()
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|DFSTestUtil
operator|.
name|generateSequentialBytes
argument_list|(
literal|0
argument_list|,
name|writeSize
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error in writer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"unable to close file"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|Thread
name|opener
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
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|requiredSuccessfulOpens
condition|;
name|i
operator|++
control|)
block|{
name|fileSystem
operator|.
name|open
argument_list|(
name|file
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|openerDone
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|openerDone
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|errorMessage
operator|.
name|set
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"got exception : %s"
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|openerDone
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|errorMessage
operator|.
name|set
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"got exception : %s"
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"here"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|writer
operator|.
name|start
argument_list|()
expr_stmt|;
name|opener
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|join
argument_list|()
expr_stmt|;
name|opener
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|errorMessage
operator|.
name|get
argument_list|()
argument_list|,
name|errorMessage
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// for some reason, using tranferTo evokes the race condition more often
comment|// so test separately
DECL|method|testUnfinishedBlockCRCErrorTransferTo ()
specifier|public
name|void
name|testUnfinishedBlockCRCErrorTransferTo
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestUnfinishedBlockCRCError
argument_list|(
literal|true
argument_list|,
name|SyncType
operator|.
name|SYNC
argument_list|,
name|DEFAULT_WRITE_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnfinishedBlockCRCErrorTransferToVerySmallWrite ()
specifier|public
name|void
name|testUnfinishedBlockCRCErrorTransferToVerySmallWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestUnfinishedBlockCRCError
argument_list|(
literal|true
argument_list|,
name|SyncType
operator|.
name|SYNC
argument_list|,
name|SMALL_WRITE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|// fails due to issue w/append, disable
DECL|method|_testUnfinishedBlockCRCErrorTransferToAppend ()
specifier|public
name|void
name|_testUnfinishedBlockCRCErrorTransferToAppend
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestUnfinishedBlockCRCError
argument_list|(
literal|true
argument_list|,
name|SyncType
operator|.
name|APPEND
argument_list|,
name|DEFAULT_WRITE_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnfinishedBlockCRCErrorNormalTransfer ()
specifier|public
name|void
name|testUnfinishedBlockCRCErrorNormalTransfer
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestUnfinishedBlockCRCError
argument_list|(
literal|false
argument_list|,
name|SyncType
operator|.
name|SYNC
argument_list|,
name|DEFAULT_WRITE_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnfinishedBlockCRCErrorNormalTransferVerySmallWrite ()
specifier|public
name|void
name|testUnfinishedBlockCRCErrorNormalTransferVerySmallWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestUnfinishedBlockCRCError
argument_list|(
literal|false
argument_list|,
name|SyncType
operator|.
name|SYNC
argument_list|,
name|SMALL_WRITE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|// fails due to issue w/append, disable
DECL|method|_testUnfinishedBlockCRCErrorNormalTransferAppend ()
specifier|public
name|void
name|_testUnfinishedBlockCRCErrorNormalTransferAppend
parameter_list|()
throws|throws
name|IOException
block|{
name|runTestUnfinishedBlockCRCError
argument_list|(
literal|false
argument_list|,
name|SyncType
operator|.
name|APPEND
argument_list|,
name|DEFAULT_WRITE_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestUnfinishedBlockCRCError ( final boolean transferToAllowed, SyncType syncType, int writeSize )
specifier|private
name|void
name|runTestUnfinishedBlockCRCError
parameter_list|(
specifier|final
name|boolean
name|transferToAllowed
parameter_list|,
name|SyncType
name|syncType
parameter_list|,
name|int
name|writeSize
parameter_list|)
throws|throws
name|IOException
block|{
name|runTestUnfinishedBlockCRCError
argument_list|(
name|transferToAllowed
argument_list|,
name|syncType
argument_list|,
name|writeSize
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestUnfinishedBlockCRCError ( final boolean transferToAllowed, final SyncType syncType, final int writeSize, Configuration conf )
specifier|private
name|void
name|runTestUnfinishedBlockCRCError
parameter_list|(
specifier|final
name|boolean
name|transferToAllowed
parameter_list|,
specifier|final
name|SyncType
name|syncType
parameter_list|,
specifier|final
name|int
name|writeSize
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_TRANSFERTO_ALLOWED_KEY
argument_list|,
name|transferToAllowed
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/block-being-written-to"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numWrites
init|=
literal|2000
decl_stmt|;
specifier|final
name|AtomicBoolean
name|writerDone
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|writerStarted
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|error
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|initialOutputStream
init|=
name|fileSystem
operator|.
name|create
argument_list|(
name|file
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|writer
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|private
name|FSDataOutputStream
name|outputStream
init|=
name|initialOutputStream
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|error
operator|.
name|get
argument_list|()
operator|&&
name|i
operator|<
name|numWrites
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
specifier|final
name|byte
index|[]
name|writeBuf
init|=
name|DFSTestUtil
operator|.
name|generateSequentialBytes
argument_list|(
name|i
operator|*
name|writeSize
argument_list|,
name|writeSize
argument_list|)
decl_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|writeBuf
argument_list|)
expr_stmt|;
if|if
condition|(
name|syncType
operator|==
name|SyncType
operator|.
name|SYNC
condition|)
block|{
name|outputStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// append
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|outputStream
operator|=
name|fileSystem
operator|.
name|append
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|writerStarted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|error
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"error writing to file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|writerDone
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"error in writer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|Thread
name|tailer
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
try|try
block|{
name|long
name|startPos
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|writerDone
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|error
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|writerStarted
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|startPos
operator|=
name|tailFile
argument_list|(
name|file
argument_list|,
name|startPos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"error tailing file %s"
argument_list|,
name|file
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ChecksumException
condition|)
block|{
name|error
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"error in tailer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|writer
operator|.
name|start
argument_list|()
expr_stmt|;
name|tailer
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|join
argument_list|()
expr_stmt|;
name|tailer
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"error occurred, see log above"
argument_list|,
name|error
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"interrupted waiting for writer or tailer to complete"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|initialOutputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|validateSequentialBytes (byte[] buf, int startPos, int len)
specifier|private
name|boolean
name|validateSequentialBytes
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|startPos
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|expected
init|=
operator|(
name|i
operator|+
name|startPos
operator|)
operator|%
literal|127
decl_stmt|;
if|if
condition|(
name|buf
index|[
name|i
index|]
operator|%
literal|127
operator|!=
name|expected
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"at position [%d], got [%d] and expected [%d]"
argument_list|,
name|startPos
argument_list|,
name|buf
index|[
name|i
index|]
argument_list|,
name|expected
argument_list|)
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
DECL|method|tailFile (Path file, long startPos)
specifier|private
name|long
name|tailFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|long
name|startPos
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|numRead
init|=
literal|0
decl_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|fileSystem
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|inputStream
operator|.
name|seek
argument_list|(
name|startPos
argument_list|)
expr_stmt|;
name|int
name|len
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|int
name|read
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"read %d bytes"
argument_list|,
name|read
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|validateSequentialBytes
argument_list|(
name|buf
argument_list|,
call|(
name|int
call|)
argument_list|(
name|startPos
operator|+
name|numRead
argument_list|)
argument_list|,
name|read
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"invalid bytes: [%s]\n"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|buf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ChecksumException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"unable to validate bytes"
argument_list|)
argument_list|,
name|startPos
argument_list|)
throw|;
block|}
name|numRead
operator|+=
name|read
expr_stmt|;
block|}
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|numRead
operator|+
name|startPos
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

