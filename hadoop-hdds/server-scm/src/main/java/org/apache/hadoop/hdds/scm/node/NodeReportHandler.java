begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|NodeReportFromDatanode
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventHandler
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventPublisher
import|;
end_import

begin_comment
comment|/**  * Handles Node Reports from datanode.  */
end_comment

begin_class
DECL|class|NodeReportHandler
specifier|public
class|class
name|NodeReportHandler
implements|implements
name|EventHandler
argument_list|<
name|NodeReportFromDatanode
argument_list|>
block|{
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|method|NodeReportHandler (NodeManager nodeManager)
specifier|public
name|NodeReportHandler
parameter_list|(
name|NodeManager
name|nodeManager
parameter_list|)
block|{
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (NodeReportFromDatanode nodeReportFromDatanode, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|NodeReportFromDatanode
name|nodeReportFromDatanode
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
comment|//TODO: process node report.
block|}
block|}
end_class

end_unit

