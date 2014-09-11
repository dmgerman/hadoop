begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
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
name|rmapp
operator|.
name|attempt
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
import|;
end_import

begin_class
annotation|@
name|Private
DECL|class|AggregateAppResourceUsage
specifier|public
class|class
name|AggregateAppResourceUsage
block|{
DECL|field|memorySeconds
name|long
name|memorySeconds
decl_stmt|;
DECL|field|vcoreSeconds
name|long
name|vcoreSeconds
decl_stmt|;
DECL|method|AggregateAppResourceUsage (long memorySeconds, long vcoreSeconds)
specifier|public
name|AggregateAppResourceUsage
parameter_list|(
name|long
name|memorySeconds
parameter_list|,
name|long
name|vcoreSeconds
parameter_list|)
block|{
name|this
operator|.
name|memorySeconds
operator|=
name|memorySeconds
expr_stmt|;
name|this
operator|.
name|vcoreSeconds
operator|=
name|vcoreSeconds
expr_stmt|;
block|}
comment|/**    * @return the memorySeconds    */
DECL|method|getMemorySeconds ()
specifier|public
name|long
name|getMemorySeconds
parameter_list|()
block|{
return|return
name|memorySeconds
return|;
block|}
comment|/**    * @param memorySeconds the memorySeconds to set    */
DECL|method|setMemorySeconds (long memorySeconds)
specifier|public
name|void
name|setMemorySeconds
parameter_list|(
name|long
name|memorySeconds
parameter_list|)
block|{
name|this
operator|.
name|memorySeconds
operator|=
name|memorySeconds
expr_stmt|;
block|}
comment|/**    * @return the vcoreSeconds    */
DECL|method|getVcoreSeconds ()
specifier|public
name|long
name|getVcoreSeconds
parameter_list|()
block|{
return|return
name|vcoreSeconds
return|;
block|}
comment|/**    * @param vcoreSeconds the vcoreSeconds to set    */
DECL|method|setVcoreSeconds (long vcoreSeconds)
specifier|public
name|void
name|setVcoreSeconds
parameter_list|(
name|long
name|vcoreSeconds
parameter_list|)
block|{
name|this
operator|.
name|vcoreSeconds
operator|=
name|vcoreSeconds
expr_stmt|;
block|}
block|}
end_class

end_unit

