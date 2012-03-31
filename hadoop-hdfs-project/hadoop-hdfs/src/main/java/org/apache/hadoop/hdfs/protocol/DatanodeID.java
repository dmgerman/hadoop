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
name|WritableComparable
import|;
end_import

begin_comment
comment|/**  * This class represents the primary identifier for a Datanode.  * Datanodes are identified by how they can be contacted (hostname  * and ports) and their storage ID, a unique number that associates  * the Datanodes blocks with a particular Datanode.  */
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
name|WritableComparable
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
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
comment|// IP:port (data transfer port)
DECL|field|hostName
specifier|protected
name|String
name|hostName
decl_stmt|;
comment|// hostname
DECL|field|storageID
specifier|protected
name|String
name|storageID
decl_stmt|;
comment|// unique per cluster storageID
DECL|field|infoPort
specifier|protected
name|int
name|infoPort
decl_stmt|;
comment|// info server port
DECL|field|ipcPort
specifier|protected
name|int
name|ipcPort
decl_stmt|;
comment|// IPC server port
comment|/** Equivalent to DatanodeID(""). */
DECL|method|DatanodeID ()
specifier|public
name|DatanodeID
parameter_list|()
block|{
name|this
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/** Equivalent to DatanodeID(nodeName, "", -1, -1). */
DECL|method|DatanodeID (String nodeName)
specifier|public
name|DatanodeID
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
name|this
argument_list|(
name|nodeName
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * DatanodeID copy constructor    *     * @param from    */
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
name|getName
argument_list|()
argument_list|,
name|from
operator|.
name|getHostName
argument_list|()
argument_list|,
name|from
operator|.
name|getStorageID
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
block|}
comment|/**    * Create DatanodeID    * @param node IP:port    * @param hostName hostname    * @param storageID data storage ID    * @param infoPort info server port     * @param ipcPort ipc server port    */
DECL|method|DatanodeID (String name, String hostName, String storageID, int infoPort, int ipcPort)
specifier|public
name|DatanodeID
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|hostName
parameter_list|,
name|String
name|storageID
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
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
name|this
operator|.
name|storageID
operator|=
name|storageID
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
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|setHostName (String hostName)
specifier|public
name|void
name|setHostName
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
block|}
DECL|method|setInfoPort (int infoPort)
specifier|public
name|void
name|setInfoPort
parameter_list|(
name|int
name|infoPort
parameter_list|)
block|{
name|this
operator|.
name|infoPort
operator|=
name|infoPort
expr_stmt|;
block|}
DECL|method|setIpcPort (int ipcPort)
specifier|public
name|void
name|setIpcPort
parameter_list|(
name|int
name|ipcPort
parameter_list|)
block|{
name|this
operator|.
name|ipcPort
operator|=
name|ipcPort
expr_stmt|;
block|}
comment|/**    * @return hostname:portNumber.    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
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
comment|/**    * @return data storage ID.    */
DECL|method|getStorageID ()
specifier|public
name|String
name|getStorageID
parameter_list|()
block|{
return|return
name|this
operator|.
name|storageID
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
comment|/**    * sets the data storage ID.    */
DECL|method|setStorageID (String storageID)
specifier|public
name|void
name|setStorageID
parameter_list|(
name|String
name|storageID
parameter_list|)
block|{
name|this
operator|.
name|storageID
operator|=
name|storageID
expr_stmt|;
block|}
comment|/**    * @return hostname and no :portNumber.    */
DECL|method|getHost ()
specifier|public
name|String
name|getHost
parameter_list|()
block|{
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|<
literal|0
condition|)
block|{
return|return
name|name
return|;
block|}
else|else
block|{
return|return
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon
argument_list|)
return|;
block|}
block|}
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
name|int
name|colon
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon
operator|<
literal|0
condition|)
block|{
return|return
literal|50010
return|;
comment|// default port.
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|colon
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
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
name|name
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
name|getName
argument_list|()
argument_list|)
operator|&&
name|storageID
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
name|getStorageID
argument_list|()
argument_list|)
operator|)
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
operator|^
name|storageID
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
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
name|name
operator|=
name|nodeReg
operator|.
name|getName
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
comment|/** Comparable.    * Basis of compare is the String name (host:portNumber) only.    * @param that    * @return as specified by Comparable.    */
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
name|name
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/////////////////////////////////////////////////
comment|// Writable
comment|/////////////////////////////////////////////////
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
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|hostName
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|storageID
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|infoPort
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|ipcPort
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
name|name
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|hostName
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|storageID
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// The port read could be negative, if the port is a large number (more
comment|// than 15 bits in storage size (but less than 16 bits).
comment|// So chop off the first two bytes (and hence the signed bits) before
comment|// setting the field.
name|this
operator|.
name|infoPort
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
name|ipcPort
operator|=
name|in
operator|.
name|readShort
argument_list|()
operator|&
literal|0x0000ffff
expr_stmt|;
block|}
block|}
end_class

end_unit

