begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl.live
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
operator|.
name|live
package|;
end_package

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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|net
operator|.
name|URI
import|;
end_import

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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Shell
operator|.
name|WINDOWS
import|;
end_import

begin_comment
comment|/**  * Run collection of tests for the {@link FileContext}.  */
end_comment

begin_class
DECL|class|TestAdlFileContextMainOperationsLive
specifier|public
class|class
name|TestAdlFileContextMainOperationsLive
extends|extends
name|FileContextMainOperationsBaseTest
block|{
DECL|field|KEY_FILE_SYSTEM
specifier|private
specifier|static
specifier|final
name|String
name|KEY_FILE_SYSTEM
init|=
literal|"test.fs.adl.name"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|skipTestCheck ()
specifier|public
specifier|static
name|void
name|skipTestCheck
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|AdlStorageConfiguration
operator|.
name|isContractTestEnabled
argument_list|()
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
name|Configuration
name|conf
init|=
name|AdlStorageConfiguration
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|String
name|fileSystem
init|=
name|conf
operator|.
name|get
argument_list|(
name|KEY_FILE_SYSTEM
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileSystem
operator|==
literal|null
operator|||
name|fileSystem
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Default file system not configured."
argument_list|)
throw|;
block|}
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|fileSystem
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|AdlStorageConfiguration
operator|.
name|createStorageConnector
argument_list|()
decl_stmt|;
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
operator|new
name|DelegateToFileSystem
argument_list|(
name|uri
argument_list|,
name|fs
argument_list|,
name|conf
argument_list|,
name|fs
operator|.
name|getScheme
argument_list|()
argument_list|,
literal|false
argument_list|)
block|{         }
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createFileContextHelper ()
specifier|protected
name|FileContextTestHelper
name|createFileContextHelper
parameter_list|()
block|{
comment|// On Windows, root directory path is created from local running directory.
comment|// Adl does not support ':' as part of the path which results in failure.
comment|//    return new FileContextTestHelper(GenericTestUtils
comment|// .getRandomizedTestDir()
comment|//        .getAbsolutePath().replaceAll(":",""));
return|return
operator|new
name|FileContextTestHelper
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listCorruptedBlocksSupported ()
specifier|protected
name|boolean
name|listCorruptedBlocksSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|testWorkingDirectory ()
specifier|public
name|void
name|testWorkingDirectory
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|WINDOWS
condition|)
block|{
comment|// TODO :Fix is required in Hadoop shell to support windows permission
comment|// set.
comment|// The test is failing with NPE on windows platform only, with Linux
comment|// platform test passes.
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|testWorkingDirectory
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|testUnsupportedSymlink ()
specifier|public
name|void
name|testUnsupportedSymlink
parameter_list|()
throws|throws
name|IOException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetVerifyChecksum ()
specifier|public
name|void
name|testSetVerifyChecksum
parameter_list|()
throws|throws
name|IOException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

