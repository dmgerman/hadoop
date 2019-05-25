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
comment|/**  * Used by {@link DecayRpcScheduler} to get the cost of users' operations. This  * is configurable using  * {@link org.apache.hadoop.fs.CommonConfigurationKeys#IPC_COST_PROVIDER_KEY}.  */
end_comment

begin_interface
DECL|interface|CostProvider
specifier|public
interface|interface
name|CostProvider
block|{
comment|/**    * Initialize this provider using the given configuration, examining only    * ones which fall within the provided namespace.    *    * @param namespace The namespace to use when looking up configurations.    * @param conf The configuration    */
DECL|method|init (String namespace, Configuration conf)
name|void
name|init
parameter_list|(
name|String
name|namespace
parameter_list|,
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Get cost from {@link ProcessingDetails} which will be used in scheduler.    *    * @param details Process details    * @return The cost of the call    */
DECL|method|getCost (ProcessingDetails details)
name|long
name|getCost
parameter_list|(
name|ProcessingDetails
name|details
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

