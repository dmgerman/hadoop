begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
package|;
end_package

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
name|OutputStream
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
name|Charset
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|annotations
operator|.
name|OnWebSocketClose
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|annotations
operator|.
name|OnWebSocketConnect
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|annotations
operator|.
name|OnWebSocketMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|websocket
operator|.
name|api
operator|.
name|annotations
operator|.
name|WebSocket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jline
operator|.
name|terminal
operator|.
name|Terminal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jline
operator|.
name|terminal
operator|.
name|TerminalBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jline
operator|.
name|reader
operator|.
name|LineReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jline
operator|.
name|reader
operator|.
name|LineReaderBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jline
operator|.
name|reader
operator|.
name|impl
operator|.
name|LineReaderImpl
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

begin_comment
comment|/**  * Web socket for establishing interactive command shell connection through  * Node Manage to container executor.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|,
literal|"YARN"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|WebSocket
DECL|class|ContainerShellWebSocket
specifier|public
class|class
name|ContainerShellWebSocket
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
name|ContainerShellWebSocket
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mySession
specifier|private
name|Session
name|mySession
decl_stmt|;
DECL|field|terminal
specifier|private
name|Terminal
name|terminal
decl_stmt|;
DECL|field|reader
specifier|private
name|LineReader
name|reader
decl_stmt|;
DECL|field|sttySet
specifier|private
name|boolean
name|sttySet
init|=
literal|false
decl_stmt|;
annotation|@
name|OnWebSocketMessage
DECL|method|onText (Session session, String message)
specifier|public
name|void
name|onText
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|sttySet
condition|)
block|{
name|session
operator|.
name|getRemote
argument_list|()
operator|.
name|sendString
argument_list|(
literal|"stty -echo"
argument_list|)
expr_stmt|;
name|session
operator|.
name|getRemote
argument_list|()
operator|.
name|sendString
argument_list|(
literal|"\r"
argument_list|)
expr_stmt|;
name|session
operator|.
name|getRemote
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|sttySet
operator|=
literal|true
expr_stmt|;
block|}
name|terminal
operator|.
name|output
argument_list|()
operator|.
name|write
argument_list|(
name|message
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|terminal
operator|.
name|output
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|OnWebSocketConnect
DECL|method|onConnect (Session s)
specifier|public
name|void
name|onConnect
parameter_list|(
name|Session
name|s
parameter_list|)
block|{
name|initTerminal
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|s
operator|.
name|getRemoteAddress
argument_list|()
operator|.
name|getHostString
argument_list|()
operator|+
literal|" connected!"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|OnWebSocketClose
DECL|method|onClose (Session session, int status, String reason)
specifier|public
name|void
name|onClose
parameter_list|(
name|Session
name|session
parameter_list|,
name|int
name|status
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
if|if
condition|(
name|status
operator|==
literal|1000
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|session
operator|.
name|getRemoteAddress
argument_list|()
operator|.
name|getHostString
argument_list|()
operator|+
literal|" closed, status: "
operator|+
name|status
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|session
operator|.
name|getRemoteAddress
argument_list|()
operator|.
name|getHostString
argument_list|()
operator|+
literal|" closed, status: "
operator|+
name|status
operator|+
literal|" Reason: "
operator|+
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Reader
name|consoleReader
init|=
operator|new
name|Reader
argument_list|()
decl_stmt|;
name|Thread
name|inputThread
init|=
operator|new
name|Thread
argument_list|(
name|consoleReader
argument_list|,
literal|"consoleReader"
argument_list|)
decl_stmt|;
name|inputThread
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
name|mySession
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|mySession
operator|.
name|getRemote
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|consoleReader
operator|.
name|hasData
argument_list|()
condition|)
block|{
name|String
name|message
init|=
name|consoleReader
operator|.
name|read
argument_list|()
decl_stmt|;
name|mySession
operator|.
name|getRemote
argument_list|()
operator|.
name|sendString
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|mySession
operator|.
name|getRemote
argument_list|()
operator|.
name|sendString
argument_list|(
literal|"\r"
argument_list|)
expr_stmt|;
block|}
name|String
name|message
init|=
literal|"1{}"
decl_stmt|;
name|mySession
operator|.
name|getRemote
argument_list|()
operator|.
name|sendString
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|mySession
operator|.
name|getRemote
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|inputThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
try|try
block|{
name|mySession
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error closing connection: "
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|initTerminal (final Session session)
specifier|protected
name|void
name|initTerminal
parameter_list|(
specifier|final
name|Session
name|session
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|mySession
operator|=
name|session
expr_stmt|;
try|try
block|{
name|terminal
operator|=
name|TerminalBuilder
operator|.
name|builder
argument_list|()
operator|.
name|system
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|t
parameter_list|)
block|{
name|terminal
operator|=
name|TerminalBuilder
operator|.
name|builder
argument_list|()
operator|.
name|system
argument_list|(
literal|false
argument_list|)
operator|.
name|streams
argument_list|(
name|System
operator|.
name|in
argument_list|,
operator|(
name|OutputStream
operator|)
name|System
operator|.
name|out
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|reader
operator|=
name|LineReaderBuilder
operator|.
name|builder
argument_list|()
operator|.
name|terminal
argument_list|(
name|terminal
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|session
operator|.
name|close
argument_list|(
literal|1002
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Reader
class|class
name|Reader
implements|implements
name|Runnable
block|{
DECL|field|sb
specifier|private
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|hasData
specifier|private
name|boolean
name|hasData
init|=
literal|false
decl_stmt|;
DECL|method|read ()
specifier|public
name|String
name|read
parameter_list|()
block|{
try|try
block|{
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|hasData
operator|=
literal|false
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|hasData ()
specifier|public
name|boolean
name|hasData
parameter_list|()
block|{
return|return
name|hasData
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|c
init|=
operator|(
operator|(
name|LineReaderImpl
operator|)
name|reader
operator|)
operator|.
name|readCharacter
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|10
operator|||
name|c
operator|==
literal|13
condition|)
block|{
name|hasData
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
name|sb
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|Character
operator|.
name|toChars
argument_list|(
name|c
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

