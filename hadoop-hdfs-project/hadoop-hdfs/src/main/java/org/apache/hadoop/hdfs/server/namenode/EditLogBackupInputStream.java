begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
import|;
end_import

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

begin_comment
comment|/**  * An implementation of the abstract class {@link EditLogInputStream},  * which is used to updates HDFS meta-data state on a backup node.  *   * @see org.apache.hadoop.hdfs.server.protocol.NamenodeProtocol#journal  * (org.apache.hadoop.hdfs.server.protocol.NamenodeRegistration,  *  int, int, byte[])  */
end_comment

begin_class
DECL|class|EditLogBackupInputStream
class|class
name|EditLogBackupInputStream
extends|extends
name|EditLogInputStream
block|{
DECL|field|address
specifier|final
name|String
name|address
decl_stmt|;
comment|// sender address
DECL|field|inner
specifier|private
specifier|final
name|ByteBufferInputStream
name|inner
decl_stmt|;
DECL|field|in
specifier|private
name|DataInputStream
name|in
decl_stmt|;
DECL|field|reader
specifier|private
name|FSEditLogOp
operator|.
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|tracker
specifier|private
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
name|tracker
init|=
literal|null
decl_stmt|;
DECL|field|version
specifier|private
name|int
name|version
init|=
literal|0
decl_stmt|;
comment|/**    * A ByteArrayInputStream, which lets modify the underlying byte array.    */
DECL|class|ByteBufferInputStream
specifier|private
specifier|static
class|class
name|ByteBufferInputStream
extends|extends
name|ByteArrayInputStream
block|{
DECL|method|ByteBufferInputStream ()
name|ByteBufferInputStream
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|setData (byte[] newBytes)
name|void
name|setData
parameter_list|(
name|byte
index|[]
name|newBytes
parameter_list|)
block|{
name|super
operator|.
name|buf
operator|=
name|newBytes
expr_stmt|;
name|super
operator|.
name|count
operator|=
name|newBytes
operator|==
literal|null
condition|?
literal|0
else|:
name|newBytes
operator|.
name|length
expr_stmt|;
name|super
operator|.
name|mark
operator|=
literal|0
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**      * Number of bytes read from the stream so far.      */
DECL|method|length ()
name|int
name|length
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
DECL|method|EditLogBackupInputStream (String name)
name|EditLogBackupInputStream
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|address
operator|=
name|name
expr_stmt|;
name|inner
operator|=
operator|new
name|ByteBufferInputStream
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|address
return|;
block|}
annotation|@
name|Override
DECL|method|nextOp ()
specifier|protected
name|FSEditLogOp
name|nextOp
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|reader
operator|!=
literal|null
argument_list|,
literal|"Must call setBytes() before readOp()"
argument_list|)
expr_stmt|;
return|return
name|reader
operator|.
name|readOp
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextValidOp ()
specifier|protected
name|FSEditLogOp
name|nextValidOp
parameter_list|()
block|{
try|try
block|{
return|return
name|reader
operator|.
name|readOp
argument_list|(
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"got unexpected IOException "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getVersion (boolean verifyVersion)
specifier|public
name|int
name|getVersion
parameter_list|(
name|boolean
name|verifyVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
annotation|@
name|Override
DECL|method|getPosition ()
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|tracker
operator|.
name|getPos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length ()
specifier|public
name|long
name|length
parameter_list|()
throws|throws
name|IOException
block|{
comment|// file size + size of both buffers
return|return
name|inner
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|setBytes (byte[] newBytes, int version)
name|void
name|setBytes
parameter_list|(
name|byte
index|[]
name|newBytes
parameter_list|,
name|int
name|version
parameter_list|)
throws|throws
name|IOException
block|{
name|inner
operator|.
name|setData
argument_list|(
name|newBytes
argument_list|)
expr_stmt|;
name|tracker
operator|=
operator|new
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
argument_list|(
name|inner
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|reader
operator|=
operator|new
name|FSEditLogOp
operator|.
name|Reader
argument_list|(
name|in
argument_list|,
name|tracker
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
DECL|method|clear ()
name|void
name|clear
parameter_list|()
throws|throws
name|IOException
block|{
name|setBytes
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|version
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFirstTxId ()
specifier|public
name|long
name|getFirstTxId
parameter_list|()
block|{
return|return
name|HdfsConstants
operator|.
name|INVALID_TXID
return|;
block|}
annotation|@
name|Override
DECL|method|getLastTxId ()
specifier|public
name|long
name|getLastTxId
parameter_list|()
block|{
return|return
name|HdfsConstants
operator|.
name|INVALID_TXID
return|;
block|}
annotation|@
name|Override
DECL|method|isInProgress ()
specifier|public
name|boolean
name|isInProgress
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|setMaxOpSize (int maxOpSize)
specifier|public
name|void
name|setMaxOpSize
parameter_list|(
name|int
name|maxOpSize
parameter_list|)
block|{
name|reader
operator|.
name|setMaxOpSize
argument_list|(
name|maxOpSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isLocalLog ()
specifier|public
name|boolean
name|isLocalLog
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

