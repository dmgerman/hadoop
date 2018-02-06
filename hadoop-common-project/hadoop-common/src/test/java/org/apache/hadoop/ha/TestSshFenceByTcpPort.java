begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ha
operator|.
name|SshFenceByTcpPort
operator|.
name|Args
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
import|;
end_import

begin_class
DECL|class|TestSshFenceByTcpPort
specifier|public
class|class
name|TestSshFenceByTcpPort
block|{
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|SshFenceByTcpPort
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
block|}
DECL|field|TEST_FENCING_HOST
specifier|private
specifier|static
name|String
name|TEST_FENCING_HOST
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.TestSshFenceByTcpPort.host"
argument_list|,
literal|"localhost"
argument_list|)
decl_stmt|;
DECL|field|TEST_FENCING_PORT
specifier|private
specifier|static
specifier|final
name|String
name|TEST_FENCING_PORT
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.TestSshFenceByTcpPort.port"
argument_list|,
literal|"8020"
argument_list|)
decl_stmt|;
DECL|field|TEST_KEYFILE
specifier|private
specifier|static
specifier|final
name|String
name|TEST_KEYFILE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.TestSshFenceByTcpPort.key"
argument_list|)
decl_stmt|;
DECL|field|TEST_ADDR
specifier|private
specifier|static
specifier|final
name|InetSocketAddress
name|TEST_ADDR
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|TEST_FENCING_HOST
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|TEST_FENCING_PORT
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|TEST_TARGET
specifier|private
specifier|static
specifier|final
name|HAServiceTarget
name|TEST_TARGET
init|=
operator|new
name|DummyHAService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
name|TEST_ADDR
argument_list|)
decl_stmt|;
comment|/**    *  Connect to Google's DNS server - not running ssh!    */
DECL|field|UNFENCEABLE_TARGET
specifier|private
specifier|static
specifier|final
name|HAServiceTarget
name|UNFENCEABLE_TARGET
init|=
operator|new
name|DummyHAService
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
literal|"8.8.8.8"
argument_list|,
literal|1234
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testFence ()
specifier|public
name|void
name|testFence
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|isConfigured
argument_list|()
argument_list|)
expr_stmt|;
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
name|SshFenceByTcpPort
operator|.
name|CONF_IDENTITIES_KEY
argument_list|,
name|TEST_KEYFILE
argument_list|)
expr_stmt|;
name|SshFenceByTcpPort
name|fence
init|=
operator|new
name|SshFenceByTcpPort
argument_list|()
decl_stmt|;
name|fence
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fence
operator|.
name|tryFence
argument_list|(
name|TEST_TARGET
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test connecting to a host which definitely won't respond.    * Make sure that it times out and returns false, but doesn't throw    * any exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testConnectTimeout ()
specifier|public
name|void
name|testConnectTimeout
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|SshFenceByTcpPort
operator|.
name|CONF_CONNECT_TIMEOUT_KEY
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|SshFenceByTcpPort
name|fence
init|=
operator|new
name|SshFenceByTcpPort
argument_list|()
decl_stmt|;
name|fence
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fence
operator|.
name|tryFence
argument_list|(
name|UNFENCEABLE_TARGET
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgsParsing ()
specifier|public
name|void
name|testArgsParsing
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|Args
name|args
init|=
operator|new
name|SshFenceByTcpPort
operator|.
name|Args
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|args
operator|.
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|args
operator|.
name|sshPort
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|SshFenceByTcpPort
operator|.
name|Args
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|args
operator|.
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|args
operator|.
name|sshPort
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|SshFenceByTcpPort
operator|.
name|Args
argument_list|(
literal|"12345"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"12345"
argument_list|,
name|args
operator|.
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|args
operator|.
name|sshPort
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|SshFenceByTcpPort
operator|.
name|Args
argument_list|(
literal|":12345"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|args
operator|.
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12345
argument_list|,
name|args
operator|.
name|sshPort
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|SshFenceByTcpPort
operator|.
name|Args
argument_list|(
literal|"foo:2222"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|args
operator|.
name|user
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2222
argument_list|,
name|args
operator|.
name|sshPort
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadArgsParsing ()
specifier|public
name|void
name|testBadArgsParsing
parameter_list|()
throws|throws
name|BadFencingConfigurationException
block|{
name|assertBadArgs
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
comment|// No port specified
name|assertBadArgs
argument_list|(
literal|"bar.com:"
argument_list|)
expr_stmt|;
comment|// "
name|assertBadArgs
argument_list|(
literal|":xx"
argument_list|)
expr_stmt|;
comment|// Port does not parse
name|assertBadArgs
argument_list|(
literal|"bar.com:xx"
argument_list|)
expr_stmt|;
comment|// "
block|}
DECL|method|assertBadArgs (String argStr)
specifier|private
name|void
name|assertBadArgs
parameter_list|(
name|String
name|argStr
parameter_list|)
block|{
try|try
block|{
operator|new
name|Args
argument_list|(
name|argStr
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not fail on bad args: "
operator|+
name|argStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadFencingConfigurationException
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
DECL|method|isConfigured ()
specifier|private
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
operator|(
name|TEST_FENCING_HOST
operator|!=
literal|null
operator|&&
operator|!
name|TEST_FENCING_HOST
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|TEST_FENCING_PORT
operator|!=
literal|null
operator|&&
operator|!
name|TEST_FENCING_PORT
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|TEST_KEYFILE
operator|!=
literal|null
operator|&&
operator|!
name|TEST_KEYFILE
operator|.
name|isEmpty
argument_list|()
operator|)
return|;
block|}
block|}
end_class

end_unit

