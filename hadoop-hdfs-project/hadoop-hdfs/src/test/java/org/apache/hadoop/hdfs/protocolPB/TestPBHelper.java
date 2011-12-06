begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolPB
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|protocol
operator|.
name|DatanodeID
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|BlockKeyProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|BlockProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|BlockWithLocationsProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|BlocksWithLocationsProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|CheckpointCommandProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|CheckpointSignatureProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|DatanodeIDProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|ExportedBlockKeysProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|ExtendedBlockProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|NamenodeRegistrationProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|NamenodeRegistrationProto
operator|.
name|NamenodeRoleProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|RecoveringBlockProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|RemoteEditLogManifestProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|RemoteEditLogProto
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|StorageInfoProto
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockKey
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|ExportedBlockKeys
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
name|NamenodeRole
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
name|StorageInfo
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
name|CheckpointSignature
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
name|protocol
operator|.
name|BlockRecoveryCommand
operator|.
name|RecoveringBlock
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
name|protocol
operator|.
name|BlocksWithLocations
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
name|protocol
operator|.
name|BlocksWithLocations
operator|.
name|BlockWithLocations
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
name|protocol
operator|.
name|NamenodeRegistration
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
name|protocol
operator|.
name|RemoteEditLog
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
name|protocol
operator|.
name|RemoteEditLogManifest
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
name|io
operator|.
name|Text
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

begin_comment
comment|/**  * Tests for {@link PBHelper}  */
end_comment

