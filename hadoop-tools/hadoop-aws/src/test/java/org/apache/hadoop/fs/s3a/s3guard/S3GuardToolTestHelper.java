begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
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
operator|.
name|s3guard
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Helper class for tests which make CLI invocations of the S3Guard tools.  * That's {@link AbstractS3GuardToolTestBase} and others.  */
end_comment

begin_class
DECL|class|S3GuardToolTestHelper
specifier|public
specifier|final
class|class
name|S3GuardToolTestHelper
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
name|S3GuardToolTestHelper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|S3GuardToolTestHelper ()
specifier|private
name|S3GuardToolTestHelper
parameter_list|()
block|{   }
comment|/**    * Execute a command, returning the buffer if the command actually completes.    * If an exception is raised the output is logged instead.    * @param cmd command    * @param args argument list    * @throws Exception on any failure    */
DECL|method|exec (S3GuardTool cmd, String... args)
specifier|public
specifier|static
name|String
name|exec
parameter_list|(
name|S3GuardTool
name|cmd
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|buf
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|exec
argument_list|(
literal|0
argument_list|,
literal|""
argument_list|,
name|cmd
argument_list|,
name|buf
argument_list|,
name|args
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Command {} failed: \n{}"
argument_list|,
name|cmd
argument_list|,
name|buf
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**    * Execute a command, saving the output into the buffer.    * @param expectedResult expected result of the command.    * @param errorText error text to include in the assertion.    * @param cmd command    * @param buf buffer to use for tool output (not SLF4J output)    * @param args argument list    * @throws Exception on any failure    */
DECL|method|exec (final int expectedResult, final String errorText, final S3GuardTool cmd, final ByteArrayOutputStream buf, final String... args)
specifier|public
specifier|static
name|void
name|exec
parameter_list|(
specifier|final
name|int
name|expectedResult
parameter_list|,
specifier|final
name|String
name|errorText
parameter_list|,
specifier|final
name|S3GuardTool
name|cmd
parameter_list|,
specifier|final
name|ByteArrayOutputStream
name|buf
parameter_list|,
specifier|final
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"exec {}"
argument_list|,
operator|(
name|Object
operator|)
name|args
argument_list|)
expr_stmt|;
name|int
name|r
decl_stmt|;
try|try
init|(
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|buf
argument_list|)
init|)
block|{
name|r
operator|=
name|cmd
operator|.
name|run
argument_list|(
name|args
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|expectedResult
operator|!=
name|r
condition|)
block|{
name|String
name|message
init|=
name|errorText
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
operator|(
name|errorText
operator|+
literal|": "
operator|)
operator|+
literal|"Command "
operator|+
name|cmd
operator|+
literal|" failed\n"
operator|+
name|buf
decl_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expectedResult
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

