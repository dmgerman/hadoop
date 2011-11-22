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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|SimulatedFSDataset
import|;
end_import

begin_comment
comment|/**  * This class tests the creation of files with block-size  * smaller than the default buffer size of 4K.  */
end_comment

begin_class
DECL|class|TestSmallBlock
specifier|public
class|class
name|TestSmallBlock
extends|extends
name|TestCase
block|{
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
literal|1
decl_stmt|;
DECL|field|fileSize
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|20
decl_stmt|;
DECL|field|simulatedStorage
name|boolean
name|simulatedStorage
init|=
literal|false
decl_stmt|;
DECL|method|writeFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|writeFile
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
comment|// create and write a file that contains three blocks of data
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
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
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
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
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
DECL|method|checkFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|checkFile
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
name|fileSize
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of blocks"
argument_list|,
name|fileSize
argument_list|,
name|locations
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
if|if
condition|(
name|simulatedStorage
condition|)
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
name|expected
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|expected
index|[
name|i
index|]
operator|=
name|SimulatedFSDataset
operator|.
name|DEFAULT_DATABYTE
expr_stmt|;
block|}
block|}
else|else
block|{
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
block|}
comment|// do a sanity check. Read the file
name|byte
index|[]
name|actual
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
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
DECL|method|cleanupFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|cleanupFile
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
comment|/**    * Tests small block size in in DFS.    */
DECL|method|testSmallBlock ()
specifier|public
name|void
name|testSmallBlock
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
if|if
condition|(
name|simulatedStorage
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SIMULATEDDATASTORAGE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|"1"
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
name|build
argument_list|()
decl_stmt|;
name|FileSystem
name|fileSys
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"smallblocktest.dat"
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|cleanupFile
argument_list|(
name|fileSys
argument_list|,
name|file1
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
DECL|method|testSmallBlockSimulatedStorage ()
specifier|public
name|void
name|testSmallBlockSimulatedStorage
parameter_list|()
throws|throws
name|IOException
block|{
name|simulatedStorage
operator|=
literal|true
expr_stmt|;
name|testSmallBlock
argument_list|()
expr_stmt|;
name|simulatedStorage
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

