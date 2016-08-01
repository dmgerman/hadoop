begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|UnsupportedCharsetException
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|io
operator|.
name|serializer
operator|.
name|Deserializer
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
name|serializer
operator|.
name|Serialization
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
name|serializer
operator|.
name|SerializationFactory
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
name|serializer
operator|.
name|Serializer
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
name|GenericsUtil
import|;
end_import

begin_comment
comment|/**  * DefaultStringifier is the default implementation of the {@link Stringifier}  * interface which stringifies the objects using base64 encoding of the  * serialized version of the objects. The {@link Serializer} and  * {@link Deserializer} are obtained from the {@link SerializationFactory}.  *<br>  * DefaultStringifier offers convenience methods to store/load objects to/from  * the configuration.  *   * @param<T> the class of the objects to stringify  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|DefaultStringifier
specifier|public
class|class
name|DefaultStringifier
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Stringifier
argument_list|<
name|T
argument_list|>
block|{
DECL|field|SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|","
decl_stmt|;
DECL|field|serializer
specifier|private
name|Serializer
argument_list|<
name|T
argument_list|>
name|serializer
decl_stmt|;
DECL|field|deserializer
specifier|private
name|Deserializer
argument_list|<
name|T
argument_list|>
name|deserializer
decl_stmt|;
DECL|field|inBuf
specifier|private
name|DataInputBuffer
name|inBuf
decl_stmt|;
DECL|field|outBuf
specifier|private
name|DataOutputBuffer
name|outBuf
decl_stmt|;
DECL|method|DefaultStringifier (Configuration conf, Class<T> c)
specifier|public
name|DefaultStringifier
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
block|{
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|serializer
operator|=
name|factory
operator|.
name|getSerializer
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|this
operator|.
name|deserializer
operator|=
name|factory
operator|.
name|getDeserializer
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|this
operator|.
name|inBuf
operator|=
operator|new
name|DataInputBuffer
argument_list|()
expr_stmt|;
name|this
operator|.
name|outBuf
operator|=
operator|new
name|DataOutputBuffer
argument_list|()
expr_stmt|;
try|try
block|{
name|serializer
operator|.
name|open
argument_list|(
name|outBuf
argument_list|)
expr_stmt|;
name|deserializer
operator|.
name|open
argument_list|(
name|inBuf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|fromString (String str)
specifier|public
name|T
name|fromString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|byte
index|[]
name|bytes
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|str
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|inBuf
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|T
name|restored
init|=
name|deserializer
operator|.
name|deserialize
argument_list|(
literal|null
argument_list|)
decl_stmt|;
return|return
name|restored
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedCharsetException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString (T obj)
specifier|public
name|String
name|toString
parameter_list|(
name|T
name|obj
parameter_list|)
throws|throws
name|IOException
block|{
name|outBuf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|outBuf
operator|.
name|getLength
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|outBuf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|buf
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|inBuf
operator|.
name|close
argument_list|()
expr_stmt|;
name|outBuf
operator|.
name|close
argument_list|()
expr_stmt|;
name|deserializer
operator|.
name|close
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stores the item in the configuration with the given keyName.    *     * @param<K>  the class of the item    * @param conf the configuration to store    * @param item the object to be stored    * @param keyName the name of the key to use    * @throws IOException : forwards Exceptions from the underlying     * {@link Serialization} classes.     */
DECL|method|store (Configuration conf, K item, String keyName)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|>
name|void
name|store
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|K
name|item
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
block|{
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
name|stringifier
init|=
operator|new
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
argument_list|(
name|conf
argument_list|,
name|GenericsUtil
operator|.
name|getClass
argument_list|(
name|item
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|keyName
argument_list|,
name|stringifier
operator|.
name|toString
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
name|stringifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Restores the object from the configuration.    *     * @param<K> the class of the item    * @param conf the configuration to use    * @param keyName the name of the key to use    * @param itemClass the class of the item    * @return restored object    * @throws IOException : forwards Exceptions from the underlying     * {@link Serialization} classes.    */
DECL|method|load (Configuration conf, String keyName, Class<K> itemClass)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|>
name|K
name|load
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|keyName
parameter_list|,
name|Class
argument_list|<
name|K
argument_list|>
name|itemClass
parameter_list|)
throws|throws
name|IOException
block|{
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
name|stringifier
init|=
operator|new
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
argument_list|(
name|conf
argument_list|,
name|itemClass
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|itemStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|keyName
argument_list|)
decl_stmt|;
return|return
name|stringifier
operator|.
name|fromString
argument_list|(
name|itemStr
argument_list|)
return|;
block|}
finally|finally
block|{
name|stringifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Stores the array of items in the configuration with the given keyName.    *     * @param<K> the class of the item    * @param conf the configuration to use     * @param items the objects to be stored    * @param keyName the name of the key to use    * @throws IndexOutOfBoundsException if the items array is empty    * @throws IOException : forwards Exceptions from the underlying     * {@link Serialization} classes.             */
DECL|method|storeArray (Configuration conf, K[] items, String keyName)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|>
name|void
name|storeArray
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|K
index|[]
name|items
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
block|{
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
name|stringifier
init|=
operator|new
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
argument_list|(
name|conf
argument_list|,
name|GenericsUtil
operator|.
name|getClass
argument_list|(
name|items
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|K
name|item
range|:
name|items
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|stringifier
operator|.
name|toString
argument_list|(
name|item
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|SEPARATOR
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|keyName
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stringifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Restores the array of objects from the configuration.    *     * @param<K> the class of the item    * @param conf the configuration to use    * @param keyName the name of the key to use    * @param itemClass the class of the item    * @return restored object    * @throws IOException : forwards Exceptions from the underlying     * {@link Serialization} classes.    */
DECL|method|loadArray (Configuration conf, String keyName, Class<K> itemClass)
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|>
name|K
index|[]
name|loadArray
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|keyName
parameter_list|,
name|Class
argument_list|<
name|K
argument_list|>
name|itemClass
parameter_list|)
throws|throws
name|IOException
block|{
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
name|stringifier
init|=
operator|new
name|DefaultStringifier
argument_list|<
name|K
argument_list|>
argument_list|(
name|conf
argument_list|,
name|itemClass
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|itemStr
init|=
name|conf
operator|.
name|get
argument_list|(
name|keyName
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|K
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|K
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|parts
init|=
name|itemStr
operator|.
name|split
argument_list|(
name|SEPARATOR
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|parts
control|)
block|{
if|if
condition|(
operator|!
name|part
operator|.
name|isEmpty
argument_list|()
condition|)
name|list
operator|.
name|add
argument_list|(
name|stringifier
operator|.
name|fromString
argument_list|(
name|part
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|GenericsUtil
operator|.
name|toArray
argument_list|(
name|itemClass
argument_list|,
name|list
argument_list|)
return|;
block|}
finally|finally
block|{
name|stringifier
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

