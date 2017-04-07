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
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|StringUtils
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
name|impl
operator|.
name|pb
operator|.
name|PBRecord
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * Protobuf implementation of the State Store serializer.  */
end_comment

begin_class
DECL|class|StateStoreSerializerPBImpl
specifier|public
specifier|final
class|class
name|StateStoreSerializerPBImpl
extends|extends
name|StateStoreSerializer
block|{
DECL|field|PB_IMPL_PACKAGE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PB_IMPL_PACKAGE_SUFFIX
init|=
literal|"impl.pb"
decl_stmt|;
DECL|field|PB_IMPL_CLASS_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PB_IMPL_CLASS_SUFFIX
init|=
literal|"PBImpl"
decl_stmt|;
DECL|field|localConf
specifier|private
name|Configuration
name|localConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|StateStoreSerializerPBImpl ()
specifier|private
name|StateStoreSerializerPBImpl
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|newRecordInstance (Class<T> clazz)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newRecordInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
try|try
block|{
name|String
name|clazzPBImpl
init|=
name|getPBImplClassName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|pbClazz
init|=
name|localConf
operator|.
name|getClassByName
argument_list|(
name|clazzPBImpl
argument_list|)
decl_stmt|;
name|Object
name|retObject
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|pbClazz
argument_list|,
name|localConf
argument_list|)
decl_stmt|;
return|return
operator|(
name|T
operator|)
name|retObject
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getPBImplClassName (Class<?> clazz)
specifier|private
name|String
name|getPBImplClassName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|srcPackagePart
init|=
name|getPackageName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|String
name|srcClassName
init|=
name|getClassName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|String
name|destPackagePart
init|=
name|srcPackagePart
operator|+
literal|"."
operator|+
name|PB_IMPL_PACKAGE_SUFFIX
decl_stmt|;
name|String
name|destClassPart
init|=
name|srcClassName
operator|+
name|PB_IMPL_CLASS_SUFFIX
decl_stmt|;
return|return
name|destPackagePart
operator|+
literal|"."
operator|+
name|destClassPart
return|;
block|}
DECL|method|getClassName (Class<?> clazz)
specifier|private
name|String
name|getClassName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|fqName
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
operator|(
name|fqName
operator|.
name|substring
argument_list|(
name|fqName
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
operator|+
literal|1
argument_list|,
name|fqName
operator|.
name|length
argument_list|()
argument_list|)
operator|)
return|;
block|}
DECL|method|getPackageName (Class<?> clazz)
specifier|private
name|String
name|getPackageName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|serialize (BaseRecord record)
specifier|public
name|byte
index|[]
name|serialize
parameter_list|(
name|BaseRecord
name|record
parameter_list|)
block|{
name|byte
index|[]
name|byteArray64
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|record
operator|instanceof
name|PBRecord
condition|)
block|{
name|PBRecord
name|recordPB
init|=
operator|(
name|PBRecord
operator|)
name|record
decl_stmt|;
name|Message
name|msg
init|=
name|recordPB
operator|.
name|getProto
argument_list|()
decl_stmt|;
name|byte
index|[]
name|byteArray
init|=
name|msg
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|byteArray64
operator|=
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|byteArray
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|byteArray64
return|;
block|}
annotation|@
name|Override
DECL|method|serializeString (BaseRecord record)
specifier|public
name|String
name|serializeString
parameter_list|(
name|BaseRecord
name|record
parameter_list|)
block|{
name|byte
index|[]
name|byteArray64
init|=
name|serialize
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|String
name|base64Encoded
init|=
name|StringUtils
operator|.
name|newStringUtf8
argument_list|(
name|byteArray64
argument_list|)
decl_stmt|;
return|return
name|base64Encoded
return|;
block|}
annotation|@
name|Override
DECL|method|deserialize ( byte[] byteArray, Class<T> clazz)
specifier|public
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|T
name|deserialize
parameter_list|(
name|byte
index|[]
name|byteArray
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|T
name|record
init|=
name|newRecord
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|record
operator|instanceof
name|PBRecord
condition|)
block|{
name|PBRecord
name|pbRecord
init|=
operator|(
name|PBRecord
operator|)
name|record
decl_stmt|;
name|byte
index|[]
name|byteArray64
init|=
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|byteArray
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|base64Encoded
init|=
name|StringUtils
operator|.
name|newStringUtf8
argument_list|(
name|byteArray64
argument_list|)
decl_stmt|;
name|pbRecord
operator|.
name|readInstance
argument_list|(
name|base64Encoded
argument_list|)
expr_stmt|;
block|}
return|return
name|record
return|;
block|}
annotation|@
name|Override
DECL|method|deserialize (String data, Class<T> clazz)
specifier|public
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
name|T
name|deserialize
parameter_list|(
name|String
name|data
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|byteArray64
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|data
argument_list|)
decl_stmt|;
return|return
name|deserialize
argument_list|(
name|byteArray64
argument_list|,
name|clazz
argument_list|)
return|;
block|}
block|}
end_class

end_unit

