begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|JobId
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
name|event
operator|.
name|AbstractEvent
import|;
end_import

begin_class
DECL|class|JobHistoryEvent
specifier|public
class|class
name|JobHistoryEvent
extends|extends
name|AbstractEvent
argument_list|<
name|EventType
argument_list|>
block|{
DECL|field|jobID
specifier|private
specifier|final
name|JobId
name|jobID
decl_stmt|;
DECL|field|historyEvent
specifier|private
specifier|final
name|HistoryEvent
name|historyEvent
decl_stmt|;
DECL|method|JobHistoryEvent (JobId jobID, HistoryEvent historyEvent)
specifier|public
name|JobHistoryEvent
parameter_list|(
name|JobId
name|jobID
parameter_list|,
name|HistoryEvent
name|historyEvent
parameter_list|)
block|{
name|this
argument_list|(
name|jobID
argument_list|,
name|historyEvent
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|JobHistoryEvent (JobId jobID, HistoryEvent historyEvent, long timestamp)
specifier|public
name|JobHistoryEvent
parameter_list|(
name|JobId
name|jobID
parameter_list|,
name|HistoryEvent
name|historyEvent
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|super
argument_list|(
name|historyEvent
operator|.
name|getEventType
argument_list|()
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
name|this
operator|.
name|jobID
operator|=
name|jobID
expr_stmt|;
name|this
operator|.
name|historyEvent
operator|=
name|historyEvent
expr_stmt|;
block|}
DECL|method|getJobID ()
specifier|public
name|JobId
name|getJobID
parameter_list|()
block|{
return|return
name|jobID
return|;
block|}
DECL|method|getHistoryEvent ()
specifier|public
name|HistoryEvent
name|getHistoryEvent
parameter_list|()
block|{
return|return
name|historyEvent
return|;
block|}
block|}
end_class

end_unit

