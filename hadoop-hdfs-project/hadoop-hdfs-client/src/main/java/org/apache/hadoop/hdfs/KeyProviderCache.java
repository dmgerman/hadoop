begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|RemovalNotification
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KeyProviderCache
specifier|public
class|class
name|KeyProviderCache
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
name|KeyProviderCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|URI
argument_list|,
name|KeyProvider
argument_list|>
name|cache
decl_stmt|;
DECL|method|KeyProviderCache (long expiryMs)
specifier|public
name|KeyProviderCache
parameter_list|(
name|long
name|expiryMs
parameter_list|)
block|{
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterAccess
argument_list|(
name|expiryMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|removalListener
argument_list|(
operator|new
name|RemovalListener
argument_list|<
name|URI
argument_list|,
name|KeyProvider
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
annotation|@
name|Nonnull
name|RemovalNotification
argument_list|<
name|URI
argument_list|,
name|KeyProvider
argument_list|>
name|notification
parameter_list|)
block|{
try|try
block|{
assert|assert
name|notification
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
assert|;
name|notification
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error closing KeyProvider with uri ["
operator|+
name|notification
operator|.
name|getKey
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|get (final Configuration conf)
specifier|public
name|KeyProvider
name|get
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
name|URI
name|kpURI
init|=
name|createKeyProviderURI
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|kpURI
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|kpURI
argument_list|,
operator|new
name|Callable
argument_list|<
name|KeyProvider
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyProvider
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|DFSUtilClient
operator|.
name|createKeyProvider
argument_list|(
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not create KeyProvider for DFSClient !!"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|createKeyProviderURI (Configuration conf)
specifier|private
name|URI
name|createKeyProviderURI
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
specifier|final
name|String
name|providerUriStr
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_ENCRYPTION_KEY_PROVIDER_URI
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not find uri with key ["
operator|+
name|HdfsClientConfigKeys
operator|.
name|DFS_ENCRYPTION_KEY_PROVIDER_URI
operator|+
literal|"] to create a keyProvider !!"
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"KeyProvider URI string is invalid ["
operator|+
name|providerUriStr
operator|+
literal|"]!!"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|providerUri
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setKeyProvider (Configuration conf, KeyProvider keyProvider)
specifier|public
name|void
name|setKeyProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|KeyProvider
name|keyProvider
parameter_list|)
block|{
name|URI
name|uri
init|=
name|createKeyProviderURI
argument_list|(
name|conf
argument_list|)
decl_stmt|;
assert|assert
name|uri
operator|!=
literal|null
assert|;
name|cache
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|keyProvider
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

