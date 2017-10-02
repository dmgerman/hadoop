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
name|FileNotFoundException
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
name|FileContext
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
name|RemoteIterator
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
name|contract
operator|.
name|ContractTestUtils
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
name|ipc
operator|.
name|RemoteException
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
comment|/**  * This class tests the FileStatus API.  */
end_comment

begin_class
DECL|class|TestFileStatus
specifier|public
class|class
name|TestFileStatus
block|{
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|FSNamesystem
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|TRACE
parameter_list|)
constructor_decl|;
name|GenericTestUtils
operator|.
name|setLogLevel
parameter_list|(
name|FileSystem
operator|.
name|LOG
parameter_list|,
name|Level
operator|.
name|TRACE
parameter_list|)
constructor_decl|;
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
DECL|field|fileSize
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|16384
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
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
name|FileSystem
name|fs
decl_stmt|;
DECL|field|fc
specifier|private
specifier|static
name|FileContext
name|fc
decl_stmt|;
DECL|field|dfsClient
specifier|private
specifier|static
name|DFSClient
name|dfsClient
decl_stmt|;
DECL|field|file1
specifier|private
specifier|static
name|Path
name|file1
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|testSetUp ()
specifier|public
specifier|static
name|void
name|testSetUp
parameter_list|()
throws|throws
name|Exception
block|{
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
name|DFS_LIST_LIMIT
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
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|dfsClient
operator|=
operator|new
name|DFSClient
argument_list|(
name|DFSUtilClient
operator|.
name|getNNAddress
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|file1
operator|=
operator|new
name|Path
argument_list|(
literal|"filestatus.dat"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
name|fileSize
argument_list|,
name|fileSize
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|testTearDown ()
specifier|public
specifier|static
name|void
name|testTearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
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
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkFile (FileSystem fileSys, Path name, int repl)
specifier|private
name|void
name|checkFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|,
name|int
name|repl
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fileSys
argument_list|,
name|name
argument_list|,
operator|(
name|short
operator|)
name|repl
argument_list|)
expr_stmt|;
block|}
comment|/** Test calling getFileInfo directly on the client */
annotation|@
name|Test
DECL|method|testGetFileInfo ()
specifier|public
name|void
name|testGetFileInfo
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Check that / exists
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"/ should be a directory"
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// Make sure getFileInfo returns null for files which do not exist
name|HdfsFileStatus
name|fileInfo
init|=
name|dfsClient
operator|.
name|getFileInfo
argument_list|(
literal|"/noSuchFile"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Non-existant file should result in null"
argument_list|,
literal|null
argument_list|,
name|fileInfo
argument_list|)
expr_stmt|;
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"/name1"
argument_list|)
decl_stmt|;
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
literal|"/name1/name2"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|mkdirs
argument_list|(
name|path1
argument_list|)
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|path2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|fileInfo
operator|=
name|dfsClient
operator|.
name|getFileInfo
argument_list|(
name|path1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fileInfo
operator|.
name|getChildrenNum
argument_list|()
argument_list|)
expr_stmt|;
name|fileInfo
operator|=
name|dfsClient
operator|.
name|getFileInfo
argument_list|(
name|path2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fileInfo
operator|.
name|getChildrenNum
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test getFileInfo throws the right exception given a non-absolute path.
try|try
block|{
name|dfsClient
operator|.
name|getFileInfo
argument_list|(
literal|"non-absolute"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"getFileInfo for a non-absolute path did not throw IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Wrong exception for invalid file name: "
operator|+
name|re
argument_list|,
name|re
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Absolute path required"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test the FileStatus obtained calling getFileStatus on a file */
annotation|@
name|Test
DECL|method|testGetFileStatusOnFile ()
specifier|public
name|void
name|testGetFileStatusOnFile
parameter_list|()
throws|throws
name|Exception
block|{
name|checkFile
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// test getFileStatus on a file
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|file1
operator|+
literal|" should be a file"
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockSize
argument_list|,
name|status
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file1
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|file1
operator|+
literal|" should have erasure coding unset in "
operator|+
literal|"FileStatus#toString(): "
operator|+
name|status
argument_list|,
name|status
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"isErasureCoded=false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test the FileStatus obtained calling listStatus on a file */
annotation|@
name|Test
DECL|method|testListStatusOnFile ()
specifier|public
name|void
name|testListStatusOnFile
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|stats
index|[
literal|0
index|]
decl_stmt|;
name|assertFalse
argument_list|(
name|file1
operator|+
literal|" should be a file"
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockSize
argument_list|,
name|status
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|file1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file1
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|itor
init|=
name|fc
operator|.
name|listStatus
argument_list|(
name|file1
argument_list|)
decl_stmt|;
name|status
operator|=
name|itor
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
index|[
literal|0
index|]
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|file1
operator|+
literal|" should be a file"
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test getting a FileStatus object using a non-existant path */
annotation|@
name|Test
DECL|method|testGetFileStatusOnNonExistantFileDir ()
specifier|public
name|void
name|testGetFileStatusOnNonExistantFileDir
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/test/mkdirs"
argument_list|)
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"listStatus of non-existent path should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fe
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"File "
operator|+
name|dir
operator|+
literal|" does not exist."
argument_list|,
name|fe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|fc
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"listStatus of non-existent path should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fe
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"File "
operator|+
name|dir
operator|+
literal|" does not exist."
argument_list|,
name|fe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"getFileStatus of non-existent path should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fe
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Exception doesn't indicate non-existant path"
argument_list|,
name|fe
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"File does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test FileStatus objects obtained from a directory */
annotation|@
name|Test
DECL|method|testGetFileStatusOnDir ()
specifier|public
name|void
name|testGetFileStatusOnDir
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create the directory
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/test/mkdirs"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"mkdir failed"
argument_list|,
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"mkdir failed"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
comment|// test getFileStatus on an empty directory
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|+
literal|" should be a directory"
argument_list|,
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|+
literal|" should be zero size "
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertNotErasureCoded
argument_list|(
name|fs
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test listStatus on an empty directory
name|FileStatus
index|[]
name|stats
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|+
literal|" should be empty"
argument_list|,
literal|0
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|+
literal|" should be zero size "
argument_list|,
literal|0
argument_list|,
name|fs
operator|.
name|getContentSummary
argument_list|(
name|dir
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|itor
init|=
name|fc
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|dir
operator|+
literal|" should be empty"
argument_list|,
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listStatusIterator
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dir
operator|+
literal|" should be empty"
argument_list|,
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// create another file that is smaller than a block.
name|Path
name|file2
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"filestatus2.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|,
name|blockSize
operator|/
literal|4
argument_list|,
name|blockSize
operator|/
literal|4
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fs
argument_list|,
name|file2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// verify file attributes
name|status
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|file2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blockSize
argument_list|,
name|status
operator|.
name|getBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|status
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|file2
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|file2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create another file in the same directory
name|Path
name|file3
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"filestatus3.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file3
argument_list|,
name|blockSize
operator|/
literal|4
argument_list|,
name|blockSize
operator|/
literal|4
argument_list|,
name|blockSize
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|seed
argument_list|)
expr_stmt|;
name|checkFile
argument_list|(
name|fs
argument_list|,
name|file3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|file3
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|file3
argument_list|)
expr_stmt|;
comment|// Verify that the size of the directory increased by the size
comment|// of the two files
specifier|final
name|int
name|expected
init|=
name|blockSize
operator|/
literal|2
decl_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|+
literal|" size should be "
operator|+
name|expected
argument_list|,
name|expected
argument_list|,
name|fs
operator|.
name|getContentSummary
argument_list|(
name|dir
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test listStatus on a non-empty directory
name|stats
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|+
literal|" should have two entries"
argument_list|,
literal|2
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|1
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fc
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Unexpected addtional file"
argument_list|,
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listStatusIterator
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Unexpected addtional file"
argument_list|,
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test iterative listing. Now dir has 2 entries, create one more.
name|Path
name|dir3
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"dir3"
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir3
argument_list|)
expr_stmt|;
name|dir3
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|dir3
argument_list|)
expr_stmt|;
name|stats
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|+
literal|" should have three entries"
argument_list|,
literal|3
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir3
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|1
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|2
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fc
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Unexpected addtional file"
argument_list|,
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listStatusIterator
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Unexpected addtional file"
argument_list|,
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now dir has 3 entries, create two more
name|Path
name|dir4
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"dir4"
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir4
argument_list|)
expr_stmt|;
name|dir4
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|dir4
argument_list|)
expr_stmt|;
name|Path
name|dir5
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"dir5"
argument_list|)
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir5
argument_list|)
expr_stmt|;
name|dir5
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|dir5
argument_list|)
expr_stmt|;
name|stats
operator|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|+
literal|" should have five entries"
argument_list|,
literal|5
argument_list|,
name|stats
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir3
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir4
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|1
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir5
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|2
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|3
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|stats
index|[
literal|4
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fc
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir4
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir5
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listStatusIterator
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir4
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir5
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file2
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|itor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listStatusIterator
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir3
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir4
operator|.
name|toString
argument_list|()
argument_list|,
name|itor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
operator|.
name|getParent
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|itor
operator|.
name|hasNext
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"FileNotFoundException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{     }
name|fs
operator|.
name|mkdirs
argument_list|(
name|file2
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir3
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir4
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir5
argument_list|)
expr_stmt|;
name|itor
operator|=
name|fs
operator|.
name|listStatusIterator
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|delete
argument_list|(
name|dir
operator|.
name|getParent
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|itor
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"FileNotFoundException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{     }
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

