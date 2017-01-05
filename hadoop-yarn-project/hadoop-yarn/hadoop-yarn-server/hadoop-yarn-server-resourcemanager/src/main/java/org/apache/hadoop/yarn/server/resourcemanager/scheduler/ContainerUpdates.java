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
name|UpdateContainerRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Holder class that maintains list of container update requests  */
end_comment

begin_class
DECL|class|ContainerUpdates
specifier|public
class|class
name|ContainerUpdates
block|{
DECL|field|increaseRequests
specifier|final
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|increaseRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|decreaseRequests
specifier|final
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|decreaseRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|promotionRequests
specifier|final
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|promotionRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|demotionRequests
specifier|final
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|demotionRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Returns Container Increase Requests.    * @return Container Increase Requests.    */
DECL|method|getIncreaseRequests ()
specifier|public
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|getIncreaseRequests
parameter_list|()
block|{
return|return
name|increaseRequests
return|;
block|}
comment|/**    * Returns Container Decrease Requests.    * @return Container Decrease Requests.    */
DECL|method|getDecreaseRequests ()
specifier|public
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|getDecreaseRequests
parameter_list|()
block|{
return|return
name|decreaseRequests
return|;
block|}
comment|/**    * Returns Container Promotion Requests.    * @return Container Promotion Requests.    */
DECL|method|getPromotionRequests ()
specifier|public
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|getPromotionRequests
parameter_list|()
block|{
return|return
name|promotionRequests
return|;
block|}
comment|/**    * Returns Container Demotion Requests.    * @return Container Demotion Requests.    */
DECL|method|getDemotionRequests ()
specifier|public
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|getDemotionRequests
parameter_list|()
block|{
return|return
name|demotionRequests
return|;
block|}
block|}
end_class

end_unit

