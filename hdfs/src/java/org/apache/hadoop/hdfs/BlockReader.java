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
name|java
operator|.
name|net
operator|.
name|Socket
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
name|PositionedReadable
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
name|Seekable
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
name|Seekable
extends|,
name|PositionedReadable
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
comment|/**    * Read a single byte, returning -1 at enf of stream.    */
DECL|method|read ()
name|int
name|read
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * kind of like readFully(). Only reads as much as possible.    * And allows use of protected readFully().    */
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
comment|/**    * Take the socket used to talk to the DN.    */
DECL|method|takeSocket ()
name|Socket
name|takeSocket
parameter_list|()
function_decl|;
comment|/**    * Whether the BlockReader has reached the end of its input stream    * and successfully sent a status code back to the datanode.    */
DECL|method|hasSentStatusCode ()
name|boolean
name|hasSentStatusCode
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

