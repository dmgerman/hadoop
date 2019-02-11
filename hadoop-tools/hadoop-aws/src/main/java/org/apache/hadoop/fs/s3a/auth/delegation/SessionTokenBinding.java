begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
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
operator|.
name|delegation
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
name|time
operator|.
name|OffsetDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|AWSSessionCredentials
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
name|AWSCredentialProviderList
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
name|S3ARetryPolicy
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
name|S3AUtils
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
name|MarshalledCredentialProvider
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
name|RoleModel
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
name|STSClientFactory
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
name|IOUtils
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
name|Text
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
name|AWS_CREDENTIALS_PROVIDER
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
name|Invoker
operator|.
name|once
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
name|STANDARD_AWS_PROVIDERS
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
name|buildAWSProviderList
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
name|auth
operator|.
name|MarshalledCredentialBinding
operator|.
name|fromAWSCredentials
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
name|auth
operator|.
name|MarshalledCredentialBinding
operator|.
name|fromSTSCredentials
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
name|auth
operator|.
name|delegation
operator|.
name|DelegationConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The session token DT binding: creates an AWS session token  * for the DT, extracts and serves it up afterwards.  */
end_comment

begin_class
DECL|class|SessionTokenBinding
specifier|public
class|class
name|SessionTokenBinding
extends|extends
name|AbstractDelegationTokenBinding
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
name|SessionTokenBinding
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Wire name of this binding: {@value}.    */
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"SessionTokens/001"
decl_stmt|;
comment|/**    * A message added to the standard origin string when the DT is    * built from session credentials passed in.    */
annotation|@
name|VisibleForTesting
DECL|field|CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN
init|=
literal|"Existing session credentials converted to Delegation Token"
decl_stmt|;
DECL|field|SESSION_TOKEN
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_TOKEN
init|=
literal|"Session Delegation Token"
decl_stmt|;
comment|/** Invoker for STS calls. */
DECL|field|invoker
specifier|private
name|Invoker
name|invoker
decl_stmt|;
comment|/**    * Has an attempt to initialize STS been attempted?    */
DECL|field|stsInitAttempted
specifier|private
specifier|final
name|AtomicBoolean
name|stsInitAttempted
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/** The STS client; created in startup if the parental credentials permit. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"FieldAccessedSynchronizedAndUnsynchronized"
argument_list|)
DECL|field|stsClient
specifier|private
name|Optional
argument_list|<
name|STSClientFactory
operator|.
name|STSClient
argument_list|>
name|stsClient
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
comment|/**    * Duration of session in seconds.    */
DECL|field|duration
specifier|private
name|long
name|duration
decl_stmt|;
comment|/**    * Flag to indicate that the auth chain provides session credentials.    * If true it means that STS cannot be used (and stsClient is null).    */
DECL|field|hasSessionCreds
specifier|private
name|boolean
name|hasSessionCreds
decl_stmt|;
comment|/**    * The auth chain for the parent options.    */
DECL|field|parentAuthChain
specifier|private
name|AWSCredentialProviderList
name|parentAuthChain
decl_stmt|;
comment|/**    * Has a log message about forwarding credentials been printed yet?    */
DECL|field|forwardMessageLogged
specifier|private
specifier|final
name|AtomicBoolean
name|forwardMessageLogged
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/** STS endpoint. */
DECL|field|endpoint
specifier|private
name|String
name|endpoint
decl_stmt|;
comment|/** STS region. */
DECL|field|region
specifier|private
name|String
name|region
decl_stmt|;
comment|/**    * Expiration date time as passed in from source.    * If unset, either we are unbound, or the token which came in does not    * know its expiry.    */
DECL|field|expirationDateTime
specifier|private
name|Optional
argument_list|<
name|OffsetDateTime
argument_list|>
name|expirationDateTime
decl_stmt|;
comment|/**    * Token identifier bound to.    */
DECL|field|tokenIdentifier
specifier|private
name|Optional
argument_list|<
name|SessionTokenIdentifier
argument_list|>
name|tokenIdentifier
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
comment|/** Constructor for reflection. */
DECL|method|SessionTokenBinding ()
specifier|public
name|SessionTokenBinding
parameter_list|()
block|{
name|this
argument_list|(
name|NAME
argument_list|,
name|SESSION_TOKEN_KIND
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for subclasses.    * @param name binding name.    * @param kind token kind.    */
DECL|method|SessionTokenBinding (final String name, final Text kind)
specifier|protected
name|SessionTokenBinding
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Text
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|)
expr_stmt|;
block|}
comment|/**    * Service start will read in all configuration options    * then build that client.    */
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|duration
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DELEGATION_TOKEN_DURATION
argument_list|,
name|DEFAULT_DELEGATION_TOKEN_DURATION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|endpoint
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DELEGATION_TOKEN_ENDPOINT
argument_list|,
name|DEFAULT_DELEGATION_TOKEN_ENDPOINT
argument_list|)
expr_stmt|;
name|region
operator|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|DELEGATION_TOKEN_REGION
argument_list|,
name|DEFAULT_DELEGATION_TOKEN_REGION
argument_list|)
expr_stmt|;
comment|// create the provider set for session credentials.
name|parentAuthChain
operator|=
name|buildAWSProviderList
argument_list|(
name|getCanonicalUri
argument_list|()
argument_list|,
name|conf
argument_list|,
name|AWS_CREDENTIALS_PROVIDER
argument_list|,
name|STANDARD_AWS_PROVIDERS
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
comment|// this is here to keep findbugs quiet, even though nothing
comment|// can safely invoke stsClient as we are shut down.
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|stsClient
operator|.
name|ifPresent
argument_list|(
name|IOUtils
operator|::
name|closeStream
argument_list|)
expr_stmt|;
name|this
operator|.
name|stsClient
operator|=
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Return an unbonded provider chain.    * @return the auth chain built from the assumed role credentials    * @throws IOException any failure.    */
annotation|@
name|Override
DECL|method|deployUnbonded ()
specifier|public
name|AWSCredentialProviderList
name|deployUnbonded
parameter_list|()
throws|throws
name|IOException
block|{
name|requireServiceStarted
argument_list|()
expr_stmt|;
return|return
name|parentAuthChain
return|;
block|}
comment|/**    * Get the invoker for STS calls.    * @return the invoker    */
DECL|method|getInvoker ()
specifier|protected
name|Invoker
name|getInvoker
parameter_list|()
block|{
return|return
name|invoker
return|;
block|}
comment|/**    * Sets the field {@link #tokenIdentifier} to the extracted/cast    * session token identifier, and {@link #expirationDateTime} to    * any expiration passed in.    * @param retrievedIdentifier the unmarshalled data    * @return the provider list    * @throws IOException failure    */
annotation|@
name|Override
DECL|method|bindToTokenIdentifier ( final AbstractS3ATokenIdentifier retrievedIdentifier)
specifier|public
name|AWSCredentialProviderList
name|bindToTokenIdentifier
parameter_list|(
specifier|final
name|AbstractS3ATokenIdentifier
name|retrievedIdentifier
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SessionTokenIdentifier
name|identifier
init|=
name|convertTokenIdentifier
argument_list|(
name|retrievedIdentifier
argument_list|,
name|SessionTokenIdentifier
operator|.
name|class
argument_list|)
decl_stmt|;
name|setTokenIdentifier
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
name|MarshalledCredentials
name|marshalledCredentials
init|=
name|identifier
operator|.
name|getMarshalledCredentials
argument_list|()
decl_stmt|;
name|setExpirationDateTime
argument_list|(
name|marshalledCredentials
operator|.
name|getExpirationDateTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|AWSCredentialProviderList
argument_list|(
literal|"Session Token Binding"
argument_list|,
operator|new
name|MarshalledCredentialProvider
argument_list|(
name|SESSION_TOKEN
argument_list|,
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|marshalledCredentials
argument_list|,
name|MarshalledCredentials
operator|.
name|CredentialTypeRequired
operator|.
name|SessionOnly
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s token binding for user %s, "
operator|+
literal|"with STS endpoint \"%s\", region \"%s\""
operator|+
literal|" and token duration %d:%02d"
argument_list|,
name|bindingName
argument_list|()
argument_list|,
name|getOwner
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|endpoint
argument_list|,
name|region
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMinutes
argument_list|(
name|duration
argument_list|)
argument_list|,
name|duration
operator|%
literal|60
argument_list|)
return|;
block|}
comment|/**    * Get the role of this token; subclasses should override this    * for better logging.    * @return the role of this token    */
DECL|method|bindingName ()
specifier|protected
name|String
name|bindingName
parameter_list|()
block|{
return|return
literal|"Session"
return|;
block|}
comment|/**    * UA field contains the UUID of the token if present.    * @return a string for the S3 logs.    */
DECL|method|getUserAgentField ()
specifier|public
name|String
name|getUserAgentField
parameter_list|()
block|{
if|if
condition|(
name|tokenIdentifier
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
literal|"; session ID "
operator|+
name|tokenIdentifier
operator|.
name|get
argument_list|()
operator|.
name|getUuid
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
comment|/**    * Attempt to init the STS connection, only does it once.    * If the AWS credential list to this service return session credentials    * then this method will return {@code empty()}; no attempt is    * made to connect to STS.    * Otherwise, the STS binding info will be looked up and an attempt    * made to connect to STS.    * Only one attempt will be made.    * @return any STS client created.    * @throws IOException any failure to bind to STS.    */
DECL|method|maybeInitSTS ()
specifier|private
specifier|synchronized
name|Optional
argument_list|<
name|STSClientFactory
operator|.
name|STSClient
argument_list|>
name|maybeInitSTS
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stsInitAttempted
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
comment|// whether or not it succeeded, the state of the STS client is what
comment|// callers get after the first attempt.
return|return
name|stsClient
return|;
block|}
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|URI
name|uri
init|=
name|getCanonicalUri
argument_list|()
decl_stmt|;
comment|// Ask the owner for any session credentials which it already has
comment|// so that it can just propagate them.
comment|// this call may fail if there are no credentials on the auth
comment|// chain.
comment|// As no codepath (session propagation, STS creation) will work,
comment|// throw this.
specifier|final
name|AWSCredentials
name|parentCredentials
init|=
name|once
argument_list|(
literal|"get credentials"
argument_list|,
literal|""
argument_list|,
parameter_list|()
lambda|->
name|parentAuthChain
operator|.
name|getCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|hasSessionCreds
operator|=
name|parentCredentials
operator|instanceof
name|AWSSessionCredentials
expr_stmt|;
if|if
condition|(
operator|!
name|hasSessionCreds
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating STS client for {}"
argument_list|,
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|invoker
operator|=
operator|new
name|Invoker
argument_list|(
operator|new
name|S3ARetryPolicy
argument_list|(
name|conf
argument_list|)
argument_list|,
name|LOG_EVENT
argument_list|)
expr_stmt|;
name|ClientConfiguration
name|awsConf
init|=
name|S3AUtils
operator|.
name|createAwsConf
argument_list|(
name|conf
argument_list|,
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
name|AWSSecurityTokenService
name|tokenService
init|=
name|STSClientFactory
operator|.
name|builder
argument_list|(
name|parentAuthChain
argument_list|,
name|awsConf
argument_list|,
name|endpoint
argument_list|,
name|region
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|stsClient
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|STSClientFactory
operator|.
name|createClientConnection
argument_list|(
name|tokenService
argument_list|,
name|invoker
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parent-provided session credentials will be propagated"
argument_list|)
expr_stmt|;
name|stsClient
operator|=
name|Optional
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
return|return
name|stsClient
return|;
block|}
comment|/**    * Log retries at debug.    */
DECL|field|LOG_EVENT
specifier|public
specifier|static
specifier|final
name|Invoker
operator|.
name|Retried
name|LOG_EVENT
init|=
parameter_list|(
name|text
parameter_list|,
name|exception
parameter_list|,
name|retries
parameter_list|,
name|idempotent
parameter_list|)
lambda|->
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"{}: "
operator|+
name|exception
argument_list|,
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|retries
operator|==
literal|1
condition|)
block|{
comment|// stack on first attempt, to keep noise down
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}: "
operator|+
name|exception
argument_list|,
name|text
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Get the client to AWS STS.    * @return the STS client, when successfully inited.    * @throws IOException any failure to bind to STS.    */
DECL|method|prepareSTSClient ()
specifier|protected
name|Optional
argument_list|<
name|STSClientFactory
operator|.
name|STSClient
argument_list|>
name|prepareSTSClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|maybeInitSTS
argument_list|()
return|;
block|}
comment|/**    * Duration of sessions.    * @return duration in seconds.    */
DECL|method|getDuration ()
specifier|public
name|long
name|getDuration
parameter_list|()
block|{
return|return
name|duration
return|;
block|}
annotation|@
name|Override
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|createTokenIdentifier ( final Optional<RoleModel.Policy> policy, final EncryptionSecrets encryptionSecrets)
specifier|public
name|SessionTokenIdentifier
name|createTokenIdentifier
parameter_list|(
specifier|final
name|Optional
argument_list|<
name|RoleModel
operator|.
name|Policy
argument_list|>
name|policy
parameter_list|,
specifier|final
name|EncryptionSecrets
name|encryptionSecrets
parameter_list|)
throws|throws
name|IOException
block|{
name|requireServiceStarted
argument_list|()
expr_stmt|;
specifier|final
name|MarshalledCredentials
name|marshalledCredentials
decl_stmt|;
name|String
name|origin
init|=
name|AbstractS3ATokenIdentifier
operator|.
name|createDefaultOriginMessage
argument_list|()
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|STSClientFactory
operator|.
name|STSClient
argument_list|>
name|client
init|=
name|prepareSTSClient
argument_list|()
decl_stmt|;
if|if
condition|(
name|client
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// this is the normal route: ask for a new STS token
name|marshalledCredentials
operator|=
name|fromSTSCredentials
argument_list|(
name|client
operator|.
name|get
argument_list|()
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
expr_stmt|;
block|}
else|else
block|{
comment|// get a new set of parental session credentials (pick up IAM refresh)
if|if
condition|(
operator|!
name|forwardMessageLogged
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
comment|// warn caller on the first -and only the first- use.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Forwarding existing session credentials to {}"
operator|+
literal|" -duration unknown"
argument_list|,
name|getCanonicalUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|origin
operator|+=
literal|" "
operator|+
name|CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN
expr_stmt|;
specifier|final
name|AWSCredentials
name|awsCredentials
init|=
name|parentAuthChain
operator|.
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|awsCredentials
operator|instanceof
name|AWSSessionCredentials
condition|)
block|{
name|marshalledCredentials
operator|=
name|fromAWSCredentials
argument_list|(
operator|(
name|AWSSessionCredentials
operator|)
name|awsCredentials
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DelegationTokenIOException
argument_list|(
literal|"AWS Authentication chain is no longer supplying session secrets"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|SessionTokenIdentifier
argument_list|(
name|getKind
argument_list|()
argument_list|,
name|getOwnerText
argument_list|()
argument_list|,
name|getCanonicalUri
argument_list|()
argument_list|,
name|marshalledCredentials
argument_list|,
name|encryptionSecrets
argument_list|,
name|origin
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createEmptyIdentifier ()
specifier|public
name|SessionTokenIdentifier
name|createEmptyIdentifier
parameter_list|()
block|{
return|return
operator|new
name|SessionTokenIdentifier
argument_list|()
return|;
block|}
comment|/**    * Expiration date time as passed in from source.    * If unset, either we are unbound, or the token which came in does not    * know its expiry.    * @return expiration data time.    */
DECL|method|getExpirationDateTime ()
specifier|protected
name|Optional
argument_list|<
name|OffsetDateTime
argument_list|>
name|getExpirationDateTime
parameter_list|()
block|{
return|return
name|expirationDateTime
return|;
block|}
DECL|method|setExpirationDateTime ( Optional<OffsetDateTime> expirationDateTime)
specifier|protected
name|void
name|setExpirationDateTime
parameter_list|(
name|Optional
argument_list|<
name|OffsetDateTime
argument_list|>
name|expirationDateTime
parameter_list|)
block|{
name|this
operator|.
name|expirationDateTime
operator|=
name|expirationDateTime
expr_stmt|;
block|}
comment|/**    * Token identifier bound to.    * @return token identifier.    */
DECL|method|getTokenIdentifier ()
specifier|protected
name|Optional
argument_list|<
name|SessionTokenIdentifier
argument_list|>
name|getTokenIdentifier
parameter_list|()
block|{
return|return
name|tokenIdentifier
return|;
block|}
DECL|method|setTokenIdentifier (Optional<SessionTokenIdentifier> tokenIdentifier)
specifier|protected
name|void
name|setTokenIdentifier
parameter_list|(
name|Optional
argument_list|<
name|SessionTokenIdentifier
argument_list|>
name|tokenIdentifier
parameter_list|)
block|{
name|this
operator|.
name|tokenIdentifier
operator|=
name|tokenIdentifier
expr_stmt|;
block|}
block|}
end_class

end_unit

