begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

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
name|StorageType
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
name|net
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
name|datatransfer
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
name|server
operator|.
name|datanode
operator|.
name|metrics
operator|.
name|DataNodeMetrics
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
name|DataChecksum
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
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
name|DataOutputStream
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|*
import|;
end_import

begin_comment
comment|/**  * Mock-based unit test to verify that the DataXceiver correctly handles the  * LazyPersist hint from clients.  */
end_comment

begin_class
DECL|class|TestDataXceiverLazyPersistHint
specifier|public
class|class
name|TestDataXceiverLazyPersistHint
block|{
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|enum|PeerLocality
specifier|private
enum|enum
name|PeerLocality
block|{
DECL|enumConstant|LOCAL
name|LOCAL
block|,
DECL|enumConstant|REMOTE
name|REMOTE
block|}
DECL|enum|NonLocalLazyPersist
specifier|private
enum|enum
name|NonLocalLazyPersist
block|{
DECL|enumConstant|ALLOWED
name|ALLOWED
block|,
DECL|enumConstant|NOT_ALLOWED
name|NOT_ALLOWED
block|}
comment|/**    * Ensure that the correct hint is passed to the block receiver when    * the client is local.    */
annotation|@
name|Test
DECL|method|testWithLocalClient ()
specifier|public
name|void
name|testWithLocalClient
parameter_list|()
throws|throws
name|IOException
block|{
name|ArgumentCaptor
argument_list|<
name|Boolean
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataXceiver
name|xceiver
init|=
name|makeStubDataXceiver
argument_list|(
name|PeerLocality
operator|.
name|LOCAL
argument_list|,
name|NonLocalLazyPersist
operator|.
name|NOT_ALLOWED
argument_list|,
name|captor
argument_list|)
decl_stmt|;
for|for
control|(
name|Boolean
name|lazyPersistSetting
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|issueWriteBlockCall
argument_list|(
name|xceiver
argument_list|,
name|lazyPersistSetting
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|captor
operator|.
name|getValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|lazyPersistSetting
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that hint is always false when the client is remote.    */
annotation|@
name|Test
DECL|method|testWithRemoteClient ()
specifier|public
name|void
name|testWithRemoteClient
parameter_list|()
throws|throws
name|IOException
block|{
name|ArgumentCaptor
argument_list|<
name|Boolean
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataXceiver
name|xceiver
init|=
name|makeStubDataXceiver
argument_list|(
name|PeerLocality
operator|.
name|REMOTE
argument_list|,
name|NonLocalLazyPersist
operator|.
name|NOT_ALLOWED
argument_list|,
name|captor
argument_list|)
decl_stmt|;
for|for
control|(
name|Boolean
name|lazyPersistSetting
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|issueWriteBlockCall
argument_list|(
name|xceiver
argument_list|,
name|lazyPersistSetting
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|captor
operator|.
name|getValue
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that the correct hint is passed to the block receiver when    * the client is remote AND dfs.datanode.allow.non.local.lazy.persist    * is set to true.    */
annotation|@
name|Test
DECL|method|testOverrideWithRemoteClient ()
specifier|public
name|void
name|testOverrideWithRemoteClient
parameter_list|()
throws|throws
name|IOException
block|{
name|ArgumentCaptor
argument_list|<
name|Boolean
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataXceiver
name|xceiver
init|=
name|makeStubDataXceiver
argument_list|(
name|PeerLocality
operator|.
name|REMOTE
argument_list|,
name|NonLocalLazyPersist
operator|.
name|ALLOWED
argument_list|,
name|captor
argument_list|)
decl_stmt|;
for|for
control|(
name|Boolean
name|lazyPersistSetting
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|issueWriteBlockCall
argument_list|(
name|xceiver
argument_list|,
name|lazyPersistSetting
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|captor
operator|.
name|getValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|lazyPersistSetting
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Issue a write block call with dummy parameters. The only parameter useful    * for this test is the value of lazyPersist.    */
DECL|method|issueWriteBlockCall (DataXceiver xceiver, boolean lazyPersist)
specifier|private
name|void
name|issueWriteBlockCall
parameter_list|(
name|DataXceiver
name|xceiver
parameter_list|,
name|boolean
name|lazyPersist
parameter_list|)
throws|throws
name|IOException
block|{
name|xceiver
operator|.
name|writeBlock
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
literal|"Dummy-pool"
argument_list|,
literal|0L
argument_list|)
argument_list|,
name|StorageType
operator|.
name|RAM_DISK
argument_list|,
literal|null
argument_list|,
literal|"Dummy-Client"
argument_list|,
operator|new
name|DatanodeInfo
index|[
literal|0
index|]
argument_list|,
operator|new
name|StorageType
index|[
literal|0
index|]
argument_list|,
name|mock
argument_list|(
name|DatanodeInfo
operator|.
name|class
argument_list|)
argument_list|,
name|BlockConstructionStage
operator|.
name|PIPELINE_SETUP_CREATE
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|DataChecksum
operator|.
name|Type
operator|.
name|NULL
argument_list|,
literal|0
argument_list|)
argument_list|,
name|CachingStrategy
operator|.
name|newDefaultStrategy
argument_list|()
argument_list|,
name|lazyPersist
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// Helper functions to setup the mock objects.
DECL|method|makeStubDataXceiver ( PeerLocality locality, NonLocalLazyPersist nonLocalLazyPersist, final ArgumentCaptor<Boolean> captor)
specifier|private
specifier|static
name|DataXceiver
name|makeStubDataXceiver
parameter_list|(
name|PeerLocality
name|locality
parameter_list|,
name|NonLocalLazyPersist
name|nonLocalLazyPersist
parameter_list|,
specifier|final
name|ArgumentCaptor
argument_list|<
name|Boolean
argument_list|>
name|captor
parameter_list|)
throws|throws
name|IOException
block|{
name|DataXceiver
name|xceiverSpy
init|=
name|spy
argument_list|(
name|DataXceiver
operator|.
name|create
argument_list|(
name|getMockPeer
argument_list|(
name|locality
argument_list|)
argument_list|,
name|getMockDn
argument_list|(
name|nonLocalLazyPersist
argument_list|)
argument_list|,
name|mock
argument_list|(
name|DataXceiverServer
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|mock
argument_list|(
name|BlockReceiver
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|xceiverSpy
argument_list|)
operator|.
name|getBlockReceiver
argument_list|(
name|any
argument_list|(
name|ExtendedBlock
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|StorageType
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|DataInputStream
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|BlockConstructionStage
operator|.
name|class
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|DatanodeInfo
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|DataNode
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|DataChecksum
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|CachingStrategy
operator|.
name|class
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|doReturn
argument_list|(
name|mock
argument_list|(
name|DataOutputStream
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|xceiverSpy
argument_list|)
operator|.
name|getBufferedOutputStream
argument_list|()
expr_stmt|;
return|return
name|xceiverSpy
return|;
block|}
DECL|method|getMockPeer (PeerLocality locality)
specifier|private
specifier|static
name|Peer
name|getMockPeer
parameter_list|(
name|PeerLocality
name|locality
parameter_list|)
block|{
name|Peer
name|peer
init|=
name|mock
argument_list|(
name|Peer
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|peer
operator|.
name|isLocal
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|locality
operator|==
name|PeerLocality
operator|.
name|LOCAL
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|peer
operator|.
name|getRemoteAddressString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"1.1.1.1:1000"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|peer
operator|.
name|getLocalAddressString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"2.2.2.2:2000"
argument_list|)
expr_stmt|;
return|return
name|peer
return|;
block|}
DECL|method|getMockDn (NonLocalLazyPersist nonLocalLazyPersist)
specifier|private
specifier|static
name|DataNode
name|getMockDn
parameter_list|(
name|NonLocalLazyPersist
name|nonLocalLazyPersist
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFS_DATANODE_NON_LOCAL_LAZY_PERSIST
argument_list|,
name|nonLocalLazyPersist
operator|==
name|NonLocalLazyPersist
operator|.
name|ALLOWED
argument_list|)
expr_stmt|;
name|DNConf
name|dnConf
init|=
operator|new
name|DNConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DataNodeMetrics
name|mockMetrics
init|=
name|mock
argument_list|(
name|DataNodeMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataNode
name|mockDn
init|=
name|mock
argument_list|(
name|DataNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockDn
operator|.
name|getDnConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dnConf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockDn
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockDn
operator|.
name|getMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockMetrics
argument_list|)
expr_stmt|;
return|return
name|mockDn
return|;
block|}
block|}
end_class

end_unit

