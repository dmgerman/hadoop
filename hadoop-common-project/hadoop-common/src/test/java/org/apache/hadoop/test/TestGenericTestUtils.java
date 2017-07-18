begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|fail
import|;
end_import

begin_class
DECL|class|TestGenericTestUtils
specifier|public
class|class
name|TestGenericTestUtils
extends|extends
name|GenericTestUtils
block|{
annotation|@
name|Test
DECL|method|testAssertExceptionContainsNullEx ()
specifier|public
name|void
name|testAssertExceptionContainsNullEx
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|assertExceptionContains
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|E_NULL_THROWABLE
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAssertExceptionContainsNullString ()
specifier|public
name|void
name|testAssertExceptionContainsNullString
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|assertExceptionContains
argument_list|(
literal|""
argument_list|,
operator|new
name|BrokenException
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|E_NULL_THROWABLE_STRING
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAssertExceptionContainsWrongText ()
specifier|public
name|void
name|testAssertExceptionContainsWrongText
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|assertExceptionContains
argument_list|(
literal|"Expected"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"(actual)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
name|String
name|s
init|=
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|contains
argument_list|(
name|E_UNEXPECTED_EXCEPTION
argument_list|)
operator|||
operator|!
name|s
operator|.
name|contains
argument_list|(
literal|"(actual)"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"No nested cause in assertion"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAssertExceptionContainsWorking ()
specifier|public
name|void
name|testAssertExceptionContainsWorking
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertExceptionContains
argument_list|(
literal|"Expected"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"Expected"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|BrokenException
specifier|private
specifier|static
class|class
name|BrokenException
extends|extends
name|Exception
block|{
DECL|method|BrokenException ()
specifier|public
name|BrokenException
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testLogCapturer ()
specifier|public
name|void
name|testLogCapturer
parameter_list|()
block|{
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestGenericTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
name|LogCapturer
name|logCapturer
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|log
argument_list|)
decl_stmt|;
specifier|final
name|String
name|infoMessage
init|=
literal|"info message"
decl_stmt|;
comment|// test get output message
name|log
operator|.
name|info
argument_list|(
name|infoMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|endsWith
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|infoMessage
operator|+
literal|"%n"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test clear output
name|logCapturer
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// test stop capturing
name|logCapturer
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|infoMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testLogCapturerSlf4jLogger ()
specifier|public
name|void
name|testLogCapturerSlf4jLogger
parameter_list|()
block|{
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestGenericTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
name|LogCapturer
name|logCapturer
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|logger
argument_list|)
decl_stmt|;
specifier|final
name|String
name|infoMessage
init|=
literal|"info message"
decl_stmt|;
comment|// test get output message
name|logger
operator|.
name|info
argument_list|(
name|infoMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|endsWith
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|infoMessage
operator|+
literal|"%n"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test clear output
name|logCapturer
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// test stop capturing
name|logCapturer
operator|.
name|stopCapturing
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
name|infoMessage
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|logCapturer
operator|.
name|getOutput
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWaitingForConditionWithInvalidParams ()
specifier|public
name|void
name|testWaitingForConditionWithInvalidParams
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// test waitFor method with null supplier interface
try|try
block|{
name|waitFor
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
name|GenericTestUtils
operator|.
name|ERROR_MISSING_ARGUMENT
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|simpleSupplier
init|=
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
comment|// test waitFor method with waitForMillis greater than checkEveryMillis
name|waitFor
argument_list|(
name|simpleSupplier
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
comment|// test waitFor method with waitForMillis smaller than checkEveryMillis
name|waitFor
argument_list|(
name|simpleSupplier
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Excepted a failure when the param value of"
operator|+
literal|" waitForMillis is smaller than checkEveryMillis."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
name|GenericTestUtils
operator|.
name|ERROR_INVALID_ARGUMENT
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testToLevel ()
specifier|public
name|void
name|testToLevel
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertEquals
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
name|toLevel
argument_list|(
literal|"INFO"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|,
name|toLevel
argument_list|(
literal|"NonExistLevel"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
name|toLevel
argument_list|(
literal|"INFO"
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|,
name|toLevel
argument_list|(
literal|"NonExistLevel"
argument_list|,
name|Level
operator|.
name|TRACE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

