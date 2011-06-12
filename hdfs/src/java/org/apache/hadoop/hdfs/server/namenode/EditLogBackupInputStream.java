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
name|String
name|address
decl_stmt|;
comment|// sender address
DECL|field|inner
specifier|private
name|ByteBufferInputStream
name|inner
decl_stmt|;
DECL|field|in
specifier|private
name|DataInputStream
name|in
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
DECL|method|getData ()
name|byte
index|[]
name|getData
parameter_list|()
block|{
return|return
name|super
operator|.
name|buf
return|;
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
operator|new
name|DataInputStream
argument_list|(
name|inner
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// JournalStream
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
comment|// JournalStream
DECL|method|getType ()
specifier|public
name|JournalType
name|getType
parameter_list|()
block|{
return|return
name|JournalType
operator|.
name|BACKUP
return|;
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|available
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|read (byte[] b, int off, int len)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
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
DECL|method|getDataInputStream ()
name|DataInputStream
name|getDataInputStream
parameter_list|()
block|{
return|return
name|in
return|;
block|}
DECL|method|setBytes (byte[] newBytes)
name|void
name|setBytes
parameter_list|(
name|byte
index|[]
name|newBytes
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
name|in
operator|.
name|reset
argument_list|()
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
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

