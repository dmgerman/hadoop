begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanAttributeInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServerConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ReflectionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXConnectorFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|remote
operator|.
name|JMXServiceURL
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
name|HelpFormatter
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
name|Option
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
name|OptionBuilder
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
name|util
operator|.
name|ExitUtil
import|;
end_import

begin_comment
comment|/**  * tool to get data from NameNode or DataNode using MBeans currently the  * following MBeans are available (under hadoop domain):  * hadoop:service=NameNode,name=FSNamesystemState (static)  * hadoop:service=NameNode,name=NameNodeActivity (dynamic)  * hadoop:service=NameNode,name=RpcActivityForPort9000 (dynamic)  * hadoop:service=DataNode,name=RpcActivityForPort50020 (dynamic)  * hadoop:name=service=DataNode,FSDatasetState-UndefinedStorageId663800459  * (static)  * hadoop:service=DataNode,name=DataNodeActivity-UndefinedStorageId-520845215  * (dynamic)  *   *   * implementation note: all logging is sent to System.err (since it is a command  * line tool)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JMXGet
specifier|public
class|class
name|JMXGet
block|{
DECL|field|format
specifier|private
specifier|static
specifier|final
name|String
name|format
init|=
literal|"%s=%s%n"
decl_stmt|;
DECL|field|hadoopObjectNames
specifier|private
name|ArrayList
argument_list|<
name|ObjectName
argument_list|>
name|hadoopObjectNames
decl_stmt|;
DECL|field|mbsc
specifier|private
name|MBeanServerConnection
name|mbsc
decl_stmt|;
DECL|field|service
DECL|field|port
DECL|field|server
specifier|private
name|String
name|service
init|=
literal|"NameNode"
decl_stmt|,
name|port
init|=
literal|""
decl_stmt|,
name|server
init|=
literal|"localhost"
decl_stmt|;
DECL|field|localVMUrl
specifier|private
name|String
name|localVMUrl
init|=
literal|null
decl_stmt|;
DECL|method|JMXGet ()
specifier|public
name|JMXGet
parameter_list|()
block|{   }
DECL|method|setService (String service)
specifier|public
name|void
name|setService
parameter_list|(
name|String
name|service
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
block|}
DECL|method|setPort (String port)
specifier|public
name|void
name|setPort
parameter_list|(
name|String
name|port
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
DECL|method|setServer (String server)
specifier|public
name|void
name|setServer
parameter_list|(
name|String
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
DECL|method|setLocalVMUrl (String url)
specifier|public
name|void
name|setLocalVMUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|localVMUrl
operator|=
name|url
expr_stmt|;
block|}
comment|/**    * print all attributes' values    */
DECL|method|printAllValues ()
specifier|public
name|void
name|printAllValues
parameter_list|()
throws|throws
name|Exception
block|{
name|err
argument_list|(
literal|"List of all the available keys:"
argument_list|)
expr_stmt|;
name|Object
name|val
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectName
name|oname
range|:
name|hadoopObjectNames
control|)
block|{
name|err
argument_list|(
literal|">>>>>>>>jmx name: "
operator|+
name|oname
operator|.
name|getCanonicalKeyPropertyListString
argument_list|()
argument_list|)
expr_stmt|;
name|MBeanInfo
name|mbinfo
init|=
name|mbsc
operator|.
name|getMBeanInfo
argument_list|(
name|oname
argument_list|)
decl_stmt|;
name|MBeanAttributeInfo
index|[]
name|mbinfos
init|=
name|mbinfo
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
for|for
control|(
name|MBeanAttributeInfo
name|mb
range|:
name|mbinfos
control|)
block|{
name|val
operator|=
name|mbsc
operator|.
name|getAttribute
argument_list|(
name|oname
argument_list|,
name|mb
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|mb
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|val
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * get single value by key    */
DECL|method|getValue (String key)
specifier|public
name|String
name|getValue
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|val
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectName
name|oname
range|:
name|hadoopObjectNames
control|)
block|{
try|try
block|{
name|val
operator|=
name|mbsc
operator|.
name|getAttribute
argument_list|(
name|oname
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AttributeNotFoundException
name|anfe
parameter_list|)
block|{
comment|/* just go to the next */
continue|continue;
block|}
catch|catch
parameter_list|(
name|ReflectionException
name|re
parameter_list|)
block|{
if|if
condition|(
name|re
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NoSuchMethodException
condition|)
block|{
continue|continue;
block|}
block|}
name|err
argument_list|(
literal|"Info: key = "
operator|+
name|key
operator|+
literal|"; val = "
operator|+
operator|(
name|val
operator|==
literal|null
condition|?
literal|"null"
else|:
name|val
operator|.
name|getClass
argument_list|()
operator|)
operator|+
literal|":"
operator|+
name|val
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
operator|(
name|val
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @throws Exception    *           initializes MBeanServer    */
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|err
argument_list|(
literal|"init: server="
operator|+
name|server
operator|+
literal|";port="
operator|+
name|port
operator|+
literal|";service="
operator|+
name|service
operator|+
literal|";localVMUrl="
operator|+
name|localVMUrl
argument_list|)
expr_stmt|;
name|String
name|url_string
init|=
literal|null
decl_stmt|;
comment|// build connection url
if|if
condition|(
name|localVMUrl
operator|!=
literal|null
condition|)
block|{
comment|// use
comment|// jstat -snap<vmpid> | grep sun.management.JMXConnectorServer.address
comment|// to get url
name|url_string
operator|=
name|localVMUrl
expr_stmt|;
name|err
argument_list|(
literal|"url string for local pid = "
operator|+
name|localVMUrl
operator|+
literal|" = "
operator|+
name|url_string
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|port
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|server
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// using server and port
name|url_string
operator|=
literal|"service:jmx:rmi:///jndi/rmi://"
operator|+
name|server
operator|+
literal|":"
operator|+
name|port
operator|+
literal|"/jmxrmi"
expr_stmt|;
block|}
comment|// else url stays null
comment|// Create an RMI connector client and
comment|// connect it to the RMI connector server
if|if
condition|(
name|url_string
operator|==
literal|null
condition|)
block|{
comment|// assume local vm (for example for Testing)
name|mbsc
operator|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|JMXServiceURL
name|url
init|=
operator|new
name|JMXServiceURL
argument_list|(
name|url_string
argument_list|)
decl_stmt|;
name|err
argument_list|(
literal|"Create RMI connector and connect to the RMI connector server"
operator|+
name|url
argument_list|)
expr_stmt|;
name|JMXConnector
name|jmxc
init|=
name|JMXConnectorFactory
operator|.
name|connect
argument_list|(
name|url
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Get an MBeanServerConnection
comment|//
name|err
argument_list|(
literal|"\nGet an MBeanServerConnection"
argument_list|)
expr_stmt|;
name|mbsc
operator|=
name|jmxc
operator|.
name|getMBeanServerConnection
argument_list|()
expr_stmt|;
block|}
comment|// Get domains from MBeanServer
comment|//
name|err
argument_list|(
literal|"\nDomains:"
argument_list|)
expr_stmt|;
name|String
name|domains
index|[]
init|=
name|mbsc
operator|.
name|getDomains
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|domains
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|domain
range|:
name|domains
control|)
block|{
name|err
argument_list|(
literal|"\tDomain = "
operator|+
name|domain
argument_list|)
expr_stmt|;
block|}
comment|// Get MBeanServer's default domain
comment|//
name|err
argument_list|(
literal|"\nMBeanServer default domain = "
operator|+
name|mbsc
operator|.
name|getDefaultDomain
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get MBean count
comment|//
name|err
argument_list|(
literal|"\nMBean count = "
operator|+
name|mbsc
operator|.
name|getMBeanCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Query MBean names for specific domain "hadoop" and service
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service="
operator|+
name|service
operator|+
literal|",*"
argument_list|)
decl_stmt|;
name|hadoopObjectNames
operator|=
operator|new
name|ArrayList
argument_list|<
name|ObjectName
argument_list|>
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|err
argument_list|(
literal|"\nQuery MBeanServer MBeans:"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
operator|new
name|TreeSet
argument_list|<
name|ObjectName
argument_list|>
argument_list|(
name|mbsc
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectName
name|name
range|:
name|names
control|)
block|{
name|hadoopObjectNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|err
argument_list|(
literal|"Hadoop service: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Print JMXGet usage information    */
DECL|method|printUsage (Options opts)
specifier|static
name|void
name|printUsage
parameter_list|(
name|Options
name|opts
parameter_list|)
block|{
name|HelpFormatter
name|formatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
literal|"jmxget options are: "
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param msg error message    */
DECL|method|err (String msg)
specifier|private
specifier|static
name|void
name|err
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**    * parse args    */
DECL|method|parseArgs (Options opts, String... args)
specifier|private
specifier|static
name|CommandLine
name|parseArgs
parameter_list|(
name|Options
name|opts
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"NameNode|DataNode"
argument_list|)
expr_stmt|;
name|OptionBuilder
operator|.
name|hasArg
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|withDescription
argument_list|(
literal|"specify jmx service (NameNode by default)"
argument_list|)
expr_stmt|;
name|Option
name|jmx_service
init|=
name|OptionBuilder
operator|.
name|create
argument_list|(
literal|"service"
argument_list|)
decl_stmt|;
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"mbean server"
argument_list|)
expr_stmt|;
name|OptionBuilder
operator|.
name|hasArg
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|withDescription
argument_list|(
literal|"specify mbean server (localhost by default)"
argument_list|)
expr_stmt|;
name|Option
name|jmx_server
init|=
name|OptionBuilder
operator|.
name|create
argument_list|(
literal|"server"
argument_list|)
decl_stmt|;
name|OptionBuilder
operator|.
name|withDescription
argument_list|(
literal|"print help"
argument_list|)
expr_stmt|;
name|Option
name|jmx_help
init|=
name|OptionBuilder
operator|.
name|create
argument_list|(
literal|"help"
argument_list|)
decl_stmt|;
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"mbean server port"
argument_list|)
expr_stmt|;
name|OptionBuilder
operator|.
name|hasArg
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|withDescription
argument_list|(
literal|"specify mbean server port, "
operator|+
literal|"if missing - it will try to connect to MBean Server in the same VM"
argument_list|)
expr_stmt|;
name|Option
name|jmx_port
init|=
name|OptionBuilder
operator|.
name|create
argument_list|(
literal|"port"
argument_list|)
decl_stmt|;
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"VM's connector url"
argument_list|)
expr_stmt|;
name|OptionBuilder
operator|.
name|hasArg
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|withDescription
argument_list|(
literal|"connect to the VM on the same machine;"
operator|+
literal|"\n use:\n jstat -J-Djstat.showUnsupported=true -snap<vmpid> | "
operator|+
literal|"grep sun.management.JMXConnectorServer.address\n "
operator|+
literal|"to find the url"
argument_list|)
expr_stmt|;
name|Option
name|jmx_localVM
init|=
name|OptionBuilder
operator|.
name|create
argument_list|(
literal|"localVM"
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|jmx_server
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|jmx_help
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|jmx_service
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|jmx_port
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|jmx_localVM
argument_list|)
expr_stmt|;
name|CommandLine
name|commandLine
init|=
literal|null
decl_stmt|;
name|CommandLineParser
name|parser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
try|try
block|{
name|commandLine
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid args: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|commandLine
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|int
name|res
init|=
operator|-
literal|1
decl_stmt|;
comment|// parse arguments
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|CommandLine
name|commandLine
init|=
literal|null
decl_stmt|;
try|try
block|{
name|commandLine
operator|=
name|parseArgs
argument_list|(
name|opts
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|commandLine
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|commandLine
operator|==
literal|null
condition|)
block|{
comment|// invalid arguments
name|err
argument_list|(
literal|"Invalid args"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|JMXGet
name|jm
init|=
operator|new
name|JMXGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
literal|"port"
argument_list|)
condition|)
block|{
name|jm
operator|.
name|setPort
argument_list|(
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"port"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
literal|"service"
argument_list|)
condition|)
block|{
name|jm
operator|.
name|setService
argument_list|(
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"service"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
literal|"server"
argument_list|)
condition|)
block|{
name|jm
operator|.
name|setServer
argument_list|(
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"server"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
literal|"localVM"
argument_list|)
condition|)
block|{
comment|// from the file /tmp/hsperfdata*
name|jm
operator|.
name|setLocalVMUrl
argument_list|(
name|commandLine
operator|.
name|getOptionValue
argument_list|(
literal|"localVM"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
literal|"help"
argument_list|)
condition|)
block|{
name|printUsage
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// rest of args
name|args
operator|=
name|commandLine
operator|.
name|getArgs
argument_list|()
expr_stmt|;
try|try
block|{
name|jm
operator|.
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|jm
operator|.
name|printAllValues
argument_list|()
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|key
range|:
name|args
control|)
block|{
name|err
argument_list|(
literal|"key = "
operator|+
name|key
argument_list|)
expr_stmt|;
name|String
name|val
init|=
name|jm
operator|.
name|getValue
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
name|JMXGet
operator|.
name|format
argument_list|,
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|res
operator|=
literal|0
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|re
parameter_list|)
block|{
name|re
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|res
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|ExitUtil
operator|.
name|terminate
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

