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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|AbstractService
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
name|NodeLabel
import|;
end_import

begin_comment
comment|/**  * Interface which will be responsible for fetching the labels  *   */
end_comment

begin_class
DECL|class|NodeLabelsProvider
specifier|public
specifier|abstract
class|class
name|NodeLabelsProvider
extends|extends
name|AbstractService
block|{
DECL|method|NodeLabelsProvider (String name)
specifier|public
name|NodeLabelsProvider
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Provides the labels. LabelProvider is expected to give same Labels    * continuously until there is a change in labels.     * If null is returned then Empty label set is assumed by the caller.    *     * @return Set of node label strings applicable for a node    */
DECL|method|getNodeLabels ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|getNodeLabels
parameter_list|()
function_decl|;
block|}
end_class

end_unit

