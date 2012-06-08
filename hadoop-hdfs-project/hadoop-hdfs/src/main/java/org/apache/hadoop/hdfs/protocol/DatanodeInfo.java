begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|net
operator|.
name|NetUtils
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
name|Node
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**   * This class extends the primary identifier of a Datanode with ephemeral  * state, eg usage information, current administrative state, and the  * network location that is communicated to clients.  */
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
DECL|class|DatanodeInfo
specifier|public
class|class
name|DatanodeInfo
extends|extends
name|DatanodeID
implements|implements
name|Node
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
comment|// Datanode administrative states
DECL|enum|AdminStates
specifier|public
enum|enum
name|AdminStates
block|{
DECL|enumConstant|NORMAL
name|NORMAL
argument_list|(
literal|"In Service"
argument_list|)
block|,
DECL|enumConstant|DECOMMISSION_INPROGRESS
name|DECOMMISSION_INPROGRESS
argument_list|(
literal|"Decommission In Progress"
argument_list|)
block|,
DECL|enumConstant|DECOMMISSIONED
name|DECOMMISSIONED
argument_list|(
literal|"Decommissioned"
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
return|return
name|NORMAL
return|;
block|}
block|}
DECL|field|adminState
specifier|protected
name|AdminStates
name|adminState
decl_stmt|;
DECL|method|DatanodeInfo (DatanodeInfo from)
specifier|public
name|DatanodeInfo
parameter_list|(
name|DatanodeInfo
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
DECL|method|DatanodeInfo (DatanodeID nodeID)
specifier|public
name|DatanodeInfo
parameter_list|(
name|DatanodeID
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
DECL|method|DatanodeInfo (DatanodeID nodeID, String location)
specifier|public
name|DatanodeInfo
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|String
name|location
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
block|}
DECL|method|DatanodeInfo (DatanodeID nodeID, String location, final long capacity, final long dfsUsed, final long remaining, final long blockPoolUsed, final long lastUpdate, final int xceiverCount, final AdminStates adminState)
specifier|public
name|DatanodeInfo
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|String
name|location
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
operator|.
name|getIpAddr
argument_list|()
argument_list|,
name|nodeID
operator|.
name|getHostName
argument_list|()
argument_list|,
name|nodeID
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|nodeID
operator|.
name|getXferPort
argument_list|()
argument_list|,
name|nodeID
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|nodeID
operator|.
name|getIpcPort
argument_list|()
argument_list|,
name|capacity
argument_list|,
name|dfsUsed
argument_list|,
name|remaining
argument_list|,
name|blockPoolUsed
argument_list|,
name|lastUpdate
argument_list|,
name|xceiverCount
argument_list|,
name|location
argument_list|,
name|adminState
argument_list|)
expr_stmt|;
block|}
comment|/** Constructor */
DECL|method|DatanodeInfo (final String ipAddr, final String hostName, final String storageID, final int xferPort, final int infoPort, final int ipcPort, final long capacity, final long dfsUsed, final long remaining, final long blockPoolUsed, final long lastUpdate, final int xceiverCount, final String networkLocation, final AdminStates adminState)
specifier|public
name|DatanodeInfo
parameter_list|(
specifier|final
name|String
name|ipAddr
parameter_list|,
specifier|final
name|String
name|hostName
parameter_list|,
specifier|final
name|String
name|storageID
parameter_list|,
specifier|final
name|int
name|xferPort
parameter_list|,
specifier|final
name|int
name|infoPort
parameter_list|,
specifier|final
name|int
name|ipcPort
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
name|String
name|networkLocation
parameter_list|,
specifier|final
name|AdminStates
name|adminState
parameter_list|)
block|{
name|super
argument_list|(
name|ipAddr
argument_list|,
name|hostName
argument_list|,
name|storageID
argument_list|,
name|xferPort
argument_list|,
name|infoPort
argument_list|,
name|ipcPort
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
name|location
operator|=
name|networkLocation
expr_stmt|;
name|this
operator|.
name|adminState
operator|=
name|adminState
expr_stmt|;
block|}
comment|/** Network location name */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getXferAddr
argument_list|()
return|;
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
comment|/** network location */
DECL|method|getNetworkLocation ()
specifier|public
specifier|synchronized
name|String
name|getNetworkLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
comment|/** Sets the network location */
DECL|method|setNetworkLocation (String location)
specifier|public
specifier|synchronized
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
comment|/** A formatted string for reporting the status of the DataNode. */
DECL|method|getDatanodeReport ()
specifier|public
name|String
name|getDatanodeReport
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|long
name|c
init|=
name|getCapacity
argument_list|()
decl_stmt|;
name|long
name|r
init|=
name|getRemaining
argument_list|()
decl_stmt|;
name|long
name|u
init|=
name|getDfsUsed
argument_list|()
decl_stmt|;
name|long
name|nonDFSUsed
init|=
name|getNonDfsUsed
argument_list|()
decl_stmt|;
name|float
name|usedPercent
init|=
name|getDfsUsedPercent
argument_list|()
decl_stmt|;
name|float
name|remainingPercent
init|=
name|getRemainingPercent
argument_list|()
decl_stmt|;
name|String
name|lookupName
init|=
name|NetUtils
operator|.
name|getHostNameOfIP
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Name: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookupName
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" ("
operator|+
name|lookupName
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Hostname: "
operator|+
name|getHostName
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|NetworkTopology
operator|.
name|DEFAULT_RACK
operator|.
name|equals
argument_list|(
name|location
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Rack: "
operator|+
name|location
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"Decommission Status : "
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDecommissioned
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Decommissioned\n"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isDecommissionInProgress
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Decommission in progress\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Normal\n"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"Configured Capacity: "
operator|+
name|c
operator|+
literal|" ("
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|c
argument_list|)
operator|+
literal|")"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"DFS Used: "
operator|+
name|u
operator|+
literal|" ("
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|u
argument_list|)
operator|+
literal|")"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Non DFS Used: "
operator|+
name|nonDFSUsed
operator|+
literal|" ("
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|nonDFSUsed
argument_list|)
operator|+
literal|")"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"DFS Remaining: "
operator|+
name|r
operator|+
literal|" ("
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|r
argument_list|)
operator|+
literal|")"
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"DFS Used%: "
operator|+
name|StringUtils
operator|.
name|limitDecimalTo2
argument_list|(
name|usedPercent
argument_list|)
operator|+
literal|"%\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"DFS Remaining%: "
operator|+
name|StringUtils
operator|.
name|limitDecimalTo2
argument_list|(
name|remainingPercent
argument_list|)
operator|+
literal|"%\n"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Last contact: "
operator|+
operator|new
name|Date
argument_list|(
name|lastUpdate
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** A formatted string for printing the status of the DataNode. */
DECL|method|dumpDatanode ()
specifier|public
name|String
name|dumpDatanode
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|long
name|c
init|=
name|getCapacity
argument_list|()
decl_stmt|;
name|long
name|r
init|=
name|getRemaining
argument_list|()
decl_stmt|;
name|long
name|u
init|=
name|getDfsUsed
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|NetworkTopology
operator|.
name|DEFAULT_RACK
operator|.
name|equals
argument_list|(
name|location
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|location
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isDecommissioned
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" DD"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isDecommissionInProgress
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" DP"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" IN"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|c
operator|+
literal|"("
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|c
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|u
operator|+
literal|"("
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|u
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|StringUtils
operator|.
name|limitDecimalTo2
argument_list|(
operator|(
operator|(
literal|1.0
operator|*
name|u
operator|)
operator|/
name|c
operator|)
operator|*
literal|100
argument_list|)
operator|+
literal|"%"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|r
operator|+
literal|"("
operator|+
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|r
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
operator|+
operator|new
name|Date
argument_list|(
name|lastUpdate
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Start decommissioning a node.    * old state.    */
DECL|method|startDecommission ()
specifier|public
name|void
name|startDecommission
parameter_list|()
block|{
name|adminState
operator|=
name|AdminStates
operator|.
name|DECOMMISSION_INPROGRESS
expr_stmt|;
block|}
comment|/**    * Stop decommissioning a node.    * old state.    */
DECL|method|stopDecommission ()
specifier|public
name|void
name|stopDecommission
parameter_list|()
block|{
name|adminState
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Returns true if the node is in the process of being decommissioned    */
DECL|method|isDecommissionInProgress ()
specifier|public
name|boolean
name|isDecommissionInProgress
parameter_list|()
block|{
return|return
name|adminState
operator|==
name|AdminStates
operator|.
name|DECOMMISSION_INPROGRESS
return|;
block|}
comment|/**    * Returns true if the node has been decommissioned.    */
DECL|method|isDecommissioned ()
specifier|public
name|boolean
name|isDecommissioned
parameter_list|()
block|{
return|return
name|adminState
operator|==
name|AdminStates
operator|.
name|DECOMMISSIONED
return|;
block|}
comment|/**    * Sets the admin state to indicate that decommission is complete.    */
DECL|method|setDecommissioned ()
specifier|public
name|void
name|setDecommissioned
parameter_list|()
block|{
name|adminState
operator|=
name|AdminStates
operator|.
name|DECOMMISSIONED
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
DECL|field|level
specifier|private
specifier|transient
name|int
name|level
decl_stmt|;
comment|//which level of the tree the node resides
DECL|field|parent
specifier|private
specifier|transient
name|Node
name|parent
decl_stmt|;
comment|//its parent
comment|/** Return this node's parent */
DECL|method|getParent ()
specifier|public
name|Node
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|setParent (Node parent)
specifier|public
name|void
name|setParent
parameter_list|(
name|Node
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/** Return this node's level in the tree.    * E.g. the root of a tree returns 0 and its children return 1    */
DECL|method|getLevel ()
specifier|public
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
DECL|method|setLevel (int level)
specifier|public
name|void
name|setLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Super implementation is sufficient
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// Sufficient to use super equality as datanodes are uniquely identified
comment|// by DatanodeID
return|return
operator|(
name|this
operator|==
name|obj
operator|)
operator|||
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
block|}
end_class

end_unit

