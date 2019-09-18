begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_comment
comment|/**  * A priority queue that stores a number of {@link BackgroundTask}.  */
end_comment

begin_class
DECL|class|BackgroundTaskQueue
specifier|public
class|class
name|BackgroundTaskQueue
block|{
DECL|field|tasks
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|BackgroundTask
argument_list|>
name|tasks
decl_stmt|;
DECL|method|BackgroundTaskQueue ()
specifier|public
name|BackgroundTaskQueue
parameter_list|()
block|{
name|tasks
operator|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|(
parameter_list|(
name|task1
parameter_list|,
name|task2
parameter_list|)
lambda|->
name|task1
operator|.
name|getPriority
argument_list|()
operator|-
name|task2
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the head task in this queue.    */
DECL|method|poll ()
specifier|public
specifier|synchronized
name|BackgroundTask
name|poll
parameter_list|()
block|{
return|return
name|tasks
operator|.
name|poll
argument_list|()
return|;
block|}
comment|/**    * Add a {@link BackgroundTask} to the queue,    * the task will be sorted by its priority.    *    * @param task    */
DECL|method|add (BackgroundTask task)
specifier|public
specifier|synchronized
name|void
name|add
parameter_list|(
name|BackgroundTask
name|task
parameter_list|)
block|{
name|tasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return true if the queue contains no task, false otherwise.    */
DECL|method|isEmpty ()
specifier|public
specifier|synchronized
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|tasks
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * @return the size of the queue.    */
DECL|method|size ()
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|tasks
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

