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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  * This class represents the primary identifier for a Datanode.  * Datanodes are identified by how they can be contacted (hostname  * and ports) and their storage ID, a unique number that associates  * the Datanodes blocks with a particular Datanode.  *  * {@link DatanodeInfo#getName()} should be used to get the network  * location (for topology) of a datanode, instead of using  * {@link DatanodeID#getXferAddr()} here. Helpers are defined below  * for each context in which a DatanodeID is used.  */
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
DECL|class|DatanodeID
specifier|public
class|class
name|DatanodeID
implements|implements
name|Comparable
argument_list|<
name|DatanodeID
argument_list|>
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|DatanodeID
index|[]
name|EMPTY_ARRAY
init|=
block|{}
decl_stmt|;
DECL|field|ipAddr
specifier|private
name|String
name|ipAddr
decl_stmt|;
comment|// IP address
DECL|field|hostName
specifier|private
name|String
name|hostName
decl_stmt|;
comment|// hostname claimed by datanode
DECL|field|peerHostName
specifier|private
name|String
name|peerHostName
decl_stmt|;
comment|// hostname from the actual connection
DECL|field|xferPort
specifier|private
name|int
name|xferPort
decl_stmt|;
comment|// data streaming port
DECL|field|infoPort
specifier|private
name|int
name|infoPort
decl_stmt|;
comment|// info server port
DECL|field|ipcPort
specifier|private
name|int
name|ipcPort
decl_stmt|;
comment|// IPC server port
comment|// UUID identifying a given datanode. For upgraded Datanodes this is the
comment|// same as the StorageID that was previously used by this Datanode. For
comment|// newly formatted Datanodes it is a UUID.
DECL|field|datanodeUuid
specifier|private
name|String
name|datanodeUuid
init|=
literal|null
decl_stmt|;
DECL|method|DatanodeID (DatanodeID from)
specifier|public
name|DatanodeID
parameter_list|(
name|DatanodeID
name|from
parameter_list|)
block|{
name|this
argument_list|(
name|from
operator|.
name|getIpAddr
argument_list|()
argument_list|,
name|from
operator|.
name|getHostName
argument_list|()
argument_list|,
name|from
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|from
operator|.
name|getXferPort
argument_list|()
argument_list|,
name|from
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|from
operator|.
name|getIpcPort
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|peerHostName
operator|=
name|from
operator|.
name|getPeerHostName
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a DatanodeID    * @param ipAddr IP    * @param hostName hostname    * @param datanodeUuid data node ID, UUID for new Datanodes, may be the    *                     storage ID for pre-UUID datanodes. NULL if unknown    *                     e.g. if this is a new datanode. A new UUID will    *                     be assigned by the namenode.    * @param xferPort data transfer port    * @param infoPort info server port     * @param ipcPort ipc server port    */
DECL|method|DatanodeID (String ipAddr, String hostName, String datanodeUuid, int xferPort, int infoPort, int ipcPort)
specifier|public
name|DatanodeID
parameter_list|(
name|String
name|ipAddr
parameter_list|,
name|String
name|hostName
parameter_list|,
name|String
name|datanodeUuid
parameter_list|,
name|int
name|xferPort
parameter_list|,
name|int
name|infoPort
parameter_list|,
name|int
name|ipcPort
parameter_list|)
block|{
name|this
operator|.
name|ipAddr
operator|=
name|ipAddr
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
name|this
operator|.
name|datanodeUuid
operator|=
name|checkDatanodeUuid
argument_list|(
name|datanodeUuid
argument_list|)
expr_stmt|;
name|this
operator|.
name|xferPort
operator|=
name|xferPort
expr_stmt|;
name|this
operator|.
name|infoPort
operator|=
name|infoPort
expr_stmt|;
name|this
operator|.
name|ipcPort
operator|=
name|ipcPort
expr_stmt|;
block|}
DECL|method|setIpAddr (String ipAddr)
specifier|public
name|void
name|setIpAddr
parameter_list|(
name|String
name|ipAddr
parameter_list|)
block|{
name|this
operator|.
name|ipAddr
operator|=
name|ipAddr
expr_stmt|;
block|}
DECL|method|setPeerHostName (String peerHostName)
specifier|public
name|void
name|setPeerHostName
parameter_list|(
name|String
name|peerHostName
parameter_list|)
block|{
name|this
operator|.
name|peerHostName
operator|=
name|peerHostName
expr_stmt|;
block|}
comment|/**    * @return data node ID.    */
DECL|method|getDatanodeUuid ()
specifier|public
name|String
name|getDatanodeUuid
parameter_list|()
block|{
return|return
name|datanodeUuid
return|;
block|}
DECL|method|setDatanodeUuid (String datanodeUuid)
specifier|public
name|void
name|setDatanodeUuid
parameter_list|(
name|String
name|datanodeUuid
parameter_list|)
block|{
name|this
operator|.
name|datanodeUuid
operator|=
name|datanodeUuid
expr_stmt|;
block|}
DECL|method|checkDatanodeUuid (String uuid)
specifier|private
name|String
name|checkDatanodeUuid
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
if|if
condition|(
name|uuid
operator|==
literal|null
operator|||
name|uuid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|uuid
return|;
block|}
block|}
DECL|method|generateNewDatanodeUuid ()
specifier|public
name|String
name|generateNewDatanodeUuid
parameter_list|()
block|{
name|datanodeUuid
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|datanodeUuid
return|;
block|}
comment|/**    * @return ipAddr;    */
DECL|method|getIpAddr ()
specifier|public
name|String
name|getIpAddr
parameter_list|()
block|{
return|return
name|ipAddr
return|;
block|}
comment|/**    * @return hostname    */
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
comment|/**    * @return hostname from the actual connection     */
DECL|method|getPeerHostName ()
specifier|public
name|String
name|getPeerHostName
parameter_list|()
block|{
return|return
name|peerHostName
return|;
block|}
comment|/**    * @return IP:xferPort string    */
DECL|method|getXferAddr ()
specifier|public
name|String
name|getXferAddr
parameter_list|()
block|{
return|return
name|ipAddr
operator|+
literal|":"
operator|+
name|xferPort
return|;
block|}
comment|/**    * @return IP:ipcPort string    */
DECL|method|getIpcAddr ()
specifier|private
name|String
name|getIpcAddr
parameter_list|()
block|{
return|return
name|ipAddr
operator|+
literal|":"
operator|+
name|ipcPort
return|;
block|}
comment|/**    * @return IP:infoPort string    */
DECL|method|getInfoAddr ()
specifier|public
name|String
name|getInfoAddr
parameter_list|()
block|{
return|return
name|ipAddr
operator|+
literal|":"
operator|+
name|infoPort
return|;
block|}
comment|/**    * @return hostname:xferPort    */
DECL|method|getXferAddrWithHostname ()
specifier|public
name|String
name|getXferAddrWithHostname
parameter_list|()
block|{
return|return
name|hostName
operator|+
literal|":"
operator|+
name|xferPort
return|;
block|}
comment|/**    * @return hostname:ipcPort    */
DECL|method|getIpcAddrWithHostname ()
specifier|private
name|String
name|getIpcAddrWithHostname
parameter_list|()
block|{
return|return
name|hostName
operator|+
literal|":"
operator|+
name|ipcPort
return|;
block|}
comment|/**    * @param useHostname true to use the DN hostname, use the IP otherwise    * @return name:xferPort    */
DECL|method|getXferAddr (boolean useHostname)
specifier|public
name|String
name|getXferAddr
parameter_list|(
name|boolean
name|useHostname
parameter_list|)
block|{
return|return
name|useHostname
condition|?
name|getXferAddrWithHostname
argument_list|()
else|:
name|getXferAddr
argument_list|()
return|;
block|}
comment|/**    * @param useHostname true to use the DN hostname, use the IP otherwise    * @return name:ipcPort    */
DECL|method|getIpcAddr (boolean useHostname)
specifier|public
name|String
name|getIpcAddr
parameter_list|(
name|boolean
name|useHostname
parameter_list|)
block|{
return|return
name|useHostname
condition|?
name|getIpcAddrWithHostname
argument_list|()
else|:
name|getIpcAddr
argument_list|()
return|;
block|}
comment|/**    * @return xferPort (the port for data streaming)    */
DECL|method|getXferPort ()
specifier|public
name|int
name|getXferPort
parameter_list|()
block|{
return|return
name|xferPort
return|;
block|}
comment|/**    * @return infoPort (the port at which the HTTP server bound to)    */
DECL|method|getInfoPort ()
specifier|public
name|int
name|getInfoPort
parameter_list|()
block|{
return|return
name|infoPort
return|;
block|}
comment|/**    * @return ipcPort (the port at which the IPC server bound to)    */
DECL|method|getIpcPort ()
specifier|public
name|int
name|getIpcPort
parameter_list|()
block|{
return|return
name|ipcPort
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object to)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|to
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|to
operator|instanceof
name|DatanodeID
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
name|getXferAddr
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DatanodeID
operator|)
name|to
operator|)
operator|.
name|getXferAddr
argument_list|()
argument_list|)
operator|&&
name|datanodeUuid
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DatanodeID
operator|)
name|to
operator|)
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getXferAddr
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|datanodeUuid
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getXferAddr
argument_list|()
return|;
block|}
comment|/**    * Update fields when a new registration request comes in.    * Note that this does not update storageID.    */
DECL|method|updateRegInfo (DatanodeID nodeReg)
specifier|public
name|void
name|updateRegInfo
parameter_list|(
name|DatanodeID
name|nodeReg
parameter_list|)
block|{
name|ipAddr
operator|=
name|nodeReg
operator|.
name|getIpAddr
argument_list|()
expr_stmt|;
name|hostName
operator|=
name|nodeReg
operator|.
name|getHostName
argument_list|()
expr_stmt|;
name|peerHostName
operator|=
name|nodeReg
operator|.
name|getPeerHostName
argument_list|()
expr_stmt|;
name|xferPort
operator|=
name|nodeReg
operator|.
name|getXferPort
argument_list|()
expr_stmt|;
name|infoPort
operator|=
name|nodeReg
operator|.
name|getInfoPort
argument_list|()
expr_stmt|;
name|ipcPort
operator|=
name|nodeReg
operator|.
name|getIpcPort
argument_list|()
expr_stmt|;
block|}
comment|/**    * Compare based on data transfer address.    *    * @param that    * @return as specified by Comparable    */
annotation|@
name|Override
DECL|method|compareTo (DatanodeID that)
specifier|public
name|int
name|compareTo
parameter_list|(
name|DatanodeID
name|that
parameter_list|)
block|{
return|return
name|getXferAddr
argument_list|()
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|getXferAddr
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

