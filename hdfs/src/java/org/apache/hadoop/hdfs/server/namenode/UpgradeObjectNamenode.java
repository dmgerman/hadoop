begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsConstants
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|UpgradeObject
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|UpgradeCommand
import|;
end_import

begin_comment
comment|/**  * Base class for name-node upgrade objects.  * Data-node upgrades are run in separate threads.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|UpgradeObjectNamenode
specifier|public
specifier|abstract
class|class
name|UpgradeObjectNamenode
extends|extends
name|UpgradeObject
block|{
comment|/**    * Process an upgrade command.    * RPC has only one very generic command for all upgrade related inter     * component communications.     * The actual command recognition and execution should be handled here.    * The reply is sent back also as an UpgradeCommand.    *     * @param command    * @return the reply command which is analyzed on the client side.    */
DECL|method|processUpgradeCommand (UpgradeCommand command )
specifier|public
specifier|abstract
name|UpgradeCommand
name|processUpgradeCommand
parameter_list|(
name|UpgradeCommand
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getType ()
specifier|public
name|HdfsConstants
operator|.
name|NodeType
name|getType
parameter_list|()
block|{
return|return
name|HdfsConstants
operator|.
name|NodeType
operator|.
name|NAME_NODE
return|;
block|}
comment|/**    */
DECL|method|startUpgrade ()
specifier|public
name|UpgradeCommand
name|startUpgrade
parameter_list|()
throws|throws
name|IOException
block|{
comment|// broadcast that data-nodes must start the upgrade
return|return
operator|new
name|UpgradeCommand
argument_list|(
name|UpgradeCommand
operator|.
name|UC_ACTION_START_UPGRADE
argument_list|,
name|getVersion
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
return|;
block|}
DECL|method|forceProceed ()
specifier|public
name|void
name|forceProceed
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing by default
name|NameNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"forceProceed() is not defined for the upgrade. "
operator|+
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

