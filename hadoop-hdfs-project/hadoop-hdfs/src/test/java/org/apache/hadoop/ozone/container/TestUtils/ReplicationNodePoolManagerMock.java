begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.TestUtils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|TestUtils
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
operator|.
name|NodePoolManager
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_comment
comment|/**  * Pool Manager replication mock.  */
end_comment

begin_class
DECL|class|ReplicationNodePoolManagerMock
specifier|public
class|class
name|ReplicationNodePoolManagerMock
implements|implements
name|NodePoolManager
block|{
DECL|field|nodeMemberShip
specifier|private
specifier|final
name|Map
argument_list|<
name|DatanodeID
argument_list|,
name|String
argument_list|>
name|nodeMemberShip
decl_stmt|;
comment|/**    * A node pool manager for testing.    */
DECL|method|ReplicationNodePoolManagerMock ()
specifier|public
name|ReplicationNodePoolManagerMock
parameter_list|()
block|{
name|nodeMemberShip
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add a node to a node pool.    *    * @param pool - name of the node pool.    * @param node - data node.    */
annotation|@
name|Override
DECL|method|addNode (String pool, DatanodeID node)
specifier|public
name|void
name|addNode
parameter_list|(
name|String
name|pool
parameter_list|,
name|DatanodeID
name|node
parameter_list|)
block|{
name|nodeMemberShip
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|pool
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove a node from a node pool.    *    * @param pool - name of the node pool.    * @param node - data node.    * @throws SCMException    */
annotation|@
name|Override
DECL|method|removeNode (String pool, DatanodeID node)
specifier|public
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
block|{
name|nodeMemberShip
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get a list of known node pools.    *    * @return a list of known node pool names or an empty list if not node pool    * is defined.    */
annotation|@
name|Override
DECL|method|getNodePools ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getNodePools
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|poolSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DatanodeID
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|nodeMemberShip
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|poolSet
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|poolSet
argument_list|)
return|;
block|}
comment|/**    * Get all nodes of a node pool given the name of the node pool.    *    * @param pool - name of the node pool.    * @return a list of datanode ids or an empty list if the node pool was not    * found.    */
annotation|@
name|Override
DECL|method|getNodes (String pool)
specifier|public
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getNodes
parameter_list|(
name|String
name|pool
parameter_list|)
block|{
name|Set
argument_list|<
name|DatanodeID
argument_list|>
name|datanodeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DatanodeID
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|nodeMemberShip
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|pool
argument_list|)
condition|)
block|{
name|datanodeSet
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|datanodeSet
argument_list|)
return|;
block|}
comment|/**    * Get the node pool name if the node has been added to a node pool.    *    * @param datanodeID - datanode ID.    * @return node pool name if it has been assigned. null if the node has not    * been assigned to any node pool yet.    */
annotation|@
name|Override
DECL|method|getNodePool (DatanodeID datanodeID)
specifier|public
name|String
name|getNodePool
parameter_list|(
name|DatanodeID
name|datanodeID
parameter_list|)
block|{
return|return
name|nodeMemberShip
operator|.
name|get
argument_list|(
name|datanodeID
argument_list|)
return|;
block|}
comment|/**    * Closes this stream and releases any system resources associated    * with it. If the stream is already closed then invoking this    * method has no effect.    *<p>    *<p> As noted in {@link AutoCloseable#close()}, cases where the    * close may fail require careful attention. It is strongly advised    * to relinquish the underlying resources and to internally    *<em>mark</em> the {@code Closeable} as closed, prior to throwing    * the {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
block|}
end_class

end_unit

