begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|resourcemanager
operator|.
name|scheduler
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_comment
comment|/**  * {@link AbstractUsersManager} tracks users in the system.  */
end_comment

begin_interface
annotation|@
name|Private
DECL|interface|AbstractUsersManager
specifier|public
interface|interface
name|AbstractUsersManager
block|{
comment|/**    * An application has new outstanding requests.    *    * @param user    *          application user    * @param applicationId    *          activated application    */
DECL|method|activateApplication (String user, ApplicationId applicationId)
name|void
name|activateApplication
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
comment|/**    * An application has no more outstanding requests.    *    * @param user    *          application user    * @param applicationId    *          deactivated application    */
DECL|method|deactivateApplication (String user, ApplicationId applicationId)
name|void
name|deactivateApplication
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
comment|/**    * Get number of active users i.e. users with applications which have pending    * resource requests.    *    * @return number of active users    */
DECL|method|getNumActiveUsers ()
name|int
name|getNumActiveUsers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

