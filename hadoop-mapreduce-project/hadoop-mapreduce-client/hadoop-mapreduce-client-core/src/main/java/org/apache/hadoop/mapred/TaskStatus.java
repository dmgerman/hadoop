begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|List
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableUtils
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
name|util
operator|.
name|StringInterner
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**************************************************  * Describes the current status of a task.  This is  * not intended to be a comprehensive piece of data.  *  **************************************************/
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
DECL|class|TaskStatus
specifier|public
specifier|abstract
class|class
name|TaskStatus
implements|implements
name|Writable
implements|,
name|Cloneable
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TaskStatus
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//enumeration for reporting current phase of a task.
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|Phase
DECL|enumConstant|STARTING
DECL|enumConstant|MAP
DECL|enumConstant|SHUFFLE
DECL|enumConstant|SORT
DECL|enumConstant|REDUCE
DECL|enumConstant|CLEANUP
specifier|public
enum|enum
name|Phase
block|{
name|STARTING
block|,
name|MAP
block|,
name|SHUFFLE
block|,
name|SORT
block|,
name|REDUCE
block|,
name|CLEANUP
block|}
comment|// what state is the task in?
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|State
DECL|enumConstant|RUNNING
DECL|enumConstant|SUCCEEDED
DECL|enumConstant|FAILED
DECL|enumConstant|UNASSIGNED
DECL|enumConstant|KILLED
specifier|public
enum|enum
name|State
block|{
name|RUNNING
block|,
name|SUCCEEDED
block|,
name|FAILED
block|,
name|UNASSIGNED
block|,
name|KILLED
block|,
DECL|enumConstant|COMMIT_PENDING
DECL|enumConstant|FAILED_UNCLEAN
DECL|enumConstant|KILLED_UNCLEAN
DECL|enumConstant|PREEMPTED
name|COMMIT_PENDING
block|,
name|FAILED_UNCLEAN
block|,
name|KILLED_UNCLEAN
block|,
name|PREEMPTED
block|}
DECL|field|taskid
specifier|private
specifier|final
name|TaskAttemptID
name|taskid
decl_stmt|;
DECL|field|progress
specifier|private
name|float
name|progress
decl_stmt|;
DECL|field|runState
specifier|private
specifier|volatile
name|State
name|runState
decl_stmt|;
DECL|field|diagnosticInfo
specifier|private
name|String
name|diagnosticInfo
decl_stmt|;
DECL|field|stateString
specifier|private
name|String
name|stateString
decl_stmt|;
DECL|field|taskTracker
specifier|private
name|String
name|taskTracker
decl_stmt|;
DECL|field|numSlots
specifier|private
name|int
name|numSlots
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
comment|//in ms
DECL|field|finishTime
specifier|private
name|long
name|finishTime
decl_stmt|;
DECL|field|outputSize
specifier|private
name|long
name|outputSize
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|phase
specifier|private
specifier|volatile
name|Phase
name|phase
init|=
name|Phase
operator|.
name|STARTING
decl_stmt|;
DECL|field|counters
specifier|private
name|Counters
name|counters
decl_stmt|;
DECL|field|includeAllCounters
specifier|private
name|boolean
name|includeAllCounters
decl_stmt|;
DECL|field|nextRecordRange
specifier|private
name|SortedRanges
operator|.
name|Range
name|nextRecordRange
init|=
operator|new
name|SortedRanges
operator|.
name|Range
argument_list|()
decl_stmt|;
comment|// max task-status string size
DECL|field|MAX_STRING_SIZE
specifier|static
specifier|final
name|int
name|MAX_STRING_SIZE
init|=
literal|1024
decl_stmt|;
comment|/**    * Testcases can override {@link #getMaxStringSize()} to control the max-size     * of strings in {@link TaskStatus}. Note that the {@link TaskStatus} is never    * exposed to clients or users (i.e Map or Reduce) and hence users cannot     * override this api to pass large strings in {@link TaskStatus}.    */
DECL|method|getMaxStringSize ()
specifier|protected
name|int
name|getMaxStringSize
parameter_list|()
block|{
return|return
name|MAX_STRING_SIZE
return|;
block|}
DECL|method|TaskStatus ()
specifier|public
name|TaskStatus
parameter_list|()
block|{
name|taskid
operator|=
operator|new
name|TaskAttemptID
argument_list|()
expr_stmt|;
name|numSlots
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|TaskStatus (TaskAttemptID taskid, float progress, int numSlots, State runState, String diagnosticInfo, String stateString, String taskTracker, Phase phase, Counters counters)
specifier|public
name|TaskStatus
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|,
name|float
name|progress
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|State
name|runState
parameter_list|,
name|String
name|diagnosticInfo
parameter_list|,
name|String
name|stateString
parameter_list|,
name|String
name|taskTracker
parameter_list|,
name|Phase
name|phase
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
name|this
operator|.
name|taskid
operator|=
name|taskid
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|this
operator|.
name|numSlots
operator|=
name|numSlots
expr_stmt|;
name|this
operator|.
name|runState
operator|=
name|runState
expr_stmt|;
name|setDiagnosticInfo
argument_list|(
name|diagnosticInfo
argument_list|)
expr_stmt|;
name|setStateString
argument_list|(
name|stateString
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskTracker
operator|=
name|taskTracker
expr_stmt|;
name|this
operator|.
name|phase
operator|=
name|phase
expr_stmt|;
name|this
operator|.
name|counters
operator|=
name|counters
expr_stmt|;
name|this
operator|.
name|includeAllCounters
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getTaskID ()
specifier|public
name|TaskAttemptID
name|getTaskID
parameter_list|()
block|{
return|return
name|taskid
return|;
block|}
DECL|method|getIsMap ()
specifier|public
specifier|abstract
name|boolean
name|getIsMap
parameter_list|()
function_decl|;
DECL|method|getNumSlots ()
specifier|public
name|int
name|getNumSlots
parameter_list|()
block|{
return|return
name|numSlots
return|;
block|}
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|progress
return|;
block|}
DECL|method|setProgress (float progress)
specifier|public
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
block|}
DECL|method|getRunState ()
specifier|public
name|State
name|getRunState
parameter_list|()
block|{
return|return
name|runState
return|;
block|}
DECL|method|getTaskTracker ()
specifier|public
name|String
name|getTaskTracker
parameter_list|()
block|{
return|return
name|taskTracker
return|;
block|}
DECL|method|setTaskTracker (String tracker)
specifier|public
name|void
name|setTaskTracker
parameter_list|(
name|String
name|tracker
parameter_list|)
block|{
name|this
operator|.
name|taskTracker
operator|=
name|tracker
expr_stmt|;
block|}
DECL|method|setRunState (State runState)
specifier|public
name|void
name|setRunState
parameter_list|(
name|State
name|runState
parameter_list|)
block|{
name|this
operator|.
name|runState
operator|=
name|runState
expr_stmt|;
block|}
DECL|method|getDiagnosticInfo ()
specifier|public
name|String
name|getDiagnosticInfo
parameter_list|()
block|{
return|return
name|diagnosticInfo
return|;
block|}
DECL|method|setDiagnosticInfo (String info)
specifier|public
name|void
name|setDiagnosticInfo
parameter_list|(
name|String
name|info
parameter_list|)
block|{
comment|// if the diag-info has already reached its max then log and return
if|if
condition|(
name|diagnosticInfo
operator|!=
literal|null
operator|&&
name|diagnosticInfo
operator|.
name|length
argument_list|()
operator|==
name|getMaxStringSize
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"task-diagnostic-info for task "
operator|+
name|taskid
operator|+
literal|" : "
operator|+
name|info
argument_list|)
expr_stmt|;
return|return;
block|}
name|diagnosticInfo
operator|=
operator|(
operator|(
name|diagnosticInfo
operator|==
literal|null
operator|)
condition|?
name|info
else|:
name|diagnosticInfo
operator|.
name|concat
argument_list|(
name|info
argument_list|)
operator|)
expr_stmt|;
comment|// trim the string to MAX_STRING_SIZE if needed
if|if
condition|(
name|diagnosticInfo
operator|!=
literal|null
operator|&&
name|diagnosticInfo
operator|.
name|length
argument_list|()
operator|>
name|getMaxStringSize
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"task-diagnostic-info for task "
operator|+
name|taskid
operator|+
literal|" : "
operator|+
name|diagnosticInfo
argument_list|)
expr_stmt|;
name|diagnosticInfo
operator|=
name|diagnosticInfo
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|getMaxStringSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getStateString ()
specifier|public
name|String
name|getStateString
parameter_list|()
block|{
return|return
name|stateString
return|;
block|}
comment|/**    * Set the state of the {@link TaskStatus}.    */
DECL|method|setStateString (String stateString)
specifier|public
name|void
name|setStateString
parameter_list|(
name|String
name|stateString
parameter_list|)
block|{
if|if
condition|(
name|stateString
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|stateString
operator|.
name|length
argument_list|()
operator|<=
name|getMaxStringSize
argument_list|()
condition|)
block|{
name|this
operator|.
name|stateString
operator|=
name|stateString
expr_stmt|;
block|}
else|else
block|{
comment|// log it
name|LOG
operator|.
name|info
argument_list|(
literal|"state-string for task "
operator|+
name|taskid
operator|+
literal|" : "
operator|+
name|stateString
argument_list|)
expr_stmt|;
comment|// trim the state string
name|this
operator|.
name|stateString
operator|=
name|stateString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|getMaxStringSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get the next record range which is going to be processed by Task.    * @return nextRecordRange    */
DECL|method|getNextRecordRange ()
specifier|public
name|SortedRanges
operator|.
name|Range
name|getNextRecordRange
parameter_list|()
block|{
return|return
name|nextRecordRange
return|;
block|}
comment|/**    * Set the next record range which is going to be processed by Task.    * @param nextRecordRange    */
DECL|method|setNextRecordRange (SortedRanges.Range nextRecordRange)
specifier|public
name|void
name|setNextRecordRange
parameter_list|(
name|SortedRanges
operator|.
name|Range
name|nextRecordRange
parameter_list|)
block|{
name|this
operator|.
name|nextRecordRange
operator|=
name|nextRecordRange
expr_stmt|;
block|}
comment|/**    * Get task finish time. if shuffleFinishTime and sortFinishTime     * are not set before, these are set to finishTime. It takes care of     * the case when shuffle, sort and finish are completed with in the     * heartbeat interval and are not reported separately. if task state is     * TaskStatus.FAILED then finish time represents when the task failed.    * @return finish time of the task.     */
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|finishTime
return|;
block|}
comment|/**    * Sets finishTime for the task status if and only if the    * start time is set and passed finish time is greater than    * zero.    *     * @param finishTime finish time of task.    */
DECL|method|setFinishTime (long finishTime)
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
operator|&&
name|finishTime
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
block|}
else|else
block|{
comment|//Using String utils to get the stack trace.
name|LOG
operator|.
name|error
argument_list|(
literal|"Trying to set finish time for task "
operator|+
name|taskid
operator|+
literal|" when no start time is set, stackTrace is : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
operator|new
name|Exception
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get shuffle finish time for the task. If shuffle finish time was     * not set due to shuffle/sort/finish phases ending within same    * heartbeat interval, it is set to finish time of next phase i.e. sort     * or task finish when these are set.      * @return 0 if shuffleFinishTime, sortFinishTime and finish time are not set. else     * it returns approximate shuffle finish time.      */
DECL|method|getShuffleFinishTime ()
specifier|public
name|long
name|getShuffleFinishTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Set shuffle finish time.     * @param shuffleFinishTime     */
DECL|method|setShuffleFinishTime (long shuffleFinishTime)
name|void
name|setShuffleFinishTime
parameter_list|(
name|long
name|shuffleFinishTime
parameter_list|)
block|{}
comment|/**    * Get map phase finish time for the task. If map finsh time was    * not set due to sort phase ending within same heartbeat interval,    * it is set to finish time of next phase i.e. sort phase    * when it is set.    * @return 0 if mapFinishTime, sortFinishTime are not set. else     * it returns approximate map finish time.    */
DECL|method|getMapFinishTime ()
specifier|public
name|long
name|getMapFinishTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Set map phase finish time.     * @param mapFinishTime     */
DECL|method|setMapFinishTime (long mapFinishTime)
name|void
name|setMapFinishTime
parameter_list|(
name|long
name|mapFinishTime
parameter_list|)
block|{}
comment|/**    * Get sort finish time for the task,. If sort finish time was not set     * due to sort and reduce phase finishing in same heartebat interval, it is     * set to finish time, when finish time is set.     * @return 0 if sort finish time and finish time are not set, else returns sort    * finish time if that is set, else it returns finish time.     */
DECL|method|getSortFinishTime ()
specifier|public
name|long
name|getSortFinishTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * Sets sortFinishTime, if shuffleFinishTime is not set before     * then its set to sortFinishTime.      * @param sortFinishTime    */
DECL|method|setSortFinishTime (long sortFinishTime)
name|void
name|setSortFinishTime
parameter_list|(
name|long
name|sortFinishTime
parameter_list|)
block|{}
comment|/**    * Get start time of the task.     * @return 0 is start time is not set, else returns start time.     */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
comment|/**    * Set startTime of the task if start time is greater than zero.    * @param startTime start time    */
DECL|method|setStartTime (long startTime)
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
comment|//Making the assumption of passed startTime to be a positive
comment|//long value explicit.
if|if
condition|(
name|startTime
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
else|else
block|{
comment|//Using String utils to get the stack trace.
name|LOG
operator|.
name|error
argument_list|(
literal|"Trying to set illegal startTime for task : "
operator|+
name|taskid
operator|+
literal|".Stack trace is : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
operator|new
name|Exception
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get current phase of this task. Phase.Map in case of map tasks,     * for reduce one of Phase.SHUFFLE, Phase.SORT or Phase.REDUCE.     * @return .     */
DECL|method|getPhase ()
specifier|public
name|Phase
name|getPhase
parameter_list|()
block|{
return|return
name|this
operator|.
name|phase
return|;
block|}
comment|/**    * Set current phase of this task.      * @param phase phase of this task    */
DECL|method|setPhase (Phase phase)
specifier|public
name|void
name|setPhase
parameter_list|(
name|Phase
name|phase
parameter_list|)
block|{
name|TaskStatus
operator|.
name|Phase
name|oldPhase
init|=
name|getPhase
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldPhase
operator|!=
name|phase
condition|)
block|{
comment|// sort phase started
if|if
condition|(
name|phase
operator|==
name|TaskStatus
operator|.
name|Phase
operator|.
name|SORT
condition|)
block|{
if|if
condition|(
name|oldPhase
operator|==
name|TaskStatus
operator|.
name|Phase
operator|.
name|MAP
condition|)
block|{
name|setMapFinishTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setShuffleFinishTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|phase
operator|==
name|TaskStatus
operator|.
name|Phase
operator|.
name|REDUCE
condition|)
block|{
name|setSortFinishTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|phase
operator|=
name|phase
expr_stmt|;
block|}
block|}
DECL|method|inTaskCleanupPhase ()
name|boolean
name|inTaskCleanupPhase
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|phase
operator|==
name|TaskStatus
operator|.
name|Phase
operator|.
name|CLEANUP
operator|&&
operator|(
name|this
operator|.
name|runState
operator|==
name|TaskStatus
operator|.
name|State
operator|.
name|FAILED_UNCLEAN
operator|||
name|this
operator|.
name|runState
operator|==
name|TaskStatus
operator|.
name|State
operator|.
name|KILLED_UNCLEAN
operator|)
operator|)
return|;
block|}
DECL|method|getIncludeAllCounters ()
specifier|public
name|boolean
name|getIncludeAllCounters
parameter_list|()
block|{
return|return
name|includeAllCounters
return|;
block|}
DECL|method|setIncludeAllCounters (boolean send)
specifier|public
name|void
name|setIncludeAllCounters
parameter_list|(
name|boolean
name|send
parameter_list|)
block|{
name|includeAllCounters
operator|=
name|send
expr_stmt|;
name|counters
operator|.
name|setWriteAllCounters
argument_list|(
name|send
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get task's counters.    */
DECL|method|getCounters ()
specifier|public
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|counters
return|;
block|}
comment|/**    * Set the task's counters.    * @param counters    */
DECL|method|setCounters (Counters counters)
specifier|public
name|void
name|setCounters
parameter_list|(
name|Counters
name|counters
parameter_list|)
block|{
name|this
operator|.
name|counters
operator|=
name|counters
expr_stmt|;
block|}
comment|/**    * Returns the number of bytes of output from this map.    */
DECL|method|getOutputSize ()
specifier|public
name|long
name|getOutputSize
parameter_list|()
block|{
return|return
name|outputSize
return|;
block|}
comment|/**    * Set the size on disk of this task's output.    * @param l the number of map output bytes    */
DECL|method|setOutputSize (long l)
name|void
name|setOutputSize
parameter_list|(
name|long
name|l
parameter_list|)
block|{
name|outputSize
operator|=
name|l
expr_stmt|;
block|}
comment|/**    * Get the list of maps from which output-fetches failed.    *     * @return the list of maps from which output-fetches failed.    */
DECL|method|getFetchFailedMaps ()
specifier|public
name|List
argument_list|<
name|TaskAttemptID
argument_list|>
name|getFetchFailedMaps
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Add to the list of maps from which output-fetches failed.    *      * @param mapTaskId map from which fetch failed    */
DECL|method|addFetchFailedMap (TaskAttemptID mapTaskId)
specifier|public
specifier|abstract
name|void
name|addFetchFailedMap
parameter_list|(
name|TaskAttemptID
name|mapTaskId
parameter_list|)
function_decl|;
comment|/**    * Update the status of the task.    *     * This update is done by ping thread before sending the status.     *     * @param progress    * @param state    * @param counters    */
DECL|method|statusUpdate (float progress, String state, Counters counters)
specifier|synchronized
name|void
name|statusUpdate
parameter_list|(
name|float
name|progress
parameter_list|,
name|String
name|state
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
name|setProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|setStateString
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|setCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the status of the task.    *     * @param status updated status    */
DECL|method|statusUpdate (TaskStatus status)
specifier|synchronized
name|void
name|statusUpdate
parameter_list|(
name|TaskStatus
name|status
parameter_list|)
block|{
name|setProgress
argument_list|(
name|status
operator|.
name|getProgress
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|runState
operator|=
name|status
operator|.
name|getRunState
argument_list|()
expr_stmt|;
name|setStateString
argument_list|(
name|status
operator|.
name|getStateString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|nextRecordRange
operator|=
name|status
operator|.
name|getNextRecordRange
argument_list|()
expr_stmt|;
name|setDiagnosticInfo
argument_list|(
name|status
operator|.
name|getDiagnosticInfo
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|setStartTime
argument_list|(
name|status
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|getFinishTime
argument_list|()
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|setFinishTime
argument_list|(
name|status
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|phase
operator|=
name|status
operator|.
name|getPhase
argument_list|()
expr_stmt|;
name|this
operator|.
name|counters
operator|=
name|status
operator|.
name|getCounters
argument_list|()
expr_stmt|;
name|this
operator|.
name|outputSize
operator|=
name|status
operator|.
name|outputSize
expr_stmt|;
block|}
comment|/**    * Update specific fields of task status    *     * This update is done in JobTracker when a cleanup attempt of task    * reports its status. Then update only specific fields, not all.    *     * @param runState    * @param progress    * @param state    * @param phase    * @param finishTime    */
DECL|method|statusUpdate (State runState, float progress, String state, Phase phase, long finishTime)
specifier|synchronized
name|void
name|statusUpdate
parameter_list|(
name|State
name|runState
parameter_list|,
name|float
name|progress
parameter_list|,
name|String
name|state
parameter_list|,
name|Phase
name|phase
parameter_list|,
name|long
name|finishTime
parameter_list|)
block|{
name|setRunState
argument_list|(
name|runState
argument_list|)
expr_stmt|;
name|setProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|setStateString
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|setPhase
argument_list|(
name|phase
argument_list|)
expr_stmt|;
if|if
condition|(
name|finishTime
operator|>
literal|0
condition|)
block|{
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Clear out transient information after sending out a status-update    * from either the {@link Task} to the {@link TaskTracker} or from the    * {@link TaskTracker} to the {@link JobTracker}.     */
DECL|method|clearStatus ()
specifier|synchronized
name|void
name|clearStatus
parameter_list|()
block|{
comment|// Clear diagnosticInfo
name|diagnosticInfo
operator|=
literal|""
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone ()
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|cnse
parameter_list|)
block|{
comment|// Shouldn't happen since we do implement Clonable
throw|throw
operator|new
name|InternalError
argument_list|(
name|cnse
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|//////////////////////////////////////////////
comment|// Writable
comment|//////////////////////////////////////////////
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|taskid
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numSlots
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|runState
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|diagnosticInfo
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|stateString
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|phase
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|includeAllCounters
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|outputSize
argument_list|)
expr_stmt|;
name|counters
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|nextRecordRange
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|taskid
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|setProgress
argument_list|(
name|in
operator|.
name|readFloat
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|numSlots
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|runState
operator|=
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|State
operator|.
name|class
argument_list|)
expr_stmt|;
name|setDiagnosticInfo
argument_list|(
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|setStateString
argument_list|(
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|phase
operator|=
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|Phase
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|counters
operator|=
operator|new
name|Counters
argument_list|()
expr_stmt|;
name|this
operator|.
name|includeAllCounters
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|outputSize
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|counters
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|nextRecordRange
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|//////////////////////////////////////////////////////////////////////////////
comment|// Factory-like methods to create/read/write appropriate TaskStatus objects
comment|//////////////////////////////////////////////////////////////////////////////
DECL|method|createTaskStatus (DataInput in, TaskAttemptID taskId, float progress, int numSlots, State runState, String diagnosticInfo, String stateString, String taskTracker, Phase phase, Counters counters)
specifier|static
name|TaskStatus
name|createTaskStatus
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|TaskAttemptID
name|taskId
parameter_list|,
name|float
name|progress
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|State
name|runState
parameter_list|,
name|String
name|diagnosticInfo
parameter_list|,
name|String
name|stateString
parameter_list|,
name|String
name|taskTracker
parameter_list|,
name|Phase
name|phase
parameter_list|,
name|Counters
name|counters
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isMap
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
return|return
name|createTaskStatus
argument_list|(
name|isMap
argument_list|,
name|taskId
argument_list|,
name|progress
argument_list|,
name|numSlots
argument_list|,
name|runState
argument_list|,
name|diagnosticInfo
argument_list|,
name|stateString
argument_list|,
name|taskTracker
argument_list|,
name|phase
argument_list|,
name|counters
argument_list|)
return|;
block|}
DECL|method|createTaskStatus (boolean isMap, TaskAttemptID taskId, float progress, int numSlots, State runState, String diagnosticInfo, String stateString, String taskTracker, Phase phase, Counters counters)
specifier|static
name|TaskStatus
name|createTaskStatus
parameter_list|(
name|boolean
name|isMap
parameter_list|,
name|TaskAttemptID
name|taskId
parameter_list|,
name|float
name|progress
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|State
name|runState
parameter_list|,
name|String
name|diagnosticInfo
parameter_list|,
name|String
name|stateString
parameter_list|,
name|String
name|taskTracker
parameter_list|,
name|Phase
name|phase
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
return|return
operator|(
name|isMap
operator|)
condition|?
operator|new
name|MapTaskStatus
argument_list|(
name|taskId
argument_list|,
name|progress
argument_list|,
name|numSlots
argument_list|,
name|runState
argument_list|,
name|diagnosticInfo
argument_list|,
name|stateString
argument_list|,
name|taskTracker
argument_list|,
name|phase
argument_list|,
name|counters
argument_list|)
else|:
operator|new
name|ReduceTaskStatus
argument_list|(
name|taskId
argument_list|,
name|progress
argument_list|,
name|numSlots
argument_list|,
name|runState
argument_list|,
name|diagnosticInfo
argument_list|,
name|stateString
argument_list|,
name|taskTracker
argument_list|,
name|phase
argument_list|,
name|counters
argument_list|)
return|;
block|}
DECL|method|createTaskStatus (boolean isMap)
specifier|static
name|TaskStatus
name|createTaskStatus
parameter_list|(
name|boolean
name|isMap
parameter_list|)
block|{
return|return
operator|(
name|isMap
operator|)
condition|?
operator|new
name|MapTaskStatus
argument_list|()
else|:
operator|new
name|ReduceTaskStatus
argument_list|()
return|;
block|}
block|}
end_class

end_unit

