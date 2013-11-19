begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|DirectDecompressor
specifier|public
interface|interface
name|DirectDecompressor
extends|extends
name|Decompressor
block|{
comment|/**    * Example usage    *     *<pre>{@code    * private void decompress(DirectDecompressor decomp, ByteBufferProducer in, ByteBufferConsumer out) throws IOException {    *    ByteBuffer outBB = ByteBuffer.allocate(64*1024);    *    outBB.clear();    *    // returns inBB.remaining()&gt; 0 || inBB == null     *    // if you do a inBB.put(), remember to do a inBB.flip()    *    ByteBuffer inBB = in.get();    *    if(inBB == null) {    *      // no data at all?    *    }    *    while(!decomp.finished()) {    *      decomp.decompress(outBB, inBB);    *      if(outBB.remaining() == 0) {    *        // flush when the buffer is full    *        outBB.flip();    *        // has to consume the buffer, because it is reused    *        out.put(outBB);    *        outBB.clear();    *      }    *      if(inBB != null&amp;&amp; inBB.remaining() == 0) {    *        // inBB = null for EOF    *        inBB = in.get();    *      }    *    }    *        *    if(outBB.position()&gt; 0) {    *      outBB.flip();    *      out.put(outBB);    *      outBB.clear();    *    }    *  }    * }</pre>    * @param dst Destination {@link ByteBuffer} for storing the results into. Requires dst.remaining() to be> 0    * @param src Source {@link ByteBuffer} for reading from. This can be null or src.remaining()> 0    * @return bytes stored into dst (dst.postion += more)    * @throws IOException if compression fails       */
DECL|method|decompress (ByteBuffer dst, ByteBuffer src)
specifier|public
name|int
name|decompress
parameter_list|(
name|ByteBuffer
name|dst
parameter_list|,
name|ByteBuffer
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

