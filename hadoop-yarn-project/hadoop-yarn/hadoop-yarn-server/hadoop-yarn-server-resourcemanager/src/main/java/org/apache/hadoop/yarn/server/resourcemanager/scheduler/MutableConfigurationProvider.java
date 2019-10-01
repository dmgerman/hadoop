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
name|security
operator|.
name|UserGroupInformation
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
name|webapp
operator|.
name|dao
operator|.
name|SchedConfUpdateInfo
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

begin_comment
comment|/**  * Interface for allowing changing scheduler configurations.  */
end_comment

begin_interface
DECL|interface|MutableConfigurationProvider
specifier|public
interface|interface
name|MutableConfigurationProvider
block|{
comment|/**    * Get the acl mutation policy for this configuration provider.    * @return The acl mutation policy.    */
DECL|method|getAclMutationPolicy ()
name|ConfigurationMutationACLPolicy
name|getAclMutationPolicy
parameter_list|()
function_decl|;
comment|/**    * Called when a new ResourceManager is starting/becomes active. Ensures    * configuration is up-to-date.    * @throws Exception if configuration could not be refreshed from store    */
DECL|method|reloadConfigurationFromStore ()
name|void
name|reloadConfigurationFromStore
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Log user's requested configuration mutation, and applies it in-memory.    * @param user User who requested the change    * @param confUpdate User's requested configuration change    * @throws Exception if logging the mutation fails    */
DECL|method|logAndApplyMutation (UserGroupInformation user, SchedConfUpdateInfo confUpdate)
name|void
name|logAndApplyMutation
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|SchedConfUpdateInfo
name|confUpdate
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Confirm last logged mutation.    * @param isValid if the last logged mutation is applied to scheduler    *                properly.    * @throws Exception if confirming mutation fails    */
DECL|method|confirmPendingMutation (boolean isValid)
name|void
name|confirmPendingMutation
parameter_list|(
name|boolean
name|isValid
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Returns scheduler configuration cached in this provider.    * @return scheduler configuration.    */
DECL|method|getConfiguration ()
name|Configuration
name|getConfiguration
parameter_list|()
function_decl|;
DECL|method|formatConfigurationInStore (Configuration conf)
name|void
name|formatConfigurationInStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Closes the configuration provider, releasing any required resources.    * @throws IOException on failure to close    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

