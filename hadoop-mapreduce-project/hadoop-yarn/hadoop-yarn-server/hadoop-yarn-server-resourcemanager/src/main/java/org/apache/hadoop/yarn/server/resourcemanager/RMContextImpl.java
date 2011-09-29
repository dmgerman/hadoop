begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|ApplicationId
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
name|NodeId
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
name|Dispatcher
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
name|recovery
operator|.
name|ApplicationsStore
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
name|recovery
operator|.
name|NodeStore
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
name|recovery
operator|.
name|Store
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
name|rmapp
operator|.
name|RMApp
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
name|rmapp
operator|.
name|attempt
operator|.
name|AMLivelinessMonitor
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
name|ContainerAllocationExpirer
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

begin_class
DECL|class|RMContextImpl
specifier|public
class|class
name|RMContextImpl
implements|implements
name|RMContext
block|{
DECL|field|rmDispatcher
specifier|private
specifier|final
name|Dispatcher
name|rmDispatcher
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|applications
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|applications
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|nodes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|amLivelinessMonitor
specifier|private
name|AMLivelinessMonitor
name|amLivelinessMonitor
decl_stmt|;
DECL|field|containerAllocationExpirer
specifier|private
name|ContainerAllocationExpirer
name|containerAllocationExpirer
decl_stmt|;
DECL|method|RMContextImpl (Store store, Dispatcher rmDispatcher, ContainerAllocationExpirer containerAllocationExpirer, AMLivelinessMonitor amLivelinessMonitor)
specifier|public
name|RMContextImpl
parameter_list|(
name|Store
name|store
parameter_list|,
name|Dispatcher
name|rmDispatcher
parameter_list|,
name|ContainerAllocationExpirer
name|containerAllocationExpirer
parameter_list|,
name|AMLivelinessMonitor
name|amLivelinessMonitor
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|rmDispatcher
operator|=
name|rmDispatcher
expr_stmt|;
name|this
operator|.
name|containerAllocationExpirer
operator|=
name|containerAllocationExpirer
expr_stmt|;
name|this
operator|.
name|amLivelinessMonitor
operator|=
name|amLivelinessMonitor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDispatcher ()
specifier|public
name|Dispatcher
name|getDispatcher
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmDispatcher
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeStore ()
specifier|public
name|NodeStore
name|getNodeStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationsStore ()
specifier|public
name|ApplicationsStore
name|getApplicationsStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
annotation|@
name|Override
DECL|method|getRMApps ()
specifier|public
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|getRMApps
parameter_list|()
block|{
return|return
name|this
operator|.
name|applications
return|;
block|}
annotation|@
name|Override
DECL|method|getRMNodes ()
specifier|public
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|getRMNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodes
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerAllocationExpirer ()
specifier|public
name|ContainerAllocationExpirer
name|getContainerAllocationExpirer
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerAllocationExpirer
return|;
block|}
annotation|@
name|Override
DECL|method|getAMLivelinessMonitor ()
specifier|public
name|AMLivelinessMonitor
name|getAMLivelinessMonitor
parameter_list|()
block|{
return|return
name|this
operator|.
name|amLivelinessMonitor
return|;
block|}
block|}
end_class

end_unit

