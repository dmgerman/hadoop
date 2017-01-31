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

begin_comment
comment|/**  * A DropSPSWorkCommand is an instruction to a datanode to drop the SPSWorker's  * pending block storage movement queues.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DropSPSWorkCommand
specifier|public
class|class
name|DropSPSWorkCommand
extends|extends
name|DatanodeCommand
block|{
DECL|field|DNA_DROP_SPS_WORK_COMMAND
specifier|public
specifier|static
specifier|final
name|DropSPSWorkCommand
name|DNA_DROP_SPS_WORK_COMMAND
init|=
operator|new
name|DropSPSWorkCommand
argument_list|()
decl_stmt|;
DECL|method|DropSPSWorkCommand ()
specifier|public
name|DropSPSWorkCommand
parameter_list|()
block|{
name|super
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_DROP_SPS_WORK_COMMAND
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

