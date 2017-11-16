begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.placement
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
name|placement
package|;
end_package

begin_comment
comment|/**  * Each placement rule when it successfully places an application onto a queue  * returns a PlacementRuleContext which encapsulates the queue the  * application was mapped to and any parent queue for the queue (if configured)  */
end_comment

begin_class
DECL|class|ApplicationPlacementContext
specifier|public
class|class
name|ApplicationPlacementContext
block|{
DECL|field|queue
specifier|private
name|String
name|queue
decl_stmt|;
DECL|field|parentQueue
specifier|private
name|String
name|parentQueue
decl_stmt|;
DECL|method|ApplicationPlacementContext (String queue)
specifier|public
name|ApplicationPlacementContext
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
name|this
argument_list|(
name|queue
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ApplicationPlacementContext (String queue, String parentQueue)
specifier|public
name|ApplicationPlacementContext
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|parentQueue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|parentQueue
operator|=
name|parentQueue
expr_stmt|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
DECL|method|getParentQueue ()
specifier|public
name|String
name|getParentQueue
parameter_list|()
block|{
return|return
name|parentQueue
return|;
block|}
DECL|method|hasParentQueue ()
specifier|public
name|boolean
name|hasParentQueue
parameter_list|()
block|{
return|return
name|parentQueue
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

