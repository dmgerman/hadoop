begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.alias
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|alias
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|CommonConfigurationKeysPublic
import|;
end_import

begin_comment
comment|/**  * A factory to create a list of CredentialProvider based on the path given in a  * Configuration. It uses a service loader interface to find the available  * CredentialProviders and create them based on the list of URIs.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|CredentialProviderFactory
specifier|public
specifier|abstract
class|class
name|CredentialProviderFactory
block|{
DECL|field|CREDENTIAL_PROVIDER_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CREDENTIAL_PROVIDER_PATH
init|=
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_CREDENTIAL_PROVIDER_PATH
decl_stmt|;
DECL|method|createProvider (URI providerName, Configuration conf )
specifier|public
specifier|abstract
name|CredentialProvider
name|createProvider
parameter_list|(
name|URI
name|providerName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|field|serviceLoader
specifier|private
specifier|static
specifier|final
name|ServiceLoader
argument_list|<
name|CredentialProviderFactory
argument_list|>
name|serviceLoader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|CredentialProviderFactory
operator|.
name|class
argument_list|,
name|CredentialProviderFactory
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|getProviders (Configuration conf )
specifier|public
specifier|static
name|List
argument_list|<
name|CredentialProvider
argument_list|>
name|getProviders
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|CredentialProvider
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|CredentialProvider
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|conf
operator|.
name|getStringCollection
argument_list|(
name|CREDENTIAL_PROVIDER_PATH
argument_list|)
control|)
block|{
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|CredentialProviderFactory
name|factory
range|:
name|serviceLoader
control|)
block|{
name|CredentialProvider
name|kp
init|=
name|factory
operator|.
name|createProvider
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|kp
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|kp
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No CredentialProviderFactory for "
operator|+
name|uri
operator|+
literal|" in "
operator|+
name|CREDENTIAL_PROVIDER_PATH
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|error
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Bad configuration of "
operator|+
name|CREDENTIAL_PROVIDER_PATH
operator|+
literal|" at "
operator|+
name|path
argument_list|,
name|error
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

