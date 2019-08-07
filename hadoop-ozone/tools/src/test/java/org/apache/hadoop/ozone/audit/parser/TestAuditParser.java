begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.audit.parser
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|audit
operator|.
name|parser
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
comment|/**  * Tests AuditParser.  */
end_comment

begin_class
DECL|class|TestAuditParser
specifier|public
class|class
name|TestAuditParser
block|{
DECL|field|outputBaseDir
specifier|private
specifier|static
name|File
name|outputBaseDir
decl_stmt|;
DECL|field|parserTool
specifier|private
specifier|static
name|AuditParser
name|parserTool
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
name|TestAuditParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|OUT
specifier|private
specifier|static
specifier|final
name|ByteArrayOutputStream
name|OUT
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
DECL|field|dbName
specifier|private
specifier|static
name|String
name|dbName
decl_stmt|;
DECL|field|LOGS
specifier|private
specifier|static
specifier|final
name|String
name|LOGS
init|=
name|TestAuditParser
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"testaudit.log"
argument_list|)
operator|.
name|getPath
argument_list|()
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
name|getRandomTempDir
argument_list|()
expr_stmt|;
name|dbName
operator|=
name|getRandomTempDir
argument_list|()
operator|+
literal|"/testAudit.db"
expr_stmt|;
name|parserTool
operator|=
operator|new
name|AuditParser
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
name|dbName
block|,
literal|"load"
block|,
name|LOGS
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|""
argument_list|)
expr_stmt|;
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
name|OUT
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
name|OUT
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
specifier|static
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
name|parserTool
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
name|OUT
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
comment|/**    * Test to find top 5 commands.    */
annotation|@
name|Test
DECL|method|testTemplateTop5Cmds ()
specifier|public
name|void
name|testTemplateTop5Cmds
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|dbName
block|,
literal|"template"
block|,
literal|"top5cmds"
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|"DELETE_KEY\t3\t\n"
operator|+
literal|"ALLOCATE_KEY\t2\t\n"
operator|+
literal|"COMMIT_KEY\t2\t\n"
operator|+
literal|"CREATE_BUCKET\t1\t\n"
operator|+
literal|"CREATE_VOLUME\t1\t\n\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to find top 5 users.    */
annotation|@
name|Test
DECL|method|testTemplateTop5Users ()
specifier|public
name|void
name|testTemplateTop5Users
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|dbName
block|,
literal|"template"
block|,
literal|"top5users"
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|"hadoop\t9\t\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to find top 5 users.    */
annotation|@
name|Test
DECL|method|testTemplateTop5ActiveTimeBySeconds ()
specifier|public
name|void
name|testTemplateTop5ActiveTimeBySeconds
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|dbName
block|,
literal|"template"
block|,
literal|"top5activetimebyseconds"
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|"2018-09-06 01:57:22\t3\t\n"
operator|+
literal|"2018-09-06 01:58:08\t1\t\n"
operator|+
literal|"2018-09-06 01:58:18\t1\t\n"
operator|+
literal|"2018-09-06 01:59:36\t1\t\n"
operator|+
literal|"2018-09-06 01:59:41\t1\t\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to execute custom query.    */
annotation|@
name|Test
DECL|method|testQueryCommand ()
specifier|public
name|void
name|testQueryCommand
parameter_list|()
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|dbName
block|,
literal|"query"
block|,
literal|"select count(*) from audit"
block|}
decl_stmt|;
name|execute
argument_list|(
name|args
argument_list|,
literal|"9"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to check help message.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testHelp ()
specifier|public
name|void
name|testHelp
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"Usage: ozone auditparser [-hV] [--verbose] "
operator|+
literal|"[-conf=<configurationPath>]\n"
operator|+
literal|"                         [-D=<String=String>]...<database> "
operator|+
literal|"[COMMAND]"
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomTempDir ()
specifier|private
specifier|static
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

