begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|erasurecode
operator|.
name|codec
operator|.
name|ErasureCodec
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
name|erasurecode
operator|.
name|codec
operator|.
name|HHXORErasureCodec
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
name|erasurecode
operator|.
name|codec
operator|.
name|RSErasureCodec
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
name|erasurecode
operator|.
name|codec
operator|.
name|XORErasureCodec
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
name|erasurecode
operator|.
name|coder
operator|.
name|ErasureDecoder
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
name|erasurecode
operator|.
name|coder
operator|.
name|ErasureEncoder
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RSRawErasureCoderFactory
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RSRawErasureCoderFactoryLegacy
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RawErasureCoderFactory
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RawErasureDecoder
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RawErasureEncoder
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|XORRawErasureCoderFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_comment
comment|/**  * A codec& coder utility to help create coders conveniently.  *  * {@link CodecUtil} includes erasure coder configurations key and default  * values such as coder class name and erasure codec option values included  * by {@link ErasureCodecOptions}. {@link ErasureEncoder} and  * {@link ErasureDecoder} are created by createEncoder and createDecoder  * respectively.{@link RawErasureEncoder} and {@link RawErasureDecoder} are  * are created by createRawEncoder and createRawDecoder.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CodecUtil
specifier|public
specifier|final
class|class
name|CodecUtil
block|{
comment|/** Erasure coder XOR codec. */
DECL|field|IO_ERASURECODE_CODEC_XOR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_XOR_KEY
init|=
literal|"io.erasurecode.codec.xor"
decl_stmt|;
DECL|field|IO_ERASURECODE_CODEC_XOR
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_XOR
init|=
name|XORErasureCodec
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|/** Erasure coder Reed-Solomon codec. */
DECL|field|IO_ERASURECODE_CODEC_RS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_RS_KEY
init|=
literal|"io.erasurecode.codec.rs"
decl_stmt|;
DECL|field|IO_ERASURECODE_CODEC_RS
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_RS
init|=
name|RSErasureCodec
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|/** Erasure coder hitch hiker XOR codec. */
DECL|field|IO_ERASURECODE_CODEC_HHXOR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_HHXOR_KEY
init|=
literal|"io.erasurecode.codec.hhxor"
decl_stmt|;
DECL|field|IO_ERASURECODE_CODEC_HHXOR
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_HHXOR
init|=
name|HHXORErasureCodec
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|/** Supported erasure codec classes. */
comment|/** Raw coder factory for the RS codec. */
DECL|field|IO_ERASURECODE_CODEC_RS_RAWCODER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_RS_RAWCODER_KEY
init|=
literal|"io.erasurecode.codec.rs.rawcoder"
decl_stmt|;
DECL|field|IO_ERASURECODE_CODEC_RS_RAWCODER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_RS_RAWCODER_DEFAULT
init|=
name|RSRawErasureCoderFactory
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|/** Raw coder factory for the RS legacy codec. */
DECL|field|IO_ERASURECODE_CODEC_RS_LEGACY_RAWCODER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_RS_LEGACY_RAWCODER_KEY
init|=
literal|"io.erasurecode.codec.rs-legacy.rawcoder"
decl_stmt|;
DECL|field|IO_ERASURECODE_CODEC_RS_LEGACY_RAWCODER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_RS_LEGACY_RAWCODER_DEFAULT
init|=
name|RSRawErasureCoderFactoryLegacy
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
comment|/** Raw coder factory for the XOR codec. */
DECL|field|IO_ERASURECODE_CODEC_XOR_RAWCODER_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_XOR_RAWCODER_KEY
init|=
literal|"io.erasurecode.codec.xor.rawcoder"
decl_stmt|;
DECL|field|IO_ERASURECODE_CODEC_XOR_RAWCODER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|IO_ERASURECODE_CODEC_XOR_RAWCODER_DEFAULT
init|=
name|XORRawErasureCoderFactory
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
DECL|method|CodecUtil ()
specifier|private
name|CodecUtil
parameter_list|()
block|{ }
comment|/**    * Create encoder corresponding to given codec.    * @param options Erasure codec options    * @return erasure encoder    */
DECL|method|createEncoder (Configuration conf, ErasureCodecOptions options)
specifier|public
specifier|static
name|ErasureEncoder
name|createEncoder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ErasureCodecOptions
name|options
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|String
name|codecKey
init|=
name|getCodecClassName
argument_list|(
name|conf
argument_list|,
name|options
operator|.
name|getSchema
argument_list|()
operator|.
name|getCodecName
argument_list|()
argument_list|)
decl_stmt|;
name|ErasureCodec
name|codec
init|=
name|createCodec
argument_list|(
name|conf
argument_list|,
name|codecKey
argument_list|,
name|options
argument_list|)
decl_stmt|;
return|return
name|codec
operator|.
name|createEncoder
argument_list|()
return|;
block|}
comment|/**    * Create decoder corresponding to given codec.    * @param options Erasure codec options    * @return erasure decoder    */
DECL|method|createDecoder (Configuration conf, ErasureCodecOptions options)
specifier|public
specifier|static
name|ErasureDecoder
name|createDecoder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ErasureCodecOptions
name|options
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|String
name|codecKey
init|=
name|getCodecClassName
argument_list|(
name|conf
argument_list|,
name|options
operator|.
name|getSchema
argument_list|()
operator|.
name|getCodecName
argument_list|()
argument_list|)
decl_stmt|;
name|ErasureCodec
name|codec
init|=
name|createCodec
argument_list|(
name|conf
argument_list|,
name|codecKey
argument_list|,
name|options
argument_list|)
decl_stmt|;
return|return
name|codec
operator|.
name|createDecoder
argument_list|()
return|;
block|}
comment|/**    * Create RS raw encoder according to configuration.    * @param conf configuration    * @param coderOptions coder options that's used to create the coder    * @param codec the codec to use. If null, will use the default codec    * @return raw encoder    */
DECL|method|createRawEncoder ( Configuration conf, String codec, ErasureCoderOptions coderOptions)
specifier|public
specifier|static
name|RawErasureEncoder
name|createRawEncoder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|codec
parameter_list|,
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|String
name|rawCoderFactoryKey
init|=
name|getRawCoderFactNameFromCodec
argument_list|(
name|conf
argument_list|,
name|codec
argument_list|)
decl_stmt|;
name|RawErasureCoderFactory
name|fact
init|=
name|createRawCoderFactory
argument_list|(
name|conf
argument_list|,
name|rawCoderFactoryKey
argument_list|)
decl_stmt|;
return|return
name|fact
operator|.
name|createEncoder
argument_list|(
name|coderOptions
argument_list|)
return|;
block|}
comment|/**    * Create RS raw decoder according to configuration.    * @param conf configuration    * @param coderOptions coder options that's used to create the coder    * @param codec the codec to use. If null, will use the default codec    * @return raw decoder    */
DECL|method|createRawDecoder ( Configuration conf, String codec, ErasureCoderOptions coderOptions)
specifier|public
specifier|static
name|RawErasureDecoder
name|createRawDecoder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|codec
parameter_list|,
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|String
name|rawCoderFactoryKey
init|=
name|getRawCoderFactNameFromCodec
argument_list|(
name|conf
argument_list|,
name|codec
argument_list|)
decl_stmt|;
name|RawErasureCoderFactory
name|fact
init|=
name|createRawCoderFactory
argument_list|(
name|conf
argument_list|,
name|rawCoderFactoryKey
argument_list|)
decl_stmt|;
return|return
name|fact
operator|.
name|createDecoder
argument_list|(
name|coderOptions
argument_list|)
return|;
block|}
DECL|method|createRawCoderFactory ( Configuration conf, String rawCoderFactoryKey)
specifier|private
specifier|static
name|RawErasureCoderFactory
name|createRawCoderFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|rawCoderFactoryKey
parameter_list|)
block|{
name|RawErasureCoderFactory
name|fact
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|RawErasureCoderFactory
argument_list|>
name|factClass
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|rawCoderFactoryKey
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|RawErasureCoderFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|fact
operator|=
name|factClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
decl||
name|InstantiationException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create raw coder factory"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|fact
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create raw coder factory"
argument_list|)
throw|;
block|}
return|return
name|fact
return|;
block|}
DECL|method|getRawCoderFactNameFromCodec (Configuration conf, String codec)
specifier|private
specifier|static
name|String
name|getRawCoderFactNameFromCodec
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|codec
parameter_list|)
block|{
switch|switch
condition|(
name|codec
condition|)
block|{
case|case
name|ErasureCodeConstants
operator|.
name|RS_CODEC_NAME
case|:
return|return
name|conf
operator|.
name|get
argument_list|(
name|IO_ERASURECODE_CODEC_RS_RAWCODER_KEY
argument_list|,
name|IO_ERASURECODE_CODEC_RS_RAWCODER_DEFAULT
argument_list|)
return|;
case|case
name|ErasureCodeConstants
operator|.
name|RS_LEGACY_CODEC_NAME
case|:
return|return
name|conf
operator|.
name|get
argument_list|(
name|IO_ERASURECODE_CODEC_RS_LEGACY_RAWCODER_KEY
argument_list|,
name|IO_ERASURECODE_CODEC_RS_LEGACY_RAWCODER_DEFAULT
argument_list|)
return|;
case|case
name|ErasureCodeConstants
operator|.
name|XOR_CODEC_NAME
case|:
return|return
name|conf
operator|.
name|get
argument_list|(
name|IO_ERASURECODE_CODEC_XOR_RAWCODER_KEY
argument_list|,
name|IO_ERASURECODE_CODEC_XOR_RAWCODER_DEFAULT
argument_list|)
return|;
default|default:
comment|// For custom codec, we throw exception if the factory is not configured
name|String
name|rawCoderKey
init|=
literal|"io.erasurecode.codec."
operator|+
name|codec
operator|+
literal|".rawcoder"
decl_stmt|;
name|String
name|factName
init|=
name|conf
operator|.
name|get
argument_list|(
name|rawCoderKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|factName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Raw coder factory not configured "
operator|+
literal|"for custom codec "
operator|+
name|codec
argument_list|)
throw|;
block|}
return|return
name|factName
return|;
block|}
block|}
DECL|method|createCodec (Configuration conf, String codecClassName, ErasureCodecOptions options)
specifier|private
specifier|static
name|ErasureCodec
name|createCodec
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|codecClassName
parameter_list|,
name|ErasureCodecOptions
name|options
parameter_list|)
block|{
name|ErasureCodec
name|codec
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|ErasureCodec
argument_list|>
name|codecClass
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|codecClassName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|ErasureCodec
operator|.
name|class
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|ErasureCodec
argument_list|>
name|constructor
init|=
name|codecClass
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|ErasureCodecOptions
operator|.
name|class
argument_list|)
decl_stmt|;
name|codec
operator|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
decl||
name|InstantiationException
decl||
name|IllegalAccessException
decl||
name|NoSuchMethodException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create erasure codec"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create erasure codec"
argument_list|)
throw|;
block|}
return|return
name|codec
return|;
block|}
DECL|method|getCodecClassName (Configuration conf, String codec)
specifier|private
specifier|static
name|String
name|getCodecClassName
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|codec
parameter_list|)
block|{
switch|switch
condition|(
name|codec
condition|)
block|{
case|case
name|ErasureCodeConstants
operator|.
name|RS_CODEC_NAME
case|:
return|return
name|conf
operator|.
name|get
argument_list|(
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_RS_KEY
argument_list|,
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_RS
argument_list|)
return|;
case|case
name|ErasureCodeConstants
operator|.
name|RS_LEGACY_CODEC_NAME
case|:
comment|//TODO:rs-legacy should be handled differently.
return|return
name|conf
operator|.
name|get
argument_list|(
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_RS_KEY
argument_list|,
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_RS
argument_list|)
return|;
case|case
name|ErasureCodeConstants
operator|.
name|XOR_CODEC_NAME
case|:
return|return
name|conf
operator|.
name|get
argument_list|(
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_XOR_KEY
argument_list|,
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_XOR
argument_list|)
return|;
case|case
name|ErasureCodeConstants
operator|.
name|HHXOR_CODEC_NAME
case|:
return|return
name|conf
operator|.
name|get
argument_list|(
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_HHXOR_KEY
argument_list|,
name|CodecUtil
operator|.
name|IO_ERASURECODE_CODEC_HHXOR
argument_list|)
return|;
default|default:
comment|// For custom codec, we throw exception if the factory is not configured
name|String
name|codecKey
init|=
literal|"io.erasurecode.codec."
operator|+
name|codec
operator|+
literal|".coder"
decl_stmt|;
name|String
name|codecClass
init|=
name|conf
operator|.
name|get
argument_list|(
name|codecKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|codecClass
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Codec not configured "
operator|+
literal|"for custom codec "
operator|+
name|codec
argument_list|)
throw|;
block|}
return|return
name|codecClass
return|;
block|}
block|}
block|}
end_class

end_unit

