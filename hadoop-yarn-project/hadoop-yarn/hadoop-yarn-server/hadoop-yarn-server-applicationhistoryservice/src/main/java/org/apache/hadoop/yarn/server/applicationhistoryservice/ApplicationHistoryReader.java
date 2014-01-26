begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptHistoryData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationHistoryData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ContainerHistoryData
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
name|Unstable
DECL|interface|ApplicationHistoryReader
specifier|public
interface|interface
name|ApplicationHistoryReader
block|{
comment|/**    * This method returns Application {@link ApplicationHistoryData} for the    * specified {@link ApplicationId}.    *     * @param appId    *     * @return {@link ApplicationHistoryData} for the ApplicationId.    * @throws IOException    */
DECL|method|getApplication (ApplicationId appId)
name|ApplicationHistoryData
name|getApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns all Application {@link ApplicationHistoryData}s    *     * @return map of {@link ApplicationId} to {@link ApplicationHistoryData}s.    * @throws IOException    */
DECL|method|getAllApplications ()
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationHistoryData
argument_list|>
name|getAllApplications
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Application can have multiple application attempts    * {@link ApplicationAttemptHistoryData}. This method returns the all    * {@link ApplicationAttemptHistoryData}s for the Application.    *     * @param appId    *     * @return all {@link ApplicationAttemptHistoryData}s for the Application.    * @throws IOException    */
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptHistoryData
argument_list|>
DECL|method|getApplicationAttempts (ApplicationId appId)
name|getApplicationAttempts
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns {@link ApplicationAttemptHistoryData} for specified    * {@link ApplicationId}.    *     * @param appAttemptId    *          {@link ApplicationAttemptId}    * @return {@link ApplicationAttemptHistoryData} for ApplicationAttemptId    * @throws IOException    */
DECL|method|getApplicationAttempt ( ApplicationAttemptId appAttemptId)
name|ApplicationAttemptHistoryData
name|getApplicationAttempt
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns {@link ContainerHistoryData} for specified    * {@link ContainerId}.    *     * @param containerId    *          {@link ContainerId}    * @return {@link ContainerHistoryData} for ContainerId    * @throws IOException    */
DECL|method|getContainer (ContainerId containerId)
name|ContainerHistoryData
name|getContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns {@link ContainerHistoryData} for specified    * {@link ApplicationAttemptId}.    *     * @param appAttemptId    *          {@link ApplicationAttemptId}    * @return {@link ContainerHistoryData} for ApplicationAttemptId    * @throws IOException    */
DECL|method|getAMContainer (ApplicationAttemptId appAttemptId)
name|ContainerHistoryData
name|getAMContainer
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns Map{@link ContainerId} to {@link ContainerHistoryData}    * for specified {@link ApplicationAttemptId}.    *     * @param appAttemptId    *          {@link ApplicationAttemptId}    * @return Map{@link ContainerId} to {@link ContainerHistoryData} for    *         ApplicationAttemptId    * @throws IOException    */
DECL|method|getContainers ( ApplicationAttemptId appAttemptId)
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerHistoryData
argument_list|>
name|getContainers
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

