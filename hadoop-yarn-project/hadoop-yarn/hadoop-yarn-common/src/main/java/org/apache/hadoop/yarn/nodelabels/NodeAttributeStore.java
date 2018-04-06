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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|NodeToAttributes
import|;
end_import

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
name|List
import|;
end_import

begin_comment
comment|/**  * Interface class for Node label store.  */
end_comment

begin_interface
DECL|interface|NodeAttributeStore
specifier|public
interface|interface
name|NodeAttributeStore
extends|extends
name|Closeable
block|{
comment|/**    * Replace labels on node.    *    * @param nodeToAttribute node to attribute list.    * @throws IOException    */
DECL|method|replaceNodeAttributes (List<NodeToAttributes> nodeToAttribute)
name|void
name|replaceNodeAttributes
parameter_list|(
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|nodeToAttribute
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Add attribute to node.    *    * @param nodeToAttribute node to attribute list.    * @throws IOException    */
DECL|method|addNodeAttributes (List<NodeToAttributes> nodeToAttribute)
name|void
name|addNodeAttributes
parameter_list|(
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|nodeToAttribute
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove attribute from node.    *    * @param nodeToAttribute node to attribute list.    * @throws IOException    */
DECL|method|removeNodeAttributes (List<NodeToAttributes> nodeToAttribute)
name|void
name|removeNodeAttributes
parameter_list|(
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|nodeToAttribute
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Initialize based on configuration and NodeAttributesManager.    *    * @param configuration configuration instance.    * @param mgr nodeattributemanager instance.    * @throws Exception    */
DECL|method|init (Configuration configuration, NodeAttributesManager mgr)
name|void
name|init
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|NodeAttributesManager
name|mgr
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Recover store on resourcemanager startup.    * @throws IOException    * @throws YarnException    */
DECL|method|recover ()
name|void
name|recover
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
block|}
end_interface

end_unit

