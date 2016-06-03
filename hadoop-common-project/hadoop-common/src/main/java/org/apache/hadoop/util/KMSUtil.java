begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderFactory
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

begin_comment
comment|/**  * Utils for KMS.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSUtil
specifier|public
specifier|final
class|class
name|KMSUtil
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|KMSUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|KMSUtil ()
specifier|private
name|KMSUtil
parameter_list|()
block|{
comment|/* Hidden constructor */
block|}
comment|/**    * Creates a new KeyProvider from the given Configuration    * and configuration key name.    *    * @param conf Configuration    * @param configKeyName The configuration key name    * @return new KeyProvider, or null if no provider was found.    * @throws IOException if the KeyProvider is improperly specified in    *                             the Configuration    */
DECL|method|createKeyProvider (final Configuration conf, final String configKeyName)
specifier|public
specifier|static
name|KeyProvider
name|createKeyProvider
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|configKeyName
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating key provider with config key {}"
argument_list|,
name|configKeyName
argument_list|)
expr_stmt|;
specifier|final
name|String
name|providerUriStr
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|configKeyName
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|// No provider set in conf
if|if
condition|(
name|providerUriStr
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|URI
name|providerUri
decl_stmt|;
try|try
block|{
name|providerUri
operator|=
operator|new
name|URI
argument_list|(
name|providerUriStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|KeyProvider
name|keyProvider
init|=
name|KeyProviderFactory
operator|.
name|get
argument_list|(
name|providerUri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not instantiate KeyProvider from "
operator|+
name|configKeyName
operator|+
literal|" setting of '"
operator|+
name|providerUriStr
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|keyProvider
operator|.
name|isTransient
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"KeyProvider "
operator|+
name|keyProvider
operator|.
name|toString
argument_list|()
operator|+
literal|" was found but it is a transient provider."
argument_list|)
throw|;
block|}
return|return
name|keyProvider
return|;
block|}
block|}
end_class

end_unit

