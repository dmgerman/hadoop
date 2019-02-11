begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|AbstractSessionCredentialsProvider
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
name|s3a
operator|.
name|auth
operator|.
name|MarshalledCredentialBinding
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
name|s3a
operator|.
name|auth
operator|.
name|MarshalledCredentials
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
name|s3a
operator|.
name|auth
operator|.
name|NoAuthWithAWSException
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
name|s3a
operator|.
name|auth
operator|.
name|NoAwsCredentialsException
import|;
end_import

begin_comment
comment|/**  * Support session credentials for authenticating with AWS.  *  * Please note that users may reference this class name from configuration  * property fs.s3a.aws.credentials.provider.  Therefore, changing the class name  * would be a backward-incompatible change.  *  * This credential provider must not fail in creation because that will  * break a chain of credential providers.  */
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
extends|extends
name|AbstractSessionCredentialsProvider
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
DECL|field|COMPONENT
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT
init|=
literal|"Session credentials in Hadoop configuration"
decl_stmt|;
comment|/**    * Construct from just a configuration.    * @param conf configuration.    * @throws IOException on any failure to load the credentials.    */
DECL|method|TemporaryAWSCredentialsProvider (final Configuration conf)
specifier|public
name|TemporaryAWSCredentialsProvider
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor: the URI will be null if the provider is inited unbonded    * to a filesystem.    * @param uri binding to a filesystem URI.    * @param conf configuration.    * @throws IOException on any failure to load the credentials.    */
DECL|method|TemporaryAWSCredentialsProvider ( @ullable final URI uri, final Configuration conf)
specifier|public
name|TemporaryAWSCredentialsProvider
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|URI
name|uri
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * The credentials here must include a session token, else this operation    * will raise an exception.    * @param config the configuration    * @return temporary credentials.    * @throws IOException on any failure to load the credentials.    * @throws NoAuthWithAWSException validation failure    * @throws NoAwsCredentialsException the credentials are actually empty.    */
annotation|@
name|Override
DECL|method|createCredentials (Configuration config)
specifier|protected
name|AWSCredentials
name|createCredentials
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|MarshalledCredentials
name|creds
init|=
name|MarshalledCredentialBinding
operator|.
name|fromFileSystem
argument_list|(
name|getUri
argument_list|()
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
name|sessionOnly
init|=
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|SessionOnly
decl_stmt|;
comment|// treat only having non-session creds as empty.
if|if
condition|(
operator|!
name|creds
operator|.
name|isValid
argument_list|(
name|sessionOnly
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoAwsCredentialsException
argument_list|(
name|COMPONENT
argument_list|)
throw|;
block|}
return|return
name|MarshalledCredentialBinding
operator|.
name|toAWSCredentials
argument_list|(
name|creds
argument_list|,
name|sessionOnly
argument_list|,
name|COMPONENT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

