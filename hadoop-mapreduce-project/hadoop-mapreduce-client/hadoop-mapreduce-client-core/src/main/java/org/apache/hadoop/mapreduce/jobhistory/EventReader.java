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
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|io
operator|.
name|EOFException
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|CounterGroup
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
name|avro
operator|.
name|Schema
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
name|io
operator|.
name|Decoder
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
name|io
operator|.
name|DecoderFactory
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
name|io
operator|.
name|DatumReader
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
name|specific
operator|.
name|SpecificData
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
name|specific
operator|.
name|SpecificDatumReader
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|EventReader
specifier|public
class|class
name|EventReader
implements|implements
name|Closeable
block|{
DECL|field|version
specifier|private
name|String
name|version
decl_stmt|;
DECL|field|schema
specifier|private
name|Schema
name|schema
decl_stmt|;
DECL|field|in
specifier|private
name|DataInputStream
name|in
decl_stmt|;
DECL|field|decoder
specifier|private
name|Decoder
name|decoder
decl_stmt|;
DECL|field|reader
specifier|private
name|DatumReader
name|reader
decl_stmt|;
comment|/**    * Create a new Event Reader    * @param fs    * @param name    * @throws IOException    */
DECL|method|EventReader (FileSystem fs, Path name)
specifier|public
name|EventReader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new Event Reader    * @param in    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|EventReader (DataInputStream in)
specifier|public
name|EventReader
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|EventWriter
operator|.
name|VERSION
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incompatible event log version: "
operator|+
name|version
argument_list|)
throw|;
block|}
name|Schema
name|myschema
init|=
operator|new
name|SpecificData
argument_list|(
name|Event
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
operator|.
name|getSchema
argument_list|(
name|Event
operator|.
name|class
argument_list|)
decl_stmt|;
name|Schema
operator|.
name|Parser
name|parser
init|=
operator|new
name|Schema
operator|.
name|Parser
argument_list|()
decl_stmt|;
name|this
operator|.
name|schema
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|in
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
operator|new
name|SpecificDatumReader
argument_list|(
name|schema
argument_list|,
name|myschema
argument_list|)
expr_stmt|;
name|this
operator|.
name|decoder
operator|=
name|DecoderFactory
operator|.
name|get
argument_list|()
operator|.
name|jsonDecoder
argument_list|(
name|schema
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the next event from the stream    * @return the next event    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getNextEvent ()
specifier|public
name|HistoryEvent
name|getNextEvent
parameter_list|()
throws|throws
name|IOException
block|{
name|Event
name|wrapper
decl_stmt|;
try|try
block|{
name|wrapper
operator|=
operator|(
name|Event
operator|)
name|reader
operator|.
name|read
argument_list|(
literal|null
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// at EOF
return|return
literal|null
return|;
block|}
name|HistoryEvent
name|result
decl_stmt|;
switch|switch
condition|(
name|wrapper
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|JOB_SUBMITTED
case|:
name|result
operator|=
operator|new
name|JobSubmittedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_INITED
case|:
name|result
operator|=
operator|new
name|JobInitedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_FINISHED
case|:
name|result
operator|=
operator|new
name|JobFinishedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_PRIORITY_CHANGED
case|:
name|result
operator|=
operator|new
name|JobPriorityChangeEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_QUEUE_CHANGED
case|:
name|result
operator|=
operator|new
name|JobQueueChangeEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_STATUS_CHANGED
case|:
name|result
operator|=
operator|new
name|JobStatusChangedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_FAILED
case|:
name|result
operator|=
operator|new
name|JobUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_KILLED
case|:
name|result
operator|=
operator|new
name|JobUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_ERROR
case|:
name|result
operator|=
operator|new
name|JobUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|JOB_INFO_CHANGED
case|:
name|result
operator|=
operator|new
name|JobInfoChangeEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|TASK_STARTED
case|:
name|result
operator|=
operator|new
name|TaskStartedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|TASK_FINISHED
case|:
name|result
operator|=
operator|new
name|TaskFinishedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|TASK_FAILED
case|:
name|result
operator|=
operator|new
name|TaskFailedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|TASK_UPDATED
case|:
name|result
operator|=
operator|new
name|TaskUpdatedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|MAP_ATTEMPT_STARTED
case|:
name|result
operator|=
operator|new
name|TaskAttemptStartedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|MAP_ATTEMPT_FINISHED
case|:
name|result
operator|=
operator|new
name|MapAttemptFinishedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|MAP_ATTEMPT_FAILED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|MAP_ATTEMPT_KILLED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE_ATTEMPT_STARTED
case|:
name|result
operator|=
operator|new
name|TaskAttemptStartedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE_ATTEMPT_FINISHED
case|:
name|result
operator|=
operator|new
name|ReduceAttemptFinishedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE_ATTEMPT_FAILED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|REDUCE_ATTEMPT_KILLED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|SETUP_ATTEMPT_STARTED
case|:
name|result
operator|=
operator|new
name|TaskAttemptStartedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|SETUP_ATTEMPT_FINISHED
case|:
name|result
operator|=
operator|new
name|TaskAttemptFinishedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|SETUP_ATTEMPT_FAILED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|SETUP_ATTEMPT_KILLED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|CLEANUP_ATTEMPT_STARTED
case|:
name|result
operator|=
operator|new
name|TaskAttemptStartedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|CLEANUP_ATTEMPT_FINISHED
case|:
name|result
operator|=
operator|new
name|TaskAttemptFinishedEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|CLEANUP_ATTEMPT_FAILED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|CLEANUP_ATTEMPT_KILLED
case|:
name|result
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletionEvent
argument_list|()
expr_stmt|;
break|break;
case|case
name|AM_STARTED
case|:
name|result
operator|=
operator|new
name|AMStartedEvent
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected event type: "
operator|+
name|wrapper
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
name|result
operator|.
name|setDatum
argument_list|(
name|wrapper
operator|.
name|getEvent
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Close the Event reader    * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|in
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|fromAvro (JhCounters counters)
specifier|static
name|Counters
name|fromAvro
parameter_list|(
name|JhCounters
name|counters
parameter_list|)
block|{
name|Counters
name|result
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
if|if
condition|(
name|counters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|JhCounterGroup
name|g
range|:
name|counters
operator|.
name|getGroups
argument_list|()
control|)
block|{
name|CounterGroup
name|group
init|=
name|result
operator|.
name|addGroup
argument_list|(
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|g
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|g
operator|.
name|getDisplayName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|JhCounter
name|c
range|:
name|g
operator|.
name|getCounts
argument_list|()
control|)
block|{
name|group
operator|.
name|addCounter
argument_list|(
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|c
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|c
operator|.
name|getDisplayName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

