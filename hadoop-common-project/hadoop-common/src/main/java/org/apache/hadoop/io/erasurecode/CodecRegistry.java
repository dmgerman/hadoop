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
name|annotations
operator|.
name|VisibleForTesting
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
name|io
operator|.
name|erasurecode
operator|.
name|rawcoder
operator|.
name|NativeRSRawErasureCoderFactory
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
name|NativeXORRawErasureCoderFactory
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
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * This class registers all coder implementations.  *  * {@link CodecRegistry} maps codec names to coder factories. All coder  * factories are dynamically identified and loaded using ServiceLoader.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CodecRegistry
specifier|public
specifier|final
class|class
name|CodecRegistry
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
name|CodecRegistry
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|instance
specifier|private
specifier|static
name|CodecRegistry
name|instance
init|=
operator|new
name|CodecRegistry
argument_list|()
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|CodecRegistry
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
DECL|field|coderMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RawErasureCoderFactory
argument_list|>
argument_list|>
name|coderMap
decl_stmt|;
DECL|field|coderNameMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|coderNameMap
decl_stmt|;
comment|// Protobuffer 2.5.0 doesn't support map<String, String[]> type well, so use
comment|// the compact value instead
DECL|field|coderNameCompactMap
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coderNameCompactMap
decl_stmt|;
DECL|method|CodecRegistry ()
specifier|private
name|CodecRegistry
parameter_list|()
block|{
name|coderMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|coderNameMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|coderNameCompactMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
specifier|final
name|ServiceLoader
argument_list|<
name|RawErasureCoderFactory
argument_list|>
name|coderFactories
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|RawErasureCoderFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|updateCoders
argument_list|(
name|coderFactories
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update coderMap and coderNameMap with iterable type of coder factories.    * @param coderFactories    */
annotation|@
name|VisibleForTesting
DECL|method|updateCoders (Iterable<RawErasureCoderFactory> coderFactories)
name|void
name|updateCoders
parameter_list|(
name|Iterable
argument_list|<
name|RawErasureCoderFactory
argument_list|>
name|coderFactories
parameter_list|)
block|{
for|for
control|(
name|RawErasureCoderFactory
name|coderFactory
range|:
name|coderFactories
control|)
block|{
name|String
name|codecName
init|=
name|coderFactory
operator|.
name|getCodecName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RawErasureCoderFactory
argument_list|>
name|coders
init|=
name|coderMap
operator|.
name|get
argument_list|(
name|codecName
argument_list|)
decl_stmt|;
if|if
condition|(
name|coders
operator|==
literal|null
condition|)
block|{
name|coders
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|coders
operator|.
name|add
argument_list|(
name|coderFactory
argument_list|)
expr_stmt|;
name|coderMap
operator|.
name|put
argument_list|(
name|codecName
argument_list|,
name|coders
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Codec registered: codec = {}, coder = {}"
argument_list|,
name|coderFactory
operator|.
name|getCodecName
argument_list|()
argument_list|,
name|coderFactory
operator|.
name|getCoderName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Boolean
name|hasConflit
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RawErasureCoderFactory
name|coder
range|:
name|coders
control|)
block|{
if|if
condition|(
name|coder
operator|.
name|getCoderName
argument_list|()
operator|.
name|equals
argument_list|(
name|coderFactory
operator|.
name|getCoderName
argument_list|()
argument_list|)
condition|)
block|{
name|hasConflit
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Coder {} cannot be registered because its coder name "
operator|+
literal|"{} has conflict with {}"
argument_list|,
name|coderFactory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|coderFactory
operator|.
name|getCoderName
argument_list|()
argument_list|,
name|coder
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|hasConflit
condition|)
block|{
comment|// set native coders as default if user does not
comment|// specify a fallback order
if|if
condition|(
name|coderFactory
operator|instanceof
name|NativeRSRawErasureCoderFactory
operator|||
name|coderFactory
operator|instanceof
name|NativeXORRawErasureCoderFactory
condition|)
block|{
name|coders
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|coderFactory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|coders
operator|.
name|add
argument_list|(
name|coderFactory
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Codec registered: codec = {}, coder = {}"
argument_list|,
name|coderFactory
operator|.
name|getCodecName
argument_list|()
argument_list|,
name|coderFactory
operator|.
name|getCoderName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// update coderNameMap accordingly
name|coderNameMap
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RawErasureCoderFactory
argument_list|>
argument_list|>
name|entry
range|:
name|coderMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|codecName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RawErasureCoderFactory
argument_list|>
name|coders
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|coderNameMap
operator|.
name|put
argument_list|(
name|codecName
argument_list|,
name|coders
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|RawErasureCoderFactory
operator|::
name|getCoderName
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|coderNameCompactMap
operator|.
name|put
argument_list|(
name|codecName
argument_list|,
name|coders
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|RawErasureCoderFactory
operator|::
name|getCoderName
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|", "
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get all coder names of the given codec.    * @param codecName the name of codec    * @return an array of all coder names, null if not exist    */
DECL|method|getCoderNames (String codecName)
specifier|public
name|String
index|[]
name|getCoderNames
parameter_list|(
name|String
name|codecName
parameter_list|)
block|{
name|String
index|[]
name|coderNames
init|=
name|coderNameMap
operator|.
name|get
argument_list|(
name|codecName
argument_list|)
decl_stmt|;
return|return
name|coderNames
return|;
block|}
comment|/**    * Get all coder factories of the given codec.    * @param codecName the name of codec    * @return a list of all coder factories, null if not exist    */
DECL|method|getCoders (String codecName)
specifier|public
name|List
argument_list|<
name|RawErasureCoderFactory
argument_list|>
name|getCoders
parameter_list|(
name|String
name|codecName
parameter_list|)
block|{
name|List
argument_list|<
name|RawErasureCoderFactory
argument_list|>
name|coders
init|=
name|coderMap
operator|.
name|get
argument_list|(
name|codecName
argument_list|)
decl_stmt|;
return|return
name|coders
return|;
block|}
comment|/**    * Get all codec names.    * @return a set of all codec names    */
DECL|method|getCodecNames ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getCodecNames
parameter_list|()
block|{
return|return
name|coderMap
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**    * Get a specific coder factory defined by codec name and coder name.    * @param codecName name of the codec    * @param coderName name of the coder    * @return the specific coder, null if not exist    */
DECL|method|getCoderByName ( String codecName, String coderName)
specifier|public
name|RawErasureCoderFactory
name|getCoderByName
parameter_list|(
name|String
name|codecName
parameter_list|,
name|String
name|coderName
parameter_list|)
block|{
name|List
argument_list|<
name|RawErasureCoderFactory
argument_list|>
name|coders
init|=
name|getCoders
argument_list|(
name|codecName
argument_list|)
decl_stmt|;
comment|// find the RawErasureCoderFactory with the name of coderName
for|for
control|(
name|RawErasureCoderFactory
name|coder
range|:
name|coders
control|)
block|{
if|if
condition|(
name|coder
operator|.
name|getCoderName
argument_list|()
operator|.
name|equals
argument_list|(
name|coderName
argument_list|)
condition|)
block|{
return|return
name|coder
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get all codec names and their corresponding coder list.    * @return a map of all codec names, and their corresponding code list    * separated by ','.    */
DECL|method|getCodec2CoderCompactMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getCodec2CoderCompactMap
parameter_list|()
block|{
return|return
name|coderNameCompactMap
return|;
block|}
block|}
end_class

end_unit

