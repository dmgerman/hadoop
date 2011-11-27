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
name|yarn
operator|.
name|api
operator|.
name|ContainerManager
import|;
end_import

begin_comment
comment|/**  *<p>The response sent by the<code>NodeManager</code> to the   *<code>ApplicationMaster</code> when asked to<em>start</em> an  * allocated container.</p>  *   * @see ContainerManager#startContainer(StartContainerRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|StartContainerResponse
specifier|public
interface|interface
name|StartContainerResponse
block|{
comment|/**    *<p>Get the responses from all auxiliary services running on the     *<code>NodeManager</code>.</p>    *<p>The responses are returned as a Map between the auxiliary service names    * and their corresponding opaque blob<code>ByteBuffer</code>s</p>     * @return a Map between the auxiliary service names and their outputs    */
DECL|method|getAllServiceResponse ()
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|getAllServiceResponse
parameter_list|()
function_decl|;
comment|/**    * Get the response from a single auxiliary service running on the    *<code>NodeManager</code>    *     * @param key The auxiliary service name whose response is desired.    * @return The opaque blob<code>ByteBuffer</code> returned by the auxiliary    * service.    */
DECL|method|getServiceResponse (String key)
name|ByteBuffer
name|getServiceResponse
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Add to the list of auxiliary services which have been started on the    *<code>NodeManager</code>. This is done only once when the    *<code>NodeManager</code> starts up    * @param serviceResponse A map from auxiliary service names to the opaque    * blob<code>ByteBuffer</code>s for that auxiliary service    */
DECL|method|addAllServiceResponse (Map<String, ByteBuffer> serviceResponse)
name|void
name|addAllServiceResponse
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceResponse
parameter_list|)
function_decl|;
comment|/**    * Add to the list of auxiliary services which have been started on the    *<code>NodeManager</code>. This is done only once when the    *<code>NodeManager</code> starts up    *     * @param key The auxiliary service name    * @param value The opaque blob<code>ByteBuffer</code> managed by the    * auxiliary service    */
DECL|method|setServiceResponse (String key, ByteBuffer value)
name|void
name|setServiceResponse
parameter_list|(
name|String
name|key
parameter_list|,
name|ByteBuffer
name|value
parameter_list|)
function_decl|;
comment|/**    * Remove a single auxiliary service from the StartContainerResponse object    * @param key The auxiliary service to remove    */
DECL|method|removeServiceResponse (String key)
name|void
name|removeServiceResponse
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Remove all the auxiliary services from the StartContainerResponse object    */
DECL|method|clearServiceResponse ()
name|void
name|clearServiceResponse
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

