begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|AddApplicationHomeSubClusterRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|AddApplicationHomeSubClusterResponse
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|DeleteApplicationHomeSubClusterRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|DeleteApplicationHomeSubClusterResponse
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|GetApplicationHomeSubClusterRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|GetApplicationHomeSubClusterResponse
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|GetApplicationsHomeSubClusterRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|GetApplicationsHomeSubClusterResponse
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|UpdateApplicationHomeSubClusterRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|UpdateApplicationHomeSubClusterResponse
import|;
end_import

begin_comment
comment|/**  * FederationApplicationHomeSubClusterStore maintains the state of all  *<em>Applications</em> that have been submitted to the federated cluster.  *  * *  *<p>  * The mapping details contains:  *<ul>  *<li>{@code ApplicationId}</li>  *<li>{@code SubClusterId}</li>  *</ul>  *  */
end_comment

begin_interface
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|FederationApplicationHomeSubClusterStore
specifier|public
interface|interface
name|FederationApplicationHomeSubClusterStore
block|{
comment|/**    * Register the home {@code SubClusterId} of the newly submitted    * {@code ApplicationId}. Currently response is empty if the operation was    * successful, if not an exception reporting reason for a failure.    *    * @param request the request to register a new application with its home    *          sub-cluster    * @return empty on successful registration of the application in the    *         StateStore, if not an exception reporting reason for a failure    * @throws YarnException if the request is invalid/fails    */
DECL|method|addApplicationHomeSubClusterMap ( AddApplicationHomeSubClusterRequest request)
name|AddApplicationHomeSubClusterResponse
name|addApplicationHomeSubClusterMap
parameter_list|(
name|AddApplicationHomeSubClusterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Update the home {@code SubClusterId} of a previously submitted    * {@code ApplicationId}. Currently response is empty if the operation was    * successful, if not an exception reporting reason for a failure.    *    * @param request the request to update the home sub-cluster of an    *          application.    * @return empty on successful update of the application in the StateStore, if    *         not an exception reporting reason for a failure    * @throws YarnException if the request is invalid/fails    */
DECL|method|updateApplicationHomeSubClusterMap ( UpdateApplicationHomeSubClusterRequest request)
name|UpdateApplicationHomeSubClusterResponse
name|updateApplicationHomeSubClusterMap
parameter_list|(
name|UpdateApplicationHomeSubClusterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Get information about the application identified by the input    * {@code ApplicationId}.    *    * @param request contains the application queried    * @return {@code ApplicationHomeSubCluster} containing the application's home    *         subcluster    * @throws YarnException if the request is invalid/fails    */
DECL|method|getApplicationHomeSubClusterMap ( GetApplicationHomeSubClusterRequest request)
name|GetApplicationHomeSubClusterResponse
name|getApplicationHomeSubClusterMap
parameter_list|(
name|GetApplicationHomeSubClusterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Get the {@code ApplicationHomeSubCluster} list representing the mapping of    * all submitted applications to it's home sub-cluster.    *    * @param request empty representing all applications    * @return the mapping of all submitted application to it's home sub-cluster    * @throws YarnException if the request is invalid/fails    */
DECL|method|getApplicationsHomeSubClusterMap ( GetApplicationsHomeSubClusterRequest request)
name|GetApplicationsHomeSubClusterResponse
name|getApplicationsHomeSubClusterMap
parameter_list|(
name|GetApplicationsHomeSubClusterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Delete the mapping of home {@code SubClusterId} of a previously submitted    * {@code ApplicationId}. Currently response is empty if the operation was    * successful, if not an exception reporting reason for a failure.    *    * @param request the request to delete the home sub-cluster of an    *          application.    * @return empty on successful update of the application in the StateStore, if    *         not an exception reporting reason for a failure    * @throws YarnException if the request is invalid/fails    */
DECL|method|deleteApplicationHomeSubClusterMap ( DeleteApplicationHomeSubClusterRequest request)
name|DeleteApplicationHomeSubClusterResponse
name|deleteApplicationHomeSubClusterMap
parameter_list|(
name|DeleteApplicationHomeSubClusterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

