begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.extensions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|extensions
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|io
operator|.
name|PrintStream
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|FileSystem
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
name|Path
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
name|azurebfs
operator|.
name|AbstractAbfsIntegrationTest
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
name|azurebfs
operator|.
name|AzureBlobFileSystem
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
name|mapreduce
operator|.
name|security
operator|.
name|TokenCache
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
name|SecurityUtil
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
name|UserGroupInformation
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
name|DtUtilShell
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|ServiceOperations
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
name|StringUtils
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
name|ToolRunner
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
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_TOKEN_SERVICE_USE_IP
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
name|test
operator|.
name|LambdaTestUtils
operator|.
name|doAs
import|;
end_import

begin_comment
comment|/**  * Test custom DT support in ABFS.  * This brings up a mini KDC in class setup/teardown, as the FS checks  * for that when it enables security.  *  * Much of this code is copied from  * {@code org.apache.hadoop.fs.s3a.auth.delegation.AbstractDelegationIT}  */
end_comment

begin_class
DECL|class|ITestAbfsDelegationTokens
specifier|public
class|class
name|ITestAbfsDelegationTokens
extends|extends
name|AbstractAbfsIntegrationTest
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
name|ITestAbfsDelegationTokens
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Created in static {@link #setupCluster()} call.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StaticNonFinalField"
argument_list|)
DECL|field|cluster
specifier|private
specifier|static
name|KerberizedAbfsCluster
name|cluster
decl_stmt|;
DECL|field|aliceUser
specifier|private
name|UserGroupInformation
name|aliceUser
decl_stmt|;
comment|/***    * Set up the clusters.    */
annotation|@
name|BeforeClass
DECL|method|setupCluster ()
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|resetUGI
argument_list|()
expr_stmt|;
name|cluster
operator|=
operator|new
name|KerberizedAbfsCluster
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tear down the Cluster.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ThrowableNotThrown"
argument_list|)
annotation|@
name|AfterClass
DECL|method|teardownCluster ()
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|resetUGI
argument_list|()
expr_stmt|;
name|ServiceOperations
operator|.
name|stopQuietly
argument_list|(
name|LOG
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
DECL|method|ITestAbfsDelegationTokens ()
specifier|public
name|ITestAbfsDelegationTokens
parameter_list|()
throws|throws
name|Exception
block|{   }
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
comment|// create the FS
name|Configuration
name|conf
init|=
name|getRawConfiguration
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|bindConfToCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HADOOP_SECURITY_TOKEN_SERVICE_USE_IP
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|resetUGI
argument_list|()
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|aliceUser
operator|=
name|cluster
operator|.
name|createAliceUser
argument_list|()
expr_stmt|;
name|assertSecurityEnabled
argument_list|()
expr_stmt|;
comment|// log in as alice so that filesystems belong to that user
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|aliceUser
argument_list|)
expr_stmt|;
name|StubDelegationTokenManager
operator|.
name|useStubDTManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|closeAllForUGI
argument_list|(
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"No StubDelegationTokenManager created in filesystem init"
argument_list|,
name|getStubDTManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getStubDTManager ()
specifier|protected
name|StubDelegationTokenManager
name|getStubDTManager
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|StubDelegationTokenManager
operator|)
name|getDelegationTokenManager
argument_list|()
operator|.
name|getTokenManager
argument_list|()
return|;
block|}
comment|/**    * Cleanup removes cached filesystems and the last instance of the    * StubDT manager.    */
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
comment|// clean up all of alice's instances.
name|FileSystem
operator|.
name|closeAllForUGI
argument_list|(
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
comment|/**    * General assertion that security is turred on for a cluster.    */
DECL|method|assertSecurityEnabled ()
specifier|public
specifier|static
name|void
name|assertSecurityEnabled
parameter_list|()
block|{
name|assertTrue
argument_list|(
literal|"Security is needed for this test"
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reset UGI info.    */
DECL|method|resetUGI ()
specifier|protected
specifier|static
name|void
name|resetUGI
parameter_list|()
block|{
name|UserGroupInformation
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create credentials with the DTs of the given FS.    * @param fs filesystem    * @return a non-empty set of credentials.    * @throws IOException failure to create.    */
DECL|method|mkTokens (final FileSystem fs)
specifier|protected
specifier|static
name|Credentials
name|mkTokens
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|Credentials
name|cred
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|fs
operator|.
name|addDelegationTokens
argument_list|(
literal|"rm/rm1@EXAMPLE.COM"
argument_list|,
name|cred
argument_list|)
expr_stmt|;
return|return
name|cred
return|;
block|}
annotation|@
name|Test
DECL|method|testTokenManagerBinding ()
specifier|public
name|void
name|testTokenManagerBinding
parameter_list|()
throws|throws
name|Throwable
block|{
name|StubDelegationTokenManager
name|instance
init|=
name|getStubDTManager
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No StubDelegationTokenManager created in filesystem init"
argument_list|,
name|instance
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token manager not initialized: "
operator|+
name|instance
argument_list|,
name|instance
operator|.
name|isInitialized
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * When bound to a custom DT manager, it provides the service name.    * The stub returns the URI by default.    */
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
name|String
name|service
init|=
name|getCanonicalServiceName
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No canonical service name from filesystem "
operator|+
name|getFileSystem
argument_list|()
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"canonical URI and service name mismatch"
argument_list|,
name|getFilesystemURI
argument_list|()
argument_list|,
operator|new
name|URI
argument_list|(
name|service
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getFilesystemURI ()
specifier|protected
name|URI
name|getFilesystemURI
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
return|;
block|}
DECL|method|getCanonicalServiceName ()
specifier|protected
name|String
name|getCanonicalServiceName
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|getCanonicalServiceName
argument_list|()
return|;
block|}
comment|/**    * Checks here to catch any regressions in canonicalization    * logic.    */
annotation|@
name|Test
DECL|method|testDefaultCanonicalization ()
specifier|public
name|void
name|testDefaultCanonicalization
parameter_list|()
throws|throws
name|Throwable
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|clearTokenServiceName
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"canonicalServiceName is not the default"
argument_list|,
name|getDefaultServiceName
argument_list|(
name|fs
argument_list|)
argument_list|,
name|getCanonicalServiceName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getDefaultServiceName (final FileSystem fs)
specifier|protected
name|String
name|getDefaultServiceName
parameter_list|(
specifier|final
name|FileSystem
name|fs
parameter_list|)
block|{
return|return
name|SecurityUtil
operator|.
name|buildDTServiceName
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|clearTokenServiceName ()
specifier|protected
name|void
name|clearTokenServiceName
parameter_list|()
throws|throws
name|IOException
block|{
name|getStubDTManager
argument_list|()
operator|.
name|setCanonicalServiceName
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Request a token; this tests the collection workflow.    */
annotation|@
name|Test
DECL|method|testRequestToken ()
specifier|public
name|void
name|testRequestToken
parameter_list|()
throws|throws
name|Throwable
block|{
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Credentials
name|credentials
init|=
name|mkTokens
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of collected tokens"
argument_list|,
literal|1
argument_list|,
name|credentials
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCredentialsContainsToken
argument_list|(
name|credentials
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Request a token; this tests the collection workflow.    */
annotation|@
name|Test
DECL|method|testRequestTokenDefault ()
specifier|public
name|void
name|testRequestTokenDefault
parameter_list|()
throws|throws
name|Throwable
block|{
name|clearTokenServiceName
argument_list|()
expr_stmt|;
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"canonicalServiceName is not the default"
argument_list|,
name|getDefaultServiceName
argument_list|(
name|fs
argument_list|)
argument_list|,
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
argument_list|)
expr_stmt|;
name|Credentials
name|credentials
init|=
name|mkTokens
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of collected tokens"
argument_list|,
literal|1
argument_list|,
name|credentials
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCredentialsContainsToken
argument_list|(
name|credentials
argument_list|,
name|getDefaultServiceName
argument_list|(
name|fs
argument_list|)
argument_list|,
name|getFilesystemURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyCredentialsContainsToken (final Credentials credentials, FileSystem fs)
specifier|public
name|void
name|verifyCredentialsContainsToken
parameter_list|(
specifier|final
name|Credentials
name|credentials
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyCredentialsContainsToken
argument_list|(
name|credentials
argument_list|,
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
argument_list|,
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that the set of credentials contains a token for the given    * canonical service name, and that it is of the given kind.    * @param credentials set of credentials    * @param serviceName canonical service name for lookup.    * @param tokenService service kind; also expected in string value.    * @return the retrieved token.    * @throws IOException IO failure    */
DECL|method|verifyCredentialsContainsToken ( final Credentials credentials, final String serviceName, final String tokenService)
specifier|public
name|StubAbfsTokenIdentifier
name|verifyCredentialsContainsToken
parameter_list|(
specifier|final
name|Credentials
name|credentials
parameter_list|,
specifier|final
name|String
name|serviceName
parameter_list|,
specifier|final
name|String
name|tokenService
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
init|=
name|credentials
operator|.
name|getToken
argument_list|(
operator|new
name|Text
argument_list|(
name|serviceName
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Token Kind in "
operator|+
name|token
argument_list|,
name|StubAbfsTokenIdentifier
operator|.
name|TOKEN_KIND
argument_list|,
name|token
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Token Service Kind in "
operator|+
name|token
argument_list|,
name|tokenService
argument_list|,
name|token
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|StubAbfsTokenIdentifier
name|abfsId
init|=
operator|(
name|StubAbfsTokenIdentifier
operator|)
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created token {}"
argument_list|,
name|abfsId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"token URI in "
operator|+
name|abfsId
argument_list|,
name|tokenService
argument_list|,
name|abfsId
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|abfsId
return|;
block|}
comment|/**    * This mimics the DT collection performed inside FileInputFormat to    * collect DTs for a job.    * @throws Throwable on failure.    */
annotation|@
name|Test
DECL|method|testJobsCollectTokens ()
specifier|public
name|void
name|testJobsCollectTokens
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// get tokens for all the required FileSystems..
name|AzureBlobFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Path
name|root
init|=
name|fs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
index|[]
name|paths
init|=
block|{
name|root
block|}
decl_stmt|;
name|Configuration
name|conf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|TokenCache
operator|.
name|obtainTokensForNamenodes
argument_list|(
name|credentials
argument_list|,
name|paths
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|verifyCredentialsContainsToken
argument_list|(
name|credentials
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run the DT Util command.    * @param expected expected outcome    * @param conf configuration for the command (hence: FS to create)    * @param args other arguments    * @return the output of the command.    */
DECL|method|dtutil (final int expected, final Configuration conf, final String... args)
specifier|protected
name|String
name|dtutil
parameter_list|(
specifier|final
name|int
name|expected
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ByteArrayOutputStream
name|dtUtilContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DtUtilShell
name|dt
init|=
operator|new
name|DtUtilShell
argument_list|()
decl_stmt|;
name|dt
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|dtUtilContent
argument_list|)
argument_list|)
expr_stmt|;
name|dtUtilContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|r
init|=
name|doAs
argument_list|(
name|aliceUser
argument_list|,
parameter_list|()
lambda|->
name|ToolRunner
operator|.
name|run
argument_list|(
name|conf
argument_list|,
name|dt
argument_list|,
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|dtUtilContent
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"\n{}"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Exit code from command dtutil "
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|" "
argument_list|,
name|args
argument_list|)
operator|+
literal|" with output "
operator|+
name|s
argument_list|,
name|expected
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
comment|/**    * Verify the dtutil shell command can fetch tokens    */
annotation|@
name|Test
DECL|method|testDTUtilShell ()
specifier|public
name|void
name|testDTUtilShell
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|tokenfile
init|=
name|cluster
operator|.
name|createTempTokenFile
argument_list|()
decl_stmt|;
name|String
name|tfs
init|=
name|tokenfile
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|fsURI
init|=
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|dtutil
argument_list|(
literal|0
argument_list|,
name|getRawConfiguration
argument_list|()
argument_list|,
literal|"get"
argument_list|,
name|fsURI
argument_list|,
literal|"-format"
argument_list|,
literal|"protobuf"
argument_list|,
name|tfs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"not created: "
operator|+
name|tokenfile
argument_list|,
name|tokenfile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File is empty "
operator|+
name|tokenfile
argument_list|,
name|tokenfile
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"File only contains header "
operator|+
name|tokenfile
argument_list|,
name|tokenfile
operator|.
name|length
argument_list|()
operator|>
literal|6
argument_list|)
expr_stmt|;
name|String
name|printed
init|=
name|dtutil
argument_list|(
literal|0
argument_list|,
name|getRawConfiguration
argument_list|()
argument_list|,
literal|"print"
argument_list|,
name|tfs
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"no "
operator|+
name|fsURI
operator|+
literal|" in "
operator|+
name|printed
argument_list|,
name|printed
operator|.
name|contains
argument_list|(
name|fsURI
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no "
operator|+
name|StubAbfsTokenIdentifier
operator|.
name|ID
operator|+
literal|" in "
operator|+
name|printed
argument_list|,
name|printed
operator|.
name|contains
argument_list|(
name|StubAbfsTokenIdentifier
operator|.
name|ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new FS instance with the simplest binding lifecycle;    * get a token.    * This verifies the classic binding mechanism works.    */
annotation|@
name|Test
DECL|method|testBaseDTLifecycle ()
specifier|public
name|void
name|testBaseDTLifecycle
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|getRawConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|ClassicDelegationTokenManager
operator|.
name|useClassicDTManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
init|(
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|getFilesystemURI
argument_list|()
argument_list|,
name|conf
argument_list|)
init|)
block|{
name|Credentials
name|credentials
init|=
name|mkTokens
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of collected tokens"
argument_list|,
literal|1
argument_list|,
name|credentials
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCredentialsContainsToken
argument_list|(
name|credentials
argument_list|,
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
argument_list|,
name|ClassicDelegationTokenManager
operator|.
name|UNSET
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

