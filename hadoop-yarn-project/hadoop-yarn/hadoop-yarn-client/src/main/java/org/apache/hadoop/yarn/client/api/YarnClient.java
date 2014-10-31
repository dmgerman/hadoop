begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|io
operator|.
name|Text
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
name|service
operator|.
name|AbstractService
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
name|ApplicationClientProtocol
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
name|ReservationDeleteRequest
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
name|ReservationDeleteResponse
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
name|ReservationSubmissionRequest
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
name|ReservationSubmissionResponse
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
name|ReservationUpdateRequest
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
name|ReservationUpdateResponse
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
name|records
operator|.
name|ApplicationAttemptId
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
name|ApplicationAttemptReport
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
name|ApplicationSubmissionContext
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
name|ContainerId
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
name|ContainerReport
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
name|NodeId
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
name|NodeState
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
name|QueueInfo
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
name|QueueUserACLInfo
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
name|ReservationDefinition
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
name|ReservationId
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
name|YarnApplicationState
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
name|client
operator|.
name|api
operator|.
name|impl
operator|.
name|YarnClientImpl
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
name|ApplicationIdNotProvidedException
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
name|ApplicationNotFoundException
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
name|security
operator|.
name|AMRMTokenIdentifier
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|YarnClient
specifier|public
specifier|abstract
class|class
name|YarnClient
extends|extends
name|AbstractService
block|{
comment|/**    * Create a new instance of YarnClient.    */
annotation|@
name|Public
DECL|method|createYarnClient ()
specifier|public
specifier|static
name|YarnClient
name|createYarnClient
parameter_list|()
block|{
name|YarnClient
name|client
init|=
operator|new
name|YarnClientImpl
argument_list|()
decl_stmt|;
return|return
name|client
return|;
block|}
annotation|@
name|Private
DECL|method|YarnClient (String name)
specifier|protected
name|YarnClient
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * Obtain a {@link YarnClientApplication} for a new application,    * which in turn contains the {@link ApplicationSubmissionContext} and    * {@link org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse}    * objects.    *</p>    *    * @return {@link YarnClientApplication} built for a new application    * @throws YarnException    * @throws IOException    */
DECL|method|createApplication ()
specifier|public
specifier|abstract
name|YarnClientApplication
name|createApplication
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Submit a new application to<code>YARN.</code> It is a blocking call - it    * will not return {@link ApplicationId} until the submitted application is    * submitted successfully and accepted by the ResourceManager.    *</p>    *     *<p>    * Users should provide an {@link ApplicationId} as part of the parameter    * {@link ApplicationSubmissionContext} when submitting a new application,    * otherwise it will throw the {@link ApplicationIdNotProvidedException}.    *</p>    *    *<p>This internally calls {@link ApplicationClientProtocol#submitApplication    * (SubmitApplicationRequest)}, and after that, it internally invokes    * {@link ApplicationClientProtocol#getApplicationReport    * (GetApplicationReportRequest)} and waits till it can make sure that the    * application gets properly submitted. If RM fails over or RM restart    * happens before ResourceManager saves the application's state,    * {@link ApplicationClientProtocol    * #getApplicationReport(GetApplicationReportRequest)} will throw    * the {@link ApplicationNotFoundException}. This API automatically resubmits    * the application with the same {@link ApplicationSubmissionContext} when it    * catches the {@link ApplicationNotFoundException}</p>    *    * @param appContext    *          {@link ApplicationSubmissionContext} containing all the details    *          needed to submit a new application    * @return {@link ApplicationId} of the accepted application    * @throws YarnException    * @throws IOException    * @see #createApplication()    */
DECL|method|submitApplication ( ApplicationSubmissionContext appContext)
specifier|public
specifier|abstract
name|ApplicationId
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContext
name|appContext
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Kill an application identified by given ID.    *</p>    *     * @param applicationId    *          {@link ApplicationId} of the application that needs to be killed    * @throws YarnException    *           in case of errors or if YARN rejects the request due to    *           access-control restrictions.    * @throws IOException    * @see #getQueueAclsInfo()    */
DECL|method|killApplication (ApplicationId applicationId)
specifier|public
specifier|abstract
name|void
name|killApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of the given Application.    *</p>    *     *<p>    * In secure mode,<code>YARN</code> verifies access to the application, queue    * etc. before accepting the request.    *</p>    *     *<p>    * If the user does not have<code>VIEW_APP</code> access then the following    * fields in the report will be set to stubbed values:    *<ul>    *<li>host - set to "N/A"</li>    *<li>RPC port - set to -1</li>    *<li>client token - set to "N/A"</li>    *<li>diagnostics - set to "N/A"</li>    *<li>tracking URL - set to "N/A"</li>    *<li>original tracking URL - set to "N/A"</li>    *<li>resource usage report - all values are -1</li>    *</ul>    *</p>    *     * @param appId    *          {@link ApplicationId} of the application that needs a report    * @return application report    * @throws YarnException    * @throws IOException    */
DECL|method|getApplicationReport (ApplicationId appId)
specifier|public
specifier|abstract
name|ApplicationReport
name|getApplicationReport
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Get the AMRM token of the application.    *<p/>    * The AMRM token is required for AM to RM scheduling operations. For     * managed Application Masters Yarn takes care of injecting it. For unmanaged    * Applications Masters, the token must be obtained via this method and set    * in the {@link org.apache.hadoop.security.UserGroupInformation} of the    * current user.    *<p/>    * The AMRM token will be returned only if all the following conditions are    * met:    *<li>    *<ul>the requester is the owner of the ApplicationMaster</ul>    *<ul>the application master is an unmanaged ApplicationMaster</ul>    *<ul>the application master is in ACCEPTED state</ul>    *</li>    * Else this method returns NULL.    *    * @param appId {@link ApplicationId} of the application to get the AMRM token    * @return the AMRM token if available    * @throws YarnException    * @throws IOException    */
specifier|public
specifier|abstract
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
DECL|method|getAMRMToken (ApplicationId appId)
name|getAMRMToken
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report (ApplicationReport) of all Applications in the cluster.    *</p>    *    *<p>    * If the user does not have<code>VIEW_APP</code> access for an application    * then the corresponding report will be filtered as described in    * {@link #getApplicationReport(ApplicationId)}.    *</p>    *    * @return a list of reports of all running applications    * @throws YarnException    * @throws IOException    */
DECL|method|getApplications ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report (ApplicationReport) of Applications    * matching the given application types in the cluster.    *</p>    *    *<p>    * If the user does not have<code>VIEW_APP</code> access for an application    * then the corresponding report will be filtered as described in    * {@link #getApplicationReport(ApplicationId)}.    *</p>    *    * @param applicationTypes    * @return a list of reports of applications    * @throws YarnException    * @throws IOException    */
DECL|method|getApplications ( Set<String> applicationTypes)
specifier|public
specifier|abstract
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report (ApplicationReport) of Applications matching the given    * application states in the cluster.    *</p>    *    *<p>    * If the user does not have<code>VIEW_APP</code> access for an application    * then the corresponding report will be filtered as described in    * {@link #getApplicationReport(ApplicationId)}.    *</p>    *    * @param applicationStates    * @return a list of reports of applications    * @throws YarnException    * @throws IOException    */
specifier|public
specifier|abstract
name|List
argument_list|<
name|ApplicationReport
argument_list|>
DECL|method|getApplications (EnumSet<YarnApplicationState> applicationStates)
name|getApplications
parameter_list|(
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|applicationStates
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report (ApplicationReport) of Applications matching the given    * application types and application states in the cluster.    *</p>    *    *<p>    * If the user does not have<code>VIEW_APP</code> access for an application    * then the corresponding report will be filtered as described in    * {@link #getApplicationReport(ApplicationId)}.    *</p>    *    * @param applicationTypes    * @param applicationStates    * @return a list of reports of applications    * @throws YarnException    * @throws IOException    */
DECL|method|getApplications ( Set<String> applicationTypes, EnumSet<YarnApplicationState> applicationStates)
specifier|public
specifier|abstract
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|,
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|applicationStates
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get metrics ({@link YarnClusterMetrics}) about the cluster.    *</p>    *     * @return cluster metrics    * @throws YarnException    * @throws IOException    */
DECL|method|getYarnClusterMetrics ()
specifier|public
specifier|abstract
name|YarnClusterMetrics
name|getYarnClusterMetrics
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of nodes ({@link NodeReport}) in the cluster.    *</p>    *     * @param states The {@link NodeState}s to filter on. If no filter states are    *          given, nodes in all states will be returned.    * @return A list of node reports    * @throws YarnException    * @throws IOException    */
DECL|method|getNodeReports (NodeState... states)
specifier|public
specifier|abstract
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getNodeReports
parameter_list|(
name|NodeState
modifier|...
name|states
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a delegation token so as to be able to talk to YARN using those tokens.    *     * @param renewer    *          Address of the renewer who can renew these tokens when needed by    *          securely talking to YARN.    * @return a delegation token ({@link Token}) that can be used to    *         talk to YARN    * @throws YarnException    * @throws IOException    */
DECL|method|getRMDelegationToken (Text renewer)
specifier|public
specifier|abstract
name|Token
name|getRMDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about a given<em>queue</em>.    *</p>    *     * @param queueName    *          Name of the queue whose information is needed    * @return queue information    * @throws YarnException    *           in case of errors or if YARN rejects the request due to    *           access-control restrictions.    * @throws IOException    */
DECL|method|getQueueInfo (String queueName)
specifier|public
specifier|abstract
name|QueueInfo
name|getQueueInfo
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about all queues, recursively if there    * is a hierarchy    *</p>    *     * @return a list of queue-information for all queues    * @throws YarnException    * @throws IOException    */
DECL|method|getAllQueues ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getAllQueues
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about top level queues.    *</p>    *     * @return a list of queue-information for all the top-level queues    * @throws YarnException    * @throws IOException    */
DECL|method|getRootQueueInfos ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getRootQueueInfos
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about all the immediate children queues    * of the given queue    *</p>    *     * @param parent    *          Name of the queue whose child-queues' information is needed    * @return a list of queue-information for all queues who are direct children    *         of the given parent queue.    * @throws YarnException    * @throws IOException    */
DECL|method|getChildQueueInfos (String parent)
specifier|public
specifier|abstract
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getChildQueueInfos
parameter_list|(
name|String
name|parent
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information about<em>acls</em> for<em>current user</em> on all the    * existing queues.    *</p>    *     * @return a list of queue acls ({@link QueueUserACLInfo}) for    *<em>current user</em>    * @throws YarnException    * @throws IOException    */
DECL|method|getQueueAclsInfo ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getQueueAclsInfo
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of the given ApplicationAttempt.    *</p>    *     *<p>    * In secure mode,<code>YARN</code> verifies access to the application, queue    * etc. before accepting the request.    *</p>    *     * @param applicationAttemptId    *          {@link ApplicationAttemptId} of the application attempt that needs    *          a report    * @return application attempt report    * @throws YarnException    * @throws {@link ApplicationAttemptNotFoundException} if application attempt    *         not found    * @throws IOException    */
DECL|method|getApplicationAttemptReport ( ApplicationAttemptId applicationAttemptId)
specifier|public
specifier|abstract
name|ApplicationAttemptReport
name|getApplicationAttemptReport
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of all (ApplicationAttempts) of Application in the cluster.    *</p>    *     * @param applicationId    * @return a list of reports for all application attempts for specified    *         application.    * @throws YarnException    * @throws IOException    */
DECL|method|getApplicationAttempts ( ApplicationId applicationId)
specifier|public
specifier|abstract
name|List
argument_list|<
name|ApplicationAttemptReport
argument_list|>
name|getApplicationAttempts
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of the given Container.    *</p>    *     *<p>    * In secure mode,<code>YARN</code> verifies access to the application, queue    * etc. before accepting the request.    *</p>    *     * @param containerId    *          {@link ContainerId} of the container that needs a report    * @return container report    * @throws YarnException    * @throws {@link ContainerNotFoundException} if container not found.    * @throws IOException    */
DECL|method|getContainerReport (ContainerId containerId)
specifier|public
specifier|abstract
name|ContainerReport
name|getContainerReport
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of all (Containers) of ApplicationAttempt in the cluster.    *</p>    *     * @param applicationAttemptId    * @return a list of reports of all containers for specified application    *         attempts    * @throws YarnException    * @throws IOException    */
DECL|method|getContainers ( ApplicationAttemptId applicationAttemptId)
specifier|public
specifier|abstract
name|List
argument_list|<
name|ContainerReport
argument_list|>
name|getContainers
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Attempts to move the given application to the given queue.    *</p>    *     * @param appId    *    Application to move.    * @param queue    *    Queue to place it in to.    * @throws YarnException    * @throws IOException    */
DECL|method|moveApplicationAcrossQueues (ApplicationId appId, String queue)
specifier|public
specifier|abstract
name|void
name|moveApplicationAcrossQueues
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The interface used by clients to submit a new reservation to the    * {@code ResourceManager}.    *</p>    *     *<p>    * The client packages all details of its request in a    * {@link ReservationSubmissionRequest} object. This contains information    * about the amount of capacity, temporal constraints, and gang needs.    * Furthermore, the reservation might be composed of multiple stages, with    * ordering dependencies among them.    *</p>    *     *<p>    * In order to respond, a new admission control component in the    * {@code ResourceManager} performs an analysis of the resources that have    * been committed over the period of time the user is requesting, verify that    * the user requests can be fulfilled, and that it respect a sharing policy    * (e.g., {@code CapacityOverTimePolicy}). Once it has positively determined    * that the ReservationRequest is satisfiable the {@code ResourceManager}    * answers with a {@link ReservationSubmissionResponse} that includes a    * {@link ReservationId}. Upon failure to find a valid allocation the response    * is an exception with the message detailing the reason of failure.    *</p>    *     *<p>    * The semantics guarantees that the {@link ReservationId} returned,    * corresponds to a valid reservation existing in the time-range request by    * the user. The amount of capacity dedicated to such reservation can vary    * overtime, depending of the allocation that has been determined. But it is    * guaranteed to satisfy all the constraint expressed by the user in the    * {@link ReservationDefinition}    *</p>    *     * @param request request to submit a new Reservation    * @return response contains the {@link ReservationId} on accepting the    *         submission    * @throws YarnException if the reservation cannot be created successfully    * @throws IOException    *     */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|submitReservation ( ReservationSubmissionRequest request)
specifier|public
specifier|abstract
name|ReservationSubmissionResponse
name|submitReservation
parameter_list|(
name|ReservationSubmissionRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The interface used by clients to update an existing Reservation. This is    * referred to as a re-negotiation process, in which a user that has    * previously submitted a Reservation.    *</p>    *     *<p>    * The allocation is attempted by virtually substituting all previous    * allocations related to this Reservation with new ones, that satisfy the new    * {@link ReservationDefinition}. Upon success the previous allocation is    * atomically substituted by the new one, and on failure (i.e., if the system    * cannot find a valid allocation for the updated request), the previous    * allocation remains valid.    *</p>    *     * @param request to update an existing Reservation (the    *          {@link ReservationUpdateRequest} should refer to an existing valid    *          {@link ReservationId})    * @return response empty on successfully updating the existing reservation    * @throws YarnException if the request is invalid or reservation cannot be    *           updated successfully    * @throws IOException    *     */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|updateReservation ( ReservationUpdateRequest request)
specifier|public
specifier|abstract
name|ReservationUpdateResponse
name|updateReservation
parameter_list|(
name|ReservationUpdateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The interface used by clients to remove an existing Reservation.    *</p>    *     * @param request to remove an existing Reservation (the    *          {@link ReservationDeleteRequest} should refer to an existing valid    *          {@link ReservationId})    * @return response empty on successfully deleting the existing reservation    * @throws YarnException if the request is invalid or reservation cannot be    *           deleted successfully    * @throws IOException    *     */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|deleteReservation ( ReservationDeleteRequest request)
specifier|public
specifier|abstract
name|ReservationDeleteResponse
name|deleteReservation
parameter_list|(
name|ReservationDeleteRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The interface used by client to get node to labels mappings in existing cluster    *</p>    *     * @return node to labels mappings    * @throws YarnException    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getNodeToLabels ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|getNodeToLabels
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The interface used by client to get node labels in the cluster    *</p>    *    * @return cluster node labels collection    * @throws YarnException    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getClusterNodeLabels ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getClusterNodeLabels
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_class

end_unit

