begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.bkjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|bkjournal
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
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|bookkeeper
operator|.
name|util
operator|.
name|LocalBookKeeper
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
name|zookeeper
operator|.
name|KeeperException
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
name|NIOServerCnxnFactory
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
name|ZooKeeperServer
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
name|AfterClass
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
name|Before
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|NamespaceInfo
import|;
end_import

begin_class
DECL|class|TestBookKeeperConfiguration
specifier|public
class|class
name|TestBookKeeperConfiguration
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
name|TestBookKeeperConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ZK_SESSION_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|ZK_SESSION_TIMEOUT
init|=
literal|5000
decl_stmt|;
DECL|field|HOSTPORT
specifier|private
specifier|static
specifier|final
name|String
name|HOSTPORT
init|=
literal|"127.0.0.1:2181"
decl_stmt|;
DECL|field|CONNECTION_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|CONNECTION_TIMEOUT
init|=
literal|30000
decl_stmt|;
DECL|field|serverFactory
specifier|private
specifier|static
name|NIOServerCnxnFactory
name|serverFactory
decl_stmt|;
DECL|field|zks
specifier|private
specifier|static
name|ZooKeeperServer
name|zks
decl_stmt|;
DECL|field|zkc
specifier|private
specifier|static
name|ZooKeeper
name|zkc
decl_stmt|;
DECL|field|ZooKeeperDefaultPort
specifier|private
specifier|static
name|int
name|ZooKeeperDefaultPort
init|=
literal|2181
decl_stmt|;
DECL|field|ZkTmpDir
specifier|private
specifier|static
name|File
name|ZkTmpDir
decl_stmt|;
DECL|field|bkjm
specifier|private
name|BookKeeperJournalManager
name|bkjm
decl_stmt|;
DECL|field|BK_ROOT_PATH
specifier|private
specifier|static
specifier|final
name|String
name|BK_ROOT_PATH
init|=
literal|"/ledgers"
decl_stmt|;
DECL|method|connectZooKeeper (String ensemble)
specifier|private
specifier|static
name|ZooKeeper
name|connectZooKeeper
parameter_list|(
name|String
name|ensemble
parameter_list|)
throws|throws
name|IOException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ZooKeeper
name|zkc
init|=
operator|new
name|ZooKeeper
argument_list|(
name|HOSTPORT
argument_list|,
name|ZK_SESSION_TIMEOUT
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getState
argument_list|()
operator|==
name|Watcher
operator|.
name|Event
operator|.
name|KeeperState
operator|.
name|SyncConnected
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|latch
operator|.
name|await
argument_list|(
name|ZK_SESSION_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Zookeeper took too long to connect"
argument_list|)
throw|;
block|}
return|return
name|zkc
return|;
block|}
DECL|method|newNSInfo ()
specifier|private
name|NamespaceInfo
name|newNSInfo
parameter_list|()
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
return|return
operator|new
name|NamespaceInfo
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|"testCluster"
argument_list|,
literal|"TestBPID"
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|setupZooKeeper ()
specifier|public
specifier|static
name|void
name|setupZooKeeper
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a ZooKeeper server(dataDir, dataLogDir, port)
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting ZK server"
argument_list|)
expr_stmt|;
name|ZkTmpDir
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"zookeeper"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|ZkTmpDir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|ZkTmpDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
try|try
block|{
name|zks
operator|=
operator|new
name|ZooKeeperServer
argument_list|(
name|ZkTmpDir
argument_list|,
name|ZkTmpDir
argument_list|,
name|ZooKeeperDefaultPort
argument_list|)
expr_stmt|;
name|serverFactory
operator|=
operator|new
name|NIOServerCnxnFactory
argument_list|()
expr_stmt|;
name|serverFactory
operator|.
name|configure
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|ZooKeeperDefaultPort
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|serverFactory
operator|.
name|startup
argument_list|(
name|zks
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
literal|"Exception while instantiating ZooKeeper"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|boolean
name|b
init|=
name|LocalBookKeeper
operator|.
name|waitForServerUp
argument_list|(
name|HOSTPORT
argument_list|,
name|CONNECTION_TIMEOUT
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"ZooKeeper server up: "
operator|+
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|zkc
operator|=
name|connectZooKeeper
argument_list|(
name|HOSTPORT
argument_list|)
expr_stmt|;
try|try
block|{
name|ZKUtil
operator|.
name|deleteRecursive
argument_list|(
name|zkc
argument_list|,
name|BK_ROOT_PATH
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ignoring no node exception on cleanup"
argument_list|,
name|e
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
literal|"Exception when deleting bookie root path in zk"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
literal|null
operator|!=
name|zkc
condition|)
block|{
name|zkc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|bkjm
condition|)
block|{
name|bkjm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|teardownZooKeeper ()
specifier|public
specifier|static
name|void
name|teardownZooKeeper
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
literal|null
operator|!=
name|zkc
condition|)
block|{
name|zkc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Verify the BKJM is creating the bookie available path configured in    * 'dfs.namenode.bookkeeperjournal.zk.availablebookies'    */
annotation|@
name|Test
DECL|method|testWithConfiguringBKAvailablePath ()
specifier|public
name|void
name|testWithConfiguringBKAvailablePath
parameter_list|()
throws|throws
name|Exception
block|{
comment|// set Bookie available path in the configuration
name|String
name|bkAvailablePath
init|=
name|BookKeeperJournalManager
operator|.
name|BKJM_ZK_LEDGERS_AVAILABLE_PATH_DEFAULT
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
name|setStrings
argument_list|(
name|BookKeeperJournalManager
operator|.
name|BKJM_ZK_LEDGERS_AVAILABLE_PATH
argument_list|,
name|bkAvailablePath
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|bkAvailablePath
operator|+
literal|" already exists"
argument_list|,
name|zkc
operator|.
name|exists
argument_list|(
name|bkAvailablePath
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|NamespaceInfo
name|nsi
init|=
name|newNSInfo
argument_list|()
decl_stmt|;
name|bkjm
operator|=
operator|new
name|BookKeeperJournalManager
argument_list|(
name|conf
argument_list|,
name|URI
operator|.
name|create
argument_list|(
literal|"bookkeeper://"
operator|+
name|HOSTPORT
operator|+
literal|"/hdfsjournal-WithBKPath"
argument_list|)
argument_list|,
name|nsi
argument_list|)
expr_stmt|;
name|bkjm
operator|.
name|format
argument_list|(
name|nsi
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Bookie available path : "
operator|+
name|bkAvailablePath
operator|+
literal|" doesn't exists"
argument_list|,
name|zkc
operator|.
name|exists
argument_list|(
name|bkAvailablePath
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the BKJM is creating the bookie available default path, when there    * is no 'dfs.namenode.bookkeeperjournal.zk.availablebookies' configured    */
annotation|@
name|Test
DECL|method|testDefaultBKAvailablePath ()
specifier|public
name|void
name|testDefaultBKAvailablePath
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|BK_ROOT_PATH
operator|+
literal|" already exists"
argument_list|,
name|zkc
operator|.
name|exists
argument_list|(
name|BK_ROOT_PATH
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|NamespaceInfo
name|nsi
init|=
name|newNSInfo
argument_list|()
decl_stmt|;
name|bkjm
operator|=
operator|new
name|BookKeeperJournalManager
argument_list|(
name|conf
argument_list|,
name|URI
operator|.
name|create
argument_list|(
literal|"bookkeeper://"
operator|+
name|HOSTPORT
operator|+
literal|"/hdfsjournal-DefaultBKPath"
argument_list|)
argument_list|,
name|nsi
argument_list|)
expr_stmt|;
name|bkjm
operator|.
name|format
argument_list|(
name|nsi
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Bookie available path : "
operator|+
name|BK_ROOT_PATH
operator|+
literal|" doesn't exists"
argument_list|,
name|zkc
operator|.
name|exists
argument_list|(
name|BK_ROOT_PATH
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

