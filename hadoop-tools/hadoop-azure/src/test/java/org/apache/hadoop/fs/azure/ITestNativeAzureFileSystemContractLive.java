begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeNotNull
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
name|FileSystemContractBaseTest
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
name|azure
operator|.
name|integration
operator|.
name|AzureTestConstants
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
name|azure
operator|.
name|integration
operator|.
name|AzureTestUtils
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
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_comment
comment|/**  * Run the {@link FileSystemContractBaseTest} test suite against azure storage.  */
end_comment

begin_class
DECL|class|ITestNativeAzureFileSystemContractLive
specifier|public
class|class
name|ITestNativeAzureFileSystemContractLive
extends|extends
name|FileSystemContractBaseTest
block|{
DECL|field|testAccount
specifier|private
name|AzureBlobStorageTestAccount
name|testAccount
decl_stmt|;
DECL|field|basePath
specifier|private
name|Path
name|basePath
decl_stmt|;
annotation|@
name|Rule
DECL|field|methodName
specifier|public
name|TestName
name|methodName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
DECL|method|nameThread ()
specifier|private
name|void
name|nameThread
parameter_list|()
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"JUnit-"
operator|+
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|nameThread
argument_list|()
expr_stmt|;
name|testAccount
operator|=
name|AzureBlobStorageTestAccount
operator|.
name|create
argument_list|()
expr_stmt|;
if|if
condition|(
name|testAccount
operator|!=
literal|null
condition|)
block|{
name|fs
operator|=
name|testAccount
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
name|assumeNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|basePath
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|AzureTestUtils
operator|.
name|createTestPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"NativeAzureFileSystemContractLive"
argument_list|)
argument_list|)
argument_list|)
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|testAccount
operator|=
name|AzureTestUtils
operator|.
name|cleanup
argument_list|(
name|testAccount
argument_list|)
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTestBaseDir ()
specifier|public
name|Path
name|getTestBaseDir
parameter_list|()
block|{
return|return
name|basePath
return|;
block|}
DECL|method|getGlobalTimeout ()
specifier|protected
name|int
name|getGlobalTimeout
parameter_list|()
block|{
return|return
name|AzureTestConstants
operator|.
name|AZURE_TEST_TIMEOUT
return|;
block|}
comment|/**    * The following tests are failing on Azure and the Azure     * file system code needs to be modified to make them pass.    * A separate work item has been opened for this.    */
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testMoveFileUnderParent ()
specifier|public
name|void
name|testMoveFileUnderParent
parameter_list|()
throws|throws
name|Throwable
block|{   }
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testRenameFileToSelf ()
specifier|public
name|void
name|testRenameFileToSelf
parameter_list|()
throws|throws
name|Throwable
block|{   }
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testRenameChildDirForbidden ()
specifier|public
name|void
name|testRenameChildDirForbidden
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testMoveDirUnderParent ()
specifier|public
name|void
name|testMoveDirUnderParent
parameter_list|()
throws|throws
name|Throwable
block|{   }
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testRenameDirToSelf ()
specifier|public
name|void
name|testRenameDirToSelf
parameter_list|()
throws|throws
name|Throwable
block|{   }
block|}
end_class

end_unit

