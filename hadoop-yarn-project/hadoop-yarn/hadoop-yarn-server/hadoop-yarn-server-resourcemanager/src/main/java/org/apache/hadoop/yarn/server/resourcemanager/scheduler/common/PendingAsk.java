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
name|ResourceSizing
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
comment|/**  * {@link PendingAsk} is the class to include minimal information of how much  * resource to ask under constraints (e.g. on one host / rack / node-attributes)  * , etc.  */
end_comment

begin_class
DECL|class|PendingAsk
specifier|public
class|class
name|PendingAsk
block|{
DECL|field|perAllocationResource
specifier|private
specifier|final
name|Resource
name|perAllocationResource
decl_stmt|;
DECL|field|count
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
DECL|field|ZERO
specifier|public
specifier|final
specifier|static
name|PendingAsk
name|ZERO
init|=
operator|new
name|PendingAsk
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|method|PendingAsk (ResourceSizing sizing)
specifier|public
name|PendingAsk
parameter_list|(
name|ResourceSizing
name|sizing
parameter_list|)
block|{
name|this
operator|.
name|perAllocationResource
operator|=
name|sizing
operator|.
name|getResources
argument_list|()
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|sizing
operator|.
name|getNumAllocations
argument_list|()
expr_stmt|;
block|}
DECL|method|PendingAsk (Resource res, int num)
specifier|public
name|PendingAsk
parameter_list|(
name|Resource
name|res
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|this
operator|.
name|perAllocationResource
operator|=
name|res
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|num
expr_stmt|;
block|}
DECL|method|getPerAllocationResource ()
specifier|public
name|Resource
name|getPerAllocationResource
parameter_list|()
block|{
return|return
name|perAllocationResource
return|;
block|}
DECL|method|getCount ()
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<per-allocation-resource="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getPerAllocationResource
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",repeat="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

