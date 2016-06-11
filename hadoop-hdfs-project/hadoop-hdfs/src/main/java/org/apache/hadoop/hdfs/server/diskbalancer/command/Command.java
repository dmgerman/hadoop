begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.command
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|command
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|lang
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
name|commons
operator|.
name|lang
operator|.
name|text
operator|.
name|StrBuilder
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
name|fs
operator|.
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileSystem
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
name|Path
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
name|DFSConfigKeys
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
name|DFSUtilClient
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
name|protocol
operator|.
name|ClientDatanodeProtocol
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
name|server
operator|.
name|diskbalancer
operator|.
name|connectors
operator|.
name|ClusterConnector
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
name|server
operator|.
name|diskbalancer
operator|.
name|connectors
operator|.
name|ConnectorFactory
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
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerCluster
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
name|server
operator|.
name|diskbalancer
operator|.
name|datamodel
operator|.
name|DiskBalancerDataNode
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
name|DiskBalancer
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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Common interface for command handling.  */
end_comment

begin_class
DECL|class|Command
specifier|public
specifier|abstract
class|class
name|Command
extends|extends
name|Configured
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Command
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|validArgs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|validArgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|clusterURI
specifier|private
name|URI
name|clusterURI
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|cluster
specifier|private
name|DiskBalancerCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|topNodes
specifier|private
name|int
name|topNodes
decl_stmt|;
DECL|field|DEFAULT_LOG_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|DEFAULT_LOG_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"/system/diskbalancer"
argument_list|)
decl_stmt|;
DECL|field|diskBalancerLogs
specifier|private
name|Path
name|diskBalancerLogs
decl_stmt|;
comment|/**    * Constructs a command.    */
DECL|method|Command (Configuration conf)
specifier|public
name|Command
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// These arguments are valid for all commands.
name|addValidCommandParameters
argument_list|(
name|DiskBalancer
operator|.
name|HELP
argument_list|,
literal|"Help for this command"
argument_list|)
expr_stmt|;
name|addValidCommandParameters
argument_list|(
literal|"arg"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|topNodes
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Executes the Client Calls.    *    * @param cmd - CommandLine    * @throws IOException    * @throws URISyntaxException    */
DECL|method|execute (CommandLine cmd)
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|CommandLine
name|cmd
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Gets extended help for this command.    *    * @return Help Message    */
DECL|method|getHelp ()
specifier|protected
specifier|abstract
name|String
name|getHelp
parameter_list|()
function_decl|;
comment|/**    * verifies user provided URL.    *    * @param uri - UrlString    * @return URL    * @throws URISyntaxException, MalformedURLException    */
DECL|method|verifyURI (String uri)
specifier|protected
name|URI
name|verifyURI
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|MalformedURLException
block|{
if|if
condition|(
operator|(
name|uri
operator|==
literal|null
operator|)
operator|||
name|uri
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MalformedURLException
argument_list|(
literal|"A valid URI is needed to execute this command."
argument_list|)
throw|;
block|}
return|return
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
return|;
block|}
comment|/**    * Process the URI and return the cluster with nodes setup. This is used in    * all commands.    *    * @param cmd - CommandLine    * @return DiskBalancerCluster    * @throws Exception    */
DECL|method|readClusterInfo (CommandLine cmd)
specifier|protected
name|DiskBalancerCluster
name|readClusterInfo
parameter_list|(
name|CommandLine
name|cmd
parameter_list|)
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|NAMENODEURI
argument_list|)
operator|!=
literal|null
argument_list|,
literal|"Required argument missing : uri"
argument_list|)
expr_stmt|;
name|setClusterURI
argument_list|(
name|verifyURI
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|NAMENODEURI
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"using name node URI : {}"
argument_list|,
name|this
operator|.
name|getClusterURI
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterConnector
name|connector
init|=
name|ConnectorFactory
operator|.
name|getCluster
argument_list|(
name|this
operator|.
name|clusterURI
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|cluster
operator|=
operator|new
name|DiskBalancerCluster
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reading cluster info"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|readClusterInfo
argument_list|()
expr_stmt|;
return|return
name|cluster
return|;
block|}
comment|/**    * Setup the outpath.    *    * @param path - Path or null to use default path.    * @throws IOException    */
DECL|method|setOutputPath (String path)
specifier|protected
name|void
name|setOutputPath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MMM-dd-HH-mm-ss"
argument_list|)
decl_stmt|;
name|Date
name|now
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getClusterURI
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|getClusterURI
argument_list|()
operator|.
name|getScheme
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|diskBalancerLogs
operator|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
operator|+
name|DEFAULT_LOG_DIR
operator|.
name|toString
argument_list|()
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|format
operator|.
name|format
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diskBalancerLogs
operator|=
operator|new
name|Path
argument_list|(
name|DEFAULT_LOG_DIR
operator|.
name|toString
argument_list|()
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|format
operator|.
name|format
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|diskBalancerLogs
operator|=
operator|new
name|Path
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|diskBalancerLogs
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Another Diskbalancer instance is running ? - Target "
operator|+
literal|"Directory already exists. {}"
argument_list|,
name|diskBalancerLogs
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Another DiskBalancer files already exist at the "
operator|+
literal|"target location. "
operator|+
name|diskBalancerLogs
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|fs
operator|.
name|mkdirs
argument_list|(
name|diskBalancerLogs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the nodes to process.    *    * @param node - Node    */
DECL|method|setNodesToProcess (DiskBalancerDataNode node)
specifier|protected
name|void
name|setNodesToProcess
parameter_list|(
name|DiskBalancerDataNode
name|node
parameter_list|)
block|{
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|nodelist
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nodelist
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|setNodesToProcess
argument_list|(
name|nodelist
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the list of Nodes to process.    *    * @param nodes Nodes.    */
DECL|method|setNodesToProcess (List<DiskBalancerDataNode> nodes)
specifier|protected
name|void
name|setNodesToProcess
parameter_list|(
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|nodes
parameter_list|)
block|{
if|if
condition|(
name|cluster
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Set nodes to process invoked before "
operator|+
literal|"initializing cluster. Illegal usage."
argument_list|)
throw|;
block|}
name|cluster
operator|.
name|setNodesToProcess
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a DiskBalancer Node from the Cluster or null if not found.    *    * @param nodeName - can the hostname, IP address or UUID of the node.    * @return - DataNode if found.    */
DECL|method|getNode (String nodeName)
name|DiskBalancerDataNode
name|getNode
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
name|DiskBalancerDataNode
name|node
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nodeName
operator|==
literal|null
operator|||
name|nodeName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|node
return|;
block|}
if|if
condition|(
name|cluster
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|node
return|;
block|}
name|node
operator|=
name|cluster
operator|.
name|getNodeByName
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
return|return
name|node
return|;
block|}
name|node
operator|=
name|cluster
operator|.
name|getNodeByIPAddress
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
return|return
name|node
return|;
block|}
name|node
operator|=
name|cluster
operator|.
name|getNodeByUUID
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
comment|/**    * Gets the node set from a file or a string.    *    * @param listArg - String File URL or a comma separated list of node names.    * @return Set of node names    * @throws IOException    */
DECL|method|getNodeList (String listArg)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeList
parameter_list|(
name|String
name|listArg
parameter_list|)
throws|throws
name|IOException
block|{
name|URL
name|listURL
decl_stmt|;
name|String
name|nodeData
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|resultSet
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|listArg
operator|==
literal|null
operator|)
operator|||
name|listArg
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|resultSet
return|;
block|}
if|if
condition|(
name|listArg
operator|.
name|startsWith
argument_list|(
literal|"file://"
argument_list|)
condition|)
block|{
name|listURL
operator|=
operator|new
name|URL
argument_list|(
name|listArg
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|listURL
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|nodeData
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodeData
operator|=
name|listArg
expr_stmt|;
block|}
name|String
index|[]
name|nodes
init|=
name|nodeData
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|resultSet
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
return|return
name|resultSet
return|;
block|}
comment|/**    * Verifies if the command line options are sane.    *    * @param commandName - Name of the command    * @param cmd         - Parsed Command Line    */
DECL|method|verifyCommandOptions (String commandName, CommandLine cmd)
specifier|protected
name|void
name|verifyCommandOptions
parameter_list|(
name|String
name|commandName
parameter_list|,
name|CommandLine
name|cmd
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Iterator
argument_list|<
name|Option
argument_list|>
name|iter
init|=
name|cmd
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Option
name|opt
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|validArgs
operator|.
name|containsKey
argument_list|(
name|opt
operator|.
name|getArgName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|errMessage
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%nInvalid argument found for command %s : %s%n"
argument_list|,
name|commandName
argument_list|,
name|opt
operator|.
name|getArgName
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|validArguments
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|validArguments
operator|.
name|append
argument_list|(
literal|"Valid arguments are : %n"
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
name|String
argument_list|>
name|args
range|:
name|validArgs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|args
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|desc
init|=
name|args
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|String
operator|.
name|format
argument_list|(
literal|"\t %s : %s %n"
argument_list|,
name|key
argument_list|,
name|desc
argument_list|)
decl_stmt|;
name|validArguments
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
name|errMessage
operator|+
name|validArguments
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid Arguments found."
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Gets cluster URL.    *    * @return - URL    */
DECL|method|getClusterURI ()
specifier|public
name|URI
name|getClusterURI
parameter_list|()
block|{
return|return
name|clusterURI
return|;
block|}
comment|/**    * Set cluster URL.    *    * @param clusterURI - URL    */
DECL|method|setClusterURI (URI clusterURI)
specifier|public
name|void
name|setClusterURI
parameter_list|(
name|URI
name|clusterURI
parameter_list|)
block|{
name|this
operator|.
name|clusterURI
operator|=
name|clusterURI
expr_stmt|;
block|}
comment|/**    * Copied from DFSAdmin.java. -- Creates a connection to dataNode.    *    * @param datanode - dataNode.    * @return ClientDataNodeProtocol    * @throws IOException    */
DECL|method|getDataNodeProxy (String datanode)
specifier|public
name|ClientDatanodeProtocol
name|getDataNodeProxy
parameter_list|(
name|String
name|datanode
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|datanodeAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|datanode
argument_list|)
decl_stmt|;
comment|// For datanode proxy the server principal should be DN's one.
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
argument_list|,
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// Create the client
name|ClientDatanodeProtocol
name|dnProtocol
init|=
name|DFSUtilClient
operator|.
name|createClientDatanodeProtocolProxy
argument_list|(
name|datanodeAddr
argument_list|,
name|getUGI
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|,
name|NetUtils
operator|.
name|getSocketFactory
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|ClientDatanodeProtocol
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|dnProtocol
return|;
block|}
comment|/**    * Returns UGI.    *    * @return UserGroupInformation.    * @throws IOException    */
DECL|method|getUGI ()
specifier|private
specifier|static
name|UserGroupInformation
name|getUGI
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
comment|/**    * Returns a file created in the cluster.    *    * @param fileName - fileName to open.    * @return OutputStream.    */
DECL|method|create (String fileName)
specifier|protected
name|FSDataOutputStream
name|create
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|this
operator|.
name|diskBalancerLogs
argument_list|,
name|fileName
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns a InputStream to read data.    */
DECL|method|open (String fileName)
specifier|protected
name|FSDataInputStream
name|open
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|==
literal|null
condition|)
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the output path where the plan and snapshot gets written.    *    * @return Path    */
DECL|method|getOutputPath ()
specifier|protected
name|Path
name|getOutputPath
parameter_list|()
block|{
return|return
name|diskBalancerLogs
return|;
block|}
comment|/**    * Adds valid params to the valid args table.    *    * @param key    * @param desc    */
DECL|method|addValidCommandParameters (String key, String desc)
specifier|protected
name|void
name|addValidCommandParameters
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
name|validArgs
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|desc
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the cluster.    *    * @return Cluster.    */
DECL|method|getCluster ()
specifier|protected
name|DiskBalancerCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
comment|/**    * returns default top number of nodes.    * @return default top number of nodes.    */
DECL|method|getDefaultTop ()
specifier|protected
name|int
name|getDefaultTop
parameter_list|()
block|{
return|return
name|DiskBalancer
operator|.
name|DEFAULT_TOP
return|;
block|}
comment|/**    * Put output line to log and string buffer.    * */
DECL|method|recordOutput (final StrBuilder result, final String outputLine)
specifier|protected
name|void
name|recordOutput
parameter_list|(
specifier|final
name|StrBuilder
name|result
parameter_list|,
specifier|final
name|String
name|outputLine
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|outputLine
argument_list|)
expr_stmt|;
name|result
operator|.
name|appendln
argument_list|(
name|outputLine
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parse top number of nodes to be processed.    * @return top number of nodes to be processed.    */
DECL|method|parseTopNodes (final CommandLine cmd, final StrBuilder result)
specifier|protected
name|int
name|parseTopNodes
parameter_list|(
specifier|final
name|CommandLine
name|cmd
parameter_list|,
specifier|final
name|StrBuilder
name|result
parameter_list|)
block|{
name|String
name|outputLine
init|=
literal|""
decl_stmt|;
name|int
name|nodes
init|=
literal|0
decl_stmt|;
specifier|final
name|String
name|topVal
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|DiskBalancer
operator|.
name|TOP
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|topVal
argument_list|)
condition|)
block|{
name|outputLine
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"No top limit specified, using default top value %d."
argument_list|,
name|getDefaultTop
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|outputLine
argument_list|)
expr_stmt|;
name|result
operator|.
name|appendln
argument_list|(
name|outputLine
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|getDefaultTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|nodes
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|topVal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|outputLine
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Top limit input is not numeric, using default top value %d."
argument_list|,
name|getDefaultTop
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|outputLine
argument_list|)
expr_stmt|;
name|result
operator|.
name|appendln
argument_list|(
name|outputLine
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|getDefaultTop
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|Math
operator|.
name|min
argument_list|(
name|nodes
argument_list|,
name|cluster
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Set top number of nodes to be processed.    * */
DECL|method|setTopNodes (int topNodes)
specifier|public
name|void
name|setTopNodes
parameter_list|(
name|int
name|topNodes
parameter_list|)
block|{
name|this
operator|.
name|topNodes
operator|=
name|topNodes
expr_stmt|;
block|}
comment|/**    * Get top number of nodes to be processed.    * @return top number of nodes to be processed.    * */
DECL|method|getTopNodes ()
specifier|public
name|int
name|getTopNodes
parameter_list|()
block|{
return|return
name|topNodes
return|;
block|}
block|}
end_class

end_unit

