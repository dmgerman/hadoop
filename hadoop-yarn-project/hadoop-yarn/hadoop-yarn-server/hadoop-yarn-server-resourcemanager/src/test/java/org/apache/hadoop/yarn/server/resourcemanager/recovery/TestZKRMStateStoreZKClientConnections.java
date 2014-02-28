begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
package|;
end_package

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
name|ha
operator|.
name|ClientBaseWithFixes
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|RMStateStoreTestBase
operator|.
name|TestDispatcher
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
name|ZKUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|WatchedEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|Watcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooKeeper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|auth
operator|.
name|DigestAuthenticationProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|security
operator|.
name|NoSuchAlgorithmException
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
name|CyclicBarrier
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

begin_class
DECL|class|TestZKRMStateStoreZKClientConnections
specifier|public
class|class
name|TestZKRMStateStoreZKClientConnections
extends|extends
name|ClientBaseWithFixes
block|{
DECL|field|ZK_OP_WAIT_TIME
specifier|private
specifier|static
specifier|final
name|int
name|ZK_OP_WAIT_TIME
init|=
literal|3000
decl_stmt|;
DECL|field|LOG
specifier|private
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestZKRMStateStoreZKClientConnections
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DIGEST_USER_PASS
specifier|private
specifier|static
specifier|final
name|String
name|DIGEST_USER_PASS
init|=
literal|"test-user:test-password"
decl_stmt|;
DECL|field|TEST_AUTH_GOOD
specifier|private
specifier|static
specifier|final
name|String
name|TEST_AUTH_GOOD
init|=
literal|"digest:"
operator|+
name|DIGEST_USER_PASS
decl_stmt|;
DECL|field|DIGEST_USER_HASH
specifier|private
specifier|static
specifier|final
name|String
name|DIGEST_USER_HASH
decl_stmt|;
static|static
block|{
try|try
block|{
name|DIGEST_USER_HASH
operator|=
name|DigestAuthenticationProvider
operator|.
name|generateDigest
argument_list|(
name|DIGEST_USER_PASS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_ACL
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ACL
init|=
literal|"digest:"
operator|+
name|DIGEST_USER_HASH
operator|+
literal|":rwcda"
decl_stmt|;
DECL|class|TestZKClient
class|class
name|TestZKClient
block|{
DECL|field|store
name|ZKRMStateStore
name|store
decl_stmt|;
DECL|field|forExpire
name|boolean
name|forExpire
init|=
literal|false
decl_stmt|;
DECL|field|watcher
name|TestForwardingWatcher
name|watcher
decl_stmt|;
DECL|field|syncBarrier
name|CyclicBarrier
name|syncBarrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|class|TestZKRMStateStore
specifier|protected
class|class
name|TestZKRMStateStore
extends|extends
name|ZKRMStateStore
block|{
DECL|method|TestZKRMStateStore (Configuration conf, String workingZnode)
specifier|public
name|TestZKRMStateStore
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|workingZnode
parameter_list|)
throws|throws
name|Exception
block|{
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|znodeWorkingPath
operator|.
name|equals
argument_list|(
name|workingZnode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNewZooKeeper ()
specifier|public
name|ZooKeeper
name|getNewZooKeeper
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|createClient
argument_list|(
name|watcher
argument_list|,
name|hostPort
argument_list|,
literal|100
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|processWatchEvent (WatchedEvent event)
specifier|public
specifier|synchronized
name|void
name|processWatchEvent
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|forExpire
condition|)
block|{
comment|// a hack... couldn't find a way to trigger expired event.
name|WatchedEvent
name|expriredEvent
init|=
operator|new
name|WatchedEvent
argument_list|(
name|Watcher
operator|.
name|Event
operator|.
name|EventType
operator|.
name|None
argument_list|,
name|Watcher
operator|.
name|Event
operator|.
name|KeeperState
operator|.
name|Expired
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|super
operator|.
name|processWatchEvent
argument_list|(
name|expriredEvent
argument_list|)
expr_stmt|;
name|forExpire
operator|=
literal|false
expr_stmt|;
name|syncBarrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|processWatchEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TestForwardingWatcher
specifier|private
class|class
name|TestForwardingWatcher
extends|extends
name|ClientBaseWithFixes
operator|.
name|CountdownWatcher
block|{
DECL|method|process (WatchedEvent event)
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
name|super
operator|.
name|process
argument_list|(
name|event
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|processWatchEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to process watcher event "
operator|+
name|event
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRMStateStore (Configuration conf)
specifier|public
name|RMStateStore
name|getRMStateStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|workingZnode
init|=
literal|"/Test"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|ZK_RM_STATE_STORE_PARENT_PATH
argument_list|,
name|workingZnode
argument_list|)
expr_stmt|;
name|watcher
operator|=
operator|new
name|TestForwardingWatcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|new
name|TestZKRMStateStore
argument_list|(
name|conf
argument_list|,
name|workingZnode
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|store
return|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testZKClientRetry ()
specifier|public
name|void
name|testZKClientRetry
parameter_list|()
throws|throws
name|Exception
block|{
name|TestZKClient
name|zkClientTester
init|=
operator|new
name|TestZKClient
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
literal|"/test"
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_RETRY_INTERVAL_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
specifier|final
name|ZKRMStateStore
name|store
init|=
operator|(
name|ZKRMStateStore
operator|)
name|zkClientTester
operator|.
name|getRMStateStore
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TestDispatcher
name|dispatcher
init|=
operator|new
name|TestDispatcher
argument_list|()
decl_stmt|;
name|store
operator|.
name|setRMDispatcher
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|assertionFailedInThread
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|stopServer
argument_list|()
expr_stmt|;
name|Thread
name|clientThread
init|=
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
name|store
operator|.
name|getDataWithRetries
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertionFailedInThread
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|startServer
argument_list|()
expr_stmt|;
name|clientThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|assertionFailedInThread
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testZKClientDisconnectAndReconnect ()
specifier|public
name|void
name|testZKClientDisconnectAndReconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|TestZKClient
name|zkClientTester
init|=
operator|new
name|TestZKClient
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/test"
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|ZKRMStateStore
name|store
init|=
operator|(
name|ZKRMStateStore
operator|)
name|zkClientTester
operator|.
name|getRMStateStore
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TestDispatcher
name|dispatcher
init|=
operator|new
name|TestDispatcher
argument_list|()
decl_stmt|;
name|store
operator|.
name|setRMDispatcher
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
comment|// trigger watch
name|store
operator|.
name|createWithRetries
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|store
operator|.
name|getDataWithRetries
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|store
operator|.
name|setDataWithRetries
argument_list|(
name|path
argument_list|,
literal|"newBytes"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|stopServer
argument_list|()
expr_stmt|;
name|zkClientTester
operator|.
name|watcher
operator|.
name|waitForDisconnected
argument_list|(
name|ZK_OP_WAIT_TIME
argument_list|)
expr_stmt|;
try|try
block|{
name|store
operator|.
name|getDataWithRetries
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ZKClient time out exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Wait for ZKClient creation timed out"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// ZKRMStateStore Session restored
name|startServer
argument_list|()
expr_stmt|;
name|zkClientTester
operator|.
name|watcher
operator|.
name|waitForConnected
argument_list|(
name|ZK_OP_WAIT_TIME
argument_list|)
expr_stmt|;
name|byte
index|[]
name|ret
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ret
operator|=
name|store
operator|.
name|getDataWithRetries
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|error
init|=
literal|"ZKRMStateStore Session restore failed"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|error
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"newBytes"
argument_list|,
operator|new
name|String
argument_list|(
name|ret
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testZKSessionTimeout ()
specifier|public
name|void
name|testZKSessionTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|TestZKClient
name|zkClientTester
init|=
operator|new
name|TestZKClient
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/test"
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|ZKRMStateStore
name|store
init|=
operator|(
name|ZKRMStateStore
operator|)
name|zkClientTester
operator|.
name|getRMStateStore
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|TestDispatcher
name|dispatcher
init|=
operator|new
name|TestDispatcher
argument_list|()
decl_stmt|;
name|store
operator|.
name|setRMDispatcher
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
comment|// a hack to trigger expired event
name|zkClientTester
operator|.
name|forExpire
operator|=
literal|true
expr_stmt|;
comment|// trigger watch
name|store
operator|.
name|createWithRetries
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|store
operator|.
name|getDataWithRetries
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|store
operator|.
name|setDataWithRetries
argument_list|(
name|path
argument_list|,
literal|"bytes"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|zkClientTester
operator|.
name|syncBarrier
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// after this point, expired event has already been processed.
try|try
block|{
name|byte
index|[]
name|ret
init|=
name|store
operator|.
name|getDataWithRetries
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"bytes"
argument_list|,
operator|new
name|String
argument_list|(
name|ret
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|error
init|=
literal|"New session creation failed"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|error
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testSetZKAcl ()
specifier|public
name|void
name|testSetZKAcl
parameter_list|()
block|{
name|TestZKClient
name|zkClientTester
init|=
operator|new
name|TestZKClient
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ACL
argument_list|,
literal|"world:anyone:rwca"
argument_list|)
expr_stmt|;
try|try
block|{
name|zkClientTester
operator|.
name|store
operator|.
name|zkClient
operator|.
name|delete
argument_list|(
name|zkClientTester
operator|.
name|store
operator|.
name|znodeWorkingPath
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Shouldn't be able to delete path"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* expected behavior */
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testInvalidZKAclConfiguration ()
specifier|public
name|void
name|testInvalidZKAclConfiguration
parameter_list|()
block|{
name|TestZKClient
name|zkClientTester
init|=
operator|new
name|TestZKClient
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ACL
argument_list|,
literal|"randomstring&*"
argument_list|)
expr_stmt|;
try|try
block|{
name|zkClientTester
operator|.
name|getRMStateStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ZKRMStateStore created with bad ACL"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ZKUtil
operator|.
name|BadAclFormatException
name|bafe
parameter_list|)
block|{
comment|// expected behavior
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|error
init|=
literal|"Incorrect exception on BadAclFormat"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|error
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testZKAuths ()
specifier|public
name|void
name|testZKAuths
parameter_list|()
throws|throws
name|Exception
block|{
name|TestZKClient
name|zkClientTester
init|=
operator|new
name|TestZKClient
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_NUM_RETRIES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ACL
argument_list|,
name|TEST_ACL
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_AUTH
argument_list|,
name|TEST_AUTH_GOOD
argument_list|)
expr_stmt|;
name|zkClientTester
operator|.
name|getRMStateStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

