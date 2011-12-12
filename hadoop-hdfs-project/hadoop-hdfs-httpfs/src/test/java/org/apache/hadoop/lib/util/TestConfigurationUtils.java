begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|util
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_class
DECL|class|TestConfigurationUtils
specifier|public
class|class
name|TestConfigurationUtils
block|{
annotation|@
name|Test
DECL|method|constructors ()
specifier|public
name|void
name|constructors
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
literal|"<configuration><property><name>a</name><value>A</value></property></configuration>"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ConfigurationUtils
operator|.
name|load
argument_list|(
name|conf
argument_list|,
name|is
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|constructorsFail3 ()
specifier|public
name|void
name|constructorsFail3
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"<xonfiguration></xonfiguration>"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|ConfigurationUtils
operator|.
name|load
argument_list|(
name|conf
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copy ()
specifier|public
name|void
name|copy
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|srcConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Configuration
name|targetConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|srcConf
operator|.
name|set
argument_list|(
literal|"testParameter1"
argument_list|,
literal|"valueFromSource"
argument_list|)
expr_stmt|;
name|srcConf
operator|.
name|set
argument_list|(
literal|"testParameter2"
argument_list|,
literal|"valueFromSource"
argument_list|)
expr_stmt|;
name|targetConf
operator|.
name|set
argument_list|(
literal|"testParameter2"
argument_list|,
literal|"valueFromTarget"
argument_list|)
expr_stmt|;
name|targetConf
operator|.
name|set
argument_list|(
literal|"testParameter3"
argument_list|,
literal|"valueFromTarget"
argument_list|)
expr_stmt|;
name|ConfigurationUtils
operator|.
name|copy
argument_list|(
name|srcConf
argument_list|,
name|targetConf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"valueFromSource"
argument_list|,
name|targetConf
operator|.
name|get
argument_list|(
literal|"testParameter1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"valueFromSource"
argument_list|,
name|targetConf
operator|.
name|get
argument_list|(
literal|"testParameter2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"valueFromTarget"
argument_list|,
name|targetConf
operator|.
name|get
argument_list|(
literal|"testParameter3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|injectDefaults ()
specifier|public
name|void
name|injectDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|srcConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Configuration
name|targetConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|srcConf
operator|.
name|set
argument_list|(
literal|"testParameter1"
argument_list|,
literal|"valueFromSource"
argument_list|)
expr_stmt|;
name|srcConf
operator|.
name|set
argument_list|(
literal|"testParameter2"
argument_list|,
literal|"valueFromSource"
argument_list|)
expr_stmt|;
name|targetConf
operator|.
name|set
argument_list|(
literal|"testParameter2"
argument_list|,
literal|"originalValueFromTarget"
argument_list|)
expr_stmt|;
name|targetConf
operator|.
name|set
argument_list|(
literal|"testParameter3"
argument_list|,
literal|"originalValueFromTarget"
argument_list|)
expr_stmt|;
name|ConfigurationUtils
operator|.
name|injectDefaults
argument_list|(
name|srcConf
argument_list|,
name|targetConf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"valueFromSource"
argument_list|,
name|targetConf
operator|.
name|get
argument_list|(
literal|"testParameter1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"originalValueFromTarget"
argument_list|,
name|targetConf
operator|.
name|get
argument_list|(
literal|"testParameter2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"originalValueFromTarget"
argument_list|,
name|targetConf
operator|.
name|get
argument_list|(
literal|"testParameter3"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"valueFromSource"
argument_list|,
name|srcConf
operator|.
name|get
argument_list|(
literal|"testParameter1"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"valueFromSource"
argument_list|,
name|srcConf
operator|.
name|get
argument_list|(
literal|"testParameter2"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|srcConf
operator|.
name|get
argument_list|(
literal|"testParameter3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|resolve ()
specifier|public
name|void
name|resolve
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"b"
argument_list|,
literal|"${a}"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getRaw
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getRaw
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"${a}"
argument_list|)
expr_stmt|;
name|conf
operator|=
name|ConfigurationUtils
operator|.
name|resolve
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getRaw
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getRaw
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVarResolutionAndSysProps ()
specifier|public
name|void
name|testVarResolutionAndSysProps
parameter_list|()
block|{
name|String
name|userName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"b"
argument_list|,
literal|"${a}"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"c"
argument_list|,
literal|"${user.name}"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"d"
argument_list|,
literal|"${aaa}"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getRaw
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getRaw
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"${a}"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|getRaw
argument_list|(
literal|"c"
argument_list|)
argument_list|,
literal|"${user.name}"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|userName
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
argument_list|,
literal|"${aaa}"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"user.name"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

