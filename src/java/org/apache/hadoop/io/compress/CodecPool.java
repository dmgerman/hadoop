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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * A global compressor/decompressor pool used to save and reuse   * (possibly native) compression/decompression codecs.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CodecPool
specifier|public
class|class
name|CodecPool
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CodecPool
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A global compressor pool used to save the expensive     * construction/destruction of (possibly native) decompression codecs.    */
DECL|field|compressorPool
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|Compressor
argument_list|>
argument_list|,
name|List
argument_list|<
name|Compressor
argument_list|>
argument_list|>
name|compressorPool
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|Compressor
argument_list|>
argument_list|,
name|List
argument_list|<
name|Compressor
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * A global decompressor pool used to save the expensive     * construction/destruction of (possibly native) decompression codecs.    */
DECL|field|decompressorPool
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|Decompressor
argument_list|>
argument_list|,
name|List
argument_list|<
name|Decompressor
argument_list|>
argument_list|>
name|decompressorPool
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|Decompressor
argument_list|>
argument_list|,
name|List
argument_list|<
name|Decompressor
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|borrow (Map<Class<T>, List<T>> pool, Class<? extends T> codecClass)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|borrow
parameter_list|(
name|Map
argument_list|<
name|Class
argument_list|<
name|T
argument_list|>
argument_list|,
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|pool
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|codecClass
parameter_list|)
block|{
name|T
name|codec
init|=
literal|null
decl_stmt|;
comment|// Check if an appropriate codec is available
synchronized|synchronized
init|(
name|pool
init|)
block|{
if|if
condition|(
name|pool
operator|.
name|containsKey
argument_list|(
name|codecClass
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|codecList
init|=
name|pool
operator|.
name|get
argument_list|(
name|codecClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|codecList
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|codecList
init|)
block|{
if|if
condition|(
operator|!
name|codecList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|codec
operator|=
name|codecList
operator|.
name|remove
argument_list|(
name|codecList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|codec
return|;
block|}
DECL|method|payback (Map<Class<T>, List<T>> pool, T codec)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|payback
parameter_list|(
name|Map
argument_list|<
name|Class
argument_list|<
name|T
argument_list|>
argument_list|,
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|pool
parameter_list|,
name|T
name|codec
parameter_list|)
block|{
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|Class
argument_list|<
name|T
argument_list|>
name|codecClass
init|=
name|ReflectionUtils
operator|.
name|getClass
argument_list|(
name|codec
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|pool
init|)
block|{
if|if
condition|(
operator|!
name|pool
operator|.
name|containsKey
argument_list|(
name|codecClass
argument_list|)
condition|)
block|{
name|pool
operator|.
name|put
argument_list|(
name|codecClass
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|T
argument_list|>
name|codecList
init|=
name|pool
operator|.
name|get
argument_list|(
name|codecClass
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|codecList
init|)
block|{
name|codecList
operator|.
name|add
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Get a {@link Compressor} for the given {@link CompressionCodec} from the     * pool or a new one.    *    * @param codec the<code>CompressionCodec</code> for which to get the     *<code>Compressor</code>    * @param conf the<code>Configuration</code> object which contains confs for creating or reinit the compressor    * @return<code>Compressor</code> for the given     *<code>CompressionCodec</code> from the pool or a new one    */
DECL|method|getCompressor (CompressionCodec codec, Configuration conf)
specifier|public
specifier|static
name|Compressor
name|getCompressor
parameter_list|(
name|CompressionCodec
name|codec
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Compressor
name|compressor
init|=
name|borrow
argument_list|(
name|compressorPool
argument_list|,
name|codec
operator|.
name|getCompressorType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|==
literal|null
condition|)
block|{
name|compressor
operator|=
name|codec
operator|.
name|createCompressor
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got brand-new compressor ["
operator|+
name|codec
operator|.
name|getDefaultExtension
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compressor
operator|.
name|reinit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got recycled compressor"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|compressor
return|;
block|}
DECL|method|getCompressor (CompressionCodec codec)
specifier|public
specifier|static
name|Compressor
name|getCompressor
parameter_list|(
name|CompressionCodec
name|codec
parameter_list|)
block|{
return|return
name|getCompressor
argument_list|(
name|codec
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Get a {@link Decompressor} for the given {@link CompressionCodec} from the    * pool or a new one.    *      * @param codec the<code>CompressionCodec</code> for which to get the     *<code>Decompressor</code>    * @return<code>Decompressor</code> for the given     *<code>CompressionCodec</code> the pool or a new one    */
DECL|method|getDecompressor (CompressionCodec codec)
specifier|public
specifier|static
name|Decompressor
name|getDecompressor
parameter_list|(
name|CompressionCodec
name|codec
parameter_list|)
block|{
name|Decompressor
name|decompressor
init|=
name|borrow
argument_list|(
name|decompressorPool
argument_list|,
name|codec
operator|.
name|getDecompressorType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|decompressor
operator|==
literal|null
condition|)
block|{
name|decompressor
operator|=
name|codec
operator|.
name|createDecompressor
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got brand-new decompressor ["
operator|+
name|codec
operator|.
name|getDefaultExtension
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Got recycled decompressor"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|decompressor
return|;
block|}
comment|/**    * Return the {@link Compressor} to the pool.    *     * @param compressor the<code>Compressor</code> to be returned to the pool    */
DECL|method|returnCompressor (Compressor compressor)
specifier|public
specifier|static
name|void
name|returnCompressor
parameter_list|(
name|Compressor
name|compressor
parameter_list|)
block|{
if|if
condition|(
name|compressor
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|compressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|payback
argument_list|(
name|compressorPool
argument_list|,
name|compressor
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the {@link Decompressor} to the pool.    *     * @param decompressor the<code>Decompressor</code> to be returned to the     *                     pool    */
DECL|method|returnDecompressor (Decompressor decompressor)
specifier|public
specifier|static
name|void
name|returnDecompressor
parameter_list|(
name|Decompressor
name|decompressor
parameter_list|)
block|{
if|if
condition|(
name|decompressor
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|decompressor
operator|.
name|reset
argument_list|()
expr_stmt|;
name|payback
argument_list|(
name|decompressorPool
argument_list|,
name|decompressor
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

