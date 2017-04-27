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
name|BufferedWriter
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
name|FileOutputStream
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
name|OutputStreamWriter
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Fencer method that uses PowerShell to remotely connect to a machine and kill  * the required process. This only works in Windows.  *  * The argument passed to this fencer should be a unique string in the  * "CommandLine" attribute for the "java.exe" process. For example, the full  * path for the Namenode: "org.apache.hadoop.hdfs.server.namenode.NameNode".  * The administrator can also shorten the name to "Namenode" if it's unique.  */
end_comment

begin_class
DECL|class|PowerShellFencer
specifier|public
class|class
name|PowerShellFencer
extends|extends
name|Configured
implements|implements
name|FenceMethod
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PowerShellFencer
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"The parameter for the PowerShell fencer is "
operator|+
name|argStr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tryFence (HAServiceTarget target, String argsStr)
specifier|public
name|boolean
name|tryFence
parameter_list|(
name|HAServiceTarget
name|target
parameter_list|,
name|String
name|argsStr
parameter_list|)
throws|throws
name|BadFencingConfigurationException
block|{
name|String
name|processName
init|=
name|argsStr
decl_stmt|;
name|InetSocketAddress
name|serviceAddr
init|=
name|target
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|String
name|hostname
init|=
name|serviceAddr
operator|.
name|getHostName
argument_list|()
decl_stmt|;
comment|// Use PowerShell to kill a remote process
name|String
name|ps1script
init|=
name|buildPSScript
argument_list|(
name|processName
argument_list|,
name|hostname
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps1script
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot build PowerShell script"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Execute PowerShell script
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing "
operator|+
name|ps1script
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|(
literal|"powershell.exe"
argument_list|,
name|ps1script
argument_list|)
decl_stmt|;
name|Process
name|p
init|=
literal|null
decl_stmt|;
try|try
block|{
name|p
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
name|p
operator|.
name|getOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Unable to execute "
operator|+
name|ps1script
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Pump logs to stderr
name|StreamPumper
name|errPumper
init|=
operator|new
name|StreamPumper
argument_list|(
name|LOG
argument_list|,
literal|"fencer"
argument_list|,
name|p
operator|.
name|getErrorStream
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
name|StreamPumper
name|outPumper
init|=
operator|new
name|StreamPumper
argument_list|(
name|LOG
argument_list|,
literal|"fencer"
argument_list|,
name|p
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
comment|// Waiting for the process to finish
name|int
name|rc
init|=
literal|0
decl_stmt|;
try|try
block|{
name|rc
operator|=
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
name|errPumper
operator|.
name|join
argument_list|()
expr_stmt|;
name|outPumper
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted while waiting for fencing command: "
operator|+
name|ps1script
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|rc
operator|==
literal|0
return|;
block|}
comment|/**    * Build a PowerShell script to kill a java.exe process in a remote machine.    *    * @param processName Name of the process to kill. This is an attribute in    *                    CommandLine.    * @param host Host where the process is.    * @return Path of the PowerShell script.    */
DECL|method|buildPSScript (final String processName, final String host)
specifier|private
name|String
name|buildPSScript
parameter_list|(
specifier|final
name|String
name|processName
parameter_list|,
specifier|final
name|String
name|host
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Building PowerShell script to kill "
operator|+
name|processName
operator|+
literal|" at "
operator|+
name|host
argument_list|)
expr_stmt|;
name|String
name|ps1script
init|=
literal|null
decl_stmt|;
name|BufferedWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|file
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"temp-fence-command"
argument_list|,
literal|".ps1"
argument_list|)
decl_stmt|;
name|file
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|osw
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|fos
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|BufferedWriter
argument_list|(
name|osw
argument_list|)
expr_stmt|;
comment|// Filter to identify the Namenode process
name|String
name|filter
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|" and "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Name LIKE '%java.exe%'"
block|,
literal|"CommandLine LIKE '%"
operator|+
name|processName
operator|+
literal|"%'"
block|}
argument_list|)
decl_stmt|;
comment|// Identify the process
name|String
name|cmd
init|=
literal|"Get-WmiObject Win32_Process"
decl_stmt|;
name|cmd
operator|+=
literal|" -Filter \""
operator|+
name|filter
operator|+
literal|"\""
expr_stmt|;
comment|// Remote location
name|cmd
operator|+=
literal|" -Computer "
operator|+
name|host
expr_stmt|;
comment|// Kill it
name|cmd
operator|+=
literal|" |% { $_.Terminate() }"
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"PowerShell command: "
operator|+
name|cmd
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|ps1script
operator|=
name|file
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot create PowerShell script"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot close PowerShell script"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ps1script
return|;
block|}
block|}
end_class

end_unit

