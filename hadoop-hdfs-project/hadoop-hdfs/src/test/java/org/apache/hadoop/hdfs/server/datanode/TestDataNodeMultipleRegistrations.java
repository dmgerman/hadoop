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
name|assertFalse
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
name|assertNotNull
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
name|assertNotSame
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
name|DFSTestUtil
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
name|MiniDFSCluster
operator|.
name|DataNodeProperties
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
name|namenode
operator|.
name|FSImageTestUtil
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
name|FSNamesystem
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
name|NameNode
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
DECL|class|TestDataNodeMultipleRegistrations
specifier|public
class|class
name|TestDataNodeMultipleRegistrations
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
name|TestDataNodeMultipleRegistrations
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
block|}
comment|/**    * start multiple NNs and single DN and verifies per BP registrations and    * handshakes.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|test2NNRegistration ()
specifier|public
name|void
name|test2NNRegistration
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
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
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|NameNode
name|nn1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NameNode
name|nn2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"cannot create nn1"
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"cannot create nn2"
argument_list|,
name|nn2
argument_list|)
expr_stmt|;
name|String
name|bpid1
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn1
argument_list|)
operator|.
name|getBlockPoolID
argument_list|()
decl_stmt|;
name|String
name|bpid2
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn2
argument_list|)
operator|.
name|getBlockPoolID
argument_list|()
decl_stmt|;
name|String
name|cid1
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn1
argument_list|)
operator|.
name|getClusterID
argument_list|()
decl_stmt|;
name|String
name|cid2
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn2
argument_list|)
operator|.
name|getClusterID
argument_list|()
decl_stmt|;
name|int
name|lv1
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn1
argument_list|)
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
name|int
name|lv2
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn2
argument_list|)
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
name|int
name|ns1
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn1
argument_list|)
operator|.
name|getNamespaceID
argument_list|()
decl_stmt|;
name|int
name|ns2
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn2
argument_list|)
operator|.
name|getNamespaceID
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
literal|"namespace ids should be different"
argument_list|,
name|ns1
argument_list|,
name|ns2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nn1: lv="
operator|+
name|lv1
operator|+
literal|";cid="
operator|+
name|cid1
operator|+
literal|";bpid="
operator|+
name|bpid1
operator|+
literal|";uri="
operator|+
name|nn1
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nn2: lv="
operator|+
name|lv2
operator|+
literal|";cid="
operator|+
name|cid2
operator|+
literal|";bpid="
operator|+
name|bpid2
operator|+
literal|";uri="
operator|+
name|nn2
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// check number of volumes in fsdataset
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
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|volInfos
init|=
name|dn
operator|.
name|data
operator|.
name|getVolumeInfoMap
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"No volumes in the fsdataset"
argument_list|,
name|volInfos
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|volInfos
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"vol "
operator|+
name|i
operator|++
operator|+
literal|") "
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// number of volumes should be 2 - [data1, data2]
name|assertEquals
argument_list|(
literal|"number of volumes is wrong"
argument_list|,
literal|2
argument_list|,
name|volInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"BP: "
operator|+
name|bpos
argument_list|)
expr_stmt|;
block|}
name|BPOfferService
name|bpos1
init|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|BPOfferService
name|bpos2
init|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// The order of bpos is not guaranteed, so fix the order
if|if
condition|(
name|getNNSocketAddress
argument_list|(
name|bpos1
argument_list|)
operator|.
name|equals
argument_list|(
name|nn2
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
condition|)
block|{
name|BPOfferService
name|tmp
init|=
name|bpos1
decl_stmt|;
name|bpos1
operator|=
name|bpos2
expr_stmt|;
name|bpos2
operator|=
name|tmp
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"wrong nn address"
argument_list|,
name|getNNSocketAddress
argument_list|(
name|bpos1
argument_list|)
argument_list|,
name|nn1
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong nn address"
argument_list|,
name|getNNSocketAddress
argument_list|(
name|bpos2
argument_list|)
argument_list|,
name|nn2
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong bpid"
argument_list|,
name|bpos1
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|bpid1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong bpid"
argument_list|,
name|bpos2
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|bpid2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong cid"
argument_list|,
name|dn
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|cid1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"cid should be same"
argument_list|,
name|cid2
argument_list|,
name|cid1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"namespace should be same"
argument_list|,
name|bpos1
operator|.
name|bpNSInfo
operator|.
name|namespaceID
argument_list|,
name|ns1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"namespace should be same"
argument_list|,
name|bpos2
operator|.
name|bpNSInfo
operator|.
name|namespaceID
argument_list|,
name|ns2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getNNSocketAddress (BPOfferService bpos)
specifier|private
specifier|static
name|InetSocketAddress
name|getNNSocketAddress
parameter_list|(
name|BPOfferService
name|bpos
parameter_list|)
block|{
name|List
argument_list|<
name|BPServiceActor
argument_list|>
name|actors
init|=
name|bpos
operator|.
name|getBPServiceActors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|actors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|actors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNNSocketAddress
argument_list|()
return|;
block|}
comment|/**    * starts single nn and single dn and verifies registration and handshake    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testFedSingleNN ()
specifier|public
name|void
name|testFedSingleNN
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniDFSCluster
name|cluster
init|=
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
literal|9927
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|NameNode
name|nn1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"cannot create nn1"
argument_list|,
name|nn1
argument_list|)
expr_stmt|;
name|String
name|bpid1
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn1
argument_list|)
operator|.
name|getBlockPoolID
argument_list|()
decl_stmt|;
name|String
name|cid1
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn1
argument_list|)
operator|.
name|getClusterID
argument_list|()
decl_stmt|;
name|int
name|lv1
init|=
name|FSImageTestUtil
operator|.
name|getFSImage
argument_list|(
name|nn1
argument_list|)
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"nn1: lv="
operator|+
name|lv1
operator|+
literal|";cid="
operator|+
name|cid1
operator|+
literal|";bpid="
operator|+
name|bpid1
operator|+
literal|";uri="
operator|+
name|nn1
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// check number of vlumes in fsdataset
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
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|volInfos
init|=
name|dn
operator|.
name|data
operator|.
name|getVolumeInfoMap
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"No volumes in the fsdataset"
argument_list|,
name|volInfos
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|volInfos
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"vol "
operator|+
name|i
operator|++
operator|+
literal|") "
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// number of volumes should be 2 - [data1, data2]
name|assertEquals
argument_list|(
literal|"number of volumes is wrong"
argument_list|,
literal|2
argument_list|,
name|volInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"reg: bpid="
operator|+
literal|"; name="
operator|+
name|bpos
operator|.
name|bpRegistration
operator|+
literal|"; sid="
operator|+
name|bpos
operator|.
name|bpRegistration
operator|.
name|getDatanodeUuid
argument_list|()
operator|+
literal|"; nna="
operator|+
name|getNNSocketAddress
argument_list|(
name|bpos
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// try block report
name|BPOfferService
name|bpos1
init|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|bpos1
operator|.
name|triggerBlockReportForTests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong nn address"
argument_list|,
name|getNNSocketAddress
argument_list|(
name|bpos1
argument_list|)
argument_list|,
name|nn1
operator|.
name|getNameNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong bpid"
argument_list|,
name|bpos1
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|bpid1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong cid"
argument_list|,
name|dn
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|cid1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// Ensure all the BPOfferService threads are shutdown
name|assertEquals
argument_list|(
literal|0
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
operator|=
literal|null
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
annotation|@
name|Test
DECL|method|testClusterIdMismatch ()
specifier|public
name|void
name|testClusterIdMismatch
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
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
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
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
name|List
argument_list|<
name|BPOfferService
argument_list|>
name|bposs
init|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"dn bpos len (should be 2):"
operator|+
name|bposs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"should've registered with two namenodes"
argument_list|,
name|bposs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// add another namenode
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
literal|9938
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|// lets wait for the registration to happen
name|bposs
operator|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"dn bpos len (should be 3):"
operator|+
name|bposs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"should've registered with three namenodes"
argument_list|,
name|bposs
operator|.
name|size
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|// change cluster id and another Namenode
name|StartupOption
operator|.
name|FORMAT
operator|.
name|setClusterId
argument_list|(
literal|"DifferentCID"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
literal|9948
argument_list|)
expr_stmt|;
name|NameNode
name|nn4
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"cannot create nn4"
argument_list|,
name|nn4
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|// lets wait for the registration to happen
name|bposs
operator|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"dn bpos len (still should be 3):"
operator|+
name|bposs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"should've registered with three namenodes"
argument_list|,
literal|3
argument_list|,
name|bposs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testClusterIdMismatchAtStartupWithHA ()
specifier|public
name|void
name|testClusterIdMismatchAtStartupWithHA
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSNNTopology
name|top
init|=
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
literal|"ns1"
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
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns2"
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn2"
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|"bad-cid"
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
literal|"nn3"
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|"bad-cid"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|top
operator|.
name|setFederation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|top
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// let the initialization be complete
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
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
name|assertTrue
argument_list|(
literal|"Datanode should be running"
argument_list|,
name|dn
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Only one BPOfferService should be running"
argument_list|,
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
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDNWithInvalidStorageWithHA ()
specifier|public
name|void
name|testDNWithInvalidStorageWithHA
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSNNTopology
name|top
init|=
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
literal|"ns1"
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
operator|.
name|setClusterId
argument_list|(
literal|"cluster-1"
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
operator|.
name|setClusterId
argument_list|(
literal|"cluster-1"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|top
operator|.
name|setFederation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|top
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// let the initialization be complete
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
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
name|assertTrue
argument_list|(
literal|"Datanode should be running"
argument_list|,
name|dn
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BPOfferService should be running"
argument_list|,
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
name|DataNodeProperties
name|dnProp
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|1
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|Configuration
name|nn1
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Configuration
name|nn2
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// setting up invalid cluster
name|StartupOption
operator|.
name|FORMAT
operator|.
name|setClusterId
argument_list|(
literal|"cluster-2"
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|nn1
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|copyNameDirs
argument_list|(
name|FSNamesystem
operator|.
name|getNamespaceDirs
argument_list|(
name|nn1
argument_list|)
argument_list|,
name|FSNamesystem
operator|.
name|getNamespaceDirs
argument_list|(
name|nn2
argument_list|)
argument_list|,
name|nn2
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnProp
argument_list|)
expr_stmt|;
comment|// let the initialization be complete
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|dn
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Datanode should have shutdown as only service failed"
argument_list|,
name|dn
operator|.
name|isDatanodeUp
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMiniDFSClusterWithMultipleNN ()
specifier|public
name|void
name|testMiniDFSClusterWithMultipleNN
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// start Federated cluster and add a node.
name|MiniDFSCluster
name|cluster
init|=
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
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// add a node
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"(1)Should be 2 namenodes"
argument_list|,
literal|2
argument_list|,
name|cluster
operator|.
name|getNumNameNodes
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"(1)Should be 3 namenodes"
argument_list|,
literal|3
argument_list|,
name|cluster
operator|.
name|getNumNameNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to add NN to cluster:"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// 2. start with Federation flag set
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
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
name|MiniDFSNNTopology
operator|.
name|simpleFederatedTopology
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"(2)Should be 1 namenodes"
argument_list|,
literal|1
argument_list|,
name|cluster
operator|.
name|getNumNameNodes
argument_list|()
argument_list|)
expr_stmt|;
comment|// add a node
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"(2)Should be 2 namenodes"
argument_list|,
literal|2
argument_list|,
name|cluster
operator|.
name|getNumNameNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Failed to add NN to cluster:"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// 3. start non-federated
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
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
name|build
argument_list|()
expr_stmt|;
comment|// add a node
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"(2)Should be 1 namenodes"
argument_list|,
literal|1
argument_list|,
name|cluster
operator|.
name|getNumNameNodes
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addNameNode
argument_list|(
name|conf
argument_list|,
literal|9929
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"shouldn't be able to add another NN to non federated cluster"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// correct
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"cannot add namenode"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"(3)Should be 1 namenodes"
argument_list|,
literal|1
argument_list|,
name|cluster
operator|.
name|getNumNameNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

