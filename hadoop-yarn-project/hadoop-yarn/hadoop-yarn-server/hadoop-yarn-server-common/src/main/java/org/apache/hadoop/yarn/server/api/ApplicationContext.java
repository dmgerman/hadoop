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
name|Map
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
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_interface
DECL|interface|ApplicationContext
specifier|public
interface|interface
name|ApplicationContext
block|{
comment|/**    * This method returns Application {@link ApplicationReport} for the specified    * {@link ApplicationId}.    *     * @param appId    *     * @return {@link ApplicationReport} for the ApplicationId.    * @throws YarnException    * @throws IOException    */
DECL|method|getApplication (ApplicationId appId)
name|ApplicationReport
name|getApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * This method returns all Application {@link ApplicationReport}s    *     * @return map of {@link ApplicationId} to {@link ApplicationReport}s.    * @throws YarnException    * @throws IOException    */
DECL|method|getAllApplications ()
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationReport
argument_list|>
name|getAllApplications
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * Application can have multiple application attempts    * {@link ApplicationAttemptReport}. This method returns the all    * {@link ApplicationAttemptReport}s for the Application.    *     * @param appId    *     * @return all {@link ApplicationAttemptReport}s for the Application.    * @throws YarnException    * @throws IOException    */
DECL|method|getApplicationAttempts ( ApplicationId appId)
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptReport
argument_list|>
name|getApplicationAttempts
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * This method returns {@link ApplicationAttemptReport} for specified    * {@link ApplicationId}.    *     * @param appAttemptId    *          {@link ApplicationAttemptId}    * @return {@link ApplicationAttemptReport} for ApplicationAttemptId    * @throws YarnException    * @throws IOException    */
DECL|method|getApplicationAttempt ( ApplicationAttemptId appAttemptId)
name|ApplicationAttemptReport
name|getApplicationAttempt
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * This method returns {@link ContainerReport} for specified    * {@link ContainerId}.    *     * @param containerId    *          {@link ContainerId}    * @return {@link ContainerReport} for ContainerId    * @throws YarnException    * @throws IOException    */
DECL|method|getContainer (ContainerId containerId)
name|ContainerReport
name|getContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * This method returns {@link ContainerReport} for specified    * {@link ApplicationAttemptId}.    *     * @param appAttemptId    *          {@link ApplicationAttemptId}    * @return {@link ContainerReport} for ApplicationAttemptId    * @throws YarnException    * @throws IOException    */
DECL|method|getAMContainer (ApplicationAttemptId appAttemptId)
name|ContainerReport
name|getAMContainer
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    * This method returns Map of {@link ContainerId} to {@link ContainerReport}    * for specified {@link ApplicationAttemptId}.    *     * @param appAttemptId    *          {@link ApplicationAttemptId}    * @return Map of {@link ContainerId} to {@link ContainerReport} for    *         ApplicationAttemptId    * @throws YarnException    * @throws IOException    */
DECL|method|getContainers ( ApplicationAttemptId appAttemptId)
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerReport
argument_list|>
name|getContainers
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

