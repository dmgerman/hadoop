begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy
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
name|policy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|scheduler
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * An OrderingPolicy which orders SchedulableEntities by input order  */
end_comment

begin_class
DECL|class|FifoOrderingPolicy
specifier|public
class|class
name|FifoOrderingPolicy
parameter_list|<
name|S
extends|extends
name|SchedulableEntity
parameter_list|>
extends|extends
name|AbstractComparatorOrderingPolicy
argument_list|<
name|S
argument_list|>
block|{
DECL|method|FifoOrderingPolicy ()
specifier|public
name|FifoOrderingPolicy
parameter_list|()
block|{
name|this
operator|.
name|comparator
operator|=
operator|new
name|FifoComparator
argument_list|()
expr_stmt|;
name|this
operator|.
name|schedulableEntities
operator|=
operator|new
name|TreeSet
argument_list|<
name|S
argument_list|>
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure (Map<String, String> conf)
specifier|public
name|void
name|configure
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
parameter_list|)
block|{        }
annotation|@
name|Override
DECL|method|containerAllocated (S schedulableEntity, RMContainer r)
specifier|public
name|void
name|containerAllocated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|containerReleased (S schedulableEntity, RMContainer r)
specifier|public
name|void
name|containerReleased
parameter_list|(
name|S
name|schedulableEntity
parameter_list|,
name|RMContainer
name|r
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|demandUpdated (S schedulableEntity)
specifier|public
name|void
name|demandUpdated
parameter_list|(
name|S
name|schedulableEntity
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getInfo ()
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
return|return
literal|"FifoOrderingPolicy"
return|;
block|}
block|}
end_class

end_unit

