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
comment|/**  * AddApplicationHomeSubClusterResponse contains the answer from the  * {@code FederationApplicationHomeSubClusterStore} to a request to insert a  * newly generated applicationId and its owner.  *  * The response contains application's home sub-cluster as it is stored in the  * {@code FederationApplicationHomeSubClusterStore}. If a mapping for the  * application already existed, the {@code SubClusterId} in this response will  * return the existing mapping which might be different from that in the  * {@code AddApplicationHomeSubClusterRequest}.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AddApplicationHomeSubClusterResponse
specifier|public
specifier|abstract
class|class
name|AddApplicationHomeSubClusterResponse
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( SubClusterId homeSubCluster)
specifier|public
specifier|static
name|AddApplicationHomeSubClusterResponse
name|newInstance
parameter_list|(
name|SubClusterId
name|homeSubCluster
parameter_list|)
block|{
name|AddApplicationHomeSubClusterResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AddApplicationHomeSubClusterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setHomeSubCluster
argument_list|(
name|homeSubCluster
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Set the home sub-cluster that this application has been assigned to.    *    * @param homeSubCluster the {@link SubClusterId} of this application's home    *          sub-cluster    */
DECL|method|setHomeSubCluster (SubClusterId homeSubCluster)
specifier|public
specifier|abstract
name|void
name|setHomeSubCluster
parameter_list|(
name|SubClusterId
name|homeSubCluster
parameter_list|)
function_decl|;
comment|/**    * Get the home sub-cluster that this application has been assigned to. This    * may not match the {@link SubClusterId} in the corresponding response, if    * the mapping for the request's application already existed.    *    * @return the {@link SubClusterId} of this application's home sub-cluster    */
DECL|method|getHomeSubCluster ()
specifier|public
specifier|abstract
name|SubClusterId
name|getHomeSubCluster
parameter_list|()
function_decl|;
block|}
end_class

end_unit

