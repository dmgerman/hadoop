begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records
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
name|federation
operator|.
name|store
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
name|Unstable
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
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>  * The request sent to set the state of a subcluster to either  * SC_DECOMMISSIONED, SC_LOST, or SC_DEREGISTERED.  *  *<p>  * The update includes details such as:  *<ul>  *<li>{@link SubClusterId}</li>  *<li>{@link SubClusterState}</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SubClusterDeregisterRequest
specifier|public
specifier|abstract
class|class
name|SubClusterDeregisterRequest
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( SubClusterId subClusterId, SubClusterState subClusterState)
specifier|public
specifier|static
name|SubClusterDeregisterRequest
name|newInstance
parameter_list|(
name|SubClusterId
name|subClusterId
parameter_list|,
name|SubClusterState
name|subClusterState
parameter_list|)
block|{
name|SubClusterDeregisterRequest
name|registerRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|SubClusterDeregisterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|registerRequest
operator|.
name|setSubClusterId
argument_list|(
name|subClusterId
argument_list|)
expr_stmt|;
name|registerRequest
operator|.
name|setState
argument_list|(
name|subClusterState
argument_list|)
expr_stmt|;
return|return
name|registerRequest
return|;
block|}
comment|/**    * Get the {@link SubClusterId} representing the unique identifier of the    * subcluster.    *    * @return the subcluster identifier    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getSubClusterId ()
specifier|public
specifier|abstract
name|SubClusterId
name|getSubClusterId
parameter_list|()
function_decl|;
comment|/**    * Set the {@link SubClusterId} representing the unique identifier of the    * subcluster.    *    * @param subClusterId the subcluster identifier    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setSubClusterId (SubClusterId subClusterId)
specifier|public
specifier|abstract
name|void
name|setSubClusterId
parameter_list|(
name|SubClusterId
name|subClusterId
parameter_list|)
function_decl|;
comment|/**    * Get the {@link SubClusterState} of the subcluster.    *    * @return the state of the subcluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getState ()
specifier|public
specifier|abstract
name|SubClusterState
name|getState
parameter_list|()
function_decl|;
comment|/**    * Set the {@link SubClusterState} of the subcluster.    *    * @param state the state of the subCluster    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setState (SubClusterState state)
specifier|public
specifier|abstract
name|void
name|setState
parameter_list|(
name|SubClusterState
name|state
parameter_list|)
function_decl|;
block|}
end_class

end_unit

