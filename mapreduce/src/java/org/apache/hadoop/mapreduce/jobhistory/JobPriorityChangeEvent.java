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
name|io
operator|.
name|IOException
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
name|mapred
operator|.
name|JobPriority
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
name|mapreduce
operator|.
name|JobID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|util
operator|.
name|Utf8
import|;
end_import

begin_comment
comment|/**  * Event to record the change of priority of a job  *  */
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
DECL|class|JobPriorityChangeEvent
specifier|public
class|class
name|JobPriorityChangeEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|JobPriorityChange
name|datum
init|=
operator|new
name|JobPriorityChange
argument_list|()
decl_stmt|;
comment|/** Generate an event to record changes in Job priority    * @param id Job Id    * @param priority The new priority of the job    */
DECL|method|JobPriorityChangeEvent (JobID id, JobPriority priority)
specifier|public
name|JobPriorityChangeEvent
parameter_list|(
name|JobID
name|id
parameter_list|,
name|JobPriority
name|priority
parameter_list|)
block|{
name|datum
operator|.
name|jobid
operator|=
operator|new
name|Utf8
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|priority
operator|=
operator|new
name|Utf8
argument_list|(
name|priority
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|JobPriorityChangeEvent ()
name|JobPriorityChangeEvent
parameter_list|()
block|{ }
DECL|method|getDatum ()
specifier|public
name|Object
name|getDatum
parameter_list|()
block|{
return|return
name|datum
return|;
block|}
DECL|method|setDatum (Object datum)
specifier|public
name|void
name|setDatum
parameter_list|(
name|Object
name|datum
parameter_list|)
block|{
name|this
operator|.
name|datum
operator|=
operator|(
name|JobPriorityChange
operator|)
name|datum
expr_stmt|;
block|}
comment|/** Get the Job ID */
DECL|method|getJobId ()
specifier|public
name|JobID
name|getJobId
parameter_list|()
block|{
return|return
name|JobID
operator|.
name|forName
argument_list|(
name|datum
operator|.
name|jobid
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Get the job priority */
DECL|method|getPriority ()
specifier|public
name|JobPriority
name|getPriority
parameter_list|()
block|{
return|return
name|JobPriority
operator|.
name|valueOf
argument_list|(
name|datum
operator|.
name|priority
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Get the event type */
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
return|return
name|EventType
operator|.
name|JOB_PRIORITY_CHANGED
return|;
block|}
block|}
end_class

end_unit

