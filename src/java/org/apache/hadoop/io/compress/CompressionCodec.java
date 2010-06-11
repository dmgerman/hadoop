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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
comment|/**  * This class encapsulates a streaming compression/decompression pair.  */
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
DECL|interface|CompressionCodec
specifier|public
interface|interface
name|CompressionCodec
block|{
comment|/**    * Create a {@link CompressionOutputStream} that will write to the given     * {@link OutputStream}.    *     * @param out the location for the final output stream    * @return a stream the user can write uncompressed data to have it compressed    * @throws IOException    */
DECL|method|createOutputStream (OutputStream out)
name|CompressionOutputStream
name|createOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link CompressionOutputStream} that will write to the given     * {@link OutputStream} with the given {@link Compressor}.    *     * @param out the location for the final output stream    * @param compressor compressor to use    * @return a stream the user can write uncompressed data to have it compressed    * @throws IOException    */
DECL|method|createOutputStream (OutputStream out, Compressor compressor)
name|CompressionOutputStream
name|createOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|Compressor
name|compressor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the type of {@link Compressor} needed by this {@link CompressionCodec}.    *     * @return the type of compressor needed by this codec.    */
DECL|method|getCompressorType ()
name|Class
argument_list|<
name|?
extends|extends
name|Compressor
argument_list|>
name|getCompressorType
parameter_list|()
function_decl|;
comment|/**    * Create a new {@link Compressor} for use by this {@link CompressionCodec}.    *     * @return a new compressor for use by this codec    */
DECL|method|createCompressor ()
name|Compressor
name|createCompressor
parameter_list|()
function_decl|;
comment|/**    * Create a stream decompressor that will read from the given input stream.    *     * @param in the stream to read compressed bytes from    * @return a stream to read uncompressed bytes from    * @throws IOException    */
DECL|method|createInputStream (InputStream in)
name|CompressionInputStream
name|createInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a {@link CompressionInputStream} that will read from the given     * {@link InputStream} with the given {@link Decompressor}.    *     * @param in the stream to read compressed bytes from    * @param decompressor decompressor to use    * @return a stream to read uncompressed bytes from    * @throws IOException    */
DECL|method|createInputStream (InputStream in, Decompressor decompressor)
name|CompressionInputStream
name|createInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|Decompressor
name|decompressor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the type of {@link Decompressor} needed by this {@link CompressionCodec}.    *     * @return the type of decompressor needed by this codec.    */
DECL|method|getDecompressorType ()
name|Class
argument_list|<
name|?
extends|extends
name|Decompressor
argument_list|>
name|getDecompressorType
parameter_list|()
function_decl|;
comment|/**    * Create a new {@link Decompressor} for use by this {@link CompressionCodec}.    *     * @return a new decompressor for use by this codec    */
DECL|method|createDecompressor ()
name|Decompressor
name|createDecompressor
parameter_list|()
function_decl|;
comment|/**    * Get the default filename extension for this kind of compression.    * @return the extension including the '.'    */
DECL|method|getDefaultExtension ()
name|String
name|getDefaultExtension
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

