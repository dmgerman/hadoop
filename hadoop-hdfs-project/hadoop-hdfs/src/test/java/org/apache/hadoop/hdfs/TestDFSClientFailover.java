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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|fs
operator|.
name|FileContext
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
name|hdfs
operator|.
name|HAUtil
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|server
operator|.
name|namenode
operator|.
name|ha
operator|.
name|ConfiguredFailoverProxyProvider
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
name|server
operator|.
name|namenode
operator|.
name|ha
operator|.
name|IPFailoverProxyProvider
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
name|server
operator|.
name|namenode
operator|.
name|ha
operator|.
name|HATestUtil
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
name|retry
operator|.
name|DefaultFailoverProxyProvider
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
name|FailoverProxyProvider
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
name|net
operator|.
name|ConnectTimeoutException
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
name|net
operator|.
name|StandardSocketFactory
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
name|test
operator|.
name|GenericTestUtils
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
name|hamcrest
operator|.
name|BaseMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|net
operator|.
name|spi
operator|.
name|nameservice
operator|.
name|NameService
import|;
end_import

begin_class
DECL|class|TestDFSClientFailover
specifier|public
class|class
name|TestDFSClientFailover
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDFSClientFailover
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_FILE
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_FILE
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/failover-test-file"
argument_list|)
decl_stmt|;
DECL|field|FILE_LENGTH_TO_VERIFY
specifier|private
specifier|static
specifier|final
name|int
name|FILE_LENGTH_TO_VERIFY
init|=
literal|100
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
annotation|@
name|Before
DECL|method|setUpCluster ()
specifier|public
name|void
name|setUpCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDownCluster ()
specifier|public
name|void
name|tearDownCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|clearConfig ()
specifier|public
name|void
name|clearConfig
parameter_list|()
block|{
name|SecurityUtil
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure that client failover works when an active NN dies and the standby    * takes over.    */
annotation|@
name|Test
DECL|method|testDfsClientFailover ()
specifier|public
name|void
name|testDfsClientFailover
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|TEST_FILE
argument_list|,
name|FILE_LENGTH_TO_VERIFY
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TEST_FILE
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|,
name|FILE_LENGTH_TO_VERIFY
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TEST_FILE
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|,
name|FILE_LENGTH_TO_VERIFY
argument_list|)
expr_stmt|;
comment|// Check that it functions even if the URL becomes canonicalized
comment|// to include a port number.
name|Path
name|withPort
init|=
operator|new
name|Path
argument_list|(
literal|"hdfs://"
operator|+
name|HATestUtil
operator|.
name|getLogicalHostname
argument_list|(
name|cluster
argument_list|)
operator|+
literal|":"
operator|+
name|NameNode
operator|.
name|DEFAULT_PORT
operator|+
literal|"/"
operator|+
name|TEST_FILE
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|FileSystem
name|fs2
init|=
name|withPort
operator|.
name|getFileSystem
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fs2
operator|.
name|exists
argument_list|(
name|withPort
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that even a non-idempotent method will properly fail-over if the    * first IPC attempt times out trying to connect. Regression test for    * HDFS-4404.     */
annotation|@
name|Test
DECL|method|testFailoverOnConnectTimeout ()
specifier|public
name|void
name|testFailoverOnConnectTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_RPC_SOCKET_FACTORY_CLASS_DEFAULT_KEY
argument_list|,
name|InjectingSocketFactory
operator|.
name|class
argument_list|,
name|SocketFactory
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Set up the InjectingSocketFactory to throw a ConnectTimeoutException
comment|// when connecting to the first NN.
name|InjectingSocketFactory
operator|.
name|portToInjectOn
operator|=
name|cluster
operator|.
name|getNameNodePort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Make the second NN the active one.
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Call a non-idempotent method, and ensure the failover of the call proceeds
comment|// successfully.
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|fs
operator|.
name|create
argument_list|(
name|TEST_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|InjectingSocketFactory
specifier|private
specifier|static
class|class
name|InjectingSocketFactory
extends|extends
name|StandardSocketFactory
block|{
DECL|field|defaultFactory
specifier|static
specifier|final
name|SocketFactory
name|defaultFactory
init|=
name|SocketFactory
operator|.
name|getDefault
argument_list|()
decl_stmt|;
DECL|field|portToInjectOn
specifier|static
name|int
name|portToInjectOn
decl_stmt|;
annotation|@
name|Override
DECL|method|createSocket ()
specifier|public
name|Socket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
name|Socket
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|defaultFactory
operator|.
name|createSocket
argument_list|()
argument_list|)
decl_stmt|;
comment|// Simplify our spying job by not having to also spy on the channel
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|getChannel
argument_list|()
expr_stmt|;
comment|// Throw a ConnectTimeoutException when connecting to our target "bad"
comment|// host.
name|Mockito
operator|.
name|doThrow
argument_list|(
operator|new
name|ConnectTimeoutException
argument_list|(
literal|"injected"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|connect
argument_list|(
name|Mockito
operator|.
name|argThat
argument_list|(
operator|new
name|MatchesPort
argument_list|()
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spy
return|;
block|}
DECL|class|MatchesPort
specifier|private
class|class
name|MatchesPort
extends|extends
name|BaseMatcher
argument_list|<
name|SocketAddress
argument_list|>
block|{
annotation|@
name|Override
DECL|method|matches (Object arg0)
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
return|return
operator|(
operator|(
name|InetSocketAddress
operator|)
name|arg0
operator|)
operator|.
name|getPort
argument_list|()
operator|==
name|portToInjectOn
return|;
block|}
annotation|@
name|Override
DECL|method|describeTo (Description desc)
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|desc
parameter_list|)
block|{
name|desc
operator|.
name|appendText
argument_list|(
literal|"matches port "
operator|+
name|portToInjectOn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Regression test for HDFS-2683.    */
annotation|@
name|Test
DECL|method|testLogicalUriShouldNotHavePorts ()
specifier|public
name|void
name|testLogicalUriShouldNotHavePorts
parameter_list|()
block|{
name|Configuration
name|config
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|logicalName
init|=
name|HATestUtil
operator|.
name|getLogicalHostname
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|HATestUtil
operator|.
name|setFailoverConfigurations
argument_list|(
name|cluster
argument_list|,
name|config
argument_list|,
name|logicalName
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"hdfs://"
operator|+
name|logicalName
operator|+
literal|":12345/"
argument_list|)
decl_stmt|;
try|try
block|{
name|p
operator|.
name|getFileSystem
argument_list|(
name|config
argument_list|)
operator|.
name|exists
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail with fake FS"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"does not use port information"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Make sure that a helpful error message is shown if a proxy provider is    * configured for a given URI, but no actual addresses are configured for that    * URI.    */
annotation|@
name|Test
DECL|method|testFailureWithMisconfiguredHaNNs ()
specifier|public
name|void
name|testFailureWithMisconfiguredHaNNs
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|logicalHost
init|=
literal|"misconfigured-ha-uri"
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
operator|+
literal|"."
operator|+
name|logicalHost
argument_list|,
name|ConfiguredFailoverProxyProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"hdfs://"
operator|+
name|logicalHost
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
try|try
block|{
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Successfully got proxy provider for misconfigured FS"
argument_list|)
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
name|info
argument_list|(
literal|"got expected exception"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected exception did not contain helpful message"
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
operator|.
name|contains
argument_list|(
literal|"Could not find any configured addresses for URI "
operator|+
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Spy on the Java DNS infrastructure.    * This likely only works on Sun-derived JDKs, but uses JUnit's    * Assume functionality so that any tests using it are skipped on    * incompatible JDKs.    */
DECL|method|spyOnNameService ()
specifier|private
name|NameService
name|spyOnNameService
parameter_list|()
block|{
try|try
block|{
name|Field
name|f
init|=
name|InetAddress
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"nameServices"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|f
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|NameService
argument_list|>
name|nsList
init|=
operator|(
name|List
argument_list|<
name|NameService
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|NameService
name|ns
init|=
name|nsList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"NameServiceSpy"
argument_list|)
decl_stmt|;
name|ns
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NameService
operator|.
name|class
argument_list|,
operator|new
name|GenericTestUtils
operator|.
name|DelegateAnswer
argument_list|(
name|log
argument_list|,
name|ns
argument_list|)
argument_list|)
expr_stmt|;
name|nsList
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|ns
argument_list|)
expr_stmt|;
return|return
name|ns
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unable to spy on DNS. Skipping test."
argument_list|,
name|t
argument_list|)
expr_stmt|;
comment|// In case the JDK we're testing on doesn't work like Sun's, just
comment|// skip the test.
name|Assume
operator|.
name|assumeNoException
argument_list|(
name|t
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
comment|/**    * Test that the client doesn't ever try to DNS-resolve the logical URI.    * Regression test for HADOOP-9150.    */
annotation|@
name|Test
DECL|method|testDoesntDnsResolveLogicalURI ()
specifier|public
name|void
name|testDoesntDnsResolveLogicalURI
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|NameService
name|spyNS
init|=
name|spyOnNameService
argument_list|()
decl_stmt|;
name|String
name|logicalHost
init|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|Path
name|qualifiedRoot
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
comment|// Make a few calls against the filesystem.
name|fs
operator|.
name|getCanonicalServiceName
argument_list|()
expr_stmt|;
name|fs
operator|.
name|listStatus
argument_list|(
name|qualifiedRoot
argument_list|)
expr_stmt|;
comment|// Ensure that the logical hostname was never resolved.
name|Mockito
operator|.
name|verify
argument_list|(
name|spyNS
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|lookupAllHostAddr
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|logicalHost
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Same test as above, but for FileContext.    */
annotation|@
name|Test
DECL|method|testFileContextDoesntDnsResolveLogicalURI ()
specifier|public
name|void
name|testFileContextDoesntDnsResolveLogicalURI
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|fs
init|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|NameService
name|spyNS
init|=
name|spyOnNameService
argument_list|()
decl_stmt|;
name|String
name|logicalHost
init|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|Configuration
name|haClientConf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|FileContext
name|fc
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|haClientConf
argument_list|)
decl_stmt|;
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|fc
operator|.
name|listStatus
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|fc
operator|.
name|listStatus
argument_list|(
name|fc
operator|.
name|makeQualified
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|fc
operator|.
name|getDefaultFileSystem
argument_list|()
operator|.
name|getCanonicalServiceName
argument_list|()
expr_stmt|;
comment|// Ensure that the logical hostname was never resolved.
name|Mockito
operator|.
name|verify
argument_list|(
name|spyNS
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|lookupAllHostAddr
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|logicalHost
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Dummy implementation of plain FailoverProxyProvider */
DECL|class|DummyLegacyFailoverProxyProvider
specifier|public
specifier|static
class|class
name|DummyLegacyFailoverProxyProvider
parameter_list|<
name|T
parameter_list|>
implements|implements
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
block|{
DECL|field|xface
specifier|private
name|Class
argument_list|<
name|T
argument_list|>
name|xface
decl_stmt|;
DECL|field|proxy
specifier|private
name|T
name|proxy
decl_stmt|;
DECL|method|DummyLegacyFailoverProxyProvider (Configuration conf, URI uri, Class<T> xface)
specifier|public
name|DummyLegacyFailoverProxyProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|proxy
operator|=
name|NameNodeProxies
operator|.
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|NameNode
operator|.
name|getAddress
argument_list|(
name|uri
argument_list|)
argument_list|,
name|xface
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|this
operator|.
name|xface
operator|=
name|xface
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{       }
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
argument_list|<
name|T
argument_list|>
name|getInterface
parameter_list|()
block|{
return|return
name|xface
return|;
block|}
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|getProxy
parameter_list|()
block|{
return|return
operator|new
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|(
name|proxy
argument_list|,
literal|"dummy"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|performFailover (T currentProxy)
specifier|public
name|void
name|performFailover
parameter_list|(
name|T
name|currentProxy
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
block|}
comment|/**    * Test to verify legacy proxy providers are correctly wrapped.    */
annotation|@
name|Test
DECL|method|testWrappedFailoverProxyProvider ()
specifier|public
name|void
name|testWrappedFailoverProxyProvider
parameter_list|()
throws|throws
name|Exception
block|{
comment|// setup the config with the dummy provider class
name|Configuration
name|config
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|logicalName
init|=
name|HATestUtil
operator|.
name|getLogicalHostname
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|HATestUtil
operator|.
name|setFailoverConfigurations
argument_list|(
name|cluster
argument_list|,
name|config
argument_list|,
name|logicalName
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
operator|+
literal|"."
operator|+
name|logicalName
argument_list|,
name|DummyLegacyFailoverProxyProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"hdfs://"
operator|+
name|logicalName
operator|+
literal|"/"
argument_list|)
decl_stmt|;
comment|// not to use IP address for token service
name|SecurityUtil
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Logical URI should be used.
name|assertTrue
argument_list|(
literal|"Legacy proxy providers should use logical URI."
argument_list|,
name|HAUtil
operator|.
name|useLogicalUri
argument_list|(
name|config
argument_list|,
name|p
operator|.
name|toUri
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify IPFailoverProxyProvider is not requiring logical URI.    */
annotation|@
name|Test
DECL|method|testIPFailoverProxyProviderLogicalUri ()
specifier|public
name|void
name|testIPFailoverProxyProviderLogicalUri
parameter_list|()
throws|throws
name|Exception
block|{
comment|// setup the config with the IP failover proxy provider class
name|Configuration
name|config
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|URI
name|nnUri
init|=
name|cluster
operator|.
name|getURI
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
operator|+
literal|"."
operator|+
name|nnUri
operator|.
name|getHost
argument_list|()
argument_list|,
name|IPFailoverProxyProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"IPFailoverProxyProvider should not use logical URI."
argument_list|,
name|HAUtil
operator|.
name|useLogicalUri
argument_list|(
name|config
argument_list|,
name|nnUri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

