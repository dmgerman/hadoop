begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|db
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
name|Map
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Collection of available codecs.  */
end_comment

begin_class
DECL|class|CodecRegistry
specifier|public
class|class
name|CodecRegistry
block|{
DECL|field|valueCodecs
specifier|private
name|Map
argument_list|<
name|Class
argument_list|,
name|Codec
argument_list|<
name|?
argument_list|>
argument_list|>
name|valueCodecs
decl_stmt|;
DECL|method|CodecRegistry ()
specifier|public
name|CodecRegistry
parameter_list|()
block|{
name|valueCodecs
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|valueCodecs
operator|.
name|put
argument_list|(
name|String
operator|.
name|class
argument_list|,
operator|new
name|StringCodec
argument_list|()
argument_list|)
expr_stmt|;
name|valueCodecs
operator|.
name|put
argument_list|(
name|Long
operator|.
name|class
argument_list|,
operator|new
name|LongCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Convert raw value to strongly typed value/key with the help of a codec.    *    * @param rawData original byte array from the db.    * @param format  Class of the return value    * @param<T>     Type of the return value.    * @return the object with the parsed field data    */
DECL|method|asObject (byte[] rawData, Class<T> format)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|asObject
parameter_list|(
name|byte
index|[]
name|rawData
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|format
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|rawData
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Codec
name|codec
init|=
name|getCodec
argument_list|(
name|format
argument_list|)
decl_stmt|;
return|return
operator|(
name|T
operator|)
name|codec
operator|.
name|fromPersistedFormat
argument_list|(
name|rawData
argument_list|)
return|;
block|}
comment|/**    * Convert strongly typed object to raw data to store it in the kv store.    *    * @param object typed object.    * @param<T>    Type of the typed object.    * @return byte array to store it ini the kv store.    */
DECL|method|asRawData (T object)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|byte
index|[]
name|asRawData
parameter_list|(
name|T
name|object
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|object
argument_list|,
literal|"Null value shouldn't be persisted in the database"
argument_list|)
expr_stmt|;
name|Codec
argument_list|<
name|T
argument_list|>
name|codec
init|=
name|getCodec
argument_list|(
name|object
argument_list|)
decl_stmt|;
return|return
name|codec
operator|.
name|toPersistedFormat
argument_list|(
name|object
argument_list|)
return|;
block|}
comment|/**    * Get codec for the typed object including class and subclass.    * @param object typed object.    * @return Codec for the typed object.    * @throws IOException    */
DECL|method|getCodec (T object)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Codec
name|getCodec
parameter_list|(
name|T
name|object
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|T
argument_list|>
name|format
init|=
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|object
operator|.
name|getClass
argument_list|()
decl_stmt|;
return|return
name|getCodec
argument_list|(
name|format
argument_list|)
return|;
block|}
comment|/**    * Get codec for the typed object including class and subclass.    * @param<T>    Type of the typed object.    * @return Codec for the typed object.    * @throws IOException    */
DECL|method|getCodec (Class<T> format)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Codec
name|getCodec
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|format
parameter_list|)
throws|throws
name|IOException
block|{
name|Codec
argument_list|<
name|T
argument_list|>
name|codec
decl_stmt|;
if|if
condition|(
name|valueCodecs
operator|.
name|containsKey
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|codec
operator|=
operator|(
name|Codec
argument_list|<
name|T
argument_list|>
operator|)
name|valueCodecs
operator|.
name|get
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueCodecs
operator|.
name|containsKey
argument_list|(
name|format
operator|.
name|getSuperclass
argument_list|()
argument_list|)
condition|)
block|{
name|codec
operator|=
operator|(
name|Codec
argument_list|<
name|T
argument_list|>
operator|)
name|valueCodecs
operator|.
name|get
argument_list|(
name|format
operator|.
name|getSuperclass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Codec is not registered for type: "
operator|+
name|format
argument_list|)
throw|;
block|}
return|return
name|codec
return|;
block|}
comment|/**    * Addds codec to the internal collection.    *    * @param type  Type of the codec source/destination object.    * @param codec The codec itself.    * @param<T>   The type of the codec    */
DECL|method|addCodec (Class<T> type, Codec<T> codec)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|addCodec
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|Codec
argument_list|<
name|T
argument_list|>
name|codec
parameter_list|)
block|{
name|valueCodecs
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

