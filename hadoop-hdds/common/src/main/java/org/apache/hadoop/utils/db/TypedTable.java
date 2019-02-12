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

begin_comment
comment|/**  * Strongly typed table implementation.  *<p>  * Automatically converts values and keys using a raw byte[] based table  * implementation and registered converters.  *  * @param<KEY>   type of the keys in the store.  * @param<VALUE> type of the values in the store.  */
end_comment

begin_class
DECL|class|TypedTable
specifier|public
class|class
name|TypedTable
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
implements|implements
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
block|{
DECL|field|rawTable
specifier|private
name|Table
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|rawTable
decl_stmt|;
DECL|field|codecRegistry
specifier|private
name|CodecRegistry
name|codecRegistry
decl_stmt|;
DECL|field|keyType
specifier|private
name|Class
argument_list|<
name|KEY
argument_list|>
name|keyType
decl_stmt|;
DECL|field|valueType
specifier|private
name|Class
argument_list|<
name|VALUE
argument_list|>
name|valueType
decl_stmt|;
DECL|method|TypedTable ( Table<byte[], byte[]> rawTable, CodecRegistry codecRegistry, Class<KEY> keyType, Class<VALUE> valueType)
specifier|public
name|TypedTable
parameter_list|(
name|Table
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|rawTable
parameter_list|,
name|CodecRegistry
name|codecRegistry
parameter_list|,
name|Class
argument_list|<
name|KEY
argument_list|>
name|keyType
parameter_list|,
name|Class
argument_list|<
name|VALUE
argument_list|>
name|valueType
parameter_list|)
block|{
name|this
operator|.
name|rawTable
operator|=
name|rawTable
expr_stmt|;
name|this
operator|.
name|codecRegistry
operator|=
name|codecRegistry
expr_stmt|;
name|this
operator|.
name|keyType
operator|=
name|keyType
expr_stmt|;
name|this
operator|.
name|valueType
operator|=
name|valueType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|put (KEY key, VALUE value)
specifier|public
name|void
name|put
parameter_list|(
name|KEY
name|key
parameter_list|,
name|VALUE
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|keyData
init|=
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|byte
index|[]
name|valueData
init|=
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|rawTable
operator|.
name|put
argument_list|(
name|keyData
argument_list|,
name|valueData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|putWithBatch (BatchOperation batch, KEY key, VALUE value)
specifier|public
name|void
name|putWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
name|KEY
name|key
parameter_list|,
name|VALUE
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|keyData
init|=
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|byte
index|[]
name|valueData
init|=
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|rawTable
operator|.
name|putWithBatch
argument_list|(
name|batch
argument_list|,
name|keyData
argument_list|,
name|valueData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|rawTable
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|get (KEY key)
specifier|public
name|VALUE
name|get
parameter_list|(
name|KEY
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|keyBytes
init|=
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|byte
index|[]
name|valueBytes
init|=
name|rawTable
operator|.
name|get
argument_list|(
name|keyBytes
argument_list|)
decl_stmt|;
return|return
name|codecRegistry
operator|.
name|asObject
argument_list|(
name|valueBytes
argument_list|,
name|valueType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete (KEY key)
specifier|public
name|void
name|delete
parameter_list|(
name|KEY
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|rawTable
operator|.
name|delete
argument_list|(
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteWithBatch (BatchOperation batch, KEY key)
specifier|public
name|void
name|deleteWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
name|KEY
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|rawTable
operator|.
name|deleteWithBatch
argument_list|(
name|batch
argument_list|,
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|TableIterator
argument_list|<
name|KEY
argument_list|,
name|TypedKeyValue
argument_list|>
name|iterator
parameter_list|()
block|{
name|TableIterator
argument_list|<
name|byte
index|[]
argument_list|,
name|?
extends|extends
name|KeyValue
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|iterator
init|=
name|rawTable
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|TypedTableIterator
argument_list|(
name|iterator
argument_list|,
name|keyType
argument_list|,
name|valueType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|rawTable
operator|.
name|getName
argument_list|()
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
name|Exception
block|{
name|rawTable
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Key value implementation for strongly typed tables.    */
DECL|class|TypedKeyValue
specifier|public
class|class
name|TypedKeyValue
implements|implements
name|KeyValue
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
block|{
DECL|field|rawKeyValue
specifier|private
name|KeyValue
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|rawKeyValue
decl_stmt|;
DECL|method|TypedKeyValue (KeyValue<byte[], byte[]> rawKeyValue)
specifier|public
name|TypedKeyValue
parameter_list|(
name|KeyValue
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|rawKeyValue
parameter_list|)
block|{
name|this
operator|.
name|rawKeyValue
operator|=
name|rawKeyValue
expr_stmt|;
block|}
DECL|method|TypedKeyValue (KeyValue<byte[], byte[]> rawKeyValue, Class<KEY> keyType, Class<VALUE> valueType)
specifier|public
name|TypedKeyValue
parameter_list|(
name|KeyValue
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|rawKeyValue
parameter_list|,
name|Class
argument_list|<
name|KEY
argument_list|>
name|keyType
parameter_list|,
name|Class
argument_list|<
name|VALUE
argument_list|>
name|valueType
parameter_list|)
block|{
name|this
operator|.
name|rawKeyValue
operator|=
name|rawKeyValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKey ()
specifier|public
name|KEY
name|getKey
parameter_list|()
block|{
return|return
name|codecRegistry
operator|.
name|asObject
argument_list|(
name|rawKeyValue
operator|.
name|getKey
argument_list|()
argument_list|,
name|keyType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue ()
specifier|public
name|VALUE
name|getValue
parameter_list|()
block|{
return|return
name|codecRegistry
operator|.
name|asObject
argument_list|(
name|rawKeyValue
operator|.
name|getValue
argument_list|()
argument_list|,
name|valueType
argument_list|)
return|;
block|}
block|}
comment|/**    * Table Iterator implementation for strongly typed tables.    */
DECL|class|TypedTableIterator
specifier|public
class|class
name|TypedTableIterator
implements|implements
name|TableIterator
argument_list|<
name|KEY
argument_list|,
name|TypedKeyValue
argument_list|>
block|{
specifier|private
name|TableIterator
argument_list|<
name|byte
index|[]
argument_list|,
name|?
extends|extends
name|KeyValue
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
DECL|field|rawIterator
name|rawIterator
decl_stmt|;
DECL|field|keyClass
specifier|private
specifier|final
name|Class
argument_list|<
name|KEY
argument_list|>
name|keyClass
decl_stmt|;
DECL|field|valueClass
specifier|private
specifier|final
name|Class
argument_list|<
name|VALUE
argument_list|>
name|valueClass
decl_stmt|;
DECL|method|TypedTableIterator ( TableIterator<byte[], ? extends KeyValue<byte[], byte[]>> rawIterator, Class<KEY> keyType, Class<VALUE> valueType)
specifier|public
name|TypedTableIterator
parameter_list|(
name|TableIterator
argument_list|<
name|byte
index|[]
argument_list|,
name|?
extends|extends
name|KeyValue
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|rawIterator
parameter_list|,
name|Class
argument_list|<
name|KEY
argument_list|>
name|keyType
parameter_list|,
name|Class
argument_list|<
name|VALUE
argument_list|>
name|valueType
parameter_list|)
block|{
name|this
operator|.
name|rawIterator
operator|=
name|rawIterator
expr_stmt|;
name|keyClass
operator|=
name|keyType
expr_stmt|;
name|valueClass
operator|=
name|valueType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekToFirst ()
specifier|public
name|void
name|seekToFirst
parameter_list|()
block|{
name|rawIterator
operator|.
name|seekToFirst
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekToLast ()
specifier|public
name|void
name|seekToLast
parameter_list|()
block|{
name|rawIterator
operator|.
name|seekToLast
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seek (KEY key)
specifier|public
name|TypedKeyValue
name|seek
parameter_list|(
name|KEY
name|key
parameter_list|)
block|{
name|byte
index|[]
name|keyBytes
init|=
name|codecRegistry
operator|.
name|asRawData
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|KeyValue
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|result
init|=
name|rawIterator
operator|.
name|seek
argument_list|(
name|keyBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|TypedKeyValue
argument_list|(
name|result
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|key ()
specifier|public
name|KEY
name|key
parameter_list|()
block|{
name|byte
index|[]
name|result
init|=
name|rawIterator
operator|.
name|key
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|codecRegistry
operator|.
name|asObject
argument_list|(
name|result
argument_list|,
name|keyClass
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|value ()
specifier|public
name|TypedKeyValue
name|value
parameter_list|()
block|{
name|KeyValue
name|keyValue
init|=
name|rawIterator
operator|.
name|value
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyValue
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|TypedKeyValue
argument_list|(
name|keyValue
argument_list|,
name|keyClass
argument_list|,
name|valueClass
argument_list|)
return|;
block|}
return|return
literal|null
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
name|rawIterator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|rawIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|TypedKeyValue
name|next
parameter_list|()
block|{
return|return
operator|new
name|TypedKeyValue
argument_list|(
name|rawIterator
operator|.
name|next
argument_list|()
argument_list|,
name|keyType
argument_list|,
name|valueType
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

