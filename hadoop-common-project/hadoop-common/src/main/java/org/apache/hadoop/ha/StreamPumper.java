begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_comment
comment|/**  * Class responsible for pumping the streams of the subprocess  * out to log4j. stderr is pumped to WARN level and stdout is  * pumped to INFO level  */
end_comment

begin_class
DECL|class|StreamPumper
class|class
name|StreamPumper
block|{
DECL|enum|StreamType
enum|enum
name|StreamType
block|{
DECL|enumConstant|STDOUT
DECL|enumConstant|STDERR
name|STDOUT
block|,
name|STDERR
block|;   }
DECL|field|log
specifier|private
specifier|final
name|Logger
name|log
decl_stmt|;
DECL|field|thread
specifier|final
name|Thread
name|thread
decl_stmt|;
DECL|field|logPrefix
specifier|final
name|String
name|logPrefix
decl_stmt|;
DECL|field|type
specifier|final
name|StreamPumper
operator|.
name|StreamType
name|type
decl_stmt|;
DECL|field|stream
specifier|private
specifier|final
name|InputStream
name|stream
decl_stmt|;
DECL|field|started
specifier|private
name|boolean
name|started
init|=
literal|false
decl_stmt|;
DECL|method|StreamPumper (final Logger log, final String logPrefix, final InputStream stream, final StreamType type)
name|StreamPumper
parameter_list|(
specifier|final
name|Logger
name|log
parameter_list|,
specifier|final
name|String
name|logPrefix
parameter_list|,
specifier|final
name|InputStream
name|stream
parameter_list|,
specifier|final
name|StreamType
name|type
parameter_list|)
block|{
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
name|this
operator|.
name|logPrefix
operator|=
name|logPrefix
expr_stmt|;
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|thread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|pump
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|ShellCommandFencer
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|logPrefix
operator|+
literal|": Unable to pump output from "
operator|+
name|type
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|logPrefix
operator|+
literal|": StreamPumper for "
operator|+
name|type
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|join ()
name|void
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
assert|assert
name|started
assert|;
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|start ()
name|void
name|start
parameter_list|()
block|{
assert|assert
operator|!
name|started
assert|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|started
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|pump ()
specifier|protected
name|void
name|pump
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStreamReader
name|inputStreamReader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
name|inputStreamReader
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|StreamType
operator|.
name|STDOUT
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|logPrefix
operator|+
literal|": "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
name|logPrefix
operator|+
literal|": "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

