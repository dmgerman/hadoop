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
name|*
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

begin_comment
comment|/**  * Stream that permits positional reading.  *  * Implementations are required to implement thread-safe operations; this may  * be supported by concurrent access to the data, or by using a synchronization  * mechanism to serialize access.  *  * Not all implementations meet this requirement. Those that do not cannot  * be used as a backing store for some applications, such as Apache HBase.  *  * Independent of whether or not they are thread safe, some implementations  * may make the intermediate state of the system, specifically the position  * obtained in {@code Seekable.getPos()} visible.  */
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
DECL|interface|PositionedReadable
specifier|public
interface|interface
name|PositionedReadable
block|{
comment|/**    * Read up to the specified number of bytes, from a given    * position within a file, and return the number of bytes read. This does not    * change the current offset of a file, and is thread-safe.    *    *<i>Warning: Not all filesystems satisfy the thread-safety requirement.</i>    * @param position position within file    * @param buffer destination buffer    * @param offset offset in the buffer    * @param length number of bytes to read    * @return actual number of bytes read; -1 means "none"    * @throws IOException IO problems.    */
DECL|method|read (long position, byte[] buffer, int offset, int length)
name|int
name|read
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read the specified number of bytes, from a given    * position within a file. This does not    * change the current offset of a file, and is thread-safe.    *    *<i>Warning: Not all filesystems satisfy the thread-safety requirement.</i>    * @param position position within file    * @param buffer destination buffer    * @param offset offset in the buffer    * @param length number of bytes to read    * @throws IOException IO problems.    * @throws EOFException the end of the data was reached before    * the read operation completed    */
DECL|method|readFully (long position, byte[] buffer, int offset, int length)
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Read number of bytes equal to the length of the buffer, from a given    * position within a file. This does not    * change the current offset of a file, and is thread-safe.    *    *<i>Warning: Not all filesystems satisfy the thread-safety requirement.</i>    * @param position position within file    * @param buffer destination buffer    * @throws IOException IO problems.    * @throws EOFException the end of the data was reached before    * the read operation completed    */
DECL|method|readFully (long position, byte[] buffer)
name|void
name|readFully
parameter_list|(
name|long
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

