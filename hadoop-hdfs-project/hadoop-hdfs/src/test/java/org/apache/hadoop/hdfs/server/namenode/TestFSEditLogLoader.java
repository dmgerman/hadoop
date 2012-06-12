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
name|BufferedInputStream
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
name|SortedMap
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
name|HdfsConstants
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
name|Storage
operator|.
name|StorageDirectory
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
name|FSEditLogLoader
operator|.
name|EditLogValidation
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
name|FSEditLogOp
operator|.
name|DeleteOp
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
name|FSEditLogOp
operator|.
name|OpInstanceCache
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
name|NNStorage
operator|.
name|NameNodeDirType
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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|collect
operator|.
name|Maps
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
name|collect
operator|.
name|Sets
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
name|io
operator|.
name|Files
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doNothing
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

begin_class
DECL|class|TestFSEditLogLoader
specifier|public
class|class
name|TestFSEditLogLoader
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
name|FSEditLogLoader
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
DECL|field|TEST_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|NUM_DATA_NODES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DATA_NODES
init|=
literal|0
decl_stmt|;
annotation|@
name|Test
DECL|method|testDisplayRecentEditLogOpCodes ()
specifier|public
name|void
name|testDisplayRecentEditLogOpCodes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// start a cluster
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
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
name|NUM_DATA_NODES
argument_list|)
operator|.
name|enableManagedDfsDirsRedundancy
argument_list|(
literal|false
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
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|FSImage
name|fsimage
init|=
name|namesystem
operator|.
name|getFSImage
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|fileSys
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp/tmp"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|StorageDirectory
name|sd
init|=
name|fsimage
operator|.
name|getStorage
argument_list|()
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|File
name|editFile
init|=
name|FSImageTestUtil
operator|.
name|findLatestEditsLog
argument_list|(
name|sd
argument_list|)
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should exist: "
operator|+
name|editFile
argument_list|,
name|editFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Corrupt the edits file.
name|long
name|fileLen
init|=
name|editFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|RandomAccessFile
name|rwf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|editFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|rwf
operator|.
name|seek
argument_list|(
name|fileLen
operator|-
literal|40
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
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|rwf
operator|.
name|write
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_DELETE
operator|.
name|getOpCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rwf
operator|.
name|close
argument_list|()
expr_stmt|;
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|bld
operator|.
name|append
argument_list|(
literal|"^Error replaying edit log at offset \\d+.  "
argument_list|)
expr_stmt|;
name|bld
operator|.
name|append
argument_list|(
literal|"Expected transaction ID was \\d+\n"
argument_list|)
expr_stmt|;
name|bld
operator|.
name|append
argument_list|(
literal|"Recent opcode offsets: (\\d+\\s*){4}$"
argument_list|)
expr_stmt|;
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
name|NUM_DATA_NODES
argument_list|)
operator|.
name|enableManagedDfsDirsRedundancy
argument_list|(
literal|false
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should not be able to start"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"error message contains opcodes message"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|matches
argument_list|(
name|bld
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that, if the NN restarts with a new minimum replication,    * any files created with the old replication count will get    * automatically bumped up to the new minimum upon restart.    */
annotation|@
name|Test
DECL|method|testReplicationAdjusted ()
specifier|public
name|void
name|testReplicationAdjusted
parameter_list|()
throws|throws
name|IOException
block|{
comment|// start a cluster
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// Replicate and heartbeat fast to shave a few seconds off test
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
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
literal|2
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create a file with replication count 1
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/testfile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
literal|10
argument_list|,
comment|/*repl*/
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// Shut down and restart cluster with new minimum replication of 2
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MIN_KEY
argument_list|,
literal|2
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
literal|2
argument_list|)
operator|.
name|format
argument_list|(
literal|false
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
comment|// The file should get adjusted to replication 2 when
comment|// the edit log is replayed.
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
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
comment|/**    * Corrupt the byte at the given offset in the given file,    * by subtracting 1 from it.    */
DECL|method|corruptByteInFile (File file, long offset)
specifier|private
name|void
name|corruptByteInFile
parameter_list|(
name|File
name|file
parameter_list|,
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|raf
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|int
name|origByte
init|=
name|raf
operator|.
name|read
argument_list|()
decl_stmt|;
name|raf
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|raf
operator|.
name|writeByte
argument_list|(
name|origByte
operator|-
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
name|raf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Truncate the given file to the given length    */
DECL|method|truncateFile (File logFile, long newLength)
specifier|private
name|void
name|truncateFile
parameter_list|(
name|File
name|logFile
parameter_list|,
name|long
name|newLength
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|logFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|raf
operator|.
name|setLength
argument_list|(
name|newLength
argument_list|)
expr_stmt|;
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Return the length of bytes in the given file after subtracting    * the trailer of 0xFF (OP_INVALID)s.    * This seeks to the end of the file and reads chunks backwards until    * it finds a non-0xFF byte.    * @throws IOException if the file cannot be read    */
DECL|method|getNonTrailerLength (File f)
specifier|private
specifier|static
name|long
name|getNonTrailerLength
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|chunkSizeToRead
init|=
literal|256
operator|*
literal|1024
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|chunkSizeToRead
index|]
decl_stmt|;
name|FileChannel
name|fc
init|=
name|fis
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|fc
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|pos
init|=
name|size
operator|-
operator|(
name|size
operator|%
name|chunkSizeToRead
operator|)
decl_stmt|;
while|while
condition|(
name|pos
operator|>=
literal|0
condition|)
block|{
name|fc
operator|.
name|position
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|int
name|readLen
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|size
operator|-
name|pos
argument_list|,
name|chunkSizeToRead
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|fis
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|readLen
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|readLen
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|buf
index|[
name|i
index|]
operator|!=
name|FSEditLogOpCodes
operator|.
name|OP_INVALID
operator|.
name|getOpCode
argument_list|()
condition|)
block|{
return|return
name|pos
operator|+
name|i
operator|+
literal|1
return|;
comment|// + 1 since we count this byte!
block|}
block|}
name|pos
operator|-=
name|chunkSizeToRead
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStreamLimiter ()
specifier|public
name|void
name|testStreamLimiter
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|File
name|LIMITER_TEST_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"limiter.test"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|LIMITER_TEST_FILE
argument_list|)
decl_stmt|;
try|try
block|{
name|fos
operator|.
name|write
argument_list|(
literal|0x12
argument_list|)
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
literal|0x12
argument_list|)
expr_stmt|;
name|fos
operator|.
name|write
argument_list|(
literal|0x12
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileInputStream
name|fin
init|=
operator|new
name|FileInputStream
argument_list|(
name|LIMITER_TEST_FILE
argument_list|)
decl_stmt|;
name|BufferedInputStream
name|bin
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|fin
argument_list|)
decl_stmt|;
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
name|tracker
init|=
operator|new
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
argument_list|(
name|bin
argument_list|)
decl_stmt|;
try|try
block|{
name|tracker
operator|.
name|setLimit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|mark
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|read
argument_list|()
expr_stmt|;
name|tracker
operator|.
name|read
argument_list|()
expr_stmt|;
try|try
block|{
name|tracker
operator|.
name|read
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected to get IOException after reading past the limit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
name|tracker
operator|.
name|reset
argument_list|()
expr_stmt|;
name|tracker
operator|.
name|mark
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|byte
name|arr
index|[]
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
try|try
block|{
name|tracker
operator|.
name|read
argument_list|(
name|arr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected to get IOException after reading past the limit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
name|tracker
operator|.
name|reset
argument_list|()
expr_stmt|;
name|arr
operator|=
operator|new
name|byte
index|[
literal|2
index|]
expr_stmt|;
name|tracker
operator|.
name|read
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tracker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Create an unfinalized edit log for testing purposes    *    * @param testDir           Directory to create the edit log in    * @param numTx             Number of transactions to add to the new edit log    * @param offsetToTxId      A map from transaction IDs to offsets in the     *                          edit log file.    * @return                  The new edit log file name.    * @throws IOException    */
DECL|method|prepareUnfinalizedTestEditLog (File testDir, int numTx, SortedMap<Long, Long> offsetToTxId)
specifier|static
specifier|private
name|File
name|prepareUnfinalizedTestEditLog
parameter_list|(
name|File
name|testDir
parameter_list|,
name|int
name|numTx
parameter_list|,
name|SortedMap
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|offsetToTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|inProgressFile
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|FSEditLog
name|fsel
init|=
literal|null
decl_stmt|,
name|spyLog
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fsel
operator|=
name|FSImageTestUtil
operator|.
name|createStandaloneEditLog
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
name|spyLog
operator|=
name|spy
argument_list|(
name|fsel
argument_list|)
expr_stmt|;
comment|// Normally, the in-progress edit log would be finalized by
comment|// FSEditLog#endCurrentLogSegment.  For testing purposes, we
comment|// disable that here.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|spyLog
argument_list|)
operator|.
name|endCurrentLogSegment
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|spyLog
operator|.
name|openForWrite
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should exist: "
operator|+
name|inProgressFile
argument_list|,
name|inProgressFile
operator|.
name|exists
argument_list|()
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
name|numTx
condition|;
name|i
operator|++
control|)
block|{
name|long
name|trueOffset
init|=
name|getNonTrailerLength
argument_list|(
name|inProgressFile
argument_list|)
decl_stmt|;
name|long
name|thisTxId
init|=
name|spyLog
operator|.
name|getLastWrittenTxId
argument_list|()
operator|+
literal|1
decl_stmt|;
name|offsetToTxId
operator|.
name|put
argument_list|(
name|trueOffset
argument_list|,
name|thisTxId
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"txid "
operator|+
name|thisTxId
operator|+
literal|" at offset "
operator|+
name|trueOffset
argument_list|)
expr_stmt|;
name|spyLog
operator|.
name|logDelete
argument_list|(
literal|"path"
operator|+
name|i
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|spyLog
operator|.
name|logSync
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|spyLog
operator|!=
literal|null
condition|)
block|{
name|spyLog
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fsel
operator|!=
literal|null
condition|)
block|{
name|fsel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|inProgressFile
return|;
block|}
annotation|@
name|Test
DECL|method|testValidateEditLogWithCorruptHeader ()
specifier|public
name|void
name|testValidateEditLogWithCorruptHeader
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testValidateEditLogWithCorruptHeader"
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|offsetToTxId
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
name|File
name|logFile
init|=
name|prepareUnfinalizedTestEditLog
argument_list|(
name|testDir
argument_list|,
literal|2
argument_list|,
name|offsetToTxId
argument_list|)
decl_stmt|;
name|RandomAccessFile
name|rwf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|logFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
try|try
block|{
name|rwf
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rwf
operator|.
name|writeLong
argument_list|(
literal|42
argument_list|)
expr_stmt|;
comment|// corrupt header
block|}
finally|finally
block|{
name|rwf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|EditLogValidation
name|validation
init|=
name|EditLogFileInputStream
operator|.
name|validateEditLog
argument_list|(
name|logFile
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|validation
operator|.
name|hasCorruptHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidateEditLogWithCorruptBody ()
specifier|public
name|void
name|testValidateEditLogWithCorruptBody
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testValidateEditLogWithCorruptBody"
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|offsetToTxId
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
specifier|final
name|int
name|NUM_TXNS
init|=
literal|20
decl_stmt|;
name|File
name|logFile
init|=
name|prepareUnfinalizedTestEditLog
argument_list|(
name|testDir
argument_list|,
name|NUM_TXNS
argument_list|,
name|offsetToTxId
argument_list|)
decl_stmt|;
comment|// Back up the uncorrupted log
name|File
name|logFileBak
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
name|logFile
operator|.
name|getName
argument_list|()
operator|+
literal|".bak"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|logFile
argument_list|,
name|logFileBak
argument_list|)
expr_stmt|;
name|EditLogValidation
name|validation
init|=
name|EditLogFileInputStream
operator|.
name|validateEditLog
argument_list|(
name|logFile
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|validation
operator|.
name|hasCorruptHeader
argument_list|()
argument_list|)
expr_stmt|;
comment|// We expect that there will be an OP_START_LOG_SEGMENT, followed by
comment|// NUM_TXNS opcodes, followed by an OP_END_LOG_SEGMENT.
name|assertEquals
argument_list|(
name|NUM_TXNS
operator|+
literal|1
argument_list|,
name|validation
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Corrupt each edit and verify that validation continues to work
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|offsetToTxId
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|txOffset
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|txId
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Restore backup, corrupt the txn opcode
name|Files
operator|.
name|copy
argument_list|(
name|logFileBak
argument_list|,
name|logFile
argument_list|)
expr_stmt|;
name|corruptByteInFile
argument_list|(
name|logFile
argument_list|,
name|txOffset
argument_list|)
expr_stmt|;
name|validation
operator|=
name|EditLogFileInputStream
operator|.
name|validateEditLog
argument_list|(
name|logFile
argument_list|)
expr_stmt|;
name|long
name|expectedEndTxId
init|=
operator|(
name|txId
operator|==
operator|(
name|NUM_TXNS
operator|+
literal|1
operator|)
operator|)
condition|?
name|NUM_TXNS
else|:
operator|(
name|NUM_TXNS
operator|+
literal|1
operator|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Failed when corrupting txn opcode at "
operator|+
name|txOffset
argument_list|,
name|expectedEndTxId
argument_list|,
name|validation
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|validation
operator|.
name|hasCorruptHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Truncate right before each edit and verify that validation continues
comment|// to work
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|offsetToTxId
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|txOffset
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|txId
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Restore backup, corrupt the txn opcode
name|Files
operator|.
name|copy
argument_list|(
name|logFileBak
argument_list|,
name|logFile
argument_list|)
expr_stmt|;
name|truncateFile
argument_list|(
name|logFile
argument_list|,
name|txOffset
argument_list|)
expr_stmt|;
name|validation
operator|=
name|EditLogFileInputStream
operator|.
name|validateEditLog
argument_list|(
name|logFile
argument_list|)
expr_stmt|;
name|long
name|expectedEndTxId
init|=
operator|(
name|txId
operator|==
literal|0
operator|)
condition|?
name|HdfsConstants
operator|.
name|INVALID_TXID
else|:
operator|(
name|txId
operator|-
literal|1
operator|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Failed when corrupting txid "
operator|+
name|txId
operator|+
literal|" txn opcode "
operator|+
literal|"at "
operator|+
name|txOffset
argument_list|,
name|expectedEndTxId
argument_list|,
name|validation
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|validation
operator|.
name|hasCorruptHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValidateEmptyEditLog ()
specifier|public
name|void
name|testValidateEmptyEditLog
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testValidateEmptyEditLog"
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|offsetToTxId
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
name|File
name|logFile
init|=
name|prepareUnfinalizedTestEditLog
argument_list|(
name|testDir
argument_list|,
literal|0
argument_list|,
name|offsetToTxId
argument_list|)
decl_stmt|;
comment|// Truncate the file so that there is nothing except the header
name|truncateFile
argument_list|(
name|logFile
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|EditLogValidation
name|validation
init|=
name|EditLogFileInputStream
operator|.
name|validateEditLog
argument_list|(
name|logFile
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|validation
operator|.
name|hasCorruptHeader
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HdfsConstants
operator|.
name|INVALID_TXID
argument_list|,
name|validation
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

