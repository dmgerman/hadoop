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
name|ProgressSplitsBlock
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
name|Counters
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
name|TaskAttemptID
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
name|TaskID
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
name|TaskType
import|;
end_import

begin_comment
comment|/**  * Event to record successful completion of a reduce attempt  *  */
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
DECL|class|ReduceAttemptFinishedEvent
specifier|public
class|class
name|ReduceAttemptFinishedEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|ReduceAttemptFinished
name|datum
init|=
operator|new
name|ReduceAttemptFinished
argument_list|()
decl_stmt|;
comment|/**    * Create an event to record completion of a reduce attempt    * @param id Attempt Id    * @param taskType Type of task    * @param taskStatus Status of the task    * @param shuffleFinishTime Finish time of the shuffle phase    * @param sortFinishTime Finish time of the sort phase    * @param finishTime Finish time of the attempt    * @param hostname Name of the host where the attempt executed    * @param port RPC port for the tracker host.    * @param rackName Name of the rack where the attempt executed    * @param state State of the attempt    * @param counters Counters for the attempt    * @param allSplits the "splits", or a pixelated graph of various    *        measurable worker node state variables against progress.    *        Currently there are four; wallclock time, CPU time,    *        virtual memory and physical memory.      */
DECL|method|ReduceAttemptFinishedEvent (TaskAttemptID id, TaskType taskType, String taskStatus, long shuffleFinishTime, long sortFinishTime, long finishTime, String hostname, int port, String rackName, String state, Counters counters, int[][] allSplits)
specifier|public
name|ReduceAttemptFinishedEvent
parameter_list|(
name|TaskAttemptID
name|id
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|taskStatus
parameter_list|,
name|long
name|shuffleFinishTime
parameter_list|,
name|long
name|sortFinishTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|rackName
parameter_list|,
name|String
name|state
parameter_list|,
name|Counters
name|counters
parameter_list|,
name|int
index|[]
index|[]
name|allSplits
parameter_list|)
block|{
name|datum
operator|.
name|taskid
operator|=
operator|new
name|Utf8
argument_list|(
name|id
operator|.
name|getTaskID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|attemptId
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
name|taskType
operator|=
operator|new
name|Utf8
argument_list|(
name|taskType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|taskStatus
operator|=
operator|new
name|Utf8
argument_list|(
name|taskStatus
argument_list|)
expr_stmt|;
name|datum
operator|.
name|shuffleFinishTime
operator|=
name|shuffleFinishTime
expr_stmt|;
name|datum
operator|.
name|sortFinishTime
operator|=
name|sortFinishTime
expr_stmt|;
name|datum
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
name|datum
operator|.
name|hostname
operator|=
operator|new
name|Utf8
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
name|datum
operator|.
name|port
operator|=
name|port
expr_stmt|;
if|if
condition|(
name|rackName
operator|!=
literal|null
condition|)
block|{
name|datum
operator|.
name|rackname
operator|=
operator|new
name|Utf8
argument_list|(
name|rackName
argument_list|)
expr_stmt|;
block|}
name|datum
operator|.
name|state
operator|=
operator|new
name|Utf8
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|datum
operator|.
name|counters
operator|=
name|EventWriter
operator|.
name|toAvro
argument_list|(
name|counters
argument_list|)
expr_stmt|;
name|datum
operator|.
name|clockSplits
operator|=
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetWallclockTime
argument_list|(
name|allSplits
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|cpuUsages
operator|=
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetCPUTime
argument_list|(
name|allSplits
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|vMemKbytes
operator|=
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetVMemKbytes
argument_list|(
name|allSplits
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|physMemKbytes
operator|=
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetPhysMemKbytes
argument_list|(
name|allSplits
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated please use the constructor with an additional    *              argument, an array of splits arrays instead.  See    *              {@link org.apache.hadoop.mapred.ProgressSplitsBlock}    *              for an explanation of the meaning of that parameter.    *    * Create an event to record completion of a reduce attempt    * @param id Attempt Id    * @param taskType Type of task    * @param taskStatus Status of the task    * @param shuffleFinishTime Finish time of the shuffle phase    * @param sortFinishTime Finish time of the sort phase    * @param finishTime Finish time of the attempt    * @param hostname Name of the host where the attempt executed    * @param state State of the attempt    * @param counters Counters for the attempt    */
DECL|method|ReduceAttemptFinishedEvent (TaskAttemptID id, TaskType taskType, String taskStatus, long shuffleFinishTime, long sortFinishTime, long finishTime, String hostname, String state, Counters counters)
specifier|public
name|ReduceAttemptFinishedEvent
parameter_list|(
name|TaskAttemptID
name|id
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|taskStatus
parameter_list|,
name|long
name|shuffleFinishTime
parameter_list|,
name|long
name|sortFinishTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|String
name|state
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
name|this
argument_list|(
name|id
argument_list|,
name|taskType
argument_list|,
name|taskStatus
argument_list|,
name|shuffleFinishTime
argument_list|,
name|sortFinishTime
argument_list|,
name|finishTime
argument_list|,
name|hostname
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|state
argument_list|,
name|counters
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ReduceAttemptFinishedEvent ()
name|ReduceAttemptFinishedEvent
parameter_list|()
block|{}
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
name|ReduceAttemptFinished
operator|)
name|datum
expr_stmt|;
block|}
comment|/** Get the Task ID */
DECL|method|getTaskId ()
specifier|public
name|TaskID
name|getTaskId
parameter_list|()
block|{
return|return
name|TaskID
operator|.
name|forName
argument_list|(
name|datum
operator|.
name|taskid
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Get the attempt id */
DECL|method|getAttemptId ()
specifier|public
name|TaskAttemptID
name|getAttemptId
parameter_list|()
block|{
return|return
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|datum
operator|.
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Get the task type */
DECL|method|getTaskType ()
specifier|public
name|TaskType
name|getTaskType
parameter_list|()
block|{
return|return
name|TaskType
operator|.
name|valueOf
argument_list|(
name|datum
operator|.
name|taskType
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Get the task status */
DECL|method|getTaskStatus ()
specifier|public
name|String
name|getTaskStatus
parameter_list|()
block|{
return|return
name|datum
operator|.
name|taskStatus
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Get the finish time of the sort phase */
DECL|method|getSortFinishTime ()
specifier|public
name|long
name|getSortFinishTime
parameter_list|()
block|{
return|return
name|datum
operator|.
name|sortFinishTime
return|;
block|}
comment|/** Get the finish time of the shuffle phase */
DECL|method|getShuffleFinishTime ()
specifier|public
name|long
name|getShuffleFinishTime
parameter_list|()
block|{
return|return
name|datum
operator|.
name|shuffleFinishTime
return|;
block|}
comment|/** Get the finish time of the attempt */
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|datum
operator|.
name|finishTime
return|;
block|}
comment|/** Get the name of the host where the attempt ran */
DECL|method|getHostname ()
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|datum
operator|.
name|hostname
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Get the tracker rpc port */
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|datum
operator|.
name|port
return|;
block|}
comment|/** Get the rack name of the node where the attempt ran */
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
return|return
name|datum
operator|.
name|rackname
operator|==
literal|null
condition|?
literal|null
else|:
name|datum
operator|.
name|rackname
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Get the state string */
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|datum
operator|.
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Get the counters for the attempt */
DECL|method|getCounters ()
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|EventReader
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|counters
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
name|REDUCE_ATTEMPT_FINISHED
return|;
block|}
DECL|method|getClockSplits ()
specifier|public
name|int
index|[]
name|getClockSplits
parameter_list|()
block|{
return|return
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|clockSplits
argument_list|)
return|;
block|}
DECL|method|getCpuUsages ()
specifier|public
name|int
index|[]
name|getCpuUsages
parameter_list|()
block|{
return|return
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|cpuUsages
argument_list|)
return|;
block|}
DECL|method|getVMemKbytes ()
specifier|public
name|int
index|[]
name|getVMemKbytes
parameter_list|()
block|{
return|return
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|vMemKbytes
argument_list|)
return|;
block|}
DECL|method|getPhysMemKbytes ()
specifier|public
name|int
index|[]
name|getPhysMemKbytes
parameter_list|()
block|{
return|return
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|physMemKbytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

