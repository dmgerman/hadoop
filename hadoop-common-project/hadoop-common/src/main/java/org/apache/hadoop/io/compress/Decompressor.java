begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Specification of a stream-based 'de-compressor' which can be    * plugged into a {@link CompressionInputStream} to compress data.  * This is modelled after {@link java.util.zip.Inflater}  *   */
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
DECL|interface|Decompressor
specifier|public
interface|interface
name|Decompressor
block|{
comment|/**    * Sets input data for decompression.     * This should be called if and only if {@link #needsInput()} returns     *<code>true</code> indicating that more input data is required.    * (Both native and non-native versions of various Decompressors require    * that the data passed in via<code>b[]</code> remain unmodified until    * the caller is explicitly notified--via {@link #needsInput()}--that the    * buffer may be safely modified.  With this requirement, an extra    * buffer-copy can be avoided.)    *     * @param b Input data    * @param off Start offset    * @param len Length    */
DECL|method|setInput (byte[] b, int off, int len)
specifier|public
name|void
name|setInput
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
function_decl|;
comment|/**    * Returns<code>true</code> if the input data buffer is empty and     * {@link #setInput(byte[], int, int)} should be called to    * provide more input.     *     * @return<code>true</code> if the input data buffer is empty and     * {@link #setInput(byte[], int, int)} should be called in    * order to provide more input.    */
DECL|method|needsInput ()
specifier|public
name|boolean
name|needsInput
parameter_list|()
function_decl|;
comment|/**    * Sets preset dictionary for compression. A preset dictionary    * is used when the history buffer can be predetermined.     *    * @param b Dictionary data bytes    * @param off Start offset    * @param len Length    */
DECL|method|setDictionary (byte[] b, int off, int len)
specifier|public
name|void
name|setDictionary
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
function_decl|;
comment|/**    * Returns<code>true</code> if a preset dictionary is needed for decompression.    * @return<code>true</code> if a preset dictionary is needed for decompression    */
DECL|method|needsDictionary ()
specifier|public
name|boolean
name|needsDictionary
parameter_list|()
function_decl|;
comment|/**    * Returns<code>true</code> if the end of the decompressed     * data output stream has been reached. Indicates a concatenated data stream    * when finished() returns<code>true</code> and {@link #getRemaining()}    * returns a positive value. finished() will be reset with the    * {@link #reset()} method.    * @return<code>true</code> if the end of the decompressed    * data output stream has been reached.    */
DECL|method|finished ()
specifier|public
name|boolean
name|finished
parameter_list|()
function_decl|;
comment|/**    * Fills specified buffer with uncompressed data. Returns actual number    * of bytes of uncompressed data. A return value of 0 indicates that    * {@link #needsInput()} should be called in order to determine if more    * input data is required.    *     * @param b Buffer for the compressed data    * @param off Start offset of the data    * @param len Size of the buffer    * @return The actual number of bytes of uncompressed data.    * @throws IOException    */
DECL|method|decompress (byte[] b, int off, int len)
specifier|public
name|int
name|decompress
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
function_decl|;
comment|/**    * Returns the number of bytes remaining in the compressed data buffer.    * Indicates a concatenated data stream if {@link #finished()} returns    *<code>true</code> and getRemaining() returns a positive value. If    * {@link #finished()} returns<code>true</code> and getRemaining() returns    * a zero value, indicates that the end of data stream has been reached and    * is not a concatenated data stream.     * @return The number of bytes remaining in the compressed data buffer.    */
DECL|method|getRemaining ()
specifier|public
name|int
name|getRemaining
parameter_list|()
function_decl|;
comment|/**    * Resets decompressor and input and output buffers so that a new set of    * input data can be processed. If {@link #finished()}} returns    *<code>true</code> and {@link #getRemaining()} returns a positive value,    * reset() is called before processing of the next data stream in the    * concatenated data stream. {@link #finished()} will be reset and will    * return<code>false</code> when reset() is called.    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * Closes the decompressor and discards any unprocessed input.    */
DECL|method|end ()
specifier|public
name|void
name|end
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

