begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|CancelDelegationTokenRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|CancelDelegationTokenResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetAllApplicationsRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetAllApplicationsResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetApplicationReportRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetApplicationReportResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetClusterMetricsRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetClusterMetricsResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetClusterNodesRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetClusterNodesResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetDelegationTokenRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetDelegationTokenResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetNewApplicationRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetNewApplicationResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetQueueInfoRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetQueueInfoResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetQueueUserAclsInfoRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetQueueUserAclsInfoResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|KillApplicationRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|KillApplicationResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|RenewDelegationTokenRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|RenewDelegationTokenResponse
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
name|api
operator|.
name|protocolrecords
operator|.
name|SubmitApplicationRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|SubmitApplicationResponse
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|api
operator|.
name|records
operator|.
name|ApplicationReport
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
name|api
operator|.
name|records
operator|.
name|ContainerLaunchContext
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
name|api
operator|.
name|records
operator|.
name|NodeReport
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|api
operator|.
name|records
operator|.
name|Token
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
name|api
operator|.
name|records
operator|.
name|YarnClusterMetrics
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

begin_comment
comment|/**  *<p>The protocol between clients and the<code>ResourceManager</code>  * to submit/abort jobs and to get information on applications, cluster metrics,  * nodes, queues and ACLs.</p>   */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ClientRMProtocol
specifier|public
interface|interface
name|ClientRMProtocol
block|{
comment|/**    *<p>The interface used by clients to obtain a new {@link ApplicationId} for     * submitting new applications.</p>    *     *<p>The<code>ResourceManager</code> responds with a new, monotonically    * increasing, {@link ApplicationId} which is used by the client to submit    * a new application.</p>    *    *<p>The<code>ResourceManager</code> also responds with details such     * as minimum and maximum resource capabilities in the cluster as specified in    * {@link GetNewApplicationResponse}.</p>    *    * @param request request to get a new<code>ApplicationId</code>    * @return response containing the new<code>ApplicationId</code> to be used    * to submit an application    * @throws YarnException    * @throws IOException    * @see #submitApplication(SubmitApplicationRequest)    */
DECL|method|getNewApplication ( GetNewApplicationRequest request)
specifier|public
name|GetNewApplicationResponse
name|getNewApplication
parameter_list|(
name|GetNewApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to submit a new application to the    *<code>ResourceManager.</code></p>    *     *<p>The client is required to provide details such as queue,     * {@link Resource} required to run the<code>ApplicationMaster</code>,     * the equivalent of {@link ContainerLaunchContext} for launching    * the<code>ApplicationMaster</code> etc. via the     * {@link SubmitApplicationRequest}.</p>    *     *<p>Currently the<code>ResourceManager</code> sends an immediate (empty)     * {@link SubmitApplicationResponse} on accepting the submission and throws     * an exception if it rejects the submission. However, this call needs to be    * followed by {@link #getApplicationReport(GetApplicationReportRequest)}    * to make sure that the application gets properly submitted.</p>    *     *<p> In secure mode,the<code>ResourceManager</code> verifies access to    * queues etc. before accepting the application submission.</p>    *     * @param request request to submit a new application    * @return (empty) response on accepting the submission    * @throws YarnException    * @throws IOException    * @see #getNewApplication(GetNewApplicationRequest)    */
DECL|method|submitApplication ( SubmitApplicationRequest request)
specifier|public
name|SubmitApplicationResponse
name|submitApplication
parameter_list|(
name|SubmitApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to request the     *<code>ResourceManager</code> to abort submitted application.</p>    *     *<p>The client, via {@link KillApplicationRequest} provides the    * {@link ApplicationId} of the application to be aborted.</p>    *     *<p> In secure mode,the<code>ResourceManager</code> verifies access to the    * application, queue etc. before terminating the application.</p>     *     *<p>Currently, the<code>ResourceManager</code> returns an empty response    * on success and throws an exception on rejecting the request.</p>    *     * @param request request to abort a submited application    * @return<code>ResourceManager</code> returns an empty response    *         on success and throws an exception on rejecting the request    * @throws YarnException    * @throws IOException    * @see #getQueueUserAcls(GetQueueUserAclsInfoRequest)     */
DECL|method|forceKillApplication ( KillApplicationRequest request)
specifier|public
name|KillApplicationResponse
name|forceKillApplication
parameter_list|(
name|KillApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to get a report of an Application from    * the<code>ResourceManager</code>.</p>    *     *<p>The client, via {@link GetApplicationReportRequest} provides the    * {@link ApplicationId} of the application.</p>    *    *<p> In secure mode,the<code>ResourceManager</code> verifies access to the    * application, queue etc. before accepting the request.</p>     *     *<p>The<code>ResourceManager</code> responds with a     * {@link GetApplicationReportResponse} which includes the     * {@link ApplicationReport} for the application.</p>    *     *<p>If the user does not have<code>VIEW_APP</code> access then the    * following fields in the report will be set to stubbed values:    *<ul>    *<li>host - set to "N/A"</li>    *<li>RPC port - set to -1</li>    *<li>client token - set to "N/A"</li>    *<li>diagnostics - set to "N/A"</li>    *<li>tracking URL - set to "N/A"</li>    *<li>original tracking URL - set to "N/A"</li>    *<li>resource usage report - all values are -1</li>    *</ul></p>    *    * @param request request for an application report    * @return application report     * @throws YarnException    * @throws IOException    */
DECL|method|getApplicationReport ( GetApplicationReportRequest request)
specifier|public
name|GetApplicationReportResponse
name|getApplicationReport
parameter_list|(
name|GetApplicationReportRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to get metrics about the cluster from    * the<code>ResourceManager</code>.</p>    *     *<p>The<code>ResourceManager</code> responds with a    * {@link GetClusterMetricsResponse} which includes the     * {@link YarnClusterMetrics} with details such as number of current    * nodes in the cluster.</p>    *     * @param request request for cluster metrics    * @return cluster metrics    * @throws YarnException    * @throws IOException    */
DECL|method|getClusterMetrics ( GetClusterMetricsRequest request)
specifier|public
name|GetClusterMetricsResponse
name|getClusterMetrics
parameter_list|(
name|GetClusterMetricsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to get a report of all Applications    * in the cluster from the<code>ResourceManager</code>.</p>    *     *<p>The<code>ResourceManager</code> responds with a     * {@link GetAllApplicationsResponse} which includes the     * {@link ApplicationReport} for all the applications.</p>    *     *<p>If the user does not have<code>VIEW_APP</code> access for an    * application then the corresponding report will be filtered as    * described in {@link #getApplicationReport(GetApplicationReportRequest)}.    *</p>    *    * @param request request for report on all running applications    * @return report on all running applications    * @throws YarnException    * @throws IOException    */
DECL|method|getAllApplications ( GetAllApplicationsRequest request)
specifier|public
name|GetAllApplicationsResponse
name|getAllApplications
parameter_list|(
name|GetAllApplicationsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to get a report of all nodes    * in the cluster from the<code>ResourceManager</code>.</p>    *     *<p>The<code>ResourceManager</code> responds with a     * {@link GetClusterNodesResponse} which includes the     * {@link NodeReport} for all the nodes in the cluster.</p>    *     * @param request request for report on all nodes    * @return report on all nodes    * @throws YarnException    * @throws IOException    */
DECL|method|getClusterNodes ( GetClusterNodesRequest request)
specifier|public
name|GetClusterNodesResponse
name|getClusterNodes
parameter_list|(
name|GetClusterNodesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to get information about<em>queues</em>    * from the<code>ResourceManager</code>.</p>    *     *<p>The client, via {@link GetQueueInfoRequest}, can ask for details such    * as used/total resources, child queues, running applications etc.</p>    *    *<p> In secure mode,the<code>ResourceManager</code> verifies access before    * providing the information.</p>     *     * @param request request to get queue information    * @return queue information    * @throws YarnException    * @throws IOException    */
DECL|method|getQueueInfo ( GetQueueInfoRequest request)
specifier|public
name|GetQueueInfoResponse
name|getQueueInfo
parameter_list|(
name|GetQueueInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to get information about<em>queue     * acls</em> for<em>current user</em> from the<code>ResourceManager</code>.    *</p>    *     *<p>The<code>ResourceManager</code> responds with queue acls for all    * existing queues.</p>    *     * @param request request to get queue acls for<em>current user</em>    * @return queue acls for<em>current user</em>    * @throws YarnException    * @throws IOException    */
DECL|method|getQueueUserAcls ( GetQueueUserAclsInfoRequest request)
specifier|public
name|GetQueueUserAclsInfoResponse
name|getQueueUserAcls
parameter_list|(
name|GetQueueUserAclsInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by clients to get delegation token, enabling the     * containers to be able to talk to the service using those tokens.    *     *<p> The<code>ResourceManager</code> responds with the delegation token    *  {@link Token} that can be used by the client to speak to this    *  service.    * @param request request to get a delegation token for the client.    * @return delegation token that can be used to talk to this service    * @throws YarnException    * @throws IOException    */
DECL|method|getDelegationToken ( GetDelegationTokenRequest request)
specifier|public
name|GetDelegationTokenResponse
name|getDelegationToken
parameter_list|(
name|GetDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Renew an existing delegation token.    *     * @param request the delegation token to be renewed.    * @return the new expiry time for the delegation token.    * @throws YarnException    * @throws IOException    */
annotation|@
name|Private
DECL|method|renewDelegationToken ( RenewDelegationTokenRequest request)
specifier|public
name|RenewDelegationTokenResponse
name|renewDelegationToken
parameter_list|(
name|RenewDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Cancel an existing delegation token.    *     * @param request the delegation token to be cancelled.    * @return an empty response.    * @throws YarnException    * @throws IOException    */
annotation|@
name|Private
DECL|method|cancelDelegationToken ( CancelDelegationTokenRequest request)
specifier|public
name|CancelDelegationTokenResponse
name|cancelDelegationToken
parameter_list|(
name|CancelDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

