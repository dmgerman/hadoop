begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.connectors
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|connectors
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
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
comment|/**  * This connector allows user to create an in-memory cluster  * and is useful in testing.  */
end_comment

begin_class
DECL|class|NullConnector
specifier|public
class|class
name|NullConnector
implements|implements
name|ClusterConnector
block|{
DECL|field|nodes
specifier|private
specifier|final
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|nodes
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * getNodes function returns a list of DiskBalancerDataNodes.    *    * @return Array of DiskBalancerDataNodes    */
annotation|@
name|Override
DECL|method|getNodes ()
specifier|public
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|getNodes
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|nodes
return|;
block|}
comment|/**    * Returns info about the connector.    *    * @return String.    */
annotation|@
name|Override
DECL|method|getConnectorInfo ()
specifier|public
name|String
name|getConnectorInfo
parameter_list|()
block|{
return|return
literal|"Null Connector : No persistence, in-memory connector"
return|;
block|}
comment|/**    * Allows user to add nodes into this connector.    *    * @param node - Node to add    */
DECL|method|addNode (DiskBalancerDataNode node)
specifier|public
name|void
name|addNode
parameter_list|(
name|DiskBalancerDataNode
name|node
parameter_list|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

