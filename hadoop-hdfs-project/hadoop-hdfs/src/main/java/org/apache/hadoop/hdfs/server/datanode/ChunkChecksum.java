begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_comment
comment|/**  * holder class that holds checksum bytes and the length in a block at which  * the checksum bytes end  *   * ex: length = 1023 and checksum is 4 bytes which is for 512 bytes, then  *     the checksum applies for the last chunk, or bytes 512 - 1023  */
end_comment

begin_class
DECL|class|ChunkChecksum
specifier|public
class|class
name|ChunkChecksum
block|{
DECL|field|dataLength
specifier|private
specifier|final
name|long
name|dataLength
decl_stmt|;
comment|// can be null if not available
DECL|field|checksum
specifier|private
specifier|final
name|byte
index|[]
name|checksum
decl_stmt|;
DECL|method|ChunkChecksum (long dataLength, byte[] checksum)
specifier|public
name|ChunkChecksum
parameter_list|(
name|long
name|dataLength
parameter_list|,
name|byte
index|[]
name|checksum
parameter_list|)
block|{
name|this
operator|.
name|dataLength
operator|=
name|dataLength
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
name|checksum
expr_stmt|;
block|}
DECL|method|getDataLength ()
specifier|public
name|long
name|getDataLength
parameter_list|()
block|{
return|return
name|dataLength
return|;
block|}
DECL|method|getChecksum ()
specifier|public
name|byte
index|[]
name|getChecksum
parameter_list|()
block|{
return|return
name|checksum
return|;
block|}
block|}
end_class

end_unit

