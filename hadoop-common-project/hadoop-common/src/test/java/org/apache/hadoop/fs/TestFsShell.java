begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|junit
operator|.
name|framework
operator|.
name|AssertionFailedError
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
name|IOUtils
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
name|tracing
operator|.
name|SetSpanReceiver
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
name|tracing
operator|.
name|SpanReceiverHost
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
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|SamplerBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|impl
operator|.
name|AlwaysSampler
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

begin_class
DECL|class|TestFsShell
specifier|public
class|class
name|TestFsShell
block|{
annotation|@
name|Test
DECL|method|testConfWithInvalidFile ()
specifier|public
name|void
name|testConfWithInvalidFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"--conf=invalidFile"
expr_stmt|;
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FsShell
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|th
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|th
operator|instanceof
name|RuntimeException
operator|)
condition|)
block|{
throw|throw
operator|new
name|AssertionFailedError
argument_list|(
literal|"Expected Runtime exception, got: "
operator|+
name|th
argument_list|)
operator|.
name|initCause
argument_list|(
name|th
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTracing ()
specifier|public
name|void
name|testTracing
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|FsShell
operator|.
name|SEHLL_HTRACE_PREFIX
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|prefix
operator|+
name|SpanReceiverHost
operator|.
name|SPAN_RECEIVERS_CONF_SUFFIX
argument_list|,
name|SetSpanReceiver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|prefix
operator|+
name|SamplerBuilder
operator|.
name|SAMPLER_CONF_KEY
argument_list|,
name|AlwaysSampler
operator|.
name|class
operator|.
name|getName
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
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|res
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"ls"
block|,
literal|"cat"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shell
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|SetSpanReceiver
operator|.
name|assertSpanNamesFound
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"help"
block|}
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"-help ls cat"
argument_list|,
name|SetSpanReceiver
operator|.
name|getMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"help"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKVAnnotations
argument_list|()
operator|.
name|get
argument_list|(
literal|"args"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDFSWithInvalidCommmand ()
specifier|public
name|void
name|testDFSWithInvalidCommmand
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|args
index|[
literal|0
index|]
operator|=
literal|"dfs -mkdirs"
expr_stmt|;
specifier|final
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
specifier|final
name|PrintStream
name|oldErr
init|=
name|System
operator|.
name|err
decl_stmt|;
try|try
block|{
name|System
operator|.
name|setErr
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|errorValue
init|=
operator|new
name|String
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"FSShell dfs command did not print the error "
operator|+
literal|"message when invalid command is passed"
argument_list|,
name|errorValue
operator|.
name|contains
argument_list|(
literal|"-mkdirs: Unknown command"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"FSShell dfs command did not print help "
operator|+
literal|"message when invalid command is passed"
argument_list|,
name|errorValue
operator|.
name|contains
argument_list|(
literal|"Usage: hadoop fs [generic options]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|oldErr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

