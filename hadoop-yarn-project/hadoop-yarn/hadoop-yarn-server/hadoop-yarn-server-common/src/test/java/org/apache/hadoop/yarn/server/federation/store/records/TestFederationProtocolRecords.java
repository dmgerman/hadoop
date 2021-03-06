begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|BasePBImplRecordsTest
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|AddApplicationHomeSubClusterRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|AddApplicationHomeSubClusterResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|DeleteApplicationHomeSubClusterRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|DeleteApplicationHomeSubClusterResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetApplicationHomeSubClusterRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetApplicationHomeSubClusterResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetApplicationsHomeSubClusterRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetApplicationsHomeSubClusterResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClusterInfoRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClusterInfoResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClusterPoliciesConfigurationsRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClusterPoliciesConfigurationsResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClusterPolicyConfigurationRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClusterPolicyConfigurationResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClustersInfoRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|GetSubClustersInfoResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SetSubClusterPolicyConfigurationRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SetSubClusterPolicyConfigurationResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterDeregisterRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterDeregisterResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterHeartbeatRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterHeartbeatResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterIdProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterInfoProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterRegisterRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterRegisterResponseProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|UpdateApplicationHomeSubClusterRequestProto
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
name|yarn
operator|.
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|UpdateApplicationHomeSubClusterResponseProto
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|AddApplicationHomeSubClusterRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|AddApplicationHomeSubClusterResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|DeleteApplicationHomeSubClusterRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|DeleteApplicationHomeSubClusterResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetApplicationHomeSubClusterRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetApplicationHomeSubClusterResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetApplicationsHomeSubClusterRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetApplicationsHomeSubClusterResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClusterInfoRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClusterInfoResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClusterPoliciesConfigurationsRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClusterPoliciesConfigurationsResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClusterPolicyConfigurationRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClusterPolicyConfigurationResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClustersInfoRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetSubClustersInfoResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SetSubClusterPolicyConfigurationRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SetSubClusterPolicyConfigurationResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterDeregisterRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterDeregisterResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterHeartbeatRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterHeartbeatResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterIdPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterInfoPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterRegisterRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubClusterRegisterResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|UpdateApplicationHomeSubClusterRequestPBImpl
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|UpdateApplicationHomeSubClusterResponsePBImpl
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
name|yarn
operator|.
name|server
operator|.
name|records
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
comment|/**  * Test class for federation protocol records.  */
end_comment

