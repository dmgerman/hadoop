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
name|MapAttemptFinishedEvent
import|;
end_import

begin_class
DECL|class|MapAttempt20LineHistoryEventEmitter
specifier|public
class|class
name|MapAttempt20LineHistoryEventEmitter
extends|extends
name|TaskAttempt20LineEventEmitter
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
static|static
block|{
name|nonFinals
operator|.
name|addAll
argument_list|(
name|taskEventNonFinalSEEs
argument_list|)
expr_stmt|;
name|finals
operator|.
name|add
argument_list|(
operator|new
name|MapAttemptFinishedEventEmitter
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MapAttempt20LineHistoryEventEmitter ()
specifier|protected
name|MapAttempt20LineHistoryEventEmitter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|class|MapAttemptFinishedEventEmitter
specifier|static
specifier|private
class|class
name|MapAttemptFinishedEventEmitter
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
name|MapAttempt20LineHistoryEventEmitter
name|that
init|=
operator|(
name|MapAttempt20LineHistoryEventEmitter
operator|)
name|thatg
decl_stmt|;
if|if
condition|(
name|finishTime
operator|!=
literal|null
operator|&&
literal|"success"
operator|.
name|equalsIgnoreCase
argument_list|(
name|status
argument_list|)
condition|)
block|{
return|return
operator|new
name|MapAttemptFinishedEvent
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
literal|null
argument_list|,
name|state
argument_list|,
name|maybeParseCounters
argument_list|(
name|counters
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
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

