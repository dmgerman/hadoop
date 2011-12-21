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
name|TaskAttemptFinishedEvent
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
name|TaskAttemptStartedEvent
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
name|TaskAttemptUnsuccessfulCompletionEvent
import|;
end_import

begin_class
DECL|class|TaskAttempt20LineEventEmitter
specifier|public
specifier|abstract
class|class
name|TaskAttempt20LineEventEmitter
extends|extends
name|HistoryEventEmitter
block|{
DECL|field|taskEventNonFinalSEEs
specifier|static
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|taskEventNonFinalSEEs
init|=
operator|new
name|LinkedList
argument_list|<
name|SingleEventEmitter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|taskEventFinalSEEs
specifier|static
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|taskEventFinalSEEs
init|=
operator|new
name|LinkedList
argument_list|<
name|SingleEventEmitter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_HTTP_PORT
specifier|static
specifier|private
specifier|final
name|int
name|DEFAULT_HTTP_PORT
init|=
literal|80
decl_stmt|;
DECL|field|originalStartTime
name|Long
name|originalStartTime
init|=
literal|null
decl_stmt|;
DECL|field|originalTaskType
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskType
name|originalTaskType
init|=
literal|null
decl_stmt|;
static|static
block|{
name|taskEventNonFinalSEEs
operator|.
name|add
argument_list|(
operator|new
name|TaskAttemptStartedEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
name|taskEventNonFinalSEEs
operator|.
name|add
argument_list|(
operator|new
name|TaskAttemptFinishedEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
name|taskEventNonFinalSEEs
operator|.
name|add
argument_list|(
operator|new
name|TaskAttemptUnsuccessfulCompletionEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskAttempt20LineEventEmitter ()
specifier|protected
name|TaskAttempt20LineEventEmitter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|class|TaskAttemptStartedEventEmitter
specifier|static
specifier|private
class|class
name|TaskAttemptStartedEventEmitter
extends|extends
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String taskAttemptIDName, HistoryEventEmitter thatg)
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|taskAttemptIDName
parameter_list|,
name|HistoryEventEmitter
name|thatg
parameter_list|)
block|{
if|if
condition|(
name|taskAttemptIDName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TaskAttemptID
name|taskAttemptID
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|taskAttemptIDName
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
name|trackerName
init|=
name|line
operator|.
name|get
argument_list|(
literal|"TRACKER_NAME"
argument_list|)
decl_stmt|;
name|String
name|httpPort
init|=
name|line
operator|.
name|get
argument_list|(
literal|"HTTP_PORT"
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
name|TaskAttempt20LineEventEmitter
name|that
init|=
operator|(
name|TaskAttempt20LineEventEmitter
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
name|int
name|port
init|=
name|httpPort
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|?
name|DEFAULT_HTTP_PORT
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|httpPort
argument_list|)
decl_stmt|;
return|return
operator|new
name|TaskAttemptStartedEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|that
operator|.
name|originalTaskType
argument_list|,
name|that
operator|.
name|originalStartTime
argument_list|,
name|trackerName
argument_list|,
name|port
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|TaskAttemptFinishedEventEmitter
specifier|static
specifier|private
class|class
name|TaskAttemptFinishedEventEmitter
extends|extends
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String taskAttemptIDName, HistoryEventEmitter thatg)
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|taskAttemptIDName
parameter_list|,
name|HistoryEventEmitter
name|thatg
parameter_list|)
block|{
if|if
condition|(
name|taskAttemptIDName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TaskAttemptID
name|taskAttemptID
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|taskAttemptIDName
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
name|status
init|=
name|line
operator|.
name|get
argument_list|(
literal|"TASK_STATUS"
argument_list|)
decl_stmt|;
if|if
condition|(
name|finishTime
operator|!=
literal|null
operator|&&
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
condition|)
block|{
name|String
name|hostName
init|=
name|line
operator|.
name|get
argument_list|(
literal|"HOSTNAME"
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
name|String
name|state
init|=
name|line
operator|.
name|get
argument_list|(
literal|"STATE_STRING"
argument_list|)
decl_stmt|;
name|TaskAttempt20LineEventEmitter
name|that
init|=
operator|(
name|TaskAttempt20LineEventEmitter
operator|)
name|thatg
decl_stmt|;
name|ParsedHost
name|pHost
init|=
name|ParsedHost
operator|.
name|parse
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
return|return
operator|new
name|TaskAttemptFinishedEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|that
operator|.
name|originalTaskType
argument_list|,
name|status
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|finishTime
argument_list|)
argument_list|,
name|pHost
operator|.
name|getRackName
argument_list|()
argument_list|,
name|pHost
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|state
argument_list|,
name|maybeParseCounters
argument_list|(
name|counters
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|TaskAttemptUnsuccessfulCompletionEventEmitter
specifier|static
specifier|private
class|class
name|TaskAttemptUnsuccessfulCompletionEventEmitter
extends|extends
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String taskAttemptIDName, HistoryEventEmitter thatg)
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|taskAttemptIDName
parameter_list|,
name|HistoryEventEmitter
name|thatg
parameter_list|)
block|{
if|if
condition|(
name|taskAttemptIDName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TaskAttemptID
name|taskAttemptID
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|taskAttemptIDName
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
name|status
init|=
name|line
operator|.
name|get
argument_list|(
literal|"TASK_STATUS"
argument_list|)
decl_stmt|;
if|if
condition|(
name|finishTime
operator|!=
literal|null
operator|&&
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
condition|)
block|{
name|String
name|hostName
init|=
name|line
operator|.
name|get
argument_list|(
literal|"HOSTNAME"
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
name|TaskAttempt20LineEventEmitter
name|that
init|=
operator|(
name|TaskAttempt20LineEventEmitter
operator|)
name|thatg
decl_stmt|;
name|ParsedHost
name|pHost
init|=
name|ParsedHost
operator|.
name|parse
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
name|String
name|rackName
init|=
literal|null
decl_stmt|;
comment|// Earlier versions of MR logged on hostnames (without rackname) for
comment|// unsuccessful attempts
if|if
condition|(
name|pHost
operator|!=
literal|null
condition|)
block|{
name|rackName
operator|=
name|pHost
operator|.
name|getRackName
argument_list|()
expr_stmt|;
name|hostName
operator|=
name|pHost
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|(
name|taskAttemptID
argument_list|,
name|that
operator|.
name|originalTaskType
argument_list|,
name|status
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|finishTime
argument_list|)
argument_list|,
name|hostName
argument_list|,
operator|-
literal|1
argument_list|,
name|rackName
argument_list|,
name|error
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
block|}
end_class

end_unit

