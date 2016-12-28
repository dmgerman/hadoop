begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
operator|.
name|Private
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
operator|.
name|Unstable
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
name|api
operator|.
name|records
operator|.
name|QueueState
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|resourcemanager
operator|.
name|reservation
operator|.
name|ReservationSchedulerConfiguration
import|;
end_import

begin_comment
comment|/**  *  * QueueStateManager which can be used by Scheduler to manage the queue state.  *  */
end_comment

begin_comment
comment|// TODO: The class will be used by YARN-5734-OrgQueue for
end_comment

begin_comment
comment|// easy CapacityScheduler queue configuration management.
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|QueueStateManager
specifier|public
class|class
name|QueueStateManager
parameter_list|<
name|T
extends|extends
name|SchedulerQueue
parameter_list|,
name|E
extends|extends
name|ReservationSchedulerConfiguration
parameter_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|QueueStateManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|queueManager
specifier|private
name|SchedulerQueueManager
argument_list|<
name|T
argument_list|,
name|E
argument_list|>
name|queueManager
decl_stmt|;
DECL|method|initialize (SchedulerQueueManager<T, E> newQueueManager)
specifier|public
specifier|synchronized
name|void
name|initialize
parameter_list|(
name|SchedulerQueueManager
argument_list|<
name|T
argument_list|,
name|E
argument_list|>
name|newQueueManager
parameter_list|)
block|{
name|this
operator|.
name|queueManager
operator|=
name|newQueueManager
expr_stmt|;
block|}
comment|/**    * Stop the queue.    * @param queueName the queue name    * @throws YarnException if the queue does not exist    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|stopQueue (String queueName)
specifier|public
specifier|synchronized
name|void
name|stopQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|YarnException
block|{
name|SchedulerQueue
argument_list|<
name|T
argument_list|>
name|queue
init|=
name|queueManager
operator|.
name|getQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"The specified queue:"
operator|+
name|queueName
operator|+
literal|" does not exist!"
argument_list|)
throw|;
block|}
name|queue
operator|.
name|stopQueue
argument_list|()
expr_stmt|;
block|}
comment|/**    * Active the queue.    * @param queueName the queue name    * @throws YarnException if the queue does not exist    *         or the queue can not be activated.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|activateQueue (String queueName)
specifier|public
specifier|synchronized
name|void
name|activateQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|YarnException
block|{
name|SchedulerQueue
argument_list|<
name|T
argument_list|>
name|queue
init|=
name|queueManager
operator|.
name|getQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"The specified queue:"
operator|+
name|queueName
operator|+
literal|" does not exist!"
argument_list|)
throw|;
block|}
name|queue
operator|.
name|activeQueue
argument_list|()
expr_stmt|;
block|}
comment|/**    * Whether this queue can be deleted.    * @param queueName the queue name    * @return true if the queue can be deleted    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|canDelete (String queueName)
specifier|public
name|boolean
name|canDelete
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|SchedulerQueue
argument_list|<
name|T
argument_list|>
name|queue
init|=
name|queueManager
operator|.
name|getQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"The specified queue:"
operator|+
name|queueName
operator|+
literal|" does not exist!"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|queue
operator|.
name|getState
argument_list|()
operator|==
name|QueueState
operator|.
name|STOPPED
condition|)
block|{
return|return
literal|true
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Need to stop the specific queue:"
operator|+
name|queueName
operator|+
literal|" first."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

