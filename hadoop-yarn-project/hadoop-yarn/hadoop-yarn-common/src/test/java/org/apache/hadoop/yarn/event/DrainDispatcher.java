begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.event
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|event
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|DrainDispatcher
specifier|public
class|class
name|DrainDispatcher
extends|extends
name|AsyncDispatcher
block|{
DECL|method|DrainDispatcher ()
specifier|public
name|DrainDispatcher
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Event
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DrainDispatcher (BlockingQueue<Event> eventQueue)
specifier|private
name|DrainDispatcher
parameter_list|(
name|BlockingQueue
argument_list|<
name|Event
argument_list|>
name|eventQueue
parameter_list|)
block|{
name|super
argument_list|(
name|eventQueue
argument_list|)
expr_stmt|;
block|}
comment|/**    * Busy loop waiting for all queued events to drain.    */
DECL|method|await ()
specifier|public
name|void
name|await
parameter_list|()
block|{
while|while
condition|(
operator|!
name|isDrained
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

