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
name|org
operator|.
name|junit
operator|.
name|Test
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
name|*
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HadoopIllegalArgumentException
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
name|LocatedBlocks
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
name|LocatedBlock
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
name|ExtendedBlock
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
name|DatanodeInfo
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
name|BlockLocation
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
name|DFSConfigKeys
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestDFSUtil
specifier|public
class|class
name|TestDFSUtil
block|{
comment|/**    * Test conversion of LocatedBlock to BlockLocation    */
annotation|@
name|Test
DECL|method|testLocatedBlocks2Locations ()
specifier|public
name|void
name|testLocatedBlocks2Locations
parameter_list|()
block|{
name|DatanodeInfo
name|d
init|=
operator|new
name|DatanodeInfo
argument_list|()
decl_stmt|;
name|DatanodeInfo
index|[]
name|ds
init|=
operator|new
name|DatanodeInfo
index|[
literal|1
index|]
decl_stmt|;
name|ds
index|[
literal|0
index|]
operator|=
name|d
expr_stmt|;
comment|// ok
name|ExtendedBlock
name|b1
init|=
operator|new
name|ExtendedBlock
argument_list|(
literal|"bpid"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|LocatedBlock
name|l1
init|=
operator|new
name|LocatedBlock
argument_list|(
name|b1
argument_list|,
name|ds
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// corrupt
name|ExtendedBlock
name|b2
init|=
operator|new
name|ExtendedBlock
argument_list|(
literal|"bpid"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|LocatedBlock
name|l2
init|=
operator|new
name|LocatedBlock
argument_list|(
name|b2
argument_list|,
name|ds
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|ls
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|l1
argument_list|,
name|l2
argument_list|)
decl_stmt|;
name|LocatedBlocks
name|lbs
init|=
operator|new
name|LocatedBlocks
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|,
name|ls
argument_list|,
name|l2
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|BlockLocation
index|[]
name|bs
init|=
name|DFSUtil
operator|.
name|locatedBlocks2Locations
argument_list|(
name|lbs
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected 2 blocks but got "
operator|+
name|bs
operator|.
name|length
argument_list|,
name|bs
operator|.
name|length
operator|==
literal|2
argument_list|)
expr_stmt|;
name|int
name|corruptCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BlockLocation
name|b
range|:
name|bs
control|)
block|{
if|if
condition|(
name|b
operator|.
name|isCorrupt
argument_list|()
condition|)
block|{
name|corruptCount
operator|++
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"expected 1 corrupt files but got "
operator|+
name|corruptCount
argument_list|,
name|corruptCount
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// test an empty location
name|bs
operator|=
name|DFSUtil
operator|.
name|locatedBlocks2Locations
argument_list|(
operator|new
name|LocatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|setupAddress (String key)
specifier|private
name|Configuration
name|setupAddress
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|getNameServiceIdKey
argument_list|(
name|key
argument_list|,
literal|"nn1"
argument_list|)
argument_list|,
literal|"localhost:9000"
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Test {@link DFSUtil#getNamenodeNameServiceId(Configuration)} to ensure    * nameserviceId from the configuration returned    */
annotation|@
name|Test
DECL|method|getNameServiceId ()
specifier|public
name|void
name|getNameServiceId
parameter_list|()
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICE_ID
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nn1"
argument_list|,
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link DFSUtil#getNameNodeNameServiceId(Configuration)} to ensure    * nameserviceId for namenode is determined based on matching the address with    * local node's address    */
annotation|@
name|Test
DECL|method|getNameNodeNameServiceId ()
specifier|public
name|void
name|getNameNodeNameServiceId
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|setupAddress
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"nn1"
argument_list|,
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link DFSUtil#getBackupNameServiceId(Configuration)} to ensure    * nameserviceId for backup node is determined based on matching the address    * with local node's address    */
annotation|@
name|Test
DECL|method|getBackupNameServiceId ()
specifier|public
name|void
name|getBackupNameServiceId
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|setupAddress
argument_list|(
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"nn1"
argument_list|,
name|DFSUtil
operator|.
name|getBackupNameServiceId
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link DFSUtil#getSecondaryNameServiceId(Configuration)} to ensure    * nameserviceId for backup node is determined based on matching the address    * with local node's address    */
annotation|@
name|Test
DECL|method|getSecondaryNameServiceId ()
specifier|public
name|void
name|getSecondaryNameServiceId
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|setupAddress
argument_list|(
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"nn1"
argument_list|,
name|DFSUtil
operator|.
name|getSecondaryNameServiceId
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link DFSUtil#getNameServiceId(Configuration, String))} to ensure    * exception is thrown when multiple rpc addresses match the local node's    * address    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|HadoopIllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testGetNameServiceIdException ()
specifier|public
name|void
name|testGetNameServiceIdException
parameter_list|()
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
literal|"nn1,nn2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|getNameServiceIdKey
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"nn1"
argument_list|)
argument_list|,
literal|"localhost:9000"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|getNameServiceIdKey
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"nn2"
argument_list|)
argument_list|,
literal|"localhost:9001"
argument_list|)
expr_stmt|;
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception is not thrown"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test {@link DFSUtil#getNameServiceIds(Configuration)}    */
annotation|@
name|Test
DECL|method|testGetNameServiceIds ()
specifier|public
name|void
name|testGetNameServiceIds
parameter_list|()
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
literal|"nn1,nn2"
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|nameserviceIds
init|=
name|DFSUtil
operator|.
name|getNameServiceIds
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|nameserviceIds
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nameserviceIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nn1"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"nn2"
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for {@link DFSUtil#getNNServiceRpcAddresses(Configuration)}    * {@link DFSUtil#getNameServiceIdFromAddress(Configuration, InetSocketAddress, String...)    * (Configuration)}    */
annotation|@
name|Test
DECL|method|testMultipleNamenodes ()
specifier|public
name|void
name|testMultipleNamenodes
parameter_list|()
throws|throws
name|IOException
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
literal|"nn1,nn2"
argument_list|)
expr_stmt|;
comment|// Test - configured list of namenodes are returned
specifier|final
name|String
name|NN1_ADDRESS
init|=
literal|"localhost:9000"
decl_stmt|;
specifier|final
name|String
name|NN2_ADDRESS
init|=
literal|"localhost:9001"
decl_stmt|;
specifier|final
name|String
name|NN3_ADDRESS
init|=
literal|"localhost:9002"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|getNameServiceIdKey
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"nn1"
argument_list|)
argument_list|,
name|NN1_ADDRESS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|getNameServiceIdKey
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
literal|"nn2"
argument_list|)
argument_list|,
name|NN2_ADDRESS
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|nnAddresses
init|=
name|DFSUtil
operator|.
name|getNNServiceRpcAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nnAddresses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|InetSocketAddress
argument_list|>
name|iterator
init|=
name|nnAddresses
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"localhost"
argument_list|,
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9000
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|addr
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost"
argument_list|,
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9001
argument_list|,
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test - can look up nameservice ID from service address
name|checkNameServiceId
argument_list|(
name|conf
argument_list|,
name|NN1_ADDRESS
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|checkNameServiceId
argument_list|(
name|conf
argument_list|,
name|NN2_ADDRESS
argument_list|,
literal|"nn2"
argument_list|)
expr_stmt|;
name|checkNameServiceId
argument_list|(
name|conf
argument_list|,
name|NN3_ADDRESS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNameServiceId (Configuration conf, String addr, String expectedNameServiceId)
specifier|public
name|void
name|checkNameServiceId
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|addr
parameter_list|,
name|String
name|expectedNameServiceId
parameter_list|)
block|{
name|InetSocketAddress
name|s
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|String
name|nameserviceId
init|=
name|DFSUtil
operator|.
name|getNameServiceIdFromAddress
argument_list|(
name|conf
argument_list|,
name|s
argument_list|,
name|DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY
argument_list|,
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedNameServiceId
argument_list|,
name|nameserviceId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for    * {@link DFSUtil#isDefaultNamenodeAddress(Configuration, InetSocketAddress, String...)}    */
annotation|@
name|Test
DECL|method|testSingleNamenode ()
specifier|public
name|void
name|testSingleNamenode
parameter_list|()
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|DEFAULT_ADDRESS
init|=
literal|"localhost:9000"
decl_stmt|;
specifier|final
name|String
name|NN2_ADDRESS
init|=
literal|"localhost:9001"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|DEFAULT_ADDRESS
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|testAddress1
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|DEFAULT_ADDRESS
argument_list|)
decl_stmt|;
name|boolean
name|isDefault
init|=
name|DFSUtil
operator|.
name|isDefaultNamenodeAddress
argument_list|(
name|conf
argument_list|,
name|testAddress1
argument_list|,
name|DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY
argument_list|,
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|isDefault
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|testAddress2
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|NN2_ADDRESS
argument_list|)
decl_stmt|;
name|isDefault
operator|=
name|DFSUtil
operator|.
name|isDefaultNamenodeAddress
argument_list|(
name|conf
argument_list|,
name|testAddress2
argument_list|,
name|DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY
argument_list|,
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isDefault
argument_list|)
expr_stmt|;
block|}
comment|/** Tests to ensure default namenode is used as fallback */
annotation|@
name|Test
DECL|method|testDefaultNamenode ()
specifier|public
name|void
name|testDefaultNamenode
parameter_list|()
throws|throws
name|IOException
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|String
name|hdfs_default
init|=
literal|"hdfs://localhost:9999/"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|hdfs_default
argument_list|)
expr_stmt|;
comment|// If DFS_FEDERATION_NAMESERVICES is not set, verify that
comment|// default namenode address is returned.
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|addrList
init|=
name|DFSUtil
operator|.
name|getNNServiceRpcAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|addrList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9999
argument_list|,
name|addrList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure nameservice specific keys in the configuration are    * copied to generic keys when the namenode starts.    */
annotation|@
name|Test
DECL|method|testConfModification ()
specifier|public
name|void
name|testConfModification
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_FEDERATION_NAMESERVICE_ID
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nameserviceId
init|=
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Set the nameservice specific keys with nameserviceId in the config key
for|for
control|(
name|String
name|key
range|:
name|NameNode
operator|.
name|NAMESERVICE_SPECIFIC_KEYS
control|)
block|{
comment|// Note: value is same as the key
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|getNameServiceIdKey
argument_list|(
name|key
argument_list|,
name|nameserviceId
argument_list|)
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
comment|// Initialize generic keys from specific keys
name|NameNode
operator|.
name|initializeGenericKeys
argument_list|(
name|conf
argument_list|,
name|nameserviceId
argument_list|)
expr_stmt|;
comment|// Retrieve the keys without nameserviceId and Ensure generic keys are set
comment|// to the correct value
for|for
control|(
name|String
name|key
range|:
name|NameNode
operator|.
name|NAMESERVICE_SPECIFIC_KEYS
control|)
block|{
name|assertEquals
argument_list|(
name|key
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests for empty configuration, an exception is thrown from    * {@link DFSUtil#getNNServiceRpcAddresses(Configuration)}    * {@link DFSUtil#getBackupNodeAddresses(Configuration)}    * {@link DFSUtil#getSecondaryNameNodeAddresses(Configuration)}    */
annotation|@
name|Test
DECL|method|testEmptyConf ()
specifier|public
name|void
name|testEmptyConf
parameter_list|()
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|DFSUtil
operator|.
name|getNNServiceRpcAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException is not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{     }
try|try
block|{
name|DFSUtil
operator|.
name|getBackupNodeAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException is not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{     }
try|try
block|{
name|DFSUtil
operator|.
name|getSecondaryNameNodeAddresses
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IOException is not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
DECL|method|testGetServerInfo ()
specifier|public
name|void
name|testGetServerInfo
parameter_list|()
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|httpsport
init|=
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0.0.0.0:50470"
argument_list|,
name|httpsport
argument_list|)
expr_stmt|;
name|String
name|httpport
init|=
name|DFSUtil
operator|.
name|getInfoServer
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0.0.0.0:50070"
argument_list|,
name|httpport
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

