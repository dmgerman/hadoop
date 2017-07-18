begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress.zlib
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
operator|.
name|zlib
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
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|compress
operator|.
name|Compressor
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
name|io
operator|.
name|compress
operator|.
name|Decompressor
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
name|io
operator|.
name|compress
operator|.
name|DirectDecompressor
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
name|io
operator|.
name|compress
operator|.
name|zlib
operator|.
name|ZlibCompressor
operator|.
name|CompressionLevel
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
name|io
operator|.
name|compress
operator|.
name|zlib
operator|.
name|ZlibCompressor
operator|.
name|CompressionStrategy
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
name|util
operator|.
name|NativeCodeLoader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A collection of factories to create the right   * zlib/gzip compressor/decompressor instances.  *   */
end_comment

begin_class
DECL|class|ZlibFactory
specifier|public
class|class
name|ZlibFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZlibFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nativeZlibLoaded
specifier|private
specifier|static
name|boolean
name|nativeZlibLoaded
init|=
literal|false
decl_stmt|;
static|static
block|{
name|loadNativeZLib
argument_list|()
expr_stmt|;
block|}
comment|/**    * Load native library and set the flag whether to use native library. The    * method is also used for reset the flag modified by setNativeZlibLoaded    */
annotation|@
name|VisibleForTesting
DECL|method|loadNativeZLib ()
specifier|public
specifier|static
name|void
name|loadNativeZLib
parameter_list|()
block|{
if|if
condition|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
name|nativeZlibLoaded
operator|=
name|ZlibCompressor
operator|.
name|isNativeZlibLoaded
argument_list|()
operator|&&
name|ZlibDecompressor
operator|.
name|isNativeZlibLoaded
argument_list|()
expr_stmt|;
if|if
condition|(
name|nativeZlibLoaded
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully loaded& initialized native-zlib library"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to load/initialize native-zlib library"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Set the flag whether to use native library. Used for testing non-native    * libraries    *    */
annotation|@
name|VisibleForTesting
DECL|method|setNativeZlibLoaded (final boolean isLoaded)
specifier|public
specifier|static
name|void
name|setNativeZlibLoaded
parameter_list|(
specifier|final
name|boolean
name|isLoaded
parameter_list|)
block|{
name|ZlibFactory
operator|.
name|nativeZlibLoaded
operator|=
name|isLoaded
expr_stmt|;
block|}
comment|/**    * Check if native-zlib code is loaded& initialized correctly and     * can be loaded for this job.    *     * @param conf configuration    * @return<code>true</code> if native-zlib is loaded& initialized     *         and can be loaded for this job, else<code>false</code>    */
DECL|method|isNativeZlibLoaded (Configuration conf)
specifier|public
specifier|static
name|boolean
name|isNativeZlibLoaded
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|nativeZlibLoaded
return|;
block|}
DECL|method|getLibraryName ()
specifier|public
specifier|static
name|String
name|getLibraryName
parameter_list|()
block|{
return|return
name|ZlibCompressor
operator|.
name|getLibraryName
argument_list|()
return|;
block|}
comment|/**    * Return the appropriate type of the zlib compressor.     *     * @param conf configuration    * @return the appropriate type of the zlib compressor.    */
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Compressor
argument_list|>
DECL|method|getZlibCompressorType (Configuration conf)
name|getZlibCompressorType
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|(
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
operator|)
condition|?
name|ZlibCompressor
operator|.
name|class
else|:
name|BuiltInZlibDeflater
operator|.
name|class
return|;
block|}
comment|/**    * Return the appropriate implementation of the zlib compressor.     *     * @param conf configuration    * @return the appropriate implementation of the zlib compressor.    */
DECL|method|getZlibCompressor (Configuration conf)
specifier|public
specifier|static
name|Compressor
name|getZlibCompressor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|(
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
operator|)
condition|?
operator|new
name|ZlibCompressor
argument_list|(
name|conf
argument_list|)
else|:
operator|new
name|BuiltInZlibDeflater
argument_list|(
name|ZlibFactory
operator|.
name|getCompressionLevel
argument_list|(
name|conf
argument_list|)
operator|.
name|compressionLevel
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Return the appropriate type of the zlib decompressor.     *     * @param conf configuration    * @return the appropriate type of the zlib decompressor.    */
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Decompressor
argument_list|>
DECL|method|getZlibDecompressorType (Configuration conf)
name|getZlibDecompressorType
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|(
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
operator|)
condition|?
name|ZlibDecompressor
operator|.
name|class
else|:
name|BuiltInZlibInflater
operator|.
name|class
return|;
block|}
comment|/**    * Return the appropriate implementation of the zlib decompressor.     *     * @param conf configuration    * @return the appropriate implementation of the zlib decompressor.    */
DECL|method|getZlibDecompressor (Configuration conf)
specifier|public
specifier|static
name|Decompressor
name|getZlibDecompressor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|(
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
operator|)
condition|?
operator|new
name|ZlibDecompressor
argument_list|()
else|:
operator|new
name|BuiltInZlibInflater
argument_list|()
return|;
block|}
comment|/**    * Return the appropriate implementation of the zlib direct decompressor.     *     * @param conf configuration    * @return the appropriate implementation of the zlib decompressor.    */
DECL|method|getZlibDirectDecompressor (Configuration conf)
specifier|public
specifier|static
name|DirectDecompressor
name|getZlibDirectDecompressor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|(
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
operator|)
condition|?
operator|new
name|ZlibDecompressor
operator|.
name|ZlibDirectDecompressor
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|setCompressionStrategy (Configuration conf, CompressionStrategy strategy)
specifier|public
specifier|static
name|void
name|setCompressionStrategy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|CompressionStrategy
name|strategy
parameter_list|)
block|{
name|conf
operator|.
name|setEnum
argument_list|(
literal|"zlib.compress.strategy"
argument_list|,
name|strategy
argument_list|)
expr_stmt|;
block|}
DECL|method|getCompressionStrategy (Configuration conf)
specifier|public
specifier|static
name|CompressionStrategy
name|getCompressionStrategy
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getEnum
argument_list|(
literal|"zlib.compress.strategy"
argument_list|,
name|CompressionStrategy
operator|.
name|DEFAULT_STRATEGY
argument_list|)
return|;
block|}
DECL|method|setCompressionLevel (Configuration conf, CompressionLevel level)
specifier|public
specifier|static
name|void
name|setCompressionLevel
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|CompressionLevel
name|level
parameter_list|)
block|{
name|conf
operator|.
name|setEnum
argument_list|(
literal|"zlib.compress.level"
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
DECL|method|getCompressionLevel (Configuration conf)
specifier|public
specifier|static
name|CompressionLevel
name|getCompressionLevel
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getEnum
argument_list|(
literal|"zlib.compress.level"
argument_list|,
name|CompressionLevel
operator|.
name|DEFAULT_COMPRESSION
argument_list|)
return|;
block|}
block|}
end_class

end_unit

