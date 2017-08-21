begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.driver.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|impl
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
name|Collection
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|StateStoreSerializer
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|BaseRecord
import|;
end_import

begin_comment
comment|/**  * State Store driver that stores a serialization of the records. The serializer  * is pluggable.  */
end_comment

begin_class
DECL|class|StateStoreSerializableImpl
specifier|public
specifier|abstract
class|class
name|StateStoreSerializableImpl
extends|extends
name|StateStoreBaseImpl
block|{
comment|/** Mark for slashes in path names. */
DECL|field|SLASH_MARK
specifier|protected
specifier|static
specifier|final
name|String
name|SLASH_MARK
init|=
literal|"0SLASH0"
decl_stmt|;
comment|/** Mark for colon in path names. */
DECL|field|COLON_MARK
specifier|protected
specifier|static
specifier|final
name|String
name|COLON_MARK
init|=
literal|"_"
decl_stmt|;
comment|/** Default serializer for this driver. */
DECL|field|serializer
specifier|private
name|StateStoreSerializer
name|serializer
decl_stmt|;
annotation|@
name|Override
DECL|method|init (final Configuration config, final String id, final Collection<Class<? extends BaseRecord>> records)
specifier|public
name|boolean
name|init
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|BaseRecord
argument_list|>
argument_list|>
name|records
parameter_list|)
block|{
name|boolean
name|ret
init|=
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|id
argument_list|,
name|records
argument_list|)
decl_stmt|;
name|this
operator|.
name|serializer
operator|=
name|StateStoreSerializer
operator|.
name|getSerializer
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * Serialize a record using the serializer.    * @param record Record to serialize.    * @return Byte array with the serialization of the record.    */
DECL|method|serialize (T record)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|byte
index|[]
name|serialize
parameter_list|(
name|T
name|record
parameter_list|)
block|{
return|return
name|serializer
operator|.
name|serialize
argument_list|(
name|record
argument_list|)
return|;
block|}
comment|/**    * Serialize a record using the serializer.    * @param record Record to serialize.    * @return String with the serialization of the record.    */
DECL|method|serializeString (T record)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|String
name|serializeString
parameter_list|(
name|T
name|record
parameter_list|)
block|{
return|return
name|serializer
operator|.
name|serializeString
argument_list|(
name|record
argument_list|)
return|;
block|}
comment|/**    * Creates a record from an input data string.    * @param data Serialized text of the record.    * @param clazz Record class.    * @param includeDates If dateModified and dateCreated are serialized.    * @return The created record.    * @throws IOException    */
DECL|method|newRecord ( String data, Class<T> clazz, boolean includeDates)
specifier|protected
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|T
name|newRecord
parameter_list|(
name|String
name|data
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|boolean
name|includeDates
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|serializer
operator|.
name|deserialize
argument_list|(
name|data
argument_list|,
name|clazz
argument_list|)
return|;
block|}
comment|/**    * Get the primary key for a record. If we don't want to store in folders, we    * need to remove / from the name.    *    * @param record Record to get the primary key for.    * @return Primary key for the record.    */
DECL|method|getPrimaryKey (BaseRecord record)
specifier|protected
specifier|static
name|String
name|getPrimaryKey
parameter_list|(
name|BaseRecord
name|record
parameter_list|)
block|{
name|String
name|primaryKey
init|=
name|record
operator|.
name|getPrimaryKey
argument_list|()
decl_stmt|;
name|primaryKey
operator|=
name|primaryKey
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
name|SLASH_MARK
argument_list|)
expr_stmt|;
name|primaryKey
operator|=
name|primaryKey
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
name|COLON_MARK
argument_list|)
expr_stmt|;
return|return
name|primaryKey
return|;
block|}
block|}
end_class

end_unit

