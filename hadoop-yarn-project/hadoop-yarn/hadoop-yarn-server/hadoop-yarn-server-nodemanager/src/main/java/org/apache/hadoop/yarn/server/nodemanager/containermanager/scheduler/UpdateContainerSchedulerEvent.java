begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler
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
name|nodemanager
operator|.
name|containermanager
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
name|hadoop
operator|.
name|yarn
operator|.
name|security
operator|.
name|ContainerTokenIdentifier
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|UpdateContainerTokenEvent
import|;
end_import

begin_comment
comment|/**  * Update Event consumed by the {@link ContainerScheduler}.  */
end_comment

begin_class
DECL|class|UpdateContainerSchedulerEvent
specifier|public
class|class
name|UpdateContainerSchedulerEvent
extends|extends
name|ContainerSchedulerEvent
block|{
DECL|field|containerEvent
specifier|private
specifier|final
name|UpdateContainerTokenEvent
name|containerEvent
decl_stmt|;
DECL|field|originalToken
specifier|private
specifier|final
name|ContainerTokenIdentifier
name|originalToken
decl_stmt|;
comment|/**    * Create instance of Event.    *    * @param container Container.    * @param origToken The Original Container Token.    * @param event The Container Event.    */
DECL|method|UpdateContainerSchedulerEvent (Container container, ContainerTokenIdentifier origToken, UpdateContainerTokenEvent event)
specifier|public
name|UpdateContainerSchedulerEvent
parameter_list|(
name|Container
name|container
parameter_list|,
name|ContainerTokenIdentifier
name|origToken
parameter_list|,
name|UpdateContainerTokenEvent
name|event
parameter_list|)
block|{
name|super
argument_list|(
name|container
argument_list|,
name|ContainerSchedulerEventType
operator|.
name|UPDATE_CONTAINER
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerEvent
operator|=
name|event
expr_stmt|;
name|this
operator|.
name|originalToken
operator|=
name|origToken
expr_stmt|;
block|}
comment|/**    * Original Token before update.    *    * @return Container Token.    */
DECL|method|getOriginalToken ()
specifier|public
name|ContainerTokenIdentifier
name|getOriginalToken
parameter_list|()
block|{
return|return
name|this
operator|.
name|originalToken
return|;
block|}
comment|/**    * Update Container Token.    *    * @return Container Token.    */
DECL|method|getUpdatedToken ()
specifier|public
name|ContainerTokenIdentifier
name|getUpdatedToken
parameter_list|()
block|{
return|return
name|containerEvent
operator|.
name|getUpdatedToken
argument_list|()
return|;
block|}
comment|/**    * isResourceChange.    * @return isResourceChange.    */
DECL|method|isResourceChange ()
specifier|public
name|boolean
name|isResourceChange
parameter_list|()
block|{
return|return
name|containerEvent
operator|.
name|isResourceChange
argument_list|()
return|;
block|}
comment|/**    * isExecTypeUpdate.    * @return isExecTypeUpdate.    */
DECL|method|isExecTypeUpdate ()
specifier|public
name|boolean
name|isExecTypeUpdate
parameter_list|()
block|{
return|return
name|containerEvent
operator|.
name|isExecTypeUpdate
argument_list|()
return|;
block|}
comment|/**    * isIncrease.    * @return isIncrease.    */
DECL|method|isIncrease ()
specifier|public
name|boolean
name|isIncrease
parameter_list|()
block|{
return|return
name|containerEvent
operator|.
name|isIncrease
argument_list|()
return|;
block|}
block|}
end_class

end_unit

