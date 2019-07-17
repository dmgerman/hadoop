begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.transport.server.ratis
package|package
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
name|transport
operator|.
name|server
operator|.
name|ratis
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|classification
operator|.
name|InterfaceAudience
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
name|metrics2
operator|.
name|MetricsSystem
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
name|annotation
operator|.
name|Metric
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
name|annotation
operator|.
name|Metrics
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
name|lib
operator|.
name|DefaultMetricsSystem
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
name|lib
operator|.
name|MutableCounterLong
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
name|lib
operator|.
name|MutableRate
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
name|lib
operator|.
name|MetricsRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
import|;
end_import

begin_comment
comment|/**  * This class is for maintaining Container State Machine statistics.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Metrics
argument_list|(
name|about
operator|=
literal|"Container State Machine Metrics"
argument_list|,
name|context
operator|=
literal|"dfs"
argument_list|)
DECL|class|CSMMetrics
specifier|public
class|class
name|CSMMetrics
block|{
DECL|field|SOURCE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_NAME
init|=
name|CSMMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
comment|// ratis op metrics metrics
DECL|field|numWriteStateMachineOps
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numWriteStateMachineOps
decl_stmt|;
DECL|field|numQueryStateMachineOps
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numQueryStateMachineOps
decl_stmt|;
DECL|field|numApplyTransactionOps
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numApplyTransactionOps
decl_stmt|;
DECL|field|numReadStateMachineOps
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numReadStateMachineOps
decl_stmt|;
DECL|field|numBytesWrittenCount
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBytesWrittenCount
decl_stmt|;
DECL|field|numBytesCommittedCount
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numBytesCommittedCount
decl_stmt|;
DECL|field|transactionLatency
specifier|private
annotation|@
name|Metric
name|MutableRate
name|transactionLatency
decl_stmt|;
DECL|field|opsLatency
specifier|private
name|MutableRate
index|[]
name|opsLatency
decl_stmt|;
DECL|field|registry
specifier|private
name|MetricsRegistry
name|registry
init|=
literal|null
decl_stmt|;
comment|// Failure Metrics
DECL|field|numWriteStateMachineFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numWriteStateMachineFails
decl_stmt|;
DECL|field|numQueryStateMachineFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numQueryStateMachineFails
decl_stmt|;
DECL|field|numApplyTransactionFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numApplyTransactionFails
decl_stmt|;
DECL|field|numReadStateMachineFails
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numReadStateMachineFails
decl_stmt|;
DECL|field|numReadStateMachineMissCount
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numReadStateMachineMissCount
decl_stmt|;
DECL|field|numStartTransactionVerifyFailures
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numStartTransactionVerifyFailures
decl_stmt|;
DECL|field|numContainerNotOpenVerifyFailures
specifier|private
annotation|@
name|Metric
name|MutableCounterLong
name|numContainerNotOpenVerifyFailures
decl_stmt|;
DECL|method|CSMMetrics ()
specifier|public
name|CSMMetrics
parameter_list|()
block|{
name|int
name|numCmdTypes
init|=
name|ContainerProtos
operator|.
name|Type
operator|.
name|values
argument_list|()
operator|.
name|length
decl_stmt|;
name|this
operator|.
name|opsLatency
operator|=
operator|new
name|MutableRate
index|[
name|numCmdTypes
index|]
expr_stmt|;
name|this
operator|.
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|CSMMetrics
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
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
name|numCmdTypes
condition|;
name|i
operator|++
control|)
block|{
name|opsLatency
index|[
name|i
index|]
operator|=
name|registry
operator|.
name|newRate
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|ContainerProtos
operator|.
name|Type
operator|.
name|forNumber
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|+
literal|" op"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|create (RaftGroupId gid)
specifier|public
specifier|static
name|CSMMetrics
name|create
parameter_list|(
name|RaftGroupId
name|gid
parameter_list|)
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
name|ms
operator|.
name|register
argument_list|(
name|SOURCE_NAME
operator|+
name|gid
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Container State Machine"
argument_list|,
operator|new
name|CSMMetrics
argument_list|()
argument_list|)
return|;
block|}
DECL|method|incNumWriteStateMachineOps ()
specifier|public
name|void
name|incNumWriteStateMachineOps
parameter_list|()
block|{
name|numWriteStateMachineOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumQueryStateMachineOps ()
specifier|public
name|void
name|incNumQueryStateMachineOps
parameter_list|()
block|{
name|numQueryStateMachineOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumReadStateMachineOps ()
specifier|public
name|void
name|incNumReadStateMachineOps
parameter_list|()
block|{
name|numReadStateMachineOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumApplyTransactionsOps ()
specifier|public
name|void
name|incNumApplyTransactionsOps
parameter_list|()
block|{
name|numApplyTransactionOps
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumWriteStateMachineFails ()
specifier|public
name|void
name|incNumWriteStateMachineFails
parameter_list|()
block|{
name|numWriteStateMachineFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumQueryStateMachineFails ()
specifier|public
name|void
name|incNumQueryStateMachineFails
parameter_list|()
block|{
name|numQueryStateMachineFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumBytesWrittenCount (long value)
specifier|public
name|void
name|incNumBytesWrittenCount
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|numBytesWrittenCount
operator|.
name|incr
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|incNumBytesCommittedCount (long value)
specifier|public
name|void
name|incNumBytesCommittedCount
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|numBytesCommittedCount
operator|.
name|incr
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|incNumReadStateMachineFails ()
specifier|public
name|void
name|incNumReadStateMachineFails
parameter_list|()
block|{
name|numReadStateMachineFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumReadStateMachineMissCount ()
specifier|public
name|void
name|incNumReadStateMachineMissCount
parameter_list|()
block|{
name|numReadStateMachineMissCount
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumApplyTransactionsFails ()
specifier|public
name|void
name|incNumApplyTransactionsFails
parameter_list|()
block|{
name|numApplyTransactionFails
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumWriteStateMachineOps ()
specifier|public
name|long
name|getNumWriteStateMachineOps
parameter_list|()
block|{
return|return
name|numWriteStateMachineOps
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumQueryStateMachineOps ()
specifier|public
name|long
name|getNumQueryStateMachineOps
parameter_list|()
block|{
return|return
name|numQueryStateMachineOps
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumApplyTransactionsOps ()
specifier|public
name|long
name|getNumApplyTransactionsOps
parameter_list|()
block|{
return|return
name|numApplyTransactionOps
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumWriteStateMachineFails ()
specifier|public
name|long
name|getNumWriteStateMachineFails
parameter_list|()
block|{
return|return
name|numWriteStateMachineFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumQueryStateMachineFails ()
specifier|public
name|long
name|getNumQueryStateMachineFails
parameter_list|()
block|{
return|return
name|numQueryStateMachineFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumApplyTransactionsFails ()
specifier|public
name|long
name|getNumApplyTransactionsFails
parameter_list|()
block|{
return|return
name|numApplyTransactionFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumReadStateMachineFails ()
specifier|public
name|long
name|getNumReadStateMachineFails
parameter_list|()
block|{
return|return
name|numReadStateMachineFails
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumReadStateMachineMissCount ()
specifier|public
name|long
name|getNumReadStateMachineMissCount
parameter_list|()
block|{
return|return
name|numReadStateMachineMissCount
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBytesWrittenCount ()
specifier|public
name|long
name|getNumBytesWrittenCount
parameter_list|()
block|{
return|return
name|numBytesWrittenCount
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumBytesCommittedCount ()
specifier|public
name|long
name|getNumBytesCommittedCount
parameter_list|()
block|{
return|return
name|numBytesCommittedCount
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|incPipelineLatency (ContainerProtos.Type type, long latencyNanos)
specifier|public
name|void
name|incPipelineLatency
parameter_list|(
name|ContainerProtos
operator|.
name|Type
name|type
parameter_list|,
name|long
name|latencyNanos
parameter_list|)
block|{
name|opsLatency
index|[
name|type
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|add
argument_list|(
name|latencyNanos
argument_list|)
expr_stmt|;
name|transactionLatency
operator|.
name|add
argument_list|(
name|latencyNanos
argument_list|)
expr_stmt|;
block|}
DECL|method|incNumStartTransactionVerifyFailures ()
specifier|public
name|void
name|incNumStartTransactionVerifyFailures
parameter_list|()
block|{
name|numStartTransactionVerifyFailures
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incNumContainerNotOpenVerifyFailures ()
specifier|public
name|void
name|incNumContainerNotOpenVerifyFailures
parameter_list|()
block|{
name|numContainerNotOpenVerifyFailures
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|unRegister ()
specifier|public
name|void
name|unRegister
parameter_list|()
block|{
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
name|ms
operator|.
name|unregisterSource
argument_list|(
name|SOURCE_NAME
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

