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

begin_comment
comment|/**  *<p>  * The response from the SharedCacheManager to the client that indicates whether  * a requested resource exists in the cache.  *</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|UseSharedCacheResourceResponse
specifier|public
specifier|abstract
class|class
name|UseSharedCacheResourceResponse
block|{
comment|/**    * Get the<code>Path</code> corresponding to the requested resource in the    * shared cache.    *    * @return String A<code>Path</code> if the resource exists in the shared    *         cache,<code>null</code> otherwise    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getPath ()
specifier|public
specifier|abstract
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Path</code> corresponding to a resource in the shared cache.    *    * @param p A<code>Path</code> corresponding to a resource in the shared    *          cache    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setPath (String p)
specifier|public
specifier|abstract
name|void
name|setPath
parameter_list|(
name|String
name|p
parameter_list|)
function_decl|;
block|}
end_class

end_unit

