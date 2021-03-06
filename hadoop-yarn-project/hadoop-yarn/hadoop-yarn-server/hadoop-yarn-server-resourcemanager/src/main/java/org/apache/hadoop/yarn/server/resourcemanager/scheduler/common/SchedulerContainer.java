begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.common
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
operator|.
name|common
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmcontainer
operator|.
name|RMContainer
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
name|rmcontainer
operator|.
name|RMContainerState
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
name|scheduler
operator|.
name|SchedulerApplicationAttempt
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
name|scheduler
operator|.
name|SchedulerNode
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
name|scheduler
operator|.
name|SchedulerRequestKey
import|;
end_import

begin_comment
comment|/**  * Contexts for a container inside scheduler  */
end_comment

begin_class
DECL|class|SchedulerContainer
specifier|public
class|class
name|SchedulerContainer
parameter_list|<
name|A
extends|extends
name|SchedulerApplicationAttempt
parameter_list|,
name|N
extends|extends
name|SchedulerNode
parameter_list|>
block|{
DECL|field|rmContainer
specifier|private
name|RMContainer
name|rmContainer
decl_stmt|;
DECL|field|nodePartition
specifier|private
name|String
name|nodePartition
decl_stmt|;
DECL|field|schedulerApplicationAttempt
specifier|private
name|A
name|schedulerApplicationAttempt
decl_stmt|;
DECL|field|schedulerNode
specifier|private
name|N
name|schedulerNode
decl_stmt|;
DECL|field|allocated
specifier|private
name|boolean
name|allocated
decl_stmt|;
comment|// Allocated (True) or reserved (False)
DECL|method|SchedulerContainer (A app, N node, RMContainer rmContainer, String nodePartition, boolean allocated)
specifier|public
name|SchedulerContainer
parameter_list|(
name|A
name|app
parameter_list|,
name|N
name|node
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|String
name|nodePartition
parameter_list|,
name|boolean
name|allocated
parameter_list|)
block|{
name|this
operator|.
name|schedulerApplicationAttempt
operator|=
name|app
expr_stmt|;
name|this
operator|.
name|schedulerNode
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|rmContainer
operator|=
name|rmContainer
expr_stmt|;
name|this
operator|.
name|nodePartition
operator|=
name|nodePartition
expr_stmt|;
name|this
operator|.
name|allocated
operator|=
name|allocated
expr_stmt|;
block|}
DECL|method|getNodePartition ()
specifier|public
name|String
name|getNodePartition
parameter_list|()
block|{
return|return
name|nodePartition
return|;
block|}
DECL|method|getRmContainer ()
specifier|public
name|RMContainer
name|getRmContainer
parameter_list|()
block|{
return|return
name|rmContainer
return|;
block|}
DECL|method|getSchedulerApplicationAttempt ()
specifier|public
name|A
name|getSchedulerApplicationAttempt
parameter_list|()
block|{
return|return
name|schedulerApplicationAttempt
return|;
block|}
DECL|method|getSchedulerNode ()
specifier|public
name|N
name|getSchedulerNode
parameter_list|()
block|{
return|return
name|schedulerNode
return|;
block|}
DECL|method|isAllocated ()
specifier|public
name|boolean
name|isAllocated
parameter_list|()
block|{
return|return
name|allocated
return|;
block|}
DECL|method|getSchedulerRequestKey ()
specifier|public
name|SchedulerRequestKey
name|getSchedulerRequestKey
parameter_list|()
block|{
if|if
condition|(
name|rmContainer
operator|.
name|getState
argument_list|()
operator|==
name|RMContainerState
operator|.
name|RESERVED
condition|)
block|{
return|return
name|rmContainer
operator|.
name|getReservedSchedulerKey
argument_list|()
return|;
block|}
return|return
name|rmContainer
operator|.
name|getAllocatedSchedulerKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"(Application="
operator|+
name|schedulerApplicationAttempt
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|"; Node="
operator|+
name|schedulerNode
operator|.
name|getNodeID
argument_list|()
operator|+
literal|"; Resource="
operator|+
name|rmContainer
operator|.
name|getAllocatedOrReservedResource
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