begin_class
DECL|class|TestPBHelper
specifier|public
class|class
name|TestPBHelper
block|{
annotation|@
name|Test
DECL|method|testConvertNamenodeRole ()
specifier|public
name|void
name|testConvertNamenodeRole
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|NamenodeRoleProto
operator|.
name|BACKUP
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|NamenodeRole
operator|.
name|BACKUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NamenodeRoleProto
operator|.
name|CHECKPOINT
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|NamenodeRole
operator|.
name|CHECKPOINT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NamenodeRoleProto
operator|.
name|NAMENODE
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|NamenodeRole
operator|.
name|NAMENODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NamenodeRole
operator|.
name|BACKUP
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|NamenodeRoleProto
operator|.
name|BACKUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NamenodeRole
operator|.
name|CHECKPOINT
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|NamenodeRoleProto
operator|.
name|CHECKPOINT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NamenodeRole
operator|.
name|NAMENODE
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|NamenodeRoleProto
operator|.
name|NAMENODE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getStorageInfo ()
specifier|private
specifier|static
name|StorageInfo
name|getStorageInfo
parameter_list|()
block|{
return|return
operator|new
name|StorageInfo
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|"cid"
argument_list|,
literal|3
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testConvertStoragInfo ()
specifier|public
name|void
name|testConvertStoragInfo
parameter_list|()
block|{
name|StorageInfo
name|info
init|=
name|getStorageInfo
argument_list|()
decl_stmt|;
name|StorageInfoProto
name|infoProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|StorageInfo
name|info2
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|infoProto
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getClusterID
argument_list|()
argument_list|,
name|info2
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getCTime
argument_list|()
argument_list|,
name|info2
operator|.
name|getCTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getLayoutVersion
argument_list|()
argument_list|,
name|info2
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getNamespaceID
argument_list|()
argument_list|,
name|info2
operator|.
name|getNamespaceID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertNamenodeRegistration ()
specifier|public
name|void
name|testConvertNamenodeRegistration
parameter_list|()
block|{
name|StorageInfo
name|info
init|=
name|getStorageInfo
argument_list|()
decl_stmt|;
name|NamenodeRegistration
name|reg
init|=
operator|new
name|NamenodeRegistration
argument_list|(
literal|"address:999"
argument_list|,
literal|"http:1000"
argument_list|,
name|info
argument_list|,
name|NamenodeRole
operator|.
name|NAMENODE
argument_list|)
decl_stmt|;
name|NamenodeRegistrationProto
name|regProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|reg
argument_list|)
decl_stmt|;
name|NamenodeRegistration
name|reg2
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|regProto
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getAddress
argument_list|()
argument_list|,
name|reg2
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getClusterID
argument_list|()
argument_list|,
name|reg2
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getCTime
argument_list|()
argument_list|,
name|reg2
operator|.
name|getCTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getHttpAddress
argument_list|()
argument_list|,
name|reg2
operator|.
name|getHttpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getLayoutVersion
argument_list|()
argument_list|,
name|reg2
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getNamespaceID
argument_list|()
argument_list|,
name|reg2
operator|.
name|getNamespaceID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getRegistrationID
argument_list|()
argument_list|,
name|reg2
operator|.
name|getRegistrationID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getRole
argument_list|()
argument_list|,
name|reg2
operator|.
name|getRole
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reg
operator|.
name|getVersion
argument_list|()
argument_list|,
name|reg2
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertDatanodeID ()
specifier|public
name|void
name|testConvertDatanodeID
parameter_list|()
block|{
name|DatanodeID
name|dn
init|=
operator|new
name|DatanodeID
argument_list|(
literal|"node"
argument_list|,
literal|"sid"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|DatanodeIDProto
name|dnProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|DatanodeID
name|dn2
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|dnProto
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dn
operator|.
name|getHost
argument_list|()
argument_list|,
name|dn2
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|dn2
operator|.
name|getInfoPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn
operator|.
name|getIpcPort
argument_list|()
argument_list|,
name|dn2
operator|.
name|getIpcPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn
operator|.
name|getName
argument_list|()
argument_list|,
name|dn2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn
operator|.
name|getPort
argument_list|()
argument_list|,
name|dn2
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|dn2
operator|.
name|getStorageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertBlock ()
specifier|public
name|void
name|testConvertBlock
parameter_list|()
block|{
name|Block
name|b
init|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|BlockProto
name|bProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|Block
name|b2
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|bProto
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|b2
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlockWithLocations (int bid)
specifier|private
specifier|static
name|BlockWithLocations
name|getBlockWithLocations
parameter_list|(
name|int
name|bid
parameter_list|)
block|{
return|return
operator|new
name|BlockWithLocations
argument_list|(
operator|new
name|Block
argument_list|(
name|bid
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dn1"
block|,
literal|"dn2"
block|,
literal|"dn3"
block|}
argument_list|)
return|;
block|}
DECL|method|compare (BlockWithLocations locs1, BlockWithLocations locs2)
specifier|private
name|void
name|compare
parameter_list|(
name|BlockWithLocations
name|locs1
parameter_list|,
name|BlockWithLocations
name|locs2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|locs1
operator|.
name|getBlock
argument_list|()
argument_list|,
name|locs2
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|locs1
operator|.
name|getDatanodes
argument_list|()
argument_list|,
name|locs2
operator|.
name|getDatanodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertBlockWithLocations ()
specifier|public
name|void
name|testConvertBlockWithLocations
parameter_list|()
block|{
name|BlockWithLocations
name|locs
init|=
name|getBlockWithLocations
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockWithLocationsProto
name|locsProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|locs
argument_list|)
decl_stmt|;
name|BlockWithLocations
name|locs2
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|locsProto
argument_list|)
decl_stmt|;
name|compare
argument_list|(
name|locs
argument_list|,
name|locs2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertBlocksWithLocations ()
specifier|public
name|void
name|testConvertBlocksWithLocations
parameter_list|()
block|{
name|BlockWithLocations
index|[]
name|list
init|=
operator|new
name|BlockWithLocations
index|[]
block|{
name|getBlockWithLocations
argument_list|(
literal|1
argument_list|)
block|,
name|getBlockWithLocations
argument_list|(
literal|2
argument_list|)
block|}
decl_stmt|;
name|BlocksWithLocations
name|locs
init|=
operator|new
name|BlocksWithLocations
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|BlocksWithLocationsProto
name|locsProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|locs
argument_list|)
decl_stmt|;
name|BlocksWithLocations
name|locs2
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|locsProto
argument_list|)
decl_stmt|;
name|BlockWithLocations
index|[]
name|blocks
init|=
name|locs
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|BlockWithLocations
index|[]
name|blocks2
init|=
name|locs2
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|blocks
operator|.
name|length
argument_list|,
name|blocks2
operator|.
name|length
argument_list|)
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
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|compare
argument_list|(
name|blocks
index|[
name|i
index|]
argument_list|,
name|blocks2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getBlockKey (int keyId)
specifier|private
specifier|static
name|BlockKey
name|getBlockKey
parameter_list|(
name|int
name|keyId
parameter_list|)
block|{
return|return
operator|new
name|BlockKey
argument_list|(
name|keyId
argument_list|,
literal|10
argument_list|,
literal|"encodedKey"
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|compare (BlockKey k1, BlockKey k2)
specifier|private
name|void
name|compare
parameter_list|(
name|BlockKey
name|k1
parameter_list|,
name|BlockKey
name|k2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|k1
operator|.
name|getExpiryDate
argument_list|()
argument_list|,
name|k2
operator|.
name|getExpiryDate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|k1
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|k2
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|k1
operator|.
name|getEncodedKey
argument_list|()
argument_list|,
name|k2
operator|.
name|getEncodedKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertBlockKey ()
specifier|public
name|void
name|testConvertBlockKey
parameter_list|()
block|{
name|BlockKey
name|key
init|=
name|getBlockKey
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|BlockKeyProto
name|keyProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|BlockKey
name|key1
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|keyProto
argument_list|)
decl_stmt|;
name|compare
argument_list|(
name|key
argument_list|,
name|key1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertExportedBlockKeys ()
specifier|public
name|void
name|testConvertExportedBlockKeys
parameter_list|()
block|{
name|BlockKey
index|[]
name|keys
init|=
operator|new
name|BlockKey
index|[]
block|{
name|getBlockKey
argument_list|(
literal|2
argument_list|)
block|,
name|getBlockKey
argument_list|(
literal|3
argument_list|)
block|}
decl_stmt|;
name|ExportedBlockKeys
name|expKeys
init|=
operator|new
name|ExportedBlockKeys
argument_list|(
literal|true
argument_list|,
literal|9
argument_list|,
literal|10
argument_list|,
name|getBlockKey
argument_list|(
literal|1
argument_list|)
argument_list|,
name|keys
argument_list|)
decl_stmt|;
name|ExportedBlockKeysProto
name|expKeysProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|expKeys
argument_list|)
decl_stmt|;
name|ExportedBlockKeys
name|expKeys1
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|expKeysProto
argument_list|)
decl_stmt|;
name|BlockKey
index|[]
name|allKeys
init|=
name|expKeys
operator|.
name|getAllKeys
argument_list|()
decl_stmt|;
name|BlockKey
index|[]
name|allKeys1
init|=
name|expKeys1
operator|.
name|getAllKeys
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|allKeys
operator|.
name|length
argument_list|,
name|allKeys1
operator|.
name|length
argument_list|)
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
name|allKeys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|compare
argument_list|(
name|allKeys
index|[
name|i
index|]
argument_list|,
name|allKeys1
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|compare
argument_list|(
name|expKeys
operator|.
name|getCurrentKey
argument_list|()
argument_list|,
name|expKeys1
operator|.
name|getCurrentKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expKeys
operator|.
name|getKeyUpdateInterval
argument_list|()
argument_list|,
name|expKeys1
operator|.
name|getKeyUpdateInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expKeys
operator|.
name|getTokenLifetime
argument_list|()
argument_list|,
name|expKeys1
operator|.
name|getTokenLifetime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertCheckpointSignature ()
specifier|public
name|void
name|testConvertCheckpointSignature
parameter_list|()
block|{
name|CheckpointSignature
name|s
init|=
operator|new
name|CheckpointSignature
argument_list|(
name|getStorageInfo
argument_list|()
argument_list|,
literal|"bpid"
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|CheckpointSignatureProto
name|sProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|CheckpointSignature
name|s1
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|sProto
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|getBlockpoolID
argument_list|()
argument_list|,
name|s1
operator|.
name|getBlockpoolID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|getClusterID
argument_list|()
argument_list|,
name|s1
operator|.
name|getClusterID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|getCTime
argument_list|()
argument_list|,
name|s1
operator|.
name|getCTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|,
name|s1
operator|.
name|getCurSegmentTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|getLayoutVersion
argument_list|()
argument_list|,
name|s1
operator|.
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
argument_list|,
name|s1
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
operator|.
name|getNamespaceID
argument_list|()
argument_list|,
name|s1
operator|.
name|getNamespaceID
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (RemoteEditLog l1, RemoteEditLog l2)
specifier|private
specifier|static
name|void
name|compare
parameter_list|(
name|RemoteEditLog
name|l1
parameter_list|,
name|RemoteEditLog
name|l2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|l1
operator|.
name|getEndTxId
argument_list|()
argument_list|,
name|l2
operator|.
name|getEndTxId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|l1
operator|.
name|getStartTxId
argument_list|()
argument_list|,
name|l2
operator|.
name|getStartTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertRemoteEditLog ()
specifier|public
name|void
name|testConvertRemoteEditLog
parameter_list|()
block|{
name|RemoteEditLog
name|l
init|=
operator|new
name|RemoteEditLog
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|RemoteEditLogProto
name|lProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|l
argument_list|)
decl_stmt|;
name|RemoteEditLog
name|l1
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|lProto
argument_list|)
decl_stmt|;
name|compare
argument_list|(
name|l
argument_list|,
name|l1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertRemoteEditLogManifest ()
specifier|public
name|void
name|testConvertRemoteEditLogManifest
parameter_list|()
block|{
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|logs
init|=
operator|new
name|ArrayList
argument_list|<
name|RemoteEditLog
argument_list|>
argument_list|()
decl_stmt|;
name|logs
operator|.
name|add
argument_list|(
operator|new
name|RemoteEditLog
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|logs
operator|.
name|add
argument_list|(
operator|new
name|RemoteEditLog
argument_list|(
literal|11
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|RemoteEditLogManifest
name|m
init|=
operator|new
name|RemoteEditLogManifest
argument_list|(
name|logs
argument_list|)
decl_stmt|;
name|RemoteEditLogManifestProto
name|mProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|RemoteEditLogManifest
name|m1
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|mProto
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|logs1
init|=
name|m1
operator|.
name|getLogs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|logs
operator|.
name|size
argument_list|()
argument_list|,
name|logs1
operator|.
name|size
argument_list|()
argument_list|)
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
name|logs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|compare
argument_list|(
name|logs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|logs1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getExtendedBlock ()
specifier|public
name|ExtendedBlock
name|getExtendedBlock
parameter_list|()
block|{
return|return
operator|new
name|ExtendedBlock
argument_list|(
literal|"bpid"
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|2
argument_list|)
return|;
block|}
DECL|method|getDNInfo ()
specifier|public
name|DatanodeInfo
name|getDNInfo
parameter_list|()
block|{
return|return
operator|new
name|DatanodeInfo
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"node"
argument_list|,
literal|"sid"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
return|;
block|}
DECL|method|compare (DatanodeInfo dn1, DatanodeInfo dn2)
specifier|private
name|void
name|compare
parameter_list|(
name|DatanodeInfo
name|dn1
parameter_list|,
name|DatanodeInfo
name|dn2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getAdminState
argument_list|()
argument_list|,
name|dn2
operator|.
name|getAdminState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|,
name|dn2
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getBlockPoolUsedPercent
argument_list|()
argument_list|,
name|dn2
operator|.
name|getBlockPoolUsedPercent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|dn2
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getDatanodeReport
argument_list|()
argument_list|,
name|dn2
operator|.
name|getDatanodeReport
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
name|dn1
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getDfsUsedPercent
argument_list|()
argument_list|,
name|dn1
operator|.
name|getDfsUsedPercent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getHost
argument_list|()
argument_list|,
name|dn2
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getHostName
argument_list|()
argument_list|,
name|dn2
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|dn2
operator|.
name|getInfoPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getIpcPort
argument_list|()
argument_list|,
name|dn2
operator|.
name|getIpcPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getLastUpdate
argument_list|()
argument_list|,
name|dn2
operator|.
name|getLastUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getLevel
argument_list|()
argument_list|,
name|dn2
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dn1
operator|.
name|getNetworkLocation
argument_list|()
argument_list|,
name|dn2
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertExtendedBlock ()
specifier|public
name|void
name|testConvertExtendedBlock
parameter_list|()
block|{
name|ExtendedBlock
name|b
init|=
name|getExtendedBlock
argument_list|()
decl_stmt|;
name|ExtendedBlockProto
name|bProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|ExtendedBlock
name|b1
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|bProto
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|b1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertRecoveringBlock ()
specifier|public
name|void
name|testConvertRecoveringBlock
parameter_list|()
block|{
name|DatanodeInfo
index|[]
name|dnInfo
init|=
operator|new
name|DatanodeInfo
index|[]
block|{
name|getDNInfo
argument_list|()
block|,
name|getDNInfo
argument_list|()
block|}
decl_stmt|;
name|RecoveringBlock
name|b
init|=
operator|new
name|RecoveringBlock
argument_list|(
name|getExtendedBlock
argument_list|()
argument_list|,
name|dnInfo
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|RecoveringBlockProto
name|bProto
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|RecoveringBlock
name|b1
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|bProto
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|b
operator|.
name|getBlock
argument_list|()
argument_list|,
name|b1
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|DatanodeInfo
index|[]
name|dnInfo1
init|=
name|b1
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|dnInfo
operator|.
name|length
argument_list|,
name|dnInfo1
operator|.
name|length
argument_list|)
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
name|dnInfo
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|compare
argument_list|(
name|dnInfo
index|[
literal|0
index|]
argument_list|,
name|dnInfo1
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testConvertText ()
specifier|public
name|void
name|testConvertText
parameter_list|()
block|{
name|Text
name|t
init|=
operator|new
name|Text
argument_list|(
literal|"abc"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|t
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Text
name|t1
init|=
operator|new
name|Text
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|t
argument_list|,
name|t1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

