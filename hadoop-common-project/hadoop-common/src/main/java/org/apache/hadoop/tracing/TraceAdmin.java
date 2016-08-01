begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tracing
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tracing
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
name|LinkedList
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|ProtobufRpcEngine
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
name|security
operator|.
name|UserGroupInformation
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
name|tools
operator|.
name|TableListing
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
comment|/**  * A command-line tool for viewing and modifying tracing settings.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TraceAdmin
specifier|public
class|class
name|TraceAdmin
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|proxy
specifier|private
name|TraceAdminProtocolPB
name|proxy
decl_stmt|;
DECL|field|remote
specifier|private
name|TraceAdminProtocolTranslatorPB
name|remote
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
name|TraceAdmin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|usage ()
specifier|private
name|void
name|usage
parameter_list|()
block|{
name|PrintStream
name|err
init|=
name|System
operator|.
name|err
decl_stmt|;
name|err
operator|.
name|print
argument_list|(
literal|"Hadoop tracing configuration commands:\n"
operator|+
literal|"  -add [-class classname] [-Ckey=value] [-Ckey2=value2] ...\n"
operator|+
literal|"    Add a span receiver with the provided class name.  Configuration\n"
operator|+
literal|"    keys for the span receiver can be specified with the -C options.\n"
operator|+
literal|"    The span receiver will also inherit whatever configuration keys\n"
operator|+
literal|"    exist in the daemon's configuration.\n"
operator|+
literal|"  -help: Print this help message.\n"
operator|+
literal|"  -host [hostname:port]\n"
operator|+
literal|"    Specify the hostname and port of the daemon to examine.\n"
operator|+
literal|"    Required for all commands.\n"
operator|+
literal|"  -list: List the current span receivers.\n"
operator|+
literal|"  -remove [id]\n"
operator|+
literal|"    Remove the span receiver with the specified id.  Use -list to\n"
operator|+
literal|"    find the id of each receiver.\n"
operator|+
literal|"  -principal: If the daemon is Kerberized, specify the service\n"
operator|+
literal|"    principal name."
argument_list|)
expr_stmt|;
block|}
DECL|method|listSpanReceivers (List<String> args)
specifier|private
name|int
name|listSpanReceivers
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanReceiverInfo
name|infos
index|[]
init|=
name|remote
operator|.
name|listSpanReceivers
argument_list|()
decl_stmt|;
if|if
condition|(
name|infos
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"[no span receivers found]"
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|TableListing
name|listing
init|=
operator|new
name|TableListing
operator|.
name|Builder
argument_list|()
operator|.
name|addField
argument_list|(
literal|"ID"
argument_list|)
operator|.
name|addField
argument_list|(
literal|"CLASS"
argument_list|)
operator|.
name|showHeaders
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|SpanReceiverInfo
name|info
range|:
name|infos
control|)
block|{
name|listing
operator|.
name|addRow
argument_list|(
literal|""
operator|+
name|info
operator|.
name|getId
argument_list|()
argument_list|,
name|info
operator|.
name|getClassName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|listing
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|field|CONFIG_PREFIX
specifier|private
specifier|final
specifier|static
name|String
name|CONFIG_PREFIX
init|=
literal|"-C"
decl_stmt|;
DECL|method|addSpanReceiver (List<String> args)
specifier|private
name|int
name|addSpanReceiver
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|className
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-class"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify the classname with -class."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|ByteArrayOutputStream
name|configStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|configsOut
init|=
operator|new
name|PrintStream
argument_list|(
name|configStream
argument_list|,
literal|false
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|SpanReceiverInfoBuilder
name|factory
init|=
operator|new
name|SpanReceiverInfoBuilder
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|String
name|str
init|=
name|args
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|str
operator|.
name|startsWith
argument_list|(
name|CONFIG_PREFIX
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't understand argument: "
operator|+
name|str
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|str
operator|=
name|str
operator|.
name|substring
argument_list|(
name|CONFIG_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|equalsIndex
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|equalsIndex
operator|<
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Can't parse configuration argument "
operator|+
name|str
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Arguments must be in the form key=value"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|String
name|key
init|=
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|equalsIndex
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|str
operator|.
name|substring
argument_list|(
name|equalsIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
name|factory
operator|.
name|addConfigurationPair
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|configsOut
operator|.
name|print
argument_list|(
name|prefix
operator|+
name|key
operator|+
literal|" = "
operator|+
name|value
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|", "
expr_stmt|;
block|}
name|String
name|configStreamStr
init|=
name|configStream
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|id
init|=
name|remote
operator|.
name|addSpanReceiver
argument_list|(
name|factory
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Added trace span receiver "
operator|+
name|id
operator|+
literal|" with configuration "
operator|+
name|configStreamStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"addSpanReceiver error with configuration "
operator|+
name|configStreamStr
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|removeSpanReceiver (List<String> args)
specifier|private
name|int
name|removeSpanReceiver
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|indexStr
init|=
name|StringUtils
operator|.
name|popFirstNonOption
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|long
name|id
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|id
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|indexStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to parse ID string "
operator|+
name|indexStr
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|remote
operator|.
name|removeSpanReceiver
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Removed trace span receiver "
operator|+
name|id
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|run (String argv[])
specifier|public
name|int
name|run
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|argv
control|)
block|{
name|args
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|StringUtils
operator|.
name|popOption
argument_list|(
literal|"-h"
argument_list|,
name|args
argument_list|)
operator|||
name|StringUtils
operator|.
name|popOption
argument_list|(
literal|"-help"
argument_list|,
name|args
argument_list|)
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
name|String
name|hostPort
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-host"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostPort
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a host with -host."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|<
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify an operation."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|String
name|servicePrincipal
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-principal"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|servicePrincipal
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set service principal: {}"
argument_list|,
name|servicePrincipal
argument_list|)
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
argument_list|,
name|servicePrincipal
argument_list|)
expr_stmt|;
block|}
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|TraceAdminProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|address
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|hostPort
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|xface
init|=
name|TraceAdminProtocolPB
operator|.
name|class
decl_stmt|;
name|proxy
operator|=
operator|(
name|TraceAdminProtocolPB
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|xface
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|xface
argument_list|)
argument_list|,
name|address
argument_list|,
name|ugi
argument_list|,
name|getConf
argument_list|()
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|remote
operator|=
operator|new
name|TraceAdminProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"-list"
argument_list|)
condition|)
block|{
return|return
name|listSpanReceivers
argument_list|(
name|args
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|args
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"-add"
argument_list|)
condition|)
block|{
return|return
name|addSpanReceiver
argument_list|(
name|args
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|args
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"-remove"
argument_list|)
condition|)
block|{
return|return
name|removeSpanReceiver
argument_list|(
name|args
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|args
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unrecognized tracing command: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Use -help for help."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
finally|finally
block|{
name|remote
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|TraceAdmin
name|admin
init|=
operator|new
name|TraceAdmin
argument_list|()
decl_stmt|;
name|admin
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|admin
operator|.
name|run
argument_list|(
name|argv
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

