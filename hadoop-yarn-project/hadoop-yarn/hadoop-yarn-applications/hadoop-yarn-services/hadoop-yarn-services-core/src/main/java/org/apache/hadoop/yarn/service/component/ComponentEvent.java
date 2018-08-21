begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.component
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|component
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|ContainerStatus
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
name|service
operator|.
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
import|;
end_import

begin_class
DECL|class|ComponentEvent
specifier|public
class|class
name|ComponentEvent
extends|extends
name|AbstractEvent
argument_list|<
name|ComponentEventType
argument_list|>
block|{
DECL|field|desired
specifier|private
name|long
name|desired
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|ComponentEventType
name|type
decl_stmt|;
DECL|field|container
specifier|private
name|Container
name|container
decl_stmt|;
DECL|field|instance
specifier|private
name|ComponentInstance
name|instance
decl_stmt|;
DECL|field|status
specifier|private
name|ContainerStatus
name|status
decl_stmt|;
DECL|field|containerId
specifier|private
name|ContainerId
name|containerId
decl_stmt|;
DECL|field|targetSpec
specifier|private
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
name|targetSpec
decl_stmt|;
DECL|field|upgradeVersion
specifier|private
name|String
name|upgradeVersion
decl_stmt|;
DECL|field|expressUpgrade
specifier|private
name|boolean
name|expressUpgrade
decl_stmt|;
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|containerId
return|;
block|}
DECL|method|setContainerId (ContainerId containerId)
specifier|public
name|ComponentEvent
name|setContainerId
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|ComponentEvent (String name, ComponentEventType type)
specifier|public
name|ComponentEvent
parameter_list|(
name|String
name|name
parameter_list|,
name|ComponentEventType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getType ()
specifier|public
name|ComponentEventType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getDesired ()
specifier|public
name|long
name|getDesired
parameter_list|()
block|{
return|return
name|desired
return|;
block|}
DECL|method|setDesired (long desired)
specifier|public
name|ComponentEvent
name|setDesired
parameter_list|(
name|long
name|desired
parameter_list|)
block|{
name|this
operator|.
name|desired
operator|=
name|desired
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getContainer ()
specifier|public
name|Container
name|getContainer
parameter_list|()
block|{
return|return
name|container
return|;
block|}
DECL|method|setContainer (Container container)
specifier|public
name|ComponentEvent
name|setContainer
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getInstance ()
specifier|public
name|ComponentInstance
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
DECL|method|setInstance (ComponentInstance instance)
specifier|public
name|ComponentEvent
name|setInstance
parameter_list|(
name|ComponentInstance
name|instance
parameter_list|)
block|{
name|this
operator|.
name|instance
operator|=
name|instance
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getStatus ()
specifier|public
name|ContainerStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|setStatus (ContainerStatus status)
specifier|public
name|ComponentEvent
name|setStatus
parameter_list|(
name|ContainerStatus
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getTargetSpec ()
specifier|public
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
name|getTargetSpec
parameter_list|()
block|{
return|return
name|targetSpec
return|;
block|}
DECL|method|setTargetSpec ( org.apache.hadoop.yarn.service.api.records.Component targetSpec)
specifier|public
name|ComponentEvent
name|setTargetSpec
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
name|targetSpec
parameter_list|)
block|{
name|this
operator|.
name|targetSpec
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|targetSpec
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getUpgradeVersion ()
specifier|public
name|String
name|getUpgradeVersion
parameter_list|()
block|{
return|return
name|upgradeVersion
return|;
block|}
DECL|method|setUpgradeVersion (String upgradeVersion)
specifier|public
name|ComponentEvent
name|setUpgradeVersion
parameter_list|(
name|String
name|upgradeVersion
parameter_list|)
block|{
name|this
operator|.
name|upgradeVersion
operator|=
name|upgradeVersion
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|isExpressUpgrade ()
specifier|public
name|boolean
name|isExpressUpgrade
parameter_list|()
block|{
return|return
name|expressUpgrade
return|;
block|}
DECL|method|setExpressUpgrade (boolean expressUpgrade)
specifier|public
name|ComponentEvent
name|setExpressUpgrade
parameter_list|(
name|boolean
name|expressUpgrade
parameter_list|)
block|{
name|this
operator|.
name|expressUpgrade
operator|=
name|expressUpgrade
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

