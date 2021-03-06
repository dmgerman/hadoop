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
name|util
operator|.
name|List
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|MiniDFSNNTopology
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
name|Mockito
import|;
end_import

begin_comment
comment|/**   * Tests if DataNode process exits if all Block Pool services exit.   */
end_comment

begin_class
DECL|class|TestDataNodeExit
specifier|public
class|class
name|TestDataNodeExit
block|{
DECL|field|WAIT_TIME_IN_MILLIS
specifier|private
specifier|static
specifier|final
name|long
name|WAIT_TIME_IN_MILLIS
init|=
literal|10
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|100
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
name|simpleFederatedTopology
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|waitActive
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
DECL|method|stopBPServiceThreads (int numStopThreads, DataNode dn)
specifier|private
name|void
name|stopBPServiceThreads
parameter_list|(
name|int
name|numStopThreads
parameter_list|,
name|DataNode
name|dn
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|BPOfferService
argument_list|>
name|bpoList
init|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
decl_stmt|;
name|int
name|expected
init|=
name|dn
operator|.
name|getBpOsCount
argument_list|()
operator|-
name|numStopThreads
decl_stmt|;
name|int
name|index
init|=
name|numStopThreads
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|bpoList
operator|.
name|get
argument_list|(
name|index
operator|--
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|int
name|iterations
init|=
literal|3000
decl_stmt|;
comment|// Total 30 seconds MAX wait time
while|while
condition|(
name|dn
operator|.
name|getBpOsCount
argument_list|()
operator|!=
name|expected
operator|&&
name|iterations
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|WAIT_TIME_IN_MILLIS
argument_list|)
expr_stmt|;
name|iterations
operator|--
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Mismatch in number of BPServices running"
argument_list|,
name|expected
argument_list|,
name|dn
operator|.
name|getBpOsCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test BPService Thread Exit    */
annotation|@
name|Test
DECL|method|testBPServiceExit ()
specifier|public
name|void
name|testBPServiceExit
parameter_list|()
throws|throws
name|Exception
block|{
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|stopBPServiceThreads
argument_list|(
literal|1
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"DataNode should not exit"
argument_list|,
name|dn
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
name|stopBPServiceThreads
argument_list|(
literal|2
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"DataNode should exit"
argument_list|,
name|dn
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSendOOBToPeers ()
specifier|public
name|void
name|testSendOOBToPeers
parameter_list|()
throws|throws
name|Exception
block|{
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DataXceiverServer
name|spyXserver
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|dn
operator|.
name|getXferServer
argument_list|()
argument_list|)
decl_stmt|;
name|NullPointerException
name|npe
init|=
operator|new
name|NullPointerException
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|doThrow
argument_list|(
name|npe
argument_list|)
operator|.
name|when
argument_list|(
name|spyXserver
argument_list|)
operator|.
name|sendOOBToPeers
argument_list|()
expr_stmt|;
name|dn
operator|.
name|xserver
operator|=
name|spyXserver
expr_stmt|;
try|try
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"DataNode shutdown should not have thrown exception "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

