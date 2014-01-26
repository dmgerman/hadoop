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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
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
name|api
operator|.
name|records
operator|.
name|ApplicationAccessType
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
name|Container
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The response sent by the<code>ResourceManager</code> to a new   *<code>ApplicationMaster</code> on registration.</p>  *   *<p>The response contains critical details such as:  *<ul>  *<li>Maximum capability for allocated resources in the cluster.</li>  *<li><code>ApplicationACL</code>s for the application.</li>  *<li>ClientToAMToken master key.</li>  *</ul>  *</p>  *   * @see ApplicationMasterProtocol#registerApplicationMaster(RegisterApplicationMasterRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|RegisterApplicationMasterResponse
specifier|public
specifier|abstract
class|class
name|RegisterApplicationMasterResponse
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance ( Resource minCapability, Resource maxCapability, Map<ApplicationAccessType, String> acls, ByteBuffer key, List<Container> containersFromPreviousAttempt, String queue)
specifier|public
specifier|static
name|RegisterApplicationMasterResponse
name|newInstance
parameter_list|(
name|Resource
name|minCapability
parameter_list|,
name|Resource
name|maxCapability
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|,
name|ByteBuffer
name|key
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|containersFromPreviousAttempt
parameter_list|,
name|String
name|queue
parameter_list|)
block|{
name|RegisterApplicationMasterResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setMaximumResourceCapability
argument_list|(
name|maxCapability
argument_list|)
expr_stmt|;
name|response
operator|.
name|setApplicationACLs
argument_list|(
name|acls
argument_list|)
expr_stmt|;
name|response
operator|.
name|setClientToAMTokenMasterKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContainersFromPreviousAttempt
argument_list|(
name|containersFromPreviousAttempt
argument_list|)
expr_stmt|;
name|response
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get the maximum capability for any {@link Resource} allocated by the     *<code>ResourceManager</code> in the cluster.    * @return maximum capability of allocated resources in the cluster    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMaximumResourceCapability ()
specifier|public
specifier|abstract
name|Resource
name|getMaximumResourceCapability
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setMaximumResourceCapability (Resource capability)
specifier|public
specifier|abstract
name|void
name|setMaximumResourceCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ApplicationACL</code>s for the application.     * @return all the<code>ApplicationACL</code>s    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationACLs ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|getApplicationACLs
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationACL</code>s for the application.     * @param acls    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationACLs (Map<ApplicationAccessType, String> acls)
specifier|public
specifier|abstract
name|void
name|setApplicationACLs
parameter_list|(
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|)
function_decl|;
comment|/**    *<p>Get ClientToAMToken master key.</p>    *<p>The ClientToAMToken master key is sent to<code>ApplicationMaster</code>    * by<code>ResourceManager</code> via {@link RegisterApplicationMasterResponse}    * , used to verify corresponding ClientToAMToken.</p>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getClientToAMTokenMasterKey ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getClientToAMTokenMasterKey
parameter_list|()
function_decl|;
comment|/**    * Set ClientToAMToken master key.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setClientToAMTokenMasterKey (ByteBuffer key)
specifier|public
specifier|abstract
name|void
name|setClientToAMTokenMasterKey
parameter_list|(
name|ByteBuffer
name|key
parameter_list|)
function_decl|;
comment|/**    *<p>Get the queue that the application was placed in.<p>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueue ()
specifier|public
specifier|abstract
name|String
name|getQueue
parameter_list|()
function_decl|;
comment|/**    *<p>Set the queue that the application was placed in.<p>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setQueue (String queue)
specifier|public
specifier|abstract
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
function_decl|;
comment|/**    *<p>    * Get the list of running containers as viewed by    *<code>ResourceManager</code> from previous application attempt.    *</p>    *     * @return the list of running containers as viewed by    *<code>ResourceManager</code> from previous application attempt    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getContainersFromPreviousAttempt ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|Container
argument_list|>
name|getContainersFromPreviousAttempt
parameter_list|()
function_decl|;
comment|/**    * Set the list of running containers as viewed by    *<code>ResourceManager</code> from previous application attempt.    *     * @param containersFromPreviousAttempt    *          the list of running containers as viewed by    *<code>ResourceManager</code> from previous application attempt.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setContainersFromPreviousAttempt ( List<Container> containersFromPreviousAttempt)
specifier|public
specifier|abstract
name|void
name|setContainersFromPreviousAttempt
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containersFromPreviousAttempt
parameter_list|)
function_decl|;
block|}
end_class

end_unit

