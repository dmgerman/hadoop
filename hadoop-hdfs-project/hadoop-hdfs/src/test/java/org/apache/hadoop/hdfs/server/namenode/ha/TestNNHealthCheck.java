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
name|HA_HM_RPC_TIMEOUT_DEFAULT
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
name|HA_HM_RPC_TIMEOUT_KEY
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
name|DFS_NAMENODE_LIFELINE_RPC_ADDRESS_KEY
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
name|HealthCheckFailedException
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
name|NameNodeResourceChecker
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
name|tools
operator|.
name|NNHAServiceTarget
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
name|Mockito
import|;
end_import

begin_class
DECL|class|TestNNHealthCheck
specifier|public
class|class
name|TestNNHealthCheck
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
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
DECL|method|testNNHealthCheck ()
specifier|public
name|void
name|testNNHealthCheck
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
name|numDataNodes
argument_list|(
literal|0
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
name|doNNHealthCheckTest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNNHealthCheckWithLifelineAddress ()
specifier|public
name|void
name|testNNHealthCheckWithLifelineAddress
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_LIFELINE_RPC_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
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
name|numDataNodes
argument_list|(
literal|0
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
name|doNNHealthCheckTest
argument_list|()
expr_stmt|;
block|}
DECL|method|doNNHealthCheckTest ()
specifier|private
name|void
name|doNNHealthCheckTest
parameter_list|()
throws|throws
name|IOException
block|{
name|NameNodeResourceChecker
name|mockResourceChecker
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NameNodeResourceChecker
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|mockResourceChecker
argument_list|)
operator|.
name|hasAvailableDiskSpace
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNamesystem
argument_list|()
operator|.
name|setNNResourceChecker
argument_list|(
name|mockResourceChecker
argument_list|)
expr_stmt|;
name|NNHAServiceTarget
name|haTarget
init|=
operator|new
name|NNHAServiceTarget
argument_list|(
name|conf
argument_list|,
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"nn1"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|expectedTargetString
decl_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|DFS_NAMENODE_LIFELINE_RPC_ADDRESS_KEY
operator|+
literal|"."
operator|+
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
operator|+
literal|".nn1"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|expectedTargetString
operator|=
name|haTarget
operator|.
name|getHealthMonitorAddress
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|expectedTargetString
operator|=
name|haTarget
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Expected haTarget "
operator|+
name|haTarget
operator|+
literal|" containing "
operator|+
name|expectedTargetString
argument_list|,
name|haTarget
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|expectedTargetString
argument_list|)
argument_list|)
expr_stmt|;
name|HAServiceProtocol
name|rpc
init|=
name|haTarget
operator|.
name|getHealthMonitorProxy
argument_list|(
name|conf
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|HA_HM_RPC_TIMEOUT_KEY
argument_list|,
name|HA_HM_RPC_TIMEOUT_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
comment|// Should not throw error, which indicates healthy.
name|rpc
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|false
argument_list|)
operator|.
name|when
argument_list|(
name|mockResourceChecker
argument_list|)
operator|.
name|hasAvailableDiskSpace
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Should throw error - NN is unhealthy.
name|rpc
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should not have succeeded in calling monitorHealth"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HealthCheckFailedException
name|hcfe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"The NameNode has no resources available"
argument_list|,
name|hcfe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"The NameNode has no resources available"
argument_list|,
name|re
operator|.
name|unwrapRemoteException
argument_list|(
name|HealthCheckFailedException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

