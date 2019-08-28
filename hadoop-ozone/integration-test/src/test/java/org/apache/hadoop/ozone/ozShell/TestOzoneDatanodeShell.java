begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ozShell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ozShell
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
name|File
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
name|FileUtil
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
name|client
operator|.
name|ReplicationFactor
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
name|ozone
operator|.
name|HddsDatanodeService
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_REPLICATION
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
name|Test
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
name|ParameterException
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
name|RunLast
import|;
end_import

begin_comment
comment|/**  * This test class specified for testing Ozone datanode shell command.  */
end_comment

begin_class
DECL|class|TestOzoneDatanodeShell
specifier|public
class|class
name|TestOzoneDatanodeShell
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
name|TestOzoneDatanodeShell
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|300000
argument_list|)
decl_stmt|;
DECL|field|baseDir
specifier|private
specifier|static
name|File
name|baseDir
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|static
name|HddsDatanodeService
name|datanode
init|=
literal|null
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
comment|/**    * Create a MiniDFSCluster for testing with using distributed Ozone    * handler type.    *    * @throws Exception    */
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
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getTempPath
argument_list|(
name|TestOzoneDatanodeShell
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|baseDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|datanode
operator|=
name|HddsDatanodeService
operator|.
name|createHddsDatanodeService
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_REPLICATION
argument_list|,
name|ReplicationFactor
operator|.
name|THREE
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
block|}
comment|/**    * shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|baseDir
operator|!=
literal|null
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|baseDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
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
DECL|method|executeDatanode (HddsDatanodeService hdds, String[] args)
specifier|private
name|void
name|executeDatanode
parameter_list|(
name|HddsDatanodeService
name|hdds
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing datanode command with args {}"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|CommandLine
name|cmd
init|=
name|hdds
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
name|RunLast
argument_list|()
argument_list|,
name|exceptionHandler
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Execute command, assert exception message and returns true if error    * was thrown and contains the specified usage string.    */
DECL|method|executeDatanodeWithError (HddsDatanodeService hdds, String[] args, String expectedError)
specifier|private
name|void
name|executeDatanodeWithError
parameter_list|(
name|HddsDatanodeService
name|hdds
parameter_list|,
name|String
index|[]
name|args
parameter_list|,
name|String
name|expectedError
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|expectedError
argument_list|)
condition|)
block|{
name|executeDatanode
argument_list|(
name|hdds
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|executeDatanode
argument_list|(
name|hdds
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception is expected from command execution "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|expectedError
argument_list|)
condition|)
block|{
name|Throwable
name|exceptionToCheck
init|=
name|ex
decl_stmt|;
if|if
condition|(
name|exceptionToCheck
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|exceptionToCheck
operator|=
name|exceptionToCheck
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Error of shell code doesn't contain the "
operator|+
literal|"exception [%s] in [%s]"
argument_list|,
name|expectedError
argument_list|,
name|exceptionToCheck
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|exceptionToCheck
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|expectedError
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testDatanodeCommand ()
specifier|public
name|void
name|testDatanodeCommand
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testDatanodeIncompleteCommand"
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
comment|//executing 'ozone datanode'
comment|//'ozone datanode' command should not result in error
name|executeDatanodeWithError
argument_list|(
name|datanode
argument_list|,
name|args
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDatanodeInvalidParamCommand ()
specifier|public
name|void
name|testDatanodeInvalidParamCommand
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testDatanodeIncompleteCommand"
argument_list|)
expr_stmt|;
name|String
name|expectedError
init|=
literal|"Unknown option: -invalidParam"
decl_stmt|;
comment|//executing 'ozone datanode -invalidParam'
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-invalidParam"
block|}
decl_stmt|;
name|executeDatanodeWithError
argument_list|(
name|datanode
argument_list|,
name|args
argument_list|,
name|expectedError
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

