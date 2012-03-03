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
comment|/**  * Implementers of this interface provide a read API that writes to a  * ByteBuffer, not a byte[].  */
end_comment

begin_interface
DECL|interface|ByteBufferReadable
specifier|public
interface|interface
name|ByteBufferReadable
block|{
comment|/**    * Reads up to buf.remaining() bytes into buf. Callers should use    * buf.limit(..) to control the size of the desired read.    *    * After the call, buf.position() should be unchanged, and therefore any data    * can be immediately read from buf.    *    * Many implementations will throw {@link UnsupportedOperationException}, so    * callers that are not confident in support for this method from the    * underlying filesystem should be prepared to handle that exception.    *    * @param buf    *          the ByteBuffer to receive the results of the read operation    * @return the number of bytes available to read from buf    * @throws IOException if there is some error performing the read    */
DECL|method|read (ByteBuffer buf)
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

