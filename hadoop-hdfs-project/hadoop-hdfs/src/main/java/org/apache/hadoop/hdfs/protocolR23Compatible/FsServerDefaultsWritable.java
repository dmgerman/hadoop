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
name|Stable
DECL|class|FsServerDefaultsWritable
specifier|public
class|class
name|FsServerDefaultsWritable
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
name|FsServerDefaultsWritable
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
name|FsServerDefaultsWritable
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
DECL|method|convert ( FsServerDefaultsWritable fs)
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FsServerDefaults
name|convert
parameter_list|(
name|FsServerDefaultsWritable
name|fs
parameter_list|)
block|{
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FsServerDefaults
argument_list|(
name|fs
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|fs
operator|.
name|getBytesPerChecksum
argument_list|()
argument_list|,
name|fs
operator|.
name|getWritePacketSize
argument_list|()
argument_list|,
name|fs
operator|.
name|getReplication
argument_list|()
argument_list|,
name|fs
operator|.
name|getFileBufferSize
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convert ( org.apache.hadoop.fs.FsServerDefaults fs)
specifier|public
specifier|static
name|FsServerDefaultsWritable
name|convert
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FsServerDefaults
name|fs
parameter_list|)
block|{
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|FsServerDefaultsWritable
argument_list|(
name|fs
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|fs
operator|.
name|getBytesPerChecksum
argument_list|()
argument_list|,
name|fs
operator|.
name|getWritePacketSize
argument_list|()
argument_list|,
name|fs
operator|.
name|getReplication
argument_list|()
argument_list|,
name|fs
operator|.
name|getFileBufferSize
argument_list|()
argument_list|)
return|;
block|}
DECL|method|FsServerDefaultsWritable ()
specifier|public
name|FsServerDefaultsWritable
parameter_list|()
block|{   }
DECL|method|FsServerDefaultsWritable (long blockSize, int bytesPerChecksum, int writePacketSize, short replication, int fileBufferSize)
specifier|public
name|FsServerDefaultsWritable
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
block|}
block|}
end_class

end_unit

