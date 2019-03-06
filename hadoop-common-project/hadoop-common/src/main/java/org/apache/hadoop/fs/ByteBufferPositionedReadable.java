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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Implementers of this interface provide a positioned read API that writes to a  * {@link ByteBuffer} rather than a {@code byte[]}.  *  * @see PositionedReadable  * @see ByteBufferReadable  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|ByteBufferPositionedReadable
specifier|public
interface|interface
name|ByteBufferPositionedReadable
block|{
comment|/**    * Reads up to {@code buf.remaining()} bytes into buf from a given position    * in the file and returns the number of bytes read. Callers should use    * {@code buf.limit(...)} to control the size of the desired read and    * {@code buf.position(...)} to control the offset into the buffer the data    * should be written to.    *<p>    * After a successful call, {@code buf.position()} will be advanced by the    * number of bytes read and {@code buf.limit()} should be unchanged.    *<p>    * In the case of an exception, the values of {@code buf.position()} and    * {@code buf.limit()} are undefined, and callers should be prepared to    * recover from this eventuality.    *<p>    * Many implementations will throw {@link UnsupportedOperationException}, so    * callers that are not confident in support for this method from the    * underlying filesystem should be prepared to handle that exception.    *<p>    * Implementations should treat 0-length requests as legitimate, and must not    * signal an error upon their receipt.    *    * @param position position within file    * @param buf the ByteBuffer to receive the results of the read operation.    * @return the number of bytes read, possibly zero, or -1 if reached    *         end-of-stream    * @throws IOException if there is some error performing the read    */
DECL|method|read (long position, ByteBuffer buf)
name|int
name|read
parameter_list|(
name|long
name|position
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

