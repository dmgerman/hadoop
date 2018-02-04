begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.nodelabels
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
name|nodemanager
operator|.
name|nodelabels
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Interface which will be responsible for fetching node descriptors,  * a node descriptor could be a  * {@link org.apache.hadoop.yarn.api.records.NodeLabel} or a  * {@link org.apache.hadoop.yarn.api.records.NodeAttribute}.  */
end_comment

begin_interface
DECL|interface|NodeDescriptorsProvider
specifier|public
interface|interface
name|NodeDescriptorsProvider
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Provides the descriptors. The provider is expected to give same    * descriptors continuously until there is a change.    * If null is returned then an empty set is assumed by the caller.    *    * @return Set of node descriptors applicable for a node    */
DECL|method|getDescriptors ()
name|Set
argument_list|<
name|T
argument_list|>
name|getDescriptors
parameter_list|()
function_decl|;
comment|/**    * Sets a set of descriptors to the provider.    * @param descriptors node descriptors.    */
DECL|method|setDescriptors (Set<T> descriptors)
name|void
name|setDescriptors
parameter_list|(
name|Set
argument_list|<
name|T
argument_list|>
name|descriptors
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

