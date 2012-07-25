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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|InvalidPathException
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
name|ParentNotDirectoryException
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
name|permission
operator|.
name|FsPermission
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
name|NamenodeProtocol
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
name|NamenodeProtocols
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
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
comment|/**  * This class tests that the DFS command mkdirs only creates valid  * directories, and generally behaves as expected.  */
end_comment

begin_class
DECL|class|TestDFSMkdirs
specifier|public
class|class
name|TestDFSMkdirs
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|NON_CANONICAL_PATHS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|NON_CANONICAL_PATHS
init|=
operator|new
name|String
index|[]
block|{
literal|"//test1"
block|,
literal|"/test2/.."
block|,
literal|"/test2//bar"
block|,
literal|"/test2/../test4"
block|,
literal|"/test5/."
block|}
decl_stmt|;
comment|/**    * Tests mkdirs can create a directory that does not exist and will    * not create a subdirectory off a file. Regression test for HADOOP-281.    */
annotation|@
name|Test
DECL|method|testDFSMkdirs ()
specifier|public
name|void
name|testDFSMkdirs
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|1
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
comment|// First create a new directory with mkdirs
name|Path
name|myPath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/mkdirs"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|myPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|myPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|myPath
argument_list|)
argument_list|)
expr_stmt|;
comment|// Second, create a file in that directory.
name|Path
name|myFile
init|=
operator|new
name|Path
argument_list|(
literal|"/test/mkdirs/myFile"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fileSys
argument_list|,
name|myFile
argument_list|,
literal|"hello world"
argument_list|)
expr_stmt|;
comment|// Third, use mkdir to create a subdirectory off of that file,
comment|// and check that it fails.
name|Path
name|myIllegalPath
init|=
operator|new
name|Path
argument_list|(
literal|"/test/mkdirs/myFile/subdir"
argument_list|)
decl_stmt|;
name|Boolean
name|exist
init|=
literal|true
decl_stmt|;
try|try
block|{
name|fileSys
operator|.
name|mkdirs
argument_list|(
name|myIllegalPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exist
operator|=
literal|false
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|exist
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fileSys
operator|.
name|exists
argument_list|(
name|myIllegalPath
argument_list|)
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|myFile
argument_list|,
literal|true
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
comment|/**    * Tests mkdir will not create directory when parent is missing.    */
annotation|@
name|Test
DECL|method|testMkdir ()
specifier|public
name|void
name|testMkdir
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
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Create a dir in root dir, should succeed
name|assertTrue
argument_list|(
name|dfs
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/mkdir-"
operator|+
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create a dir when parent dir exists as a file, should fail
name|IOException
name|expectedException
init|=
literal|null
decl_stmt|;
name|String
name|filePath
init|=
literal|"/mkdir-file-"
operator|+
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
name|filePath
argument_list|)
argument_list|,
literal|"hello world"
argument_list|)
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
name|filePath
operator|+
literal|"/mkdir"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|expectedException
operator|=
name|e
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Create a directory when parent dir exists as file using"
operator|+
literal|" mkdir() should throw ParentNotDirectoryException "
argument_list|,
name|expectedException
operator|!=
literal|null
operator|&&
name|expectedException
operator|instanceof
name|ParentNotDirectoryException
argument_list|)
expr_stmt|;
comment|// Create a dir in a non-exist directory, should fail
name|expectedException
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|dfs
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/non-exist/mkdir-"
operator|+
name|Time
operator|.
name|now
argument_list|()
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|expectedException
operator|=
name|e
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Create a directory in a non-exist parent dir using"
operator|+
literal|" mkdir() should throw FileNotFoundException "
argument_list|,
name|expectedException
operator|!=
literal|null
operator|&&
name|expectedException
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dfs
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
comment|/**    * Regression test for HDFS-3626. Creates a file using a non-canonical path    * (i.e. with extra slashes between components) and makes sure that the NN    * rejects it.    */
annotation|@
name|Test
DECL|method|testMkdirRpcNonCanonicalPath ()
specifier|public
name|void
name|testMkdirRpcNonCanonicalPath
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NamenodeProtocols
name|nnrpc
init|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pathStr
range|:
name|NON_CANONICAL_PATHS
control|)
block|{
try|try
block|{
name|nnrpc
operator|.
name|mkdirs
argument_list|(
name|pathStr
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0755
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail when called with a non-canonicalized path: "
operator|+
name|pathStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidPathException
name|ipe
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
finally|finally
block|{
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

