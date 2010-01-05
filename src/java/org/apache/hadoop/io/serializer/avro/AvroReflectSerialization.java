begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.serializer.avro
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
operator|.
name|avro
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|Schema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|io
operator|.
name|DatumReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|io
operator|.
name|DatumWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|reflect
operator|.
name|ReflectData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|reflect
operator|.
name|ReflectDatumReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|reflect
operator|.
name|ReflectDatumWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|specific
operator|.
name|SpecificRecord
import|;
end_import

begin_comment
comment|/**  * Serialization for Avro Reflect classes. For a class to be accepted by this   * serialization, it must either be in the package list configured via   * {@link AvroReflectSerialization#AVRO_REFLECT_PACKAGES} or implement   * {@link AvroReflectSerializable} interface.  *  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|AvroReflectSerialization
specifier|public
class|class
name|AvroReflectSerialization
extends|extends
name|AvroSerialization
argument_list|<
name|Object
argument_list|>
block|{
comment|/**    * Key to configure packages that contain classes to be serialized and     * deserialized using this class. Multiple packages can be specified using     * comma-separated list.    */
DECL|field|AVRO_REFLECT_PACKAGES
specifier|public
specifier|static
specifier|final
name|String
name|AVRO_REFLECT_PACKAGES
init|=
literal|"avro.reflect.pkgs"
decl_stmt|;
DECL|field|packages
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|packages
decl_stmt|;
annotation|@
name|Override
DECL|method|accept (Map<String, String> metadata)
specifier|public
specifier|synchronized
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
block|{
if|if
condition|(
name|packages
operator|==
literal|null
condition|)
block|{
name|getPackages
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|checkSerializationKey
argument_list|(
name|metadata
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|getClassFromMetadata
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|AvroReflectSerializable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
operator|||
name|packages
operator|.
name|contains
argument_list|(
name|c
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getPackages ()
specifier|private
name|void
name|getPackages
parameter_list|()
block|{
name|String
index|[]
name|pkgList
init|=
name|getConf
argument_list|()
operator|.
name|getStrings
argument_list|(
name|AVRO_REFLECT_PACKAGES
argument_list|)
decl_stmt|;
name|packages
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|pkgList
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|pkg
range|:
name|pkgList
control|)
block|{
name|packages
operator|.
name|add
argument_list|(
name|pkg
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getReader (Map<String, String> metadata)
specifier|protected
name|DatumReader
name|getReader
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
try|try
block|{
return|return
operator|new
name|ReflectDatumReader
argument_list|(
name|getClassFromMetadata
argument_list|(
name|metadata
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSchema (Map<String, String> metadata)
specifier|protected
name|Schema
name|getSchema
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
name|Class
argument_list|<
name|?
argument_list|>
name|c
init|=
name|getClassFromMetadata
argument_list|(
name|metadata
argument_list|)
decl_stmt|;
return|return
name|ReflectData
operator|.
name|get
argument_list|()
operator|.
name|getSchema
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWriter (Map<String, String> metadata)
specifier|protected
name|DatumWriter
name|getWriter
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
return|return
operator|new
name|ReflectDatumWriter
argument_list|()
return|;
block|}
block|}
end_class

end_unit

