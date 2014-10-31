begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  *<p>  * The request from the NodeManager to the<code>SharedCacheManager</code> that  * requests whether it can upload a resource in the shared cache.  *</p>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SCMUploaderCanUploadRequest
specifier|public
specifier|abstract
class|class
name|SCMUploaderCanUploadRequest
block|{
comment|/**    * Get the<code>key</code> of the resource that would be uploaded to the    * shared cache.    *    * @return<code>key</code>    */
DECL|method|getResourceKey ()
specifier|public
specifier|abstract
name|String
name|getResourceKey
parameter_list|()
function_decl|;
comment|/**    * Set the<code>key</code> of the resource that would be uploaded to the    * shared cache.    *    * @param key unique identifier for the resource    */
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

