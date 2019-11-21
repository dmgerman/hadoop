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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|FsShell
import|;
end_import

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
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
operator|.
name|AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
import|;
end_import

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
name|azurebfs
operator|.
name|constants
operator|.
name|FileSystemUriSchemes
operator|.
name|ABFS_SCHEME
import|;
end_import

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
name|azurebfs
operator|.
name|constants
operator|.
name|TestConfigurationKeys
operator|.
name|FS_AZURE_ABFS_ACCOUNT_NAME
import|;
end_import

begin_comment
comment|/**  * Tests for Azure Blob FileSystem CLI.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemCLI
specifier|public
class|class
name|ITestAzureBlobFileSystemCLI
extends|extends
name|AbstractAbfsIntegrationTest
block|{
DECL|method|ITestAzureBlobFileSystemCLI ()
specifier|public
name|ITestAzureBlobFileSystemCLI
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test for HADOOP-16138: hadoop fs mkdir / of nonexistent abfs    * container raises NPE.    *    * The command should return with 1 exit status, but there should be no NPE.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testMkdirRootNonExistentContainer ()
specifier|public
name|void
name|testMkdirRootNonExistentContainer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|rawConf
init|=
name|getRawConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|account
init|=
name|rawConf
operator|.
name|get
argument_list|(
name|FS_AZURE_ABFS_ACCOUNT_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rawConf
operator|.
name|setBoolean
argument_list|(
name|AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|nonExistentContainer
init|=
literal|"nonexistent-"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|FsShell
name|fsShell
init|=
operator|new
name|FsShell
argument_list|(
name|rawConf
argument_list|)
decl_stmt|;
name|int
name|result
init|=
name|fsShell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-mkdir"
block|,
name|ABFS_SCHEME
operator|+
literal|"://"
operator|+
name|nonExistentContainer
operator|+
literal|"@"
operator|+
name|account
operator|+
literal|"/"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

