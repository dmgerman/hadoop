begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolR23Compatible
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolR23Compatible
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
name|hdfs
operator|.
name|DFSUtil
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
name|protocol
operator|.
name|DatanodeInfo
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
name|Text
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
name|WritableUtils
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
name|net
operator|.
name|NetworkTopology
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
name|net
operator|.
name|NodeBase
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
name|HadoopIllegalArgumentException
import|;
end_import

begin_comment
comment|/**   * DatanodeInfo represents the status of a DataNode.  * This object is used for communication in the  * Datanode Protocol and the Client Protocol.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|DatanodeInfoWritable
specifier|public
class|class
name|DatanodeInfoWritable
extends|extends
name|DatanodeIDWritable
block|{
DECL|field|capacity
specifier|protected
name|long
name|capacity
decl_stmt|;
DECL|field|dfsUsed
specifier|protected
name|long
name|dfsUsed
decl_stmt|;
DECL|field|remaining
specifier|protected
name|long
name|remaining
decl_stmt|;
DECL|field|blockPoolUsed
specifier|protected
name|long
name|blockPoolUsed
decl_stmt|;
DECL|field|lastUpdate
specifier|protected
name|long
name|lastUpdate
decl_stmt|;
DECL|field|xceiverCount
specifier|protected
name|int
name|xceiverCount
decl_stmt|;
DECL|field|location
specifier|protected
name|String
name|location
init|=
name|NetworkTopology
operator|.
name|DEFAULT_RACK
decl_stmt|;
comment|/** HostName as supplied by the datanode during registration as its     * name. Namenode uses datanode IP address as the name.    */
DECL|field|hostName
specifier|protected
name|String
name|hostName
init|=
literal|null
decl_stmt|;
comment|// administrative states of a datanode
DECL|enum|AdminStates
specifier|public
enum|enum
name|AdminStates
block|{
DECL|enumConstant|NORMAL
DECL|enumConstant|DatanodeInfo.AdminStates.NORMAL.toString
name|NORMAL
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|NORMAL
operator|.
name|toString
argument_list|()
argument_list|)
block|,
DECL|enumConstant|DECOMMISSION_INPROGRESS
DECL|enumConstant|DatanodeInfo.AdminStates.DECOMMISSION_INPROGRESS.toString
name|DECOMMISSION_INPROGRESS
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|DECOMMISSION_INPROGRESS
operator|.
name|toString
argument_list|()
argument_list|)
block|,
DECL|enumConstant|DECOMMISSIONED
DECL|enumConstant|DatanodeInfo.AdminStates.DECOMMISSIONED.toString
name|DECOMMISSIONED
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|DECOMMISSIONED
operator|.
name|toString
argument_list|()
argument_list|)
block|;
DECL|field|value
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|AdminStates (final String v)
name|AdminStates
parameter_list|(
specifier|final
name|String
name|v
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|v
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|fromValue (final String value)
specifier|public
specifier|static
name|AdminStates
name|fromValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|AdminStates
name|as
range|:
name|AdminStates
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|as
operator|.
name|value
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
return|return
name|as
return|;
block|}
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Unknown Admin State"
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
DECL|field|adminState
specifier|protected
name|AdminStates
name|adminState
decl_stmt|;
DECL|method|convertDatanodeInfo (DatanodeInfoWritable di)
specifier|static
specifier|public
name|DatanodeInfo
name|convertDatanodeInfo
parameter_list|(
name|DatanodeInfoWritable
name|di
parameter_list|)
block|{
if|if
condition|(
name|di
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|DatanodeInfo
argument_list|(
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
argument_list|(
name|di
operator|.
name|getName
argument_list|()
argument_list|,
name|di
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|di
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|di
operator|.
name|getIpcPort
argument_list|()
argument_list|)
argument_list|,
name|di
operator|.
name|getNetworkLocation
argument_list|()
argument_list|,
name|di
operator|.
name|getHostName
argument_list|()
argument_list|,
name|di
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|di
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
name|di
operator|.
name|getRemaining
argument_list|()
argument_list|,
name|di
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|,
name|di
operator|.
name|getLastUpdate
argument_list|()
argument_list|,
name|di
operator|.
name|getXceiverCount
argument_list|()
argument_list|,
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|fromValue
argument_list|(
name|di
operator|.
name|getAdminState
argument_list|()
operator|.
name|value
argument_list|)
argument_list|)
return|;
block|}
DECL|method|convertDatanodeInfo (DatanodeInfoWritable di[])
specifier|static
specifier|public
name|DatanodeInfo
index|[]
name|convertDatanodeInfo
parameter_list|(
name|DatanodeInfoWritable
name|di
index|[]
parameter_list|)
block|{
if|if
condition|(
name|di
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|DatanodeInfo
index|[]
name|result
init|=
operator|new
name|DatanodeInfo
index|[
name|di
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|di
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|convertDatanodeInfo
argument_list|(
name|di
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|convertDatanodeInfo (DatanodeInfo[] di)
specifier|static
specifier|public
name|DatanodeInfoWritable
index|[]
name|convertDatanodeInfo
parameter_list|(
name|DatanodeInfo
index|[]
name|di
parameter_list|)
block|{
if|if
condition|(
name|di
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|DatanodeInfoWritable
index|[]
name|result
init|=
operator|new
name|DatanodeInfoWritable
index|[
name|di
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|di
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeInfoWritable
argument_list|(
operator|new
name|DatanodeIDWritable
argument_list|(
name|di
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getIpcPort
argument_list|()
argument_list|)
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getHostName
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getRemaining
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getLastUpdate
argument_list|()
argument_list|,
name|di
index|[
name|i
index|]
operator|.
name|getXceiverCount
argument_list|()
argument_list|,
name|AdminStates
operator|.
name|fromValue
argument_list|(
name|di
index|[
name|i
index|]
operator|.
name|getAdminState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|convertDatanodeInfo (DatanodeInfo di)
specifier|static
specifier|public
name|DatanodeInfoWritable
name|convertDatanodeInfo
parameter_list|(
name|DatanodeInfo
name|di
parameter_list|)
block|{
if|if
condition|(
name|di
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|DatanodeInfoWritable
argument_list|(
operator|new
name|DatanodeIDWritable
argument_list|(
name|di
operator|.
name|getName
argument_list|()
argument_list|,
name|di
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|di
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|di
operator|.
name|getIpcPort
argument_list|()
argument_list|)
argument_list|,
name|di
operator|.
name|getNetworkLocation
argument_list|()
argument_list|,
name|di
operator|.
name|getHostName
argument_list|()
argument_list|,
name|di
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|di
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
name|di
operator|.
name|getRemaining
argument_list|()
argument_list|,
name|di
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|,
name|di
operator|.
name|getLastUpdate
argument_list|()
argument_list|,
name|di
operator|.
name|getXceiverCount
argument_list|()
argument_list|,
name|AdminStates
operator|.
name|fromValue
argument_list|(
name|di
operator|.
name|getAdminState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|DatanodeInfoWritable ()
specifier|public
name|DatanodeInfoWritable
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|adminState
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|DatanodeInfoWritable (DatanodeInfoWritable from)
specifier|public
name|DatanodeInfoWritable
parameter_list|(
name|DatanodeInfoWritable
name|from
parameter_list|)
block|{
name|super
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|from
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
name|this
operator|.
name|dfsUsed
operator|=
name|from
operator|.
name|getDfsUsed
argument_list|()
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|from
operator|.
name|getRemaining
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
name|from
operator|.
name|getBlockPoolUsed
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastUpdate
operator|=
name|from
operator|.
name|getLastUpdate
argument_list|()
expr_stmt|;
name|this
operator|.
name|xceiverCount
operator|=
name|from
operator|.
name|getXceiverCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|from
operator|.
name|getNetworkLocation
argument_list|()
expr_stmt|;
name|this
operator|.
name|adminState
operator|=
name|from
operator|.
name|adminState
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|from
operator|.
name|hostName
expr_stmt|;
block|}
DECL|method|DatanodeInfoWritable (DatanodeIDWritable nodeID)
specifier|public
name|DatanodeInfoWritable
parameter_list|(
name|DatanodeIDWritable
name|nodeID
parameter_list|)
block|{
name|super
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|dfsUsed
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|lastUpdate
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|xceiverCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|adminState
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|DatanodeInfoWritable (DatanodeIDWritable nodeID, String location, String hostName)
specifier|protected
name|DatanodeInfoWritable
parameter_list|(
name|DatanodeIDWritable
name|nodeID
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|hostName
parameter_list|)
block|{
name|this
argument_list|(
name|nodeID
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
block|}
DECL|method|DatanodeInfoWritable (DatanodeIDWritable nodeID, String location, String hostName, final long capacity, final long dfsUsed, final long remaining, final long blockPoolUsed, final long lastUpdate, final int xceiverCount, final AdminStates adminState)
specifier|public
name|DatanodeInfoWritable
parameter_list|(
name|DatanodeIDWritable
name|nodeID
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|hostName
parameter_list|,
specifier|final
name|long
name|capacity
parameter_list|,
specifier|final
name|long
name|dfsUsed
parameter_list|,
specifier|final
name|long
name|remaining
parameter_list|,
specifier|final
name|long
name|blockPoolUsed
parameter_list|,
specifier|final
name|long
name|lastUpdate
parameter_list|,
specifier|final
name|int
name|xceiverCount
parameter_list|,
specifier|final
name|AdminStates
name|adminState
parameter_list|)
block|{
name|this
argument_list|(
name|nodeID
argument_list|,
name|location
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|dfsUsed
operator|=
name|dfsUsed
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|remaining
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
name|blockPoolUsed
expr_stmt|;
name|this
operator|.
name|lastUpdate
operator|=
name|lastUpdate
expr_stmt|;
name|this
operator|.
name|xceiverCount
operator|=
name|xceiverCount
expr_stmt|;
name|this
operator|.
name|adminState
operator|=
name|adminState
expr_stmt|;
block|}
comment|/** The raw capacity. */
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
comment|/** The used space by the data node. */
DECL|method|getDfsUsed ()
specifier|public
name|long
name|getDfsUsed
parameter_list|()
block|{
return|return
name|dfsUsed
return|;
block|}
comment|/** The used space by the block pool on data node. */
DECL|method|getBlockPoolUsed ()
specifier|public
name|long
name|getBlockPoolUsed
parameter_list|()
block|{
return|return
name|blockPoolUsed
return|;
block|}
comment|/** The used space by the data node. */
DECL|method|getNonDfsUsed ()
specifier|public
name|long
name|getNonDfsUsed
parameter_list|()
block|{
name|long
name|nonDFSUsed
init|=
name|capacity
operator|-
name|dfsUsed
operator|-
name|remaining
decl_stmt|;
return|return
name|nonDFSUsed
operator|<
literal|0
condition|?
literal|0
else|:
name|nonDFSUsed
return|;
block|}
comment|/** The used space by the data node as percentage of present capacity */
DECL|method|getDfsUsedPercent ()
specifier|public
name|float
name|getDfsUsedPercent
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|getPercentUsed
argument_list|(
name|dfsUsed
argument_list|,
name|capacity
argument_list|)
return|;
block|}
comment|/** The raw free space. */
DECL|method|getRemaining ()
specifier|public
name|long
name|getRemaining
parameter_list|()
block|{
return|return
name|remaining
return|;
block|}
comment|/** Used space by the block pool as percentage of present capacity */
DECL|method|getBlockPoolUsedPercent ()
specifier|public
name|float
name|getBlockPoolUsedPercent
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|getPercentUsed
argument_list|(
name|blockPoolUsed
argument_list|,
name|capacity
argument_list|)
return|;
block|}
comment|/** The remaining space as percentage of configured capacity. */
DECL|method|getRemainingPercent ()
specifier|public
name|float
name|getRemainingPercent
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|getPercentRemaining
argument_list|(
name|remaining
argument_list|,
name|capacity
argument_list|)
return|;
block|}
comment|/** The time when this information was accurate. */
DECL|method|getLastUpdate ()
specifier|public
name|long
name|getLastUpdate
parameter_list|()
block|{
return|return
name|lastUpdate
return|;
block|}
comment|/** number of active connections */
DECL|method|getXceiverCount ()
specifier|public
name|int
name|getXceiverCount
parameter_list|()
block|{
return|return
name|xceiverCount
return|;
block|}
comment|/** Sets raw capacity. */
DECL|method|setCapacity (long capacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
block|}
comment|/** Sets the used space for the datanode. */
DECL|method|setDfsUsed (long dfsUsed)
specifier|public
name|void
name|setDfsUsed
parameter_list|(
name|long
name|dfsUsed
parameter_list|)
block|{
name|this
operator|.
name|dfsUsed
operator|=
name|dfsUsed
expr_stmt|;
block|}
comment|/** Sets raw free space. */
DECL|method|setRemaining (long remaining)
specifier|public
name|void
name|setRemaining
parameter_list|(
name|long
name|remaining
parameter_list|)
block|{
name|this
operator|.
name|remaining
operator|=
name|remaining
expr_stmt|;
block|}
comment|/** Sets block pool used space */
DECL|method|setBlockPoolUsed (long bpUsed)
specifier|public
name|void
name|setBlockPoolUsed
parameter_list|(
name|long
name|bpUsed
parameter_list|)
block|{
name|this
operator|.
name|blockPoolUsed
operator|=
name|bpUsed
expr_stmt|;
block|}
comment|/** Sets time when this information was accurate. */
DECL|method|setLastUpdate (long lastUpdate)
specifier|public
name|void
name|setLastUpdate
parameter_list|(
name|long
name|lastUpdate
parameter_list|)
block|{
name|this
operator|.
name|lastUpdate
operator|=
name|lastUpdate
expr_stmt|;
block|}
comment|/** Sets number of active connections */
DECL|method|setXceiverCount (int xceiverCount)
specifier|public
name|void
name|setXceiverCount
parameter_list|(
name|int
name|xceiverCount
parameter_list|)
block|{
name|this
operator|.
name|xceiverCount
operator|=
name|xceiverCount
expr_stmt|;
block|}
comment|/** rack name */
DECL|method|getNetworkLocation ()
specifier|public
name|String
name|getNetworkLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
comment|/** Sets the rack name */
DECL|method|setNetworkLocation (String location)
specifier|public
name|void
name|setNetworkLocation
parameter_list|(
name|String
name|location
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|NodeBase
operator|.
name|normalize
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
operator|(
name|hostName
operator|==
literal|null
operator|||
name|hostName
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|getHost
argument_list|()
else|:
name|hostName
return|;
block|}
DECL|method|setHostName (String host)
specifier|public
name|void
name|setHostName
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|hostName
operator|=
name|host
expr_stmt|;
block|}
comment|/**    * Retrieves the admin state of this node.    */
DECL|method|getAdminState ()
specifier|public
name|AdminStates
name|getAdminState
parameter_list|()
block|{
if|if
condition|(
name|adminState
operator|==
literal|null
condition|)
block|{
return|return
name|AdminStates
operator|.
name|NORMAL
return|;
block|}
return|return
name|adminState
return|;
block|}
comment|/**    * Sets the admin state of this node.    */
DECL|method|setAdminState (AdminStates newState)
specifier|protected
name|void
name|setAdminState
parameter_list|(
name|AdminStates
name|newState
parameter_list|)
block|{
if|if
condition|(
name|newState
operator|==
name|AdminStates
operator|.
name|NORMAL
condition|)
block|{
name|adminState
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|adminState
operator|=
name|newState
expr_stmt|;
block|}
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
name|DatanodeInfoWritable
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
name|DatanodeInfoWritable
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|writeShort
argument_list|(
name|ipcPort
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|dfsUsed
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|blockPoolUsed
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|lastUpdate
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|xceiverCount
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|hostName
operator|==
literal|null
condition|?
literal|""
else|:
name|hostName
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|getAdminState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|ipcPort
operator|=
name|in
operator|.
name|readShort
argument_list|()
operator|&
literal|0x0000ffff
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|dfsUsed
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockPoolUsed
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastUpdate
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|xceiverCount
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|setAdminState
argument_list|(
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|AdminStates
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Read a DatanodeInfo */
DECL|method|read (DataInput in)
specifier|public
specifier|static
name|DatanodeInfoWritable
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DatanodeInfoWritable
name|d
init|=
operator|new
name|DatanodeInfoWritable
argument_list|()
decl_stmt|;
name|d
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|d
return|;
block|}
block|}
end_class

end_unit

