begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

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
name|FileNotFoundException
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
name|FileWriter
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
name|TreeMap
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_comment
comment|/**  * This class drives the creation of a mini-cluster on the local machine. By  * default, a MiniDFSCluster is spawned on the first available ports that are  * found.  *   * A series of command line flags controls the startup cluster options.  *   * This class can dump a Hadoop configuration and some basic metadata (in JSON)  * into a textfile.  *   * To shutdown the cluster, kill the process.  *   * To run this from the command line, do the following (replacing the jar  * version as appropriate):  *   * $HADOOP_HOME/bin/hadoop jar $HADOOP_HOME/share/hadoop/hdfs/hadoop-hdfs-0.24.0-SNAPSHOT-tests.jar org.apache.hadoop.test.MiniDFSClusterManager -options...  */
end_comment

begin_class
DECL|class|MiniDFSClusterManager
specifier|public
class|class
name|MiniDFSClusterManager
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
name|MiniDFSClusterManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dfs
specifier|private
name|MiniDFSCluster
name|dfs
decl_stmt|;
DECL|field|writeDetails
specifier|private
name|String
name|writeDetails
decl_stmt|;
DECL|field|numDataNodes
specifier|private
name|int
name|numDataNodes
decl_stmt|;
DECL|field|nameNodePort
specifier|private
name|int
name|nameNodePort
decl_stmt|;
DECL|field|dfsOpts
specifier|private
name|StartupOption
name|dfsOpts
decl_stmt|;
DECL|field|writeConfig
specifier|private
name|String
name|writeConfig
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|format
specifier|private
name|boolean
name|format
decl_stmt|;
DECL|field|SLEEP_INTERVAL_MS
specifier|private
specifier|static
specifier|final
name|long
name|SLEEP_INTERVAL_MS
init|=
literal|1000
operator|*
literal|60
decl_stmt|;
comment|/**    * Creates configuration options object.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"static-access"
argument_list|)
DECL|method|makeOptions ()
specifier|private
name|Options
name|makeOptions
parameter_list|()
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"datanodes"
argument_list|,
literal|true
argument_list|,
literal|"How many datanodes to start (default 1)"
argument_list|)
operator|.
name|addOption
argument_list|(
literal|"format"
argument_list|,
literal|false
argument_list|,
literal|"Format the DFS (default false)"
argument_list|)
operator|.
name|addOption
argument_list|(
literal|"cmdport"
argument_list|,
literal|true
argument_list|,
literal|"Which port to listen on for commands (default 0--we choose)"
argument_list|)
operator|.
name|addOption
argument_list|(
literal|"nnport"
argument_list|,
literal|true
argument_list|,
literal|"NameNode port (default 0--we choose)"
argument_list|)
operator|.
name|addOption
argument_list|(
literal|"namenode"
argument_list|,
literal|true
argument_list|,
literal|"URL of the namenode (default "
operator|+
literal|"is either the DFS cluster or a temporary dir)"
argument_list|)
operator|.
name|addOption
argument_list|(
name|OptionBuilder
operator|.
name|hasArgs
argument_list|()
operator|.
name|withArgName
argument_list|(
literal|"property=value"
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"Options to pass into configuration object"
argument_list|)
operator|.
name|create
argument_list|(
literal|"D"
argument_list|)
argument_list|)
operator|.
name|addOption
argument_list|(
name|OptionBuilder
operator|.
name|hasArg
argument_list|()
operator|.
name|withArgName
argument_list|(
literal|"path"
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"Save configuration to this XML file."
argument_list|)
operator|.
name|create
argument_list|(
literal|"writeConfig"
argument_list|)
argument_list|)
operator|.
name|addOption
argument_list|(
name|OptionBuilder
operator|.
name|hasArg
argument_list|()
operator|.
name|withArgName
argument_list|(
literal|"path"
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"Write basic information to this JSON file."
argument_list|)
operator|.
name|create
argument_list|(
literal|"writeDetails"
argument_list|)
argument_list|)
operator|.
name|addOption
argument_list|(
name|OptionBuilder
operator|.
name|withDescription
argument_list|(
literal|"Prints option help."
argument_list|)
operator|.
name|create
argument_list|(
literal|"help"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
comment|/**    * Main entry-point.    */
DECL|method|run (String[] args)
specifier|public
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|parseArguments
argument_list|(
name|args
argument_list|)
condition|)
block|{
return|return;
block|}
name|start
argument_list|()
expr_stmt|;
name|sleepForever
argument_list|()
expr_stmt|;
block|}
DECL|method|sleepForever ()
specifier|private
name|void
name|sleepForever
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_INTERVAL_MS
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dfs
operator|.
name|isClusterUp
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cluster is no longer up, exiting"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|_
parameter_list|)
block|{
comment|// nothing
block|}
block|}
block|}
comment|/**    * Starts DFS as specified in member-variable options. Also writes out    * configuration and details, if requested.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
throws|,
name|FileNotFoundException
block|{
name|dfs
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nameNodePort
argument_list|(
name|nameNodePort
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDataNodes
argument_list|)
operator|.
name|startupOption
argument_list|(
name|dfsOpts
argument_list|)
operator|.
name|format
argument_list|(
name|format
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Started MiniDFSCluster -- namenode on port "
operator|+
name|dfs
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeConfig
operator|!=
literal|null
condition|)
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|writeConfig
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|writeXml
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|writeDetails
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"namenode_port"
argument_list|,
name|dfs
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FileWriter
name|fw
init|=
operator|new
name|FileWriter
argument_list|(
operator|new
name|File
argument_list|(
name|writeDetails
argument_list|)
argument_list|)
decl_stmt|;
name|fw
operator|.
name|write
argument_list|(
operator|new
name|JSON
argument_list|()
operator|.
name|toJSON
argument_list|(
name|map
argument_list|)
argument_list|)
expr_stmt|;
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Parses arguments and fills out the member variables.    * @param args Command-line arguments.    * @return true on successful parse; false to indicate that the    * program should exit.    */
DECL|method|parseArguments (String[] args)
specifier|private
name|boolean
name|parseArguments
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Options
name|options
init|=
name|makeOptions
argument_list|()
decl_stmt|;
name|CommandLine
name|cli
decl_stmt|;
try|try
block|{
name|CommandLineParser
name|parser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|cli
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"options parsing failed:  "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"..."
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"help"
argument_list|)
condition|)
block|{
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"..."
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|cli
operator|.
name|getArgs
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|arg
range|:
name|cli
operator|.
name|getArgs
argument_list|()
control|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unrecognized option: "
operator|+
name|arg
argument_list|)
expr_stmt|;
operator|new
name|HelpFormatter
argument_list|()
operator|.
name|printHelp
argument_list|(
literal|"..."
argument_list|,
name|options
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|// HDFS
name|numDataNodes
operator|=
name|intArgument
argument_list|(
name|cli
argument_list|,
literal|"datanodes"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|nameNodePort
operator|=
name|intArgument
argument_list|(
name|cli
argument_list|,
literal|"nnport"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|cli
operator|.
name|hasOption
argument_list|(
literal|"format"
argument_list|)
condition|)
block|{
name|dfsOpts
operator|=
name|StartupOption
operator|.
name|FORMAT
expr_stmt|;
name|format
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|dfsOpts
operator|=
name|StartupOption
operator|.
name|REGULAR
expr_stmt|;
name|format
operator|=
literal|false
expr_stmt|;
block|}
comment|// Runner
name|writeDetails
operator|=
name|cli
operator|.
name|getOptionValue
argument_list|(
literal|"writeDetails"
argument_list|)
expr_stmt|;
name|writeConfig
operator|=
name|cli
operator|.
name|getOptionValue
argument_list|(
literal|"writeConfig"
argument_list|)
expr_stmt|;
comment|// General
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|updateConfiguration
argument_list|(
name|conf
argument_list|,
name|cli
operator|.
name|getOptionValues
argument_list|(
literal|"D"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Updates configuration based on what's given on the command line.    *    * @param conf2 The configuration object    * @param keyvalues An array of interleaved key value pairs.    */
DECL|method|updateConfiguration (Configuration conf2, String[] keyvalues)
specifier|private
name|void
name|updateConfiguration
parameter_list|(
name|Configuration
name|conf2
parameter_list|,
name|String
index|[]
name|keyvalues
parameter_list|)
block|{
name|int
name|num_confs_updated
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|keyvalues
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|prop
range|:
name|keyvalues
control|)
block|{
name|String
index|[]
name|keyval
init|=
name|prop
operator|.
name|split
argument_list|(
literal|"="
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyval
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|conf2
operator|.
name|set
argument_list|(
name|keyval
index|[
literal|0
index|]
argument_list|,
name|keyval
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|num_confs_updated
operator|++
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring -D option "
operator|+
name|prop
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Updated "
operator|+
name|num_confs_updated
operator|+
literal|" configuration settings from command line."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Extracts an integer argument with specified default value.    */
DECL|method|intArgument (CommandLine cli, String argName, int defaultValue)
specifier|private
name|int
name|intArgument
parameter_list|(
name|CommandLine
name|cli
parameter_list|,
name|String
name|argName
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
name|String
name|o
init|=
name|cli
operator|.
name|getOptionValue
argument_list|(
name|argName
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|o
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Couldn't parse value ("
operator|+
name|o
operator|+
literal|") for option "
operator|+
name|argName
operator|+
literal|". Using default: "
operator|+
name|defaultValue
argument_list|)
expr_stmt|;
block|}
return|return
name|defaultValue
return|;
block|}
comment|/**    * Starts a MiniDFSClusterManager with parameters drawn from the command line.    */
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
throws|throws
name|IOException
block|{
operator|new
name|MiniDFSClusterManager
argument_list|()
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

