begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentialsProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|BasicSessionCredentials
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|AWSCredentials
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
name|lang
operator|.
name|StringUtils
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
name|security
operator|.
name|ProviderUtils
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Support session credentials for authenticating with AWS.  *  * Please note that users may reference this class name from configuration  * property fs.s3a.aws.credentials.provider.  Therefore, changing the class name  * would be a backward-incompatible change.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|TemporaryAWSCredentialsProvider
specifier|public
class|class
name|TemporaryAWSCredentialsProvider
implements|implements
name|AWSCredentialsProvider
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.fs.s3a.TemporaryAWSCredentialsProvider"
decl_stmt|;
DECL|field|accessKey
specifier|private
name|String
name|accessKey
decl_stmt|;
DECL|field|secretKey
specifier|private
name|String
name|secretKey
decl_stmt|;
DECL|field|sessionToken
specifier|private
name|String
name|sessionToken
decl_stmt|;
DECL|field|lookupIOE
specifier|private
name|IOException
name|lookupIOE
decl_stmt|;
DECL|method|TemporaryAWSCredentialsProvider (URI uri, Configuration conf)
specifier|public
name|TemporaryAWSCredentialsProvider
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
name|Configuration
name|c
init|=
name|ProviderUtils
operator|.
name|excludeIncompatibleCredentialProviders
argument_list|(
name|conf
argument_list|,
name|S3AFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|accessKey
operator|=
name|S3AUtils
operator|.
name|lookupPassword
argument_list|(
name|c
argument_list|,
name|ACCESS_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|secretKey
operator|=
name|S3AUtils
operator|.
name|lookupPassword
argument_list|(
name|c
argument_list|,
name|SECRET_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|sessionToken
operator|=
name|S3AUtils
operator|.
name|lookupPassword
argument_list|(
name|c
argument_list|,
name|SESSION_TOKEN
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|lookupIOE
operator|=
name|e
expr_stmt|;
block|}
block|}
DECL|method|getCredentials ()
specifier|public
name|AWSCredentials
name|getCredentials
parameter_list|()
block|{
if|if
condition|(
name|lookupIOE
operator|!=
literal|null
condition|)
block|{
comment|// propagate any initialization problem
throw|throw
operator|new
name|CredentialInitializationException
argument_list|(
name|lookupIOE
operator|.
name|toString
argument_list|()
argument_list|,
name|lookupIOE
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|accessKey
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|secretKey
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|sessionToken
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicSessionCredentials
argument_list|(
name|accessKey
argument_list|,
name|secretKey
argument_list|,
name|sessionToken
argument_list|)
return|;
block|}
throw|throw
operator|new
name|CredentialInitializationException
argument_list|(
literal|"Access key, secret key or session token is unset"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

