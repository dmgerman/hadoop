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
comment|/**    * Set to the list of auxiliary services which have been started on the    *<code>NodeManager</code>. This is done only once when the    *<code>NodeManager</code> starts up    * @param serviceResponses A map from auxiliary service names to the opaque    * blob<code>ByteBuffer</code>s for that auxiliary service    */
DECL|method|setAllServiceResponse (Map<String, ByteBuffer> serviceResponses)
name|void
name|setAllServiceResponse
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceResponses
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

