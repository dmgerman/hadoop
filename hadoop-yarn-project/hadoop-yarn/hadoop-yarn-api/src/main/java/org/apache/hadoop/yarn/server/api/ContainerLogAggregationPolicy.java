begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api
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
name|api
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
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  * This API is used by NodeManager to decide if a given container's logs  * should be aggregated at run time.  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Unstable
DECL|interface|ContainerLogAggregationPolicy
specifier|public
interface|interface
name|ContainerLogAggregationPolicy
block|{
comment|/**    *<p>    * The method used by the NodeManager log aggregation service    * to initial the policy object with parameters specified by the application    * or the cluster-wide setting.    *</p>    *    * @param parameters parameters with scheme defined by the policy class.    */
DECL|method|parseParameters (String parameters)
name|void
name|parseParameters
parameter_list|(
name|String
name|parameters
parameter_list|)
function_decl|;
comment|/**    *<p>    * The method used by the NodeManager log aggregation service    * to ask the policy object if a given container's logs should be aggregated.    *</p>    *    * @param logContext ContainerLogContext    * @return Whether or not the container's logs should be aggregated.    */
DECL|method|shouldDoLogAggregation (ContainerLogContext logContext)
name|boolean
name|shouldDoLogAggregation
parameter_list|(
name|ContainerLogContext
name|logContext
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

