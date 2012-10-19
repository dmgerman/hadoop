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
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|CommonConfigurationKeys
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
name|Writable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_class
DECL|class|TestSerializationFactory
specifier|public
class|class
name|TestSerializationFactory
block|{
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|SerializationFactory
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|conf
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|factory
specifier|static
name|SerializationFactory
name|factory
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerializationKeyIsEmpty ()
specifier|public
name|void
name|testSerializationKeyIsEmpty
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_SERIALIZATIONS_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerializationKeyIsInvalid ()
specifier|public
name|void
name|testSerializationKeyIsInvalid
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_SERIALIZATIONS_KEY
argument_list|,
literal|"INVALID_KEY_XXX"
argument_list|)
expr_stmt|;
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSerializer ()
specifier|public
name|void
name|testGetSerializer
parameter_list|()
block|{
comment|// Test that a valid serializer class is returned when its present
name|assertNotNull
argument_list|(
literal|"A valid class must be returned for default Writable SerDe"
argument_list|,
name|factory
operator|.
name|getSerializer
argument_list|(
name|Writable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test that a null is returned when none can be found.
name|assertNull
argument_list|(
literal|"A null should be returned if there are no serializers found."
argument_list|,
name|factory
operator|.
name|getSerializer
argument_list|(
name|TestSerializationFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetDeserializer ()
specifier|public
name|void
name|testGetDeserializer
parameter_list|()
block|{
comment|// Test that a valid serializer class is returned when its present
name|assertNotNull
argument_list|(
literal|"A valid class must be returned for default Writable SerDe"
argument_list|,
name|factory
operator|.
name|getDeserializer
argument_list|(
name|Writable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test that a null is returned when none can be found.
name|assertNull
argument_list|(
literal|"A null should be returned if there are no deserializers found"
argument_list|,
name|factory
operator|.
name|getDeserializer
argument_list|(
name|TestSerializationFactory
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

