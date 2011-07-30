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

begin_comment
comment|/*  * A system administrator can tune the balancer bandwidth parameter  * (dfs.balance.bandwidthPerSec) dynamically by calling  * "dfsadmin -setBalanacerBandwidth newbandwidth".  * This class is to define the command which sends the new bandwidth value to  * each datanode.  */
end_comment

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
comment|/**  * Balancer bandwidth command instructs each datanode to change its value for  * the max amount of network bandwidth it may use during the block balancing  * operation.  *   * The Balancer Bandwidth Command contains the new bandwidth value as its  * payload. The bandwidth value is in bytes per second.  */
end_comment

begin_class
DECL|class|BalancerBandwidthCommand
specifier|public
class|class
name|BalancerBandwidthCommand
extends|extends
name|DatanodeCommand
block|{
DECL|field|BBC_DEFAULTBANDWIDTH
specifier|private
specifier|final
specifier|static
name|long
name|BBC_DEFAULTBANDWIDTH
init|=
literal|0L
decl_stmt|;
DECL|field|bandwidth
specifier|private
name|long
name|bandwidth
decl_stmt|;
comment|/**    * Balancer Bandwidth Command constructor. Sets bandwidth to 0.    */
DECL|method|BalancerBandwidthCommand ()
name|BalancerBandwidthCommand
parameter_list|()
block|{
name|this
argument_list|(
name|BBC_DEFAULTBANDWIDTH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Balancer Bandwidth Command constructor.    *    * @param bandwidth Blanacer bandwidth in bytes per second.    */
DECL|method|BalancerBandwidthCommand (long bandwidth)
specifier|public
name|BalancerBandwidthCommand
parameter_list|(
name|long
name|bandwidth
parameter_list|)
block|{
name|super
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_BALANCERBANDWIDTHUPDATE
argument_list|)
expr_stmt|;
name|this
operator|.
name|bandwidth
operator|=
name|bandwidth
expr_stmt|;
block|}
comment|/**    * Get current value of the max balancer bandwidth in bytes per second.    *    * @return bandwidth Blanacer bandwidth in bytes per second for this datanode.    */
DECL|method|getBalancerBandwidthValue ()
specifier|public
name|long
name|getBalancerBandwidthValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|bandwidth
return|;
block|}
comment|// ///////////////////////////////////////////////
comment|// Writable
comment|// ///////////////////////////////////////////////
static|static
block|{
comment|// register a ctor
name|WritableFactories
operator|.
name|setFactory
argument_list|(
name|BalancerBandwidthCommand
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
name|BalancerBandwidthCommand
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes the bandwidth payload to the Balancer Bandwidth Command packet.    * @param out DataOutput stream used for writing commands to the datanode.    * @throws IOException    */
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
name|writeLong
argument_list|(
name|this
operator|.
name|bandwidth
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads the bandwidth payload from the Balancer Bandwidth Command packet.    * @param in DataInput stream used for reading commands to the datanode.    * @throws IOException    */
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
name|bandwidth
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

