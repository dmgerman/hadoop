begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|File
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
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|S3AEncryptionMethods
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
name|fs
operator|.
name|s3a
operator|.
name|TemporaryAWSCredentialsProvider
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|S3ATestUtils
operator|.
name|assumeSessionTestsEnabled
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
name|S3ATestUtils
operator|.
name|roundTrip
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
name|S3ATestUtils
operator|.
name|unsetHadoopCredentialProviders
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
name|delegation
operator|.
name|DelegationConstants
operator|.
name|DELEGATION_TOKEN_CREDENTIALS_PROVIDER
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
name|DELEGATION_TOKEN_SESSION_BINDING
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
name|SESSION_TOKEN_KIND
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
name|SessionTokenBinding
operator|.
name|CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN
import|;
end_import

begin_comment
comment|/**  * Tests use of Hadoop delegation tokens to marshall S3 credentials.  */
end_comment

begin_class
DECL|class|ITestSessionDelegationTokens
specifier|public
class|class
name|ITestSessionDelegationTokens
extends|extends
name|AbstractDelegationIT
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
name|ITestSessionDelegationTokens
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KMS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KMS_KEY
init|=
literal|"arn:kms:key"
decl_stmt|;
DECL|field|delegationTokens
specifier|private
name|S3ADelegationTokens
name|delegationTokens
decl_stmt|;
comment|/**    * Get the delegation token binding for this test suite.    * @return which DT binding to use.    */
DECL|method|getDelegationBinding ()
specifier|protected
name|String
name|getDelegationBinding
parameter_list|()
block|{
return|return
name|DELEGATION_TOKEN_SESSION_BINDING
return|;
block|}
DECL|method|getTokenKind ()
specifier|public
name|Text
name|getTokenKind
parameter_list|()
block|{
return|return
name|SESSION_TOKEN_KIND
return|;
block|}
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|enableDelegationTokens
argument_list|(
name|conf
argument_list|,
name|getDelegationBinding
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|assumeSessionTestsEnabled
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|resetUGI
argument_list|()
expr_stmt|;
name|delegationTokens
operator|=
name|instantiateDTSupport
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|delegationTokens
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|delegationTokens
argument_list|)
expr_stmt|;
name|resetUGI
argument_list|()
expr_stmt|;
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Checks here to catch any regressions in canonicalization    * logic.    */
annotation|@
name|Test
DECL|method|testCanonicalization ()
specifier|public
name|void
name|testCanonicalization
parameter_list|()
throws|throws
name|Throwable
block|{
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Default port has changed"
argument_list|,
literal|0
argument_list|,
name|fs
operator|.
name|getDefaultPort
argument_list|()
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|fs
operator|.
name|getCanonicalUri
argument_list|()
decl_stmt|;
name|String
name|service
init|=
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"canonical URI and service name mismatch"
argument_list|,
name|uri
argument_list|,
operator|new
name|URI
argument_list|(
name|service
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSaveLoadTokens ()
specifier|public
name|void
name|testSaveLoadTokens
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|tokenFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"token"
argument_list|,
literal|"bin"
argument_list|)
decl_stmt|;
name|EncryptionSecrets
name|encryptionSecrets
init|=
operator|new
name|EncryptionSecrets
argument_list|(
name|S3AEncryptionMethods
operator|.
name|SSE_KMS
argument_list|,
name|KMS_KEY
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AbstractS3ATokenIdentifier
argument_list|>
name|dt
init|=
name|delegationTokens
operator|.
name|createDelegationToken
argument_list|(
name|encryptionSecrets
argument_list|)
decl_stmt|;
specifier|final
name|SessionTokenIdentifier
name|origIdentifier
init|=
operator|(
name|SessionTokenIdentifier
operator|)
name|dt
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"kind in "
operator|+
name|dt
argument_list|,
name|getTokenKind
argument_list|()
argument_list|,
name|dt
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|getConfiguration
argument_list|()
decl_stmt|;
name|saveDT
argument_list|(
name|tokenFile
argument_list|,
name|dt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Empty token file"
argument_list|,
name|tokenFile
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
name|Credentials
operator|.
name|readTokenStorageFile
argument_list|(
name|tokenFile
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Text
name|serviceId
init|=
name|delegationTokens
operator|.
name|getService
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
init|=
name|requireNonNull
argument_list|(
name|creds
operator|.
name|getToken
argument_list|(
name|serviceId
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"No token for \""
operator|+
name|serviceId
operator|+
literal|"\" in: "
operator|+
name|creds
operator|.
name|getAllTokens
argument_list|()
argument_list|)
decl_stmt|;
name|SessionTokenIdentifier
name|decoded
init|=
operator|(
name|SessionTokenIdentifier
operator|)
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|decoded
operator|.
name|validate
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"token identifier "
argument_list|,
name|origIdentifier
argument_list|,
name|decoded
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Origin in "
operator|+
name|decoded
argument_list|,
name|origIdentifier
operator|.
name|getOrigin
argument_list|()
argument_list|,
name|decoded
operator|.
name|getOrigin
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expiry time"
argument_list|,
name|origIdentifier
operator|.
name|getExpiryTime
argument_list|()
argument_list|,
name|decoded
operator|.
name|getExpiryTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Encryption Secrets"
argument_list|,
name|encryptionSecrets
argument_list|,
name|decoded
operator|.
name|getEncryptionSecrets
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This creates a DT from a set of credentials, then verifies    * that you can use the round-tripped credentials as a source of    * authentication for another DT binding, and when    * that is asked for a DT token, the secrets it returns are    * the same as the original.    *    * That is different from DT propagation, as here the propagation    * is by setting the fs.s3a session/secret/id keys from the marshalled    * values, and using session token auth.    * This verifies that session token authentication can be used    * for DT credential auth, and that new tokens aren't created.    *    * From a testing perspective, this is not as "good" as having    * separate tests, but given the effort to create session tokens    * is all hidden in the first FS, it is actually easier to write    * and now forms an extra test on those generated tokens as well    * as the marshalling.    */
annotation|@
name|Test
DECL|method|testCreateAndUseDT ()
specifier|public
name|void
name|testCreateAndUseDT
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Create a Delegation Token, round trip then reuse"
argument_list|)
expr_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Current User has delegation token"
argument_list|,
name|delegationTokens
operator|.
name|selectTokenFromFSOwner
argument_list|()
argument_list|)
expr_stmt|;
name|EncryptionSecrets
name|secrets
init|=
operator|new
name|EncryptionSecrets
argument_list|(
name|S3AEncryptionMethods
operator|.
name|SSE_KMS
argument_list|,
name|KMS_KEY
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AbstractS3ATokenIdentifier
argument_list|>
name|originalDT
init|=
name|delegationTokens
operator|.
name|createDelegationToken
argument_list|(
name|secrets
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Token kind mismatch"
argument_list|,
name|getTokenKind
argument_list|()
argument_list|,
name|originalDT
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
comment|// decode to get the binding info
name|SessionTokenIdentifier
name|issued
init|=
name|requireNonNull
argument_list|(
operator|(
name|SessionTokenIdentifier
operator|)
name|originalDT
operator|.
name|decodeIdentifier
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"no identifier in "
operator|+
name|originalDT
argument_list|)
decl_stmt|;
name|issued
operator|.
name|validate
argument_list|()
expr_stmt|;
specifier|final
name|MarshalledCredentials
name|creds
decl_stmt|;
try|try
init|(
name|S3ADelegationTokens
name|dt2
init|=
name|instantiateDTSupport
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
init|)
block|{
name|dt2
operator|.
name|start
argument_list|()
expr_stmt|;
name|dt2
operator|.
name|resetTokenBindingToDT
argument_list|(
name|originalDT
argument_list|)
expr_stmt|;
specifier|final
name|AWSSessionCredentials
name|awsSessionCreds
init|=
name|verifySessionCredentials
argument_list|(
name|dt2
operator|.
name|getCredentialProviders
argument_list|()
operator|.
name|getCredentials
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|MarshalledCredentials
name|origCreds
init|=
name|fromAWSCredentials
argument_list|(
name|awsSessionCreds
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|AbstractS3ATokenIdentifier
argument_list|>
name|boundDT
init|=
name|dt2
operator|.
name|getBoundOrNewDT
argument_list|(
name|secrets
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Delegation Tokens"
argument_list|,
name|originalDT
argument_list|,
name|boundDT
argument_list|)
expr_stmt|;
comment|// simulate marshall and transmission
name|creds
operator|=
name|roundTrip
argument_list|(
name|origCreds
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|SessionTokenIdentifier
name|reissued
init|=
operator|(
name|SessionTokenIdentifier
operator|)
name|dt2
operator|.
name|createDelegationToken
argument_list|(
name|secrets
argument_list|)
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|reissued
operator|.
name|validate
argument_list|()
expr_stmt|;
name|String
name|userAgentField
init|=
name|dt2
operator|.
name|getUserAgentField
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"UA field does not contain UUID"
argument_list|,
name|userAgentField
argument_list|,
name|Matchers
operator|.
name|containsString
argument_list|(
name|issued
operator|.
name|getUuid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now use those chained credentials to create a new FS instance
comment|// and then get a session DT from it and expect equality
name|verifyCredentialPropagation
argument_list|(
name|fs
argument_list|,
name|creds
argument_list|,
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This verifies that AWS Session credentials can be picked up and    * returned in a DT.    * With a session binding, this holds; for role binding it will fail.    * @param fs base FS to bond to.    * @param session session credentials from first DT.    * @param conf config to use    * @return the retrieved DT. This is only for error reporting.    * @throws IOException failure.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"OptionalGetWithoutIsPresent"
argument_list|)
DECL|method|verifyCredentialPropagation ( final S3AFileSystem fs, final MarshalledCredentials session, final Configuration conf)
specifier|protected
name|AbstractS3ATokenIdentifier
name|verifyCredentialPropagation
parameter_list|(
specifier|final
name|S3AFileSystem
name|fs
parameter_list|,
specifier|final
name|MarshalledCredentials
name|session
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|describe
argument_list|(
literal|"Verify Token Propagation"
argument_list|)
expr_stmt|;
comment|// clear any credential paths to ensure they don't get picked up and used
comment|// for authentication.
name|unsetHadoopCredentialProviders
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DELEGATION_TOKEN_CREDENTIALS_PROVIDER
argument_list|,
name|TemporaryAWSCredentialsProvider
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|session
operator|.
name|setSecretsInConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
init|(
name|S3ADelegationTokens
name|delegationTokens2
init|=
operator|new
name|S3ADelegationTokens
argument_list|()
init|)
block|{
name|delegationTokens2
operator|.
name|bindToFileSystem
argument_list|(
name|fs
operator|.
name|getCanonicalUri
argument_list|()
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|delegationTokens2
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|delegationTokens2
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Token
argument_list|<
name|AbstractS3ATokenIdentifier
argument_list|>
name|newDT
init|=
name|delegationTokens2
operator|.
name|getBoundOrNewDT
argument_list|(
operator|new
name|EncryptionSecrets
argument_list|()
argument_list|)
decl_stmt|;
name|delegationTokens2
operator|.
name|resetTokenBindingToDT
argument_list|(
name|newDT
argument_list|)
expr_stmt|;
specifier|final
name|AbstractS3ATokenIdentifier
name|boundId
init|=
name|delegationTokens2
operator|.
name|getDecodedIdentifier
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Regenerated DT is {}"
argument_list|,
name|newDT
argument_list|)
expr_stmt|;
specifier|final
name|MarshalledCredentials
name|creds2
init|=
name|fromAWSCredentials
argument_list|(
name|verifySessionCredentials
argument_list|(
name|delegationTokens2
operator|.
name|getCredentialProviders
argument_list|()
operator|.
name|getCredentials
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Credentials"
argument_list|,
name|session
argument_list|,
name|creds2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Origin in "
operator|+
name|boundId
argument_list|,
name|boundId
operator|.
name|getOrigin
argument_list|()
operator|.
name|contains
argument_list|(
name|CREDENTIALS_CONVERTED_TO_DELEGATION_TOKEN
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|boundId
return|;
block|}
block|}
DECL|method|verifySessionCredentials ( final AWSCredentials creds)
specifier|private
name|AWSSessionCredentials
name|verifySessionCredentials
parameter_list|(
specifier|final
name|AWSCredentials
name|creds
parameter_list|)
block|{
name|AWSSessionCredentials
name|session
init|=
operator|(
name|AWSSessionCredentials
operator|)
name|creds
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"access key"
argument_list|,
name|session
operator|.
name|getAWSAccessKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"secret key"
argument_list|,
name|session
operator|.
name|getAWSSecretKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"session token"
argument_list|,
name|session
operator|.
name|getSessionToken
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|session
return|;
block|}
annotation|@
name|Test
DECL|method|testDBindingReentrancyLock ()
specifier|public
name|void
name|testDBindingReentrancyLock
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Verify that S3ADelegationTokens cannot be bound twice when there"
operator|+
literal|" is no token"
argument_list|)
expr_stmt|;
name|S3ADelegationTokens
name|delegation
init|=
name|instantiateDTSupport
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|delegation
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Delegation is bound to a DT: "
operator|+
name|delegation
argument_list|,
name|delegation
operator|.
name|isBoundToDT
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

