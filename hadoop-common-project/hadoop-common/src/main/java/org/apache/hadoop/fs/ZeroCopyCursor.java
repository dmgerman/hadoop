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
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
comment|/**  * A ZeroCopyCursor allows you to make zero-copy reads.  *   * Cursors should be closed when they are no longer needed.  *   * Example:  *   FSDataInputStream fis = fs.open("/file");  *   ZeroCopyCursor cursor = fis.createZeroCopyCursor();  *   try {  *     cursor.read(128);  *     ByteBuffer data = cursor.getData();  *     processData(data);  *   } finally {  *     cursor.close();  *   }  */
end_comment

begin_interface
DECL|interface|ZeroCopyCursor
specifier|public
interface|interface
name|ZeroCopyCursor
extends|extends
name|Closeable
block|{
comment|/**    * Set the fallback buffer used for this zero copy cursor.    * The fallback buffer is used when a true zero-copy read is impossible.    * If there is no fallback buffer, UnsupportedOperationException is thrown    * when a true zero-copy read cannot be done.    *     * @param fallbackBuffer          The fallback buffer to set, or null for none.    */
DECL|method|setFallbackBuffer (ByteBuffer fallbackBuffer)
specifier|public
name|void
name|setFallbackBuffer
parameter_list|(
name|ByteBuffer
name|fallbackBuffer
parameter_list|)
function_decl|;
comment|/**    * @return the fallback buffer in use, or null if there is none.    */
DECL|method|getFallbackBuffer ()
specifier|public
name|ByteBuffer
name|getFallbackBuffer
parameter_list|()
function_decl|;
comment|/**    * @param skipChecksums   Whether we should skip checksumming with this     *                        zero copy cursor.    */
DECL|method|setSkipChecksums (boolean skipChecksums)
specifier|public
name|void
name|setSkipChecksums
parameter_list|(
name|boolean
name|skipChecksums
parameter_list|)
function_decl|;
comment|/**    * @return                Whether we should skip checksumming with this    *                        zero copy cursor.    */
DECL|method|getSkipChecksums ()
specifier|public
name|boolean
name|getSkipChecksums
parameter_list|()
function_decl|;
comment|/**    * @param allowShortReads   Whether we should allow short reads.    */
DECL|method|setAllowShortReads (boolean allowShortReads)
specifier|public
name|void
name|setAllowShortReads
parameter_list|(
name|boolean
name|allowShortReads
parameter_list|)
function_decl|;
comment|/**    * @return                  Whether we should allow short reads.    */
DECL|method|getAllowShortReads ()
specifier|public
name|boolean
name|getAllowShortReads
parameter_list|()
function_decl|;
comment|/**    * Perform a zero-copy read.    *    * @param toRead          The minimum number of bytes to read.    *                        Must not be negative.  If we hit EOF before    *                        reading this many bytes, we will either throw    *                        EOFException (if allowShortReads = false), or    *                        return a short read (if allowShortReads = true).    *                        A short read could be as short as 0 bytes.    * @throws UnsupportedOperationException    *             If a true zero-copy read cannot be done, and no fallback    *             buffer was set.    * @throws EOFException    *             If allowShortReads = false, and we can't read all the bytes    *             that were requested.  This will never be thrown if    *             allowShortReads = true.    * @throws IOException    *             If there was an error while reading the data.    */
DECL|method|read (int toRead)
specifier|public
name|void
name|read
parameter_list|(
name|int
name|toRead
parameter_list|)
throws|throws
name|UnsupportedOperationException
throws|,
name|EOFException
throws|,
name|IOException
function_decl|;
comment|/**    * Get the current data buffer.    *    * This buffer will remain valid until either this cursor is closed, or we    * call read() again on this same cursor.  You can find the amount of data    * that was read previously by calling ByteBuffer#remaining.    *     * @return                The current data buffer.    */
DECL|method|getData ()
specifier|public
name|ByteBuffer
name|getData
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

