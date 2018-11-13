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
name|ArrayList
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
name|CompletableFuture
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
name|CountDownLatch
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
name|client
operator|.
name|BlockID
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
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
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
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
name|MiniOzoneCluster
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
name|ozone
operator|.
name|container
operator|.
name|ContainerTestHelper
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
name|XceiverClientManager
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
name|XceiverClientMetrics
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
name|XceiverClientSpi
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|/**  * This class tests the metrics of XceiverClient.  */
end_comment

begin_class
DECL|class|TestXceiverClientMetrics
specifier|public
class|class
name|TestXceiverClientMetrics
block|{
comment|// only for testing
DECL|field|breakFlag
specifier|private
specifier|volatile
name|boolean
name|breakFlag
decl_stmt|;
DECL|field|latch
specifier|private
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|config
specifier|private
specifier|static
name|OzoneConfiguration
name|config
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|containerOwner
specifier|private
specifier|static
name|String
name|containerOwner
init|=
literal|"OZONE"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|config
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
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|getStorageContainerLocationClient
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMetrics ()
specifier|public
name|void
name|testMetrics
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
name|XceiverClientManager
name|clientManager
init|=
operator|new
name|XceiverClientManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ContainerWithPipeline
name|container
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|clientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|clientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|XceiverClientSpi
name|client
init|=
name|clientManager
operator|.
name|acquireClient
argument_list|(
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|containerMetrics
init|=
name|getMetrics
argument_list|(
name|XceiverClientMetrics
operator|.
name|SOURCE_NAME
argument_list|)
decl_stmt|;
comment|// Above request command is in a synchronous way, so there will be no
comment|// pending requests.
name|assertCounter
argument_list|(
literal|"PendingOps"
argument_list|,
literal|0L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"numPendingCreateContainer"
argument_list|,
literal|0L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
comment|// the counter value of average latency metric should be increased
name|assertCounter
argument_list|(
literal|"CreateContainerLatencyNumOps"
argument_list|,
literal|1L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|breakFlag
operator|=
literal|false
expr_stmt|;
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|int
name|numRequest
init|=
literal|10
decl_stmt|;
name|List
argument_list|<
name|CompletableFuture
argument_list|<
name|ContainerCommandResponseProto
argument_list|>
argument_list|>
name|computeResults
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// start new thread to send async requests
name|Thread
name|sendThread
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
while|while
condition|(
operator|!
name|breakFlag
condition|)
block|{
try|try
block|{
comment|// use async interface for testing pending metrics
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numRequest
condition|;
name|i
operator|++
control|)
block|{
name|BlockID
name|blockID
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|smallFileRequest
decl_stmt|;
name|smallFileRequest
operator|=
name|ContainerTestHelper
operator|.
name|getWriteSmallFileRequest
argument_list|(
name|client
operator|.
name|getPipeline
argument_list|()
argument_list|,
name|blockID
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|CompletableFuture
argument_list|<
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
argument_list|>
name|response
init|=
name|client
operator|.
name|sendCommandAsync
argument_list|(
name|smallFileRequest
argument_list|)
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|computeResults
operator|.
name|add
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{         }
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|sendThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
comment|// check if pending metric count is increased
name|MetricsRecordBuilder
name|metric
init|=
name|getMetrics
argument_list|(
name|XceiverClientMetrics
operator|.
name|SOURCE_NAME
argument_list|)
decl_stmt|;
name|long
name|pendingOps
init|=
name|getLongCounter
argument_list|(
literal|"PendingOps"
argument_list|,
name|metric
argument_list|)
decl_stmt|;
name|long
name|pendingPutSmallFileOps
init|=
name|getLongCounter
argument_list|(
literal|"numPendingPutSmallFile"
argument_list|,
name|metric
argument_list|)
decl_stmt|;
if|if
condition|(
name|pendingOps
operator|>
literal|0
operator|&&
name|pendingPutSmallFileOps
operator|>
literal|0
condition|)
block|{
comment|// reset break flag
name|breakFlag
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
comment|// blocking until we stop sending async requests
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// Wait for all futures being done.
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
block|{
for|for
control|(
name|CompletableFuture
name|future
range|:
name|computeResults
control|)
block|{
if|if
condition|(
operator|!
name|future
operator|.
name|isDone
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
argument_list|,
literal|100
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
comment|// the counter value of pending metrics should be decreased to 0
name|containerMetrics
operator|=
name|getMetrics
argument_list|(
name|XceiverClientMetrics
operator|.
name|SOURCE_NAME
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"PendingOps"
argument_list|,
literal|0L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"numPendingPutSmallFile"
argument_list|,
literal|0L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|clientManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

