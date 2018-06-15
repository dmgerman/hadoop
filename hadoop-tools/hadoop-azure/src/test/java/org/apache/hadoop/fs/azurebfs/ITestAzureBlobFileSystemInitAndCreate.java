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
name|io
operator|.
name|FileNotFoundException
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
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
import|;
end_import

begin_comment
comment|/**  * Test filesystem initialization and creation.  */
end_comment

begin_class
DECL|class|ITestAzureBlobFileSystemInitAndCreate
specifier|public
class|class
name|ITestAzureBlobFileSystemInitAndCreate
extends|extends
name|DependencyInjectedTest
block|{
DECL|method|ITestAzureBlobFileSystemInitAndCreate ()
specifier|public
name|ITestAzureBlobFileSystemInitAndCreate
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|getConfiguration
argument_list|()
operator|.
name|unset
argument_list|(
name|ConfigurationKeys
operator|.
name|AZURE_CREATE_REMOTE_FILESYSTEM_DURING_INITIALIZATION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|testCleanup ()
specifier|public
name|void
name|testCleanup
parameter_list|()
block|{   }
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileNotFoundException
operator|.
name|class
argument_list|)
DECL|method|ensureFilesystemWillNotBeCreatedIfCreationConfigIsNotSet ()
specifier|public
name|void
name|ensureFilesystemWillNotBeCreatedIfCreationConfigIsNotSet
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|this
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

