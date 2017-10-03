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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * This class is used to encapsulate the {@link NodeId} as well as the HTTP  * address that can be used to communicate with the Node.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RemoteNode
specifier|public
specifier|abstract
class|class
name|RemoteNode
implements|implements
name|Comparable
argument_list|<
name|RemoteNode
argument_list|>
block|{
comment|/**    * Create new Instance.    * @param nodeId NodeId.    * @param httpAddress Http address.    * @return RemoteNode instance.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (NodeId nodeId, String httpAddress)
specifier|public
specifier|static
name|RemoteNode
name|newInstance
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|String
name|httpAddress
parameter_list|)
block|{
name|RemoteNode
name|remoteNode
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RemoteNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|remoteNode
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|remoteNode
operator|.
name|setHttpAddress
argument_list|(
name|httpAddress
argument_list|)
expr_stmt|;
return|return
name|remoteNode
return|;
block|}
comment|/**    * Create new Instance.    * @param nodeId NodeId.    * @param httpAddress Http address.    * @param rackName Rack Name.    * @return RemoteNode instance.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (NodeId nodeId, String httpAddress, String rackName)
specifier|public
specifier|static
name|RemoteNode
name|newInstance
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|String
name|httpAddress
parameter_list|,
name|String
name|rackName
parameter_list|)
block|{
name|RemoteNode
name|remoteNode
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RemoteNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|remoteNode
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|remoteNode
operator|.
name|setHttpAddress
argument_list|(
name|httpAddress
argument_list|)
expr_stmt|;
name|remoteNode
operator|.
name|setRackName
argument_list|(
name|rackName
argument_list|)
expr_stmt|;
return|return
name|remoteNode
return|;
block|}
comment|/**    * Get {@link NodeId}.    * @return NodeId.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getNodeId ()
specifier|public
specifier|abstract
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
comment|/**    * Set {@link NodeId}.    * @param nodeId NodeId.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNodeId (NodeId nodeId)
specifier|public
specifier|abstract
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
comment|/**    * Get HTTP address.    * @return Http Address.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getHttpAddress ()
specifier|public
specifier|abstract
name|String
name|getHttpAddress
parameter_list|()
function_decl|;
comment|/**    * Set HTTP address.    * @param httpAddress HTTP address.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setHttpAddress (String httpAddress)
specifier|public
specifier|abstract
name|void
name|setHttpAddress
parameter_list|(
name|String
name|httpAddress
parameter_list|)
function_decl|;
comment|/**    * Get Rack Name.    * @return Rack Name.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getRackName ()
specifier|public
specifier|abstract
name|String
name|getRackName
parameter_list|()
function_decl|;
comment|/**    * Set Rack Name.    * @param rackName Rack Name.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRackName (String rackName)
specifier|public
specifier|abstract
name|void
name|setRackName
parameter_list|(
name|String
name|rackName
parameter_list|)
function_decl|;
comment|/**    * Use the underlying {@link NodeId} comparator.    * @param other RemoteNode.    * @return Comparison.    */
annotation|@
name|Override
DECL|method|compareTo (RemoteNode other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|RemoteNode
name|other
parameter_list|)
block|{
return|return
name|this
operator|.
name|getNodeId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getNodeId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"RemoteNode{"
operator|+
literal|"nodeId="
operator|+
name|getNodeId
argument_list|()
operator|+
literal|", "
operator|+
literal|"rackName="
operator|+
name|getRackName
argument_list|()
operator|+
literal|", "
operator|+
literal|"httpAddress="
operator|+
name|getHttpAddress
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

