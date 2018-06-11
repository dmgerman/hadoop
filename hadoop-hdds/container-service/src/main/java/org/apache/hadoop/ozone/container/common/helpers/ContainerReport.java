begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerInfo
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|max
import|;
end_import

begin_comment
comment|/**  * Container Report iterates the closed containers and sends a container report  * to SCM.  */
end_comment

begin_class
DECL|class|ContainerReport
specifier|public
class|class
name|ContainerReport
block|{
DECL|field|UNKNOWN
specifier|private
specifier|static
specifier|final
name|int
name|UNKNOWN
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|finalhash
specifier|private
specifier|final
name|String
name|finalhash
decl_stmt|;
DECL|field|size
specifier|private
name|long
name|size
decl_stmt|;
DECL|field|keyCount
specifier|private
name|long
name|keyCount
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
DECL|field|readCount
specifier|private
name|long
name|readCount
decl_stmt|;
DECL|field|writeCount
specifier|private
name|long
name|writeCount
decl_stmt|;
DECL|field|readBytes
specifier|private
name|long
name|readBytes
decl_stmt|;
DECL|field|writeBytes
specifier|private
name|long
name|writeBytes
decl_stmt|;
DECL|field|containerID
specifier|private
name|long
name|containerID
decl_stmt|;
DECL|field|deleteTransactionId
specifier|private
name|long
name|deleteTransactionId
decl_stmt|;
DECL|method|getContainerID ()
specifier|public
name|long
name|getContainerID
parameter_list|()
block|{
return|return
name|containerID
return|;
block|}
DECL|method|setContainerID (long containerID)
specifier|public
name|void
name|setContainerID
parameter_list|(
name|long
name|containerID
parameter_list|)
block|{
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
block|}
comment|/**    * Constructs the ContainerReport.    *    * @param containerID - Container ID.    * @param finalhash - Final Hash.    */
DECL|method|ContainerReport (long containerID, String finalhash)
specifier|public
name|ContainerReport
parameter_list|(
name|long
name|containerID
parameter_list|,
name|String
name|finalhash
parameter_list|)
block|{
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
name|this
operator|.
name|finalhash
operator|=
name|finalhash
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|UNKNOWN
expr_stmt|;
name|this
operator|.
name|keyCount
operator|=
name|UNKNOWN
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|readCount
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|readBytes
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|writeCount
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|writeBytes
operator|=
literal|0L
expr_stmt|;
name|this
operator|.
name|deleteTransactionId
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Gets a containerReport from protobuf class.    *    * @param info - ContainerInfo.    * @return - ContainerReport.    */
DECL|method|getFromProtoBuf (ContainerInfo info)
specifier|public
specifier|static
name|ContainerReport
name|getFromProtoBuf
parameter_list|(
name|ContainerInfo
name|info
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|ContainerReport
name|report
init|=
operator|new
name|ContainerReport
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|info
operator|.
name|getFinalhash
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|hasSize
argument_list|()
condition|)
block|{
name|report
operator|.
name|setSize
argument_list|(
name|info
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasKeyCount
argument_list|()
condition|)
block|{
name|report
operator|.
name|setKeyCount
argument_list|(
name|info
operator|.
name|getKeyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasUsed
argument_list|()
condition|)
block|{
name|report
operator|.
name|setBytesUsed
argument_list|(
name|info
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasReadCount
argument_list|()
condition|)
block|{
name|report
operator|.
name|setReadCount
argument_list|(
name|info
operator|.
name|getReadCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasReadBytes
argument_list|()
condition|)
block|{
name|report
operator|.
name|setReadBytes
argument_list|(
name|info
operator|.
name|getReadBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasWriteCount
argument_list|()
condition|)
block|{
name|report
operator|.
name|setWriteCount
argument_list|(
name|info
operator|.
name|getWriteCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasWriteBytes
argument_list|()
condition|)
block|{
name|report
operator|.
name|setWriteBytes
argument_list|(
name|info
operator|.
name|getWriteBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|hasDeleteTransactionId
argument_list|()
condition|)
block|{
name|report
operator|.
name|updateDeleteTransactionId
argument_list|(
name|info
operator|.
name|getDeleteTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|report
operator|.
name|setContainerID
argument_list|(
name|info
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
comment|/**    * Returns the final signature for this container.    *    * @return - hash    */
DECL|method|getFinalhash ()
specifier|public
name|String
name|getFinalhash
parameter_list|()
block|{
return|return
name|finalhash
return|;
block|}
comment|/**    * Returns a positive number it is a valid number, -1 if not known.    *    * @return size or -1    */
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Sets the size of the container on disk.    *    * @param size - int    */
DECL|method|setSize (long size)
specifier|public
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**    * Gets number of keys in the container if known.    *    * @return - Number of keys or -1 for not known.    */
DECL|method|getKeyCount ()
specifier|public
name|long
name|getKeyCount
parameter_list|()
block|{
return|return
name|keyCount
return|;
block|}
comment|/**    * Sets the key count.    *    * @param keyCount - Key Count    */
DECL|method|setKeyCount (long keyCount)
specifier|public
name|void
name|setKeyCount
parameter_list|(
name|long
name|keyCount
parameter_list|)
block|{
name|this
operator|.
name|keyCount
operator|=
name|keyCount
expr_stmt|;
block|}
DECL|method|getReadCount ()
specifier|public
name|long
name|getReadCount
parameter_list|()
block|{
return|return
name|readCount
return|;
block|}
DECL|method|setReadCount (long readCount)
specifier|public
name|void
name|setReadCount
parameter_list|(
name|long
name|readCount
parameter_list|)
block|{
name|this
operator|.
name|readCount
operator|=
name|readCount
expr_stmt|;
block|}
DECL|method|getWriteCount ()
specifier|public
name|long
name|getWriteCount
parameter_list|()
block|{
return|return
name|writeCount
return|;
block|}
DECL|method|setWriteCount (long writeCount)
specifier|public
name|void
name|setWriteCount
parameter_list|(
name|long
name|writeCount
parameter_list|)
block|{
name|this
operator|.
name|writeCount
operator|=
name|writeCount
expr_stmt|;
block|}
DECL|method|getReadBytes ()
specifier|public
name|long
name|getReadBytes
parameter_list|()
block|{
return|return
name|readBytes
return|;
block|}
DECL|method|setReadBytes (long readBytes)
specifier|public
name|void
name|setReadBytes
parameter_list|(
name|long
name|readBytes
parameter_list|)
block|{
name|this
operator|.
name|readBytes
operator|=
name|readBytes
expr_stmt|;
block|}
DECL|method|getWriteBytes ()
specifier|public
name|long
name|getWriteBytes
parameter_list|()
block|{
return|return
name|writeBytes
return|;
block|}
DECL|method|setWriteBytes (long writeBytes)
specifier|public
name|void
name|setWriteBytes
parameter_list|(
name|long
name|writeBytes
parameter_list|)
block|{
name|this
operator|.
name|writeBytes
operator|=
name|writeBytes
expr_stmt|;
block|}
DECL|method|getBytesUsed ()
specifier|public
name|long
name|getBytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
return|;
block|}
DECL|method|setBytesUsed (long bytesUsed)
specifier|public
name|void
name|setBytesUsed
parameter_list|(
name|long
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
DECL|method|updateDeleteTransactionId (long transactionId)
specifier|public
name|void
name|updateDeleteTransactionId
parameter_list|(
name|long
name|transactionId
parameter_list|)
block|{
name|this
operator|.
name|deleteTransactionId
operator|=
name|max
argument_list|(
name|transactionId
argument_list|,
name|deleteTransactionId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets a containerInfo protobuf message from ContainerReports.    *    * @return ContainerInfo    */
DECL|method|getProtoBufMessage ()
specifier|public
name|ContainerInfo
name|getProtoBufMessage
parameter_list|()
block|{
return|return
name|ContainerInfo
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyCount
argument_list|(
name|this
operator|.
name|getKeyCount
argument_list|()
argument_list|)
operator|.
name|setSize
argument_list|(
name|this
operator|.
name|getSize
argument_list|()
argument_list|)
operator|.
name|setUsed
argument_list|(
name|this
operator|.
name|getBytesUsed
argument_list|()
argument_list|)
operator|.
name|setReadCount
argument_list|(
name|this
operator|.
name|getReadCount
argument_list|()
argument_list|)
operator|.
name|setReadBytes
argument_list|(
name|this
operator|.
name|getReadBytes
argument_list|()
argument_list|)
operator|.
name|setWriteCount
argument_list|(
name|this
operator|.
name|getWriteCount
argument_list|()
argument_list|)
operator|.
name|setWriteBytes
argument_list|(
name|this
operator|.
name|getWriteBytes
argument_list|()
argument_list|)
operator|.
name|setFinalhash
argument_list|(
name|this
operator|.
name|getFinalhash
argument_list|()
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|this
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|.
name|setDeleteTransactionId
argument_list|(
name|this
operator|.
name|deleteTransactionId
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

