begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.resource
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
name|resource
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
name|conf
operator|.
name|Configuration
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
name|YARNFeatureNotEnabledException
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

begin_comment
comment|/**  * Interface for the resource profiles manager. Provides an interface to get  * the list of available profiles and some helper functions.  */
end_comment

begin_interface
DECL|interface|ResourceProfilesManager
specifier|public
interface|interface
name|ResourceProfilesManager
block|{
comment|/**    * Method to handle all initialization steps for ResourceProfilesManager.    * @param config Configuration object    * @throws IOException when invalid resource profile names are loaded    */
DECL|method|init (Configuration config)
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the resource capability associated with given profile name.    * @param profile name of resource profile    * @return resource capability for given profile    *    * @throws YarnException when any invalid profile name or feature is disabled    */
DECL|method|getProfile (String profile)
name|Resource
name|getProfile
parameter_list|(
name|String
name|profile
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Get all supported resource profiles.    * @return a map of resource objects associated with each profile    *    * @throws YARNFeatureNotEnabledException when feature is disabled    */
DECL|method|getResourceProfiles ()
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|getResourceProfiles
parameter_list|()
throws|throws
name|YARNFeatureNotEnabledException
function_decl|;
comment|/**    * Reload profiles based on updated configuration.    * @throws IOException when invalid resource profile names are loaded    */
DECL|method|reloadProfiles ()
name|void
name|reloadProfiles
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get default supported resource profile.    * @return resource object which is default    * @throws YarnException when any invalid profile name or feature is disabled    */
DECL|method|getDefaultProfile ()
name|Resource
name|getDefaultProfile
parameter_list|()
throws|throws
name|YarnException
function_decl|;
comment|/**    * Get minimum supported resource profile.    * @return resource object which is minimum    * @throws YarnException when any invalid profile name or feature is disabled    */
DECL|method|getMinimumProfile ()
name|Resource
name|getMinimumProfile
parameter_list|()
throws|throws
name|YarnException
function_decl|;
comment|/**    * Get maximum supported resource profile.    * @return resource object which is maximum    * @throws YarnException when any invalid profile name or feature is disabled    */
DECL|method|getMaximumProfile ()
name|Resource
name|getMaximumProfile
parameter_list|()
throws|throws
name|YarnException
function_decl|;
block|}
end_interface

end_unit

