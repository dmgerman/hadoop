begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
package|package
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
package|;
end_package

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
name|Joiner
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
name|AbstractFileSystem
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
name|hdfs
operator|.
name|*
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
name|protocol
operator|.
name|HdfsConstants
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSecretManager
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSelector
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
name|FSNamesystem
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
name|NameNodeAdapter
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
name|ipc
operator|.
name|RetriableException
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
name|ipc
operator|.
name|StandbyException
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
name|SecurityUtilTestHelper
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
name|After
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
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|HashSet
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
name|*
import|;
end_import

begin_comment
comment|/**  * Test case for client support of delegation tokens in an HA cluster.  * See HDFS-2904 for more info.  **/
end_comment

begin_class
DECL|class|TestDelegationTokensWithHA
specifier|public
class|class
name|TestDelegationTokensWithHA
block|{
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|TestDelegationTokensWithHA
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|nn0
specifier|private
specifier|static
name|NameNode
name|nn0
decl_stmt|;
DECL|field|nn1
specifier|private
specifier|static
name|NameNode
name|nn1
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|FileSystem
name|fs
decl_stmt|;
DECL|field|dtSecretManager
specifier|private
specifier|static
name|DelegationTokenSecretManager
name|dtSecretManager
decl_stmt|;
DECL|field|dfs
specifier|private
specifier|static
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|catchup
specifier|private
specifier|volatile
name|boolean
name|catchup
init|=
literal|false
decl_stmt|;
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTH_TO_LOCAL
argument_list|,
literal|"RULE:[2:$1@$0](JobTracker@.*FOO.COM)s/@.*//"
operator|+
literal|"DEFAULT"
argument_list|)
expr_stmt|;
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
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
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
name|conf
argument_list|,
name|logicalName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|nn0
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|nn1
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fs
operator|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dtSecretManager
operator|=
name|NameNodeAdapter
operator|.
name|getDtSecretManager
argument_list|(
name|nn0
operator|.
name|getNamesystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdownCluster ()
specifier|public
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testDelegationTokenDFSApi ()
specifier|public
name|void
name|testDelegationTokenDFSApi
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|getDelegationToken
argument_list|(
name|fs
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|DelegationTokenIdentifier
name|identifier
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenId
init|=
name|token
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|tokenId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure that it's present in the NN's secret manager and can
comment|// be renewed directly from there.
name|LOG
operator|.
name|info
argument_list|(
literal|"A valid token should have non-null password, "
operator|+
literal|"and should be renewed successfully"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|null
operator|!=
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
comment|// Use the client conf with the failover info present to check
comment|// renewal.
name|Configuration
name|clientConf
init|=
name|dfs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|doRenewOrCancel
argument_list|(
name|token
argument_list|,
name|clientConf
argument_list|,
name|TokenTestAction
operator|.
name|RENEW
argument_list|)
expr_stmt|;
comment|// Using a configuration that doesn't have the logical nameservice
comment|// configured should result in a reasonable error message.
name|Configuration
name|emptyConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
try|try
block|{
name|doRenewOrCancel
argument_list|(
name|token
argument_list|,
name|emptyConf
argument_list|,
name|TokenTestAction
operator|.
name|RENEW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw trying to renew with an empty conf!"
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
literal|"Unable to map logical nameservice URI"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
comment|// Ensure that the token can be renewed again after a failover.
name|cluster
operator|.
name|transitionToStandby
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
name|doRenewOrCancel
argument_list|(
name|token
argument_list|,
name|clientConf
argument_list|,
name|TokenTestAction
operator|.
name|RENEW
argument_list|)
expr_stmt|;
name|doRenewOrCancel
argument_list|(
name|token
argument_list|,
name|clientConf
argument_list|,
name|TokenTestAction
operator|.
name|CANCEL
argument_list|)
expr_stmt|;
block|}
DECL|class|EditLogTailerForTest
specifier|private
class|class
name|EditLogTailerForTest
extends|extends
name|EditLogTailer
block|{
DECL|method|EditLogTailerForTest (FSNamesystem namesystem, Configuration conf)
specifier|public
name|EditLogTailerForTest
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|namesystem
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|catchupDuringFailover ()
specifier|public
name|void
name|catchupDuringFailover
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|TestDelegationTokensWithHA
operator|.
name|this
init|)
block|{
while|while
condition|(
operator|!
name|catchup
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The editlog tailer is waiting to catchup..."
argument_list|)
expr_stmt|;
name|TestDelegationTokensWithHA
operator|.
name|this
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
block|}
name|super
operator|.
name|catchupDuringFailover
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test if correct exception (StandbyException or RetriableException) can be    * thrown during the NN failover.     */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testDelegationTokenDuringNNFailover ()
specifier|public
name|void
name|testDelegationTokenDuringNNFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|EditLogTailer
name|editLogTailer
init|=
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getEditLogTailer
argument_list|()
decl_stmt|;
comment|// stop the editLogTailer of nn1
name|editLogTailer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|(
name|Configuration
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|editLogTailer
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|setEditLogTailerForTests
argument_list|(
operator|new
name|EditLogTailerForTest
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|// create token
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|getDelegationToken
argument_list|(
name|fs
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|DelegationTokenIdentifier
name|identifier
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|byte
index|[]
name|tokenId
init|=
name|token
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|identifier
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|tokenId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure that it's present in the nn0 secret manager and can
comment|// be renewed directly from there.
name|LOG
operator|.
name|info
argument_list|(
literal|"A valid token should have non-null password, "
operator|+
literal|"and should be renewed successfully"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|null
operator|!=
name|dtSecretManager
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
name|dtSecretManager
operator|.
name|renewToken
argument_list|(
name|token
argument_list|,
literal|"JobTracker"
argument_list|)
expr_stmt|;
comment|// transition nn0 to standby
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|getNameNodeRpc
argument_list|(
literal|0
argument_list|)
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"StandbyException is expected since nn0 is in standby state"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StandbyException
name|e
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Transition nn1 to active failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
try|try
block|{
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|verifyToken
argument_list|(
name|token
operator|.
name|decodeIdentifier
argument_list|()
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"RetriableException/StandbyException is expected since nn1 is in transition"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|StandbyException
operator|||
name|e
operator|instanceof
name|RetriableException
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|catchup
operator|=
literal|true
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
name|Configuration
name|clientConf
init|=
name|dfs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|doRenewOrCancel
argument_list|(
name|token
argument_list|,
name|clientConf
argument_list|,
name|TokenTestAction
operator|.
name|RENEW
argument_list|)
expr_stmt|;
name|doRenewOrCancel
argument_list|(
name|token
argument_list|,
name|clientConf
argument_list|,
name|TokenTestAction
operator|.
name|CANCEL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testDelegationTokenWithDoAs ()
specifier|public
name|void
name|testDelegationTokenWithDoAs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|getDelegationToken
argument_list|(
name|fs
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
specifier|final
name|UserGroupInformation
name|longUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"JobTracker/foo.com@FOO.COM"
argument_list|)
decl_stmt|;
specifier|final
name|UserGroupInformation
name|shortUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|longUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
comment|// try renew with long name
name|token
operator|.
name|renew
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|shortUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|token
operator|.
name|renew
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|longUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|token
operator|.
name|cancel
argument_list|(
name|conf
argument_list|)
expr_stmt|;
empty_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testHAUtilClonesDelegationTokens ()
specifier|public
name|void
name|testHAUtilClonesDelegationTokens
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|getDelegationToken
argument_list|(
name|fs
argument_list|,
literal|"JobTracker"
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|URI
name|haUri
init|=
operator|new
name|URI
argument_list|(
literal|"hdfs://my-ha-uri/"
argument_list|)
decl_stmt|;
name|token
operator|.
name|setService
argument_list|(
name|HAUtilClient
operator|.
name|buildTokenServiceForLogicalUri
argument_list|(
name|haUri
argument_list|,
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
argument_list|)
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|nnAddrs
init|=
operator|new
name|HashSet
argument_list|<
name|InetSocketAddress
argument_list|>
argument_list|()
decl_stmt|;
name|nnAddrs
operator|.
name|add
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|nn0
operator|.
name|getNameNodeAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nnAddrs
operator|.
name|add
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|nn1
operator|.
name|getNameNodeAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|HAUtil
operator|.
name|cloneDelegationTokenForLogicalUri
argument_list|(
name|ugi
argument_list|,
name|haUri
argument_list|,
name|nnAddrs
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|tokens
init|=
name|ugi
operator|.
name|getTokens
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Tokens:\n"
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|join
argument_list|(
name|tokens
argument_list|)
argument_list|)
expr_stmt|;
name|DelegationTokenSelector
name|dts
init|=
operator|new
name|DelegationTokenSelector
argument_list|()
decl_stmt|;
comment|// check that the token selected for one of the physical IPC addresses
comment|// matches the one we received
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|nnAddrs
control|)
block|{
name|Text
name|ipcDtService
init|=
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token2
init|=
name|dts
operator|.
name|selectToken
argument_list|(
name|ipcDtService
argument_list|,
name|ugi
operator|.
name|getTokens
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|token2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|token
operator|.
name|getPassword
argument_list|()
argument_list|,
name|token2
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// switch to host-based tokens, shouldn't match existing tokens
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|nnAddrs
control|)
block|{
name|Text
name|ipcDtService
init|=
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token2
init|=
name|dts
operator|.
name|selectToken
argument_list|(
name|ipcDtService
argument_list|,
name|ugi
operator|.
name|getTokens
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|token2
argument_list|)
expr_stmt|;
block|}
comment|// reclone the tokens, and see if they match now
name|HAUtil
operator|.
name|cloneDelegationTokenForLogicalUri
argument_list|(
name|ugi
argument_list|,
name|haUri
argument_list|,
name|nnAddrs
argument_list|)
expr_stmt|;
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|nnAddrs
control|)
block|{
name|Text
name|ipcDtService
init|=
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token2
init|=
name|dts
operator|.
name|selectToken
argument_list|(
name|ipcDtService
argument_list|,
name|ugi
operator|.
name|getTokens
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|token2
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|token2
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|token
operator|.
name|getPassword
argument_list|()
argument_list|,
name|token2
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * HDFS-3062: DistributedFileSystem.getCanonicalServiceName() throws an    * exception if the URI is a logical URI. This bug fails the combination of    * ha + mapred + security.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testDFSGetCanonicalServiceName ()
specifier|public
name|void
name|testDFSGetCanonicalServiceName
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|hAUri
init|=
name|HATestUtil
operator|.
name|getLogicalUri
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|String
name|haService
init|=
name|HAUtilClient
operator|.
name|buildTokenServiceForLogicalUri
argument_list|(
name|hAUri
argument_list|,
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|haService
argument_list|,
name|dfs
operator|.
name|getCanonicalServiceName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|renewer
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|getDelegationToken
argument_list|(
name|dfs
argument_list|,
name|renewer
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|haService
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
comment|// make sure the logical uri is handled correctly
name|token
operator|.
name|renew
argument_list|(
name|dfs
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|cancel
argument_list|(
name|dfs
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testHdfsGetCanonicalServiceName ()
specifier|public
name|void
name|testHdfsGetCanonicalServiceName
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|dfs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|URI
name|haUri
init|=
name|HATestUtil
operator|.
name|getLogicalUri
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|AbstractFileSystem
name|afs
init|=
name|AbstractFileSystem
operator|.
name|createFileSystem
argument_list|(
name|haUri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|String
name|haService
init|=
name|HAUtilClient
operator|.
name|buildTokenServiceForLogicalUri
argument_list|(
name|haUri
argument_list|,
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|haService
argument_list|,
name|afs
operator|.
name|getCanonicalServiceName
argument_list|()
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|token
init|=
name|afs
operator|.
name|getDelegationTokens
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|haService
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
comment|// make sure the logical uri is handled correctly
name|token
operator|.
name|renew
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|token
operator|.
name|cancel
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getDelegationToken (FileSystem fs, String renewer)
specifier|private
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|tokens
index|[]
init|=
name|fs
operator|.
name|addDelegationTokens
argument_list|(
name|renewer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|tokens
index|[
literal|0
index|]
return|;
block|}
DECL|enum|TokenTestAction
enum|enum
name|TokenTestAction
block|{
DECL|enumConstant|RENEW
DECL|enumConstant|CANCEL
name|RENEW
block|,
name|CANCEL
block|;   }
DECL|method|doRenewOrCancel ( final Token<DelegationTokenIdentifier> token, final Configuration conf, final TokenTestAction action)
specifier|private
specifier|static
name|void
name|doRenewOrCancel
parameter_list|(
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|TokenTestAction
name|action
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"JobTracker"
argument_list|)
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|RENEW
case|:
name|token
operator|.
name|renew
argument_list|(
name|conf
argument_list|)
expr_stmt|;
break|break;
case|case
name|CANCEL
case|:
name|token
operator|.
name|cancel
argument_list|(
name|conf
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"bad action:"
operator|+
name|action
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

