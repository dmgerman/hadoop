begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Ignores process details and returns a constant value for each call.  */
end_comment

begin_class
DECL|class|DefaultCostProvider
specifier|public
class|class
name|DefaultCostProvider
implements|implements
name|CostProvider
block|{
annotation|@
name|Override
DECL|method|init (String namespace, Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|namespace
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
comment|// No-op
block|}
comment|/**    * Returns 1, regardless of the processing details.    *    * @param details Process details (ignored)    * @return 1    */
annotation|@
name|Override
DECL|method|getCost (ProcessingDetails details)
specifier|public
name|long
name|getCost
parameter_list|(
name|ProcessingDetails
name|details
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

