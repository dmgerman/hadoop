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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
import|;
end_import

begin_import
import|import static
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
name|Util
operator|.
name|fileAsURI
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
name|IOException
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
name|DataNode
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
name|SecureDataNodeStarter
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
name|datanode
operator|.
name|SecureDataNodeStarter
operator|.
name|SecureResources
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
name|net
operator|.
name|StaticMapping
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
name|security
operator|.
name|ssl
operator|.
name|SSLFactory
import|;
end_import

begin_class
DECL|class|MiniDFSClusterWithNodeGroup
specifier|public
class|class
name|MiniDFSClusterWithNodeGroup
extends|extends
name|MiniDFSCluster
block|{
DECL|field|NODE_GROUPS
specifier|private
specifier|static
name|String
index|[]
name|NODE_GROUPS
init|=
literal|null
decl_stmt|;
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
name|MiniDFSClusterWithNodeGroup
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MiniDFSClusterWithNodeGroup (Builder builder)
specifier|public
name|MiniDFSClusterWithNodeGroup
parameter_list|(
name|Builder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
DECL|method|setNodeGroups (String[] nodeGroups)
specifier|public
specifier|static
name|void
name|setNodeGroups
parameter_list|(
name|String
index|[]
name|nodeGroups
parameter_list|)
block|{
name|NODE_GROUPS
operator|=
name|nodeGroups
expr_stmt|;
block|}
DECL|method|startDataNodes (Configuration conf, int numDataNodes, StorageType storageType, boolean manageDfsDirs, StartupOption operation, String[] racks, String[] nodeGroups, String[] hosts, long[] simulatedCapacities, boolean setupHostsFile, boolean checkDataNodeAddrConfig, boolean checkDataNodeHostConfig)
specifier|public
specifier|synchronized
name|void
name|startDataNodes
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|numDataNodes
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|boolean
name|manageDfsDirs
parameter_list|,
name|StartupOption
name|operation
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|String
index|[]
name|nodeGroups
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|long
index|[]
name|simulatedCapacities
parameter_list|,
name|boolean
name|setupHostsFile
parameter_list|,
name|boolean
name|checkDataNodeAddrConfig
parameter_list|,
name|boolean
name|checkDataNodeHostConfig
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|operation
operator|==
name|StartupOption
operator|.
name|RECOVER
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|checkDataNodeHostConfig
condition|)
block|{
name|conf
operator|.
name|setIfUnset
argument_list|(
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|int
name|curDatanodesNum
init|=
name|dataNodes
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// for mincluster's the default initialDelay for BRs is 0
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INITIAL_DELAY_KEY
argument_list|)
operator|==
literal|null
condition|)
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCKREPORT_INITIAL_DELAY_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// If minicluster's name node is null assume that the conf has been
comment|// set with the right address:port of the name node.
comment|//
if|if
condition|(
name|racks
operator|!=
literal|null
operator|&&
name|numDataNodes
operator|>
name|racks
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The length of racks ["
operator|+
name|racks
operator|.
name|length
operator|+
literal|"] is less than the number of datanodes ["
operator|+
name|numDataNodes
operator|+
literal|"]."
argument_list|)
throw|;
block|}
if|if
condition|(
name|nodeGroups
operator|!=
literal|null
operator|&&
name|numDataNodes
operator|>
name|nodeGroups
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The length of nodeGroups ["
operator|+
name|nodeGroups
operator|.
name|length
operator|+
literal|"] is less than the number of datanodes ["
operator|+
name|numDataNodes
operator|+
literal|"]."
argument_list|)
throw|;
block|}
if|if
condition|(
name|hosts
operator|!=
literal|null
operator|&&
name|numDataNodes
operator|>
name|hosts
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The length of hosts ["
operator|+
name|hosts
operator|.
name|length
operator|+
literal|"] is less than the number of datanodes ["
operator|+
name|numDataNodes
operator|+
literal|"]."
argument_list|)
throw|;
block|}
comment|//Generate some hostnames if required
if|if
condition|(
name|racks
operator|!=
literal|null
operator|&&
name|hosts
operator|==
literal|null
condition|)
block|{
name|hosts
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
name|curDatanodesNum
init|;
name|i
operator|<
name|curDatanodesNum
operator|+
name|numDataNodes
condition|;
name|i
operator|++
control|)
block|{
name|hosts
index|[
name|i
operator|-
name|curDatanodesNum
index|]
operator|=
literal|"host"
operator|+
name|i
operator|+
literal|".foo.com"
expr_stmt|;
block|}
block|}
if|if
condition|(
name|simulatedCapacities
operator|!=
literal|null
operator|&&
name|numDataNodes
operator|>
name|simulatedCapacities
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The length of simulatedCapacities ["
operator|+
name|simulatedCapacities
operator|.
name|length
operator|+
literal|"] is less than the number of datanodes ["
operator|+
name|numDataNodes
operator|+
literal|"]."
argument_list|)
throw|;
block|}
name|String
index|[]
name|dnArgs
init|=
operator|(
name|operation
operator|==
literal|null
operator|||
name|operation
operator|!=
name|StartupOption
operator|.
name|ROLLBACK
operator|)
condition|?
literal|null
else|:
operator|new
name|String
index|[]
block|{
name|operation
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|curDatanodesNum
init|;
name|i
operator|<
name|curDatanodesNum
operator|+
name|numDataNodes
condition|;
name|i
operator|++
control|)
block|{
name|Configuration
name|dnConf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Set up datanode address
name|setupDatanodeAddress
argument_list|(
name|dnConf
argument_list|,
name|setupHostsFile
argument_list|,
name|checkDataNodeAddrConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|manageDfsDirs
condition|)
block|{
name|String
name|dirs
init|=
name|makeDataNodeDirs
argument_list|(
name|i
argument_list|,
name|storageType
argument_list|)
decl_stmt|;
name|dnConf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dirs
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dirs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|simulatedCapacities
operator|!=
literal|null
condition|)
block|{
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|dnConf
argument_list|)
expr_stmt|;
name|dnConf
operator|.
name|setLong
argument_list|(
name|SimulatedFSDataset
operator|.
name|CONFIG_PROPERTY_CAPACITY
argument_list|,
name|simulatedCapacities
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting DataNode "
operator|+
name|i
operator|+
literal|" with "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
operator|+
literal|": "
operator|+
name|dnConf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|hosts
operator|!=
literal|null
condition|)
block|{
name|dnConf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|,
name|hosts
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting DataNode "
operator|+
name|i
operator|+
literal|" with hostname set to: "
operator|+
name|dnConf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HOST_NAME_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|racks
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
name|hosts
index|[
name|i
operator|-
name|curDatanodesNum
index|]
decl_stmt|;
if|if
condition|(
name|nodeGroups
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding node with hostname : "
operator|+
name|name
operator|+
literal|" to rack "
operator|+
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
name|StaticMapping
operator|.
name|addNodeToRack
argument_list|(
name|name
argument_list|,
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding node with hostname : "
operator|+
name|name
operator|+
literal|" to serverGroup "
operator|+
name|nodeGroups
index|[
name|i
operator|-
name|curDatanodesNum
index|]
operator|+
literal|" and rack "
operator|+
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
name|StaticMapping
operator|.
name|addNodeToRack
argument_list|(
name|name
argument_list|,
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
operator|+
name|nodeGroups
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|Configuration
name|newconf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|dnConf
argument_list|)
decl_stmt|;
comment|// save config
if|if
condition|(
name|hosts
operator|!=
literal|null
condition|)
block|{
name|NetUtils
operator|.
name|addStaticResolution
argument_list|(
name|hosts
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
block|}
name|SecureResources
name|secureResources
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
try|try
block|{
name|secureResources
operator|=
name|SecureDataNodeStarter
operator|.
name|getSecureResources
argument_list|(
name|dnConf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|DataNode
name|dn
init|=
name|DataNode
operator|.
name|instantiateDataNode
argument_list|(
name|dnArgs
argument_list|,
name|dnConf
argument_list|,
name|secureResources
argument_list|)
decl_stmt|;
if|if
condition|(
name|dn
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot start DataNode in "
operator|+
name|dnConf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|)
argument_list|)
throw|;
comment|//since the HDFS does things based on IP:port, we need to add the mapping
comment|//for IP:port to rackId
name|String
name|ipAddr
init|=
name|dn
operator|.
name|getXferAddress
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|racks
operator|!=
literal|null
condition|)
block|{
name|int
name|port
init|=
name|dn
operator|.
name|getXferAddress
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeGroups
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding node with IP:port : "
operator|+
name|ipAddr
operator|+
literal|":"
operator|+
name|port
operator|+
literal|" to rack "
operator|+
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
name|StaticMapping
operator|.
name|addNodeToRack
argument_list|(
name|ipAddr
operator|+
literal|":"
operator|+
name|port
argument_list|,
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding node with IP:port : "
operator|+
name|ipAddr
operator|+
literal|":"
operator|+
name|port
operator|+
literal|" to nodeGroup "
operator|+
name|nodeGroups
index|[
name|i
operator|-
name|curDatanodesNum
index|]
operator|+
literal|" and rack "
operator|+
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
name|StaticMapping
operator|.
name|addNodeToRack
argument_list|(
name|ipAddr
operator|+
literal|":"
operator|+
name|port
argument_list|,
name|racks
index|[
name|i
operator|-
name|curDatanodesNum
index|]
operator|+
name|nodeGroups
index|[
name|i
operator|-
name|curDatanodesNum
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|dn
operator|.
name|runDatanodeDaemon
argument_list|()
expr_stmt|;
name|dataNodes
operator|.
name|add
argument_list|(
operator|new
name|DataNodeProperties
argument_list|(
name|dn
argument_list|,
name|newconf
argument_list|,
name|dnArgs
argument_list|,
name|secureResources
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|curDatanodesNum
operator|+=
name|numDataNodes
expr_stmt|;
name|this
operator|.
name|numDataNodes
operator|+=
name|numDataNodes
expr_stmt|;
name|waitActive
argument_list|()
expr_stmt|;
block|}
DECL|method|startDataNodes (Configuration conf, int numDataNodes, boolean manageDfsDirs, StartupOption operation, String[] racks, String[] nodeGroups, String[] hosts, long[] simulatedCapacities, boolean setupHostsFile)
specifier|public
specifier|synchronized
name|void
name|startDataNodes
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|numDataNodes
parameter_list|,
name|boolean
name|manageDfsDirs
parameter_list|,
name|StartupOption
name|operation
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|String
index|[]
name|nodeGroups
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|long
index|[]
name|simulatedCapacities
parameter_list|,
name|boolean
name|setupHostsFile
parameter_list|)
throws|throws
name|IOException
block|{
name|startDataNodes
argument_list|(
name|conf
argument_list|,
name|numDataNodes
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|,
name|manageDfsDirs
argument_list|,
name|operation
argument_list|,
name|racks
argument_list|,
name|nodeGroups
argument_list|,
name|hosts
argument_list|,
name|simulatedCapacities
argument_list|,
name|setupHostsFile
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|startDataNodes (Configuration conf, int numDataNodes, boolean manageDfsDirs, StartupOption operation, String[] racks, long[] simulatedCapacities, String[] nodeGroups)
specifier|public
name|void
name|startDataNodes
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|numDataNodes
parameter_list|,
name|boolean
name|manageDfsDirs
parameter_list|,
name|StartupOption
name|operation
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|long
index|[]
name|simulatedCapacities
parameter_list|,
name|String
index|[]
name|nodeGroups
parameter_list|)
throws|throws
name|IOException
block|{
name|startDataNodes
argument_list|(
name|conf
argument_list|,
name|numDataNodes
argument_list|,
name|manageDfsDirs
argument_list|,
name|operation
argument_list|,
name|racks
argument_list|,
name|nodeGroups
argument_list|,
literal|null
argument_list|,
name|simulatedCapacities
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// This is for initialize from parent class.
annotation|@
name|Override
DECL|method|startDataNodes (Configuration conf, int numDataNodes, StorageType storageType, boolean manageDfsDirs, StartupOption operation, String[] racks, String[] hosts, long[] simulatedCapacities, boolean setupHostsFile, boolean checkDataNodeAddrConfig, boolean checkDataNodeHostConfig, Configuration[] dnConfOverlays)
specifier|public
specifier|synchronized
name|void
name|startDataNodes
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|numDataNodes
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|boolean
name|manageDfsDirs
parameter_list|,
name|StartupOption
name|operation
parameter_list|,
name|String
index|[]
name|racks
parameter_list|,
name|String
index|[]
name|hosts
parameter_list|,
name|long
index|[]
name|simulatedCapacities
parameter_list|,
name|boolean
name|setupHostsFile
parameter_list|,
name|boolean
name|checkDataNodeAddrConfig
parameter_list|,
name|boolean
name|checkDataNodeHostConfig
parameter_list|,
name|Configuration
index|[]
name|dnConfOverlays
parameter_list|)
throws|throws
name|IOException
block|{
name|startDataNodes
argument_list|(
name|conf
argument_list|,
name|numDataNodes
argument_list|,
name|storageType
argument_list|,
name|manageDfsDirs
argument_list|,
name|operation
argument_list|,
name|racks
argument_list|,
name|NODE_GROUPS
argument_list|,
name|hosts
argument_list|,
name|simulatedCapacities
argument_list|,
name|setupHostsFile
argument_list|,
name|checkDataNodeAddrConfig
argument_list|,
name|checkDataNodeHostConfig
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

