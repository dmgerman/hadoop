begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.log
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|log
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
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
name|http
operator|.
name|HttpServer2
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
name|net
operator|.
name|NetUtils
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|logging
operator|.
name|*
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
name|logging
operator|.
name|impl
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|*
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

begin_class
DECL|class|TestLogLevel
specifier|public
class|class
name|TestLogLevel
extends|extends
name|TestCase
block|{
DECL|field|out
specifier|static
specifier|final
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
DECL|method|testDynamicLogLevel ()
specifier|public
name|void
name|testDynamicLogLevel
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|logName
init|=
name|TestLogLevel
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Log
name|testlog
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|logName
argument_list|)
decl_stmt|;
comment|//only test Log4JLogger
if|if
condition|(
name|testlog
operator|instanceof
name|Log4JLogger
condition|)
block|{
name|Logger
name|log
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|testlog
operator|)
operator|.
name|getLogger
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"log.debug1"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"log.info1"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"log.error1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"Get default Log Level which shouldn't be ERROR."
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|,
name|log
operator|.
name|getEffectiveLevel
argument_list|()
argument_list|)
expr_stmt|;
name|HttpServer2
name|server
init|=
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|".."
argument_list|)
operator|.
name|addEndpoint
argument_list|(
operator|new
name|URI
argument_list|(
literal|"http://localhost:0"
argument_list|)
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|authority
init|=
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|server
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
comment|//servlet
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://"
operator|+
name|authority
operator|+
literal|"/logLevel?log="
operator|+
name|logName
operator|+
literal|"&level="
operator|+
name|Level
operator|.
name|ERROR
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"*** Connecting to "
operator|+
name|url
argument_list|)
expr_stmt|;
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
init|;
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
control|)
empty_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"log.debug2"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"log.info2"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"log.error2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Try setting log level: ERROR from servlet."
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|,
name|log
operator|.
name|getEffectiveLevel
argument_list|()
argument_list|)
expr_stmt|;
comment|//command line
name|String
index|[]
name|args
init|=
block|{
literal|"-setlevel"
block|,
name|authority
block|,
name|logName
block|,
name|Level
operator|.
name|DEBUG
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
name|LogLevel
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"log.debug3"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"log.info3"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"log.error3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Try setting log level: DEBUG via command line"
argument_list|,
name|Level
operator|.
name|DEBUG
argument_list|,
name|log
operator|.
name|getEffectiveLevel
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test mixed upper case and lower case in level string.
name|String
index|[]
name|args2
init|=
block|{
literal|"-setlevel"
block|,
name|authority
block|,
name|logName
block|,
literal|"Info"
block|}
decl_stmt|;
name|LogLevel
operator|.
name|main
argument_list|(
name|args2
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"log.debug4"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"log.info4"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"log.error4"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Try setting log level: Info via command line."
argument_list|,
name|Level
operator|.
name|INFO
argument_list|,
name|log
operator|.
name|getEffectiveLevel
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test "Error" instead of "ERROR" should work for servlet
name|URL
name|newUrl
init|=
operator|new
name|URL
argument_list|(
literal|"http://"
operator|+
name|authority
operator|+
literal|"/logLevel?log="
operator|+
name|logName
operator|+
literal|"&level="
operator|+
literal|"Error"
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"*** Connecting to "
operator|+
name|newUrl
argument_list|)
expr_stmt|;
name|connection
operator|=
name|newUrl
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|in2
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
init|;
operator|(
name|line
operator|=
name|in2
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
control|)
empty_stmt|;
name|in2
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"log.debug5"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"log.info5"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"log.error5"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Try setting log level: Error via servlet."
argument_list|,
name|Level
operator|.
name|ERROR
argument_list|,
name|log
operator|.
name|getEffectiveLevel
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
name|testlog
operator|.
name|getClass
argument_list|()
operator|+
literal|" not tested."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

