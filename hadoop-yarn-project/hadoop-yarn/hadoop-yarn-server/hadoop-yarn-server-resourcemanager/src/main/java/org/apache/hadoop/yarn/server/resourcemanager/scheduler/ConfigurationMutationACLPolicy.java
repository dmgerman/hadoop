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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|QueueConfigsUpdateInfo
import|;
end_import

begin_comment
comment|/**  * Interface for determining whether configuration mutations are allowed.  */
end_comment

begin_interface
DECL|interface|ConfigurationMutationACLPolicy
specifier|public
interface|interface
name|ConfigurationMutationACLPolicy
block|{
comment|/**    * Initialize ACL policy with configuration and RMContext.    * @param conf Configuration to initialize with.    * @param rmContext rmContext    */
DECL|method|init (Configuration conf, RMContext rmContext)
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
function_decl|;
comment|/**    * Check if mutation is allowed.    * @param user User issuing the request    * @param confUpdate configurations to be updated    * @return whether provided mutation is allowed or not    */
DECL|method|isMutationAllowed (UserGroupInformation user, QueueConfigsUpdateInfo confUpdate)
name|boolean
name|isMutationAllowed
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|QueueConfigsUpdateInfo
name|confUpdate
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

