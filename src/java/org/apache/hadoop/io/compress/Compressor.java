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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Specification of a stream-based 'compressor' which can be    * plugged into a {@link CompressionOutputStream} to compress data.  * This is modelled after {@link java.util.zip.Deflater}  *   */
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
DECL|interface|Compressor
specifier|public
interface|interface
name|Compressor
block|{
comment|/**    * Sets input data for compression.     * This should be called whenever #needsInput() returns     *<code>true</code> indicating that more input data is required.    *     * @param b Input data    * @param off Start offset    * @param len Length    */
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
comment|/**    * Returns true if the input data buffer is empty and     * #setInput() should be called to provide more input.     *     * @return<code>true</code> if the input data buffer is empty and     * #setInput() should be called in order to provide more input.    */
DECL|method|needsInput ()
specifier|public
name|boolean
name|needsInput
parameter_list|()
function_decl|;
comment|/**    * Sets preset dictionary for compression. A preset dictionary     * is used when the history buffer can be predetermined.     *    * @param b Dictionary data bytes    * @param off Start offset    * @param len Length    */
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
comment|/**    * Return number of uncompressed bytes input so far.    */
DECL|method|getBytesRead ()
specifier|public
name|long
name|getBytesRead
parameter_list|()
function_decl|;
comment|/**    * Return number of compressed bytes output so far.    */
DECL|method|getBytesWritten ()
specifier|public
name|long
name|getBytesWritten
parameter_list|()
function_decl|;
comment|/**    * When called, indicates that compression should end    * with the current contents of the input buffer.    */
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
function_decl|;
comment|/**    * Returns true if the end of the compressed     * data output stream has been reached.    * @return<code>true</code> if the end of the compressed    * data output stream has been reached.    */
DECL|method|finished ()
specifier|public
name|boolean
name|finished
parameter_list|()
function_decl|;
comment|/**    * Fills specified buffer with compressed data. Returns actual number    * of bytes of compressed data. A return value of 0 indicates that    * needsInput() should be called in order to determine if more input    * data is required.    *     * @param b Buffer for the compressed data    * @param off Start offset of the data    * @param len Size of the buffer    * @return The actual number of bytes of compressed data.    */
DECL|method|compress (byte[] b, int off, int len)
specifier|public
name|int
name|compress
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
comment|/**    * Resets compressor so that a new set of input data can be processed.    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * Closes the compressor and discards any unprocessed input.    */
DECL|method|end ()
specifier|public
name|void
name|end
parameter_list|()
function_decl|;
comment|/**    * Prepare the compressor to be used in a new stream with settings defined in    * the given Configuration    *     * @param conf Configuration from which new setting are fetched    */
DECL|method|reinit (Configuration conf)
specifier|public
name|void
name|reinit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

