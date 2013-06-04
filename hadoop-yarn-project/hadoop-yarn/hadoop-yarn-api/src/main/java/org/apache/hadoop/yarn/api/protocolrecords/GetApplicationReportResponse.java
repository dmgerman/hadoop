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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The response sent by the<code>ResourceManager</code> to a client  * requesting an application report.</p>  *   *<p>The response includes an {@link ApplicationReport} which has details such   * as user, queue, name, host on which the<code>ApplicationMaster</code> is   * running, RPC port, tracking URL, diagnostics, start time etc.</p>  *   * @see ClientRMProtocol#getApplicationReport(GetApplicationReportRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|GetApplicationReportResponse
specifier|public
specifier|abstract
class|class
name|GetApplicationReportResponse
block|{
DECL|method|newInstance ( ApplicationReport ApplicationReport)
specifier|public
specifier|static
name|GetApplicationReportResponse
name|newInstance
parameter_list|(
name|ApplicationReport
name|ApplicationReport
parameter_list|)
block|{
name|GetApplicationReportResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setApplicationReport
argument_list|(
name|ApplicationReport
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get the<code>ApplicationReport</code> for the application.    * @return<code>ApplicationReport</code> for the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationReport ()
specifier|public
specifier|abstract
name|ApplicationReport
name|getApplicationReport
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationReport (ApplicationReport ApplicationReport)
specifier|public
specifier|abstract
name|void
name|setApplicationReport
parameter_list|(
name|ApplicationReport
name|ApplicationReport
parameter_list|)
function_decl|;
block|}
end_class

end_unit

