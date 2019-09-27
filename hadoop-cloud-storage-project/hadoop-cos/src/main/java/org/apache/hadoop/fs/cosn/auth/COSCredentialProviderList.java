begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn.auth
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
operator|.
name|auth
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Collection
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
name|AtomicBoolean
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

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|auth
operator|.
name|AnonymousCOSCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|auth
operator|.
name|COSCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|auth
operator|.
name|COSCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|exception
operator|.
name|CosClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|utils
operator|.
name|StringUtils
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
comment|/**  * a list of cos credentials provider.  */
end_comment

begin_class
DECL|class|COSCredentialProviderList
specifier|public
class|class
name|COSCredentialProviderList
implements|implements
name|COSCredentialsProvider
implements|,
name|AutoCloseable
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
name|COSCredentialProviderList
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NO_COS_CREDENTIAL_PROVIDERS
specifier|private
specifier|static
specifier|final
name|String
name|NO_COS_CREDENTIAL_PROVIDERS
init|=
literal|"No COS Credential Providers"
decl_stmt|;
DECL|field|CREDENTIALS_REQUESTED_WHEN_CLOSED
specifier|private
specifier|static
specifier|final
name|String
name|CREDENTIALS_REQUESTED_WHEN_CLOSED
init|=
literal|"Credentials requested after provider list was closed"
decl_stmt|;
DECL|field|providers
specifier|private
specifier|final
name|List
argument_list|<
name|COSCredentialsProvider
argument_list|>
name|providers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|reuseLastProvider
specifier|private
name|boolean
name|reuseLastProvider
init|=
literal|true
decl_stmt|;
DECL|field|lastProvider
specifier|private
name|COSCredentialsProvider
name|lastProvider
decl_stmt|;
DECL|field|refCount
specifier|private
specifier|final
name|AtomicInteger
name|refCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|isClosed
specifier|private
specifier|final
name|AtomicBoolean
name|isClosed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|COSCredentialProviderList ()
specifier|public
name|COSCredentialProviderList
parameter_list|()
block|{   }
DECL|method|COSCredentialProviderList ( Collection<COSCredentialsProvider> providers)
specifier|public
name|COSCredentialProviderList
parameter_list|(
name|Collection
argument_list|<
name|COSCredentialsProvider
argument_list|>
name|providers
parameter_list|)
block|{
name|this
operator|.
name|providers
operator|.
name|addAll
argument_list|(
name|providers
argument_list|)
expr_stmt|;
block|}
DECL|method|add (COSCredentialsProvider provider)
specifier|public
name|void
name|add
parameter_list|(
name|COSCredentialsProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|providers
operator|.
name|add
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
DECL|method|getRefCount ()
specifier|public
name|int
name|getRefCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|refCount
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|checkNotEmpty ()
specifier|public
name|void
name|checkNotEmpty
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|providers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoAuthWithCOSException
argument_list|(
name|NO_COS_CREDENTIAL_PROVIDERS
argument_list|)
throw|;
block|}
block|}
DECL|method|share ()
specifier|public
name|COSCredentialProviderList
name|share
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|this
operator|.
name|closed
argument_list|()
argument_list|,
literal|"Provider list is closed"
argument_list|)
expr_stmt|;
name|this
operator|.
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|closed ()
specifier|public
name|boolean
name|closed
parameter_list|()
block|{
return|return
name|this
operator|.
name|isClosed
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCredentials ()
specifier|public
name|COSCredentials
name|getCredentials
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|closed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoAuthWithCOSException
argument_list|(
name|CREDENTIALS_REQUESTED_WHEN_CLOSED
argument_list|)
throw|;
block|}
name|this
operator|.
name|checkNotEmpty
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|reuseLastProvider
operator|&&
name|this
operator|.
name|lastProvider
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|lastProvider
operator|.
name|getCredentials
argument_list|()
return|;
block|}
for|for
control|(
name|COSCredentialsProvider
name|provider
range|:
name|this
operator|.
name|providers
control|)
block|{
try|try
block|{
name|COSCredentials
name|credentials
init|=
name|provider
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isNullOrEmpty
argument_list|(
name|credentials
operator|.
name|getCOSAccessKeyId
argument_list|()
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isNullOrEmpty
argument_list|(
name|credentials
operator|.
name|getCOSSecretKey
argument_list|()
argument_list|)
operator|||
name|credentials
operator|instanceof
name|AnonymousCOSCredentials
condition|)
block|{
name|this
operator|.
name|lastProvider
operator|=
name|provider
expr_stmt|;
return|return
name|credentials
return|;
block|}
block|}
catch|catch
parameter_list|(
name|CosClientException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No credentials provided by {}: {}"
argument_list|,
name|provider
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|NoAuthWithCOSException
argument_list|(
literal|"No COS Credentials provided by "
operator|+
name|this
operator|.
name|providers
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|closed
argument_list|()
condition|)
block|{
return|return;
block|}
name|int
name|remainder
init|=
name|this
operator|.
name|refCount
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|remainder
operator|!=
literal|0
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|isClosed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|COSCredentialsProvider
name|provider
range|:
name|this
operator|.
name|providers
control|)
block|{
if|if
condition|(
name|provider
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|provider
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

