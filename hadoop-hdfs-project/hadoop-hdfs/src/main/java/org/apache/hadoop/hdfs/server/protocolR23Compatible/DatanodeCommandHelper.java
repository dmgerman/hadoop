begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocolR23Compatible
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
name|protocolR23Compatible
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|BalancerBandwidthCommand
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
name|BlockCommand
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
name|BlockRecoveryCommand
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
name|DatanodeCommand
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
name|DatanodeProtocol
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
name|FinalizeCommand
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
name|KeyUpdateCommand
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
comment|/**  * Class for translating DatanodeCommandWritable to and from DatanodeCommand.  */
end_comment

begin_class
DECL|class|DatanodeCommandHelper
class|class
name|DatanodeCommandHelper
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DatanodeCommandHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|DatanodeCommandHelper ()
specifier|private
name|DatanodeCommandHelper
parameter_list|()
block|{
comment|/* Private constructor to prevent instantiation */
block|}
DECL|method|convert (DatanodeCommandWritable cmd)
specifier|static
name|DatanodeCommand
name|convert
parameter_list|(
name|DatanodeCommandWritable
name|cmd
parameter_list|)
block|{
return|return
name|cmd
operator|.
name|convert
argument_list|()
return|;
block|}
comment|/**    * Given a subclass of {@link DatanodeCommand} return the corresponding    * writable type.    */
DECL|method|convert (DatanodeCommand cmd)
specifier|static
name|DatanodeCommandWritable
name|convert
parameter_list|(
name|DatanodeCommand
name|cmd
parameter_list|)
block|{
switch|switch
condition|(
name|cmd
operator|.
name|getAction
argument_list|()
condition|)
block|{
case|case
name|DatanodeProtocol
operator|.
name|DNA_BALANCERBANDWIDTHUPDATE
case|:
return|return
name|BalancerBandwidthCommandWritable
operator|.
name|convert
argument_list|(
operator|(
name|BalancerBandwidthCommand
operator|)
name|cmd
argument_list|)
return|;
case|case
name|DatanodeProtocol
operator|.
name|DNA_FINALIZE
case|:
return|return
name|FinalizeCommandWritable
operator|.
name|convert
argument_list|(
operator|(
name|FinalizeCommand
operator|)
name|cmd
argument_list|)
return|;
case|case
name|DatanodeProtocol
operator|.
name|DNA_ACCESSKEYUPDATE
case|:
return|return
name|KeyUpdateCommandWritable
operator|.
name|convert
argument_list|(
operator|(
name|KeyUpdateCommand
operator|)
name|cmd
argument_list|)
return|;
case|case
name|DatanodeProtocol
operator|.
name|DNA_REGISTER
case|:
return|return
name|RegisterCommandWritable
operator|.
name|REGISTER
return|;
case|case
name|DatanodeProtocol
operator|.
name|DNA_TRANSFER
case|:
case|case
name|DatanodeProtocol
operator|.
name|DNA_INVALIDATE
case|:
return|return
name|BlockCommandWritable
operator|.
name|convert
argument_list|(
operator|(
name|BlockCommand
operator|)
name|cmd
argument_list|)
return|;
case|case
name|UpgradeCommand
operator|.
name|UC_ACTION_START_UPGRADE
case|:
return|return
name|UpgradeCommandWritable
operator|.
name|convert
argument_list|(
operator|(
name|UpgradeCommand
operator|)
name|cmd
argument_list|)
return|;
case|case
name|DatanodeProtocol
operator|.
name|DNA_RECOVERBLOCK
case|:
return|return
name|BlockRecoveryCommandWritable
operator|.
name|convert
argument_list|(
operator|(
name|BlockRecoveryCommand
operator|)
name|cmd
argument_list|)
return|;
default|default:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown DatanodeCommand action - "
operator|+
name|cmd
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

