begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job.event
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
name|app
operator|.
name|job
operator|.
name|event
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
name|TaskAttemptId
import|;
end_import

begin_class
DECL|class|TaskAttemptContainerLaunchedEvent
specifier|public
class|class
name|TaskAttemptContainerLaunchedEvent
extends|extends
name|TaskAttemptEvent
block|{
DECL|field|shufflePort
specifier|private
name|int
name|shufflePort
decl_stmt|;
comment|/**    * Create a new TaskAttemptEvent.    * @param id the id of the task attempt    * @param shufflePort the port that shuffle is listening on.    */
DECL|method|TaskAttemptContainerLaunchedEvent (TaskAttemptId id, int shufflePort)
specifier|public
name|TaskAttemptContainerLaunchedEvent
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|,
name|int
name|shufflePort
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CONTAINER_LAUNCHED
argument_list|)
expr_stmt|;
name|this
operator|.
name|shufflePort
operator|=
name|shufflePort
expr_stmt|;
block|}
comment|/**    * Get the port that the shuffle handler is listening on. This is only    * valid if the type of the event is TA_CONTAINER_LAUNCHED    * @return the port the shuffle handler is listening on.    */
DECL|method|getShufflePort ()
specifier|public
name|int
name|getShufflePort
parameter_list|()
block|{
return|return
name|shufflePort
return|;
block|}
block|}
end_class

end_unit

