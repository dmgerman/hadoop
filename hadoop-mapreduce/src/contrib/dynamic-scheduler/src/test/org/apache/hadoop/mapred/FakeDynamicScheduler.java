begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

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
name|Collection
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
name|server
operator|.
name|jobtracker
operator|.
name|TaskTracker
import|;
end_import

begin_comment
comment|/**  * Mock queue scheduler for testing only  */
end_comment

begin_class
DECL|class|FakeDynamicScheduler
specifier|public
class|class
name|FakeDynamicScheduler
extends|extends
name|QueueTaskScheduler
block|{
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|terminate ()
specifier|public
name|void
name|terminate
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|assignTasks (TaskTracker taskTracker)
specifier|public
name|List
argument_list|<
name|Task
argument_list|>
name|assignTasks
parameter_list|(
name|TaskTracker
name|taskTracker
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|getJobs (String queueName)
specifier|public
name|Collection
argument_list|<
name|JobInProgress
argument_list|>
name|getJobs
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|setAllocator (QueueAllocator allocator)
specifier|public
name|void
name|setAllocator
parameter_list|(
name|QueueAllocator
name|allocator
parameter_list|)
block|{   }
block|}
end_class

end_unit

