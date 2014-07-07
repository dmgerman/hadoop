begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|stream
operator|.
name|JsonReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|stream
operator|.
name|JsonWriter
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
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|KeyGenerator
import|;
end_import

begin_comment
comment|/**  * A provider of secret key material for Hadoop applications. Provides an  * abstraction to separate key storage from users of encryption. It  * is intended to support getting or storing keys in a variety of ways,  * including third party bindings.  *<P/>  *<code>KeyProvider</code> implementations must be thread safe.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|KeyProvider
specifier|public
specifier|abstract
class|class
name|KeyProvider
block|{
DECL|field|DEFAULT_CIPHER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CIPHER_NAME
init|=
literal|"hadoop.security.key.default.cipher"
decl_stmt|;
DECL|field|DEFAULT_CIPHER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_CIPHER
init|=
literal|"AES/CTR/NoPadding"
decl_stmt|;
DECL|field|DEFAULT_BITLENGTH_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_BITLENGTH_NAME
init|=
literal|"hadoop.security.key.default.bitlength"
decl_stmt|;
DECL|field|DEFAULT_BITLENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BITLENGTH
init|=
literal|256
decl_stmt|;
comment|/**    * The combination of both the key version name and the key material.    */
DECL|class|KeyVersion
specifier|public
specifier|static
class|class
name|KeyVersion
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|versionName
specifier|private
specifier|final
name|String
name|versionName
decl_stmt|;
DECL|field|material
specifier|private
specifier|final
name|byte
index|[]
name|material
decl_stmt|;
DECL|method|KeyVersion (String name, String versionName, byte[] material)
specifier|protected
name|KeyVersion
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|versionName
parameter_list|,
name|byte
index|[]
name|material
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|versionName
operator|=
name|versionName
expr_stmt|;
name|this
operator|.
name|material
operator|=
name|material
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getVersionName ()
specifier|public
name|String
name|getVersionName
parameter_list|()
block|{
return|return
name|versionName
return|;
block|}
DECL|method|getMaterial ()
specifier|public
name|byte
index|[]
name|getMaterial
parameter_list|()
block|{
return|return
name|material
return|;
block|}
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
name|buf
operator|.
name|append
argument_list|(
literal|"key("
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|versionName
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|")="
argument_list|)
expr_stmt|;
if|if
condition|(
name|material
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|byte
name|b
range|:
name|material
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|int
name|right
init|=
name|b
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
name|right
operator|<
literal|0x10
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Key metadata that is associated with the key.    */
DECL|class|Metadata
specifier|public
specifier|static
class|class
name|Metadata
block|{
DECL|field|CIPHER_FIELD
specifier|private
specifier|final
specifier|static
name|String
name|CIPHER_FIELD
init|=
literal|"cipher"
decl_stmt|;
DECL|field|BIT_LENGTH_FIELD
specifier|private
specifier|final
specifier|static
name|String
name|BIT_LENGTH_FIELD
init|=
literal|"bitLength"
decl_stmt|;
DECL|field|CREATED_FIELD
specifier|private
specifier|final
specifier|static
name|String
name|CREATED_FIELD
init|=
literal|"created"
decl_stmt|;
DECL|field|DESCRIPTION_FIELD
specifier|private
specifier|final
specifier|static
name|String
name|DESCRIPTION_FIELD
init|=
literal|"description"
decl_stmt|;
DECL|field|VERSIONS_FIELD
specifier|private
specifier|final
specifier|static
name|String
name|VERSIONS_FIELD
init|=
literal|"versions"
decl_stmt|;
DECL|field|ATTRIBUTES_FIELD
specifier|private
specifier|final
specifier|static
name|String
name|ATTRIBUTES_FIELD
init|=
literal|"attributes"
decl_stmt|;
DECL|field|cipher
specifier|private
specifier|final
name|String
name|cipher
decl_stmt|;
DECL|field|bitLength
specifier|private
specifier|final
name|int
name|bitLength
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|field|created
specifier|private
specifier|final
name|Date
name|created
decl_stmt|;
DECL|field|versions
specifier|private
name|int
name|versions
decl_stmt|;
DECL|field|attributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|method|Metadata (String cipher, int bitLength, String description, Map<String, String> attributes, Date created, int versions)
specifier|protected
name|Metadata
parameter_list|(
name|String
name|cipher
parameter_list|,
name|int
name|bitLength
parameter_list|,
name|String
name|description
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|,
name|Date
name|created
parameter_list|,
name|int
name|versions
parameter_list|)
block|{
name|this
operator|.
name|cipher
operator|=
name|cipher
expr_stmt|;
name|this
operator|.
name|bitLength
operator|=
name|bitLength
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
operator|(
name|attributes
operator|==
literal|null
operator|||
name|attributes
operator|.
name|isEmpty
argument_list|()
operator|)
condition|?
literal|null
else|:
name|attributes
expr_stmt|;
name|this
operator|.
name|created
operator|=
name|created
expr_stmt|;
name|this
operator|.
name|versions
operator|=
name|versions
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"cipher: {0}, length: {1} description: {2} created: {3} version: {4}"
argument_list|,
name|cipher
argument_list|,
name|bitLength
argument_list|,
name|description
argument_list|,
name|created
argument_list|,
name|versions
argument_list|)
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
DECL|method|getCreated ()
specifier|public
name|Date
name|getCreated
parameter_list|()
block|{
return|return
name|created
return|;
block|}
DECL|method|getCipher ()
specifier|public
name|String
name|getCipher
parameter_list|()
block|{
return|return
name|cipher
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getAttributes ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
operator|(
name|attributes
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
name|EMPTY_MAP
else|:
name|attributes
return|;
block|}
comment|/**      * Get the algorithm from the cipher.      * @return the algorithm name      */
DECL|method|getAlgorithm ()
specifier|public
name|String
name|getAlgorithm
parameter_list|()
block|{
name|int
name|slash
init|=
name|cipher
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|cipher
return|;
block|}
else|else
block|{
return|return
name|cipher
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
return|;
block|}
block|}
DECL|method|getBitLength ()
specifier|public
name|int
name|getBitLength
parameter_list|()
block|{
return|return
name|bitLength
return|;
block|}
DECL|method|getVersions ()
specifier|public
name|int
name|getVersions
parameter_list|()
block|{
return|return
name|versions
return|;
block|}
DECL|method|addVersion ()
specifier|protected
name|int
name|addVersion
parameter_list|()
block|{
return|return
name|versions
operator|++
return|;
block|}
comment|/**      * Serialize the metadata to a set of bytes.      * @return the serialized bytes      * @throws IOException      */
DECL|method|serialize ()
specifier|protected
name|byte
index|[]
name|serialize
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|JsonWriter
name|writer
init|=
operator|new
name|JsonWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|buffer
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|beginObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|cipher
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|name
argument_list|(
name|CIPHER_FIELD
argument_list|)
operator|.
name|value
argument_list|(
name|cipher
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitLength
operator|!=
literal|0
condition|)
block|{
name|writer
operator|.
name|name
argument_list|(
name|BIT_LENGTH_FIELD
argument_list|)
operator|.
name|value
argument_list|(
name|bitLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|name
argument_list|(
name|CREATED_FIELD
argument_list|)
operator|.
name|value
argument_list|(
name|created
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|description
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|name
argument_list|(
name|DESCRIPTION_FIELD
argument_list|)
operator|.
name|value
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attributes
operator|!=
literal|null
operator|&&
name|attributes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|name
argument_list|(
name|ATTRIBUTES_FIELD
argument_list|)
operator|.
name|beginObject
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
name|String
argument_list|>
name|attribute
range|:
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writer
operator|.
name|name
argument_list|(
name|attribute
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|value
argument_list|(
name|attribute
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|name
argument_list|(
name|VERSIONS_FIELD
argument_list|)
operator|.
name|value
argument_list|(
name|versions
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/**      * Deserialize a new metadata object from a set of bytes.      * @param bytes the serialized metadata      * @throws IOException      */
DECL|method|Metadata (byte[] bytes)
specifier|protected
name|Metadata
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|cipher
init|=
literal|null
decl_stmt|;
name|int
name|bitLength
init|=
literal|0
decl_stmt|;
name|Date
name|created
init|=
literal|null
decl_stmt|;
name|int
name|versions
init|=
literal|0
decl_stmt|;
name|String
name|description
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
literal|null
decl_stmt|;
name|JsonReader
name|reader
init|=
operator|new
name|JsonReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|beginObject
argument_list|()
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|field
init|=
name|reader
operator|.
name|nextName
argument_list|()
decl_stmt|;
if|if
condition|(
name|CIPHER_FIELD
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|cipher
operator|=
name|reader
operator|.
name|nextString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BIT_LENGTH_FIELD
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|bitLength
operator|=
name|reader
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CREATED_FIELD
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|created
operator|=
operator|new
name|Date
argument_list|(
name|reader
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|VERSIONS_FIELD
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|versions
operator|=
name|reader
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DESCRIPTION_FIELD
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|description
operator|=
name|reader
operator|.
name|nextString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ATTRIBUTES_FIELD
operator|.
name|equalsIgnoreCase
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|reader
operator|.
name|beginObject
argument_list|()
expr_stmt|;
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|attributes
operator|.
name|put
argument_list|(
name|reader
operator|.
name|nextName
argument_list|()
argument_list|,
name|reader
operator|.
name|nextString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|cipher
operator|=
name|cipher
expr_stmt|;
name|this
operator|.
name|bitLength
operator|=
name|bitLength
expr_stmt|;
name|this
operator|.
name|created
operator|=
name|created
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
name|this
operator|.
name|versions
operator|=
name|versions
expr_stmt|;
block|}
block|}
comment|/**    * Options when creating key objects.    */
DECL|class|Options
specifier|public
specifier|static
class|class
name|Options
block|{
DECL|field|cipher
specifier|private
name|String
name|cipher
decl_stmt|;
DECL|field|bitLength
specifier|private
name|int
name|bitLength
decl_stmt|;
DECL|field|description
specifier|private
name|String
name|description
decl_stmt|;
DECL|field|attributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|method|Options (Configuration conf)
specifier|public
name|Options
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|cipher
operator|=
name|conf
operator|.
name|get
argument_list|(
name|DEFAULT_CIPHER_NAME
argument_list|,
name|DEFAULT_CIPHER
argument_list|)
expr_stmt|;
name|bitLength
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DEFAULT_BITLENGTH_NAME
argument_list|,
name|DEFAULT_BITLENGTH
argument_list|)
expr_stmt|;
block|}
DECL|method|setCipher (String cipher)
specifier|public
name|Options
name|setCipher
parameter_list|(
name|String
name|cipher
parameter_list|)
block|{
name|this
operator|.
name|cipher
operator|=
name|cipher
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBitLength (int bitLength)
specifier|public
name|Options
name|setBitLength
parameter_list|(
name|int
name|bitLength
parameter_list|)
block|{
name|this
operator|.
name|bitLength
operator|=
name|bitLength
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDescription (String description)
specifier|public
name|Options
name|setDescription
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setAttributes (Map<String, String> attributes)
specifier|public
name|Options
name|setAttributes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
literal|null
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"attributes cannot have a NULL key"
argument_list|)
throw|;
block|}
name|this
operator|.
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|getCipher ()
specifier|public
name|String
name|getCipher
parameter_list|()
block|{
return|return
name|cipher
return|;
block|}
DECL|method|getBitLength ()
specifier|public
name|int
name|getBitLength
parameter_list|()
block|{
return|return
name|bitLength
return|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getAttributes ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
operator|(
name|attributes
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
name|EMPTY_MAP
else|:
name|attributes
return|;
block|}
block|}
comment|/**    * A helper function to create an options object.    * @param conf the configuration to use    * @return a new options object    */
DECL|method|options (Configuration conf)
specifier|public
specifier|static
name|Options
name|options
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|Options
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Indicates whether this provider represents a store    * that is intended for transient use - such as the UserProvider    * is. These providers are generally used to provide access to    * keying material rather than for long term storage.    * @return true if transient, false otherwise    */
DECL|method|isTransient ()
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Get the key material for a specific version of the key. This method is used    * when decrypting data.    * @param versionName the name of a specific version of the key    * @return the key material    * @throws IOException    */
DECL|method|getKeyVersion (String versionName )
specifier|public
specifier|abstract
name|KeyVersion
name|getKeyVersion
parameter_list|(
name|String
name|versionName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the key names for all keys.    * @return the list of key names    * @throws IOException    */
DECL|method|getKeys ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getKeys
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get key metadata in bulk.    * @param names the names of the keys to get    * @throws IOException    */
DECL|method|getKeysMetadata (String... names)
specifier|public
name|Metadata
index|[]
name|getKeysMetadata
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
index|[]
name|result
init|=
operator|new
name|Metadata
index|[
name|names
operator|.
name|length
index|]
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
name|names
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|getMetadata
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Get the key material for all versions of a specific key name.    * @return the list of key material    * @throws IOException    */
DECL|method|getKeyVersions (String name)
specifier|public
specifier|abstract
name|List
argument_list|<
name|KeyVersion
argument_list|>
name|getKeyVersions
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the current version of the key, which should be used for encrypting new    * data.    * @param name the base name of the key    * @return the version name of the current version of the key or null if the    *    key version doesn't exist    * @throws IOException    */
DECL|method|getCurrentKey (String name)
specifier|public
name|KeyVersion
name|getCurrentKey
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
name|meta
init|=
name|getMetadata
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|meta
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|getKeyVersion
argument_list|(
name|buildVersionName
argument_list|(
name|name
argument_list|,
name|meta
operator|.
name|getVersions
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get metadata about the key.    * @param name the basename of the key    * @return the key's metadata or null if the key doesn't exist    * @throws IOException    */
DECL|method|getMetadata (String name)
specifier|public
specifier|abstract
name|Metadata
name|getMetadata
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a new key. The given key must not already exist.    * @param name the base name of the key    * @param material the key material for the first version of the key.    * @param options the options for the new key.    * @return the version name of the first version of the key.    * @throws IOException    */
DECL|method|createKey (String name, byte[] material, Options options)
specifier|public
specifier|abstract
name|KeyVersion
name|createKey
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|,
name|Options
name|options
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the algorithm from the cipher.    *    * @return the algorithm name    */
DECL|method|getAlgorithm (String cipher)
specifier|private
name|String
name|getAlgorithm
parameter_list|(
name|String
name|cipher
parameter_list|)
block|{
name|int
name|slash
init|=
name|cipher
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|cipher
return|;
block|}
else|else
block|{
return|return
name|cipher
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
return|;
block|}
block|}
comment|/**    * Generates a key material.    *    * @param size length of the key.    * @param algorithm algorithm to use for generating the key.    * @return the generated key.    * @throws NoSuchAlgorithmException    */
DECL|method|generateKey (int size, String algorithm)
specifier|protected
name|byte
index|[]
name|generateKey
parameter_list|(
name|int
name|size
parameter_list|,
name|String
name|algorithm
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
block|{
name|algorithm
operator|=
name|getAlgorithm
argument_list|(
name|algorithm
argument_list|)
expr_stmt|;
name|KeyGenerator
name|keyGenerator
init|=
name|KeyGenerator
operator|.
name|getInstance
argument_list|(
name|algorithm
argument_list|)
decl_stmt|;
name|keyGenerator
operator|.
name|init
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|byte
index|[]
name|key
init|=
name|keyGenerator
operator|.
name|generateKey
argument_list|()
operator|.
name|getEncoded
argument_list|()
decl_stmt|;
return|return
name|key
return|;
block|}
comment|/**    * Create a new key generating the material for it.    * The given key must not already exist.    *<p/>    * This implementation generates the key material and calls the    * {@link #createKey(String, byte[], Options)} method.    *    * @param name the base name of the key    * @param options the options for the new key.    * @return the version name of the first version of the key.    * @throws IOException    * @throws NoSuchAlgorithmException    */
DECL|method|createKey (String name, Options options)
specifier|public
name|KeyVersion
name|createKey
parameter_list|(
name|String
name|name
parameter_list|,
name|Options
name|options
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
name|byte
index|[]
name|material
init|=
name|generateKey
argument_list|(
name|options
operator|.
name|getBitLength
argument_list|()
argument_list|,
name|options
operator|.
name|getCipher
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|createKey
argument_list|(
name|name
argument_list|,
name|material
argument_list|,
name|options
argument_list|)
return|;
block|}
comment|/**    * Delete the given key.    * @param name the name of the key to delete    * @throws IOException    */
DECL|method|deleteKey (String name)
specifier|public
specifier|abstract
name|void
name|deleteKey
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Roll a new version of the given key.    * @param name the basename of the key    * @param material the new key material    * @return the name of the new version of the key    * @throws IOException    */
DECL|method|rollNewVersion (String name, byte[] material )
specifier|public
specifier|abstract
name|KeyVersion
name|rollNewVersion
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Roll a new version of the given key generating the material for it.    *<p/>    * This implementation generates the key material and calls the    * {@link #rollNewVersion(String, byte[])} method.    *    * @param name the basename of the key    * @return the name of the new version of the key    * @throws IOException    */
DECL|method|rollNewVersion (String name)
specifier|public
name|KeyVersion
name|rollNewVersion
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
name|Metadata
name|meta
init|=
name|getMetadata
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|material
init|=
name|generateKey
argument_list|(
name|meta
operator|.
name|getBitLength
argument_list|()
argument_list|,
name|meta
operator|.
name|getCipher
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|rollNewVersion
argument_list|(
name|name
argument_list|,
name|material
argument_list|)
return|;
block|}
comment|/**    * Ensures that any changes to the keys are written to persistent store.    * @throws IOException    */
DECL|method|flush ()
specifier|public
specifier|abstract
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Split the versionName in to a base name. Converts "/aaa/bbb/3" to    * "/aaa/bbb".    * @param versionName the version name to split    * @return the base name of the key    * @throws IOException    */
DECL|method|getBaseName (String versionName)
specifier|public
specifier|static
name|String
name|getBaseName
parameter_list|(
name|String
name|versionName
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|div
init|=
name|versionName
operator|.
name|lastIndexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
name|div
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No version in key path "
operator|+
name|versionName
argument_list|)
throw|;
block|}
return|return
name|versionName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|div
argument_list|)
return|;
block|}
comment|/**    * Build a version string from a basename and version number. Converts    * "/aaa/bbb" and 3 to "/aaa/bbb@3".    * @param name the basename of the key    * @param version the version of the key    * @return the versionName of the key.    */
DECL|method|buildVersionName (String name, int version)
specifier|protected
specifier|static
name|String
name|buildVersionName
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|version
parameter_list|)
block|{
return|return
name|name
operator|+
literal|"@"
operator|+
name|version
return|;
block|}
comment|/**    * Find the provider with the given key.    * @param providerList the list of providers    * @param keyName the key name we are looking for    * @return the KeyProvider that has the key    */
DECL|method|findProvider (List<KeyProvider> providerList, String keyName)
specifier|public
specifier|static
name|KeyProvider
name|findProvider
parameter_list|(
name|List
argument_list|<
name|KeyProvider
argument_list|>
name|providerList
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|KeyProvider
name|provider
range|:
name|providerList
control|)
block|{
if|if
condition|(
name|provider
operator|.
name|getMetadata
argument_list|(
name|keyName
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|provider
return|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't find KeyProvider for key "
operator|+
name|keyName
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

