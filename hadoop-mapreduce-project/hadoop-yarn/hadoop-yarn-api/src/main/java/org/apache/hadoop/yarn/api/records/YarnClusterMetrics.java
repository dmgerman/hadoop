begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|Stable
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
comment|/**  *<p><code>YarnClusterMetrics</code> represents cluster metrics.</p>  *   *<p>Currently only number of<code>NodeManager</code>s is provided.</p>  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|YarnClusterMetrics
specifier|public
interface|interface
name|YarnClusterMetrics
block|{
comment|/**    * Get the number of<code>NodeManager</code>s in the cluster.    * @return number of<code>NodeManager</code>s in the cluster    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumNodeManagers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumNodeManagers (int numNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumNodeManagers
parameter_list|(
name|int
name|numNodeManagers
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

