begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashMap
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
name|atomic
operator|.
name|AtomicBoolean
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
name|permission
operator|.
name|FsAction
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
name|HAServiceStatus
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|ClientProtocol
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
name|LocatedBlock
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
name|RemoteException
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
name|UserGroupInformation
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
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Tests for {@link ObserverReadProxyProvider} under various configurations of  * NameNode states. Mainly testing that the proxy provider picks the correct  * NameNode to communicate with.  */
end_comment

begin_class
DECL|class|TestObserverReadProxyProvider
specifier|public
class|class
name|TestObserverReadProxyProvider
block|{
DECL|field|EMPTY_BLOCKS
specifier|private
specifier|static
specifier|final
name|LocatedBlock
index|[]
name|EMPTY_BLOCKS
init|=
operator|new
name|LocatedBlock
index|[
literal|0
index|]
decl_stmt|;
DECL|field|ns
specifier|private
name|String
name|ns
decl_stmt|;
DECL|field|nnURI
specifier|private
name|URI
name|nnURI
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|proxyProvider
specifier|private
name|ObserverReadProxyProvider
argument_list|<
name|ClientProtocol
argument_list|>
name|proxyProvider
decl_stmt|;
DECL|field|namenodeAnswers
specifier|private
name|NameNodeAnswer
index|[]
name|namenodeAnswers
decl_stmt|;
DECL|field|namenodeAddrs
specifier|private
name|String
index|[]
name|namenodeAddrs
decl_stmt|;
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
name|ns
operator|=
literal|"testcluster"
expr_stmt|;
name|nnURI
operator|=
name|URI
operator|.
name|create
argument_list|(
literal|"hdfs://"
operator|+
name|ns
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
name|ns
argument_list|)
expr_stmt|;
block|}
DECL|method|setupProxyProvider (int namenodeCount)
specifier|private
name|void
name|setupProxyProvider
parameter_list|(
name|int
name|namenodeCount
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|namenodeIDs
init|=
operator|new
name|String
index|[
name|namenodeCount
index|]
decl_stmt|;
name|namenodeAddrs
operator|=
operator|new
name|String
index|[
name|namenodeCount
index|]
expr_stmt|;
name|namenodeAnswers
operator|=
operator|new
name|NameNodeAnswer
index|[
name|namenodeCount
index|]
expr_stmt|;
name|ClientProtocol
index|[]
name|proxies
init|=
operator|new
name|ClientProtocol
index|[
name|namenodeCount
index|]
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ClientProtocol
argument_list|>
name|proxyMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|HAServiceProtocol
index|[]
name|serviceProxies
init|=
operator|new
name|HAServiceProtocol
index|[
name|namenodeCount
index|]
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|HAServiceProtocol
argument_list|>
name|serviceProxyMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|namenodeCount
condition|;
name|i
operator|++
control|)
block|{
name|namenodeIDs
index|[
name|i
index|]
operator|=
literal|"nn"
operator|+
name|i
expr_stmt|;
name|namenodeAddrs
index|[
name|i
index|]
operator|=
literal|"namenode"
operator|+
name|i
operator|+
literal|".test:8020"
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
operator|+
literal|"."
operator|+
name|ns
operator|+
literal|"."
operator|+
name|namenodeIDs
index|[
name|i
index|]
argument_list|,
name|namenodeAddrs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
name|i
index|]
operator|=
operator|new
name|NameNodeAnswer
argument_list|()
expr_stmt|;
name|proxies
index|[
name|i
index|]
operator|=
name|mock
argument_list|(
name|ClientProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|doWrite
argument_list|(
name|Mockito
operator|.
name|doAnswer
argument_list|(
name|namenodeAnswers
index|[
name|i
index|]
operator|.
name|clientAnswer
argument_list|)
operator|.
name|when
argument_list|(
name|proxies
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|doRead
argument_list|(
name|Mockito
operator|.
name|doAnswer
argument_list|(
name|namenodeAnswers
index|[
name|i
index|]
operator|.
name|clientAnswer
argument_list|)
operator|.
name|when
argument_list|(
name|proxies
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|serviceProxies
index|[
name|i
index|]
operator|=
name|mock
argument_list|(
name|HAServiceProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
name|namenodeAnswers
index|[
name|i
index|]
operator|.
name|serviceAnswer
argument_list|)
operator|.
name|when
argument_list|(
name|serviceProxies
index|[
name|i
index|]
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|proxyMap
operator|.
name|put
argument_list|(
name|namenodeAddrs
index|[
name|i
index|]
argument_list|,
name|proxies
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|serviceProxyMap
operator|.
name|put
argument_list|(
name|namenodeAddrs
index|[
name|i
index|]
argument_list|,
name|serviceProxies
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_HA_NAMENODES_KEY_PREFIX
operator|+
literal|"."
operator|+
name|ns
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|namenodeIDs
argument_list|)
argument_list|)
expr_stmt|;
name|proxyProvider
operator|=
operator|new
name|ObserverReadProxyProvider
argument_list|<
name|ClientProtocol
argument_list|>
argument_list|(
name|conf
argument_list|,
name|nnURI
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|,
operator|new
name|ClientHAProxyFactory
argument_list|<
name|ClientProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClientProtocol
name|createProxy
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|InetSocketAddress
name|nnAddr
parameter_list|,
name|Class
argument_list|<
name|ClientProtocol
argument_list|>
name|xface
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|boolean
name|withRetries
parameter_list|,
name|AtomicBoolean
name|fallbackToSimpleAuth
parameter_list|)
block|{
return|return
name|proxyMap
operator|.
name|get
argument_list|(
name|nnAddr
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|NNProxyInfo
argument_list|<
name|ClientProtocol
argument_list|>
argument_list|>
name|getProxyAddresses
parameter_list|(
name|URI
name|uri
parameter_list|,
name|String
name|addressKey
parameter_list|)
block|{
name|List
argument_list|<
name|NNProxyInfo
argument_list|<
name|ClientProtocol
argument_list|>
argument_list|>
name|nnProxies
init|=
name|super
operator|.
name|getProxyAddresses
argument_list|(
name|uri
argument_list|,
name|addressKey
argument_list|)
decl_stmt|;
for|for
control|(
name|NNProxyInfo
argument_list|<
name|ClientProtocol
argument_list|>
name|nnProxy
range|:
name|nnProxies
control|)
block|{
name|String
name|addressStr
init|=
name|nnProxy
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|nnProxy
operator|.
name|setServiceProxyForTesting
argument_list|(
name|serviceProxyMap
operator|.
name|get
argument_list|(
name|addressStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nnProxies
return|;
block|}
block|}
expr_stmt|;
name|proxyProvider
operator|.
name|setObserverReadEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadOperationOnObserver ()
specifier|public
name|void
name|testReadOperationOnObserver
parameter_list|()
throws|throws
name|Exception
block|{
name|setupProxyProvider
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteOperationOnActive ()
specifier|public
name|void
name|testWriteOperationOnActive
parameter_list|()
throws|throws
name|Exception
block|{
name|setupProxyProvider
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|doWrite
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnreachableObserverWithNoBackup ()
specifier|public
name|void
name|testUnreachableObserverWithNoBackup
parameter_list|()
throws|throws
name|Exception
block|{
name|setupProxyProvider
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setUnreachable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Confirm that read still succeeds even though observer is not available
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnreachableObserverWithMultiple ()
specifier|public
name|void
name|testUnreachableObserverWithMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|setupProxyProvider
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|3
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setUnreachable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
comment|// Fall back to the second observer node
name|assertHandledBy
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setUnreachable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
comment|// Current index has changed, so although the first observer is back,
comment|// it should continue requesting from the second observer
name|assertHandledBy
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|3
index|]
operator|.
name|setUnreachable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
comment|// Now that second is unavailable, go back to using the first observer
name|assertHandledBy
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setUnreachable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
comment|// Both observers are now unavailable, so it should fall back to active
name|assertHandledBy
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testObserverToActive ()
specifier|public
name|void
name|testObserverToActive
parameter_list|()
throws|throws
name|Exception
block|{
name|setupProxyProvider
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|doWrite
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Transition an observer to active
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setStandbyState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
try|try
block|{
name|doWrite
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Write should fail; failover required"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
argument_list|,
name|StandbyException
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|proxyProvider
operator|.
name|performFailover
argument_list|(
name|proxyProvider
operator|.
name|getProxy
argument_list|()
operator|.
name|proxy
argument_list|)
expr_stmt|;
name|doWrite
argument_list|()
expr_stmt|;
comment|// After failover, previous observer is now active
name|assertHandledBy
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// Transition back to original state but second observer not available
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setUnreachable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|doWrite
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|proxyProvider
operator|.
name|performFailover
argument_list|(
name|proxyProvider
operator|.
name|getProxy
argument_list|()
operator|.
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
name|doWrite
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testObserverToStandby ()
specifier|public
name|void
name|testObserverToStandby
parameter_list|()
throws|throws
name|Exception
block|{
name|setupProxyProvider
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setStandbyState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|2
index|]
operator|.
name|setStandbyState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleObserverToStandby ()
specifier|public
name|void
name|testSingleObserverToStandby
parameter_list|()
throws|throws
name|Exception
block|{
name|setupProxyProvider
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|0
index|]
operator|.
name|setActiveState
argument_list|()
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setStandbyState
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|namenodeAnswers
index|[
literal|1
index|]
operator|.
name|setObserverState
argument_list|()
expr_stmt|;
comment|// The proxy provider still thinks the second NN is in observer state,
comment|// so it will take a second call for it to notice the new observer
name|doRead
argument_list|()
expr_stmt|;
name|doRead
argument_list|()
expr_stmt|;
name|assertHandledBy
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|doRead ()
specifier|private
name|void
name|doRead
parameter_list|()
throws|throws
name|Exception
block|{
name|doRead
argument_list|(
name|proxyProvider
operator|.
name|getProxy
argument_list|()
operator|.
name|proxy
argument_list|)
expr_stmt|;
block|}
DECL|method|doWrite ()
specifier|private
name|void
name|doWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|doWrite
argument_list|(
name|proxyProvider
operator|.
name|getProxy
argument_list|()
operator|.
name|proxy
argument_list|)
expr_stmt|;
block|}
DECL|method|assertHandledBy (int namenodeIdx)
specifier|private
name|void
name|assertHandledBy
parameter_list|(
name|int
name|namenodeIdx
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|namenodeAddrs
index|[
name|namenodeIdx
index|]
argument_list|,
name|proxyProvider
operator|.
name|getLastProxy
argument_list|()
operator|.
name|proxyInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|doWrite (ClientProtocol client)
specifier|private
specifier|static
name|void
name|doWrite
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|client
operator|.
name|reportBadBlocks
argument_list|(
name|EMPTY_BLOCKS
argument_list|)
expr_stmt|;
block|}
DECL|method|doRead (ClientProtocol client)
specifier|private
specifier|static
name|void
name|doRead
parameter_list|(
name|ClientProtocol
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|client
operator|.
name|checkAccess
argument_list|(
literal|"/"
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
comment|/**    * An {@link Answer} used for mocking of {@link ClientProtocol} and    * {@link HAServiceProtocol}. Setting the state or unreachability of this    * Answer will make the linked ClientProtocol respond as if it was    * communicating with a NameNode of the corresponding state. It is in Standby    * state by default.    */
DECL|class|NameNodeAnswer
specifier|private
specifier|static
class|class
name|NameNodeAnswer
block|{
DECL|field|unreachable
specifier|private
specifier|volatile
name|boolean
name|unreachable
init|=
literal|false
decl_stmt|;
comment|// Standby state by default
DECL|field|allowWrites
specifier|private
specifier|volatile
name|boolean
name|allowWrites
init|=
literal|false
decl_stmt|;
DECL|field|allowReads
specifier|private
specifier|volatile
name|boolean
name|allowReads
init|=
literal|false
decl_stmt|;
DECL|field|clientAnswer
specifier|private
name|ClientProtocolAnswer
name|clientAnswer
init|=
operator|new
name|ClientProtocolAnswer
argument_list|()
decl_stmt|;
DECL|field|serviceAnswer
specifier|private
name|HAServiceProtocolAnswer
name|serviceAnswer
init|=
operator|new
name|HAServiceProtocolAnswer
argument_list|()
decl_stmt|;
DECL|class|HAServiceProtocolAnswer
specifier|private
class|class
name|HAServiceProtocolAnswer
implements|implements
name|Answer
argument_list|<
name|HAServiceStatus
argument_list|>
block|{
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|HAServiceStatus
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|HAServiceStatus
name|status
init|=
name|mock
argument_list|(
name|HAServiceStatus
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowReads
operator|&&
name|allowWrites
condition|)
block|{
name|when
argument_list|(
name|status
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|allowReads
condition|)
block|{
name|when
argument_list|(
name|status
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|HAServiceState
operator|.
name|OBSERVER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|when
argument_list|(
name|status
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
block|}
return|return
name|status
return|;
block|}
block|}
DECL|class|ClientProtocolAnswer
specifier|private
class|class
name|ClientProtocolAnswer
implements|implements
name|Answer
argument_list|<
name|Void
argument_list|>
block|{
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocationOnMock)
specifier|public
name|Void
name|answer
parameter_list|(
name|InvocationOnMock
name|invocationOnMock
parameter_list|)
throws|throws
name|Throwable
block|{
if|if
condition|(
name|unreachable
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unavailable"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|invocationOnMock
operator|.
name|getMethod
argument_list|()
operator|.
name|getName
argument_list|()
condition|)
block|{
case|case
literal|"reportBadBlocks"
case|:
if|if
condition|(
operator|!
name|allowWrites
condition|)
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
name|StandbyException
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"No writes!"
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
case|case
literal|"checkAccess"
case|:
if|if
condition|(
operator|!
name|allowReads
condition|)
block|{
throw|throw
operator|new
name|RemoteException
argument_list|(
name|StandbyException
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
literal|"No reads!"
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only reportBadBlocks and checkAccess supported!"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|setUnreachable (boolean unreachable)
name|void
name|setUnreachable
parameter_list|(
name|boolean
name|unreachable
parameter_list|)
block|{
name|this
operator|.
name|unreachable
operator|=
name|unreachable
expr_stmt|;
block|}
DECL|method|setActiveState ()
name|void
name|setActiveState
parameter_list|()
block|{
name|allowReads
operator|=
literal|true
expr_stmt|;
name|allowWrites
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setStandbyState ()
name|void
name|setStandbyState
parameter_list|()
block|{
name|allowReads
operator|=
literal|false
expr_stmt|;
name|allowWrites
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|setObserverState ()
name|void
name|setObserverState
parameter_list|()
block|{
name|allowReads
operator|=
literal|true
expr_stmt|;
name|allowWrites
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

