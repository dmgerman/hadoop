begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
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
name|getLongGauge
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
name|util
operator|.
name|UUID
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|MiniOzoneClassicCluster
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
name|OzoneConsts
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
name|container
operator|.
name|common
operator|.
name|SCMTestUtils
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerReport
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsRequestProto
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
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|ContainerStat
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
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|SCMMetrics
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
name|scm
operator|.
name|node
operator|.
name|SCMNodeManager
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

begin_comment
comment|/**  * This class tests the metrics of Storage Container Manager.  */
end_comment

begin_class
DECL|class|TestSCMMetrics
specifier|public
class|class
name|TestSCMMetrics
block|{
comment|/**    * Set the timeout for each test.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|90000
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneClassicCluster
name|cluster
init|=
literal|null
decl_stmt|;
annotation|@
name|Test
DECL|method|testContainerMetrics ()
specifier|public
name|void
name|testContainerMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nodeCount
init|=
literal|2
decl_stmt|;
name|int
name|numReport
init|=
literal|2
decl_stmt|;
name|long
name|size
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|5
decl_stmt|;
name|long
name|used
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|2
decl_stmt|;
name|long
name|readBytes
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|1
decl_stmt|;
name|long
name|writeBytes
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|2
decl_stmt|;
name|int
name|keyCount
init|=
literal|1000
decl_stmt|;
name|int
name|readCount
init|=
literal|100
decl_stmt|;
name|int
name|writeCount
init|=
literal|50
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|nodeCount
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ContainerStat
name|stat
init|=
operator|new
name|ContainerStat
argument_list|(
name|size
argument_list|,
name|used
argument_list|,
name|keyCount
argument_list|,
name|readBytes
argument_list|,
name|writeBytes
argument_list|,
name|readCount
argument_list|,
name|writeCount
argument_list|)
decl_stmt|;
name|StorageContainerManager
name|scmManager
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
decl_stmt|;
name|ContainerReportsRequestProto
name|request
init|=
name|createContainerReport
argument_list|(
name|numReport
argument_list|,
name|stat
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|fstDatanodeID
init|=
name|request
operator|.
name|getDatanodeID
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
name|scmManager
operator|.
name|sendContainerReport
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// verify container stat metrics
name|MetricsRecordBuilder
name|scmMetrics
init|=
name|getMetrics
argument_list|(
name|SCMMetrics
operator|.
name|SOURCE_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|size
operator|*
name|numReport
argument_list|,
name|getLongGauge
argument_list|(
literal|"LastContainerReportSize"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|used
operator|*
name|numReport
argument_list|,
name|getLongGauge
argument_list|(
literal|"LastContainerReportUsed"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readBytes
operator|*
name|numReport
argument_list|,
name|getLongGauge
argument_list|(
literal|"LastContainerReportReadBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|writeBytes
operator|*
name|numReport
argument_list|,
name|getLongGauge
argument_list|(
literal|"LastContainerReportWriteBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyCount
operator|*
name|numReport
argument_list|,
name|getLongGauge
argument_list|(
literal|"LastContainerReportKeyCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readCount
operator|*
name|numReport
argument_list|,
name|getLongGauge
argument_list|(
literal|"LastContainerReportReadCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|writeCount
operator|*
name|numReport
argument_list|,
name|getLongGauge
argument_list|(
literal|"LastContainerReportWriteCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
comment|// add one new report
name|request
operator|=
name|createContainerReport
argument_list|(
literal|1
argument_list|,
name|stat
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|sndDatanodeID
init|=
name|request
operator|.
name|getDatanodeID
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
name|scmManager
operator|.
name|sendContainerReport
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|scmMetrics
operator|=
name|getMetrics
argument_list|(
name|SCMMetrics
operator|.
name|SOURCE_NAME
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
operator|*
operator|(
name|numReport
operator|+
literal|1
operator|)
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportSize"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|used
operator|*
operator|(
name|numReport
operator|+
literal|1
operator|)
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportUsed"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readBytes
operator|*
operator|(
name|numReport
operator|+
literal|1
operator|)
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportReadBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|writeBytes
operator|*
operator|(
name|numReport
operator|+
literal|1
operator|)
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyCount
operator|*
operator|(
name|numReport
operator|+
literal|1
operator|)
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportKeyCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readCount
operator|*
operator|(
name|numReport
operator|+
literal|1
operator|)
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportReadCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|writeCount
operator|*
operator|(
name|numReport
operator|+
literal|1
operator|)
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
comment|// Re-send reports but with different value for validating
comment|// the aggregation.
name|stat
operator|=
operator|new
name|ContainerStat
argument_list|(
literal|100
argument_list|,
literal|50
argument_list|,
literal|3
argument_list|,
literal|50
argument_list|,
literal|60
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|scmManager
operator|.
name|sendContainerReport
argument_list|(
name|createContainerReport
argument_list|(
literal|1
argument_list|,
name|stat
argument_list|,
name|fstDatanodeID
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|=
operator|new
name|ContainerStat
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|scmManager
operator|.
name|sendContainerReport
argument_list|(
name|createContainerReport
argument_list|(
literal|1
argument_list|,
name|stat
argument_list|,
name|sndDatanodeID
argument_list|)
argument_list|)
expr_stmt|;
comment|// the global container metrics value should be updated
name|scmMetrics
operator|=
name|getMetrics
argument_list|(
name|SCMMetrics
operator|.
name|SOURCE_NAME
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|101
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportSize"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|51
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportUsed"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|51
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportReadBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|61
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportKeyCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportReadCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
annotation|@
name|Test
DECL|method|testStaleNodeContainerReport ()
specifier|public
name|void
name|testStaleNodeContainerReport
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nodeCount
init|=
literal|2
decl_stmt|;
name|int
name|numReport
init|=
literal|2
decl_stmt|;
name|long
name|size
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|5
decl_stmt|;
name|long
name|used
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|2
decl_stmt|;
name|long
name|readBytes
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|1
decl_stmt|;
name|long
name|writeBytes
init|=
name|OzoneConsts
operator|.
name|GB
operator|*
literal|2
decl_stmt|;
name|int
name|keyCount
init|=
literal|1000
decl_stmt|;
name|int
name|readCount
init|=
literal|100
decl_stmt|;
name|int
name|writeCount
init|=
literal|50
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|nodeCount
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ContainerStat
name|stat
init|=
operator|new
name|ContainerStat
argument_list|(
name|size
argument_list|,
name|used
argument_list|,
name|keyCount
argument_list|,
name|readBytes
argument_list|,
name|writeBytes
argument_list|,
name|readCount
argument_list|,
name|writeCount
argument_list|)
decl_stmt|;
name|StorageContainerManager
name|scmManager
init|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
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
literal|0
argument_list|)
decl_stmt|;
name|String
name|datanodeUuid
init|=
name|dataNode
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
name|ContainerReportsRequestProto
name|request
init|=
name|createContainerReport
argument_list|(
name|numReport
argument_list|,
name|stat
argument_list|,
name|datanodeUuid
argument_list|)
decl_stmt|;
name|scmManager
operator|.
name|sendContainerReport
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|scmMetrics
init|=
name|getMetrics
argument_list|(
name|SCMMetrics
operator|.
name|SOURCE_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|size
operator|*
name|numReport
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportSize"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|used
operator|*
name|numReport
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportUsed"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readBytes
operator|*
name|numReport
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportReadBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|writeBytes
operator|*
name|numReport
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteBytes"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyCount
operator|*
name|numReport
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportKeyCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readCount
operator|*
name|numReport
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportReadCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|writeCount
operator|*
name|numReport
argument_list|,
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteCount"
argument_list|,
name|scmMetrics
argument_list|)
argument_list|)
expr_stmt|;
comment|// reset stale interval time to move node from healthy to stale
name|SCMNodeManager
name|nodeManager
init|=
operator|(
name|SCMNodeManager
operator|)
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
decl_stmt|;
name|nodeManager
operator|.
name|setStaleNodeIntervalMs
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// verify the metrics when node becomes stale
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
name|MetricsRecordBuilder
name|metrics
init|=
name|getMetrics
argument_list|(
name|SCMMetrics
operator|.
name|SOURCE_NAME
argument_list|)
decl_stmt|;
return|return
literal|0
operator|==
name|getLongCounter
argument_list|(
literal|"ContainerReportSize"
argument_list|,
name|metrics
argument_list|)
operator|&&
literal|0
operator|==
name|getLongCounter
argument_list|(
literal|"ContainerReportUsed"
argument_list|,
name|metrics
argument_list|)
operator|&&
literal|0
operator|==
name|getLongCounter
argument_list|(
literal|"ContainerReportReadBytes"
argument_list|,
name|metrics
argument_list|)
operator|&&
literal|0
operator|==
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteBytes"
argument_list|,
name|metrics
argument_list|)
operator|&&
literal|0
operator|==
name|getLongCounter
argument_list|(
literal|"ContainerReportKeyCount"
argument_list|,
name|metrics
argument_list|)
operator|&&
literal|0
operator|==
name|getLongCounter
argument_list|(
literal|"ContainerReportReadCount"
argument_list|,
name|metrics
argument_list|)
operator|&&
literal|0
operator|==
name|getLongCounter
argument_list|(
literal|"ContainerReportWriteCount"
argument_list|,
name|metrics
argument_list|)
return|;
block|}
argument_list|,
literal|1000
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
DECL|method|createContainerReport (int numReport, ContainerStat stat, String datanodeUuid)
specifier|private
name|ContainerReportsRequestProto
name|createContainerReport
parameter_list|(
name|int
name|numReport
parameter_list|,
name|ContainerStat
name|stat
parameter_list|,
name|String
name|datanodeUuid
parameter_list|)
block|{
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsRequestProto
operator|.
name|Builder
name|reportsBuilder
init|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsRequestProto
operator|.
name|newBuilder
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
name|numReport
condition|;
name|i
operator|++
control|)
block|{
name|ContainerReport
name|report
init|=
operator|new
name|ContainerReport
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
literal|"Simulated"
argument_list|)
argument_list|)
decl_stmt|;
name|report
operator|.
name|setSize
argument_list|(
name|stat
operator|.
name|getSize
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setBytesUsed
argument_list|(
name|stat
operator|.
name|getUsed
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setReadCount
argument_list|(
name|stat
operator|.
name|getReadCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setReadBytes
argument_list|(
name|stat
operator|.
name|getReadBytes
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setKeyCount
argument_list|(
name|stat
operator|.
name|getKeyCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setWriteCount
argument_list|(
name|stat
operator|.
name|getWriteCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setWriteBytes
argument_list|(
name|stat
operator|.
name|getWriteBytes
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|reportsBuilder
operator|.
name|addReports
argument_list|(
name|report
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DatanodeID
name|datanodeID
decl_stmt|;
if|if
condition|(
name|datanodeUuid
operator|==
literal|null
condition|)
block|{
name|datanodeID
operator|=
name|SCMTestUtils
operator|.
name|getDatanodeID
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|datanodeID
operator|=
operator|new
name|DatanodeID
argument_list|(
literal|"null"
argument_list|,
literal|"null"
argument_list|,
name|datanodeUuid
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|reportsBuilder
operator|.
name|setDatanodeID
argument_list|(
name|datanodeID
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|reportsBuilder
operator|.
name|setType
argument_list|(
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsRequestProto
operator|.
name|reportType
operator|.
name|fullReport
argument_list|)
expr_stmt|;
return|return
name|reportsBuilder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

