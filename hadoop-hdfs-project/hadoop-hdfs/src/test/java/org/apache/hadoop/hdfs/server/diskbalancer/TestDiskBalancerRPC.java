begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|fs
operator|.
name|Path
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
name|DFSTestUtil
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
name|datanode
operator|.
name|DiskBalancerWorkStatus
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|fsdataset
operator|.
name|impl
operator|.
name|FsVolumeImpl
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
name|DiskBalancerException
operator|.
name|Result
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
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|Map
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
import|import static
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
name|DiskBalancerWorkStatus
operator|.
name|Result
operator|.
name|NO_PLAN
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
name|server
operator|.
name|datanode
operator|.
name|DiskBalancerWorkStatus
operator|.
name|Result
operator|.
name|PLAN_DONE
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
name|server
operator|.
name|datanode
operator|.
name|DiskBalancerWorkStatus
operator|.
name|Result
operator|.
name|PLAN_UNDER_PROGRESS
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
DECL|method|testSubmitPlan ()
specifier|public
name|void
name|testSubmitPlan
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|int
name|planVersion
init|=
name|rpcTestHelper
operator|.
name|getPlanVersion
argument_list|()
decl_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
decl_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubmitPlanWithInvalidHash ()
specifier|public
name|void
name|testSubmitPlanWithInvalidHash
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|char
name|hashArray
index|[]
init|=
name|planHash
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|hashArray
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|planHash
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|hashArray
argument_list|)
expr_stmt|;
name|int
name|planVersion
init|=
name|rpcTestHelper
operator|.
name|getPlanVersion
argument_list|()
decl_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
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
name|thrown
operator|.
name|expect
argument_list|(
operator|new
name|DiskBalancerResultVerifier
argument_list|(
name|Result
operator|.
name|INVALID_PLAN_HASH
argument_list|)
argument_list|)
expr_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubmitPlanWithInvalidVersion ()
specifier|public
name|void
name|testSubmitPlanWithInvalidVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|int
name|planVersion
init|=
name|rpcTestHelper
operator|.
name|getPlanVersion
argument_list|()
decl_stmt|;
name|planVersion
operator|++
expr_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
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
name|thrown
operator|.
name|expect
argument_list|(
operator|new
name|DiskBalancerResultVerifier
argument_list|(
name|Result
operator|.
name|INVALID_PLAN_VERSION
argument_list|)
argument_list|)
expr_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubmitPlanWithInvalidPlan ()
specifier|public
name|void
name|testSubmitPlanWithInvalidPlan
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|int
name|planVersion
init|=
name|rpcTestHelper
operator|.
name|getPlanVersion
argument_list|()
decl_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
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
name|thrown
operator|.
name|expect
argument_list|(
operator|new
name|DiskBalancerResultVerifier
argument_list|(
name|Result
operator|.
name|INVALID_PLAN
argument_list|)
argument_list|)
expr_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCancelPlan ()
specifier|public
name|void
name|testCancelPlan
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|int
name|planVersion
init|=
name|rpcTestHelper
operator|.
name|getPlanVersion
argument_list|()
decl_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
decl_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|,
literal|false
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
DECL|method|testCancelNonExistentPlan ()
specifier|public
name|void
name|testCancelNonExistentPlan
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|char
name|hashArray
index|[]
init|=
name|planHash
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|hashArray
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|planHash
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|hashArray
argument_list|)
expr_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
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
name|thrown
operator|.
name|expect
argument_list|(
operator|new
name|DiskBalancerResultVerifier
argument_list|(
name|Result
operator|.
name|NO_SUCH_PLAN
argument_list|)
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
DECL|method|testCancelEmptyPlan ()
specifier|public
name|void
name|testCancelEmptyPlan
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
literal|""
decl_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
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
name|thrown
operator|.
name|expect
argument_list|(
operator|new
name|DiskBalancerResultVerifier
argument_list|(
name|Result
operator|.
name|NO_SUCH_PLAN
argument_list|)
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
DECL|method|testGetDiskBalancerVolumeMapping ()
specifier|public
name|void
name|testGetDiskBalancerVolumeMapping
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
name|String
name|volumeNameJson
init|=
name|dataNode
operator|.
name|getDiskBalancerSetting
argument_list|(
name|DiskBalancerConstants
operator|.
name|DISKBALANCER_VOLUME_NAME
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|volumeNameJson
argument_list|)
expr_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|volumemap
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|volumeNameJson
argument_list|,
name|HashMap
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|volumemap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetDiskBalancerInvalidSetting ()
specifier|public
name|void
name|testGetDiskBalancerInvalidSetting
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
specifier|final
name|String
name|invalidSetting
init|=
literal|"invalidSetting"
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
name|thrown
operator|.
name|expect
argument_list|(
operator|new
name|DiskBalancerResultVerifier
argument_list|(
name|Result
operator|.
name|UNKNOWN_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|dataNode
operator|.
name|getDiskBalancerSetting
argument_list|(
name|invalidSetting
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testgetDiskBalancerBandwidth ()
specifier|public
name|void
name|testgetDiskBalancerBandwidth
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|int
name|planVersion
init|=
name|rpcTestHelper
operator|.
name|getPlanVersion
argument_list|()
decl_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
decl_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|bandwidthString
init|=
name|dataNode
operator|.
name|getDiskBalancerSetting
argument_list|(
name|DiskBalancerConstants
operator|.
name|DISKBALANCER_BANDWIDTH
argument_list|)
decl_stmt|;
name|long
name|value
init|=
name|Long
operator|.
name|decode
argument_list|(
name|bandwidthString
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10L
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryPlan ()
specifier|public
name|void
name|testQueryPlan
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|String
name|planHash
init|=
name|rpcTestHelper
operator|.
name|getPlanHash
argument_list|()
decl_stmt|;
name|int
name|planVersion
init|=
name|rpcTestHelper
operator|.
name|getPlanVersion
argument_list|()
decl_stmt|;
name|NodePlan
name|plan
init|=
name|rpcTestHelper
operator|.
name|getPlan
argument_list|()
decl_stmt|;
name|dataNode
operator|.
name|submitDiskBalancerPlan
argument_list|(
name|planHash
argument_list|,
name|planVersion
argument_list|,
name|plan
operator|.
name|toJson
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DiskBalancerWorkStatus
name|status
init|=
name|dataNode
operator|.
name|queryDiskBalancerPlan
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|status
operator|.
name|getResult
argument_list|()
operator|==
name|PLAN_UNDER_PROGRESS
operator|||
name|status
operator|.
name|getResult
argument_list|()
operator|==
name|PLAN_DONE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryPlanWithoutSubmit ()
specifier|public
name|void
name|testQueryPlanWithoutSubmit
parameter_list|()
throws|throws
name|Exception
block|{
name|RpcTestHelper
name|rpcTestHelper
init|=
operator|new
name|RpcTestHelper
argument_list|()
operator|.
name|invoke
argument_list|()
decl_stmt|;
name|DataNode
name|dataNode
init|=
name|rpcTestHelper
operator|.
name|getDataNode
argument_list|()
decl_stmt|;
name|DiskBalancerWorkStatus
name|status
init|=
name|dataNode
operator|.
name|queryDiskBalancerPlan
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|status
operator|.
name|getResult
argument_list|()
operator|==
name|NO_PLAN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMoveBlockAcrossVolume ()
specifier|public
name|void
name|testMoveBlockAcrossVolume
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|100
decl_stmt|;
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
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
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
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
literal|"/tmp.txt"
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDatanodes
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|dnIndex
init|=
literal|0
decl_stmt|;
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
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|FsVolumeImpl
name|source
init|=
literal|null
decl_stmt|;
name|FsVolumeImpl
name|dest
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
literal|10
operator|*
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|DataNode
name|dnNode
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
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|refs
init|=
name|dnNode
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
decl_stmt|;
try|try
block|{
name|source
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dest
operator|=
operator|(
name|FsVolumeImpl
operator|)
name|refs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DiskBalancerTestUtil
operator|.
name|moveAllDataToDestVolume
argument_list|(
name|dnNode
operator|.
name|getFSDataset
argument_list|()
argument_list|,
name|source
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DiskBalancerTestUtil
operator|.
name|getBlockCount
argument_list|(
name|source
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|refs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|RpcTestHelper
specifier|private
class|class
name|RpcTestHelper
block|{
DECL|field|plan
specifier|private
name|NodePlan
name|plan
decl_stmt|;
DECL|field|planVersion
specifier|private
name|int
name|planVersion
decl_stmt|;
DECL|field|dataNode
specifier|private
name|DataNode
name|dataNode
decl_stmt|;
DECL|field|planHash
specifier|private
name|String
name|planHash
decl_stmt|;
DECL|method|getPlan ()
specifier|public
name|NodePlan
name|getPlan
parameter_list|()
block|{
return|return
name|plan
return|;
block|}
DECL|method|getPlanVersion ()
specifier|public
name|int
name|getPlanVersion
parameter_list|()
block|{
return|return
name|planVersion
return|;
block|}
DECL|method|getDataNode ()
specifier|public
name|DataNode
name|getDataNode
parameter_list|()
block|{
return|return
name|dataNode
return|;
block|}
DECL|method|getPlanHash ()
specifier|public
name|String
name|getPlanHash
parameter_list|()
block|{
return|return
name|planHash
return|;
block|}
DECL|method|invoke ()
specifier|public
name|RpcTestHelper
name|invoke
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
name|dataNode
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIndex
argument_list|)
expr_stmt|;
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
name|plan
operator|=
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
expr_stmt|;
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
name|planVersion
operator|=
literal|1
expr_stmt|;
name|planHash
operator|=
name|DigestUtils
operator|.
name|sha512Hex
argument_list|(
name|plan
operator|.
name|toJson
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

