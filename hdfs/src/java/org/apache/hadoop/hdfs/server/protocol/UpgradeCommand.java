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
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableFactories
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
name|io
operator|.
name|WritableFactory
import|;
end_import

begin_comment
comment|/**  * This as a generic distributed upgrade command.  *   * During the upgrade cluster components send upgrade commands to each other  * in order to obtain or share information with them.  * It is supposed that each upgrade defines specific upgrade command by  * deriving them from this class.  * The upgrade command contains version of the upgrade, which is verified   * on the receiving side and current status of the upgrade.  */
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
DECL|class|UpgradeCommand
specifier|public
class|class
name|UpgradeCommand
extends|extends
name|DatanodeCommand
block|{
DECL|field|UC_ACTION_UNKNOWN
specifier|final
specifier|static
name|int
name|UC_ACTION_UNKNOWN
init|=
name|DatanodeProtocol
operator|.
name|DNA_UNKNOWN
decl_stmt|;
DECL|field|UC_ACTION_REPORT_STATUS
specifier|public
specifier|final
specifier|static
name|int
name|UC_ACTION_REPORT_STATUS
init|=
literal|100
decl_stmt|;
comment|// report upgrade status
DECL|field|UC_ACTION_START_UPGRADE
specifier|public
specifier|final
specifier|static
name|int
name|UC_ACTION_START_UPGRADE
init|=
literal|101
decl_stmt|;
comment|// start upgrade
DECL|field|version
specifier|private
name|int
name|version
decl_stmt|;
DECL|field|upgradeStatus
specifier|private
name|short
name|upgradeStatus
decl_stmt|;
DECL|method|UpgradeCommand ()
specifier|public
name|UpgradeCommand
parameter_list|()
block|{
name|super
argument_list|(
name|UC_ACTION_UNKNOWN
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|upgradeStatus
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|UpgradeCommand (int action, int version, short status)
specifier|public
name|UpgradeCommand
parameter_list|(
name|int
name|action
parameter_list|,
name|int
name|version
parameter_list|,
name|short
name|status
parameter_list|)
block|{
name|super
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|upgradeStatus
operator|=
name|status
expr_stmt|;
block|}
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|getCurrentStatus ()
specifier|public
name|short
name|getCurrentStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|upgradeStatus
return|;
block|}
comment|/////////////////////////////////////////////////
comment|// Writable
comment|/////////////////////////////////////////////////
static|static
block|{
comment|// register a ctor
name|WritableFactories
operator|.
name|setFactory
argument_list|(
name|UpgradeCommand
operator|.
name|class
argument_list|,
operator|new
name|WritableFactory
argument_list|()
block|{
specifier|public
name|Writable
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|UpgradeCommand
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    */
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|this
operator|.
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|this
operator|.
name|upgradeStatus
argument_list|)
expr_stmt|;
block|}
comment|/**    */
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|upgradeStatus
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

