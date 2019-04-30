begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|FSMainOperationsBaseTest
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
name|azurebfs
operator|.
name|contract
operator|.
name|ABFSContractTestBinding
import|;
end_import

begin_comment
comment|/**  * Test AzureBlobFileSystem main operations.  * */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemMainOperation
specifier|public
class|class
name|ITestAzureBlobFileSystemMainOperation
extends|extends
name|FSMainOperationsBaseTest
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ROOT_DIR
init|=
literal|"/tmp/TestAzureBlobFileSystemMainOperations"
decl_stmt|;
DECL|field|binding
specifier|private
specifier|final
name|ABFSContractTestBinding
name|binding
decl_stmt|;
DECL|method|ITestAzureBlobFileSystemMainOperation ()
specifier|public
name|ITestAzureBlobFileSystemMainOperation
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
comment|// Note: There are shared resources in this test suite (eg: "test/new/newfile")
comment|// To make sure this test suite can be ran in parallel, different containers
comment|// will be used for each test.
name|binding
operator|=
operator|new
name|ABFSContractTestBinding
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|binding
operator|.
name|setup
argument_list|()
expr_stmt|;
name|fSys
operator|=
name|binding
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Note: Because "tearDown()" is called during the testing,
comment|// here we should not call binding.tearDown() to destroy the container.
comment|// Instead we should remove the test containers manually with
comment|// AbfsTestUtils.
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createFileSystem ()
specifier|protected
name|FileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|fSys
return|;
block|}
annotation|@
name|Override
annotation|@
name|Ignore
argument_list|(
literal|"Permission check for getFileInfo doesn't match the HdfsPermissionsGuide"
argument_list|)
DECL|method|testListStatusThrowsExceptionForUnreadableDir ()
specifier|public
name|void
name|testListStatusThrowsExceptionForUnreadableDir
parameter_list|()
block|{
comment|// Permission Checks:
comment|// https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html
block|}
annotation|@
name|Override
annotation|@
name|Ignore
argument_list|(
literal|"Permission check for getFileInfo doesn't match the HdfsPermissionsGuide"
argument_list|)
DECL|method|testGlobStatusThrowsExceptionForUnreadableDir ()
specifier|public
name|void
name|testGlobStatusThrowsExceptionForUnreadableDir
parameter_list|()
block|{
comment|// Permission Checks:
comment|// https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html
block|}
block|}
end_class

end_unit

