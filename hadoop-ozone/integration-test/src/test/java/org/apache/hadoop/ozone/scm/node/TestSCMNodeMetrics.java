begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|node
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|StorageReportProto
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
name|hdds
operator|.
name|scm
operator|.
name|TestUtils
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
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|SCMNodeMetrics
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|ozone
operator|.
name|HddsDatanodeService
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
name|ozone
operator|.
name|MiniOzoneCluster
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertGauge
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getLongCounter
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
import|;
end_import

begin_comment
comment|/**  * Test cases to verify the metrics exposed by SCMNodeManager.  */
end_comment

begin_class
DECL|class|TestSCMNodeMetrics
specifier|public
class|class
name|TestSCMNodeMetrics
block|{
DECL|field|cluster
specifier|private
name|MiniOzoneCluster
name|cluster
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
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies heartbeat processing count.    *    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testHBProcessing ()
specifier|public
name|void
name|testHBProcessing
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MetricsRecordBuilder
name|metrics
init|=
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|hbProcessed
init|=
name|getLongCounter
argument_list|(
literal|"NumHBProcessed"
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|triggerHeartbeat
argument_list|()
expr_stmt|;
comment|// Give some time so that SCM receives and processes the heartbeat.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumHBProcessed"
argument_list|,
name|hbProcessed
operator|+
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verifies heartbeat processing failure count.    */
annotation|@
name|Test
DECL|method|testHBProcessingFailure ()
specifier|public
name|void
name|testHBProcessingFailure
parameter_list|()
block|{
name|MetricsRecordBuilder
name|metrics
init|=
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|hbProcessedFailed
init|=
name|getLongCounter
argument_list|(
literal|"NumHBProcessingFailed"
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|processHeartbeat
argument_list|(
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumHBProcessingFailed"
argument_list|,
name|hbProcessedFailed
operator|+
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verifies node report processing count.    *    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testNodeReportProcessing ()
specifier|public
name|void
name|testNodeReportProcessing
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MetricsRecordBuilder
name|metrics
init|=
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|nrProcessed
init|=
name|getLongCounter
argument_list|(
literal|"NumNodeReportProcessed"
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|HddsDatanodeService
name|datanode
init|=
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StorageReportProto
name|storageReport
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|datanode
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|,
literal|"/tmp"
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|90
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeReportProto
name|nodeReport
init|=
name|NodeReportProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addStorageReport
argument_list|(
name|storageReport
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|datanode
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContext
argument_list|()
operator|.
name|addReport
argument_list|(
name|nodeReport
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|processNodeReport
argument_list|(
name|datanode
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|,
name|nodeReport
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumNodeReportProcessed"
argument_list|,
name|nrProcessed
operator|+
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verifies node report processing failure count.    */
annotation|@
name|Test
DECL|method|testNodeReportProcessingFailure ()
specifier|public
name|void
name|testNodeReportProcessingFailure
parameter_list|()
block|{
name|MetricsRecordBuilder
name|metrics
init|=
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|nrProcessed
init|=
name|getLongCounter
argument_list|(
literal|"NumNodeReportProcessingFailed"
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|DatanodeDetails
name|datanode
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|StorageReportProto
name|storageReport
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|datanode
operator|.
name|getUuid
argument_list|()
argument_list|,
literal|"/tmp"
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|90
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeReportProto
name|nodeReport
init|=
name|NodeReportProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addStorageReport
argument_list|(
name|storageReport
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|processNodeReport
argument_list|(
name|datanode
argument_list|,
name|nodeReport
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumNodeReportProcessingFailed"
argument_list|,
name|nrProcessed
operator|+
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that datanode aggregated state and capacity metrics are reported.    */
annotation|@
name|Test
DECL|method|testNodeCountAndInfoMetricsReported ()
specifier|public
name|void
name|testNodeCountAndInfoMetricsReported
parameter_list|()
throws|throws
name|Exception
block|{
name|HddsDatanodeService
name|datanode
init|=
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StorageReportProto
name|storageReport
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|datanode
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|,
literal|"/tmp"
argument_list|,
literal|100
argument_list|,
literal|10
argument_list|,
literal|90
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeReportProto
name|nodeReport
init|=
name|NodeReportProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addStorageReport
argument_list|(
name|storageReport
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|datanode
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContext
argument_list|()
operator|.
name|addReport
argument_list|(
name|nodeReport
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|processNodeReport
argument_list|(
name|datanode
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|,
name|nodeReport
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"HealthyNodes"
argument_list|,
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"StaleNodes"
argument_list|,
literal|0
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"DeadNodes"
argument_list|,
literal|0
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"DecommissioningNodes"
argument_list|,
literal|0
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"DecommissionedNodes"
argument_list|,
literal|0
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"DiskCapacity"
argument_list|,
literal|100L
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"DiskUsed"
argument_list|,
literal|10L
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"DiskRemaining"
argument_list|,
literal|90L
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SSDCapacity"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SSDUsed"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"SSDRemaining"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|SCMNodeMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

