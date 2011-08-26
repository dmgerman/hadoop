begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
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
name|resourcemanager
operator|.
name|recovery
package|;
end_package

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
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
import|;
end_import

begin_interface
DECL|interface|NodeStore
specifier|public
interface|interface
name|NodeStore
block|{
DECL|method|storeNode (RMNode node)
specifier|public
name|void
name|storeNode
parameter_list|(
name|RMNode
name|node
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|removeNode (RMNode node)
specifier|public
name|void
name|removeNode
parameter_list|(
name|RMNode
name|node
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getNextNodeId ()
specifier|public
name|NodeId
name|getNextNodeId
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|isLoggable ()
specifier|public
name|boolean
name|isLoggable
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

