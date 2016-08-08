begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store
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
name|federation
operator|.
name|store
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
name|exceptions
operator|.
name|YarnException
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
name|records
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * FederationStore extends the three interfaces used to coordinate the state of  * a federated cluster: {@link FederationApplicationHomeSubClusterStore},  * {@link FederationMembershipStateStore}, and {@link FederationPolicyStore}.  *  */
end_comment

begin_interface
DECL|interface|FederationStateStore
specifier|public
interface|interface
name|FederationStateStore
extends|extends
name|FederationApplicationHomeSubClusterStore
extends|,
name|FederationMembershipStateStore
extends|,
name|FederationPolicyStore
block|{
comment|/**    * Initialize the FederationStore.    *    * @param conf the cluster configuration    * @throws YarnException if initialization fails    */
DECL|method|init (Configuration conf)
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Perform any cleanup operations of the StateStore.    *    * @throws Exception if cleanup fails    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Get the {@link Version} of the underlying federation state store client.    *    * @return the {@link Version} of the underlying federation store client    */
DECL|method|getCurrentVersion ()
name|Version
name|getCurrentVersion
parameter_list|()
function_decl|;
comment|/**    * Load the version information from the federation state store.    *    * @return the {@link Version} of the federation state store    */
DECL|method|loadVersion ()
name|Version
name|loadVersion
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

