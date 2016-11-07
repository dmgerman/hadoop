begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.placement
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
name|placement
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
name|scheduler
operator|.
name|SchedulerNode
import|;
end_import

begin_class
DECL|class|PlacementSetUtils
specifier|public
class|class
name|PlacementSetUtils
block|{
comment|/*    * If the {@link PlacementSet} only has one entry, return it. otherwise    * return null    */
DECL|method|getSingleNode (PlacementSet<N> ps)
specifier|public
specifier|static
parameter_list|<
name|N
extends|extends
name|SchedulerNode
parameter_list|>
name|N
name|getSingleNode
parameter_list|(
name|PlacementSet
argument_list|<
name|N
argument_list|>
name|ps
parameter_list|)
block|{
name|N
name|node
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|1
operator|==
name|ps
operator|.
name|getAllNodes
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
name|node
operator|=
name|ps
operator|.
name|getAllNodes
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
block|}
end_class

end_unit

