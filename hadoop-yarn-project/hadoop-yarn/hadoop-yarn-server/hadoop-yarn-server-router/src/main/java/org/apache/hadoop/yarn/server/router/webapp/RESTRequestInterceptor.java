begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
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
name|router
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|conf
operator|.
name|Configurable
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
name|resourcemanager
operator|.
name|webapp
operator|.
name|RMWebServiceProtocol
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
name|webapp
operator|.
name|WebServices
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
name|webapp
operator|.
name|dao
operator|.
name|AppAttemptInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ContainerInfo
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
name|webapp
operator|.
name|dao
operator|.
name|ContainersInfo
import|;
end_import

begin_comment
comment|/**  * Defines the contract to be implemented by the request intercepter classes,  * that can be used to intercept and inspect messages sent from the client to  * the resource manager server.  *  * This class includes 4 methods getAppAttempts, getAppAttempt, getContainers  * and getContainer that belong to {@link WebServices}. They are in this class  * to make sure that RouterWebServices implements the same REST methods of  * {@code RMWebServices}.  */
end_comment

begin_interface
DECL|interface|RESTRequestInterceptor
specifier|public
interface|interface
name|RESTRequestInterceptor
extends|extends
name|RMWebServiceProtocol
extends|,
name|Configurable
block|{
comment|/**    * This method is called for initializing the intercepter. This is guaranteed    * to be called only once in the lifetime of this instance.    *    * @param user the name of the client    */
DECL|method|init (String user)
name|void
name|init
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * This method is called to release the resources held by the intercepter.    * This will be called when the application pipeline is being destroyed. The    * concrete implementations should dispose the resources and forward the    * request to the next intercepter, if any.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|/**    * Sets the next intercepter in the pipeline. The concrete implementation of    * this interface should always pass the request to the nextInterceptor after    * inspecting the message. The last intercepter in the chain is responsible to    * send the messages to the resource manager service and so the last    * intercepter will not receive this method call.    *    * @param nextInterceptor the RESTRequestInterceptor to set in the pipeline    */
DECL|method|setNextInterceptor (RESTRequestInterceptor nextInterceptor)
name|void
name|setNextInterceptor
parameter_list|(
name|RESTRequestInterceptor
name|nextInterceptor
parameter_list|)
function_decl|;
comment|/**    * Returns the next intercepter in the chain.    *    * @return the next intercepter in the chain    */
DECL|method|getNextInterceptor ()
name|RESTRequestInterceptor
name|getNextInterceptor
parameter_list|()
function_decl|;
comment|/**    *    * @see WebServices#getAppAttempt(HttpServletRequest, HttpServletResponse,    *      String, String)    * @param req the servlet request    * @param res the servlet response    * @param appId the application we want to get the appAttempt. It is a    *          PathParam.    * @param appAttemptId the AppAttempt we want to get the info. It is a    *          PathParam.    * @return AppAttemptInfo of the specific AppAttempt    */
DECL|method|getAppAttempt (HttpServletRequest req, HttpServletResponse res, String appId, String appAttemptId)
name|AppAttemptInfo
name|getAppAttempt
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|appAttemptId
parameter_list|)
function_decl|;
comment|/**    *    * @see WebServices#getContainers(HttpServletRequest, HttpServletResponse,    *      String, String)    * @param req the servlet request    * @param res the servlet response    * @param appId the application we want to get the containers info. It is a    *          PathParam.    * @param appAttemptId the AppAttempt we want to get the info. It is a    *          PathParam.    * @return ContainersInfo of all the containers that belong to the specific    *         AppAttempt    */
DECL|method|getContainers (HttpServletRequest req, HttpServletResponse res, String appId, String appAttemptId)
name|ContainersInfo
name|getContainers
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|appAttemptId
parameter_list|)
function_decl|;
comment|/**    *    * @see WebServices#getContainer(HttpServletRequest, HttpServletResponse,    *      String, String, String)    * @param req the servlet request    * @param res the servlet response    * @param appId the application we want to get the containers info. It is a    *          PathParam.    * @param appAttemptId the AppAttempt we want to get the info. It is a    *          PathParam.    * @param containerId the container we want to get the info. It is a    *          PathParam.    * @return ContainerInfo of the specific ContainerId    */
DECL|method|getContainer (HttpServletRequest req, HttpServletResponse res, String appId, String appAttemptId, String containerId)
name|ContainerInfo
name|getContainer
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|appAttemptId
parameter_list|,
name|String
name|containerId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

