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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|TestGenericWritable
operator|.
name|CONF_TEST_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|TestGenericWritable
operator|.
name|CONF_TEST_VALUE
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
name|Text
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
name|TestGenericWritable
operator|.
name|Baz
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
name|TestGenericWritable
operator|.
name|FooGenericWritable
import|;
end_import

begin_class
DECL|class|TestWritableSerialization
specifier|public
class|class
name|TestWritableSerialization
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
DECL|method|testWritableSerialization ()
specifier|public
name|void
name|testWritableSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|before
init|=
operator|new
name|Text
argument_list|(
literal|"test writable"
argument_list|)
decl_stmt|;
name|Text
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
DECL|method|testWritableConfigurable ()
specifier|public
name|void
name|testWritableConfigurable
parameter_list|()
throws|throws
name|Exception
block|{
comment|//set the configuration parameter
name|conf
operator|.
name|set
argument_list|(
name|CONF_TEST_KEY
argument_list|,
name|CONF_TEST_VALUE
argument_list|)
expr_stmt|;
comment|//reuse TestGenericWritable inner classes to test
comment|//writables that also implement Configurable.
name|FooGenericWritable
name|generic
init|=
operator|new
name|FooGenericWritable
argument_list|()
decl_stmt|;
name|generic
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Baz
name|baz
init|=
operator|new
name|Baz
argument_list|()
decl_stmt|;
name|generic
operator|.
name|set
argument_list|(
name|baz
argument_list|)
expr_stmt|;
name|Baz
name|result
init|=
name|SerializationTestUtil
operator|.
name|testSerialization
argument_list|(
name|conf
argument_list|,
name|baz
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|baz
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

