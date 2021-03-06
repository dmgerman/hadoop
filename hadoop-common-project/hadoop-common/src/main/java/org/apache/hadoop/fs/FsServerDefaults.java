begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|util
operator|.
name|DataChecksum
import|;
end_import

begin_comment
comment|/****************************************************  * Provides server default configuration values to clients.  *   ****************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|FsServerDefaults
specifier|public
class|class
name|FsServerDefaults
implements|implements
name|Writable
block|{
static|static
block|{
comment|// register a ctor
name|WritableFactories
operator|.
name|setFactory
argument_list|(
name|FsServerDefaults
operator|.
name|class
argument_list|,
operator|new
name|WritableFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Writable
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|FsServerDefaults
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|field|blockSize
specifier|private
name|long
name|blockSize
decl_stmt|;
DECL|field|bytesPerChecksum
specifier|private
name|int
name|bytesPerChecksum
decl_stmt|;
DECL|field|writePacketSize
specifier|private
name|int
name|writePacketSize
decl_stmt|;
DECL|field|replication
specifier|private
name|short
name|replication
decl_stmt|;
DECL|field|fileBufferSize
specifier|private
name|int
name|fileBufferSize
decl_stmt|;
DECL|field|encryptDataTransfer
specifier|private
name|boolean
name|encryptDataTransfer
decl_stmt|;
DECL|field|trashInterval
specifier|private
name|long
name|trashInterval
decl_stmt|;
DECL|field|checksumType
specifier|private
name|DataChecksum
operator|.
name|Type
name|checksumType
decl_stmt|;
DECL|field|keyProviderUri
specifier|private
name|String
name|keyProviderUri
decl_stmt|;
DECL|field|storagepolicyId
specifier|private
name|byte
name|storagepolicyId
decl_stmt|;
DECL|method|FsServerDefaults ()
specifier|public
name|FsServerDefaults
parameter_list|()
block|{   }
DECL|method|FsServerDefaults (long blockSize, int bytesPerChecksum, int writePacketSize, short replication, int fileBufferSize, boolean encryptDataTransfer, long trashInterval, DataChecksum.Type checksumType)
specifier|public
name|FsServerDefaults
parameter_list|(
name|long
name|blockSize
parameter_list|,
name|int
name|bytesPerChecksum
parameter_list|,
name|int
name|writePacketSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|int
name|fileBufferSize
parameter_list|,
name|boolean
name|encryptDataTransfer
parameter_list|,
name|long
name|trashInterval
parameter_list|,
name|DataChecksum
operator|.
name|Type
name|checksumType
parameter_list|)
block|{
name|this
argument_list|(
name|blockSize
argument_list|,
name|bytesPerChecksum
argument_list|,
name|writePacketSize
argument_list|,
name|replication
argument_list|,
name|fileBufferSize
argument_list|,
name|encryptDataTransfer
argument_list|,
name|trashInterval
argument_list|,
name|checksumType
argument_list|,
literal|null
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|FsServerDefaults (long blockSize, int bytesPerChecksum, int writePacketSize, short replication, int fileBufferSize, boolean encryptDataTransfer, long trashInterval, DataChecksum.Type checksumType, String keyProviderUri)
specifier|public
name|FsServerDefaults
parameter_list|(
name|long
name|blockSize
parameter_list|,
name|int
name|bytesPerChecksum
parameter_list|,
name|int
name|writePacketSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|int
name|fileBufferSize
parameter_list|,
name|boolean
name|encryptDataTransfer
parameter_list|,
name|long
name|trashInterval
parameter_list|,
name|DataChecksum
operator|.
name|Type
name|checksumType
parameter_list|,
name|String
name|keyProviderUri
parameter_list|)
block|{
name|this
argument_list|(
name|blockSize
argument_list|,
name|bytesPerChecksum
argument_list|,
name|writePacketSize
argument_list|,
name|replication
argument_list|,
name|fileBufferSize
argument_list|,
name|encryptDataTransfer
argument_list|,
name|trashInterval
argument_list|,
name|checksumType
argument_list|,
name|keyProviderUri
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|FsServerDefaults (long blockSize, int bytesPerChecksum, int writePacketSize, short replication, int fileBufferSize, boolean encryptDataTransfer, long trashInterval, DataChecksum.Type checksumType, String keyProviderUri, byte storagepolicy)
specifier|public
name|FsServerDefaults
parameter_list|(
name|long
name|blockSize
parameter_list|,
name|int
name|bytesPerChecksum
parameter_list|,
name|int
name|writePacketSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|int
name|fileBufferSize
parameter_list|,
name|boolean
name|encryptDataTransfer
parameter_list|,
name|long
name|trashInterval
parameter_list|,
name|DataChecksum
operator|.
name|Type
name|checksumType
parameter_list|,
name|String
name|keyProviderUri
parameter_list|,
name|byte
name|storagepolicy
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
name|this
operator|.
name|bytesPerChecksum
operator|=
name|bytesPerChecksum
expr_stmt|;
name|this
operator|.
name|writePacketSize
operator|=
name|writePacketSize
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|replication
expr_stmt|;
name|this
operator|.
name|fileBufferSize
operator|=
name|fileBufferSize
expr_stmt|;
name|this
operator|.
name|encryptDataTransfer
operator|=
name|encryptDataTransfer
expr_stmt|;
name|this
operator|.
name|trashInterval
operator|=
name|trashInterval
expr_stmt|;
name|this
operator|.
name|checksumType
operator|=
name|checksumType
expr_stmt|;
name|this
operator|.
name|keyProviderUri
operator|=
name|keyProviderUri
expr_stmt|;
name|this
operator|.
name|storagepolicyId
operator|=
name|storagepolicy
expr_stmt|;
block|}
DECL|method|getBlockSize ()
specifier|public
name|long
name|getBlockSize
parameter_list|()
block|{
return|return
name|blockSize
return|;
block|}
DECL|method|getBytesPerChecksum ()
specifier|public
name|int
name|getBytesPerChecksum
parameter_list|()
block|{
return|return
name|bytesPerChecksum
return|;
block|}
DECL|method|getWritePacketSize ()
specifier|public
name|int
name|getWritePacketSize
parameter_list|()
block|{
return|return
name|writePacketSize
return|;
block|}
DECL|method|getReplication ()
specifier|public
name|short
name|getReplication
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
DECL|method|getFileBufferSize ()
specifier|public
name|int
name|getFileBufferSize
parameter_list|()
block|{
return|return
name|fileBufferSize
return|;
block|}
DECL|method|getEncryptDataTransfer ()
specifier|public
name|boolean
name|getEncryptDataTransfer
parameter_list|()
block|{
return|return
name|encryptDataTransfer
return|;
block|}
DECL|method|getTrashInterval ()
specifier|public
name|long
name|getTrashInterval
parameter_list|()
block|{
return|return
name|trashInterval
return|;
block|}
DECL|method|getChecksumType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|getChecksumType
parameter_list|()
block|{
return|return
name|checksumType
return|;
block|}
comment|/* null means old style namenode.    * "" (empty string) means namenode is upgraded but EZ is not supported.    * some string means that value is the key provider.    */
DECL|method|getKeyProviderUri ()
specifier|public
name|String
name|getKeyProviderUri
parameter_list|()
block|{
return|return
name|keyProviderUri
return|;
block|}
DECL|method|getDefaultStoragePolicyId ()
specifier|public
name|byte
name|getDefaultStoragePolicyId
parameter_list|()
block|{
return|return
name|storagepolicyId
return|;
block|}
comment|// /////////////////////////////////////////
comment|// Writable
comment|// /////////////////////////////////////////
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
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
name|out
operator|.
name|writeLong
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|bytesPerChecksum
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|writePacketSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|replication
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|fileBufferSize
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|checksumType
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|storagepolicyId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|InterfaceAudience
operator|.
name|Private
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
name|blockSize
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|bytesPerChecksum
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|writePacketSize
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|replication
operator|=
name|in
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|fileBufferSize
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|checksumType
operator|=
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|DataChecksum
operator|.
name|Type
operator|.
name|class
argument_list|)
expr_stmt|;
name|storagepolicyId
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