begin_class
DECL|class|TestFederationProtocolRecords
specifier|public
class|class
name|TestFederationProtocolRecords
extends|extends
name|BasePBImplRecordsTest
block|{
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|generateByNewInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
expr_stmt|;
name|generateByNewInstance
argument_list|(
name|Version
operator|.
name|class
argument_list|)
expr_stmt|;
name|generateByNewInstance
argument_list|(
name|SubClusterId
operator|.
name|class
argument_list|)
expr_stmt|;
name|generateByNewInstance
argument_list|(
name|SubClusterInfo
operator|.
name|class
argument_list|)
expr_stmt|;
name|generateByNewInstance
argument_list|(
name|ApplicationHomeSubCluster
operator|.
name|class
argument_list|)
expr_stmt|;
name|generateByNewInstance
argument_list|(
name|SubClusterPolicyConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterId ()
specifier|public
name|void
name|testSubClusterId
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterIdPBImpl
operator|.
name|class
argument_list|,
name|SubClusterIdProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterInfo ()
specifier|public
name|void
name|testSubClusterInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterInfoPBImpl
operator|.
name|class
argument_list|,
name|SubClusterInfoProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterRegisterRequest ()
specifier|public
name|void
name|testSubClusterRegisterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterRegisterRequestPBImpl
operator|.
name|class
argument_list|,
name|SubClusterRegisterRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterRegisterResponse ()
specifier|public
name|void
name|testSubClusterRegisterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterRegisterResponsePBImpl
operator|.
name|class
argument_list|,
name|SubClusterRegisterResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterDeregisterRequest ()
specifier|public
name|void
name|testSubClusterDeregisterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterDeregisterRequestPBImpl
operator|.
name|class
argument_list|,
name|SubClusterDeregisterRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterDeregisterResponse ()
specifier|public
name|void
name|testSubClusterDeregisterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterDeregisterResponsePBImpl
operator|.
name|class
argument_list|,
name|SubClusterDeregisterResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterHeartbeatRequest ()
specifier|public
name|void
name|testSubClusterHeartbeatRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterHeartbeatRequestPBImpl
operator|.
name|class
argument_list|,
name|SubClusterHeartbeatRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubClusterHeartbeatResponse ()
specifier|public
name|void
name|testSubClusterHeartbeatResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SubClusterHeartbeatResponsePBImpl
operator|.
name|class
argument_list|,
name|SubClusterHeartbeatResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClusterRequest ()
specifier|public
name|void
name|testGetSubClusterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClusterInfoRequestPBImpl
operator|.
name|class
argument_list|,
name|GetSubClusterInfoRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClusterResponse ()
specifier|public
name|void
name|testGetSubClusterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClusterInfoResponsePBImpl
operator|.
name|class
argument_list|,
name|GetSubClusterInfoResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClustersInfoRequest ()
specifier|public
name|void
name|testGetSubClustersInfoRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClustersInfoRequestPBImpl
operator|.
name|class
argument_list|,
name|GetSubClustersInfoRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClustersInfoResponse ()
specifier|public
name|void
name|testGetSubClustersInfoResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClustersInfoResponsePBImpl
operator|.
name|class
argument_list|,
name|GetSubClustersInfoResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddApplicationHomeSubClusterRequest ()
specifier|public
name|void
name|testAddApplicationHomeSubClusterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|AddApplicationHomeSubClusterRequestPBImpl
operator|.
name|class
argument_list|,
name|AddApplicationHomeSubClusterRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddApplicationHomeSubClusterResponse ()
specifier|public
name|void
name|testAddApplicationHomeSubClusterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|AddApplicationHomeSubClusterResponsePBImpl
operator|.
name|class
argument_list|,
name|AddApplicationHomeSubClusterResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateApplicationHomeSubClusterRequest ()
specifier|public
name|void
name|testUpdateApplicationHomeSubClusterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|UpdateApplicationHomeSubClusterRequestPBImpl
operator|.
name|class
argument_list|,
name|UpdateApplicationHomeSubClusterRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateApplicationHomeSubClusterResponse ()
specifier|public
name|void
name|testUpdateApplicationHomeSubClusterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|UpdateApplicationHomeSubClusterResponsePBImpl
operator|.
name|class
argument_list|,
name|UpdateApplicationHomeSubClusterResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetApplicationHomeSubClusterRequest ()
specifier|public
name|void
name|testGetApplicationHomeSubClusterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetApplicationHomeSubClusterRequestPBImpl
operator|.
name|class
argument_list|,
name|GetApplicationHomeSubClusterRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetApplicationHomeSubClusterResponse ()
specifier|public
name|void
name|testGetApplicationHomeSubClusterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetApplicationHomeSubClusterResponsePBImpl
operator|.
name|class
argument_list|,
name|GetApplicationHomeSubClusterResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetApplicationsHomeSubClusterRequest ()
specifier|public
name|void
name|testGetApplicationsHomeSubClusterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetApplicationsHomeSubClusterRequestPBImpl
operator|.
name|class
argument_list|,
name|GetApplicationsHomeSubClusterRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetApplicationsHomeSubClusterResponse ()
specifier|public
name|void
name|testGetApplicationsHomeSubClusterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetApplicationsHomeSubClusterResponsePBImpl
operator|.
name|class
argument_list|,
name|GetApplicationsHomeSubClusterResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteApplicationHomeSubClusterRequest ()
specifier|public
name|void
name|testDeleteApplicationHomeSubClusterRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|DeleteApplicationHomeSubClusterRequestPBImpl
operator|.
name|class
argument_list|,
name|DeleteApplicationHomeSubClusterRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteApplicationHomeSubClusterResponse ()
specifier|public
name|void
name|testDeleteApplicationHomeSubClusterResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|DeleteApplicationHomeSubClusterResponsePBImpl
operator|.
name|class
argument_list|,
name|DeleteApplicationHomeSubClusterResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClusterPolicyConfigurationRequest ()
specifier|public
name|void
name|testGetSubClusterPolicyConfigurationRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClusterPolicyConfigurationRequestPBImpl
operator|.
name|class
argument_list|,
name|GetSubClusterPolicyConfigurationRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClusterPolicyConfigurationResponse ()
specifier|public
name|void
name|testGetSubClusterPolicyConfigurationResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClusterPolicyConfigurationResponsePBImpl
operator|.
name|class
argument_list|,
name|GetSubClusterPolicyConfigurationResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetSubClusterPolicyConfigurationRequest ()
specifier|public
name|void
name|testSetSubClusterPolicyConfigurationRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SetSubClusterPolicyConfigurationRequestPBImpl
operator|.
name|class
argument_list|,
name|SetSubClusterPolicyConfigurationRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetSubClusterPolicyConfigurationResponse ()
specifier|public
name|void
name|testSetSubClusterPolicyConfigurationResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|SetSubClusterPolicyConfigurationResponsePBImpl
operator|.
name|class
argument_list|,
name|SetSubClusterPolicyConfigurationResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClusterPoliciesConfigurationsRequest ()
specifier|public
name|void
name|testGetSubClusterPoliciesConfigurationsRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClusterPoliciesConfigurationsRequestPBImpl
operator|.
name|class
argument_list|,
name|GetSubClusterPoliciesConfigurationsRequestProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSubClusterPoliciesConfigurationsResponse ()
specifier|public
name|void
name|testGetSubClusterPoliciesConfigurationsResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|validatePBImplRecord
argument_list|(
name|GetSubClusterPoliciesConfigurationsResponsePBImpl
operator|.
name|class
argument_list|,
name|GetSubClusterPoliciesConfigurationsResponseProto
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

