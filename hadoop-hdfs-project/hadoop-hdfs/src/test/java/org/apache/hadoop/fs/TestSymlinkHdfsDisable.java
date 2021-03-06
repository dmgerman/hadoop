begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|Test
import|;
end_import

begin_class
DECL|class|TestSymlinkHdfsDisable
specifier|public
class|class
name|TestSymlinkHdfsDisable
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSymlinkHdfsDisable ()
specifier|public
name|void
name|testSymlinkHdfsDisable
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
comment|// disable symlink resolution
name|conf
operator|.
name|setBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|FS_CLIENT_RESOLVE_REMOTE_SYMLINKS_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// spin up minicluster, get dfs and filecontext
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
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FileContext
name|fc
init|=
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
decl_stmt|;
comment|// Create test files/links
name|FileContextTestHelper
name|helper
init|=
operator|new
name|FileContextTestHelper
argument_list|(
literal|"/tmp/TestSymlinkHdfsDisable"
argument_list|)
decl_stmt|;
name|Path
name|root
init|=
name|helper
operator|.
name|getTestRootPath
argument_list|(
name|fc
argument_list|)
decl_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"target"
argument_list|)
decl_stmt|;
name|Path
name|link
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"link"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|target
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0xDEADDEAD
argument_list|)
expr_stmt|;
name|fc
operator|.
name|createSymlink
argument_list|(
name|target
argument_list|,
name|link
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Try to resolve links with FileSystem and FileContext
try|try
block|{
name|fc
operator|.
name|open
argument_list|(
name|link
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected error when attempting to resolve link"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"resolution is disabled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dfs
operator|.
name|open
argument_list|(
name|link
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected error when attempting to resolve link"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"resolution is disabled"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

