begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  *<p><code>LocalResource</code> represents a local resource required to  * run a container.</p>  *   *<p>The<code>NodeManager</code> is responsible for localizing the resource   * prior to launching the container.</p>  *   *<p>Applications can specify {@link LocalResourceType} and   * {@link LocalResourceVisibility}.</p>  *   * @see LocalResourceType  * @see LocalResourceVisibility  * @see ContainerLaunchContext  * @see ApplicationSubmissionContext  * @see ContainerManager#startContainer(org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|LocalResource
specifier|public
interface|interface
name|LocalResource
block|{
comment|/**    * Get the<em>location</em> of the resource to be localized.    * @return<em>location</em> of the resource to be localized    */
DECL|method|getResource ()
specifier|public
name|URL
name|getResource
parameter_list|()
function_decl|;
comment|/**    * Set<em>location</em> of the resource to be localized.    * @param resource<em>location</em> of the resource to be localized    */
DECL|method|setResource (URL resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|URL
name|resource
parameter_list|)
function_decl|;
comment|/**    * Get the<em>size</em> of the resource to be localized.    * @return<em>size</em> of the resource to be localized    */
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
function_decl|;
comment|/**    * Set the<em>size</em> of the resource to be localized.    * @param size<em>size</em> of the resource to be localized    */
DECL|method|setSize (long size)
specifier|public
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
function_decl|;
comment|/**    * Get the original<em>timestamp</em> of the resource to be localized, used    * for verification.    * @return<em>timestamp</em> of the resource to be localized    */
DECL|method|getTimestamp ()
specifier|public
name|long
name|getTimestamp
parameter_list|()
function_decl|;
comment|/**    * Set the<em>timestamp</em> of the resource to be localized, used    * for verification.    * @param timestamp<em>timestamp</em> of the resource to be localized    */
DECL|method|setTimestamp (long timestamp)
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
function_decl|;
comment|/**    * Get the<code>LocalResourceType</code> of the resource to be localized.    * @return<code>LocalResourceType</code> of the resource to be localized    */
DECL|method|getType ()
specifier|public
name|LocalResourceType
name|getType
parameter_list|()
function_decl|;
comment|/**    * Set the<code>LocalResourceType</code> of the resource to be localized.    * @param type<code>LocalResourceType</code> of the resource to be localized    */
DECL|method|setType (LocalResourceType type)
specifier|public
name|void
name|setType
parameter_list|(
name|LocalResourceType
name|type
parameter_list|)
function_decl|;
comment|/**    * Get the<code>LocalResourceVisibility</code> of the resource to be     * localized.    * @return<code>LocalResourceVisibility</code> of the resource to be     *         localized    */
DECL|method|getVisibility ()
specifier|public
name|LocalResourceVisibility
name|getVisibility
parameter_list|()
function_decl|;
comment|/**    * Set the<code>LocalResourceVisibility</code> of the resource to be     * localized.    * @param visibility<code>LocalResourceVisibility</code> of the resource to be     *                   localized    */
DECL|method|setVisibility (LocalResourceVisibility visibility)
specifier|public
name|void
name|setVisibility
parameter_list|(
name|LocalResourceVisibility
name|visibility
parameter_list|)
function_decl|;
comment|/**    * Get the<em>pattern</em> that should be used to extract entries from the    * archive (only used when type is<code>PATTERN</code>).    * @return<em>pattern</em> that should be used to extract entries from the     * archive.     */
DECL|method|getPattern ()
specifier|public
name|String
name|getPattern
parameter_list|()
function_decl|;
comment|/**    * Set the<em>pattern</em> that should be used to extract entries from the    * archive (only used when type is<code>PATTERN</code>).    * @param pattern<em>pattern</em> that should be used to extract entries     * from the archive.    */
DECL|method|setPattern (String pattern)
specifier|public
name|void
name|setPattern
parameter_list|(
name|String
name|pattern
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

