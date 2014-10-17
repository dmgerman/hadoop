begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
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
package|;
end_package

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
name|io
operator|.
name|retry
operator|.
name|AtMostOnce
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
name|io
operator|.
name|retry
operator|.
name|Idempotent
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|ResourceRequest
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
name|conf
operator|.
name|YarnConfiguration
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
name|InvalidApplicationMasterRequestException
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
name|InvalidResourceBlacklistRequestException
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
name|InvalidResourceRequestException
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

begin_comment
comment|/**  *<p>The protocol between a live instance of<code>ApplicationMaster</code>   * and the<code>ResourceManager</code>.</p>  *   *<p>This is used by the<code>ApplicationMaster</code> to register/unregister  * and to request and obtain resources in the cluster from the  *<code>ResourceManager</code>.</p>  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ApplicationMasterProtocol
specifier|public
interface|interface
name|ApplicationMasterProtocol
block|{
comment|/**    *<p>    * The interface used by a new<code>ApplicationMaster</code> to register with    * the<code>ResourceManager</code>.    *</p>    *     *<p>    * The<code>ApplicationMaster</code> needs to provide details such as RPC    * Port, HTTP tracking url etc. as specified in    * {@link RegisterApplicationMasterRequest}.    *</p>    *     *<p>    * The<code>ResourceManager</code> responds with critical details such as    * maximum resource capabilities in the cluster as specified in    * {@link RegisterApplicationMasterResponse}.    *</p>    *     * @param request    *          registration request    * @return registration respose    * @throws YarnException    * @throws IOException    * @throws InvalidApplicationMasterRequestException    *           The exception is thrown when an ApplicationMaster tries to    *           register more then once.    * @see RegisterApplicationMasterRequest    * @see RegisterApplicationMasterResponse    */
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|Idempotent
DECL|method|registerApplicationMaster ( RegisterApplicationMasterRequest request)
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by an<code>ApplicationMaster</code> to notify the     *<code>ResourceManager</code> about its completion (success or failed).</p>    *     *<p>The<code>ApplicationMaster</code> has to provide details such as     * final state, diagnostics (in case of failures) etc. as specified in     * {@link FinishApplicationMasterRequest}.</p>    *     *<p>The<code>ResourceManager</code> responds with     * {@link FinishApplicationMasterResponse}.</p>    *     * @param request completion request    * @return completion response    * @throws YarnException    * @throws IOException    * @see FinishApplicationMasterRequest    * @see FinishApplicationMasterResponse    */
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|AtMostOnce
DECL|method|finishApplicationMaster ( FinishApplicationMasterRequest request)
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>    * The main interface between an<code>ApplicationMaster</code> and the    *<code>ResourceManager</code>.    *</p>    *     *<p>    * The<code>ApplicationMaster</code> uses this interface to provide a list of    * {@link ResourceRequest} and returns unused {@link Container} allocated to    * it via {@link AllocateRequest}. Optionally, the    *<code>ApplicationMaster</code> can also<em>blacklist</em> resources which    * it doesn't want to use.    *</p>    *     *<p>    * This also doubles up as a<em>heartbeat</em> to let the    *<code>ResourceManager</code> know that the<code>ApplicationMaster</code>    * is alive. Thus, applications should periodically make this call to be kept    * alive. The frequency depends on    * {@link YarnConfiguration#RM_AM_EXPIRY_INTERVAL_MS} which defaults to    * {@link YarnConfiguration#DEFAULT_RM_AM_EXPIRY_INTERVAL_MS}.    *</p>    *     *<p>    * The<code>ResourceManager</code> responds with list of allocated    * {@link Container}, status of completed containers and headroom information    * for the application.    *</p>    *     *<p>    * The<code>ApplicationMaster</code> can use the available headroom    * (resources) to decide how to utilized allocated resources and make informed    * decisions about future resource requests.    *</p>    *     * @param request    *          allocation request    * @return allocation response    * @throws YarnException    * @throws IOException    * @throws InvalidApplicationMasterRequestException    *           This exception is thrown when an ApplicationMaster calls allocate    *           without registering first.    * @throws InvalidResourceBlacklistRequestException    *           This exception is thrown when an application provides an invalid    *           specification for blacklist of resources.    * @throws InvalidResourceRequestException    *           This exception is thrown when a {@link ResourceRequest} is out of    *           the range of the configured lower and upper limits on the    *           resources.    * @see AllocateRequest    * @see AllocateResponse    */
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|AtMostOnce
DECL|method|allocate (AllocateRequest request)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

