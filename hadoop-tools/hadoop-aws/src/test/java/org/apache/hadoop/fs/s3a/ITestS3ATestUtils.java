begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|fs
operator|.
name|s3a
operator|.
name|S3ATestUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test the test utils. Why an integration test? it's needed to  * verify property pushdown.  */
end_comment

begin_class
DECL|class|ITestS3ATestUtils
specifier|public
class|class
name|ITestS3ATestUtils
extends|extends
name|Assert
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ITestS3ATestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"undefined.property"
decl_stmt|;
annotation|@
name|Before
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|KEY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTestProperty ()
specifier|public
name|void
name|testGetTestProperty
parameter_list|()
throws|throws
name|Throwable
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
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|getTestProperty
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY
argument_list|,
literal|"\t b \n"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|getTestProperty
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|KEY
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|getTestProperty
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|unsetSysprop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|getTestProperty
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTestPropertyLong ()
specifier|public
name|void
name|testGetTestPropertyLong
parameter_list|()
throws|throws
name|Throwable
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTestPropertyLong
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getTestPropertyLong
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|KEY
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getTestPropertyLong
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTestPropertyInt ()
specifier|public
name|void
name|testGetTestPropertyInt
parameter_list|()
throws|throws
name|Throwable
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getTestPropertyInt
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getTestPropertyInt
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|KEY
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getTestPropertyInt
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|KEY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|getTestPropertyInt
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|unsetSysprop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|getTestPropertyInt
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTestPropertyBool ()
specifier|public
name|void
name|testGetTestPropertyBool
parameter_list|()
throws|throws
name|Throwable
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
name|assertTrue
argument_list|(
name|getTestPropertyBool
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY
argument_list|,
literal|"\tfalse \n"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getTestPropertyBool
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getTestPropertyBool
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|unsetSysprop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|getTestProperty
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getTestPropertyBool
argument_list|(
name|conf
argument_list|,
name|KEY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|unsetSysprop ()
specifier|protected
name|void
name|unsetSysprop
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|KEY
argument_list|,
name|UNSET_PROPERTY
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

