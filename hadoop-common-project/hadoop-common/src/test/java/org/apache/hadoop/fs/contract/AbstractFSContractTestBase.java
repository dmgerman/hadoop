begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileStatus
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
name|FileSystem
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
name|Path
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
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|internal
operator|.
name|AssumptionViolatedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|net
operator|.
name|URI
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|cleanup
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|skip
import|;
end_import

begin_comment
comment|/**  * This is the base class for all the contract tests.  */
end_comment

begin_class
DECL|class|AbstractFSContractTestBase
specifier|public
specifier|abstract
class|class
name|AbstractFSContractTestBase
extends|extends
name|Assert
implements|implements
name|ContractOptions
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
name|AbstractFSContractTestBase
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Length of files to work with: {@value}.    */
DECL|field|TEST_FILE_LEN
specifier|public
specifier|static
specifier|final
name|int
name|TEST_FILE_LEN
init|=
literal|1024
decl_stmt|;
comment|/**    * standard test timeout: {@value}.    */
DECL|field|DEFAULT_TEST_TIMEOUT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_TEST_TIMEOUT
init|=
literal|180
operator|*
literal|1000
decl_stmt|;
comment|/**    * The FS contract used for these tests.    */
DECL|field|contract
specifier|private
name|AbstractFSContract
name|contract
decl_stmt|;
comment|/**    * The test filesystem extracted from it.    */
DECL|field|fileSystem
specifier|private
name|FileSystem
name|fileSystem
decl_stmt|;
comment|/**    * The path for tests.    */
DECL|field|testPath
specifier|private
name|Path
name|testPath
decl_stmt|;
annotation|@
name|Rule
DECL|field|methodName
specifier|public
name|TestName
name|methodName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|nameTestThread ()
specifier|public
specifier|static
name|void
name|nameTestThread
parameter_list|()
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"JUnit"
argument_list|)
expr_stmt|;
block|}
comment|/**    * This must be implemented by all instantiated test cases.    * -provide the FS contract    * @return the FS contract    */
DECL|method|createContract (Configuration conf)
specifier|protected
specifier|abstract
name|AbstractFSContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Get the contract.    * @return the contract, which will be non-null once the setup operation has    * succeeded    */
DECL|method|getContract ()
specifier|protected
name|AbstractFSContract
name|getContract
parameter_list|()
block|{
return|return
name|contract
return|;
block|}
comment|/**    * Get the filesystem created in startup.    * @return the filesystem to use for tests    */
DECL|method|getFileSystem ()
specifier|public
name|FileSystem
name|getFileSystem
parameter_list|()
block|{
return|return
name|fileSystem
return|;
block|}
comment|/**    * Get the log of the base class.    * @return a logger    */
DECL|method|getLogger ()
specifier|public
specifier|static
name|Logger
name|getLogger
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
comment|/**    * Skip a test if a feature is unsupported in this FS.    * @param feature feature to look for    * @throws IOException IO problem    */
DECL|method|skipIfUnsupported (String feature)
specifier|protected
name|void
name|skipIfUnsupported
parameter_list|(
name|String
name|feature
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isSupported
argument_list|(
name|feature
argument_list|)
condition|)
block|{
name|skip
argument_list|(
literal|"Skipping as unsupported feature: "
operator|+
name|feature
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Is a feature supported?    * @param feature feature    * @return true iff the feature is supported    * @throws IOException IO problems    */
DECL|method|isSupported (String feature)
specifier|protected
name|boolean
name|isSupported
parameter_list|(
name|String
name|feature
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|contract
operator|.
name|isSupported
argument_list|(
name|feature
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Include at the start of tests to skip them if the FS is not enabled.    */
DECL|method|assumeEnabled ()
specifier|protected
name|void
name|assumeEnabled
parameter_list|()
block|{
if|if
condition|(
operator|!
name|contract
operator|.
name|isEnabled
argument_list|()
condition|)
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"test cases disabled for "
operator|+
name|contract
argument_list|)
throw|;
block|}
comment|/**    * Create a configuration. May be overridden by tests/instantiations    * @return a configuration    */
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
return|;
block|}
comment|/**    * Set the timeout for every test.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
name|getTestTimeoutMillis
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Option for tests to override the default timeout value.    * @return the current test timeout    */
DECL|method|getTestTimeoutMillis ()
specifier|protected
name|int
name|getTestTimeoutMillis
parameter_list|()
block|{
return|return
name|DEFAULT_TEST_TIMEOUT
return|;
block|}
comment|/**    * Setup: create the contract then init it.    * @throws Exception on any failure    */
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"== Setup =="
argument_list|)
expr_stmt|;
name|contract
operator|=
name|createContract
argument_list|(
name|createConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|contract
operator|.
name|init
argument_list|()
expr_stmt|;
comment|//skip tests if they aren't enabled
name|assumeEnabled
argument_list|()
expr_stmt|;
comment|//extract the test FS
name|fileSystem
operator|=
name|contract
operator|.
name|getTestFileSystem
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"null filesystem"
argument_list|,
name|fileSystem
argument_list|)
expr_stmt|;
name|URI
name|fsURI
init|=
name|fileSystem
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test filesystem = {} implemented by {}"
argument_list|,
name|fsURI
argument_list|,
name|fileSystem
argument_list|)
expr_stmt|;
comment|//sanity check to make sure that the test FS picked up really matches
comment|//the scheme chosen. This is to avoid defaulting back to the localFS
comment|//which would be drastic for root FS tests
name|assertEquals
argument_list|(
literal|"wrong filesystem of "
operator|+
name|fsURI
argument_list|,
name|contract
operator|.
name|getScheme
argument_list|()
argument_list|,
name|fsURI
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
comment|//create the test path
name|testPath
operator|=
name|getContract
argument_list|()
operator|.
name|getTestPath
argument_list|()
expr_stmt|;
name|mkdirs
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"== Setup complete =="
argument_list|)
expr_stmt|;
block|}
comment|/**    * Teardown.    * @throws Exception on any failure    */
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"== Teardown =="
argument_list|)
expr_stmt|;
name|deleteTestDirInTeardown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"== Teardown complete =="
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete the test dir in the per-test teardown.    * @throws IOException    */
DECL|method|deleteTestDirInTeardown ()
specifier|protected
name|void
name|deleteTestDirInTeardown
parameter_list|()
throws|throws
name|IOException
block|{
name|cleanup
argument_list|(
literal|"TEARDOWN"
argument_list|,
name|getFileSystem
argument_list|()
argument_list|,
name|testPath
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a path under the test path provided by    * the FS contract.    * @param filepath path string in    * @return a path qualified by the test filesystem    * @throws IOException IO problems    */
DECL|method|path (String filepath)
specifier|protected
name|Path
name|path
parameter_list|(
name|String
name|filepath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|getContract
argument_list|()
operator|.
name|getTestPath
argument_list|()
argument_list|,
name|filepath
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Take a simple path like "/something" and turn it into    * a qualified path against the test FS.    * @param filepath path string in    * @return a path qualified by the test filesystem    * @throws IOException IO problems    */
DECL|method|absolutepath (String filepath)
specifier|protected
name|Path
name|absolutepath
parameter_list|(
name|String
name|filepath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|filepath
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * List a path in the test FS.    * @param path path to list    * @return the contents of the path/dir    * @throws IOException IO problems    */
DECL|method|ls (Path path)
specifier|protected
name|String
name|ls
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ContractTestUtils
operator|.
name|ls
argument_list|(
name|fileSystem
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/**    * Describe a test. This is a replacement for javadocs    * where the tests role is printed in the log output    * @param text description    */
DECL|method|describe (String text)
specifier|protected
name|void
name|describe
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
comment|/**    * Handle the outcome of an operation not being the strictest    * exception desired, but one that, while still within the boundary    * of the contract, is a bit looser.    *    * If the FS contract says that they support the strictest exceptions,    * that is what they must return, and the exception here is rethrown    * @param action Action    * @param expectedException what was expected    * @param e exception that was received    */
DECL|method|handleRelaxedException (String action, String expectedException, Exception e)
specifier|protected
name|void
name|handleRelaxedException
parameter_list|(
name|String
name|action
parameter_list|,
name|String
name|expectedException
parameter_list|,
name|Exception
name|e
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|getContract
argument_list|()
operator|.
name|isSupported
argument_list|(
name|SUPPORTS_STRICT_EXCEPTIONS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"The expected exception {}  was not the exception class"
operator|+
literal|" raised on {}: {}"
argument_list|,
name|action
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
argument_list|,
name|expectedException
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|/**    * Handle expected exceptions through logging and/or other actions.    * @param e exception raised.    */
DECL|method|handleExpectedException (Exception e)
specifier|protected
name|void
name|handleExpectedException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getLogger
argument_list|()
operator|.
name|debug
argument_list|(
literal|"expected :{}"
argument_list|,
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|/**    * assert that a path exists.    * @param message message to use in an assertion    * @param path path to probe    * @throws IOException IO problems    */
DECL|method|assertPathExists (String message, Path path)
specifier|public
name|void
name|assertPathExists
parameter_list|(
name|String
name|message
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|assertPathExists
argument_list|(
name|fileSystem
argument_list|,
name|message
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a path does not exist.    * @param message message to use in an assertion    * @param path path to probe    * @throws IOException IO problems    */
DECL|method|assertPathDoesNotExist (String message, Path path)
specifier|public
name|void
name|assertPathDoesNotExist
parameter_list|(
name|String
name|message
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|assertPathDoesNotExist
argument_list|(
name|fileSystem
argument_list|,
name|message
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a file exists and whose {@link FileStatus} entry    * declares that this is a file and not a symlink or directory.    *    * @param filename name of the file    * @throws IOException IO problems during file operations    */
DECL|method|assertIsFile (Path filename)
specifier|protected
name|void
name|assertIsFile
parameter_list|(
name|Path
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|assertIsFile
argument_list|(
name|fileSystem
argument_list|,
name|filename
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a file exists and whose {@link FileStatus} entry    * declares that this is a file and not a symlink or directory.    *    * @param path name of the file    * @throws IOException IO problems during file operations    */
DECL|method|assertIsDirectory (Path path)
specifier|protected
name|void
name|assertIsDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|assertIsDirectory
argument_list|(
name|fileSystem
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a file exists and whose {@link FileStatus} entry    * declares that this is a file and not a symlink or directory.    *    * @throws IOException IO problems during file operations    */
DECL|method|mkdirs (Path path)
specifier|protected
name|void
name|mkdirs
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
literal|"Failed to mkdir "
operator|+
name|path
argument_list|,
name|fileSystem
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that a delete succeeded.    * @param path path to delete    * @param recursive recursive flag    * @throws IOException IO problems    */
DECL|method|assertDeleted (Path path, boolean recursive)
specifier|protected
name|void
name|assertDeleted
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|assertDeleted
argument_list|(
name|fileSystem
argument_list|,
name|path
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that the result value == -1; which implies    * that a read was successful.    * @param text text to include in a message (usually the operation)    * @param result read result to validate    */
DECL|method|assertMinusOne (String text, int result)
specifier|protected
name|void
name|assertMinusOne
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|result
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|text
operator|+
literal|" wrong read result "
operator|+
name|result
argument_list|,
operator|-
literal|1
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|rename (Path src, Path dst)
specifier|protected
name|boolean
name|rename
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
return|;
block|}
DECL|method|generateAndLogErrorListing (Path src, Path dst)
specifier|protected
name|String
name|generateAndLogErrorListing
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|getLogger
argument_list|()
operator|.
name|error
argument_list|(
literal|"src dir "
operator|+
name|ContractTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|src
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|destDirLS
init|=
name|ContractTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|dst
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|isDirectory
argument_list|(
name|dst
argument_list|)
condition|)
block|{
comment|//include the dir into the listing
name|destDirLS
operator|=
name|destDirLS
operator|+
literal|"\n"
operator|+
name|ContractTestUtils
operator|.
name|ls
argument_list|(
name|fs
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
return|return
name|destDirLS
return|;
block|}
block|}
end_class

end_unit

