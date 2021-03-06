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
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
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
name|HistoryEvent
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
name|TaskFailedEvent
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
name|TaskFinishedEvent
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
name|TaskStartedEvent
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
name|TaskUpdatedEvent
import|;
end_import

begin_class
DECL|class|Task20LineHistoryEventEmitter
specifier|public
class|class
name|Task20LineHistoryEventEmitter
extends|extends
name|HistoryEventEmitter
block|{
DECL|field|nonFinals
specifier|static
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|nonFinals
init|=
operator|new
name|LinkedList
argument_list|<
name|SingleEventEmitter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|finals
specifier|static
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|finals
init|=
operator|new
name|LinkedList
argument_list|<
name|SingleEventEmitter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|originalStartTime
name|Long
name|originalStartTime
init|=
literal|null
decl_stmt|;
DECL|field|originalTaskType
name|TaskType
name|originalTaskType
init|=
literal|null
decl_stmt|;
static|static
block|{
name|nonFinals
operator|.
name|add
argument_list|(
operator|new
name|TaskStartedEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
name|nonFinals
operator|.
name|add
argument_list|(
operator|new
name|TaskUpdatedEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
name|finals
operator|.
name|add
argument_list|(
operator|new
name|TaskFinishedEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
name|finals
operator|.
name|add
argument_list|(
operator|new
name|TaskFailedEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|Task20LineHistoryEventEmitter ()
specifier|protected
name|Task20LineHistoryEventEmitter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|class|TaskStartedEventEmitter
specifier|static
specifier|private
class|class
name|TaskStartedEventEmitter
extends|extends
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String taskIDName, HistoryEventEmitter thatg)
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|taskIDName
parameter_list|,
name|HistoryEventEmitter
name|thatg
parameter_list|)
block|{
if|if
condition|(
name|taskIDName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TaskID
name|taskID
init|=
name|TaskID
operator|.
name|forName
argument_list|(
name|taskIDName
argument_list|)
decl_stmt|;
name|String
name|taskType
init|=
name|line
operator|.
name|get
argument_list|(
literal|"TASK_TYPE"
argument_list|)
decl_stmt|;
name|String
name|startTime
init|=
name|line
operator|.
name|get
argument_list|(
literal|"START_TIME"
argument_list|)
decl_stmt|;
name|String
name|splits
init|=
name|line
operator|.
name|get
argument_list|(
literal|"SPLITS"
argument_list|)
decl_stmt|;
if|if
condition|(
name|startTime
operator|!=
literal|null
operator|&&
name|taskType
operator|!=
literal|null
condition|)
block|{
name|Task20LineHistoryEventEmitter
name|that
init|=
operator|(
name|Task20LineHistoryEventEmitter
operator|)
name|thatg
decl_stmt|;
name|that
operator|.
name|originalStartTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|that
operator|.
name|originalTaskType
operator|=
name|Version20LogInterfaceUtils
operator|.
name|get20TaskType
argument_list|(
name|taskType
argument_list|)
expr_stmt|;
return|return
operator|new
name|TaskStartedEvent
argument_list|(
name|taskID
argument_list|,
name|that
operator|.
name|originalStartTime
argument_list|,
name|that
operator|.
name|originalTaskType
argument_list|,
name|splits
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|TaskUpdatedEventEmitter
specifier|static
specifier|private
class|class
name|TaskUpdatedEventEmitter
extends|extends
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String taskIDName, HistoryEventEmitter thatg)
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|taskIDName
parameter_list|,
name|HistoryEventEmitter
name|thatg
parameter_list|)
block|{
if|if
condition|(
name|taskIDName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TaskID
name|taskID
init|=
name|TaskID
operator|.
name|forName
argument_list|(
name|taskIDName
argument_list|)
decl_stmt|;
name|String
name|finishTime
init|=
name|line
operator|.
name|get
argument_list|(
literal|"FINISH_TIME"
argument_list|)
decl_stmt|;
if|if
condition|(
name|finishTime
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|TaskUpdatedEvent
argument_list|(
name|taskID
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|finishTime
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|TaskFinishedEventEmitter
specifier|static
specifier|private
class|class
name|TaskFinishedEventEmitter
extends|extends
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String taskIDName, HistoryEventEmitter thatg)
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|taskIDName
parameter_list|,
name|HistoryEventEmitter
name|thatg
parameter_list|)
block|{
if|if
condition|(
name|taskIDName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TaskID
name|taskID
init|=
name|TaskID
operator|.
name|forName
argument_list|(
name|taskIDName
argument_list|)
decl_stmt|;
name|String
name|status
init|=
name|line
operator|.
name|get
argument_list|(
literal|"TASK_STATUS"
argument_list|)
decl_stmt|;
name|String
name|finishTime
init|=
name|line
operator|.
name|get
argument_list|(
literal|"FINISH_TIME"
argument_list|)
decl_stmt|;
name|String
name|error
init|=
name|line
operator|.
name|get
argument_list|(
literal|"ERROR"
argument_list|)
decl_stmt|;
name|String
name|counters
init|=
name|line
operator|.
name|get
argument_list|(
literal|"COUNTERS"
argument_list|)
decl_stmt|;
if|if
condition|(
name|finishTime
operator|!=
literal|null
operator|&&
name|error
operator|==
literal|null
operator|&&
operator|(
name|status
operator|!=
literal|null
operator|&&
name|status
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"success"
argument_list|)
operator|)
condition|)
block|{
name|Counters
name|eventCounters
init|=
name|maybeParseCounters
argument_list|(
name|counters
argument_list|)
decl_stmt|;
name|Task20LineHistoryEventEmitter
name|that
init|=
operator|(
name|Task20LineHistoryEventEmitter
operator|)
name|thatg
decl_stmt|;
if|if
condition|(
name|that
operator|.
name|originalTaskType
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|TaskFinishedEvent
argument_list|(
name|taskID
argument_list|,
literal|null
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|finishTime
argument_list|)
argument_list|,
name|that
operator|.
name|originalTaskType
argument_list|,
name|status
argument_list|,
name|eventCounters
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|TaskFailedEventEmitter
specifier|static
specifier|private
class|class
name|TaskFailedEventEmitter
extends|extends
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String taskIDName, HistoryEventEmitter thatg)
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|taskIDName
parameter_list|,
name|HistoryEventEmitter
name|thatg
parameter_list|)
block|{
if|if
condition|(
name|taskIDName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TaskID
name|taskID
init|=
name|TaskID
operator|.
name|forName
argument_list|(
name|taskIDName
argument_list|)
decl_stmt|;
name|String
name|status
init|=
name|line
operator|.
name|get
argument_list|(
literal|"TASK_STATUS"
argument_list|)
decl_stmt|;
name|String
name|finishTime
init|=
name|line
operator|.
name|get
argument_list|(
literal|"FINISH_TIME"
argument_list|)
decl_stmt|;
name|String
name|taskType
init|=
name|line
operator|.
name|get
argument_list|(
literal|"TASK_TYPE"
argument_list|)
decl_stmt|;
name|String
name|error
init|=
name|line
operator|.
name|get
argument_list|(
literal|"ERROR"
argument_list|)
decl_stmt|;
if|if
condition|(
name|finishTime
operator|!=
literal|null
operator|&&
operator|(
name|error
operator|!=
literal|null
operator|||
operator|(
name|status
operator|!=
literal|null
operator|&&
operator|!
name|status
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"success"
argument_list|)
operator|)
operator|)
condition|)
block|{
name|Task20LineHistoryEventEmitter
name|that
init|=
operator|(
name|Task20LineHistoryEventEmitter
operator|)
name|thatg
decl_stmt|;
name|TaskType
name|originalTaskType
init|=
name|that
operator|.
name|originalTaskType
operator|==
literal|null
condition|?
name|Version20LogInterfaceUtils
operator|.
name|get20TaskType
argument_list|(
name|taskType
argument_list|)
else|:
name|that
operator|.
name|originalTaskType
decl_stmt|;
return|return
operator|new
name|TaskFailedEvent
argument_list|(
name|taskID
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|finishTime
argument_list|)
argument_list|,
name|originalTaskType
argument_list|,
name|error
argument_list|,
name|status
argument_list|,
literal|null
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|finalSEEs ()
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|finalSEEs
parameter_list|()
block|{
return|return
name|finals
return|;
block|}
annotation|@
name|Override
DECL|method|nonFinalSEEs ()
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|nonFinalSEEs
parameter_list|()
block|{
return|return
name|nonFinals
return|;
block|}
block|}
end_class

end_unit

