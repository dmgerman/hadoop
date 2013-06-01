begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
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
name|List
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
name|YarnRemoteException
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
name|service
operator|.
name|Service
import|;
end_import

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|YarnClient
specifier|public
interface|interface
name|YarnClient
extends|extends
name|Service
block|{
comment|/**    *<p>    * Obtain a new {@link ApplicationId} for submitting new applications.    *</p>    *     *<p>    * Returns a response which contains {@link ApplicationId} that can be used to    * submit a new application. See    * {@link #submitApplication(ApplicationSubmissionContext)}.    *</p>    *     *<p>    * See {@link GetNewApplicationResponse} for other information that is    * returned.    *</p>    *     * @return response containing the new<code>ApplicationId</code> to be used    *         to submit an application    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getNewApplication ()
name|GetNewApplicationResponse
name|getNewApplication
parameter_list|()
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Submit a new application to<code>YARN.</code> It is a blocking call, such    * that it will not return {@link ApplicationId} until the submitted    * application has been submitted and accepted by the ResourceManager.    *</p>    *     * @param appContext    *          {@link ApplicationSubmissionContext} containing all the details    *          needed to submit a new application    * @return {@link ApplicationId} of the accepted application    * @throws YarnRemoteException    * @throws IOException    * @see #getNewApplication()    */
DECL|method|submitApplication (ApplicationSubmissionContext appContext)
name|ApplicationId
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContext
name|appContext
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Kill an application identified by given ID.    *</p>    *     * @param applicationId    *          {@link ApplicationId} of the application that needs to be killed    * @throws YarnRemoteException    *           in case of errors or if YARN rejects the request due to    *           access-control restrictions.    * @throws IOException    * @see #getQueueAclsInfo()    */
DECL|method|killApplication (ApplicationId applicationId)
name|void
name|killApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of the given Application.    *</p>    *     *<p>    * In secure mode,<code>YARN</code> verifies access to the application, queue    * etc. before accepting the request.    *</p>    *     *<p>    * If the user does not have<code>VIEW_APP</code> access then the following    * fields in the report will be set to stubbed values:    *<ul>    *<li>host - set to "N/A"</li>    *<li>RPC port - set to -1</li>    *<li>client token - set to "N/A"</li>    *<li>diagnostics - set to "N/A"</li>    *<li>tracking URL - set to "N/A"</li>    *<li>original tracking URL - set to "N/A"</li>    *<li>resource usage report - all values are -1</li>    *</ul>    *</p>    *     * @param appId    *          {@link ApplicationId} of the application that needs a report    * @return application report    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getApplicationReport (ApplicationId appId)
name|ApplicationReport
name|getApplicationReport
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report (ApplicationReport) of all Applications in the cluster.    *</p>    *     *<p>    * If the user does not have<code>VIEW_APP</code> access for an application    * then the corresponding report will be filtered as described in    * {@link #getApplicationReport(ApplicationId)}.    *</p>    *     * @return a list of reports of all running applications    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getApplicationList ()
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplicationList
parameter_list|()
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get metrics ({@link YarnClusterMetrics}) about the cluster.    *</p>    *     * @return cluster metrics    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getYarnClusterMetrics ()
name|YarnClusterMetrics
name|getYarnClusterMetrics
parameter_list|()
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a report of all nodes ({@link NodeReport}) in the cluster.    *</p>    *     * @return A list of report of all nodes    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getNodeReports ()
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getNodeReports
parameter_list|()
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get a delegation token so as to be able to talk to YARN using those tokens.    *     * @param renewer    *          Address of the renewer who can renew these tokens when needed by    *          securely talking to YARN.    * @return a delegation token ({@link Token}) that can be used to    *         talk to YARN    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getRMDelegationToken (Text renewer)
name|Token
name|getRMDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about a given<em>queue</em>.    *</p>    *     * @param queueName    *          Name of the queue whose information is needed    * @return queue information    * @throws YarnRemoteException    *           in case of errors or if YARN rejects the request due to    *           access-control restrictions.    * @throws IOException    */
DECL|method|getQueueInfo (String queueName)
name|QueueInfo
name|getQueueInfo
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about all queues, recursively if there    * is a hierarchy    *</p>    *     * @return a list of queue-information for all queues    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getAllQueues ()
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getAllQueues
parameter_list|()
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about top level queues.    *</p>    *     * @return a list of queue-information for all the top-level queues    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getRootQueueInfos ()
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getRootQueueInfos
parameter_list|()
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information ({@link QueueInfo}) about all the immediate children queues    * of the given queue    *</p>    *     * @param parent    *          Name of the queue whose child-queues' information is needed    * @return a list of queue-information for all queues who are direct children    *         of the given parent queue.    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getChildQueueInfos (String parent)
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
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * Get information about<em>acls</em> for<em>current user</em> on all the    * existing queues.    *</p>    *     * @return a list of queue acls ({@link QueueUserACLInfo}) for    *<em>current user</em>    * @throws YarnRemoteException    * @throws IOException    */
DECL|method|getQueueAclsInfo ()
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getQueueAclsInfo
parameter_list|()
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

