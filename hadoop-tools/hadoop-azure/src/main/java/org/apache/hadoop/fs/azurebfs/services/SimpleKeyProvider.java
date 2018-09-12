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
name|azurebfs
operator|.
name|AbfsConfiguration
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
name|fs
operator|.
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|InvalidConfigurationValueException
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
comment|/**  * Key provider that simply returns the storage account key from the  * configuration as plaintext.  */
end_comment

begin_class
DECL|class|SimpleKeyProvider
specifier|public
class|class
name|SimpleKeyProvider
implements|implements
name|KeyProvider
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SimpleKeyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getStorageAccountKey (String accountName, Configuration rawConfig)
specifier|public
name|String
name|getStorageAccountKey
parameter_list|(
name|String
name|accountName
parameter_list|,
name|Configuration
name|rawConfig
parameter_list|)
throws|throws
name|KeyProviderException
block|{
name|String
name|key
init|=
literal|null
decl_stmt|;
try|try
block|{
name|AbfsConfiguration
name|abfsConfig
init|=
operator|new
name|AbfsConfiguration
argument_list|(
name|rawConfig
argument_list|,
name|accountName
argument_list|)
decl_stmt|;
name|key
operator|=
name|abfsConfig
operator|.
name|getPasswordString
argument_list|(
name|ConfigurationKeys
operator|.
name|FS_AZURE_ACCOUNT_KEY_PROPERTY_NAME
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvalidConfigurationValueException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|KeyProviderException
argument_list|(
literal|"Failure to initialize configuration"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to get key from credential providers. {}"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
return|return
name|key
return|;
block|}
block|}
end_class

end_unit

