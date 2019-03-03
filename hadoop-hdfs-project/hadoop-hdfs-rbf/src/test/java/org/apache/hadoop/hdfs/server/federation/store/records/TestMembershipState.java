begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|federation
operator|.
name|store
operator|.
name|records
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
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
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
name|federation
operator|.
name|store
operator|.
name|driver
operator|.
name|StateStoreSerializer
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
comment|/**  * Test the Membership State records.  */
end_comment

begin_class
DECL|class|TestMembershipState
specifier|public
class|class
name|TestMembershipState
block|{
DECL|field|ROUTER
specifier|private
specifier|static
specifier|final
name|String
name|ROUTER
init|=
literal|"router"
decl_stmt|;
DECL|field|NAMESERVICE
specifier|private
specifier|static
specifier|final
name|String
name|NAMESERVICE
init|=
literal|"nameservice"
decl_stmt|;
DECL|field|NAMENODE
specifier|private
specifier|static
specifier|final
name|String
name|NAMENODE
init|=
literal|"namenode"
decl_stmt|;
DECL|field|CLUSTER_ID
specifier|private
specifier|static
specifier|final
name|String
name|CLUSTER_ID
init|=
literal|"cluster"
decl_stmt|;
DECL|field|BLOCKPOOL_ID
specifier|private
specifier|static
specifier|final
name|String
name|BLOCKPOOL_ID
init|=
literal|"blockpool"
decl_stmt|;
DECL|field|RPC_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|RPC_ADDRESS
init|=
literal|"rpcaddress"
decl_stmt|;
DECL|field|SERVICE_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_ADDRESS
init|=
literal|"serviceaddress"
decl_stmt|;
DECL|field|LIFELINE_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|LIFELINE_ADDRESS
init|=
literal|"lifelineaddress"
decl_stmt|;
DECL|field|WEB_ADDRESS
specifier|private
specifier|static
specifier|final
name|String
name|WEB_ADDRESS
init|=
literal|"webaddress"
decl_stmt|;
DECL|field|SAFE_MODE
specifier|private
specifier|static
specifier|final
name|boolean
name|SAFE_MODE
init|=
literal|false
decl_stmt|;
DECL|field|DATE_CREATED
specifier|private
specifier|static
specifier|final
name|long
name|DATE_CREATED
init|=
literal|100
decl_stmt|;
DECL|field|DATE_MODIFIED
specifier|private
specifier|static
specifier|final
name|long
name|DATE_MODIFIED
init|=
literal|200
decl_stmt|;
DECL|field|NUM_BLOCKS
specifier|private
specifier|static
specifier|final
name|long
name|NUM_BLOCKS
init|=
literal|300
decl_stmt|;
DECL|field|NUM_FILES
specifier|private
specifier|static
specifier|final
name|long
name|NUM_FILES
init|=
literal|400
decl_stmt|;
DECL|field|NUM_DEAD
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DEAD
init|=
literal|500
decl_stmt|;
DECL|field|NUM_STALE
specifier|private
specifier|static
specifier|final
name|int
name|NUM_STALE
init|=
literal|550
decl_stmt|;
DECL|field|NUM_ACTIVE
specifier|private
specifier|static
specifier|final
name|int
name|NUM_ACTIVE
init|=
literal|600
decl_stmt|;
DECL|field|NUM_DECOM
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DECOM
init|=
literal|700
decl_stmt|;
DECL|field|NUM_DECOM_ACTIVE
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DECOM_ACTIVE
init|=
literal|800
decl_stmt|;
DECL|field|NUM_DECOM_DEAD
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DECOM_DEAD
init|=
literal|900
decl_stmt|;
DECL|field|NUM_MAIN_LIVE
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MAIN_LIVE
init|=
literal|151
decl_stmt|;
DECL|field|NUM_MAIN_DEAD
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MAIN_DEAD
init|=
literal|303
decl_stmt|;
DECL|field|NUM_ENTER_MAIN
specifier|private
specifier|static
specifier|final
name|int
name|NUM_ENTER_MAIN
init|=
literal|144
decl_stmt|;
DECL|field|NUM_BLOCK_MISSING
specifier|private
specifier|static
specifier|final
name|long
name|NUM_BLOCK_MISSING
init|=
literal|1000
decl_stmt|;
DECL|field|TOTAL_SPACE
specifier|private
specifier|static
specifier|final
name|long
name|TOTAL_SPACE
init|=
literal|1100
decl_stmt|;
DECL|field|AVAILABLE_SPACE
specifier|private
specifier|static
specifier|final
name|long
name|AVAILABLE_SPACE
init|=
literal|1200
decl_stmt|;
DECL|field|STATE
specifier|private
specifier|static
specifier|final
name|FederationNamenodeServiceState
name|STATE
init|=
name|FederationNamenodeServiceState
operator|.
name|ACTIVE
decl_stmt|;
DECL|method|createRecord ()
specifier|private
name|MembershipState
name|createRecord
parameter_list|()
throws|throws
name|IOException
block|{
name|MembershipState
name|record
init|=
name|MembershipState
operator|.
name|newInstance
argument_list|(
name|ROUTER
argument_list|,
name|NAMESERVICE
argument_list|,
name|NAMENODE
argument_list|,
name|CLUSTER_ID
argument_list|,
name|BLOCKPOOL_ID
argument_list|,
name|RPC_ADDRESS
argument_list|,
name|SERVICE_ADDRESS
argument_list|,
name|LIFELINE_ADDRESS
argument_list|,
name|WEB_ADDRESS
argument_list|,
name|STATE
argument_list|,
name|SAFE_MODE
argument_list|)
decl_stmt|;
name|record
operator|.
name|setDateCreated
argument_list|(
name|DATE_CREATED
argument_list|)
expr_stmt|;
name|record
operator|.
name|setDateModified
argument_list|(
name|DATE_MODIFIED
argument_list|)
expr_stmt|;
name|MembershipStats
name|stats
init|=
name|MembershipStats
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|stats
operator|.
name|setNumOfBlocks
argument_list|(
name|NUM_BLOCKS
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfFiles
argument_list|(
name|NUM_FILES
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfActiveDatanodes
argument_list|(
name|NUM_ACTIVE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDeadDatanodes
argument_list|(
name|NUM_DEAD
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfStaleDatanodes
argument_list|(
name|NUM_STALE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDecommissioningDatanodes
argument_list|(
name|NUM_DECOM
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDecomActiveDatanodes
argument_list|(
name|NUM_DECOM_ACTIVE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfDecomDeadDatanodes
argument_list|(
name|NUM_DECOM_DEAD
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfInMaintenanceLiveDataNodes
argument_list|(
name|NUM_MAIN_LIVE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfInMaintenanceDeadDataNodes
argument_list|(
name|NUM_MAIN_DEAD
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfEnteringMaintenanceDataNodes
argument_list|(
name|NUM_ENTER_MAIN
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setNumOfBlocksMissing
argument_list|(
name|NUM_BLOCK_MISSING
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setTotalSpace
argument_list|(
name|TOTAL_SPACE
argument_list|)
expr_stmt|;
name|stats
operator|.
name|setAvailableSpace
argument_list|(
name|AVAILABLE_SPACE
argument_list|)
expr_stmt|;
name|record
operator|.
name|setStats
argument_list|(
name|stats
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
DECL|method|validateRecord (MembershipState record)
specifier|private
name|void
name|validateRecord
parameter_list|(
name|MembershipState
name|record
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|ROUTER
argument_list|,
name|record
operator|.
name|getRouterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NAMESERVICE
argument_list|,
name|record
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER_ID
argument_list|,
name|record
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|BLOCKPOOL_ID
argument_list|,
name|record
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RPC_ADDRESS
argument_list|,
name|record
operator|.
name|getRpcAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|WEB_ADDRESS
argument_list|,
name|record
operator|.
name|getWebAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
argument_list|,
name|record
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SAFE_MODE
argument_list|,
name|record
operator|.
name|getIsSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_CREATED
argument_list|,
name|record
operator|.
name|getDateCreated
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DATE_MODIFIED
argument_list|,
name|record
operator|.
name|getDateModified
argument_list|()
argument_list|)
expr_stmt|;
name|MembershipStats
name|stats
init|=
name|record
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_BLOCKS
argument_list|,
name|stats
operator|.
name|getNumOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_FILES
argument_list|,
name|stats
operator|.
name|getNumOfFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_ACTIVE
argument_list|,
name|stats
operator|.
name|getNumOfActiveDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DEAD
argument_list|,
name|stats
operator|.
name|getNumOfDeadDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_STALE
argument_list|,
name|stats
operator|.
name|getNumOfStaleDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DECOM
argument_list|,
name|stats
operator|.
name|getNumOfDecommissioningDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DECOM_ACTIVE
argument_list|,
name|stats
operator|.
name|getNumOfDecomActiveDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DECOM_DEAD
argument_list|,
name|stats
operator|.
name|getNumOfDecomDeadDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_MAIN_LIVE
argument_list|,
name|stats
operator|.
name|getNumOfInMaintenanceLiveDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_MAIN_DEAD
argument_list|,
name|stats
operator|.
name|getNumOfInMaintenanceDeadDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_ENTER_MAIN
argument_list|,
name|stats
operator|.
name|getNumOfEnteringMaintenanceDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TOTAL_SPACE
argument_list|,
name|stats
operator|.
name|getTotalSpace
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AVAILABLE_SPACE
argument_list|,
name|stats
operator|.
name|getAvailableSpace
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetterSetter ()
specifier|public
name|void
name|testGetterSetter
parameter_list|()
throws|throws
name|IOException
block|{
name|MembershipState
name|record
init|=
name|createRecord
argument_list|()
decl_stmt|;
name|validateRecord
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialization ()
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|MembershipState
name|record
init|=
name|createRecord
argument_list|()
decl_stmt|;
name|StateStoreSerializer
name|serializer
init|=
name|StateStoreSerializer
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|String
name|serializedString
init|=
name|serializer
operator|.
name|serializeString
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|MembershipState
name|newRecord
init|=
name|serializer
operator|.
name|deserialize
argument_list|(
name|serializedString
argument_list|,
name|MembershipState
operator|.
name|class
argument_list|)
decl_stmt|;
name|validateRecord
argument_list|(
name|newRecord
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

