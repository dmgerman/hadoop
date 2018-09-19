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
name|classification
operator|.
name|InterfaceStability
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
name|base
operator|.
name|Preconditions
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
name|Lists
import|;
end_import

begin_comment
comment|/**  * This class is used to specify the setup of namenodes when instantiating  * a MiniDFSCluster. It consists of a set of nameservices, each of which  * may have one or more namenodes (in the case of HA)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HBase"
block|,
literal|"HDFS"
block|,
literal|"Hive"
block|,
literal|"MapReduce"
block|,
literal|"Pig"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MiniDFSNNTopology
specifier|public
class|class
name|MiniDFSNNTopology
block|{
DECL|field|nameservices
specifier|private
specifier|final
name|List
argument_list|<
name|NSConf
argument_list|>
name|nameservices
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|federation
specifier|private
name|boolean
name|federation
decl_stmt|;
DECL|method|MiniDFSNNTopology ()
specifier|public
name|MiniDFSNNTopology
parameter_list|()
block|{   }
comment|/**    * Set up a simple non-federated non-HA NN.    */
DECL|method|simpleSingleNN ( int nameNodePort, int nameNodeHttpPort)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|simpleSingleNN
parameter_list|(
name|int
name|nameNodePort
parameter_list|,
name|int
name|nameNodeHttpPort
parameter_list|)
block|{
return|return
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|null
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|null
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|nameNodeHttpPort
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|nameNodePort
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Set up an HA topology with a single HA nameservice.    */
DECL|method|simpleHATopology ()
specifier|public
specifier|static
name|MiniDFSNNTopology
name|simpleHATopology
parameter_list|()
block|{
return|return
name|simpleHATopology
argument_list|(
literal|2
argument_list|)
return|;
block|}
comment|/**    * Set up an HA topology with a single HA nameservice.    * @param nnCount of namenodes to use with the nameservice    */
DECL|method|simpleHATopology (int nnCount)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|simpleHATopology
parameter_list|(
name|int
name|nnCount
parameter_list|)
block|{
name|MiniDFSNNTopology
operator|.
name|NSConf
name|nameservice
init|=
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"minidfs-ns"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|nnCount
condition|;
name|i
operator|++
control|)
block|{
name|nameservice
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
name|nameservice
argument_list|)
decl_stmt|;
return|return
name|topology
return|;
block|}
comment|/**    * Set up an HA topology with a single HA nameservice.    * @param nnCount of namenodes to use with the nameservice    * @param basePort for IPC and Http ports of namenodes.    */
DECL|method|simpleHATopology (int nnCount, int basePort)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|simpleHATopology
parameter_list|(
name|int
name|nnCount
parameter_list|,
name|int
name|basePort
parameter_list|)
block|{
name|MiniDFSNNTopology
operator|.
name|NSConf
name|ns
init|=
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"minidfs-ns"
argument_list|)
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
name|nnCount
condition|;
name|i
operator|++
control|)
block|{
name|ns
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn"
operator|+
name|i
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|basePort
operator|++
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|basePort
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
name|ns
argument_list|)
decl_stmt|;
return|return
name|topology
return|;
block|}
comment|/**    * Set up federated cluster with the given number of nameservices, each    * of which has only a single NameNode.    */
DECL|method|simpleFederatedTopology ( int numNameservices)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|simpleFederatedTopology
parameter_list|(
name|int
name|numNameservices
parameter_list|)
block|{
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numNameservices
condition|;
name|i
operator|++
control|)
block|{
name|topology
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns"
operator|+
name|i
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|topology
operator|.
name|setFederation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|topology
return|;
block|}
comment|/**    * Set up federated cluster with the given nameservices, each    * of which has only a single NameNode.    */
DECL|method|simpleFederatedTopology (String nameservicesIds)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|simpleFederatedTopology
parameter_list|(
name|String
name|nameservicesIds
parameter_list|)
block|{
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
decl_stmt|;
name|String
name|nsIds
index|[]
init|=
name|nameservicesIds
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|nsId
range|:
name|nsIds
control|)
block|{
name|topology
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
name|nsId
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|topology
operator|.
name|setFederation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|topology
return|;
block|}
comment|/**    * Set up federated cluster with the given number of nameservices, each    * of which has two NameNodes.    */
DECL|method|simpleHAFederatedTopology ( int numNameservices)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|simpleHAFederatedTopology
parameter_list|(
name|int
name|numNameservices
parameter_list|)
block|{
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
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
name|numNameservices
condition|;
name|i
operator|++
control|)
block|{
name|topology
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns"
operator|+
name|i
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn0"
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|topology
operator|.
name|setFederation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|topology
return|;
block|}
DECL|method|setFederation (boolean federation)
specifier|public
name|MiniDFSNNTopology
name|setFederation
parameter_list|(
name|boolean
name|federation
parameter_list|)
block|{
name|this
operator|.
name|federation
operator|=
name|federation
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addNameservice (NSConf nameservice)
specifier|public
name|MiniDFSNNTopology
name|addNameservice
parameter_list|(
name|NSConf
name|nameservice
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|nameservice
operator|.
name|getNNs
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"Must have at least one NN in a nameservice"
argument_list|)
expr_stmt|;
name|this
operator|.
name|nameservices
operator|.
name|add
argument_list|(
name|nameservice
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|countNameNodes ()
specifier|public
name|int
name|countNameNodes
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NSConf
name|ns
range|:
name|nameservices
control|)
block|{
name|count
operator|+=
name|ns
operator|.
name|nns
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|getOnlyNameNode ()
specifier|public
name|NNConf
name|getOnlyNameNode
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|countNameNodes
argument_list|()
operator|==
literal|1
argument_list|,
literal|"must have exactly one NN!"
argument_list|)
expr_stmt|;
return|return
name|nameservices
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNNs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|isFederated ()
specifier|public
name|boolean
name|isFederated
parameter_list|()
block|{
return|return
name|nameservices
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|||
name|federation
return|;
block|}
comment|/**    * @return true if at least one of the nameservices    * in the topology has HA enabled.    */
DECL|method|isHA ()
specifier|public
name|boolean
name|isHA
parameter_list|()
block|{
for|for
control|(
name|NSConf
name|ns
range|:
name|nameservices
control|)
block|{
if|if
condition|(
name|ns
operator|.
name|getNNs
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * @return true if all of the NNs in the cluster have their HTTP    * port specified to be non-ephemeral.    */
DECL|method|allHttpPortsSpecified ()
specifier|public
name|boolean
name|allHttpPortsSpecified
parameter_list|()
block|{
for|for
control|(
name|NSConf
name|ns
range|:
name|nameservices
control|)
block|{
for|for
control|(
name|NNConf
name|nn
range|:
name|ns
operator|.
name|getNNs
argument_list|()
control|)
block|{
if|if
condition|(
name|nn
operator|.
name|getHttpPort
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * @return true if all of the NNs in the cluster have their IPC    * port specified to be non-ephemeral.    */
DECL|method|allIpcPortsSpecified ()
specifier|public
name|boolean
name|allIpcPortsSpecified
parameter_list|()
block|{
for|for
control|(
name|NSConf
name|ns
range|:
name|nameservices
control|)
block|{
for|for
control|(
name|NNConf
name|nn
range|:
name|ns
operator|.
name|getNNs
argument_list|()
control|)
block|{
if|if
condition|(
name|nn
operator|.
name|getIpcPort
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getNameservices ()
specifier|public
name|List
argument_list|<
name|NSConf
argument_list|>
name|getNameservices
parameter_list|()
block|{
return|return
name|nameservices
return|;
block|}
DECL|class|NSConf
specifier|public
specifier|static
class|class
name|NSConf
block|{
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|nns
specifier|private
specifier|final
name|List
argument_list|<
name|NNConf
argument_list|>
name|nns
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|NSConf (String id)
specifier|public
name|NSConf
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|addNN (NNConf nn)
specifier|public
name|NSConf
name|addNN
parameter_list|(
name|NNConf
name|nn
parameter_list|)
block|{
name|this
operator|.
name|nns
operator|.
name|add
argument_list|(
name|nn
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getNNs ()
specifier|public
name|List
argument_list|<
name|NNConf
argument_list|>
name|getNNs
parameter_list|()
block|{
return|return
name|nns
return|;
block|}
block|}
DECL|class|NNConf
specifier|public
specifier|static
class|class
name|NNConf
block|{
DECL|field|nnId
specifier|private
specifier|final
name|String
name|nnId
decl_stmt|;
DECL|field|httpPort
specifier|private
name|int
name|httpPort
decl_stmt|;
DECL|field|ipcPort
specifier|private
name|int
name|ipcPort
decl_stmt|;
DECL|field|clusterId
specifier|private
name|String
name|clusterId
decl_stmt|;
DECL|method|NNConf (String nnId)
specifier|public
name|NNConf
parameter_list|(
name|String
name|nnId
parameter_list|)
block|{
name|this
operator|.
name|nnId
operator|=
name|nnId
expr_stmt|;
block|}
DECL|method|getNnId ()
specifier|public
name|String
name|getNnId
parameter_list|()
block|{
return|return
name|nnId
return|;
block|}
DECL|method|getIpcPort ()
name|int
name|getIpcPort
parameter_list|()
block|{
return|return
name|ipcPort
return|;
block|}
DECL|method|getHttpPort ()
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
name|httpPort
return|;
block|}
DECL|method|getClusterId ()
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
DECL|method|setHttpPort (int httpPort)
specifier|public
name|NNConf
name|setHttpPort
parameter_list|(
name|int
name|httpPort
parameter_list|)
block|{
name|this
operator|.
name|httpPort
operator|=
name|httpPort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIpcPort (int ipcPort)
specifier|public
name|NNConf
name|setIpcPort
parameter_list|(
name|int
name|ipcPort
parameter_list|)
block|{
name|this
operator|.
name|ipcPort
operator|=
name|ipcPort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setClusterId (String clusterId)
specifier|public
name|NNConf
name|setClusterId
parameter_list|(
name|String
name|clusterId
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

