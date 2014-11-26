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
name|api
operator|.
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_comment
comment|/**  *<p>The request from clients to release a resource in the shared cache.</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ReleaseSharedCacheResourceRequest
specifier|public
specifier|abstract
class|class
name|ReleaseSharedCacheResourceRequest
block|{
comment|/**    * Get the<code>ApplicationId</code> of the resource to be released.    *    * @return<code>ApplicationId</code>    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAppId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getAppId
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationId</code> of the resource to be released.    *    * @param id<code>ApplicationId</code>    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setAppId (ApplicationId id)
specifier|public
specifier|abstract
name|void
name|setAppId
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
function_decl|;
comment|/**    * Get the<code>key</code> of the resource to be released.    *    * @return<code>key</code>    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getResourceKey ()
specifier|public
specifier|abstract
name|String
name|getResourceKey
parameter_list|()
function_decl|;
comment|/**    * Set the<code>key</code> of the resource to be released.    *    * @param key unique identifier for the resource    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setResourceKey (String key)
specifier|public
specifier|abstract
name|void
name|setResourceKey
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_class

end_unit

