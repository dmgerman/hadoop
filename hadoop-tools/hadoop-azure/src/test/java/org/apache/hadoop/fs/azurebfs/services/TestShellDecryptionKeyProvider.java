begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
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
operator|.
name|services
package|;
end_package

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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|azurebfs
operator|.
name|constants
operator|.
name|ConfigurationKeys
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
name|contracts
operator|.
name|exceptions
operator|.
name|KeyProviderException
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
name|Shell
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test ShellDecryptionKeyProvider.  *  */
end_comment

begin_class
DECL|class|TestShellDecryptionKeyProvider
specifier|public
class|class
name|TestShellDecryptionKeyProvider
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
name|TestShellDecryptionKeyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_ROOT_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"TestShellDecryptionKeyProvider"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testScriptPathNotSpecified ()
specifier|public
name|void
name|testScriptPathNotSpecified
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
return|return;
block|}
name|ShellDecryptionKeyProvider
name|provider
init|=
operator|new
name|ShellDecryptionKeyProvider
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|account
init|=
literal|"testacct"
decl_stmt|;
name|String
name|key
init|=
literal|"key"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
operator|+
name|account
argument_list|,
name|key
argument_list|)
expr_stmt|;
try|try
block|{
name|provider
operator|.
name|getStorageAccountKey
argument_list|(
name|account
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"fs.azure.shellkeyprovider.script is not specified, we should throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyProviderException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received an expected exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValidScript ()
specifier|public
name|void
name|testValidScript
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
return|return;
block|}
name|String
name|expectedResult
init|=
literal|"decretedKey"
decl_stmt|;
comment|// Create a simple script which echoes the given key plus the given
comment|// expected result (so that we validate both script input and output)
name|File
name|scriptFile
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"testScript.cmd"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
name|scriptFile
argument_list|,
literal|"@echo %1 "
operator|+
name|expectedResult
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|ShellDecryptionKeyProvider
name|provider
init|=
operator|new
name|ShellDecryptionKeyProvider
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|account
init|=
literal|"testacct"
decl_stmt|;
name|String
name|key
init|=
literal|"key1"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
operator|+
name|account
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|AZURE_KEY_ACCOUNT_SHELLKEYPROVIDER_SCRIPT
argument_list|,
literal|"cmd /c "
operator|+
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|provider
operator|.
name|getStorageAccountKey
argument_list|(
name|account
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|key
operator|+
literal|" "
operator|+
name|expectedResult
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

