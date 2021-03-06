begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
package|;
end_package

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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEvent
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineMetric
import|;
end_import

begin_comment
comment|/**  * Interface for event wrapper classes.  Implementations each wrap an  * Avro-generated class, adding constructors and accessor methods.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|HistoryEvent
specifier|public
interface|interface
name|HistoryEvent
block|{
comment|/** Return this event's type. */
DECL|method|getEventType ()
name|EventType
name|getEventType
parameter_list|()
function_decl|;
comment|/** Return the Avro datum wrapped by this. */
DECL|method|getDatum ()
name|Object
name|getDatum
parameter_list|()
function_decl|;
comment|/** Set the Avro datum wrapped by this. */
DECL|method|setDatum (Object datum)
name|void
name|setDatum
parameter_list|(
name|Object
name|datum
parameter_list|)
function_decl|;
comment|/**    * Map HistoryEvent to TimelineEvent.    *    * @return the timeline event    */
DECL|method|toTimelineEvent ()
name|TimelineEvent
name|toTimelineEvent
parameter_list|()
function_decl|;
comment|/**    * Counters or Metrics if any else return null.    *    * @return the set of timeline metrics    */
DECL|method|getTimelineMetrics ()
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|getTimelineMetrics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

