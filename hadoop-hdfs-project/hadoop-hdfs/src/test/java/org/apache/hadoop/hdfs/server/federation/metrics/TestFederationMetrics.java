begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
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
name|metrics
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
name|server
operator|.
name|federation
operator|.
name|FederationTestUtils
operator|.
name|getBean
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
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
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
name|collections
operator|.
name|ListUtils
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
name|router
operator|.
name|Router
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
name|records
operator|.
name|MembershipState
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
name|records
operator|.
name|MembershipStats
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
name|records
operator|.
name|MountTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
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
comment|/**  * Test the JMX interface for the {@link Router}.  */
end_comment

begin_class
DECL|class|TestFederationMetrics
specifier|public
class|class
name|TestFederationMetrics
extends|extends
name|TestMetricsBase
block|{
DECL|field|FEDERATION_BEAN
specifier|public
specifier|static
specifier|final
name|String
name|FEDERATION_BEAN
init|=
literal|"Hadoop:service=Router,name=FederationState"
decl_stmt|;
DECL|field|STATE_STORE_BEAN
specifier|public
specifier|static
specifier|final
name|String
name|STATE_STORE_BEAN
init|=
literal|"Hadoop:service=Router,name=StateStore"
decl_stmt|;
DECL|field|RPC_BEAN
specifier|public
specifier|static
specifier|final
name|String
name|RPC_BEAN
init|=
literal|"Hadoop:service=Router,name=FederationRPC"
decl_stmt|;
annotation|@
name|Test
DECL|method|testClusterStatsJMX ()
specifier|public
name|void
name|testClusterStatsJMX
parameter_list|()
throws|throws
name|MalformedObjectNameException
throws|,
name|IOException
block|{
name|FederationMBean
name|bean
init|=
name|getBean
argument_list|(
name|FEDERATION_BEAN
argument_list|,
name|FederationMBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|validateClusterStatsBean
argument_list|(
name|bean
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClusterStatsDataSource ()
specifier|public
name|void
name|testClusterStatsDataSource
parameter_list|()
throws|throws
name|IOException
block|{
name|FederationMetrics
name|metrics
init|=
name|getRouter
argument_list|()
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|validateClusterStatsBean
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMountTableStatsDataSource ()
specifier|public
name|void
name|testMountTableStatsDataSource
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSONException
block|{
name|FederationMetrics
name|metrics
init|=
name|getRouter
argument_list|()
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|String
name|jsonString
init|=
name|metrics
operator|.
name|getMountTable
argument_list|()
decl_stmt|;
name|JSONArray
name|jsonArray
init|=
operator|new
name|JSONArray
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|jsonArray
operator|.
name|length
argument_list|()
argument_list|,
name|getMockMountTable
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|match
init|=
literal|0
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
name|jsonArray
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|JSONObject
name|json
init|=
name|jsonArray
operator|.
name|getJSONObject
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|src
init|=
name|json
operator|.
name|getString
argument_list|(
literal|"sourcePath"
argument_list|)
decl_stmt|;
for|for
control|(
name|MountTable
name|entry
range|:
name|getMockMountTable
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getSourcePath
argument_list|()
operator|.
name|equals
argument_list|(
name|src
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|entry
operator|.
name|getDefaultLocation
argument_list|()
operator|.
name|getNameserviceId
argument_list|()
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"nameserviceId"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|getDefaultLocation
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNullAndNotEmpty
argument_list|(
name|json
operator|.
name|getString
argument_list|(
literal|"dateCreated"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNullAndNotEmpty
argument_list|(
name|json
operator|.
name|getString
argument_list|(
literal|"dateModified"
argument_list|)
argument_list|)
expr_stmt|;
name|match
operator|++
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
name|match
argument_list|,
name|getMockMountTable
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|findMockNamenode (String nsId, String nnId)
specifier|private
name|MembershipState
name|findMockNamenode
parameter_list|(
name|String
name|nsId
parameter_list|,
name|String
name|nnId
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|MembershipState
argument_list|>
name|namenodes
init|=
name|ListUtils
operator|.
name|union
argument_list|(
name|getActiveMemberships
argument_list|()
argument_list|,
name|getStandbyMemberships
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|MembershipState
name|nn
range|:
name|namenodes
control|)
block|{
if|if
condition|(
name|nn
operator|.
name|getNamenodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nnId
argument_list|)
operator|&&
name|nn
operator|.
name|getNameserviceId
argument_list|()
operator|.
name|equals
argument_list|(
name|nsId
argument_list|)
condition|)
block|{
return|return
name|nn
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Test
DECL|method|testNamenodeStatsDataSource ()
specifier|public
name|void
name|testNamenodeStatsDataSource
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSONException
block|{
name|FederationMetrics
name|metrics
init|=
name|getRouter
argument_list|()
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|String
name|jsonString
init|=
name|metrics
operator|.
name|getNamenodes
argument_list|()
decl_stmt|;
name|JSONObject
name|jsonObject
init|=
operator|new
name|JSONObject
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|keys
init|=
name|jsonObject
operator|.
name|keys
argument_list|()
decl_stmt|;
name|int
name|nnsFound
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// Validate each entry against our mocks
name|JSONObject
name|json
init|=
name|jsonObject
operator|.
name|getJSONObject
argument_list|(
operator|(
name|String
operator|)
name|keys
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nameserviceId
init|=
name|json
operator|.
name|getString
argument_list|(
literal|"nameserviceId"
argument_list|)
decl_stmt|;
name|String
name|namenodeId
init|=
name|json
operator|.
name|getString
argument_list|(
literal|"namenodeId"
argument_list|)
decl_stmt|;
name|MembershipState
name|mockEntry
init|=
name|this
operator|.
name|findMockNamenode
argument_list|(
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mockEntry
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getString
argument_list|(
literal|"state"
argument_list|)
argument_list|,
name|mockEntry
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|MembershipStats
name|stats
init|=
name|mockEntry
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfActiveDatanodes"
argument_list|)
argument_list|,
name|stats
operator|.
name|getNumOfActiveDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDeadDatanodes"
argument_list|)
argument_list|,
name|stats
operator|.
name|getNumOfDeadDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDecommissioningDatanodes"
argument_list|)
argument_list|,
name|stats
operator|.
name|getNumOfDecommissioningDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDecomActiveDatanodes"
argument_list|)
argument_list|,
name|stats
operator|.
name|getNumOfDecomActiveDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDecomDeadDatanodes"
argument_list|)
argument_list|,
name|stats
operator|.
name|getNumOfDecomDeadDatanodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfBlocks"
argument_list|)
argument_list|,
name|stats
operator|.
name|getNumOfBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getString
argument_list|(
literal|"rpcAddress"
argument_list|)
argument_list|,
name|mockEntry
operator|.
name|getRpcAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
operator|.
name|getString
argument_list|(
literal|"webAddress"
argument_list|)
argument_list|,
name|mockEntry
operator|.
name|getWebAddress
argument_list|()
argument_list|)
expr_stmt|;
name|nnsFound
operator|++
expr_stmt|;
block|}
comment|// Validate all memberships are present
name|assertEquals
argument_list|(
name|getActiveMemberships
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|getStandbyMemberships
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|nnsFound
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNameserviceStatsDataSource ()
specifier|public
name|void
name|testNameserviceStatsDataSource
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSONException
block|{
name|FederationMetrics
name|metrics
init|=
name|getRouter
argument_list|()
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|String
name|jsonString
init|=
name|metrics
operator|.
name|getNameservices
argument_list|()
decl_stmt|;
name|JSONObject
name|jsonObject
init|=
operator|new
name|JSONObject
argument_list|(
name|jsonString
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
argument_list|>
name|keys
init|=
name|jsonObject
operator|.
name|keys
argument_list|()
decl_stmt|;
name|int
name|nameservicesFound
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|JSONObject
name|json
init|=
name|jsonObject
operator|.
name|getJSONObject
argument_list|(
operator|(
name|String
operator|)
name|keys
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nameserviceId
init|=
name|json
operator|.
name|getString
argument_list|(
literal|"nameserviceId"
argument_list|)
decl_stmt|;
name|String
name|namenodeId
init|=
name|json
operator|.
name|getString
argument_list|(
literal|"namenodeId"
argument_list|)
decl_stmt|;
name|MembershipState
name|mockEntry
init|=
name|this
operator|.
name|findMockNamenode
argument_list|(
name|nameserviceId
argument_list|,
name|namenodeId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mockEntry
argument_list|)
expr_stmt|;
comment|// NS should report the active NN
name|assertEquals
argument_list|(
name|mockEntry
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"state"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ACTIVE"
argument_list|,
name|json
operator|.
name|getString
argument_list|(
literal|"state"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Stats in the NS should reflect the stats for the most active NN
name|MembershipStats
name|stats
init|=
name|mockEntry
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getNumOfFiles
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfFiles"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getTotalSpace
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"totalSpace"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getAvailableSpace
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"availableSpace"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getNumOfBlocksMissing
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfBlocksMissing"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getNumOfActiveDatanodes
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfActiveDatanodes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getNumOfDeadDatanodes
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDeadDatanodes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getNumOfDecommissioningDatanodes
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDecommissioningDatanodes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getNumOfDecomActiveDatanodes
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDecomActiveDatanodes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|getNumOfDecomDeadDatanodes
argument_list|()
argument_list|,
name|json
operator|.
name|getLong
argument_list|(
literal|"numOfDecomDeadDatanodes"
argument_list|)
argument_list|)
expr_stmt|;
name|nameservicesFound
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|getNameservices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|nameservicesFound
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotNullAndNotEmpty (String field)
specifier|private
name|void
name|assertNotNullAndNotEmpty
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|validateClusterStatsBean (FederationMBean bean)
specifier|private
name|void
name|validateClusterStatsBean
parameter_list|(
name|FederationMBean
name|bean
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Determine aggregates
name|long
name|numBlocks
init|=
literal|0
decl_stmt|;
name|long
name|numLive
init|=
literal|0
decl_stmt|;
name|long
name|numDead
init|=
literal|0
decl_stmt|;
name|long
name|numDecom
init|=
literal|0
decl_stmt|;
name|long
name|numDecomLive
init|=
literal|0
decl_stmt|;
name|long
name|numDecomDead
init|=
literal|0
decl_stmt|;
name|long
name|numFiles
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MembershipState
name|mock
range|:
name|getActiveMemberships
argument_list|()
control|)
block|{
name|MembershipStats
name|stats
init|=
name|mock
operator|.
name|getStats
argument_list|()
decl_stmt|;
name|numBlocks
operator|+=
name|stats
operator|.
name|getNumOfBlocks
argument_list|()
expr_stmt|;
name|numLive
operator|+=
name|stats
operator|.
name|getNumOfActiveDatanodes
argument_list|()
expr_stmt|;
name|numDead
operator|+=
name|stats
operator|.
name|getNumOfDeadDatanodes
argument_list|()
expr_stmt|;
name|numDecom
operator|+=
name|stats
operator|.
name|getNumOfDecommissioningDatanodes
argument_list|()
expr_stmt|;
name|numDecomLive
operator|+=
name|stats
operator|.
name|getNumOfDecomActiveDatanodes
argument_list|()
expr_stmt|;
name|numDecomDead
operator|+=
name|stats
operator|.
name|getNumOfDecomDeadDatanodes
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numBlocks
argument_list|,
name|bean
operator|.
name|getNumBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numLive
argument_list|,
name|bean
operator|.
name|getNumLiveNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDead
argument_list|,
name|bean
operator|.
name|getNumDeadNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDecom
argument_list|,
name|bean
operator|.
name|getNumDecommissioningNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDecomLive
argument_list|,
name|bean
operator|.
name|getNumDecomLiveNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDecomDead
argument_list|,
name|bean
operator|.
name|getNumDecomDeadNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numFiles
argument_list|,
name|bean
operator|.
name|getNumFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getActiveMemberships
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|getStandbyMemberships
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|bean
operator|.
name|getNumNamenodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getNameservices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|bean
operator|.
name|getNumNameservices
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bean
operator|.
name|getVersion
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bean
operator|.
name|getCompiledDate
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bean
operator|.
name|getCompileInfo
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bean
operator|.
name|getRouterStarted
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bean
operator|.
name|getHostAndPort
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

