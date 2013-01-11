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
name|EOFException
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
name|ByteBuffer
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
name|TimeoutException
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
name|protocol
operator|.
name|DatanodeID
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
name|io
operator|.
name|IOUtils
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
name|Test
import|;
end_import

begin_class
DECL|class|TestBlockReaderLocal
specifier|public
class|class
name|TestBlockReaderLocal
block|{
DECL|method|assertArrayRegionsEqual (byte []buf1, int off1, byte []buf2, int off2, int len)
specifier|public
specifier|static
name|void
name|assertArrayRegionsEqual
parameter_list|(
name|byte
index|[]
name|buf1
parameter_list|,
name|int
name|off1
parameter_list|,
name|byte
index|[]
name|buf2
parameter_list|,
name|int
name|off2
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
if|if
condition|(
name|buf1
index|[
name|off1
operator|+
name|i
index|]
operator|!=
name|buf2
index|[
name|off2
operator|+
name|i
index|]
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"arrays differ at byte "
operator|+
name|i
operator|+
literal|". "
operator|+
literal|"The first array has "
operator|+
operator|(
name|int
operator|)
name|buf1
index|[
name|off1
operator|+
name|i
index|]
operator|+
literal|", but the second array has "
operator|+
operator|(
name|int
operator|)
name|buf2
index|[
name|off2
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Similar to IOUtils#readFully(). Reads bytes in a loop.    *    * @param reader           The BlockReaderLocal to read bytes from    * @param buf              The ByteBuffer to read into    * @param off              The offset in the buffer to read into    * @param len              The number of bytes to read.    *     * @throws IOException     If it could not read the requested number of bytes    */
DECL|method|readFully (BlockReaderLocal reader, ByteBuffer buf, int off, int len)
specifier|private
specifier|static
name|void
name|readFully
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|amt
init|=
name|len
decl_stmt|;
while|while
condition|(
name|amt
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|limit
argument_list|(
name|off
operator|+
name|len
argument_list|)
expr_stmt|;
name|buf
operator|.
name|position
argument_list|(
name|off
argument_list|)
expr_stmt|;
name|long
name|ret
init|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Premature EOF from BlockReaderLocal "
operator|+
literal|"after reading "
operator|+
operator|(
name|len
operator|-
name|amt
operator|)
operator|+
literal|" byte(s)."
argument_list|)
throw|;
block|}
name|amt
operator|-=
name|ret
expr_stmt|;
name|off
operator|+=
name|ret
expr_stmt|;
block|}
block|}
DECL|interface|BlockReaderLocalTest
specifier|private
specifier|static
interface|interface
name|BlockReaderLocalTest
block|{
DECL|field|TEST_LENGTH
specifier|final
name|int
name|TEST_LENGTH
init|=
literal|12345
decl_stmt|;
DECL|method|setup (File blockFile, boolean usingChecksums)
specifier|public
name|void
name|setup
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|boolean
name|usingChecksums
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|doTest (BlockReaderLocal reader, byte original[])
specifier|public
name|void
name|doTest
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|byte
name|original
index|[]
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|method|runBlockReaderLocalTest (BlockReaderLocalTest test, boolean checksum)
specifier|public
name|void
name|runBlockReaderLocalTest
parameter_list|(
name|BlockReaderLocalTest
name|test
parameter_list|,
name|boolean
name|checksum
parameter_list|)
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_READ_SHORTCIRCUIT_SKIP_CHECKSUM_KEY
argument_list|,
operator|!
name|checksum
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CHECKSUM_TYPE_KEY
argument_list|,
literal|"CRC32C"
argument_list|)
expr_stmt|;
name|FileInputStream
name|dataIn
init|=
literal|null
decl_stmt|,
name|checkIn
init|=
literal|null
decl_stmt|;
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/a"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|RANDOM_SEED
init|=
literal|4567L
decl_stmt|;
name|BlockReaderLocal
name|blockReaderLocal
init|=
literal|null
decl_stmt|;
name|FSDataInputStream
name|fsIn
init|=
literal|null
decl_stmt|;
name|byte
name|original
index|[]
init|=
operator|new
name|byte
index|[
name|BlockReaderLocalTest
operator|.
name|TEST_LENGTH
index|]
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
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
name|BlockReaderLocalTest
operator|.
name|TEST_LENGTH
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|RANDOM_SEED
argument_list|)
expr_stmt|;
try|try
block|{
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"unexpected InterruptedException during "
operator|+
literal|"waitReplication: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"unexpected TimeoutException during "
operator|+
literal|"waitReplication: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|fsIn
operator|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|fsIn
argument_list|,
name|original
argument_list|,
literal|0
argument_list|,
name|BlockReaderLocalTest
operator|.
name|TEST_LENGTH
argument_list|)
expr_stmt|;
name|fsIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|fsIn
operator|=
literal|null
expr_stmt|;
name|ExtendedBlock
name|block
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|File
name|dataFile
init|=
name|MiniDFSCluster
operator|.
name|getBlockFile
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
decl_stmt|;
name|File
name|metaFile
init|=
name|MiniDFSCluster
operator|.
name|getBlockMetadataFile
argument_list|(
literal|0
argument_list|,
name|block
argument_list|)
decl_stmt|;
name|DatanodeID
name|datanodeID
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
name|getDatanodeId
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
name|test
operator|.
name|setup
argument_list|(
name|dataFile
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
name|dataIn
operator|=
operator|new
name|FileInputStream
argument_list|(
name|dataFile
argument_list|)
expr_stmt|;
name|checkIn
operator|=
operator|new
name|FileInputStream
argument_list|(
name|metaFile
argument_list|)
expr_stmt|;
name|blockReaderLocal
operator|=
operator|new
name|BlockReaderLocal
argument_list|(
name|conf
argument_list|,
name|TEST_PATH
operator|.
name|getName
argument_list|()
argument_list|,
name|block
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
name|dataIn
argument_list|,
name|checkIn
argument_list|,
name|datanodeID
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
name|dataIn
operator|=
literal|null
expr_stmt|;
name|checkIn
operator|=
literal|null
expr_stmt|;
name|test
operator|.
name|doTest
argument_list|(
name|blockReaderLocal
argument_list|,
name|original
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fsIn
operator|!=
literal|null
condition|)
name|fsIn
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
if|if
condition|(
name|dataIn
operator|!=
literal|null
condition|)
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|checkIn
operator|!=
literal|null
condition|)
name|checkIn
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|blockReaderLocal
operator|!=
literal|null
condition|)
name|blockReaderLocal
operator|.
name|close
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestBlockReaderLocalImmediateClose
specifier|private
specifier|static
class|class
name|TestBlockReaderLocalImmediateClose
implements|implements
name|BlockReaderLocalTest
block|{
annotation|@
name|Override
DECL|method|setup (File blockFile, boolean usingChecksums)
specifier|public
name|void
name|setup
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|boolean
name|usingChecksums
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|doTest (BlockReaderLocal reader, byte original[])
specifier|public
name|void
name|doTest
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|byte
name|original
index|[]
parameter_list|)
throws|throws
name|IOException
block|{ }
block|}
annotation|@
name|Test
DECL|method|testBlockReaderLocalImmediateClose ()
specifier|public
name|void
name|testBlockReaderLocalImmediateClose
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalImmediateClose
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalImmediateClose
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|TestBlockReaderSimpleReads
specifier|private
specifier|static
class|class
name|TestBlockReaderSimpleReads
implements|implements
name|BlockReaderLocalTest
block|{
annotation|@
name|Override
DECL|method|setup (File blockFile, boolean usingChecksums)
specifier|public
name|void
name|setup
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|boolean
name|usingChecksums
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|doTest (BlockReaderLocal reader, byte original[])
specifier|public
name|void
name|doTest
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|byte
name|original
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|TEST_LENGTH
index|]
decl_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|512
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|512
argument_list|,
name|buf
argument_list|,
literal|512
argument_list|,
literal|512
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|1024
argument_list|,
literal|513
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|1024
argument_list|,
name|buf
argument_list|,
literal|1024
argument_list|,
literal|513
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|1537
argument_list|,
literal|514
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|1537
argument_list|,
name|buf
argument_list|,
literal|1537
argument_list|,
literal|514
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockReaderSimpleReads ()
specifier|public
name|void
name|testBlockReaderSimpleReads
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderSimpleReads
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockReaderSimpleReadsNoChecksum ()
specifier|public
name|void
name|testBlockReaderSimpleReadsNoChecksum
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderSimpleReads
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|TestBlockReaderLocalArrayReads2
specifier|private
specifier|static
class|class
name|TestBlockReaderLocalArrayReads2
implements|implements
name|BlockReaderLocalTest
block|{
annotation|@
name|Override
DECL|method|setup (File blockFile, boolean usingChecksums)
specifier|public
name|void
name|setup
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|boolean
name|usingChecksums
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|doTest (BlockReaderLocal reader, byte original[])
specifier|public
name|void
name|doTest
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|byte
name|original
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|TEST_LENGTH
index|]
decl_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|10
argument_list|,
name|buf
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|110
argument_list|,
literal|700
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|110
argument_list|,
name|buf
argument_list|,
literal|110
argument_list|,
literal|700
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|810
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// from offset 810 to offset 811
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|811
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|811
argument_list|,
name|buf
argument_list|,
literal|811
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|816
argument_list|,
literal|900
argument_list|)
expr_stmt|;
comment|// skip from offset 816 to offset 1716
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|1716
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|1716
argument_list|,
name|buf
argument_list|,
literal|1716
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockReaderLocalArrayReads2 ()
specifier|public
name|void
name|testBlockReaderLocalArrayReads2
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalArrayReads2
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockReaderLocalArrayReads2NoChecksum ()
specifier|public
name|void
name|testBlockReaderLocalArrayReads2NoChecksum
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalArrayReads2
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|TestBlockReaderLocalByteBufferReads
specifier|private
specifier|static
class|class
name|TestBlockReaderLocalByteBufferReads
implements|implements
name|BlockReaderLocalTest
block|{
annotation|@
name|Override
DECL|method|setup (File blockFile, boolean usingChecksums)
specifier|public
name|void
name|setup
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|boolean
name|usingChecksums
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|doTest (BlockReaderLocal reader, byte original[])
specifier|public
name|void
name|doTest
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|byte
name|original
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
name|TEST_LENGTH
index|]
argument_list|)
decl_stmt|;
name|readFully
argument_list|(
name|reader
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|readFully
argument_list|(
name|reader
argument_list|,
name|buf
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|10
argument_list|,
name|buf
operator|.
name|array
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|readFully
argument_list|(
name|reader
argument_list|,
name|buf
argument_list|,
literal|110
argument_list|,
literal|700
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|110
argument_list|,
name|buf
operator|.
name|array
argument_list|()
argument_list|,
literal|110
argument_list|,
literal|700
argument_list|)
expr_stmt|;
name|reader
operator|.
name|skip
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// skip from offset 810 to offset 811
name|readFully
argument_list|(
name|reader
argument_list|,
name|buf
argument_list|,
literal|811
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|811
argument_list|,
name|buf
operator|.
name|array
argument_list|()
argument_list|,
literal|811
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockReaderLocalByteBufferReads ()
specifier|public
name|void
name|testBlockReaderLocalByteBufferReads
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalByteBufferReads
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBlockReaderLocalByteBufferReadsNoChecksum ()
specifier|public
name|void
name|testBlockReaderLocalByteBufferReadsNoChecksum
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalByteBufferReads
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|class|TestBlockReaderLocalReadCorruptStart
specifier|private
specifier|static
class|class
name|TestBlockReaderLocalReadCorruptStart
implements|implements
name|BlockReaderLocalTest
block|{
DECL|field|usingChecksums
name|boolean
name|usingChecksums
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|setup (File blockFile, boolean usingChecksums)
specifier|public
name|void
name|setup
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|boolean
name|usingChecksums
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|bf
init|=
literal|null
decl_stmt|;
name|this
operator|.
name|usingChecksums
operator|=
name|usingChecksums
expr_stmt|;
try|try
block|{
name|bf
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|blockFile
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|bf
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|bf
operator|!=
literal|null
condition|)
name|bf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTest (BlockReaderLocal reader, byte original[])
specifier|public
name|void
name|doTest
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|byte
name|original
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|TEST_LENGTH
index|]
decl_stmt|;
if|if
condition|(
name|usingChecksums
condition|)
block|{
try|try
block|{
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"did not detect corruption"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
else|else
block|{
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockReaderLocalReadCorruptStart ()
specifier|public
name|void
name|testBlockReaderLocalReadCorruptStart
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalReadCorruptStart
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|TestBlockReaderLocalReadCorrupt
specifier|private
specifier|static
class|class
name|TestBlockReaderLocalReadCorrupt
implements|implements
name|BlockReaderLocalTest
block|{
DECL|field|usingChecksums
name|boolean
name|usingChecksums
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|setup (File blockFile, boolean usingChecksums)
specifier|public
name|void
name|setup
parameter_list|(
name|File
name|blockFile
parameter_list|,
name|boolean
name|usingChecksums
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|bf
init|=
literal|null
decl_stmt|;
name|this
operator|.
name|usingChecksums
operator|=
name|usingChecksums
expr_stmt|;
try|try
block|{
name|bf
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|blockFile
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|bf
operator|.
name|seek
argument_list|(
literal|1539
argument_list|)
expr_stmt|;
name|bf
operator|.
name|write
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|bf
operator|!=
literal|null
condition|)
name|bf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTest (BlockReaderLocal reader, byte original[])
specifier|public
name|void
name|doTest
parameter_list|(
name|BlockReaderLocal
name|reader
parameter_list|,
name|byte
name|original
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|buf
index|[]
init|=
operator|new
name|byte
index|[
name|TEST_LENGTH
index|]
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|10
argument_list|,
name|buf
argument_list|,
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|110
argument_list|,
literal|700
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|110
argument_list|,
name|buf
argument_list|,
literal|110
argument_list|,
literal|700
argument_list|)
expr_stmt|;
name|reader
operator|.
name|skip
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// skip from offset 810 to offset 811
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|811
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertArrayRegionsEqual
argument_list|(
name|original
argument_list|,
literal|811
argument_list|,
name|buf
argument_list|,
literal|811
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|reader
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|816
argument_list|,
literal|900
argument_list|)
expr_stmt|;
if|if
condition|(
name|usingChecksums
condition|)
block|{
comment|// We should detect the corruption when using a checksum file.
name|Assert
operator|.
name|fail
argument_list|(
literal|"did not detect corruption"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|usingChecksums
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"didn't expect to get ChecksumException: not "
operator|+
literal|"using checksums."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testBlockReaderLocalReadCorrupt ()
specifier|public
name|void
name|testBlockReaderLocalReadCorrupt
parameter_list|()
throws|throws
name|IOException
block|{
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalReadCorrupt
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runBlockReaderLocalTest
argument_list|(
operator|new
name|TestBlockReaderLocalReadCorrupt
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

