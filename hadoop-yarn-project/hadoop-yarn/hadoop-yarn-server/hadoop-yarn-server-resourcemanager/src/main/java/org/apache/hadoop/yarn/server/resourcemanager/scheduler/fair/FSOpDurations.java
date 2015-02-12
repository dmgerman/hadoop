begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
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
name|classification
operator|.
name|InterfaceStability
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
name|MetricsCollector
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
name|MetricsInfo
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
name|MetricsSource
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
name|MetricsRegistry
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
name|metrics2
operator|.
name|lib
operator|.
name|Interns
operator|.
name|info
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
name|javax
operator|.
name|annotation
operator|.
name|concurrent
operator|.
name|ThreadSafe
import|;
end_import

begin_comment
comment|/**  * Class to capture the performance metrics of FairScheduler.  * This should be a singleton.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|Metrics
argument_list|(
name|context
operator|=
literal|"fairscheduler-op-durations"
argument_list|)
annotation|@
name|ThreadSafe
DECL|class|FSOpDurations
specifier|public
class|class
name|FSOpDurations
implements|implements
name|MetricsSource
block|{
annotation|@
name|Metric
argument_list|(
literal|"Duration for a continuous scheduling run"
argument_list|)
DECL|field|continuousSchedulingRun
name|MutableRate
name|continuousSchedulingRun
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Duration to handle a node update"
argument_list|)
DECL|field|nodeUpdateCall
name|MutableRate
name|nodeUpdateCall
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Duration for a update thread run"
argument_list|)
DECL|field|updateThreadRun
name|MutableRate
name|updateThreadRun
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Duration for an update call"
argument_list|)
DECL|field|updateCall
name|MutableRate
name|updateCall
decl_stmt|;
annotation|@
name|Metric
argument_list|(
literal|"Duration for a preempt call"
argument_list|)
DECL|field|preemptCall
name|MutableRate
name|preemptCall
decl_stmt|;
DECL|field|RECORD_INFO
specifier|private
specifier|static
specifier|final
name|MetricsInfo
name|RECORD_INFO
init|=
name|info
argument_list|(
literal|"FSOpDurations"
argument_list|,
literal|"Durations of FairScheduler calls or thread-runs"
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|field|isExtended
specifier|private
name|boolean
name|isExtended
init|=
literal|false
decl_stmt|;
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|FSOpDurations
name|INSTANCE
init|=
operator|new
name|FSOpDurations
argument_list|()
decl_stmt|;
DECL|method|getInstance (boolean isExtended)
specifier|public
specifier|static
name|FSOpDurations
name|getInstance
parameter_list|(
name|boolean
name|isExtended
parameter_list|)
block|{
name|INSTANCE
operator|.
name|setExtended
argument_list|(
name|isExtended
argument_list|)
expr_stmt|;
return|return
name|INSTANCE
return|;
block|}
DECL|method|FSOpDurations ()
specifier|private
name|FSOpDurations
parameter_list|()
block|{
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|RECORD_INFO
argument_list|)
expr_stmt|;
name|registry
operator|.
name|tag
argument_list|(
name|RECORD_INFO
argument_list|,
literal|"FSOpDurations"
argument_list|)
expr_stmt|;
name|MetricsSystem
name|ms
init|=
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
decl_stmt|;
if|if
condition|(
name|ms
operator|!=
literal|null
condition|)
block|{
name|ms
operator|.
name|register
argument_list|(
name|RECORD_INFO
operator|.
name|name
argument_list|()
argument_list|,
name|RECORD_INFO
operator|.
name|description
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setExtended (boolean isExtended)
specifier|private
specifier|synchronized
name|void
name|setExtended
parameter_list|(
name|boolean
name|isExtended
parameter_list|)
block|{
if|if
condition|(
name|isExtended
operator|==
name|INSTANCE
operator|.
name|isExtended
condition|)
return|return;
name|continuousSchedulingRun
operator|.
name|setExtended
argument_list|(
name|isExtended
argument_list|)
expr_stmt|;
name|nodeUpdateCall
operator|.
name|setExtended
argument_list|(
name|isExtended
argument_list|)
expr_stmt|;
name|updateThreadRun
operator|.
name|setExtended
argument_list|(
name|isExtended
argument_list|)
expr_stmt|;
name|updateCall
operator|.
name|setExtended
argument_list|(
name|isExtended
argument_list|)
expr_stmt|;
name|preemptCall
operator|.
name|setExtended
argument_list|(
name|isExtended
argument_list|)
expr_stmt|;
name|INSTANCE
operator|.
name|isExtended
operator|=
name|isExtended
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMetrics (MetricsCollector collector, boolean all)
specifier|public
specifier|synchronized
name|void
name|getMetrics
parameter_list|(
name|MetricsCollector
name|collector
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|registry
operator|.
name|snapshot
argument_list|(
name|collector
operator|.
name|addRecord
argument_list|(
name|registry
operator|.
name|info
argument_list|()
argument_list|)
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
DECL|method|addContinuousSchedulingRunDuration (long value)
specifier|public
name|void
name|addContinuousSchedulingRunDuration
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|continuousSchedulingRun
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addNodeUpdateDuration (long value)
specifier|public
name|void
name|addNodeUpdateDuration
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|nodeUpdateCall
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addUpdateThreadRunDuration (long value)
specifier|public
name|void
name|addUpdateThreadRunDuration
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|updateThreadRun
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addUpdateCallDuration (long value)
specifier|public
name|void
name|addUpdateCallDuration
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|updateCall
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|addPreemptCallDuration (long value)
specifier|public
name|void
name|addPreemptCallDuration
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|preemptCall
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

