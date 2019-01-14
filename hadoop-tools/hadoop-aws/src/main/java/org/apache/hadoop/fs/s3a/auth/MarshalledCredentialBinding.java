begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth
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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|com
operator|.
name|amazonaws
operator|.
name|ClientConfiguration
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
name|AWSSessionCredentials
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
name|BasicAWSCredentials
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
name|services
operator|.
name|securitytoken
operator|.
name|AWSSecurityTokenService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|securitytoken
operator|.
name|model
operator|.
name|Credentials
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
name|Invoker
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
name|Retries
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
name|S3AFileSystem
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
name|ACCESS_KEY
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
name|SECRET_KEY
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
name|SESSION_TOKEN
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
name|S3AUtils
operator|.
name|lookupPassword
import|;
end_import

begin_comment
comment|/**  * Class to bridge from the serializable/marshallabled  * {@link MarshalledCredentialBinding} class to/from AWS classes.  * This is to keep that class isolated and not dependent on aws-sdk JARs  * to load.  */
end_comment

begin_class
DECL|class|MarshalledCredentialBinding
specifier|public
specifier|final
class|class
name|MarshalledCredentialBinding
block|{
DECL|method|MarshalledCredentialBinding ()
specifier|private
name|MarshalledCredentialBinding
parameter_list|()
block|{   }
comment|/**    * Error text on empty credentials: {@value}.    */
annotation|@
name|VisibleForTesting
DECL|field|NO_AWS_CREDENTIALS
specifier|public
specifier|static
specifier|final
name|String
name|NO_AWS_CREDENTIALS
init|=
literal|"No AWS credentials"
decl_stmt|;
comment|/**    * Create a set of marshalled credentials from a set of credentials    * issued by an STS call.    * @param credentials AWS-provided session credentials    */
DECL|method|fromSTSCredentials ( final Credentials credentials)
specifier|public
specifier|static
name|MarshalledCredentials
name|fromSTSCredentials
parameter_list|(
specifier|final
name|Credentials
name|credentials
parameter_list|)
block|{
name|MarshalledCredentials
name|marshalled
init|=
operator|new
name|MarshalledCredentials
argument_list|(
name|credentials
operator|.
name|getAccessKeyId
argument_list|()
argument_list|,
name|credentials
operator|.
name|getSecretAccessKey
argument_list|()
argument_list|,
name|credentials
operator|.
name|getSessionToken
argument_list|()
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
name|credentials
operator|.
name|getExpiration
argument_list|()
decl_stmt|;
name|marshalled
operator|.
name|setExpiration
argument_list|(
name|date
operator|!=
literal|null
condition|?
name|date
operator|.
name|getTime
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
return|return
name|marshalled
return|;
block|}
comment|/**    * Create from a set of AWS credentials.    * @param credentials source credential.    * @return a set of marshalled credentials.    */
DECL|method|fromAWSCredentials ( final AWSSessionCredentials credentials)
specifier|public
specifier|static
name|MarshalledCredentials
name|fromAWSCredentials
parameter_list|(
specifier|final
name|AWSSessionCredentials
name|credentials
parameter_list|)
block|{
return|return
operator|new
name|MarshalledCredentials
argument_list|(
name|credentials
operator|.
name|getAWSAccessKeyId
argument_list|()
argument_list|,
name|credentials
operator|.
name|getAWSSecretKey
argument_list|()
argument_list|,
name|credentials
operator|.
name|getSessionToken
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Build a set of credentials from the environment.    * @param env environment.    * @return a possibly incomplete/invalid set of credentials.    */
DECL|method|fromEnvironment ( final Map<String, String> env)
specifier|public
specifier|static
name|MarshalledCredentials
name|fromEnvironment
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
return|return
operator|new
name|MarshalledCredentials
argument_list|(
name|nullToEmptyString
argument_list|(
name|env
operator|.
name|get
argument_list|(
literal|"AWS_ACCESS_KEY"
argument_list|)
argument_list|)
argument_list|,
name|nullToEmptyString
argument_list|(
name|env
operator|.
name|get
argument_list|(
literal|"AWS_SECRET_KEY"
argument_list|)
argument_list|)
argument_list|,
name|nullToEmptyString
argument_list|(
name|env
operator|.
name|get
argument_list|(
literal|"AWS_SESSION_TOKEN"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Take a string where a null value is remapped to an empty string.    * @param src source string.    * @return the value of the string or ""    */
DECL|method|nullToEmptyString (final String src)
specifier|private
specifier|static
name|String
name|nullToEmptyString
parameter_list|(
specifier|final
name|String
name|src
parameter_list|)
block|{
return|return
name|src
operator|==
literal|null
condition|?
literal|""
else|:
name|src
return|;
block|}
comment|/**    * Loads the credentials from the owning S3A FS, including    * from Hadoop credential providers.    * There is no validation.    * @param conf configuration to load from    * @return the component    * @throws IOException on any load failure    */
DECL|method|fromFileSystem ( final URI uri, final Configuration conf)
specifier|public
specifier|static
name|MarshalledCredentials
name|fromFileSystem
parameter_list|(
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
comment|// determine the bucket
specifier|final
name|String
name|bucket
init|=
name|uri
operator|!=
literal|null
condition|?
name|uri
operator|.
name|getHost
argument_list|()
else|:
literal|""
decl_stmt|;
specifier|final
name|Configuration
name|leanConf
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
return|return
operator|new
name|MarshalledCredentials
argument_list|(
name|lookupPassword
argument_list|(
name|bucket
argument_list|,
name|leanConf
argument_list|,
name|ACCESS_KEY
argument_list|)
argument_list|,
name|lookupPassword
argument_list|(
name|bucket
argument_list|,
name|leanConf
argument_list|,
name|SECRET_KEY
argument_list|)
argument_list|,
name|lookupPassword
argument_list|(
name|bucket
argument_list|,
name|leanConf
argument_list|,
name|SESSION_TOKEN
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create an AWS credential set from a set of marshalled credentials.    *    * This code would seem to fit into (@link MarshalledCredentials}, and    * while it would from a code-hygiene perspective, to keep all AWS    * SDK references out of that class, the logic is implemented here instead,    * @param marshalled marshalled credentials    * @param typeRequired type of credentials required    * @param component component name for exception messages.    * @return a new set of credentials    * @throws NoAuthWithAWSException validation failure    * @throws NoAwsCredentialsException the credentials are actually empty.    */
DECL|method|toAWSCredentials ( final MarshalledCredentials marshalled, final MarshalledCredentials.CredentialTypeRequired typeRequired, final String component)
specifier|public
specifier|static
name|AWSCredentials
name|toAWSCredentials
parameter_list|(
specifier|final
name|MarshalledCredentials
name|marshalled
parameter_list|,
specifier|final
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
name|typeRequired
parameter_list|,
specifier|final
name|String
name|component
parameter_list|)
throws|throws
name|NoAuthWithAWSException
throws|,
name|NoAwsCredentialsException
block|{
if|if
condition|(
name|marshalled
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoAwsCredentialsException
argument_list|(
name|component
argument_list|,
name|NO_AWS_CREDENTIALS
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|marshalled
operator|.
name|isValid
argument_list|(
name|typeRequired
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoAuthWithAWSException
argument_list|(
name|component
operator|+
literal|":"
operator|+
name|marshalled
operator|.
name|buildInvalidCredentialsError
argument_list|(
name|typeRequired
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|String
name|accessKey
init|=
name|marshalled
operator|.
name|getAccessKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|secretKey
init|=
name|marshalled
operator|.
name|getSecretKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|marshalled
operator|.
name|hasSessionToken
argument_list|()
condition|)
block|{
comment|// a session token was supplied, so return session credentials
return|return
operator|new
name|BasicSessionCredentials
argument_list|(
name|accessKey
argument_list|,
name|secretKey
argument_list|,
name|marshalled
operator|.
name|getSessionToken
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// these are full credentials
return|return
operator|new
name|BasicAWSCredentials
argument_list|(
name|accessKey
argument_list|,
name|secretKey
argument_list|)
return|;
block|}
block|}
comment|/**    * Request a set of credentials from an STS endpoint.    * @param parentCredentials the parent credentials needed to talk to STS    * @param stsEndpoint an endpoint, use "" for none    * @param stsRegion region; use if the endpoint isn't the AWS default.    * @param duration duration of the credentials in seconds. Minimum value: 900.    * @param invoker invoker to use for retrying the call.    * @return the credentials    * @throws IOException on a failure of the request    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|requestSessionCredentials ( final AWSCredentialsProvider parentCredentials, final ClientConfiguration awsConf, final String stsEndpoint, final String stsRegion, final int duration, final Invoker invoker)
specifier|public
specifier|static
name|MarshalledCredentials
name|requestSessionCredentials
parameter_list|(
specifier|final
name|AWSCredentialsProvider
name|parentCredentials
parameter_list|,
specifier|final
name|ClientConfiguration
name|awsConf
parameter_list|,
specifier|final
name|String
name|stsEndpoint
parameter_list|,
specifier|final
name|String
name|stsRegion
parameter_list|,
specifier|final
name|int
name|duration
parameter_list|,
specifier|final
name|Invoker
name|invoker
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|AWSSecurityTokenService
name|tokenService
init|=
name|STSClientFactory
operator|.
name|builder
argument_list|(
name|parentCredentials
argument_list|,
name|awsConf
argument_list|,
name|stsEndpoint
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|stsEndpoint
argument_list|,
name|stsRegion
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|fromSTSCredentials
argument_list|(
name|STSClientFactory
operator|.
name|createClientConnection
argument_list|(
name|tokenService
argument_list|,
name|invoker
argument_list|)
operator|.
name|requestSessionCredentials
argument_list|(
name|duration
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

