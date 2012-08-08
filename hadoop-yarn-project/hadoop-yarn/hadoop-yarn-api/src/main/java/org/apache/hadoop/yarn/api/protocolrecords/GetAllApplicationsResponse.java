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
name|ApplicationReport
import|;
end_import

begin_comment
comment|/**  *<p>The response sent by the<code>ResourceManager</code> to a client  * requesting an {@link ApplicationReport} for all applications.</p>  *   *<p>The<code>ApplicationReport</code> for each application includes details   * such as user, queue, name, host on which the<code>ApplicationMaster</code>   * is running, RPC port, tracking URL, diagnostics, start time etc.</p>  *   * @see ApplicationReport  * @see ClientRMProtocol#getAllApplications(GetAllApplicationsRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|GetAllApplicationsResponse
specifier|public
interface|interface
name|GetAllApplicationsResponse
block|{
comment|/**    * Get<code>ApplicationReport</code> for all applications.    * @return<code>ApplicationReport</code> for all applications    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationList ()
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplicationList
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationList (List<ApplicationReport> applications)
name|void
name|setApplicationList
parameter_list|(
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

