begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|UnknownHostException
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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|FSDatasetInterface
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
name|datanode
operator|.
name|SimulatedFSDataset
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
name|namenode
operator|.
name|CreateEditsLog
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
name|DNS
import|;
end_import

begin_comment
comment|/**  *    *   * This program starts a mini cluster of data nodes  *  (ie a mini cluster without the name node), all within one address space.  *  It is assumed that the name node has been started separately prior  *  to running this program.  *    *  A use case of this is to run a real name node with a large number of  *  simulated data nodes for say a NN benchmark.  *    * Synopisis:  *   DataNodeCluster -n numDatNodes [-racks numRacks] -simulated  *              [-inject startingBlockId numBlocksPerDN]  *              [ -r replicationForInjectedBlocks ]  *              [-d editsLogDirectory]  *  * if -simulated is specified then simulated data nodes are started.  * if -inject is specified then blocks are injected in each datanode;  *    -inject option is valid only for simulated data nodes.  *      *    See Also @link #CreateEditsLog for creating a edits log file to  *    inject a matching set of blocks into into a name node.  *    Typical use of -inject is to inject blocks into a set of datanodes  *    using this DataNodeCLuster command  *    and then to inject the same blocks into a name node using the  *    CreateEditsLog command.  *  */
end_comment

begin_class
DECL|class|DataNodeCluster
specifier|public
class|class
name|DataNodeCluster
block|{
DECL|field|DATANODE_DIRS
specifier|static
specifier|final
name|String
name|DATANODE_DIRS
init|=
literal|"/tmp/DataNodeCluster"
decl_stmt|;
DECL|field|dataNodeDirs
specifier|static
name|String
name|dataNodeDirs
init|=
name|DATANODE_DIRS
decl_stmt|;
DECL|field|USAGE
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"Usage: datanodecluster "
operator|+
literal|" -n<numDataNodes> "
operator|+
literal|" [-racks<numRacks>] "
operator|+
literal|" [-simulated] "
operator|+
literal|" [-inject startingBlockId numBlocksPerDN]"
operator|+
literal|" [-r replicationFactorForInjectedBlocks]"
operator|+
literal|" [-d dataNodeDirs]\n"
operator|+
literal|" [-checkDataNodeAddrConfig]\n"
operator|+
literal|"      Default datanode direcory is "
operator|+
name|DATANODE_DIRS
operator|+
literal|"\n"
operator|+
literal|"      Default replication factor for injected blocks is 1\n"
operator|+
literal|"      Defaul rack is used if -racks is not specified\n"
operator|+
literal|"      Data nodes are simulated if -simulated OR conf file specifies simulated\n"
operator|+
literal|"      -checkDataNodeAddrConfig tells DataNodeConf to use data node addresses from conf file, if it is set. If not set, use .localhost'."
decl_stmt|;
DECL|method|printUsageExit ()
specifier|static
name|void
name|printUsageExit
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|USAGE
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|printUsageExit (String err)
specifier|static
name|void
name|printUsageExit
parameter_list|(
name|String
name|err
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|printUsageExit
argument_list|()
expr_stmt|;
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
name|numDataNodes
init|=
literal|0
decl_stmt|;
name|int
name|numRacks
init|=
literal|0
decl_stmt|;
name|boolean
name|inject
init|=
literal|false
decl_stmt|;
name|long
name|startingBlockId
init|=
literal|1
decl_stmt|;
name|int
name|numBlocksPerDNtoInject
init|=
literal|0
decl_stmt|;
name|int
name|replication
init|=
literal|1
decl_stmt|;
name|boolean
name|checkDataNodeAddrConfig
init|=
literal|false
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
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
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// parse command line
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-n"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"missing number of nodes"
argument_list|)
expr_stmt|;
block|}
name|numDataNodes
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-racks"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing number of racks"
argument_list|)
expr_stmt|;
block|}
name|numRacks
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-r"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing replicaiton factor"
argument_list|)
expr_stmt|;
block|}
name|replication
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-d"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|i
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing datanode dirs parameter"
argument_list|)
expr_stmt|;
block|}
name|dataNodeDirs
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-simulated"
argument_list|)
condition|)
block|{
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-inject"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|FSDatasetInterface
operator|.
name|Factory
operator|.
name|getFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|isSimulated
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"-inject is valid only for simulated"
argument_list|)
expr_stmt|;
name|printUsageExit
argument_list|()
expr_stmt|;
block|}
name|inject
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing starting block and number of blocks per DN to inject"
argument_list|)
expr_stmt|;
block|}
name|startingBlockId
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|>=
name|args
operator|.
name|length
operator|||
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Missing number of blocks to inject"
argument_list|)
expr_stmt|;
block|}
name|numBlocksPerDNtoInject
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-checkDataNodeAddrConfig"
argument_list|)
condition|)
block|{
name|checkDataNodeAddrConfig
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|printUsageExit
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|numDataNodes
operator|<=
literal|0
operator|||
name|replication
operator|<=
literal|0
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"numDataNodes and replication have to be greater than zero"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|replication
operator|>
name|numDataNodes
condition|)
block|{
name|printUsageExit
argument_list|(
literal|"Replication must be less than or equal to numDataNodes"
argument_list|)
expr_stmt|;
block|}
name|String
name|nameNodeAdr
init|=
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|conf
argument_list|)
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameNodeAdr
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No name node address and port in config"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|boolean
name|simulated
init|=
name|FSDatasetInterface
operator|.
name|Factory
operator|.
name|getFactory
argument_list|(
name|conf
argument_list|)
operator|.
name|isSimulated
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting "
operator|+
name|numDataNodes
operator|+
operator|(
name|simulated
condition|?
literal|" Simulated "
else|:
literal|" "
operator|)
operator|+
literal|" Data Nodes that will connect to Name Node at "
operator|+
name|nameNodeAdr
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|dataNodeDirs
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|mc
init|=
operator|new
name|MiniDFSCluster
argument_list|()
decl_stmt|;
try|try
block|{
name|mc
operator|.
name|formatDataNodeDirs
argument_list|()
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
literal|"Error formating data node dirs:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|rack4DataNode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|numRacks
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using "
operator|+
name|numRacks
operator|+
literal|" racks: "
argument_list|)
expr_stmt|;
name|String
name|rackPrefix
init|=
name|getUniqueRackPrefix
argument_list|()
decl_stmt|;
name|rack4DataNode
operator|=
operator|new
name|String
index|[
name|numDataNodes
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDataNodes
condition|;
operator|++
name|i
control|)
block|{
comment|//rack4DataNode[i] = racks[i%numRacks];
name|rack4DataNode
index|[
name|i
index|]
operator|=
name|rackPrefix
operator|+
literal|"-"
operator|+
name|i
operator|%
name|numRacks
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Data Node "
operator|+
name|i
operator|+
literal|" using "
operator|+
name|rack4DataNode
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|mc
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
name|numDataNodes
argument_list|,
literal|true
argument_list|,
name|StartupOption
operator|.
name|REGULAR
argument_list|,
name|rack4DataNode
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|checkDataNodeAddrConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|inject
condition|)
block|{
name|long
name|blockSize
init|=
literal|10
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Injecting "
operator|+
name|numBlocksPerDNtoInject
operator|+
literal|" blocks in each DN starting at blockId "
operator|+
name|startingBlockId
operator|+
literal|" with blocksize of "
operator|+
name|blockSize
argument_list|)
expr_stmt|;
name|Block
index|[]
name|blocks
init|=
operator|new
name|Block
index|[
name|numBlocksPerDNtoInject
index|]
decl_stmt|;
name|long
name|blkid
init|=
name|startingBlockId
decl_stmt|;
for|for
control|(
name|int
name|i_dn
init|=
literal|0
init|;
name|i_dn
operator|<
name|numDataNodes
condition|;
operator|++
name|i_dn
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blocks
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|Block
argument_list|(
name|blkid
operator|++
argument_list|,
name|blockSize
argument_list|,
name|CreateEditsLog
operator|.
name|BLOCK_GENERATION_STAMP
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|replication
condition|;
operator|++
name|i
control|)
block|{
comment|// inject blocks for dn_i into dn_i and replica in dn_i's neighbors
name|mc
operator|.
name|injectBlocks
argument_list|(
operator|(
name|i_dn
operator|+
name|i
operator|-
literal|1
operator|)
operator|%
name|numDataNodes
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blocks
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Injecting blocks of dn "
operator|+
name|i_dn
operator|+
literal|" into dn"
operator|+
operator|(
operator|(
name|i_dn
operator|+
name|i
operator|-
literal|1
operator|)
operator|%
name|numDataNodes
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Created blocks from Bids "
operator|+
name|startingBlockId
operator|+
literal|" to "
operator|+
operator|(
name|blkid
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
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
literal|"Error creating data node:"
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * There is high probability that the rack id generated here will     * not conflict with those of other data node cluster.    * Not perfect but mostly unique rack ids are good enough    */
DECL|method|getUniqueRackPrefix ()
specifier|static
specifier|private
name|String
name|getUniqueRackPrefix
parameter_list|()
block|{
name|String
name|ip
init|=
literal|"unknownIP"
decl_stmt|;
try|try
block|{
name|ip
operator|=
name|DNS
operator|.
name|getDefaultIP
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|ignored
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Could not find ip address of \"default\" inteface."
argument_list|)
expr_stmt|;
block|}
name|int
name|rand
init|=
name|DFSUtil
operator|.
name|getSecureRandom
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
return|return
literal|"/Rack-"
operator|+
name|rand
operator|+
literal|"-"
operator|+
name|ip
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
block|}
end_class

end_unit

