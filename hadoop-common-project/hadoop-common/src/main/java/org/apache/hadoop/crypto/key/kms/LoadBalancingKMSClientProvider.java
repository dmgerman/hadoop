begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms
package|package
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
name|kms
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
name|io
operator|.
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|KeyProviderCryptoExtension
operator|.
name|CryptoExtension
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
name|KeyProviderCryptoExtension
operator|.
name|EncryptedKeyVersion
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
name|KeyProviderDelegationTokenExtension
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
operator|.
name|RetryAction
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
name|security
operator|.
name|AccessControlException
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
name|security
operator|.
name|Credentials
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|Time
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * A simple LoadBalancing KMSClientProvider that round-robins requests  * across a provided array of KMSClientProviders. It also retries failed  * requests on the next available provider in the load balancer group. It  * only retries failed requests that result in an IOException, sending back  * all other Exceptions to the caller without retry.  */
end_comment

begin_class
DECL|class|LoadBalancingKMSClientProvider
specifier|public
class|class
name|LoadBalancingKMSClientProvider
extends|extends
name|KeyProvider
implements|implements
name|CryptoExtension
implements|,
name|KeyProviderDelegationTokenExtension
operator|.
name|DelegationTokenExtension
block|{
DECL|field|LOG
specifier|public
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LoadBalancingKMSClientProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|ProviderCallable
specifier|static
interface|interface
name|ProviderCallable
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|call (KMSClientProvider provider)
specifier|public
name|T
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
throws|,
name|Exception
function_decl|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|WrapperException
specifier|static
class|class
name|WrapperException
extends|extends
name|RuntimeException
block|{
DECL|method|WrapperException (Throwable cause)
specifier|public
name|WrapperException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|providers
specifier|private
specifier|final
name|KMSClientProvider
index|[]
name|providers
decl_stmt|;
DECL|field|currentIdx
specifier|private
specifier|final
name|AtomicInteger
name|currentIdx
decl_stmt|;
DECL|field|retryPolicy
specifier|private
name|RetryPolicy
name|retryPolicy
init|=
literal|null
decl_stmt|;
DECL|method|LoadBalancingKMSClientProvider (KMSClientProvider[] providers, Configuration conf)
specifier|public
name|LoadBalancingKMSClientProvider
parameter_list|(
name|KMSClientProvider
index|[]
name|providers
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|shuffle
argument_list|(
name|providers
argument_list|)
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|LoadBalancingKMSClientProvider (KMSClientProvider[] providers, long seed, Configuration conf)
name|LoadBalancingKMSClientProvider
parameter_list|(
name|KMSClientProvider
index|[]
name|providers
parameter_list|,
name|long
name|seed
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|providers
operator|=
name|providers
expr_stmt|;
name|this
operator|.
name|currentIdx
operator|=
operator|new
name|AtomicInteger
argument_list|(
call|(
name|int
call|)
argument_list|(
name|seed
operator|%
name|providers
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|maxNumRetries
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY
argument_list|,
name|providers
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|sleepBaseMillis
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_KEY
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|sleepMaxMillis
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_FAILOVER_SLEEP_MAX_MILLIS_KEY
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_FAILOVER_SLEEP_MAX_MILLIS_DEFAULT
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|maxNumRetries
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|sleepBaseMillis
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|sleepMaxMillis
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|retryPolicy
operator|=
name|RetryPolicies
operator|.
name|failoverOnNetworkException
argument_list|(
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
argument_list|,
name|maxNumRetries
argument_list|,
literal|0
argument_list|,
name|sleepBaseMillis
argument_list|,
name|sleepMaxMillis
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProviders ()
specifier|public
name|KMSClientProvider
index|[]
name|getProviders
parameter_list|()
block|{
return|return
name|providers
return|;
block|}
DECL|method|doOp (ProviderCallable<T> op, int currPos)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|doOp
parameter_list|(
name|ProviderCallable
argument_list|<
name|T
argument_list|>
name|op
parameter_list|,
name|int
name|currPos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|providers
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No providers configured !"
argument_list|)
throw|;
block|}
name|IOException
name|ex
init|=
literal|null
decl_stmt|;
name|int
name|numFailovers
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
condition|;
name|i
operator|++
operator|,
name|numFailovers
operator|++
control|)
block|{
name|KMSClientProvider
name|provider
init|=
name|providers
index|[
operator|(
name|currPos
operator|+
name|i
operator|)
operator|%
name|providers
operator|.
name|length
index|]
decl_stmt|;
try|try
block|{
return|return
name|op
operator|.
name|call
argument_list|(
name|provider
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ace
parameter_list|)
block|{
comment|// No need to retry on AccessControlException
comment|// and AuthorizationException.
comment|// This assumes all the servers are configured with identical
comment|// permissions and identical key acls.
throw|throw
name|ace
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
literal|"KMS provider at [{}] threw an IOException: "
argument_list|,
name|provider
operator|.
name|getKMSUrl
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|ex
operator|=
name|ioe
expr_stmt|;
name|RetryAction
name|action
init|=
literal|null
decl_stmt|;
try|try
block|{
name|action
operator|=
name|retryPolicy
operator|.
name|shouldRetry
argument_list|(
name|ioe
argument_list|,
literal|0
argument_list|,
name|numFailovers
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// make sure each provider is tried at least once, to keep behavior
comment|// compatible with earlier versions of LBKMSCP
if|if
condition|(
name|action
operator|.
name|action
operator|==
name|RetryAction
operator|.
name|RetryDecision
operator|.
name|FAIL
operator|&&
name|numFailovers
operator|>=
name|providers
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Aborting since the Request has failed with all KMS"
operator|+
literal|" providers(depending on {}={} setting and numProviders={})"
operator|+
literal|" in the group OR the exception is not recoverable"
argument_list|,
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY
argument_list|,
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|KMS_CLIENT_FAILOVER_MAX_RETRIES_KEY
argument_list|,
name|providers
operator|.
name|length
argument_list|)
argument_list|,
name|providers
operator|.
name|length
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
if|if
condition|(
operator|(
operator|(
name|numFailovers
operator|+
literal|1
operator|)
operator|%
name|providers
operator|.
name|length
operator|)
operator|==
literal|0
condition|)
block|{
comment|// Sleep only after we try all the providers for every cycle.
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|action
operator|.
name|delayMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|(
literal|"Thread Interrupted"
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|WrapperException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|nextIdx ()
specifier|private
name|int
name|nextIdx
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|current
init|=
name|currentIdx
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|next
init|=
operator|(
name|current
operator|+
literal|1
operator|)
operator|%
name|providers
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|currentIdx
operator|.
name|compareAndSet
argument_list|(
name|current
argument_list|,
name|next
argument_list|)
condition|)
block|{
return|return
name|current
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
index|[]
DECL|method|addDelegationTokens (final String renewer, final Credentials credentials)
name|addDelegationTokens
parameter_list|(
specifier|final
name|String
name|renewer
parameter_list|,
specifier|final
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Token
argument_list|<
name|?
argument_list|>
index|[]
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|addDelegationTokens
argument_list|(
name|renewer
argument_list|,
name|credentials
argument_list|)
return|;
block|}
block|}
operator|,
name|nextIdx
argument_list|()
block|)
function|;
block|}
end_class

begin_function
annotation|@
name|Override
DECL|method|renewDelegationToken (final Token<?> token)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|cancelDelegationToken (final Token<?> token)
specifier|public
name|Void
name|cancelDelegationToken
parameter_list|(
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
name|provider
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_comment
comment|// This request is sent to all providers in the load-balancing group
end_comment

begin_function
annotation|@
name|Override
DECL|method|warmUpEncryptedKeys (String... keyNames)
specifier|public
name|void
name|warmUpEncryptedKeys
parameter_list|(
name|String
modifier|...
name|keyNames
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|providers
operator|.
name|length
operator|>
literal|0
argument_list|,
literal|"No providers are configured"
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IOException
name|e
init|=
literal|null
decl_stmt|;
for|for
control|(
name|KMSClientProvider
name|provider
range|:
name|providers
control|)
block|{
try|try
block|{
name|provider
operator|.
name|warmUpEncryptedKeys
argument_list|(
name|keyNames
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|e
operator|=
name|ioe
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Error warming up keys for provider with url"
operator|+
literal|"["
operator|+
name|provider
operator|.
name|getKMSUrl
argument_list|()
operator|+
literal|"]"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|success
operator|&&
name|e
operator|!=
literal|null
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
end_function

begin_comment
comment|// This request is sent to all providers in the load-balancing group
end_comment

begin_function
annotation|@
name|Override
DECL|method|drain (String keyName)
specifier|public
name|void
name|drain
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
for|for
control|(
name|KMSClientProvider
name|provider
range|:
name|providers
control|)
block|{
name|provider
operator|.
name|drain
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_comment
comment|// This request is sent to all providers in the load-balancing group
end_comment

begin_function
annotation|@
name|Override
DECL|method|invalidateCache (String keyName)
specifier|public
name|void
name|invalidateCache
parameter_list|(
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|KMSClientProvider
name|provider
range|:
name|providers
control|)
block|{
name|provider
operator|.
name|invalidateCache
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|EncryptedKeyVersion
DECL|method|generateEncryptedKey (final String encryptionKeyName)
name|generateEncryptedKey
parameter_list|(
specifier|final
name|String
name|encryptionKeyName
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
try|try
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|EncryptedKeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|EncryptedKeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
return|return
name|provider
operator|.
name|generateEncryptedKey
argument_list|(
name|encryptionKeyName
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|WrapperException
name|we
parameter_list|)
block|{
if|if
condition|(
name|we
operator|.
name|getCause
argument_list|()
operator|instanceof
name|GeneralSecurityException
condition|)
block|{
throw|throw
operator|(
name|GeneralSecurityException
operator|)
name|we
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|we
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
specifier|public
name|KeyVersion
DECL|method|decryptEncryptedKey (final EncryptedKeyVersion encryptedKeyVersion)
name|decryptEncryptedKey
parameter_list|(
specifier|final
name|EncryptedKeyVersion
name|encryptedKeyVersion
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
try|try
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|KeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
return|return
name|provider
operator|.
name|decryptEncryptedKey
argument_list|(
name|encryptedKeyVersion
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|WrapperException
name|we
parameter_list|)
block|{
if|if
condition|(
name|we
operator|.
name|getCause
argument_list|()
operator|instanceof
name|GeneralSecurityException
condition|)
block|{
throw|throw
operator|(
name|GeneralSecurityException
operator|)
name|we
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|we
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|reencryptEncryptedKey ( final EncryptedKeyVersion ekv)
specifier|public
name|EncryptedKeyVersion
name|reencryptEncryptedKey
parameter_list|(
specifier|final
name|EncryptedKeyVersion
name|ekv
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
try|try
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|EncryptedKeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|EncryptedKeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
return|return
name|provider
operator|.
name|reencryptEncryptedKey
argument_list|(
name|ekv
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|WrapperException
name|we
parameter_list|)
block|{
if|if
condition|(
name|we
operator|.
name|getCause
argument_list|()
operator|instanceof
name|GeneralSecurityException
condition|)
block|{
throw|throw
operator|(
name|GeneralSecurityException
operator|)
name|we
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|we
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|reencryptEncryptedKeys (final List<EncryptedKeyVersion> ekvs)
specifier|public
name|void
name|reencryptEncryptedKeys
parameter_list|(
specifier|final
name|List
argument_list|<
name|EncryptedKeyVersion
argument_list|>
name|ekvs
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
try|try
block|{
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
name|provider
operator|.
name|reencryptEncryptedKeys
argument_list|(
name|ekvs
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WrapperException
name|we
parameter_list|)
block|{
if|if
condition|(
name|we
operator|.
name|getCause
argument_list|()
operator|instanceof
name|GeneralSecurityException
condition|)
block|{
throw|throw
operator|(
name|GeneralSecurityException
operator|)
name|we
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|we
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getKeyVersion (final String versionName)
specifier|public
name|KeyVersion
name|getKeyVersion
parameter_list|(
specifier|final
name|String
name|versionName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|KeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|getKeyVersion
argument_list|(
name|versionName
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getKeys ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getKeys
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|getKeys
argument_list|()
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getKeysMetadata (final String... names)
specifier|public
name|Metadata
index|[]
name|getKeysMetadata
parameter_list|(
specifier|final
name|String
modifier|...
name|names
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|Metadata
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Metadata
index|[]
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|getKeysMetadata
argument_list|(
name|names
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getKeyVersions (final String name)
specifier|public
name|List
argument_list|<
name|KeyVersion
argument_list|>
name|getKeyVersions
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|List
argument_list|<
name|KeyVersion
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|KeyVersion
argument_list|>
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|getKeyVersions
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getCurrentKey (final String name)
specifier|public
name|KeyVersion
name|getCurrentKey
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|KeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|getCurrentKey
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|getMetadata (final String name)
specifier|public
name|Metadata
name|getMetadata
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|Metadata
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Metadata
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|getMetadata
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|createKey (final String name, final byte[] material, final Options options)
specifier|public
name|KeyVersion
name|createKey
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|byte
index|[]
name|material
parameter_list|,
specifier|final
name|Options
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|KeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|createKey
argument_list|(
name|name
argument_list|,
name|material
argument_list|,
name|options
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|createKey (final String name, final Options options)
specifier|public
name|KeyVersion
name|createKey
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Options
name|options
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
try|try
block|{
return|return
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|KeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
return|return
name|provider
operator|.
name|createKey
argument_list|(
name|name
argument_list|,
name|options
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|WrapperException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|GeneralSecurityException
condition|)
block|{
throw|throw
operator|(
name|NoSuchAlgorithmException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|deleteKey (final String name)
specifier|public
name|void
name|deleteKey
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
name|provider
operator|.
name|deleteKey
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|rollNewVersion (final String name, final byte[] material)
specifier|public
name|KeyVersion
name|rollNewVersion
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|byte
index|[]
name|material
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|KeyVersion
name|newVersion
init|=
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|KeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|rollNewVersion
argument_list|(
name|name
argument_list|,
name|material
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
decl_stmt|;
name|invalidateCache
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|newVersion
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|rollNewVersion (final String name)
specifier|public
name|KeyVersion
name|rollNewVersion
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
try|try
block|{
specifier|final
name|KeyVersion
name|newVersion
init|=
name|doOp
argument_list|(
operator|new
name|ProviderCallable
argument_list|<
name|KeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KeyVersion
name|call
parameter_list|(
name|KMSClientProvider
name|provider
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchAlgorithmException
block|{
return|return
name|provider
operator|.
name|rollNewVersion
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|,
name|nextIdx
argument_list|()
argument_list|)
decl_stmt|;
name|invalidateCache
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|newVersion
return|;
block|}
catch|catch
parameter_list|(
name|WrapperException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|GeneralSecurityException
condition|)
block|{
throw|throw
operator|(
name|NoSuchAlgorithmException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_function

begin_comment
comment|// Close all providers in the LB group
end_comment

begin_function
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|KMSClientProvider
name|provider
range|:
name|providers
control|)
block|{
try|try
block|{
name|provider
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error closing provider with url"
operator|+
literal|"["
operator|+
name|provider
operator|.
name|getKMSUrl
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|KMSClientProvider
name|provider
range|:
name|providers
control|)
block|{
try|try
block|{
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error flushing provider with url"
operator|+
literal|"["
operator|+
name|provider
operator|.
name|getKMSUrl
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_function

begin_function
DECL|method|shuffle (KMSClientProvider[] providers)
specifier|private
specifier|static
name|KMSClientProvider
index|[]
name|shuffle
parameter_list|(
name|KMSClientProvider
index|[]
name|providers
parameter_list|)
block|{
name|List
argument_list|<
name|KMSClientProvider
argument_list|>
name|list
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|providers
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|list
argument_list|)
expr_stmt|;
return|return
name|list
operator|.
name|toArray
argument_list|(
name|providers
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

