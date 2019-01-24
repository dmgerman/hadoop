begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * Represents the localization status of a resource.  * The status of the localization includes:  *<ul>  *<li>resource key</li>  *<li>{@link LocalizationState} of the resource</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|LocalizationStatus
specifier|public
specifier|abstract
class|class
name|LocalizationStatus
block|{
DECL|method|newInstance (String resourceKey, LocalizationState localizationState)
specifier|public
specifier|static
name|LocalizationStatus
name|newInstance
parameter_list|(
name|String
name|resourceKey
parameter_list|,
name|LocalizationState
name|localizationState
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|resourceKey
argument_list|,
name|localizationState
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|newInstance (String resourceKey, LocalizationState localizationState, String diagnostics)
specifier|public
specifier|static
name|LocalizationStatus
name|newInstance
parameter_list|(
name|String
name|resourceKey
parameter_list|,
name|LocalizationState
name|localizationState
parameter_list|,
name|String
name|diagnostics
parameter_list|)
block|{
name|LocalizationStatus
name|status
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|LocalizationStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|.
name|setResourceKey
argument_list|(
name|resourceKey
argument_list|)
expr_stmt|;
name|status
operator|.
name|setLocalizationState
argument_list|(
name|localizationState
argument_list|)
expr_stmt|;
name|status
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
comment|/**    * Get the resource key.    *    * @return resource key.    */
DECL|method|getResourceKey ()
specifier|public
specifier|abstract
name|String
name|getResourceKey
parameter_list|()
function_decl|;
comment|/**    * Sets the resource key.    * @param resourceKey    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setResourceKey (String resourceKey)
specifier|public
specifier|abstract
name|void
name|setResourceKey
parameter_list|(
name|String
name|resourceKey
parameter_list|)
function_decl|;
comment|/**    * Get the localization sate.    *    * @return localization state.    */
DECL|method|getLocalizationState ()
specifier|public
specifier|abstract
name|LocalizationState
name|getLocalizationState
parameter_list|()
function_decl|;
comment|/**    * Sets the localization state.    * @param state localization state    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setLocalizationState (LocalizationState state)
specifier|public
specifier|abstract
name|void
name|setLocalizationState
parameter_list|(
name|LocalizationState
name|state
parameter_list|)
function_decl|;
comment|/**    * Get the diagnostics.    *    * @return diagnostics.    */
DECL|method|getDiagnostics ()
specifier|public
specifier|abstract
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
comment|/**    * Sets the diagnostics.    * @param diagnostics diagnostics.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|method|setDiagnostics (String diagnostics)
specifier|public
specifier|abstract
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
block|}
end_class

end_unit

