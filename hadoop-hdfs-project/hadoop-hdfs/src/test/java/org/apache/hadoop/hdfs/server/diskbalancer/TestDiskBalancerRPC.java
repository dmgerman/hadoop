begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer
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
name|diskbalancer
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|diskbalancer
operator|.
name|connectors
operator|.
name|ClusterConnector
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
name|diskbalancer
operator|.
name|connectors
operator|.
name|ConnectorFactory
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerCluster
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
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
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
name|diskbalancer
operator|.
name|planner
operator|.
name|GreedyPlanner
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
name|diskbalancer
operator|.
name|planner
operator|.
name|NodePlan
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
name|ExpectedException
import|;
end_import

begin_class
DECL|class|TestDiskBalancerRPC
specifier|public
class|class
name|TestDiskBalancerRPC
block|{
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
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
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DISK_BALANCER_ENABLED
argument_list|,
literal|true
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
literal|2
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
block|}
block|}
annotation|@
name|Test
DECL|method|testSubmitTestRpc ()
specifier|public
name|void
name|testSubmitTestRpc
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|dnIndex
init|=
literal|0
decl_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnIndex
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|ClusterConnector
name|nameNodeConnector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DiskBalancerCluster
name|diskBalancerCluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|nameNodeConnector
argument_list|)
decl_stmt|;
name|diskBalancerCluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|diskBalancerCluster
operator|.
name|setNodesToProcess
argument_list|(
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
name|DiskBalancerDataNode
name|node
init|=
name|diskBalancerCluster
operator|.
name|getNodeByUUID
argument_list|(
name|dataNode
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
name|GreedyPlanner
name|planner
init|=
operator|new
name|GreedyPlanner
argument_list|(
literal|10.0f
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|NodePlan
name|plan
init|=
operator|new
name|NodePlan
argument_list|(
name|node
operator|.
name|getDataNodeName
argument_list|()
argument_list|,
name|node
operator|.
name|getDataNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|balanceVolumeSet
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|getVolumeSets
argument_list|()
operator|.
name|get
argument_list|(
literal|"DISK"
argument_list|)
argument_list|,
name|plan
argument_list|)
expr_stmt|;
specifier|final
name|int
name|planVersion
init|=
literal|1
decl_stmt|;
comment|// So far we support only one version.
name|String
name|planHash
init|=
name|DigestUtils
operator|.
name|sha512Hex
argument_list|(
name|plan
operator|.
name|toJson
argument_list|()
argument_list|)
decl_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
literal|10
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCancelTestRpc ()
specifier|public
name|void
name|testCancelTestRpc
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|dnIndex
init|=
literal|0
decl_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnIndex
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|ClusterConnector
name|nameNodeConnector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DiskBalancerCluster
name|diskBalancerCluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|nameNodeConnector
argument_list|)
decl_stmt|;
name|diskBalancerCluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|diskBalancerCluster
operator|.
name|setNodesToProcess
argument_list|(
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|DiskBalancerDataNode
name|node
init|=
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|GreedyPlanner
name|planner
init|=
operator|new
name|GreedyPlanner
argument_list|(
literal|10.0f
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|NodePlan
name|plan
init|=
operator|new
name|NodePlan
argument_list|(
name|node
operator|.
name|getDataNodeName
argument_list|()
argument_list|,
name|node
operator|.
name|getDataNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|balanceVolumeSet
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|getVolumeSets
argument_list|()
operator|.
name|get
argument_list|(
literal|"DISK"
argument_list|)
argument_list|,
name|plan
argument_list|)
expr_stmt|;
specifier|final
name|int
name|planVersion
init|=
literal|0
decl_stmt|;
comment|// So far we support only one version.
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
name|String
name|planHash
init|=
name|DigestUtils
operator|.
name|sha512Hex
argument_list|(
name|plan
operator|.
name|toJson
argument_list|()
argument_list|)
decl_stmt|;
comment|// Since submitDiskBalancerPlan is not implemented yet, it throws an
comment|// Exception, this will be modified with the actual implementation.
try|try
block|{
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
literal|10
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskBalancerException
name|ex
parameter_list|)
block|{
comment|// Let us ignore this for time being.
block|}
name|thrown
operator|.
name|expect
argument_list|(
name|DiskBalancerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|dataNode
operator|.
name|cancelDiskBalancePlan
argument_list|(
name|planHash
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryTestRpc ()
specifier|public
name|void
name|testQueryTestRpc
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|dnIndex
init|=
literal|0
decl_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnIndex
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|ClusterConnector
name|nameNodeConnector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DiskBalancerCluster
name|diskBalancerCluster
init|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|nameNodeConnector
argument_list|)
decl_stmt|;
name|diskBalancerCluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|diskBalancerCluster
operator|.
name|setNodesToProcess
argument_list|(
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
argument_list|)
expr_stmt|;
name|DiskBalancerDataNode
name|node
init|=
name|diskBalancerCluster
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|GreedyPlanner
name|planner
init|=
operator|new
name|GreedyPlanner
argument_list|(
literal|10.0f
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|NodePlan
name|plan
init|=
operator|new
name|NodePlan
argument_list|(
name|node
operator|.
name|getDataNodeName
argument_list|()
argument_list|,
name|node
operator|.
name|getDataNodePort
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|balanceVolumeSet
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|getVolumeSets
argument_list|()
operator|.
name|get
argument_list|(
literal|"DISK"
argument_list|)
argument_list|,
name|plan
argument_list|)
expr_stmt|;
specifier|final
name|int
name|planVersion
init|=
literal|0
decl_stmt|;
comment|// So far we support only one version.
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
name|String
name|planHash
init|=
name|DigestUtils
operator|.
name|sha512Hex
argument_list|(
name|plan
operator|.
name|toJson
argument_list|()
argument_list|)
decl_stmt|;
comment|// Since submitDiskBalancerPlan is not implemented yet, it throws an
comment|// Exception, this will be modified with the actual implementation.
try|try
block|{
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
literal|10
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskBalancerException
name|ex
parameter_list|)
block|{
comment|// Let us ignore this for time being.
block|}
comment|// TODO : This will be fixed when we have implementation for this
comment|// function in server side.
name|thrown
operator|.
name|expect
argument_list|(
name|DiskBalancerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|dataNode
operator|.
name|queryDiskBalancerPlan
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testgetDiskBalancerSetting ()
specifier|public
name|void
name|testgetDiskBalancerSetting
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|dnIndex
init|=
literal|0
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|DiskBalancerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|dataNode
operator|.
name|getDiskBalancerSetting
argument_list|(
name|DiskBalancerConstants
operator|.
name|DISKBALANCER_BANDWIDTH
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

