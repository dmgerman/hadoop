begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|ArrayList
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|DFSUtil
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

begin_class
DECL|class|TestBlockPoolManager
specifier|public
class|class
name|TestBlockPoolManager
block|{
DECL|field|LOG
specifier|private
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestBlockPoolManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mockDN
specifier|private
specifier|final
name|DataNode
name|mockDN
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DataNode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bpm
specifier|private
name|BlockPoolManager
name|bpm
decl_stmt|;
DECL|field|log
specifier|private
specifier|final
name|StringBuilder
name|log
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|mockIdx
specifier|private
name|int
name|mockIdx
init|=
literal|1
decl_stmt|;
annotation|@
name|Before
DECL|method|setupBPM ()
specifier|public
name|void
name|setupBPM
parameter_list|()
block|{
name|bpm
operator|=
operator|new
name|BlockPoolManager
argument_list|(
name|mockDN
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|BPOfferService
name|createBPOS
parameter_list|(
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|nnAddrs
parameter_list|,
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|lifelineNnAddrs
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|mockIdx
operator|++
decl_stmt|;
name|doLog
argument_list|(
literal|"create #"
operator|+
name|idx
argument_list|)
expr_stmt|;
specifier|final
name|BPOfferService
name|bpos
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|BPOfferService
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|"Mock BPOS #"
operator|+
name|idx
argument_list|)
operator|.
name|when
argument_list|(
name|bpos
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// Log refreshes
try|try
block|{
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|doLog
argument_list|(
literal|"refresh #"
operator|+
name|idx
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|bpos
argument_list|)
operator|.
name|refreshNNList
argument_list|(
name|Mockito
operator|.
expr|<
name|ArrayList
argument_list|<
name|InetSocketAddress
argument_list|>
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
expr|<
name|ArrayList
argument_list|<
name|InetSocketAddress
argument_list|>
operator|>
name|any
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
comment|// Log stops
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|doLog
argument_list|(
literal|"stop #"
operator|+
name|idx
argument_list|)
expr_stmt|;
name|bpm
operator|.
name|remove
argument_list|(
name|bpos
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|bpos
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|bpos
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|doLog (String string)
specifier|private
name|void
name|doLog
parameter_list|(
name|String
name|string
parameter_list|)
block|{
synchronized|synchronized
init|(
name|log
init|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|log
operator|.
name|append
argument_list|(
name|string
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSimpleSingleNS ()
specifier|public
name|void
name|testSimpleSingleNS
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
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|"hdfs://mock1:8020"
argument_list|)
expr_stmt|;
name|bpm
operator|.
name|refreshNamenodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"create #1\n"
argument_list|,
name|log
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFederationRefresh ()
specifier|public
name|void
name|testFederationRefresh
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
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
literal|"ns1,ns2"
argument_list|)
expr_stmt|;
name|addNN
argument_list|(
name|conf
argument_list|,
literal|"ns1"
argument_list|,
literal|"mock1:8020"
argument_list|)
expr_stmt|;
name|addNN
argument_list|(
name|conf
argument_list|,
literal|"ns2"
argument_list|,
literal|"mock1:8020"
argument_list|)
expr_stmt|;
name|bpm
operator|.
name|refreshNamenodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"create #1\n"
operator|+
literal|"create #2\n"
argument_list|,
name|log
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Remove the first NS
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
literal|"ns2"
argument_list|)
expr_stmt|;
name|bpm
operator|.
name|refreshNamenodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"stop #1\n"
operator|+
literal|"refresh #2\n"
argument_list|,
name|log
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Add back an NS -- this creates a new BPOS since the old
comment|// one for ns2 should have been previously retired
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
literal|"ns1,ns2"
argument_list|)
expr_stmt|;
name|bpm
operator|.
name|refreshNamenodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"create #3\n"
operator|+
literal|"refresh #2\n"
argument_list|,
name|log
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInternalNameService ()
specifier|public
name|void
name|testInternalNameService
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
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
literal|"ns1,ns2,ns3"
argument_list|)
expr_stmt|;
name|addNN
argument_list|(
name|conf
argument_list|,
literal|"ns1"
argument_list|,
literal|"mock1:8020"
argument_list|)
expr_stmt|;
name|addNN
argument_list|(
name|conf
argument_list|,
literal|"ns2"
argument_list|,
literal|"mock1:8020"
argument_list|)
expr_stmt|;
name|addNN
argument_list|(
name|conf
argument_list|,
literal|"ns3"
argument_list|,
literal|"mock1:8020"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_INTERNAL_NAMESERVICES_KEY
argument_list|,
literal|"ns1"
argument_list|)
expr_stmt|;
name|bpm
operator|.
name|refreshNamenodes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"create #1\n"
argument_list|,
name|log
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|BPOfferService
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|BPOfferService
argument_list|>
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|bpm
argument_list|,
literal|"bpByNameserviceId"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"ns2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"ns3"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"ns1"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|addNN (Configuration conf, String ns, String addr)
specifier|private
specifier|static
name|void
name|addNN
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|ns
parameter_list|,
name|String
name|addr
parameter_list|)
block|{
name|String
name|key
init|=
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|ns
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|key
argument_list|,
name|addr
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

