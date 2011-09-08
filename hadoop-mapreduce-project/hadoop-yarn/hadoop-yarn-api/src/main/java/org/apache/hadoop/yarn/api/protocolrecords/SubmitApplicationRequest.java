begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
operator|.
name|protocolrecords
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
name|ClientRMProtocol
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
name|Resource
import|;
end_import

begin_comment
comment|/**  *<p>The request sent by a client to<em>submit an application</em> to the   *<code>ResourceManager</code>.</p>  *   *<p>The request, via {@link ApplicationSubmissionContext}, contains  * details such as queue, {@link Resource} required to run the   *<code>ApplicationMaster</code>, the equivalent of   * {@link ContainerLaunchContext} for launching the   *<code>ApplicationMaster</code> etc.  *   * @see ClientRMProtocol#submitApplication(SubmitApplicationRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|SubmitApplicationRequest
specifier|public
interface|interface
name|SubmitApplicationRequest
block|{
comment|/**    * Get the<code>ApplicationSubmissionContext</code> for the application.    * @return<code>ApplicationSubmissionContext</code> for the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationSubmissionContext ()
specifier|public
specifier|abstract
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationSubmissionContext</code> for the application.    * @param context<code>ApplicationSubmissionContext</code> for the     *                application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setApplicationSubmissionContext ( ApplicationSubmissionContext context)
specifier|public
specifier|abstract
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

