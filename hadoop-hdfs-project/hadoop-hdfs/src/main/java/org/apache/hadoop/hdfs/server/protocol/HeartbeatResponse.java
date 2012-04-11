begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|/**  * Response to {@link DatanodeProtocol#sendHeartbeat}  */
DECL|class|HeartbeatResponse
specifier|public
class|class
name|HeartbeatResponse
block|{
comment|/** Commands returned from the namenode to the datanode */
DECL|field|commands
specifier|private
name|DatanodeCommand
index|[]
name|commands
decl_stmt|;
comment|/** Information about the current HA-related state of the NN */
DECL|field|haStatus
specifier|private
name|NNHAStatusHeartbeat
name|haStatus
decl_stmt|;
DECL|method|HeartbeatResponse (DatanodeCommand[] cmds, NNHAStatusHeartbeat haStatus)
specifier|public
name|HeartbeatResponse
parameter_list|(
name|DatanodeCommand
index|[]
name|cmds
parameter_list|,
name|NNHAStatusHeartbeat
name|haStatus
parameter_list|)
block|{
name|commands
operator|=
name|cmds
expr_stmt|;
name|this
operator|.
name|haStatus
operator|=
name|haStatus
expr_stmt|;
block|}
DECL|method|getCommands ()
specifier|public
name|DatanodeCommand
index|[]
name|getCommands
parameter_list|()
block|{
return|return
name|commands
return|;
block|}
DECL|method|getNameNodeHaState ()
specifier|public
name|NNHAStatusHeartbeat
name|getNameNodeHaState
parameter_list|()
block|{
return|return
name|haStatus
return|;
block|}
block|}
end_class

end_unit

