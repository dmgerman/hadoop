begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|conf
operator|.
name|Configuration
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
name|NodeId
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
comment|/**  * Interface class for Node label store.  */
end_comment

begin_interface
DECL|interface|NodeLabelsStore
specifier|public
interface|interface
name|NodeLabelsStore
extends|extends
name|Closeable
block|{
comment|/**    * Store node {@literal ->} label.    */
DECL|method|updateNodeToLabelsMappings ( Map<NodeId, Set<String>> nodeToLabels)
name|void
name|updateNodeToLabelsMappings
parameter_list|(
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|nodeToLabels
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Store new labels.    */
DECL|method|storeNewClusterNodeLabels (List<NodeLabel> label)
name|void
name|storeNewClusterNodeLabels
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|label
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove labels.    */
DECL|method|removeClusterNodeLabels (Collection<String> labels)
name|void
name|removeClusterNodeLabels
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Recover labels and node to labels mappings from store, but if    * ignoreNodeToLabelsMappings is true then node to labels mappings should not    * be recovered. In case of Distributed NodeLabels setup    * ignoreNodeToLabelsMappings will be set to true and recover will be invoked    * as RM will collect the node labels from NM through registration/HB.    *    * @throws IOException    * @throws YarnException    */
DECL|method|recover ()
name|void
name|recover
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|init (Configuration conf, CommonNodeLabelsManager mgr)
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|CommonNodeLabelsManager
name|mgr
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

