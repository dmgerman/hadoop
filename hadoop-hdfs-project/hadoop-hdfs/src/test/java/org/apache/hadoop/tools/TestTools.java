begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|PipedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PipedOutputStream
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|DFSAdmin
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
name|hdfs
operator|.
name|tools
operator|.
name|DelegationTokenFetcher
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
name|hdfs
operator|.
name|tools
operator|.
name|JMXGet
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
name|ExitUtil
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
name|hadoop
operator|.
name|util
operator|.
name|ExitUtil
operator|.
name|ExitException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|io
operator|.
name|ByteStreams
import|;
end_import

begin_class
DECL|class|TestTools
specifier|public
class|class
name|TestTools
block|{
DECL|field|PIPE_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|PIPE_BUFFER_SIZE
init|=
literal|1024
operator|*
literal|5
decl_stmt|;
DECL|field|INVALID_OPTION
specifier|private
specifier|final
specifier|static
name|String
name|INVALID_OPTION
init|=
literal|"-invalidOption"
decl_stmt|;
DECL|field|OPTIONS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|OPTIONS
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|before ()
specifier|public
specifier|static
name|void
name|before
parameter_list|()
block|{
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
name|OPTIONS
index|[
literal|1
index|]
operator|=
name|INVALID_OPTION
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenFetcherPrintUsage ()
specifier|public
name|void
name|testDelegationTokenFetcherPrintUsage
parameter_list|()
block|{
name|String
name|pattern
init|=
literal|"Options:"
decl_stmt|;
name|checkOutput
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|}
argument_list|,
name|pattern
argument_list|,
name|System
operator|.
name|out
argument_list|,
name|DelegationTokenFetcher
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenFetcherErrorOption ()
specifier|public
name|void
name|testDelegationTokenFetcherErrorOption
parameter_list|()
block|{
name|String
name|pattern
init|=
literal|"ERROR: Only specify cancel, renew or print."
decl_stmt|;
name|checkOutput
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-cancel"
block|,
literal|"-renew"
block|}
argument_list|,
name|pattern
argument_list|,
name|System
operator|.
name|err
argument_list|,
name|DelegationTokenFetcher
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJMXToolHelp ()
specifier|public
name|void
name|testJMXToolHelp
parameter_list|()
block|{
name|String
name|pattern
init|=
literal|"usage: jmxget options are:"
decl_stmt|;
name|checkOutput
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-help"
block|}
argument_list|,
name|pattern
argument_list|,
name|System
operator|.
name|out
argument_list|,
name|JMXGet
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJMXToolAdditionParameter ()
specifier|public
name|void
name|testJMXToolAdditionParameter
parameter_list|()
block|{
name|String
name|pattern
init|=
literal|"key = -addition"
decl_stmt|;
name|checkOutput
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-service=NameNode"
block|,
literal|"-server=localhost"
block|,
literal|"-addition"
block|}
argument_list|,
name|pattern
argument_list|,
name|System
operator|.
name|err
argument_list|,
name|JMXGet
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDFSAdminInvalidUsageHelp ()
specifier|public
name|void
name|testDFSAdminInvalidUsageHelp
parameter_list|()
block|{
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|args
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"-report"
argument_list|,
literal|"-saveNamespace"
argument_list|,
literal|"-rollEdits"
argument_list|,
literal|"-restoreFailedStorage"
argument_list|,
literal|"-refreshNodes"
argument_list|,
literal|"-finalizeUpgrade"
argument_list|,
literal|"-metasave"
argument_list|,
literal|"-refreshUserToGroupsMappings"
argument_list|,
literal|"-printTopology"
argument_list|,
literal|"-refreshNamenodes"
argument_list|,
literal|"-deleteBlockPool"
argument_list|,
literal|"-setBalancerBandwidth"
argument_list|,
literal|"-fetchImage"
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
name|assertTrue
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|DFSAdmin
argument_list|()
argument_list|,
name|fillArgs
argument_list|(
name|arg
argument_list|)
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|DFSAdmin
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-help"
block|,
literal|"-some"
block|}
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testDFSAdminHelp error"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|String
name|pattern
init|=
literal|"Usage: java DFSAdmin"
decl_stmt|;
name|checkOutput
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-cancel"
block|,
literal|"-renew"
block|}
argument_list|,
name|pattern
argument_list|,
name|System
operator|.
name|err
argument_list|,
name|DFSAdmin
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|fillArgs (String arg)
specifier|private
specifier|static
name|String
index|[]
name|fillArgs
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|OPTIONS
index|[
literal|0
index|]
operator|=
name|arg
expr_stmt|;
return|return
name|OPTIONS
return|;
block|}
DECL|method|checkOutput (String[] args, String pattern, PrintStream out, Class<?> clazz)
specifier|private
name|void
name|checkOutput
parameter_list|(
name|String
index|[]
name|args
parameter_list|,
name|String
name|pattern
parameter_list|,
name|PrintStream
name|out
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|ByteArrayOutputStream
name|outBytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|PipedOutputStream
name|pipeOut
init|=
operator|new
name|PipedOutputStream
argument_list|()
decl_stmt|;
name|PipedInputStream
name|pipeIn
init|=
operator|new
name|PipedInputStream
argument_list|(
name|pipeOut
argument_list|,
name|PIPE_BUFFER_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|out
operator|==
name|System
operator|.
name|out
condition|)
block|{
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|pipeOut
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|out
operator|==
name|System
operator|.
name|err
condition|)
block|{
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|pipeOut
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clazz
operator|==
name|DelegationTokenFetcher
operator|.
name|class
condition|)
block|{
name|expectDelegationTokenFetcherExit
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|JMXGet
operator|.
name|class
condition|)
block|{
name|expectJMXGetExit
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clazz
operator|==
name|DFSAdmin
operator|.
name|class
condition|)
block|{
name|expectDfsAdminPrint
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
name|pipeOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|ByteStreams
operator|.
name|copy
argument_list|(
name|pipeIn
argument_list|,
name|outBytes
argument_list|)
expr_stmt|;
name|pipeIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|outBytes
operator|.
name|toByteArray
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|pattern
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
name|fail
argument_list|(
literal|"checkOutput error "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|expectDfsAdminPrint (String[] args)
specifier|private
name|void
name|expectDfsAdminPrint
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|DFSAdmin
argument_list|()
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
name|fail
argument_list|(
literal|"expectDelegationTokenFetcherExit ex error "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|expectDelegationTokenFetcherExit (String[] args)
specifier|private
specifier|static
name|void
name|expectDelegationTokenFetcherExit
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|DelegationTokenFetcher
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should call exit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitException
name|e
parameter_list|)
block|{
name|ExitUtil
operator|.
name|resetFirstExitException
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"expectDelegationTokenFetcherExit ex error "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|expectJMXGetExit (String[] args)
specifier|private
specifier|static
name|void
name|expectJMXGetExit
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|JMXGet
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should call exit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitException
name|e
parameter_list|)
block|{
name|ExitUtil
operator|.
name|resetFirstExitException
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"expectJMXGetExit ex error "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

