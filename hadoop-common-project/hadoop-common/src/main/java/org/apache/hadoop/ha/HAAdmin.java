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
name|io
operator|.
name|PrintStream
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
name|Map
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
name|cli
operator|.
name|Options
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|CommandLineParser
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|ParseException
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
name|ipc
operator|.
name|RPC
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Tool
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_comment
comment|/**  * A command-line tool for making calls in the HAServiceProtocol.  * For example,. this can be used to force a daemon to standby or active  * mode, or to trigger a health-check.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HAAdmin
specifier|public
specifier|abstract
class|class
name|HAAdmin
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|FORCEFENCE
specifier|private
specifier|static
specifier|final
name|String
name|FORCEFENCE
init|=
literal|"forcefence"
decl_stmt|;
DECL|field|FORCEACTIVE
specifier|private
specifier|static
specifier|final
name|String
name|FORCEACTIVE
init|=
literal|"forceactive"
decl_stmt|;
DECL|field|USAGE
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|UsageInfo
argument_list|>
name|USAGE
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|UsageInfo
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"-transitionToActive"
argument_list|,
operator|new
name|UsageInfo
argument_list|(
literal|"<host:port>"
argument_list|,
literal|"Transitions the daemon into Active state"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"-transitionToStandby"
argument_list|,
operator|new
name|UsageInfo
argument_list|(
literal|"<host:port>"
argument_list|,
literal|"Transitions the daemon into Standby state"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"-failover"
argument_list|,
operator|new
name|UsageInfo
argument_list|(
literal|"[--"
operator|+
name|FORCEFENCE
operator|+
literal|"] [--"
operator|+
name|FORCEACTIVE
operator|+
literal|"]<host:port><host:port>"
argument_list|,
literal|"Failover from the first daemon to the second.\n"
operator|+
literal|"Unconditionally fence services if the "
operator|+
name|FORCEFENCE
operator|+
literal|" option is used.\n"
operator|+
literal|"Try to failover to the target service even if it is not ready if the "
operator|+
name|FORCEACTIVE
operator|+
literal|" option is used."
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"-getServiceState"
argument_list|,
operator|new
name|UsageInfo
argument_list|(
literal|"<host:port>"
argument_list|,
literal|"Returns the state of the daemon"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"-checkHealth"
argument_list|,
operator|new
name|UsageInfo
argument_list|(
literal|"<host:port>"
argument_list|,
literal|"Requests that the daemon perform a health check.\n"
operator|+
literal|"The HAAdmin tool will exit with a non-zero exit code\n"
operator|+
literal|"if the check fails."
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
literal|"-help"
argument_list|,
operator|new
name|UsageInfo
argument_list|(
literal|"<command>"
argument_list|,
literal|"Displays help on the specified command"
argument_list|)
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
comment|/** Output stream for errors, for use in tests */
DECL|field|errOut
name|PrintStream
name|errOut
init|=
name|System
operator|.
name|err
decl_stmt|;
DECL|field|out
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
DECL|method|printUsage (PrintStream errOut)
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|(
name|PrintStream
name|errOut
parameter_list|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"Usage: java HAAdmin"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|UsageInfo
argument_list|>
name|e
range|:
name|USAGE
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|cmd
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|UsageInfo
name|usage
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|errOut
operator|.
name|println
argument_list|(
literal|"    ["
operator|+
name|cmd
operator|+
literal|" "
operator|+
name|usage
operator|.
name|args
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|errOut
operator|.
name|println
argument_list|()
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsage (PrintStream errOut, String cmd)
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|(
name|PrintStream
name|errOut
parameter_list|,
name|String
name|cmd
parameter_list|)
block|{
name|UsageInfo
name|usage
init|=
name|USAGE
operator|.
name|get
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
if|if
condition|(
name|usage
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No usage for cmd "
operator|+
name|cmd
argument_list|)
throw|;
block|}
name|errOut
operator|.
name|println
argument_list|(
literal|"Usage: java HAAdmin ["
operator|+
name|cmd
operator|+
literal|" "
operator|+
name|usage
operator|.
name|args
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|transitionToActive (final String[] argv)
specifier|private
name|int
name|transitionToActive
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServiceFailedException
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"transitionToActive: incorrect number of arguments"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|,
literal|"-transitionToActive"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|HAServiceProtocol
name|proto
init|=
name|getProtocol
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|HAServiceProtocolHelper
operator|.
name|transitionToActive
argument_list|(
name|proto
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|transitionToStandby (final String[] argv)
specifier|private
name|int
name|transitionToStandby
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServiceFailedException
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"transitionToStandby: incorrect number of arguments"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|,
literal|"-transitionToStandby"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|HAServiceProtocol
name|proto
init|=
name|getProtocol
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|HAServiceProtocolHelper
operator|.
name|transitionToStandby
argument_list|(
name|proto
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|failover (final String[] argv)
specifier|private
name|int
name|failover
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServiceFailedException
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|boolean
name|forceFence
init|=
literal|false
decl_stmt|;
name|boolean
name|forceActive
init|=
literal|false
decl_stmt|;
name|Options
name|failoverOpts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
comment|// "-failover" isn't really an option but we need to add
comment|// it to appease CommandLineParser
name|failoverOpts
operator|.
name|addOption
argument_list|(
literal|"failover"
argument_list|,
literal|false
argument_list|,
literal|"failover"
argument_list|)
expr_stmt|;
name|failoverOpts
operator|.
name|addOption
argument_list|(
name|FORCEFENCE
argument_list|,
literal|false
argument_list|,
literal|"force fencing"
argument_list|)
expr_stmt|;
name|failoverOpts
operator|.
name|addOption
argument_list|(
name|FORCEACTIVE
argument_list|,
literal|false
argument_list|,
literal|"force failover"
argument_list|)
expr_stmt|;
name|CommandLineParser
name|parser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|CommandLine
name|cmd
decl_stmt|;
try|try
block|{
name|cmd
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|failoverOpts
argument_list|,
name|argv
argument_list|)
expr_stmt|;
name|forceFence
operator|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|FORCEFENCE
argument_list|)
expr_stmt|;
name|forceActive
operator|=
name|cmd
operator|.
name|hasOption
argument_list|(
name|FORCEACTIVE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|pe
parameter_list|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"failover: incorrect arguments"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|,
literal|"-failover"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|int
name|numOpts
init|=
name|cmd
operator|.
name|getOptions
argument_list|()
operator|==
literal|null
condition|?
literal|0
else|:
name|cmd
operator|.
name|getOptions
argument_list|()
operator|.
name|length
decl_stmt|;
specifier|final
name|String
index|[]
name|args
init|=
name|cmd
operator|.
name|getArgs
argument_list|()
decl_stmt|;
if|if
condition|(
name|numOpts
operator|>
literal|2
operator|||
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"failover: incorrect arguments"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|,
literal|"-failover"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|NodeFencer
name|fencer
decl_stmt|;
try|try
block|{
name|fencer
operator|=
name|NodeFencer
operator|.
name|create
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadFencingConfigurationException
name|bfce
parameter_list|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"failover: incorrect fencing configuration: "
operator|+
name|bfce
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|fencer
operator|==
literal|null
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"failover: no fencer configured"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|InetSocketAddress
name|addr1
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|addr2
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|HAServiceProtocol
name|proto1
init|=
name|getProtocol
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|HAServiceProtocol
name|proto2
init|=
name|getProtocol
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
try|try
block|{
name|FailoverController
operator|.
name|failover
argument_list|(
name|proto1
argument_list|,
name|addr1
argument_list|,
name|proto2
argument_list|,
name|addr2
argument_list|,
name|fencer
argument_list|,
name|forceFence
argument_list|,
name|forceActive
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Failover from "
operator|+
name|args
index|[
literal|0
index|]
operator|+
literal|" to "
operator|+
name|args
index|[
literal|1
index|]
operator|+
literal|" successful"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"Failover failed: "
operator|+
name|ffe
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|checkHealth (final String[] argv)
specifier|private
name|int
name|checkHealth
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServiceFailedException
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"checkHealth: incorrect number of arguments"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|,
literal|"-checkHealth"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|HAServiceProtocol
name|proto
init|=
name|getProtocol
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
try|try
block|{
name|HAServiceProtocolHelper
operator|.
name|monitorHealth
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HealthCheckFailedException
name|e
parameter_list|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"Health check failed: "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|getServiceState (final String[] argv)
specifier|private
name|int
name|getServiceState
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServiceFailedException
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"getServiceState: incorrect number of arguments"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|,
literal|"-getServiceState"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|HAServiceProtocol
name|proto
init|=
name|getProtocol
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|proto
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/**    * Return a proxy to the specified target host:port.    */
DECL|method|getProtocol (String target)
specifier|protected
name|HAServiceProtocol
name|getProtocol
parameter_list|(
name|String
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|target
argument_list|)
decl_stmt|;
return|return
operator|(
name|HAServiceProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|HAServiceProtocol
operator|.
name|class
argument_list|,
name|HAServiceProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|getConf
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|run (String[] argv)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|printUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|cmd
init|=
name|argv
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"Bad command '"
operator|+
name|cmd
operator|+
literal|"': expected command starting with '-'"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
literal|"-transitionToActive"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
return|return
name|transitionToActive
argument_list|(
name|argv
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"-transitionToStandby"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
return|return
name|transitionToStandby
argument_list|(
name|argv
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"-failover"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
return|return
name|failover
argument_list|(
name|argv
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"-getServiceState"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
return|return
name|getServiceState
argument_list|(
name|argv
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"-checkHealth"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
return|return
name|checkHealth
argument_list|(
name|argv
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"-help"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
return|return
name|help
argument_list|(
name|argv
argument_list|)
return|;
block|}
else|else
block|{
name|errOut
operator|.
name|println
argument_list|(
name|cmd
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|+
literal|": Unknown command"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|help (String[] argv)
specifier|private
name|int
name|help
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|printUsage
argument_list|(
name|errOut
argument_list|,
literal|"-help"
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|String
name|cmd
init|=
name|argv
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|cmd
operator|=
literal|"-"
operator|+
name|cmd
expr_stmt|;
block|}
name|UsageInfo
name|usageInfo
init|=
name|USAGE
operator|.
name|get
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
if|if
condition|(
name|usageInfo
operator|==
literal|null
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
name|cmd
operator|+
literal|": Unknown command"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|errOut
operator|.
name|println
argument_list|(
name|cmd
operator|+
literal|" ["
operator|+
name|usageInfo
operator|.
name|args
operator|+
literal|"]: "
operator|+
name|usageInfo
operator|.
name|help
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|class|UsageInfo
specifier|private
specifier|static
class|class
name|UsageInfo
block|{
DECL|field|args
specifier|private
specifier|final
name|String
name|args
decl_stmt|;
DECL|field|help
specifier|private
specifier|final
name|String
name|help
decl_stmt|;
DECL|method|UsageInfo (String args, String help)
specifier|public
name|UsageInfo
parameter_list|(
name|String
name|args
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|help
operator|=
name|help
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

