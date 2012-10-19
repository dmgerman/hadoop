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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This class tests if FSOutputSummer works correctly.  */
end_comment

begin_class
DECL|class|TestFSOutputSummer
specifier|public
class|class
name|TestFSOutputSummer
block|{
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|BYTES_PER_CHECKSUM
specifier|private
specifier|static
specifier|final
name|int
name|BYTES_PER_CHECKSUM
init|=
literal|10
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|2
operator|*
name|BYTES_PER_CHECKSUM
decl_stmt|;
DECL|field|HALF_CHUNK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|HALF_CHUNK_SIZE
init|=
name|BYTES_PER_CHECKSUM
operator|/
literal|2
decl_stmt|;
DECL|field|FILE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|FILE_SIZE
init|=
literal|2
operator|*
name|BLOCK_SIZE
operator|-
literal|1
decl_stmt|;
DECL|field|NUM_OF_DATANODES
specifier|private
specifier|static
specifier|final
name|short
name|NUM_OF_DATANODES
init|=
literal|2
decl_stmt|;
DECL|field|expected
specifier|private
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|FILE_SIZE
index|]
decl_stmt|;
DECL|field|actual
specifier|private
name|byte
index|[]
name|actual
init|=
operator|new
name|byte
index|[
name|FILE_SIZE
index|]
decl_stmt|;
DECL|field|fileSys
specifier|private
name|FileSystem
name|fileSys
decl_stmt|;
comment|/* create a file, write all data at once */
DECL|method|writeFile1 (Path name)
specifier|private
name|void
name|writeFile1
parameter_list|(
name|Path
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|NUM_OF_DATANODES
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/* create a file, write data chunk by chunk */
DECL|method|writeFile2 (Path name)
specifier|private
name|void
name|writeFile2
parameter_list|(
name|Path
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|NUM_OF_DATANODES
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|FILE_SIZE
operator|-
name|BYTES_PER_CHECKSUM
condition|;
name|i
operator|+=
name|BYTES_PER_CHECKSUM
control|)
block|{
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
name|i
argument_list|,
name|BYTES_PER_CHECKSUM
argument_list|)
expr_stmt|;
block|}
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
name|i
argument_list|,
name|FILE_SIZE
operator|-
literal|3
operator|*
name|BYTES_PER_CHECKSUM
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/* create a file, write data with vairable amount of data */
DECL|method|writeFile3 (Path name)
specifier|private
name|void
name|writeFile3
parameter_list|(
name|Path
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDataOutputStream
name|stm
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|fileSys
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
name|NUM_OF_DATANODES
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
literal|0
argument_list|,
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
name|HALF_CHUNK_SIZE
argument_list|,
name|BYTES_PER_CHECKSUM
operator|+
literal|2
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
name|HALF_CHUNK_SIZE
operator|+
name|BYTES_PER_CHECKSUM
operator|+
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
name|HALF_CHUNK_SIZE
operator|+
name|BYTES_PER_CHECKSUM
operator|+
literal|4
argument_list|,
name|HALF_CHUNK_SIZE
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
name|BLOCK_SIZE
operator|+
literal|4
argument_list|,
name|BYTES_PER_CHECKSUM
operator|-
literal|4
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|expected
argument_list|,
name|BLOCK_SIZE
operator|+
name|BYTES_PER_CHECKSUM
argument_list|,
name|FILE_SIZE
operator|-
literal|3
operator|*
name|BYTES_PER_CHECKSUM
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|checkAndEraseData (byte[] actual, int from, byte[] expected, String message)
specifier|private
name|void
name|checkAndEraseData
parameter_list|(
name|byte
index|[]
name|actual
parameter_list|,
name|int
name|from
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|actual
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|" byte "
operator|+
operator|(
name|from
operator|+
name|idx
operator|)
operator|+
literal|" differs. expected "
operator|+
name|expected
index|[
name|from
operator|+
name|idx
index|]
operator|+
literal|" actual "
operator|+
name|actual
index|[
name|idx
index|]
argument_list|,
name|actual
index|[
name|idx
index|]
argument_list|,
name|expected
index|[
name|from
operator|+
name|idx
index|]
argument_list|)
expr_stmt|;
name|actual
index|[
name|idx
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|checkFile (Path name)
specifier|private
name|void
name|checkFile
parameter_list|(
name|Path
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|FSDataInputStream
name|stm
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// do a sanity check. Read the file
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|checkAndEraseData
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
literal|"Read Sanity Test"
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|cleanupFile (Path name)
specifier|private
name|void
name|cleanupFile
parameter_list|(
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|fileSys
operator|.
name|exists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test write opeation for output stream in DFS.    */
annotation|@
name|Test
DECL|method|testFSOutputSummer ()
specifier|public
name|void
name|testFSOutputSummer
parameter_list|()
throws|throws
name|Exception
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
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|BYTES_PER_CHECKSUM
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
name|NUM_OF_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
try|try
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"try.dat"
argument_list|)
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|rand
operator|.
name|nextBytes
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|writeFile1
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|writeFile2
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|writeFile3
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileSys
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
block|}
end_class

end_unit

