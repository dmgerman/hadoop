begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|fs
operator|.
name|ByteBufferReadable
import|;
end_import

begin_comment
comment|/**  * A BlockReader is responsible for reading a single block  * from a single datanode.  */
end_comment

begin_interface
DECL|interface|BlockReader
specifier|public
interface|interface
name|BlockReader
extends|extends
name|ByteBufferReadable
block|{
comment|/* same interface as inputStream java.io.InputStream#read()    * used by DFSInputStream#read()    * This violates one rule when there is a checksum error:    * "Read should not modify user buffer before successful read"    * because it first reads the data to user buffer and then checks    * the checksum.    */
DECL|method|read (byte[] buf, int off, int len)
name|int
name|read
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Skip the given number of bytes    */
DECL|method|skip (long n)
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Close the block reader.    *    * @param peerCache      The PeerCache to put the Peer we're using back    *                       into, or null if we should simply close the Peer    *                       we're using (along with its Socket).    *                       Some block readers, like BlockReaderLocal, may    *                       not make use of this parameter.    *    * @throws IOException    */
DECL|method|close (PeerCache peerCache)
name|void
name|close
parameter_list|(
name|PeerCache
name|peerCache
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read exactly the given amount of data, throwing an exception    * if EOF is reached before that amount    */
DECL|method|readFully (byte[] buf, int readOffset, int amtToRead)
name|void
name|readFully
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|readOffset
parameter_list|,
name|int
name|amtToRead
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Similar to {@link #readFully(byte[], int, int)} except that it will    * not throw an exception on EOF. However, it differs from the simple    * {@link #read(byte[], int, int)} call in that it is guaranteed to    * read the data if it is available. In other words, if this call    * does not throw an exception, then either the buffer has been    * filled or the next call will return EOF.    */
DECL|method|readAll (byte[] buf, int offset, int len)
name|int
name|readAll
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

