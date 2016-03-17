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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * Resource limits for queues/applications, this means max overall (please note  * that, it's not "extra") resource you can get.  */
end_comment

begin_class
DECL|class|ResourceLimits
specifier|public
class|class
name|ResourceLimits
block|{
DECL|field|limit
specifier|private
specifier|volatile
name|Resource
name|limit
decl_stmt|;
comment|// This is special limit that goes with the RESERVE_CONT_LOOK_ALL_NODES
comment|// config. This limit indicates how much we need to unreserve to allocate
comment|// another container.
DECL|field|amountNeededUnreserve
specifier|private
specifier|volatile
name|Resource
name|amountNeededUnreserve
decl_stmt|;
comment|// How much resource you can use for next allocation, if this isn't enough for
comment|// next container allocation, you may need to consider unreserve some
comment|// containers.
DECL|field|headroom
specifier|private
specifier|volatile
name|Resource
name|headroom
decl_stmt|;
DECL|method|ResourceLimits (Resource limit)
specifier|public
name|ResourceLimits
parameter_list|(
name|Resource
name|limit
parameter_list|)
block|{
name|this
argument_list|(
name|limit
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ResourceLimits (Resource limit, Resource amountNeededUnreserve)
specifier|public
name|ResourceLimits
parameter_list|(
name|Resource
name|limit
parameter_list|,
name|Resource
name|amountNeededUnreserve
parameter_list|)
block|{
name|this
operator|.
name|amountNeededUnreserve
operator|=
name|amountNeededUnreserve
expr_stmt|;
name|this
operator|.
name|headroom
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
DECL|method|getLimit ()
specifier|public
name|Resource
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|getHeadroom ()
specifier|public
name|Resource
name|getHeadroom
parameter_list|()
block|{
return|return
name|headroom
return|;
block|}
DECL|method|setHeadroom (Resource headroom)
specifier|public
name|void
name|setHeadroom
parameter_list|(
name|Resource
name|headroom
parameter_list|)
block|{
name|this
operator|.
name|headroom
operator|=
name|headroom
expr_stmt|;
block|}
DECL|method|getAmountNeededUnreserve ()
specifier|public
name|Resource
name|getAmountNeededUnreserve
parameter_list|()
block|{
return|return
name|amountNeededUnreserve
return|;
block|}
DECL|method|setLimit (Resource limit)
specifier|public
name|void
name|setLimit
parameter_list|(
name|Resource
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
DECL|method|setAmountNeededUnreserve (Resource amountNeededUnreserve)
specifier|public
name|void
name|setAmountNeededUnreserve
parameter_list|(
name|Resource
name|amountNeededUnreserve
parameter_list|)
block|{
name|this
operator|.
name|amountNeededUnreserve
operator|=
name|amountNeededUnreserve
expr_stmt|;
block|}
block|}
end_class

end_unit

