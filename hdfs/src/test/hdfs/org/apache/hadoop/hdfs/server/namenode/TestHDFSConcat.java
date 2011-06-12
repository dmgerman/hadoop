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
name|assertFalse
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
name|assertNotNull
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
name|assertNull
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
name|ContentSummary
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
name|tools
operator|.
name|DFSAdmin
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
name|security
operator|.
name|UserGroupInformation
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
name|Before
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
DECL|class|TestHDFSConcat
specifier|public
class|class
name|TestHDFSConcat
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
name|TestHDFSConcat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REPL_FACTOR
specifier|private
specifier|static
specifier|final
name|short
name|REPL_FACTOR
init|=
literal|2
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|nn
specifier|private
name|NameNode
name|nn
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|static
name|long
name|blockSize
init|=
literal|512
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
static|static
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
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|startUpCluster ()
specifier|public
name|void
name|startUpCluster
parameter_list|()
throws|throws
name|IOException
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
name|REPL_FACTOR
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Failed Cluster Creation"
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Failed to get FileSystem"
argument_list|,
name|dfs
argument_list|)
expr_stmt|;
name|nn
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Failed to get NameNode"
argument_list|,
name|nn
argument_list|)
expr_stmt|;
block|}
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
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|close
argument_list|()
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
block|}
block|}
comment|/**    * Concatenates 10 files into one    * Verifies the final size, deletion of the file, number of blocks    * @throws IOException    */
annotation|@
name|Test
DECL|method|testConcat ()
specifier|public
name|void
name|testConcat
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|numFiles
init|=
literal|10
decl_stmt|;
name|long
name|fileLen
init|=
name|blockSize
operator|*
literal|3
decl_stmt|;
name|HdfsFileStatus
name|fStatus
decl_stmt|;
name|FSDataInputStream
name|stm
decl_stmt|;
name|String
name|trg
init|=
operator|new
name|String
argument_list|(
literal|"/trg"
argument_list|)
decl_stmt|;
name|Path
name|trgPath
init|=
operator|new
name|Path
argument_list|(
name|trg
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|trgPath
argument_list|,
name|fileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fStatus
operator|=
name|nn
operator|.
name|getFileInfo
argument_list|(
name|trg
argument_list|)
expr_stmt|;
name|long
name|trgLen
init|=
name|fStatus
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|long
name|trgBlocks
init|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|trg
argument_list|,
literal|0
argument_list|,
name|trgLen
argument_list|)
operator|.
name|locatedBlockCount
argument_list|()
decl_stmt|;
name|Path
index|[]
name|files
init|=
operator|new
name|Path
index|[
name|numFiles
index|]
decl_stmt|;
name|byte
index|[]
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|numFiles
index|]
index|[
operator|(
name|int
operator|)
name|fileLen
index|]
decl_stmt|;
name|LocatedBlocks
index|[]
name|lblocks
init|=
operator|new
name|LocatedBlocks
index|[
name|numFiles
index|]
decl_stmt|;
name|long
index|[]
name|lens
init|=
operator|new
name|long
index|[
name|numFiles
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
literal|"/file"
operator|+
name|i
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating file "
operator|+
name|path
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|,
name|fileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fStatus
operator|=
name|nn
operator|.
name|getFileInfo
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|lens
index|[
name|i
index|]
operator|=
name|fStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|trgLen
argument_list|,
name|lens
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// file of the same length.
name|lblocks
index|[
name|i
index|]
operator|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|lens
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|//read the file
name|stm
operator|=
name|dfs
operator|.
name|open
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|//bytes[i][10] = 10;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// check permissions -try the operation with the "wrong" user
specifier|final
name|UserGroupInformation
name|user1
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"theDoctor"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tardis"
block|}
argument_list|)
decl_stmt|;
name|DistributedFileSystem
name|hdfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|DFSTestUtil
operator|.
name|getFileSystemAs
argument_list|(
name|user1
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|hdfs
operator|.
name|concat
argument_list|(
name|trgPath
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Permission exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Got expected exception for permissions:"
operator|+
name|ie
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// expected
block|}
comment|// check count update
name|ContentSummary
name|cBefore
init|=
name|dfs
operator|.
name|getContentSummary
argument_list|(
name|trgPath
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
comment|// now concatenate
name|dfs
operator|.
name|concat
argument_list|(
name|trgPath
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// verify  count
name|ContentSummary
name|cAfter
init|=
name|dfs
operator|.
name|getContentSummary
argument_list|(
name|trgPath
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|cBefore
operator|.
name|getFileCount
argument_list|()
argument_list|,
name|cAfter
operator|.
name|getFileCount
argument_list|()
operator|+
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// verify other stuff
name|long
name|totalLen
init|=
name|trgLen
decl_stmt|;
name|long
name|totalBlocks
init|=
name|trgBlocks
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|totalLen
operator|+=
name|lens
index|[
name|i
index|]
expr_stmt|;
name|totalBlocks
operator|+=
name|lblocks
index|[
name|i
index|]
operator|.
name|locatedBlockCount
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"total len="
operator|+
name|totalLen
operator|+
literal|"; totalBlocks="
operator|+
name|totalBlocks
argument_list|)
expr_stmt|;
name|fStatus
operator|=
name|nn
operator|.
name|getFileInfo
argument_list|(
name|trg
argument_list|)
expr_stmt|;
name|trgLen
operator|=
name|fStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
comment|// new length
comment|// read the resulting file
name|stm
operator|=
name|dfs
operator|.
name|open
argument_list|(
name|trgPath
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteFileConcat
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|trgLen
index|]
decl_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|byteFileConcat
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|trgBlocks
operator|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|trg
argument_list|,
literal|0
argument_list|,
name|trgLen
argument_list|)
operator|.
name|locatedBlockCount
argument_list|()
expr_stmt|;
comment|//verifications
comment|// 1. number of blocks
name|assertEquals
argument_list|(
name|trgBlocks
argument_list|,
name|totalBlocks
argument_list|)
expr_stmt|;
comment|// 2. file lengths
name|assertEquals
argument_list|(
name|trgLen
argument_list|,
name|totalLen
argument_list|)
expr_stmt|;
comment|// 3. removal of the src file
for|for
control|(
name|Path
name|p
range|:
name|files
control|)
block|{
name|fStatus
operator|=
name|nn
operator|.
name|getFileInfo
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"File "
operator|+
name|p
operator|+
literal|" still exists"
argument_list|,
name|fStatus
argument_list|)
expr_stmt|;
comment|// file shouldn't exist
comment|// try to create fie with the same name
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|p
argument_list|,
name|fileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// 4. content
name|checkFileContent
argument_list|(
name|byteFileConcat
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
comment|// add a small file (less then a block)
name|Path
name|smallFile
init|=
operator|new
name|Path
argument_list|(
literal|"/sfile"
argument_list|)
decl_stmt|;
name|int
name|sFileLen
init|=
literal|10
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|smallFile
argument_list|,
name|sFileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|concat
argument_list|(
name|trgPath
argument_list|,
operator|new
name|Path
index|[]
block|{
name|smallFile
block|}
argument_list|)
expr_stmt|;
name|fStatus
operator|=
name|nn
operator|.
name|getFileInfo
argument_list|(
name|trg
argument_list|)
expr_stmt|;
name|trgLen
operator|=
name|fStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
comment|// new length
comment|// check number of blocks
name|trgBlocks
operator|=
name|nn
operator|.
name|getBlockLocations
argument_list|(
name|trg
argument_list|,
literal|0
argument_list|,
name|trgLen
argument_list|)
operator|.
name|locatedBlockCount
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|trgBlocks
argument_list|,
name|totalBlocks
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// and length
name|assertEquals
argument_list|(
name|trgLen
argument_list|,
name|totalLen
operator|+
name|sFileLen
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the concat operation is properly persisted in the    * edit log, and properly replayed on restart.    */
annotation|@
name|Test
DECL|method|testConcatInEditLog ()
specifier|public
name|void
name|testConcatInEditLog
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|TEST_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"/testConcatInEditLog"
argument_list|)
decl_stmt|;
specifier|final
name|long
name|FILE_LEN
init|=
name|blockSize
decl_stmt|;
comment|// 1. Concat some files
name|Path
index|[]
name|srcFiles
init|=
operator|new
name|Path
index|[
literal|3
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
name|srcFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"src-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|,
name|FILE_LEN
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|srcFiles
index|[
name|i
index|]
operator|=
name|path
expr_stmt|;
block|}
name|Path
name|targetFile
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
argument_list|,
literal|"target"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|targetFile
argument_list|,
name|FILE_LEN
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|concat
argument_list|(
name|targetFile
argument_list|,
name|srcFiles
argument_list|)
expr_stmt|;
comment|// 2. Verify the concat operation basically worked, and record
comment|// file status.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|targetFile
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|origStatus
init|=
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
comment|// 3. Restart NN to force replay from edit log
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// 4. Verify concat operation was replayed correctly and file status
comment|// did not change.
name|assertTrue
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|targetFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dfs
operator|.
name|exists
argument_list|(
name|srcFiles
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|statusAfterRestart
init|=
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|origStatus
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|statusAfterRestart
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// compare content
DECL|method|checkFileContent (byte[] concat, byte[][] bytes )
specifier|private
name|void
name|checkFileContent
parameter_list|(
name|byte
index|[]
name|concat
parameter_list|,
name|byte
index|[]
index|[]
name|bytes
parameter_list|)
block|{
name|int
name|idx
init|=
literal|0
decl_stmt|;
name|boolean
name|mismatch
init|=
literal|false
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|bb
range|:
name|bytes
control|)
block|{
for|for
control|(
name|byte
name|b
range|:
name|bb
control|)
block|{
if|if
condition|(
name|b
operator|!=
name|concat
index|[
name|idx
operator|++
index|]
condition|)
block|{
name|mismatch
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|mismatch
condition|)
break|break;
block|}
name|assertFalse
argument_list|(
literal|"File content of concatenated file is different"
argument_list|,
name|mismatch
argument_list|)
expr_stmt|;
block|}
comment|// test case when final block is not of a full length
annotation|@
name|Test
DECL|method|testConcatNotCompleteBlock ()
specifier|public
name|void
name|testConcatNotCompleteBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|trgFileLen
init|=
name|blockSize
operator|*
literal|3
decl_stmt|;
name|long
name|srcFileLen
init|=
name|blockSize
operator|*
literal|3
operator|+
literal|20
decl_stmt|;
comment|// block at the end - not full
comment|// create first file
name|String
name|name1
init|=
literal|"/trg"
decl_stmt|,
name|name2
init|=
literal|"/src"
decl_stmt|;
name|Path
name|filePath1
init|=
operator|new
name|Path
argument_list|(
name|name1
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|filePath1
argument_list|,
name|trgFileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|HdfsFileStatus
name|fStatus
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|name1
argument_list|)
decl_stmt|;
name|long
name|fileLen
init|=
name|fStatus
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|fileLen
argument_list|,
name|trgFileLen
argument_list|)
expr_stmt|;
comment|//read the file
name|FSDataInputStream
name|stm
init|=
name|dfs
operator|.
name|open
argument_list|(
name|filePath1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|byteFile1
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|trgFileLen
index|]
decl_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|byteFile1
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|LocatedBlocks
name|lb1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|name1
argument_list|,
literal|0
argument_list|,
name|trgFileLen
argument_list|)
decl_stmt|;
name|Path
name|filePath2
init|=
operator|new
name|Path
argument_list|(
name|name2
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|filePath2
argument_list|,
name|srcFileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fStatus
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|name2
argument_list|)
expr_stmt|;
name|fileLen
operator|=
name|fStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|srcFileLen
argument_list|,
name|fileLen
argument_list|)
expr_stmt|;
comment|// read the file
name|stm
operator|=
name|dfs
operator|.
name|open
argument_list|(
name|filePath2
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteFile2
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|srcFileLen
index|]
decl_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|byteFile2
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|LocatedBlocks
name|lb2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|name2
argument_list|,
literal|0
argument_list|,
name|srcFileLen
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"trg len="
operator|+
name|trgFileLen
operator|+
literal|"; src len="
operator|+
name|srcFileLen
argument_list|)
expr_stmt|;
comment|// move the blocks
name|dfs
operator|.
name|concat
argument_list|(
name|filePath1
argument_list|,
operator|new
name|Path
index|[]
block|{
name|filePath2
block|}
argument_list|)
expr_stmt|;
name|long
name|totalLen
init|=
name|trgFileLen
operator|+
name|srcFileLen
decl_stmt|;
name|fStatus
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|name1
argument_list|)
expr_stmt|;
name|fileLen
operator|=
name|fStatus
operator|.
name|getLen
argument_list|()
expr_stmt|;
comment|// read the resulting file
name|stm
operator|=
name|dfs
operator|.
name|open
argument_list|(
name|filePath1
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteFileConcat
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|fileLen
index|]
decl_stmt|;
name|stm
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|byteFileConcat
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
name|LocatedBlocks
name|lbConcat
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|name1
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
decl_stmt|;
comment|//verifications
comment|// 1. number of blocks
name|assertEquals
argument_list|(
name|lbConcat
operator|.
name|locatedBlockCount
argument_list|()
argument_list|,
name|lb1
operator|.
name|locatedBlockCount
argument_list|()
operator|+
name|lb2
operator|.
name|locatedBlockCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2. file lengths
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"file1 len="
operator|+
name|fileLen
operator|+
literal|"; total len="
operator|+
name|totalLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileLen
argument_list|,
name|totalLen
argument_list|)
expr_stmt|;
comment|// 3. removal of the src file
name|fStatus
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|name2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"File "
operator|+
name|name2
operator|+
literal|"still exists"
argument_list|,
name|fStatus
argument_list|)
expr_stmt|;
comment|// file shouldn't exist
comment|// 4. content
name|checkFileContent
argument_list|(
name|byteFileConcat
argument_list|,
operator|new
name|byte
index|[]
index|[]
block|{
name|byteFile1
block|,
name|byteFile2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * test illegal args cases    */
annotation|@
name|Test
DECL|method|testIllegalArg ()
specifier|public
name|void
name|testIllegalArg
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|fileLen
init|=
name|blockSize
operator|*
literal|3
decl_stmt|;
name|Path
name|parentDir
init|=
operator|new
name|Path
argument_list|(
literal|"/parentTrg"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dfs
operator|.
name|mkdirs
argument_list|(
name|parentDir
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|trg
init|=
operator|new
name|Path
argument_list|(
name|parentDir
argument_list|,
literal|"trg"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|trg
argument_list|,
name|fileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// must be in the same dir
block|{
comment|// create first file
name|Path
name|dir1
init|=
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dfs
operator|.
name|mkdirs
argument_list|(
name|dir1
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|src
init|=
operator|new
name|Path
argument_list|(
name|dir1
argument_list|,
literal|"src"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|src
argument_list|,
name|fileLen
argument_list|,
name|REPL_FACTOR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|concat
argument_list|(
name|trg
argument_list|,
operator|new
name|Path
index|[]
block|{
name|src
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"didn't fail for src and trg in different directories"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|// non existing file
try|try
block|{
name|dfs
operator|.
name|concat
argument_list|(
name|trg
argument_list|,
operator|new
name|Path
index|[]
block|{
operator|new
name|Path
argument_list|(
literal|"test1/a"
argument_list|)
block|}
argument_list|)
expr_stmt|;
comment|// non existing file
name|fail
argument_list|(
literal|"didn't fail with invalid arguments"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//expected
block|}
comment|// empty arg list
try|try
block|{
name|dfs
operator|.
name|concat
argument_list|(
name|trg
argument_list|,
operator|new
name|Path
index|[]
block|{}
argument_list|)
expr_stmt|;
comment|// empty array
name|fail
argument_list|(
literal|"didn't fail with invalid arguments"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// exspected
block|}
block|}
block|}
end_class

end_unit

