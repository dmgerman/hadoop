begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.serializer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|serializer
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configured
import|;
end_import

begin_comment
comment|/**  *<p>  * Encapsulates a {@link SerializerBase}/{@link DeserializerBase} pair.  *</p>  *   * @param<T>  */
end_comment

begin_class
DECL|class|SerializationBase
specifier|public
specifier|abstract
class|class
name|SerializationBase
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Configured
implements|implements
name|Serialization
argument_list|<
name|T
argument_list|>
block|{
DECL|field|SERIALIZATION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SERIALIZATION_KEY
init|=
literal|"Serialization-Class"
decl_stmt|;
DECL|field|CLASS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CLASS_KEY
init|=
literal|"Serialized-Class"
decl_stmt|;
DECL|method|getMetadataFromClass (Class<?> c)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadataFromClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|CLASS_KEY
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|accept (Class<?> c)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|accept
argument_list|(
name|getMetadataFromClass
argument_list|(
name|c
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|getDeserializer (Class<T> c)
specifier|public
name|Deserializer
argument_list|<
name|T
argument_list|>
name|getDeserializer
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|getDeserializer
argument_list|(
name|getMetadataFromClass
argument_list|(
name|c
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|getSerializer (Class<T> c)
specifier|public
name|Serializer
argument_list|<
name|T
argument_list|>
name|getSerializer
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|getSerializer
argument_list|(
name|getMetadataFromClass
argument_list|(
name|c
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Allows clients to test whether this {@link SerializationBase} supports the    * given metadata.    */
DECL|method|accept (Map<String, String> metadata)
specifier|public
specifier|abstract
name|boolean
name|accept
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
function_decl|;
comment|/**    * @return a {@link SerializerBase} for the given metadata.    */
DECL|method|getSerializer (Map<String, String> metadata)
specifier|public
specifier|abstract
name|SerializerBase
argument_list|<
name|T
argument_list|>
name|getSerializer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
function_decl|;
comment|/**    * @return a {@link DeserializerBase} for the given metadata.    */
DECL|method|getDeserializer ( Map<String, String> metadata)
specifier|public
specifier|abstract
name|DeserializerBase
argument_list|<
name|T
argument_list|>
name|getDeserializer
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
function_decl|;
DECL|method|getClassFromMetadata (Map<String, String> metadata)
specifier|protected
name|Class
argument_list|<
name|?
argument_list|>
name|getClassFromMetadata
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
parameter_list|)
block|{
name|String
name|classname
init|=
name|metadata
operator|.
name|get
argument_list|(
name|CLASS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|classname
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getClassByName
argument_list|(
name|classname
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

