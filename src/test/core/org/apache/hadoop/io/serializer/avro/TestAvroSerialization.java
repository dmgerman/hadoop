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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|util
operator|.
name|Utf8
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
name|SerializationBase
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
name|SerializationTestUtil
import|;
end_import

begin_class
DECL|class|TestAvroSerialization
specifier|public
class|class
name|TestAvroSerialization
extends|extends
name|TestCase
block|{
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testIgnoreMisconfiguredMetadata ()
specifier|public
name|void
name|testIgnoreMisconfiguredMetadata
parameter_list|()
block|{
comment|// If SERIALIZATION_KEY is set, still need class name.
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|SerializationBase
name|serialization
init|=
literal|null
decl_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|SerializationBase
operator|.
name|SERIALIZATION_KEY
argument_list|,
name|AvroGenericSerialization
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|serialization
operator|=
name|factory
operator|.
name|getSerialization
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got serializer without any class info"
argument_list|,
name|serialization
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|SerializationBase
operator|.
name|SERIALIZATION_KEY
argument_list|,
name|AvroReflectSerialization
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|serialization
operator|=
name|factory
operator|.
name|getSerialization
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got serializer without any class info"
argument_list|,
name|serialization
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|SerializationBase
operator|.
name|SERIALIZATION_KEY
argument_list|,
name|AvroSpecificSerialization
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|serialization
operator|=
name|factory
operator|.
name|getSerialization
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Got serializer without any class info"
argument_list|,
name|serialization
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpecific ()
specifier|public
name|void
name|testSpecific
parameter_list|()
throws|throws
name|Exception
block|{
name|AvroRecord
name|before
init|=
operator|new
name|AvroRecord
argument_list|()
decl_stmt|;
name|before
operator|.
name|intField
operator|=
literal|5
expr_stmt|;
name|AvroRecord
name|after
init|=
name|SerializationTestUtil
operator|.
name|testSerialization
argument_list|(
name|conf
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
DECL|method|testReflectPkg ()
specifier|public
name|void
name|testReflectPkg
parameter_list|()
throws|throws
name|Exception
block|{
name|Record
name|before
init|=
operator|new
name|Record
argument_list|()
decl_stmt|;
name|before
operator|.
name|x
operator|=
literal|10
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AvroReflectSerialization
operator|.
name|AVRO_REFLECT_PACKAGES
argument_list|,
name|before
operator|.
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Record
name|after
init|=
name|SerializationTestUtil
operator|.
name|testSerialization
argument_list|(
name|conf
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
DECL|method|testReflectInnerClass ()
specifier|public
name|void
name|testReflectInnerClass
parameter_list|()
throws|throws
name|Exception
block|{
name|InnerRecord
name|before
init|=
operator|new
name|InnerRecord
argument_list|()
decl_stmt|;
name|before
operator|.
name|x
operator|=
literal|10
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AvroReflectSerialization
operator|.
name|AVRO_REFLECT_PACKAGES
argument_list|,
name|before
operator|.
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|InnerRecord
name|after
init|=
name|SerializationTestUtil
operator|.
name|testSerialization
argument_list|(
name|conf
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
DECL|method|testReflect ()
specifier|public
name|void
name|testReflect
parameter_list|()
throws|throws
name|Exception
block|{
name|RefSerializable
name|before
init|=
operator|new
name|RefSerializable
argument_list|()
decl_stmt|;
name|before
operator|.
name|x
operator|=
literal|10
expr_stmt|;
name|RefSerializable
name|after
init|=
name|SerializationTestUtil
operator|.
name|testSerialization
argument_list|(
name|conf
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeneric ()
specifier|public
name|void
name|testGeneric
parameter_list|()
throws|throws
name|Exception
block|{
name|Utf8
name|before
init|=
operator|new
name|Utf8
argument_list|(
literal|"hadoop"
argument_list|)
decl_stmt|;
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
name|SerializationBase
operator|.
name|SERIALIZATION_KEY
argument_list|,
name|AvroGenericSerialization
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|put
argument_list|(
name|AvroSerialization
operator|.
name|AVRO_SCHEMA_KEY
argument_list|,
literal|"\"string\""
argument_list|)
expr_stmt|;
name|Utf8
name|after
init|=
name|SerializationTestUtil
operator|.
name|testSerialization
argument_list|(
name|conf
argument_list|,
name|metadata
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
DECL|class|InnerRecord
specifier|public
specifier|static
class|class
name|InnerRecord
block|{
DECL|field|x
specifier|public
name|int
name|x
init|=
literal|7
decl_stmt|;
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|x
return|;
block|}
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|InnerRecord
name|other
init|=
operator|(
name|InnerRecord
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|x
operator|!=
name|other
operator|.
name|x
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
DECL|class|RefSerializable
specifier|public
specifier|static
class|class
name|RefSerializable
implements|implements
name|AvroReflectSerializable
block|{
DECL|field|x
specifier|public
name|int
name|x
init|=
literal|7
decl_stmt|;
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|x
return|;
block|}
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|RefSerializable
name|other
init|=
operator|(
name|RefSerializable
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|x
operator|!=
name|other
operator|.
name|x
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

