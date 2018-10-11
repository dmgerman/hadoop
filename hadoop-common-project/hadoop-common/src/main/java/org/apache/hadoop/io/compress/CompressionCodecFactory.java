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
name|util
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|fs
operator|.
name|Path
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
name|StringUtils
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
comment|/**  * A factory that will find the correct codec for a given filename.  */
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
DECL|class|CompressionCodecFactory
specifier|public
class|class
name|CompressionCodecFactory
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CompressionCodecFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|CODEC_PROVIDERS
specifier|private
specifier|static
specifier|final
name|ServiceLoader
argument_list|<
name|CompressionCodec
argument_list|>
name|CODEC_PROVIDERS
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|CompressionCodec
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A map from the reversed filename suffixes to the codecs.    * This is probably overkill, because the maps should be small, but it     * automatically supports finding the longest matching suffix.     */
DECL|field|codecs
specifier|private
name|SortedMap
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
name|codecs
init|=
literal|null
decl_stmt|;
comment|/**      * A map from the reversed filename suffixes to the codecs.      * This is probably overkill, because the maps should be small, but it      * automatically supports finding the longest matching suffix.      */
DECL|field|codecsByName
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
name|codecsByName
init|=
literal|null
decl_stmt|;
comment|/**    * A map from class names to the codecs    */
DECL|field|codecsByClassName
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
name|codecsByClassName
init|=
literal|null
decl_stmt|;
DECL|method|addCodec (CompressionCodec codec)
specifier|private
name|void
name|addCodec
parameter_list|(
name|CompressionCodec
name|codec
parameter_list|)
block|{
name|String
name|suffix
init|=
name|codec
operator|.
name|getDefaultExtension
argument_list|()
decl_stmt|;
name|codecs
operator|.
name|put
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|suffix
argument_list|)
operator|.
name|reverse
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|codecsByClassName
operator|.
name|put
argument_list|(
name|codec
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|String
name|codecName
init|=
name|codec
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|codecsByName
operator|.
name|put
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|codecName
argument_list|)
argument_list|,
name|codec
argument_list|)
expr_stmt|;
if|if
condition|(
name|codecName
operator|.
name|endsWith
argument_list|(
literal|"Codec"
argument_list|)
condition|)
block|{
name|codecName
operator|=
name|codecName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|codecName
operator|.
name|length
argument_list|()
operator|-
literal|"Codec"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|codecsByName
operator|.
name|put
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|codecName
argument_list|)
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Print the extension map out as a string.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
argument_list|>
name|itr
init|=
name|codecs
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"{ "
argument_list|)
expr_stmt|;
if|if
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
name|entry
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|entry
operator|=
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Get the list of codecs discovered via a Java ServiceLoader, or    * listed in the configuration. Codecs specified in configuration come    * later in the returned list, and are considered to override those    * from the ServiceLoader.    * @param conf the configuration to look in    * @return a list of the {@link CompressionCodec} classes    */
DECL|method|getCodecClasses ( Configuration conf)
specifier|public
specifier|static
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
argument_list|>
name|getCodecClasses
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add codec classes discovered via service loading
synchronized|synchronized
init|(
name|CODEC_PROVIDERS
init|)
block|{
comment|// CODEC_PROVIDERS is a lazy collection. Synchronize so it is
comment|// thread-safe. See HADOOP-8406.
for|for
control|(
name|CompressionCodec
name|codec
range|:
name|CODEC_PROVIDERS
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|codec
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add codec classes from configuration
name|String
name|codecsString
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODECS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|codecsString
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|codecSplit
init|=
operator|new
name|StringTokenizer
argument_list|(
name|codecsString
argument_list|,
literal|","
argument_list|)
decl_stmt|;
while|while
condition|(
name|codecSplit
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|codecSubstring
init|=
name|codecSplit
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|codecSubstring
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cls
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|codecSubstring
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|CompressionCodec
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|cls
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Class "
operator|+
name|codecSubstring
operator|+
literal|" is not a CompressionCodec"
argument_list|)
throw|;
block|}
name|result
operator|.
name|add
argument_list|(
name|cls
operator|.
name|asSubclass
argument_list|(
name|CompressionCodec
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Compression codec "
operator|+
name|codecSubstring
operator|+
literal|" not found."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Sets a list of codec classes in the configuration. In addition to any    * classes specified using this method, {@link CompressionCodec} classes on    * the classpath are discovered using a Java ServiceLoader.    * @param conf the configuration to modify    * @param classes the list of classes to set    */
DECL|method|setCodecClasses (Configuration conf, List<Class> classes)
specifier|public
specifier|static
name|void
name|setCodecClasses
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|Class
argument_list|>
name|classes
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Class
argument_list|>
name|itr
init|=
name|classes
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Class
name|cls
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|cls
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|itr
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODECS_KEY
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Find the codecs specified in the config value io.compression.codecs     * and register them. Defaults to gzip and deflate.    */
DECL|method|CompressionCodecFactory (Configuration conf)
specifier|public
name|CompressionCodecFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|codecs
operator|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
argument_list|()
expr_stmt|;
name|codecsByClassName
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
argument_list|()
expr_stmt|;
name|codecsByName
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
argument_list|>
name|codecClasses
init|=
name|getCodecClasses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|codecClasses
operator|==
literal|null
operator|||
name|codecClasses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addCodec
argument_list|(
operator|new
name|GzipCodec
argument_list|()
argument_list|)
expr_stmt|;
name|addCodec
argument_list|(
operator|new
name|DefaultCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
name|codecClass
range|:
name|codecClasses
control|)
block|{
name|addCodec
argument_list|(
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|codecClass
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Find the relevant compression codec for the given file based on its    * filename suffix.    * @param file the filename to check    * @return the codec object    */
DECL|method|getCodec (Path file)
specifier|public
name|CompressionCodec
name|getCodec
parameter_list|(
name|Path
name|file
parameter_list|)
block|{
name|CompressionCodec
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|codecs
operator|!=
literal|null
condition|)
block|{
name|String
name|filename
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|reversedFilename
init|=
operator|new
name|StringBuilder
argument_list|(
name|filename
argument_list|)
operator|.
name|reverse
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|CompressionCodec
argument_list|>
name|subMap
init|=
name|codecs
operator|.
name|headMap
argument_list|(
name|reversedFilename
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|subMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|potentialSuffix
init|=
name|subMap
operator|.
name|lastKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|reversedFilename
operator|.
name|startsWith
argument_list|(
name|potentialSuffix
argument_list|)
condition|)
block|{
name|result
operator|=
name|codecs
operator|.
name|get
argument_list|(
name|potentialSuffix
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Find the relevant compression codec for the codec's canonical class name.    * @param classname the canonical class name of the codec    * @return the codec object    */
DECL|method|getCodecByClassName (String classname)
specifier|public
name|CompressionCodec
name|getCodecByClassName
parameter_list|(
name|String
name|classname
parameter_list|)
block|{
if|if
condition|(
name|codecsByClassName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|codecsByClassName
operator|.
name|get
argument_list|(
name|classname
argument_list|)
return|;
block|}
comment|/**      * Find the relevant compression codec for the codec's canonical class name      * or by codec alias.      *<p>      * Codec aliases are case insensitive.      *<p>      * The code alias is the short class name (without the package name).      * If the short class name ends with 'Codec', then there are two aliases for      * the codec, the complete short class name and the short class name without      * the 'Codec' ending. For example for the 'GzipCodec' codec class name the      * alias are 'gzip' and 'gzipcodec'.      *      * @param codecName the canonical class name of the codec      * @return the codec object      */
DECL|method|getCodecByName (String codecName)
specifier|public
name|CompressionCodec
name|getCodecByName
parameter_list|(
name|String
name|codecName
parameter_list|)
block|{
if|if
condition|(
name|codecsByClassName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|CompressionCodec
name|codec
init|=
name|getCodecByClassName
argument_list|(
name|codecName
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
comment|// trying to get the codec by name in case the name was specified
comment|// instead a class
name|codec
operator|=
name|codecsByName
operator|.
name|get
argument_list|(
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|codecName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|codec
return|;
block|}
comment|/**      * Find the relevant compression codec for the codec's canonical class name      * or by codec alias and returns its implemetation class.      *<p>      * Codec aliases are case insensitive.      *<p>      * The code alias is the short class name (without the package name).      * If the short class name ends with 'Codec', then there are two aliases for      * the codec, the complete short class name and the short class name without      * the 'Codec' ending. For example for the 'GzipCodec' codec class name the      * alias are 'gzip' and 'gzipcodec'.      *      * @param codecName the canonical class name of the codec      * @return the codec class      */
DECL|method|getCodecClassByName ( String codecName)
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
name|getCodecClassByName
parameter_list|(
name|String
name|codecName
parameter_list|)
block|{
name|CompressionCodec
name|codec
init|=
name|getCodecByName
argument_list|(
name|codecName
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|codec
operator|.
name|getClass
argument_list|()
return|;
block|}
comment|/**    * Removes a suffix from a filename, if it has it.    * @param filename the filename to strip    * @param suffix the suffix to remove    * @return the shortened filename    */
DECL|method|removeSuffix (String filename, String suffix)
specifier|public
specifier|static
name|String
name|removeSuffix
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|filename
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
return|return
name|filename
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|filename
operator|.
name|length
argument_list|()
operator|-
name|suffix
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
name|filename
return|;
block|}
comment|/**    * A little test program.    * @param args    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|CompressionCodecFactory
name|factory
init|=
operator|new
name|CompressionCodecFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|boolean
name|encode
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
literal|"-in"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|encode
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-out"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|encode
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|CompressionCodec
name|codec
init|=
name|factory
operator|.
name|getCodec
argument_list|(
operator|new
name|Path
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Codec for "
operator|+
name|args
index|[
name|i
index|]
operator|+
literal|" not found."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|encode
condition|)
block|{
name|CompressionOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|java
operator|.
name|io
operator|.
name|InputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|codec
operator|.
name|createOutputStream
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|FileOutputStream
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|100
index|]
decl_stmt|;
name|String
name|inFilename
init|=
name|removeSuffix
argument_list|(
name|args
index|[
name|i
index|]
argument_list|,
name|codec
operator|.
name|getDefaultExtension
argument_list|()
argument_list|)
decl_stmt|;
name|in
operator|=
operator|new
name|java
operator|.
name|io
operator|.
name|FileInputStream
argument_list|(
name|inFilename
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|CompressionInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
name|codec
operator|.
name|createInputStream
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|FileInputStream
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|100
index|]
decl_stmt|;
name|int
name|len
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

