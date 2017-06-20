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
name|StringTokenizer
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
name|test
operator|.
name|GenericTestUtils
operator|.
name|LogCapturer
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
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Test to validate Azure storage client side logging. Tests works only when  * testing with Live Azure storage because Emulator does not have support for  * client-side logging.  *  */
end_comment

begin_class
DECL|class|TestNativeAzureFileSystemClientLogging
specifier|public
class|class
name|TestNativeAzureFileSystemClientLogging
extends|extends
name|AbstractWasbTestBase
block|{
DECL|field|testAccount
specifier|private
name|AzureBlobStorageTestAccount
name|testAccount
decl_stmt|;
comment|// Core-site config controlling Azure Storage Client logging
DECL|field|KEY_LOGGING_CONF_STRING
specifier|private
specifier|static
specifier|final
name|String
name|KEY_LOGGING_CONF_STRING
init|=
literal|"fs.azure.storage.client.logging"
decl_stmt|;
comment|// Temporary directory created using WASB.
DECL|field|TEMP_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TEMP_DIR
init|=
literal|"tempDir"
decl_stmt|;
comment|/*    * Helper method to verify the client logging is working. This check primarily    * checks to make sure we see a line in the logs corresponding to the entity    * that is created during test run.    */
DECL|method|verifyStorageClientLogs (String capturedLogs, String entity)
specifier|private
name|boolean
name|verifyStorageClientLogs
parameter_list|(
name|String
name|capturedLogs
parameter_list|,
name|String
name|entity
parameter_list|)
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
name|testAccount
operator|.
name|getRealAccount
argument_list|()
operator|.
name|getBlobEndpoint
argument_list|()
decl_stmt|;
name|String
name|container
init|=
name|testAccount
operator|.
name|getRealContainer
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|validateString
init|=
name|uri
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|container
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|entity
decl_stmt|;
name|boolean
name|entityFound
init|=
literal|false
decl_stmt|;
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|capturedLogs
argument_list|,
literal|"\n"
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|token
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|contains
argument_list|(
name|validateString
argument_list|)
condition|)
block|{
name|entityFound
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|entityFound
return|;
block|}
comment|/*    * Helper method that updates the core-site config to enable/disable logging.    */
DECL|method|updateFileSystemConfiguration (Boolean loggingFlag)
specifier|private
name|void
name|updateFileSystemConfiguration
parameter_list|(
name|Boolean
name|loggingFlag
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_LOGGING_CONF_STRING
argument_list|,
name|loggingFlag
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|fs
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// Using WASB code to communicate with Azure Storage.
DECL|method|performWASBOperations ()
specifier|private
name|void
name|performWASBOperations
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tempDir
init|=
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|SEPARATOR
operator|+
name|TEMP_DIR
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|tempDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoggingEnabled ()
specifier|public
name|void
name|testLoggingEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|LogCapturer
name|logs
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Logger
operator|.
name|ROOT_LOGGER_NAME
argument_list|)
argument_list|)
decl_stmt|;
comment|// Update configuration based on the Test.
name|updateFileSystemConfiguration
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|performWASBOperations
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|verifyStorageClientLogs
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
argument_list|,
name|TEMP_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoggingDisabled ()
specifier|public
name|void
name|testLoggingDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|LogCapturer
name|logs
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Logger
operator|.
name|ROOT_LOGGER_NAME
argument_list|)
argument_list|)
decl_stmt|;
comment|// Update configuration based on the Test.
name|updateFileSystemConfiguration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|performWASBOperations
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|verifyStorageClientLogs
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
argument_list|,
name|TEMP_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createTestAccount ()
specifier|protected
name|AzureBlobStorageTestAccount
name|createTestAccount
parameter_list|()
throws|throws
name|Exception
block|{
name|testAccount
operator|=
name|AzureBlobStorageTestAccount
operator|.
name|create
argument_list|()
expr_stmt|;
return|return
name|testAccount
return|;
block|}
block|}
end_class

end_unit

