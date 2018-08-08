begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.diagnostics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|diagnostics
package|;
end_package

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
name|Charsets
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
name|Test
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
name|azurebfs
operator|.
name|contracts
operator|.
name|exceptions
operator|.
name|InvalidConfigurationValueException
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
operator|.
name|FileSystemConfigurations
operator|.
name|MIN_BUFFER_SIZE
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
name|azurebfs
operator|.
name|constants
operator|.
name|FileSystemConfigurations
operator|.
name|MAX_BUFFER_SIZE
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
name|azurebfs
operator|.
name|constants
operator|.
name|FileSystemConfigurations
operator|.
name|DEFAULT_READ_BUFFER_SIZE
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
name|azurebfs
operator|.
name|constants
operator|.
name|FileSystemConfigurations
operator|.
name|DEFAULT_WRITE_BUFFER_SIZE
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test configuration validators.  */
end_comment

begin_class
DECL|class|TestConfigurationValidators
specifier|public
class|class
name|TestConfigurationValidators
extends|extends
name|Assert
block|{
DECL|field|FAKE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_KEY
init|=
literal|"FakeKey"
decl_stmt|;
DECL|method|TestConfigurationValidators ()
specifier|public
name|TestConfigurationValidators
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntegerConfigValidator ()
specifier|public
name|void
name|testIntegerConfigValidator
parameter_list|()
throws|throws
name|Exception
block|{
name|IntegerConfigurationBasicValidator
name|integerConfigurationValidator
init|=
operator|new
name|IntegerConfigurationBasicValidator
argument_list|(
name|MIN_BUFFER_SIZE
argument_list|,
name|MAX_BUFFER_SIZE
argument_list|,
name|DEFAULT_READ_BUFFER_SIZE
argument_list|,
name|FAKE_KEY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MIN_BUFFER_SIZE
argument_list|,
operator|(
name|int
operator|)
name|integerConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"3072"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_READ_BUFFER_SIZE
argument_list|,
operator|(
name|int
operator|)
name|integerConfigurationValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_BUFFER_SIZE
argument_list|,
operator|(
name|int
operator|)
name|integerConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"104857600"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidConfigurationValueException
operator|.
name|class
argument_list|)
DECL|method|testIntegerConfigValidatorThrowsIfMissingValidValue ()
specifier|public
name|void
name|testIntegerConfigValidatorThrowsIfMissingValidValue
parameter_list|()
throws|throws
name|Exception
block|{
name|IntegerConfigurationBasicValidator
name|integerConfigurationValidator
init|=
operator|new
name|IntegerConfigurationBasicValidator
argument_list|(
name|MIN_BUFFER_SIZE
argument_list|,
name|MAX_BUFFER_SIZE
argument_list|,
name|DEFAULT_READ_BUFFER_SIZE
argument_list|,
name|FAKE_KEY
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|integerConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"3072"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLongConfigValidator ()
specifier|public
name|void
name|testLongConfigValidator
parameter_list|()
throws|throws
name|Exception
block|{
name|LongConfigurationBasicValidator
name|longConfigurationValidator
init|=
operator|new
name|LongConfigurationBasicValidator
argument_list|(
name|MIN_BUFFER_SIZE
argument_list|,
name|MAX_BUFFER_SIZE
argument_list|,
name|DEFAULT_WRITE_BUFFER_SIZE
argument_list|,
name|FAKE_KEY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DEFAULT_WRITE_BUFFER_SIZE
argument_list|,
operator|(
name|long
operator|)
name|longConfigurationValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MIN_BUFFER_SIZE
argument_list|,
operator|(
name|long
operator|)
name|longConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"3072"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_BUFFER_SIZE
argument_list|,
operator|(
name|long
operator|)
name|longConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"104857600"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidConfigurationValueException
operator|.
name|class
argument_list|)
DECL|method|testLongConfigValidatorThrowsIfMissingValidValue ()
specifier|public
name|void
name|testLongConfigValidatorThrowsIfMissingValidValue
parameter_list|()
throws|throws
name|Exception
block|{
name|LongConfigurationBasicValidator
name|longConfigurationValidator
init|=
operator|new
name|LongConfigurationBasicValidator
argument_list|(
name|MIN_BUFFER_SIZE
argument_list|,
name|MAX_BUFFER_SIZE
argument_list|,
name|DEFAULT_READ_BUFFER_SIZE
argument_list|,
name|FAKE_KEY
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|longConfigurationValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBooleanConfigValidator ()
specifier|public
name|void
name|testBooleanConfigValidator
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanConfigurationBasicValidator
name|booleanConfigurationValidator
init|=
operator|new
name|BooleanConfigurationBasicValidator
argument_list|(
name|FAKE_KEY
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|booleanConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|booleanConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"False"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|booleanConfigurationValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidConfigurationValueException
operator|.
name|class
argument_list|)
DECL|method|testBooleanConfigValidatorThrowsIfMissingValidValue ()
specifier|public
name|void
name|testBooleanConfigValidatorThrowsIfMissingValidValue
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanConfigurationBasicValidator
name|booleanConfigurationValidator
init|=
operator|new
name|BooleanConfigurationBasicValidator
argument_list|(
name|FAKE_KEY
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|booleanConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"almostTrue"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStringConfigValidator ()
specifier|public
name|void
name|testStringConfigValidator
parameter_list|()
throws|throws
name|Exception
block|{
name|StringConfigurationBasicValidator
name|stringConfigurationValidator
init|=
operator|new
name|StringConfigurationBasicValidator
argument_list|(
name|FAKE_KEY
argument_list|,
literal|"value"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|stringConfigurationValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"someValue"
argument_list|,
name|stringConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"someValue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidConfigurationValueException
operator|.
name|class
argument_list|)
DECL|method|testStringConfigValidatorThrowsIfMissingValidValue ()
specifier|public
name|void
name|testStringConfigValidatorThrowsIfMissingValidValue
parameter_list|()
throws|throws
name|Exception
block|{
name|StringConfigurationBasicValidator
name|stringConfigurationValidator
init|=
operator|new
name|StringConfigurationBasicValidator
argument_list|(
name|FAKE_KEY
argument_list|,
literal|"value"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|stringConfigurationValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBase64StringConfigValidator ()
specifier|public
name|void
name|testBase64StringConfigValidator
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|encodedVal
init|=
operator|new
name|String
argument_list|(
operator|new
name|Base64
argument_list|()
operator|.
name|encode
argument_list|(
literal|"someValue"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Base64StringConfigurationBasicValidator
name|base64StringConfigurationValidator
init|=
operator|new
name|Base64StringConfigurationBasicValidator
argument_list|(
name|FAKE_KEY
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|base64StringConfigurationValidator
operator|.
name|validate
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|encodedVal
argument_list|,
name|base64StringConfigurationValidator
operator|.
name|validate
argument_list|(
name|encodedVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|InvalidConfigurationValueException
operator|.
name|class
argument_list|)
DECL|method|testBase64StringConfigValidatorThrowsIfMissingValidValue ()
specifier|public
name|void
name|testBase64StringConfigValidatorThrowsIfMissingValidValue
parameter_list|()
throws|throws
name|Exception
block|{
name|Base64StringConfigurationBasicValidator
name|base64StringConfigurationValidator
init|=
operator|new
name|Base64StringConfigurationBasicValidator
argument_list|(
name|FAKE_KEY
argument_list|,
literal|"value"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|base64StringConfigurationValidator
operator|.
name|validate
argument_list|(
literal|"some&%Value"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

