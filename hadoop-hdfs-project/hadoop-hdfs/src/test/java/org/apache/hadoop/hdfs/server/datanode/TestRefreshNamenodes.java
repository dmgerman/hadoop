begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|util
operator|.
name|Set
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
name|MiniDFSNNTopology
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
name|MiniDFSNNTopology
operator|.
name|NNConf
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
name|MiniDFSNNTopology
operator|.
name|NSConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Joiner
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
name|Sets
import|;
end_import

begin_comment
comment|/**  * Tests datanode refresh namenode list functionality.  */
end_comment

begin_class
DECL|class|TestRefreshNamenodes
specifier|public
class|class
name|TestRefreshNamenodes
block|{
DECL|field|nnPort1
specifier|private
specifier|final
name|int
name|nnPort1
init|=
literal|2221
decl_stmt|;
DECL|field|nnPort2
specifier|private
specifier|final
name|int
name|nnPort2
init|=
literal|2224
decl_stmt|;
DECL|field|nnPort3
specifier|private
specifier|final
name|int
name|nnPort3
init|=
literal|2227
decl_stmt|;
DECL|field|nnPort4
specifier|private
specifier|final
name|int
name|nnPort4
init|=
literal|2230
decl_stmt|;
DECL|field|nnServicePort1
specifier|private
specifier|final
name|int
name|nnServicePort1
init|=
literal|2222
decl_stmt|;
DECL|field|nnServicePort2
specifier|private
specifier|final
name|int
name|nnServicePort2
init|=
literal|2225
decl_stmt|;
DECL|field|nnServicePort3
specifier|private
specifier|final
name|int
name|nnServicePort3
init|=
literal|2228
decl_stmt|;
DECL|field|nnServicePort4
specifier|private
specifier|final
name|int
name|nnServicePort4
init|=
literal|2231
decl_stmt|;
annotation|@
name|Test
DECL|method|testRefreshNamenodes ()
specifier|public
name|void
name|testRefreshNamenodes
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Start cluster with a single NN and DN
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|NSConf
argument_list|(
literal|"ns1"
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|NNConf
argument_list|(
literal|null
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|nnPort1
argument_list|)
operator|.
name|setServicePort
argument_list|(
name|nnServicePort1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setFederation
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|topology
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
name|nnPort2
argument_list|,
name|nnServicePort2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
name|nnPort3
argument_list|,
name|nnServicePort3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
name|nnPort4
argument_list|,
name|nnServicePort4
argument_list|)
expr_stmt|;
comment|// Ensure a BPOfferService in the datanodes corresponds to
comment|// a namenode in the cluster
name|Set
argument_list|<
name|InetSocketAddress
argument_list|>
name|nnAddrsFromCluster
init|=
name|Sets
operator|.
name|newHashSet
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|nnAddrsFromCluster
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|(
name|i
argument_list|)
operator|.
name|getServiceRpcAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|InetSocketAddress
argument_list|>
name|nnAddrsFromDN
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|BPOfferService
name|bpos
range|:
name|dn
operator|.
name|getAllBpOs
argument_list|()
control|)
block|{
for|for
control|(
name|BPServiceActor
name|bpsa
range|:
name|bpos
operator|.
name|getBPServiceActors
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|nnAddrsFromDN
operator|.
name|add
argument_list|(
name|bpsa
operator|.
name|getNNSocketAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|nnAddrsFromCluster
argument_list|,
name|nnAddrsFromDN
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

