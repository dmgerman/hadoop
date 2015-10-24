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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|LocatedBlocks
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
DECL|class|TestBlockMissingException
specifier|public
class|class
name|TestBlockMissingException
block|{
DECL|field|LOG
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.hdfs.TestBlockMissing"
argument_list|)
decl_stmt|;
DECL|field|NUM_DATANODES
specifier|final
specifier|static
name|int
name|NUM_DATANODES
init|=
literal|3
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|dfs
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
DECL|field|fileSys
name|DistributedFileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
comment|/**    * Test DFS Raid    */
annotation|@
name|Test
DECL|method|testBlockMissingException ()
specifier|public
name|void
name|testBlockMissingException
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testBlockMissingException started."
argument_list|)
expr_stmt|;
name|long
name|blockSize
init|=
literal|1024L
decl_stmt|;
name|int
name|numBlocks
init|=
literal|4
decl_stmt|;
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
comment|// Set short retry timeouts so this test runs faster
name|conf
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
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
name|NUM_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|Path
name|file1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/dhruba/raidtest/file1"
argument_list|)
decl_stmt|;
name|createOldFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|,
name|numBlocks
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
comment|// extract block locations from File system. Wait till file is closed.
name|LocatedBlocks
name|locations
init|=
literal|null
decl_stmt|;
name|locations
operator|=
name|fileSys
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|file1
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|numBlocks
operator|*
name|blockSize
argument_list|)
expr_stmt|;
comment|// remove block of file
name|LOG
operator|.
name|info
argument_list|(
literal|"Remove first block of file"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|corruptBlockOnDataNodesByDeletingBlockFile
argument_list|(
name|locations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
comment|// validate that the system throws BlockMissingException
name|validateFile
argument_list|(
name|fileSys
argument_list|,
name|file1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Test testBlockMissingException completed."
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// creates a file and populate it with data.
comment|//
DECL|method|createOldFile (FileSystem fileSys, Path name, int repl, int numBlocks, long blocksize)
specifier|private
name|void
name|createOldFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|repl
parameter_list|,
name|int
name|numBlocks
parameter_list|,
name|long
name|blocksize
parameter_list|)
throws|throws
name|IOException
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
name|repl
argument_list|,
name|blocksize
argument_list|)
decl_stmt|;
comment|// fill data into file
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|blocksize
index|]
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
name|numBlocks
condition|;
name|i
operator|++
control|)
block|{
name|stm
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//
comment|// validates that file encounters BlockMissingException
comment|//
DECL|method|validateFile (FileSystem fileSys, Path name)
specifier|private
name|void
name|validateFile
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
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|4192
index|]
decl_stmt|;
name|int
name|num
init|=
literal|0
decl_stmt|;
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
while|while
condition|(
name|num
operator|>=
literal|0
condition|)
block|{
name|num
operator|=
name|stm
operator|.
name|read
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|num
operator|<
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected BlockMissingException "
argument_list|,
name|gotException
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

