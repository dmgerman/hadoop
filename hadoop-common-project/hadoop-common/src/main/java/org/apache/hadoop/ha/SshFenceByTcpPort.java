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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|Log
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
name|LogFactory
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
name|Configured
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|ChannelExec
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|JSch
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|JSchException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Session
import|;
end_import

begin_comment
comment|/**  * This fencing implementation sshes to the target node and uses   *<code>fuser</code> to kill the process listening on the service's  * TCP port. This is more accurate than using "jps" since it doesn't   * require parsing, and will work even if there are multiple service  * processes running on the same machine.<p>  * It returns a successful status code if:  *<ul>  *<li><code>fuser</code> indicates it successfully killed a process,<em>or</em>  *<li><code>nc -z</code> indicates that nothing is listening on the target port  *</ul>  *<p>  * This fencing mechanism is configured as following in the fencing method  * list:  *<code>sshfence([[username][:ssh-port]])</code>  * where the optional argument specifies the username and port to use  * with ssh.  *<p>  * In order to achieve passwordless SSH, the operator must also configure  *<code>dfs.ha.fencing.ssh.private-key-files<code> to point to an  * SSH key that has passphrase-less access to the given username and host.  */
end_comment

begin_class
DECL|class|SshFenceByTcpPort
specifier|public
class|class
name|SshFenceByTcpPort
extends|extends
name|Configured
implements|implements
name|FenceMethod
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SshFenceByTcpPort
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONF_CONNECT_TIMEOUT_KEY
specifier|static
specifier|final
name|String
name|CONF_CONNECT_TIMEOUT_KEY
init|=
literal|"dfs.ha.fencing.ssh.connect-timeout"
decl_stmt|;
DECL|field|CONF_CONNECT_TIMEOUT_DEFAULT
specifier|private
specifier|static
specifier|final
name|int
name|CONF_CONNECT_TIMEOUT_DEFAULT
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
DECL|field|CONF_IDENTITIES_KEY
specifier|static
specifier|final
name|String
name|CONF_IDENTITIES_KEY
init|=
literal|"dfs.ha.fencing.ssh.private-key-files"
decl_stmt|;
comment|/**    * Verify that the argument, if given, in the conf is parseable.    */
annotation|@
name|Override
DECL|method|checkArgs (String argStr)
specifier|public
name|void
name|checkArgs
parameter_list|(
name|String
name|argStr
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
if|if
condition|(
name|argStr
operator|!=
literal|null
condition|)
block|{
operator|new
name|Args
argument_list|(
name|argStr
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|tryFence (InetSocketAddress serviceAddr, String argsStr)
specifier|public
name|boolean
name|tryFence
parameter_list|(
name|InetSocketAddress
name|serviceAddr
parameter_list|,
name|String
name|argsStr
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|Args
name|args
init|=
operator|new
name|Args
argument_list|(
name|argsStr
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|serviceAddr
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|Session
name|session
decl_stmt|;
try|try
block|{
name|session
operator|=
name|createSession
argument_list|(
name|serviceAddr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JSchException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to create SSH session"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to "
operator|+
name|host
operator|+
literal|"..."
argument_list|)
expr_stmt|;
try|try
block|{
name|session
operator|.
name|connect
argument_list|(
name|getSshConnectTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JSchException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to connect to "
operator|+
name|host
operator|+
literal|" as user "
operator|+
name|args
operator|.
name|user
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Connected to "
operator|+
name|host
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|doFence
argument_list|(
name|session
argument_list|,
name|serviceAddr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JSchException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to achieve fencing on remote host"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|session
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createSession (String host, Args args)
specifier|private
name|Session
name|createSession
parameter_list|(
name|String
name|host
parameter_list|,
name|Args
name|args
parameter_list|)
throws|throws
name|JSchException
block|{
name|JSch
name|jsch
init|=
operator|new
name|JSch
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|keyFile
range|:
name|getKeyFiles
argument_list|()
control|)
block|{
name|jsch
operator|.
name|addIdentity
argument_list|(
name|keyFile
argument_list|)
expr_stmt|;
block|}
name|JSch
operator|.
name|setLogger
argument_list|(
operator|new
name|LogAdapter
argument_list|()
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|jsch
operator|.
name|getSession
argument_list|(
name|args
operator|.
name|user
argument_list|,
name|host
argument_list|,
name|args
operator|.
name|sshPort
argument_list|)
decl_stmt|;
name|session
operator|.
name|setConfig
argument_list|(
literal|"StrictHostKeyChecking"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
return|return
name|session
return|;
block|}
DECL|method|doFence (Session session, InetSocketAddress serviceAddr)
specifier|private
name|boolean
name|doFence
parameter_list|(
name|Session
name|session
parameter_list|,
name|InetSocketAddress
name|serviceAddr
parameter_list|)
throws|throws
name|JSchException
block|{
name|int
name|port
init|=
name|serviceAddr
operator|.
name|getPort
argument_list|()
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Looking for process running on port "
operator|+
name|port
argument_list|)
expr_stmt|;
name|int
name|rc
init|=
name|execCommand
argument_list|(
name|session
argument_list|,
literal|"PATH=$PATH:/sbin:/usr/sbin fuser -v -k -n tcp "
operator|+
name|port
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully killed process that was "
operator|+
literal|"listening on port "
operator|+
name|port
argument_list|)
expr_stmt|;
comment|// exit code 0 indicates the process was successfully killed.
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|rc
operator|==
literal|1
condition|)
block|{
comment|// exit code 1 indicates either that the process was not running
comment|// or that fuser didn't have root privileges in order to find it
comment|// (eg running as a different user)
name|LOG
operator|.
name|info
argument_list|(
literal|"Indeterminate response from trying to kill service. "
operator|+
literal|"Verifying whether it is running using nc..."
argument_list|)
expr_stmt|;
name|rc
operator|=
name|execCommand
argument_list|(
name|session
argument_list|,
literal|"nc -z "
operator|+
name|serviceAddr
operator|.
name|getHostName
argument_list|()
operator|+
literal|" "
operator|+
name|serviceAddr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|rc
operator|==
literal|0
condition|)
block|{
comment|// the service is still listening - we are unable to fence
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to fence - it is running but we cannot kill it"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Verified that the service is down."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
comment|// other
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"rc: "
operator|+
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
operator|==
literal|0
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted while trying to fence via ssh"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown failure while trying to fence via ssh"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Execute a command through the ssh session, pumping its    * stderr and stdout to our own logs.    */
DECL|method|execCommand (Session session, String cmd)
specifier|private
name|int
name|execCommand
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|cmd
parameter_list|)
throws|throws
name|JSchException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running cmd: "
operator|+
name|cmd
argument_list|)
expr_stmt|;
name|ChannelExec
name|exec
init|=
literal|null
decl_stmt|;
try|try
block|{
name|exec
operator|=
operator|(
name|ChannelExec
operator|)
name|session
operator|.
name|openChannel
argument_list|(
literal|"exec"
argument_list|)
expr_stmt|;
name|exec
operator|.
name|setCommand
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|exec
operator|.
name|setInputStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|exec
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|// Pump stdout of the command to our WARN logs
name|StreamPumper
name|outPumper
init|=
operator|new
name|StreamPumper
argument_list|(
name|LOG
argument_list|,
name|cmd
operator|+
literal|" via ssh"
argument_list|,
name|exec
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|StreamPumper
operator|.
name|StreamType
operator|.
name|STDOUT
argument_list|)
decl_stmt|;
name|outPumper
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Pump stderr of the command to our WARN logs
name|StreamPumper
name|errPumper
init|=
operator|new
name|StreamPumper
argument_list|(
name|LOG
argument_list|,
name|cmd
operator|+
literal|" via ssh"
argument_list|,
name|exec
operator|.
name|getErrStream
argument_list|()
argument_list|,
name|StreamPumper
operator|.
name|StreamType
operator|.
name|STDERR
argument_list|)
decl_stmt|;
name|errPumper
operator|.
name|start
argument_list|()
expr_stmt|;
name|outPumper
operator|.
name|join
argument_list|()
expr_stmt|;
name|errPumper
operator|.
name|join
argument_list|()
expr_stmt|;
return|return
name|exec
operator|.
name|getExitStatus
argument_list|()
return|;
block|}
finally|finally
block|{
name|cleanup
argument_list|(
name|exec
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cleanup (ChannelExec exec)
specifier|private
specifier|static
name|void
name|cleanup
parameter_list|(
name|ChannelExec
name|exec
parameter_list|)
block|{
if|if
condition|(
name|exec
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|exec
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't disconnect ssh channel"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSshConnectTimeout ()
specifier|private
name|int
name|getSshConnectTimeout
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|CONF_CONNECT_TIMEOUT_KEY
argument_list|,
name|CONF_CONNECT_TIMEOUT_DEFAULT
argument_list|)
return|;
block|}
DECL|method|getKeyFiles ()
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|getKeyFiles
parameter_list|()
block|{
return|return
name|getConf
argument_list|()
operator|.
name|getTrimmedStringCollection
argument_list|(
name|CONF_IDENTITIES_KEY
argument_list|)
return|;
block|}
comment|/**    * Container for the parsed arg line for this fencing method.    */
annotation|@
name|VisibleForTesting
DECL|class|Args
specifier|static
class|class
name|Args
block|{
DECL|field|USER_PORT_RE
specifier|private
specifier|static
specifier|final
name|Pattern
name|USER_PORT_RE
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([^:]+?)?(?:\\:(\\d+))?"
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_SSH_PORT
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SSH_PORT
init|=
literal|22
decl_stmt|;
DECL|field|user
name|String
name|user
decl_stmt|;
DECL|field|sshPort
name|int
name|sshPort
decl_stmt|;
DECL|method|Args (String arg)
specifier|public
name|Args
parameter_list|(
name|String
name|arg
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|user
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
expr_stmt|;
name|sshPort
operator|=
name|DEFAULT_SSH_PORT
expr_stmt|;
comment|// Parse optional user and ssh port
if|if
condition|(
name|arg
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|Matcher
name|m
init|=
name|USER_PORT_RE
operator|.
name|matcher
argument_list|(
name|arg
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadFencingConfigurationException
argument_list|(
literal|"Unable to parse user and SSH port: "
operator|+
name|arg
argument_list|)
throw|;
block|}
if|if
condition|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|user
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|sshPort
operator|=
name|parseConfiggedPort
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseConfiggedPort (String portStr)
specifier|private
name|Integer
name|parseConfiggedPort
parameter_list|(
name|String
name|portStr
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
try|try
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|portStr
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
throw|throw
operator|new
name|BadFencingConfigurationException
argument_list|(
literal|"Port number '"
operator|+
name|portStr
operator|+
literal|"' invalid"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Adapter from JSch's logger interface to our log4j    */
DECL|class|LogAdapter
specifier|private
specifier|static
class|class
name|LogAdapter
implements|implements
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SshFenceByTcpPort
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".jsch"
argument_list|)
decl_stmt|;
DECL|method|isEnabled (int level)
specifier|public
name|boolean
name|isEnabled
parameter_list|(
name|int
name|level
parameter_list|)
block|{
switch|switch
condition|(
name|level
condition|)
block|{
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|DEBUG
case|:
return|return
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
return|;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|INFO
case|:
return|return
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
return|;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|WARN
case|:
return|return
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
return|;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|ERROR
case|:
return|return
name|LOG
operator|.
name|isErrorEnabled
argument_list|()
return|;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|FATAL
case|:
return|return
name|LOG
operator|.
name|isFatalEnabled
argument_list|()
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
DECL|method|log (int level, String message)
specifier|public
name|void
name|log
parameter_list|(
name|int
name|level
parameter_list|,
name|String
name|message
parameter_list|)
block|{
switch|switch
condition|(
name|level
condition|)
block|{
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|DEBUG
case|:
name|LOG
operator|.
name|debug
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|INFO
case|:
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|WARN
case|:
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|ERROR
case|:
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
case|case
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|Logger
operator|.
name|FATAL
case|:
name|LOG
operator|.
name|fatal
argument_list|(
name|message
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

