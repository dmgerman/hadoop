begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.genconf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genconf
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
name|io
operator|.
name|FileUtils
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
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|picocli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|IExceptionHandler2
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|ParseResult
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|ParameterException
import|;
end_import

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
name|File
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Tests GenerateOzoneRequiredConfigurations.  */
end_comment

begin_class
DECL|class|TestGenerateOzoneRequiredConfigurations
specifier|public
class|class
name|TestGenerateOzoneRequiredConfigurations
block|{
DECL|field|outputBaseDir
specifier|private
specifier|static
name|File
name|outputBaseDir
decl_stmt|;
DECL|field|genconfTool
specifier|private
specifier|static
name|GenerateOzoneRequiredConfigurations
name|genconfTool
decl_stmt|;
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
name|TestGenerateOzoneRequiredConfigurations
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|out
specifier|private
specifier|final
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|err
specifier|private
specifier|final
name|ByteArrayOutputStream
name|err
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|OLD_OUT
specifier|private
specifier|static
specifier|final
name|PrintStream
name|OLD_OUT
init|=
name|System
operator|.
name|out
decl_stmt|;
DECL|field|OLD_ERR
specifier|private
specifier|static
specifier|final
name|PrintStream
name|OLD_ERR
init|=
name|System
operator|.
name|err
decl_stmt|;
comment|/**    * Creates output directory which will be used by the test-cases.    * If a test-case needs a separate directory, it has to create a random    * directory inside {@code outputBaseDir}.    *    * @throws Exception In case of exception while creating output directory.    */
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|outputBaseDir
operator|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|outputBaseDir
argument_list|)
expr_stmt|;
name|genconfTool
operator|=
operator|new
name|GenerateOzoneRequiredConfigurations
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|err
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
comment|// reset stream after each unit test
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
name|err
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// restore system streams
name|System
operator|.
name|setOut
argument_list|(
name|OLD_OUT
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|OLD_ERR
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cleans up the output base directory.    */
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|outputBaseDir
argument_list|)
expr_stmt|;
block|}
DECL|method|execute (String[] args, String msg)
specifier|private
name|void
name|execute
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|arguments
init|=
operator|new
name|ArrayList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing shell command with args {}"
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
name|CommandLine
name|cmd
init|=
name|genconfTool
operator|.
name|getCmd
argument_list|()
decl_stmt|;
name|IExceptionHandler2
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|exceptionHandler
init|=
operator|new
name|IExceptionHandler2
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|handleParseException
parameter_list|(
name|ParameterException
name|ex
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|handleExecutionException
parameter_list|(
name|ExecutionException
name|ex
parameter_list|,
name|ParseResult
name|parseResult
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
block|}
decl_stmt|;
name|cmd
operator|.
name|parseWithHandlers
argument_list|(
operator|new
name|CommandLine
operator|.
name|RunLast
argument_list|()
argument_list|,
name|exceptionHandler
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|out
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|executeWithException (String[] args, String msg)
specifier|private
name|void
name|executeWithException
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|arguments
init|=
operator|new
name|ArrayList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing shell command with args {}"
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
name|CommandLine
name|cmd
init|=
name|genconfTool
operator|.
name|getCmd
argument_list|()
decl_stmt|;
name|IExceptionHandler2
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|exceptionHandler
init|=
operator|new
name|IExceptionHandler2
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|handleParseException
parameter_list|(
name|ParameterException
name|ex
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|handleExecutionException
parameter_list|(
name|ExecutionException
name|ex
parameter_list|,
name|ParseResult
name|parseResult
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|cmd
operator|.
name|parseWithHandlers
argument_list|(
operator|new
name|CommandLine
operator|.
name|RunLast
argument_list|()
argument_list|,
name|exceptionHandler
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests a valid path and generates ozone-site.xml by calling    * {@code GenerateOzoneRequiredConfigurations#generateConfigurations}.    * Further verifies that all properties have a default value.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testGenerateConfigurations ()
specifier|public
name|void
name|testGenerateConfigurations
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempPath
init|=
name|getRandomTempDir
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|tempPath
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|"ozone-site.xml has been generated at "
operator|+
name|tempPath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|//Fetch file generated by above line
name|URL
name|url
init|=
operator|new
name|File
argument_list|(
name|tempPath
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/ozone-site.xml"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
decl_stmt|;
name|OzoneConfiguration
name|oc
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OzoneConfiguration
operator|.
name|Property
argument_list|>
name|allProperties
init|=
name|oc
operator|.
name|readPropertyFromXml
argument_list|(
name|url
argument_list|)
decl_stmt|;
comment|//Asserts all properties have a non-empty value
for|for
control|(
name|OzoneConfiguration
operator|.
name|Property
name|p
range|:
name|allProperties
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
operator|&&
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Generates ozone-site.xml at specified path.    * Verify that it does not overwrite if file already exists in path.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testDoesNotOverwrite ()
specifier|public
name|void
name|testDoesNotOverwrite
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempPath
init|=
name|getRandomTempDir
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|tempPath
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|"ozone-site.xml has been generated at "
operator|+
name|tempPath
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|//attempt overwrite
name|execute
argument_list|(
name|args
argument_list|,
literal|"ozone-site.xml already exists at "
operator|+
name|tempPath
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" and will not be overwritten"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to avoid generating ozone-site.xml when insufficient permission.    * @throws Exception    */
annotation|@
name|Test
DECL|method|genconfFailureByInsufficientPermissions ()
specifier|public
name|void
name|genconfFailureByInsufficientPermissions
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempPath
init|=
name|getRandomTempDir
argument_list|()
decl_stmt|;
name|tempPath
operator|.
name|setReadOnly
argument_list|()
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|tempPath
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|executeWithException
argument_list|(
name|args
argument_list|,
literal|"Insufficient permission."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to avoid generating ozone-site.xml when invalid path.    * @throws Exception    */
annotation|@
name|Test
DECL|method|genconfFailureByInvalidPath ()
specifier|public
name|void
name|genconfFailureByInvalidPath
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempPath
init|=
name|getRandomTempDir
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"invalid-path"
block|}
decl_stmt|;
name|executeWithException
argument_list|(
name|args
argument_list|,
literal|"Invalid directory path."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to avoid generating ozone-site.xml when path not specified.    * @throws Exception    */
annotation|@
name|Test
DECL|method|genconfPathNotSpecified ()
specifier|public
name|void
name|genconfPathNotSpecified
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempPath
init|=
name|getRandomTempDir
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
name|executeWithException
argument_list|(
name|args
argument_list|,
literal|"Missing required parameter:<path>"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to check help message.    * @throws Exception    */
annotation|@
name|Test
DECL|method|genconfHelp ()
specifier|public
name|void
name|genconfHelp
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tempPath
init|=
name|getRandomTempDir
argument_list|()
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"--help"
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|"Usage: ozone genconf [-hV] [--verbose]"
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomTempDir ()
specifier|private
name|File
name|getRandomTempDir
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tempDir
init|=
operator|new
name|File
argument_list|(
name|outputBaseDir
argument_list|,
name|RandomStringUtils
operator|.
name|randomAlphanumeric
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
return|return
name|tempDir
return|;
block|}
block|}
end_class

end_unit

