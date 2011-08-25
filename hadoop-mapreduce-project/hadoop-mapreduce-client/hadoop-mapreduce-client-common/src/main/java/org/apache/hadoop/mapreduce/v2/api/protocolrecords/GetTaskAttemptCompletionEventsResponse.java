begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.protocolrecords
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|protocolrecords
package|;
end_package

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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptCompletionEvent
import|;
end_import

begin_interface
DECL|interface|GetTaskAttemptCompletionEventsResponse
specifier|public
interface|interface
name|GetTaskAttemptCompletionEventsResponse
block|{
DECL|method|getCompletionEventList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|TaskAttemptCompletionEvent
argument_list|>
name|getCompletionEventList
parameter_list|()
function_decl|;
DECL|method|getCompletionEvent (int index)
specifier|public
specifier|abstract
name|TaskAttemptCompletionEvent
name|getCompletionEvent
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getCompletionEventCount ()
specifier|public
specifier|abstract
name|int
name|getCompletionEventCount
parameter_list|()
function_decl|;
DECL|method|addAllCompletionEvents (List<TaskAttemptCompletionEvent> eventList)
specifier|public
specifier|abstract
name|void
name|addAllCompletionEvents
parameter_list|(
name|List
argument_list|<
name|TaskAttemptCompletionEvent
argument_list|>
name|eventList
parameter_list|)
function_decl|;
DECL|method|addCompletionEvent (TaskAttemptCompletionEvent event)
specifier|public
specifier|abstract
name|void
name|addCompletionEvent
parameter_list|(
name|TaskAttemptCompletionEvent
name|event
parameter_list|)
function_decl|;
DECL|method|removeCompletionEvent (int index)
specifier|public
specifier|abstract
name|void
name|removeCompletionEvent
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearCompletionEvents ()
specifier|public
specifier|abstract
name|void
name|clearCompletionEvents
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

