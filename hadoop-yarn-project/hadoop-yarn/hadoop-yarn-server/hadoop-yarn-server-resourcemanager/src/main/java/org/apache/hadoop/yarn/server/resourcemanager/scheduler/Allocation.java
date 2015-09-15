begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|Set
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
name|NMToken
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
name|Resource
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
name|ResourceRequest
import|;
end_import

begin_class
DECL|class|Allocation
specifier|public
class|class
name|Allocation
block|{
DECL|field|containers
specifier|final
name|List
argument_list|<
name|Container
argument_list|>
name|containers
decl_stmt|;
DECL|field|resourceLimit
specifier|final
name|Resource
name|resourceLimit
decl_stmt|;
DECL|field|strictContainers
specifier|final
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|strictContainers
decl_stmt|;
DECL|field|fungibleContainers
specifier|final
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|fungibleContainers
decl_stmt|;
DECL|field|fungibleResources
specifier|final
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|fungibleResources
decl_stmt|;
DECL|field|nmTokens
specifier|final
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
decl_stmt|;
DECL|field|increasedContainers
specifier|final
name|List
argument_list|<
name|Container
argument_list|>
name|increasedContainers
decl_stmt|;
DECL|field|decreasedContainers
specifier|final
name|List
argument_list|<
name|Container
argument_list|>
name|decreasedContainers
decl_stmt|;
DECL|method|Allocation (List<Container> containers, Resource resourceLimit, Set<ContainerId> strictContainers, Set<ContainerId> fungibleContainers, List<ResourceRequest> fungibleResources)
specifier|public
name|Allocation
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|,
name|Resource
name|resourceLimit
parameter_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|strictContainers
parameter_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|fungibleContainers
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|fungibleResources
parameter_list|)
block|{
name|this
argument_list|(
name|containers
argument_list|,
name|resourceLimit
argument_list|,
name|strictContainers
argument_list|,
name|fungibleContainers
argument_list|,
name|fungibleResources
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Allocation (List<Container> containers, Resource resourceLimit, Set<ContainerId> strictContainers, Set<ContainerId> fungibleContainers, List<ResourceRequest> fungibleResources, List<NMToken> nmTokens)
specifier|public
name|Allocation
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|,
name|Resource
name|resourceLimit
parameter_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|strictContainers
parameter_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|fungibleContainers
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|fungibleResources
parameter_list|,
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|)
block|{
name|this
argument_list|(
name|containers
argument_list|,
name|resourceLimit
argument_list|,
name|strictContainers
argument_list|,
name|fungibleContainers
argument_list|,
name|fungibleResources
argument_list|,
name|nmTokens
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Allocation (List<Container> containers, Resource resourceLimit, Set<ContainerId> strictContainers, Set<ContainerId> fungibleContainers, List<ResourceRequest> fungibleResources, List<NMToken> nmTokens, List<Container> increasedContainers, List<Container> decreasedContainer)
specifier|public
name|Allocation
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|,
name|Resource
name|resourceLimit
parameter_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|strictContainers
parameter_list|,
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|fungibleContainers
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|fungibleResources
parameter_list|,
name|List
argument_list|<
name|NMToken
argument_list|>
name|nmTokens
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|increasedContainers
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|decreasedContainer
parameter_list|)
block|{
name|this
operator|.
name|containers
operator|=
name|containers
expr_stmt|;
name|this
operator|.
name|resourceLimit
operator|=
name|resourceLimit
expr_stmt|;
name|this
operator|.
name|strictContainers
operator|=
name|strictContainers
expr_stmt|;
name|this
operator|.
name|fungibleContainers
operator|=
name|fungibleContainers
expr_stmt|;
name|this
operator|.
name|fungibleResources
operator|=
name|fungibleResources
expr_stmt|;
name|this
operator|.
name|nmTokens
operator|=
name|nmTokens
expr_stmt|;
name|this
operator|.
name|increasedContainers
operator|=
name|increasedContainers
expr_stmt|;
name|this
operator|.
name|decreasedContainers
operator|=
name|decreasedContainer
expr_stmt|;
block|}
DECL|method|getContainers ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getContainers
parameter_list|()
block|{
return|return
name|containers
return|;
block|}
DECL|method|getResourceLimit ()
specifier|public
name|Resource
name|getResourceLimit
parameter_list|()
block|{
return|return
name|resourceLimit
return|;
block|}
DECL|method|getStrictContainerPreemptions ()
specifier|public
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|getStrictContainerPreemptions
parameter_list|()
block|{
return|return
name|strictContainers
return|;
block|}
DECL|method|getContainerPreemptions ()
specifier|public
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|getContainerPreemptions
parameter_list|()
block|{
return|return
name|fungibleContainers
return|;
block|}
DECL|method|getResourcePreemptions ()
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getResourcePreemptions
parameter_list|()
block|{
return|return
name|fungibleResources
return|;
block|}
DECL|method|getNMTokens ()
specifier|public
name|List
argument_list|<
name|NMToken
argument_list|>
name|getNMTokens
parameter_list|()
block|{
return|return
name|nmTokens
return|;
block|}
DECL|method|getIncreasedContainers ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getIncreasedContainers
parameter_list|()
block|{
return|return
name|increasedContainers
return|;
block|}
DECL|method|getDecreasedContainers ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getDecreasedContainers
parameter_list|()
block|{
return|return
name|decreasedContainers
return|;
block|}
block|}
end_class

end_unit

