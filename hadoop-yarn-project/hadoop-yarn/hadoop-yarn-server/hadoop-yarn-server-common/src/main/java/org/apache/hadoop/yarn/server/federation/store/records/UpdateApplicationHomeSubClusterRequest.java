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
comment|/**  *<p>  * The request sent by the<code>Router</code> to  *<code>Federation state store</code> to update the home subcluster of a newly  * submitted application.  *  *<p>  * The request includes the mapping details, i.e.:  *<ul>  *<li>{@code ApplicationId}</li>  *<li>{@code SubClusterId}</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|UpdateApplicationHomeSubClusterRequest
specifier|public
specifier|abstract
class|class
name|UpdateApplicationHomeSubClusterRequest
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( ApplicationHomeSubCluster applicationHomeSubCluster)
specifier|public
specifier|static
name|UpdateApplicationHomeSubClusterRequest
name|newInstance
parameter_list|(
name|ApplicationHomeSubCluster
name|applicationHomeSubCluster
parameter_list|)
block|{
name|UpdateApplicationHomeSubClusterRequest
name|updateApplicationRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|UpdateApplicationHomeSubClusterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|updateApplicationRequest
operator|.
name|setApplicationHomeSubCluster
argument_list|(
name|applicationHomeSubCluster
argument_list|)
expr_stmt|;
return|return
name|updateApplicationRequest
return|;
block|}
comment|/**    * Get the {@link ApplicationHomeSubCluster} representing the mapping of the    * application to it's home sub-cluster.    *    * @return the mapping of the application to it's home sub-cluster.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getApplicationHomeSubCluster ()
specifier|public
specifier|abstract
name|ApplicationHomeSubCluster
name|getApplicationHomeSubCluster
parameter_list|()
function_decl|;
comment|/**    * Set the {@link ApplicationHomeSubCluster} representing the mapping of the    * application to it's home sub-cluster.    *    * @param applicationHomeSubCluster the mapping of the application to it's    *          home sub-cluster.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationHomeSubCluster ( ApplicationHomeSubCluster applicationHomeSubCluster)
specifier|public
specifier|abstract
name|void
name|setApplicationHomeSubCluster
parameter_list|(
name|ApplicationHomeSubCluster
name|applicationHomeSubCluster
parameter_list|)
function_decl|;
block|}
end_class

end_unit

