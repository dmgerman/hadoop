begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica
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
operator|.
name|fica
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|Priority
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
name|rmnode
operator|.
name|RMNode
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

begin_class
DECL|class|FiCaSchedulerNode
specifier|public
class|class
name|FiCaSchedulerNode
extends|extends
name|SchedulerNode
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
name|FiCaSchedulerNode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FiCaSchedulerNode (RMNode node, boolean usePortForNodeName)
specifier|public
name|FiCaSchedulerNode
parameter_list|(
name|RMNode
name|node
parameter_list|,
name|boolean
name|usePortForNodeName
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|,
name|usePortForNodeName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reserveResource ( SchedulerApplicationAttempt application, Priority priority, RMContainer container)
specifier|public
specifier|synchronized
name|void
name|reserveResource
parameter_list|(
name|SchedulerApplicationAttempt
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|RMContainer
name|container
parameter_list|)
block|{
comment|// Check if it's already reserved
name|RMContainer
name|reservedContainer
init|=
name|getReservedContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|reservedContainer
operator|!=
literal|null
condition|)
block|{
comment|// Sanity check
if|if
condition|(
operator|!
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to reserve"
operator|+
literal|" container "
operator|+
name|container
operator|+
literal|" on node "
operator|+
name|container
operator|.
name|getReservedNode
argument_list|()
operator|+
literal|" when currently"
operator|+
literal|" reserved resource "
operator|+
name|reservedContainer
operator|+
literal|" on node "
operator|+
name|reservedContainer
operator|.
name|getReservedNode
argument_list|()
argument_list|)
throw|;
block|}
comment|// Cannot reserve more than one application attempt on a given node!
comment|// Reservation is still against attempt.
if|if
condition|(
operator|!
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|equals
argument_list|(
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to reserve"
operator|+
literal|" container "
operator|+
name|container
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|" when currently"
operator|+
literal|" reserved container "
operator|+
name|reservedContainer
operator|+
literal|" on node "
operator|+
name|this
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updated reserved container "
operator|+
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
operator|+
literal|" for application attempt "
operator|+
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reserved container "
operator|+
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
operator|+
literal|" for application attempt "
operator|+
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|setReservedContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unreserveResource ( SchedulerApplicationAttempt application)
specifier|public
specifier|synchronized
name|void
name|unreserveResource
parameter_list|(
name|SchedulerApplicationAttempt
name|application
parameter_list|)
block|{
comment|// adding NP checks as this can now be called for preemption
if|if
condition|(
name|getReservedContainer
argument_list|()
operator|!=
literal|null
operator|&&
name|getReservedContainer
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|!=
literal|null
operator|&&
name|getReservedContainer
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|!=
literal|null
operator|&&
name|getReservedContainer
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// Cannot unreserve for wrong application...
name|ApplicationAttemptId
name|reservedApplication
init|=
name|getReservedContainer
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|reservedApplication
operator|.
name|equals
argument_list|(
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to unreserve "
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|" when currently reserved "
operator|+
literal|" for application "
operator|+
name|reservedApplication
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
name|setReservedContainer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

