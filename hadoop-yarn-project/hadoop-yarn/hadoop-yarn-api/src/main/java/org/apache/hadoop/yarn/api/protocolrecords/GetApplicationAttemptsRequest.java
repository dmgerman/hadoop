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
name|ApplicationHistoryProtocol
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>  * The request from clients to get a list of application attempt reports of an  * application from the<code>ResourceManager</code>.  *</p>  *   * @see ApplicationHistoryProtocol#getApplicationAttempts(GetApplicationAttemptsRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|GetApplicationAttemptsRequest
specifier|public
specifier|abstract
class|class
name|GetApplicationAttemptsRequest
block|{
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance ( ApplicationId applicationId)
specifier|public
specifier|static
name|GetApplicationAttemptsRequest
name|newInstance
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|GetApplicationAttemptsRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationAttemptsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get the<code>ApplicationId</code> of an application    *     * @return<code>ApplicationId</code> of an application    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getApplicationId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationId</code> of an application    *     * @param applicationId    *<code>ApplicationId</code> of an application    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setApplicationId (ApplicationId applicationId)
specifier|public
specifier|abstract
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
block|}
end_class

end_unit

