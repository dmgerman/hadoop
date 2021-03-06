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
name|ApplicationMasterProtocol
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
comment|/**  * The response sent by the<code>ResourceManager</code> to a  *<code>ApplicationMaster</code> on it's completion.  *<p>  * The response, includes:  *<ul>  *<li>A flag which indicates that the application has successfully unregistered  * with the RM and the application can safely stop.</li>  *</ul>  *<p>  * Note: The flag indicates whether the application has successfully  * unregistered and is safe to stop. The application may stop after the flag is  * true. If the application stops before the flag is true then the RM may retry  * the application.  *   * @see ApplicationMasterProtocol#finishApplicationMaster(FinishApplicationMasterRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|FinishApplicationMasterResponse
specifier|public
specifier|abstract
class|class
name|FinishApplicationMasterResponse
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( boolean isRemovedFromRMStateStore)
specifier|public
specifier|static
name|FinishApplicationMasterResponse
name|newInstance
parameter_list|(
name|boolean
name|isRemovedFromRMStateStore
parameter_list|)
block|{
name|FinishApplicationMasterResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|FinishApplicationMasterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setIsUnregistered
argument_list|(
name|isRemovedFromRMStateStore
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get the flag which indicates that the application has successfully    * unregistered with the RM and the application can safely stop.    * @return true if the application has unregistered with the RM,    *         false otherwise    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getIsUnregistered ()
specifier|public
specifier|abstract
name|boolean
name|getIsUnregistered
parameter_list|()
function_decl|;
comment|/**    * Set the flag which indicates that the application has successfully    * unregistered with the RM and the application can safely stop.    * @param isUnregistered boolean flag to indicate that the application has    *        successfully unregistered with the RM    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setIsUnregistered (boolean isUnregistered)
specifier|public
specifier|abstract
name|void
name|setIsUnregistered
parameter_list|(
name|boolean
name|isUnregistered
parameter_list|)
function_decl|;
block|}
end_class

end_unit

