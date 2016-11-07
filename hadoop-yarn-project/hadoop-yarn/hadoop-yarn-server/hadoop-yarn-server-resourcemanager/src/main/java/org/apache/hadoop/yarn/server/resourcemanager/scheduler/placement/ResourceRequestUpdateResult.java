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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
import|;
end_import

begin_comment
comment|/**  * Result of ResourceRequest update  */
end_comment

begin_class
DECL|class|ResourceRequestUpdateResult
specifier|public
class|class
name|ResourceRequestUpdateResult
block|{
DECL|field|lastAnyResourceRequest
specifier|private
specifier|final
name|ResourceRequest
name|lastAnyResourceRequest
decl_stmt|;
DECL|field|newResourceRequest
specifier|private
specifier|final
name|ResourceRequest
name|newResourceRequest
decl_stmt|;
DECL|method|ResourceRequestUpdateResult (ResourceRequest lastAnyResourceRequest, ResourceRequest newResourceRequest)
specifier|public
name|ResourceRequestUpdateResult
parameter_list|(
name|ResourceRequest
name|lastAnyResourceRequest
parameter_list|,
name|ResourceRequest
name|newResourceRequest
parameter_list|)
block|{
name|this
operator|.
name|lastAnyResourceRequest
operator|=
name|lastAnyResourceRequest
expr_stmt|;
name|this
operator|.
name|newResourceRequest
operator|=
name|newResourceRequest
expr_stmt|;
block|}
DECL|method|getLastAnyResourceRequest ()
specifier|public
name|ResourceRequest
name|getLastAnyResourceRequest
parameter_list|()
block|{
return|return
name|lastAnyResourceRequest
return|;
block|}
DECL|method|getNewResourceRequest ()
specifier|public
name|ResourceRequest
name|getNewResourceRequest
parameter_list|()
block|{
return|return
name|newResourceRequest
return|;
block|}
block|}
end_class

end_unit

