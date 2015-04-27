begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|commons
operator|.
name|configuration
operator|.
name|SubsetConfiguration
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
name|metrics2
operator|.
name|AbstractMetric
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
name|MetricsRecord
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
name|MetricsSink
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
name|MetricsTag
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
name|impl
operator|.
name|MetricsSystemImpl
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_class
DECL|class|TestDataNodeFSDataSetSink
specifier|public
class|class
name|TestDataNodeFSDataSetSink
block|{
DECL|field|ms
specifier|private
specifier|static
specifier|final
name|MetricsSystemImpl
name|ms
init|=
operator|new
name|MetricsSystemImpl
argument_list|(
literal|"TestFSDataSet"
argument_list|)
decl_stmt|;
DECL|class|FSDataSetSinkTest
class|class
name|FSDataSetSinkTest
implements|implements
name|MetricsSink
block|{
DECL|field|nameMap
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|nameMap
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
comment|/**      * add a metrics record in the sink      *      * @param record the record to add      */
annotation|@
name|Override
DECL|method|putMetrics (MetricsRecord record)
specifier|public
name|void
name|putMetrics
parameter_list|(
name|MetricsRecord
name|record
parameter_list|)
block|{
comment|// let us do this only once, otherwise
comment|// our count could go out of sync.
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|AbstractMetric
name|m
range|:
name|record
operator|.
name|metrics
argument_list|()
control|)
block|{
if|if
condition|(
name|nameMap
operator|.
name|contains
argument_list|(
name|m
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
for|for
control|(
name|MetricsTag
name|t
range|:
name|record
operator|.
name|tags
argument_list|()
control|)
block|{
if|if
condition|(
name|nameMap
operator|.
name|contains
argument_list|(
name|t
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Flush any buffered metrics      */
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
block|{      }
comment|/**      * Initialize the plugin      *      * @param conf the configuration object for the plugin      */
annotation|@
name|Override
DECL|method|init (SubsetConfiguration conf)
specifier|public
name|void
name|init
parameter_list|(
name|SubsetConfiguration
name|conf
parameter_list|)
block|{
name|nameMap
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"DfsUsed"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"Capacity"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"Remaining"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"StorageInfo"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"NumFailedVolumes"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"LastVolumeFailureDate"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"EstimatedCapacityLostTotal"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"CacheUsed"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"CacheCapacity"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"NumBlocksCached"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"NumBlocksFailedToCache"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"NumBlocksFailedToUnCache"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"Context"
argument_list|)
expr_stmt|;
name|nameMap
operator|.
name|add
argument_list|(
literal|"Hostname"
argument_list|)
expr_stmt|;
block|}
DECL|method|getMapCount ()
specifier|public
name|int
name|getMapCount
parameter_list|()
block|{
return|return
name|nameMap
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getFoundKeyCount ()
specifier|public
name|int
name|getFoundKeyCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
annotation|@
name|Test
comment|/**    * This test creates a Source and then calls into the Sink that we    * have registered. That is calls into FSDataSetSinkTest    */
DECL|method|testFSDataSetMetrics ()
specifier|public
name|void
name|testFSDataSetMetrics
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|String
name|bpid
init|=
literal|"FSDatSetSink-Test"
decl_stmt|;
name|SimulatedFSDataset
name|fsdataset
init|=
operator|new
name|SimulatedFSDataset
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|fsdataset
operator|.
name|addBlockPool
argument_list|(
name|bpid
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|FSDataSetSinkTest
name|sink
init|=
operator|new
name|FSDataSetSinkTest
argument_list|()
decl_stmt|;
name|sink
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ms
operator|.
name|init
argument_list|(
literal|"Test"
argument_list|)
expr_stmt|;
name|ms
operator|.
name|start
argument_list|()
expr_stmt|;
name|ms
operator|.
name|register
argument_list|(
literal|"FSDataSetSource"
argument_list|,
literal|"FSDataSetSource"
argument_list|,
name|fsdataset
argument_list|)
expr_stmt|;
name|ms
operator|.
name|register
argument_list|(
literal|"FSDataSetSink"
argument_list|,
literal|"FSDataSetSink"
argument_list|,
name|sink
argument_list|)
expr_stmt|;
name|ms
operator|.
name|startMetricsMBeans
argument_list|()
expr_stmt|;
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
name|ms
operator|.
name|stopMetricsMBeans
argument_list|()
expr_stmt|;
name|ms
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// make sure we got all expected metric in the call back
name|assertEquals
argument_list|(
name|sink
operator|.
name|getMapCount
argument_list|()
argument_list|,
name|sink
operator|.
name|getFoundKeyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

