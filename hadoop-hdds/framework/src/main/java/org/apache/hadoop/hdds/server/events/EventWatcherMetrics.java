begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.server.events
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|events
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

begin_comment
comment|/**  * Metrics for any event watcher.  */
end_comment

begin_class
DECL|class|EventWatcherMetrics
specifier|public
class|class
name|EventWatcherMetrics
block|{
annotation|@
name|Metric
argument_list|()
DECL|field|trackedEvents
specifier|private
name|MutableCounterLong
name|trackedEvents
decl_stmt|;
annotation|@
name|Metric
argument_list|()
DECL|field|timedOutEvents
specifier|private
name|MutableCounterLong
name|timedOutEvents
decl_stmt|;
annotation|@
name|Metric
argument_list|()
DECL|field|completedEvents
specifier|private
name|MutableCounterLong
name|completedEvents
decl_stmt|;
annotation|@
name|Metric
argument_list|()
DECL|field|completionTime
specifier|private
name|MutableRate
name|completionTime
decl_stmt|;
DECL|method|incrementTrackedEvents ()
specifier|public
name|void
name|incrementTrackedEvents
parameter_list|()
block|{
name|trackedEvents
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrementTimedOutEvents ()
specifier|public
name|void
name|incrementTimedOutEvents
parameter_list|()
block|{
name|timedOutEvents
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
DECL|method|incrementCompletedEvents ()
specifier|public
name|void
name|incrementCompletedEvents
parameter_list|()
block|{
name|completedEvents
operator|.
name|incr
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|updateFinishingTime (long duration)
specifier|public
name|void
name|updateFinishingTime
parameter_list|(
name|long
name|duration
parameter_list|)
block|{
name|completionTime
operator|.
name|add
argument_list|(
name|duration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTrackedEvents ()
specifier|public
name|MutableCounterLong
name|getTrackedEvents
parameter_list|()
block|{
return|return
name|trackedEvents
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTimedOutEvents ()
specifier|public
name|MutableCounterLong
name|getTimedOutEvents
parameter_list|()
block|{
return|return
name|timedOutEvents
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCompletedEvents ()
specifier|public
name|MutableCounterLong
name|getCompletedEvents
parameter_list|()
block|{
return|return
name|completedEvents
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCompletionTime ()
specifier|public
name|MutableRate
name|getCompletionTime
parameter_list|()
block|{
return|return
name|completionTime
return|;
block|}
block|}
end_class

end_unit

