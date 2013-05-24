begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery.records
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
name|recovery
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
name|ApplicationSubmissionContext
import|;
end_import

begin_comment
comment|/**  * Contains all the state data that needs to be stored persistently   * for an Application  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Unstable
DECL|interface|ApplicationStateData
specifier|public
interface|interface
name|ApplicationStateData
block|{
comment|/**    * The time at which the application was received by the Resource Manager    * @return submitTime    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setSubmitTime (long submitTime)
specifier|public
name|void
name|setSubmitTime
parameter_list|(
name|long
name|submitTime
parameter_list|)
function_decl|;
comment|/**    * The application submitter    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * The {@link ApplicationSubmissionContext} for the application    * {@link ApplicationId} can be obtained from the this    * @return ApplicationSubmissionContext    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getApplicationSubmissionContext ()
specifier|public
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setApplicationSubmissionContext ( ApplicationSubmissionContext context)
specifier|public
name|void
name|setApplicationSubmissionContext
parameter_list|(
name|ApplicationSubmissionContext
name|context
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

