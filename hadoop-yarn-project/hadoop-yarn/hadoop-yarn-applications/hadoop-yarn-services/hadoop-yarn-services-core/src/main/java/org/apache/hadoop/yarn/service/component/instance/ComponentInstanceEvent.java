begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.component.instance
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
operator|.
name|instance
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

begin_class
DECL|class|ComponentInstanceEvent
specifier|public
class|class
name|ComponentInstanceEvent
extends|extends
name|AbstractEvent
argument_list|<
name|ComponentInstanceEventType
argument_list|>
block|{
DECL|field|id
specifier|private
name|ContainerId
name|id
decl_stmt|;
DECL|field|status
specifier|private
name|ContainerStatus
name|status
decl_stmt|;
DECL|field|shouldDestroy
specifier|private
name|boolean
name|shouldDestroy
init|=
literal|false
decl_stmt|;
DECL|method|ComponentInstanceEvent (ContainerId containerId, ComponentInstanceEventType componentInstanceEventType)
specifier|public
name|ComponentInstanceEvent
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|ComponentInstanceEventType
name|componentInstanceEventType
parameter_list|)
block|{
name|super
argument_list|(
name|componentInstanceEventType
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|containerId
expr_stmt|;
block|}
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getStatus ()
specifier|public
name|ContainerStatus
name|getStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|status
return|;
block|}
DECL|method|setStatus (ContainerStatus status)
specifier|public
name|ComponentInstanceEvent
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
DECL|method|setShouldDestroy ()
specifier|public
name|void
name|setShouldDestroy
parameter_list|()
block|{
name|shouldDestroy
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|shouldDestroy ()
specifier|public
name|boolean
name|shouldDestroy
parameter_list|()
block|{
return|return
name|shouldDestroy
return|;
block|}
block|}
end_class

end_unit

