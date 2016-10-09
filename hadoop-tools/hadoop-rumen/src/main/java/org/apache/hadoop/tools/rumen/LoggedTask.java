begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

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
name|Collections
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
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonAnySetter
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
name|jobhistory
operator|.
name|JhCounter
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
name|jobhistory
operator|.
name|JhCounterGroup
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
name|jobhistory
operator|.
name|JhCounters
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

begin_comment
comment|/**  * A {@link LoggedTask} represents a [hadoop] task that is part of a hadoop job.  * It knows about the [pssibly empty] sequence of attempts, its I/O footprint,  * and its runtime.  *   * All of the public methods are simply accessors for the instance variables we  * want to write out in the JSON files.  *   */
end_comment

begin_class
DECL|class|LoggedTask
specifier|public
class|class
name|LoggedTask
implements|implements
name|DeepCompare
block|{
DECL|field|inputBytes
name|long
name|inputBytes
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|inputRecords
name|long
name|inputRecords
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|outputBytes
name|long
name|outputBytes
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|outputRecords
name|long
name|outputRecords
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|taskID
name|TaskID
name|taskID
decl_stmt|;
DECL|field|startTime
name|long
name|startTime
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|finishTime
name|long
name|finishTime
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|taskType
name|Pre21JobHistoryConstants
operator|.
name|Values
name|taskType
decl_stmt|;
DECL|field|taskStatus
name|Pre21JobHistoryConstants
operator|.
name|Values
name|taskStatus
decl_stmt|;
DECL|field|attempts
name|List
argument_list|<
name|LoggedTaskAttempt
argument_list|>
name|attempts
init|=
operator|new
name|ArrayList
argument_list|<
name|LoggedTaskAttempt
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|preferredLocations
name|List
argument_list|<
name|LoggedLocation
argument_list|>
name|preferredLocations
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
DECL|field|alreadySeenAnySetterAttributes
specifier|static
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|alreadySeenAnySetterAttributes
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// for input parameter ignored.
annotation|@
name|JsonAnySetter
DECL|method|setUnknownAttribute (String attributeName, Object ignored)
specifier|public
name|void
name|setUnknownAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|,
name|Object
name|ignored
parameter_list|)
block|{
if|if
condition|(
operator|!
name|alreadySeenAnySetterAttributes
operator|.
name|contains
argument_list|(
name|attributeName
argument_list|)
condition|)
block|{
name|alreadySeenAnySetterAttributes
operator|.
name|add
argument_list|(
name|attributeName
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"In LoggedJob, we saw the unknown attribute "
operator|+
name|attributeName
operator|+
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|LoggedTask ()
name|LoggedTask
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|adjustTimes (long adjustment)
name|void
name|adjustTimes
parameter_list|(
name|long
name|adjustment
parameter_list|)
block|{
name|startTime
operator|+=
name|adjustment
expr_stmt|;
name|finishTime
operator|+=
name|adjustment
expr_stmt|;
for|for
control|(
name|LoggedTaskAttempt
name|attempt
range|:
name|attempts
control|)
block|{
name|attempt
operator|.
name|adjustTimes
argument_list|(
name|adjustment
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getInputBytes ()
specifier|public
name|long
name|getInputBytes
parameter_list|()
block|{
return|return
name|inputBytes
return|;
block|}
DECL|method|setInputBytes (long inputBytes)
name|void
name|setInputBytes
parameter_list|(
name|long
name|inputBytes
parameter_list|)
block|{
name|this
operator|.
name|inputBytes
operator|=
name|inputBytes
expr_stmt|;
block|}
DECL|method|getInputRecords ()
specifier|public
name|long
name|getInputRecords
parameter_list|()
block|{
return|return
name|inputRecords
return|;
block|}
DECL|method|setInputRecords (long inputRecords)
name|void
name|setInputRecords
parameter_list|(
name|long
name|inputRecords
parameter_list|)
block|{
name|this
operator|.
name|inputRecords
operator|=
name|inputRecords
expr_stmt|;
block|}
DECL|method|getOutputBytes ()
specifier|public
name|long
name|getOutputBytes
parameter_list|()
block|{
return|return
name|outputBytes
return|;
block|}
DECL|method|setOutputBytes (long outputBytes)
name|void
name|setOutputBytes
parameter_list|(
name|long
name|outputBytes
parameter_list|)
block|{
name|this
operator|.
name|outputBytes
operator|=
name|outputBytes
expr_stmt|;
block|}
DECL|method|getOutputRecords ()
specifier|public
name|long
name|getOutputRecords
parameter_list|()
block|{
return|return
name|outputRecords
return|;
block|}
DECL|method|setOutputRecords (long outputRecords)
name|void
name|setOutputRecords
parameter_list|(
name|long
name|outputRecords
parameter_list|)
block|{
name|this
operator|.
name|outputRecords
operator|=
name|outputRecords
expr_stmt|;
block|}
DECL|method|getTaskID ()
specifier|public
name|TaskID
name|getTaskID
parameter_list|()
block|{
return|return
name|taskID
return|;
block|}
DECL|method|setTaskID (String taskID)
name|void
name|setTaskID
parameter_list|(
name|String
name|taskID
parameter_list|)
block|{
name|this
operator|.
name|taskID
operator|=
name|TaskID
operator|.
name|forName
argument_list|(
name|taskID
argument_list|)
expr_stmt|;
block|}
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
DECL|method|setStartTime (long startTime)
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
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
DECL|method|setFinishTime (long finishTime)
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
block|{
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
block|}
DECL|method|getAttempts ()
specifier|public
name|List
argument_list|<
name|LoggedTaskAttempt
argument_list|>
name|getAttempts
parameter_list|()
block|{
return|return
name|attempts
return|;
block|}
DECL|method|setAttempts (List<LoggedTaskAttempt> attempts)
name|void
name|setAttempts
parameter_list|(
name|List
argument_list|<
name|LoggedTaskAttempt
argument_list|>
name|attempts
parameter_list|)
block|{
if|if
condition|(
name|attempts
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|attempts
operator|=
operator|new
name|ArrayList
argument_list|<
name|LoggedTaskAttempt
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|attempts
operator|=
name|attempts
expr_stmt|;
block|}
block|}
DECL|method|getPreferredLocations ()
specifier|public
name|List
argument_list|<
name|LoggedLocation
argument_list|>
name|getPreferredLocations
parameter_list|()
block|{
return|return
name|preferredLocations
return|;
block|}
DECL|method|setPreferredLocations (List<LoggedLocation> preferredLocations)
name|void
name|setPreferredLocations
parameter_list|(
name|List
argument_list|<
name|LoggedLocation
argument_list|>
name|preferredLocations
parameter_list|)
block|{
if|if
condition|(
name|preferredLocations
operator|==
literal|null
operator|||
name|preferredLocations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|preferredLocations
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|preferredLocations
operator|=
name|preferredLocations
expr_stmt|;
block|}
block|}
DECL|method|getTaskStatus ()
specifier|public
name|Pre21JobHistoryConstants
operator|.
name|Values
name|getTaskStatus
parameter_list|()
block|{
return|return
name|taskStatus
return|;
block|}
DECL|method|setTaskStatus (Pre21JobHistoryConstants.Values taskStatus)
name|void
name|setTaskStatus
parameter_list|(
name|Pre21JobHistoryConstants
operator|.
name|Values
name|taskStatus
parameter_list|)
block|{
name|this
operator|.
name|taskStatus
operator|=
name|taskStatus
expr_stmt|;
block|}
DECL|method|getTaskType ()
specifier|public
name|Pre21JobHistoryConstants
operator|.
name|Values
name|getTaskType
parameter_list|()
block|{
return|return
name|taskType
return|;
block|}
DECL|method|setTaskType (Pre21JobHistoryConstants.Values taskType)
name|void
name|setTaskType
parameter_list|(
name|Pre21JobHistoryConstants
operator|.
name|Values
name|taskType
parameter_list|)
block|{
name|this
operator|.
name|taskType
operator|=
name|taskType
expr_stmt|;
block|}
DECL|method|incorporateMapCounters (JhCounters counters)
specifier|private
name|void
name|incorporateMapCounters
parameter_list|(
name|JhCounters
name|counters
parameter_list|)
block|{
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|inputBytes
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"HDFS_BYTES_READ"
argument_list|)
expr_stmt|;
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|outputBytes
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"FILE_BYTES_WRITTEN"
argument_list|)
expr_stmt|;
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|inputRecords
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"MAP_INPUT_RECORDS"
argument_list|)
expr_stmt|;
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|outputRecords
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"MAP_OUTPUT_RECORDS"
argument_list|)
expr_stmt|;
block|}
DECL|method|incorporateReduceCounters (JhCounters counters)
specifier|private
name|void
name|incorporateReduceCounters
parameter_list|(
name|JhCounters
name|counters
parameter_list|)
block|{
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|inputBytes
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"REDUCE_SHUFFLE_BYTES"
argument_list|)
expr_stmt|;
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|outputBytes
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"HDFS_BYTES_WRITTEN"
argument_list|)
expr_stmt|;
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|inputRecords
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"REDUCE_INPUT_RECORDS"
argument_list|)
expr_stmt|;
name|incorporateCounter
argument_list|(
operator|new
name|SetField
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
name|void
name|set
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|task
operator|.
name|outputRecords
operator|=
name|val
expr_stmt|;
block|}
block|}
argument_list|,
name|counters
argument_list|,
literal|"REDUCE_OUTPUT_RECORDS"
argument_list|)
expr_stmt|;
block|}
comment|// incorporate event counters
comment|// LoggedTask MUST KNOW ITS TYPE BEFORE THIS CALL
DECL|method|incorporateCounters (JhCounters counters)
specifier|public
name|void
name|incorporateCounters
parameter_list|(
name|JhCounters
name|counters
parameter_list|)
block|{
switch|switch
condition|(
name|taskType
condition|)
block|{
case|case
name|MAP
case|:
name|incorporateMapCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
return|return;
case|case
name|REDUCE
case|:
name|incorporateReduceCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
return|return;
comment|// NOT exhaustive
block|}
block|}
DECL|method|canonicalizeCounterName (String nonCanonicalName)
specifier|private
specifier|static
name|String
name|canonicalizeCounterName
parameter_list|(
name|String
name|nonCanonicalName
parameter_list|)
block|{
name|String
name|result
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|nonCanonicalName
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'|'
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|'-'
argument_list|,
literal|'|'
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|'_'
argument_list|,
literal|'|'
argument_list|)
expr_stmt|;
name|result
operator|=
name|result
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'|'
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|SetField
specifier|private
specifier|abstract
class|class
name|SetField
block|{
DECL|field|task
name|LoggedTask
name|task
decl_stmt|;
DECL|method|SetField (LoggedTask task)
name|SetField
parameter_list|(
name|LoggedTask
name|task
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
block|}
DECL|method|set (long value)
specifier|abstract
name|void
name|set
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
block|}
DECL|method|incorporateCounter (SetField thunk, JhCounters counters, String counterName)
specifier|private
specifier|static
name|void
name|incorporateCounter
parameter_list|(
name|SetField
name|thunk
parameter_list|,
name|JhCounters
name|counters
parameter_list|,
name|String
name|counterName
parameter_list|)
block|{
name|counterName
operator|=
name|canonicalizeCounterName
argument_list|(
name|counterName
argument_list|)
expr_stmt|;
for|for
control|(
name|JhCounterGroup
name|group
range|:
name|counters
operator|.
name|groups
control|)
block|{
for|for
control|(
name|JhCounter
name|counter
range|:
name|group
operator|.
name|counts
control|)
block|{
if|if
condition|(
name|counterName
operator|.
name|equals
argument_list|(
name|canonicalizeCounterName
argument_list|(
name|counter
operator|.
name|name
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|thunk
operator|.
name|set
argument_list|(
name|counter
operator|.
name|value
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
DECL|method|compare1 (long c1, long c2, TreePath loc, String eltname)
specifier|private
name|void
name|compare1
parameter_list|(
name|long
name|c1
parameter_list|,
name|long
name|c2
parameter_list|,
name|TreePath
name|loc
parameter_list|,
name|String
name|eltname
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
name|c1
operator|!=
name|c2
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
name|eltname
operator|+
literal|" miscompared"
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|compare1 (String c1, String c2, TreePath loc, String eltname)
specifier|private
name|void
name|compare1
parameter_list|(
name|String
name|c1
parameter_list|,
name|String
name|c2
parameter_list|,
name|TreePath
name|loc
parameter_list|,
name|String
name|eltname
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
name|c1
operator|==
literal|null
operator|&&
name|c2
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|c1
operator|==
literal|null
operator|||
name|c2
operator|==
literal|null
operator|||
operator|!
name|c1
operator|.
name|equals
argument_list|(
name|c2
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
name|eltname
operator|+
literal|" miscompared"
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|compare1 (Pre21JobHistoryConstants.Values c1, Pre21JobHistoryConstants.Values c2, TreePath loc, String eltname)
specifier|private
name|void
name|compare1
parameter_list|(
name|Pre21JobHistoryConstants
operator|.
name|Values
name|c1
parameter_list|,
name|Pre21JobHistoryConstants
operator|.
name|Values
name|c2
parameter_list|,
name|TreePath
name|loc
parameter_list|,
name|String
name|eltname
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
name|c1
operator|==
literal|null
operator|&&
name|c2
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|c1
operator|==
literal|null
operator|||
name|c2
operator|==
literal|null
operator|||
operator|!
name|c1
operator|.
name|equals
argument_list|(
name|c2
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
name|eltname
operator|+
literal|" miscompared"
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|compareLoggedLocations (List<LoggedLocation> c1, List<LoggedLocation> c2, TreePath loc, String eltname)
specifier|private
name|void
name|compareLoggedLocations
parameter_list|(
name|List
argument_list|<
name|LoggedLocation
argument_list|>
name|c1
parameter_list|,
name|List
argument_list|<
name|LoggedLocation
argument_list|>
name|c2
parameter_list|,
name|TreePath
name|loc
parameter_list|,
name|String
name|eltname
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
name|c1
operator|==
literal|null
operator|&&
name|c2
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|c1
operator|==
literal|null
operator|||
name|c2
operator|==
literal|null
operator|||
name|c1
operator|.
name|size
argument_list|()
operator|!=
name|c2
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
name|eltname
operator|+
literal|" miscompared"
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|)
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|c1
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|c1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|deepCompare
argument_list|(
name|c2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compareLoggedTaskAttempts (List<LoggedTaskAttempt> c1, List<LoggedTaskAttempt> c2, TreePath loc, String eltname)
specifier|private
name|void
name|compareLoggedTaskAttempts
parameter_list|(
name|List
argument_list|<
name|LoggedTaskAttempt
argument_list|>
name|c1
parameter_list|,
name|List
argument_list|<
name|LoggedTaskAttempt
argument_list|>
name|c2
parameter_list|,
name|TreePath
name|loc
parameter_list|,
name|String
name|eltname
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
name|c1
operator|==
literal|null
operator|&&
name|c2
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|c1
operator|==
literal|null
operator|||
name|c2
operator|==
literal|null
operator|||
name|c1
operator|.
name|size
argument_list|()
operator|!=
name|c2
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
name|eltname
operator|+
literal|" miscompared"
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|)
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|c1
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|c1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|deepCompare
argument_list|(
name|c2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|TreePath
argument_list|(
name|loc
argument_list|,
name|eltname
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deepCompare (DeepCompare comparand, TreePath loc)
specifier|public
name|void
name|deepCompare
parameter_list|(
name|DeepCompare
name|comparand
parameter_list|,
name|TreePath
name|loc
parameter_list|)
throws|throws
name|DeepInequalityException
block|{
if|if
condition|(
operator|!
operator|(
name|comparand
operator|instanceof
name|LoggedTask
operator|)
condition|)
block|{
throw|throw
operator|new
name|DeepInequalityException
argument_list|(
literal|"comparand has wrong type"
argument_list|,
name|loc
argument_list|)
throw|;
block|}
name|LoggedTask
name|other
init|=
operator|(
name|LoggedTask
operator|)
name|comparand
decl_stmt|;
name|compare1
argument_list|(
name|inputBytes
argument_list|,
name|other
operator|.
name|inputBytes
argument_list|,
name|loc
argument_list|,
literal|"inputBytes"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|inputRecords
argument_list|,
name|other
operator|.
name|inputRecords
argument_list|,
name|loc
argument_list|,
literal|"inputRecords"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|outputBytes
argument_list|,
name|other
operator|.
name|outputBytes
argument_list|,
name|loc
argument_list|,
literal|"outputBytes"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|outputRecords
argument_list|,
name|other
operator|.
name|outputRecords
argument_list|,
name|loc
argument_list|,
literal|"outputRecords"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|taskID
operator|.
name|toString
argument_list|()
argument_list|,
name|other
operator|.
name|taskID
operator|.
name|toString
argument_list|()
argument_list|,
name|loc
argument_list|,
literal|"taskID"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|startTime
argument_list|,
name|other
operator|.
name|startTime
argument_list|,
name|loc
argument_list|,
literal|"startTime"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|finishTime
argument_list|,
name|other
operator|.
name|finishTime
argument_list|,
name|loc
argument_list|,
literal|"finishTime"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|taskType
argument_list|,
name|other
operator|.
name|taskType
argument_list|,
name|loc
argument_list|,
literal|"taskType"
argument_list|)
expr_stmt|;
name|compare1
argument_list|(
name|taskStatus
argument_list|,
name|other
operator|.
name|taskStatus
argument_list|,
name|loc
argument_list|,
literal|"taskStatus"
argument_list|)
expr_stmt|;
name|compareLoggedTaskAttempts
argument_list|(
name|attempts
argument_list|,
name|other
operator|.
name|attempts
argument_list|,
name|loc
argument_list|,
literal|"attempts"
argument_list|)
expr_stmt|;
name|compareLoggedLocations
argument_list|(
name|preferredLocations
argument_list|,
name|other
operator|.
name|preferredLocations
argument_list|,
name|loc
argument_list|,
literal|"preferredLocations"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

