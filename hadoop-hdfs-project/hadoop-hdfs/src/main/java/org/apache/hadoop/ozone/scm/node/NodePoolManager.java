begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|node
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Interface that defines SCM NodePoolManager.  */
end_comment

begin_interface
DECL|interface|NodePoolManager
specifier|public
interface|interface
name|NodePoolManager
extends|extends
name|Closeable
block|{
comment|/**    * Add a node to a node pool.    * @param pool - name of the node pool.    * @param node - data node.    */
DECL|method|addNode (String pool, DatanodeID node)
name|void
name|addNode
parameter_list|(
name|String
name|pool
parameter_list|,
name|DatanodeID
name|node
parameter_list|)
function_decl|;
comment|/**    * Remove a node from a node pool.    * @param pool - name of the node pool.    * @param node - data node.    * @throws SCMException    */
DECL|method|removeNode (String pool, DatanodeID node)
name|void
name|removeNode
parameter_list|(
name|String
name|pool
parameter_list|,
name|DatanodeID
name|node
parameter_list|)
throws|throws
name|SCMException
function_decl|;
comment|/**    * Get a list of known node pools.    * @return a list of known node pool names or an empty list if not node pool    * is defined.    */
DECL|method|getNodePools ()
name|List
argument_list|<
name|String
argument_list|>
name|getNodePools
parameter_list|()
function_decl|;
comment|/**    * Get all nodes of a node pool given the name of the node pool.    * @param pool - name of the node pool.    * @return a list of datanode ids or an empty list if the node pool was not    *  found.    */
DECL|method|getNodes (String pool)
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getNodes
parameter_list|(
name|String
name|pool
parameter_list|)
function_decl|;
comment|/**    * Get the node pool name if the node has been added to a node pool.    * @param datanodeID - datanode ID.    * @return node pool name if it has been assigned.    * null if the node has not been assigned to any node pool yet.    */
DECL|method|getNodePool (DatanodeID datanodeID)
name|String
name|getNodePool
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

