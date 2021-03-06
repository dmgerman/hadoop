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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ha
operator|.
name|ZKFCProtocol
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
name|ClientDatanodeProtocol
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
name|ClientProtocol
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
name|ReconfigurationProtocol
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
name|qjournal
operator|.
name|protocol
operator|.
name|InterQJournalProtocol
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocol
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
name|DatanodeLifelineProtocol
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
name|DatanodeProtocol
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
name|InterDatanodeProtocol
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
name|NamenodeProtocol
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
name|RefreshUserMappingsProtocol
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
name|authorize
operator|.
name|PolicyProvider
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
name|authorize
operator|.
name|RefreshAuthorizationPolicyProtocol
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
name|authorize
operator|.
name|Service
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
name|tools
operator|.
name|GetUserMappingsProtocol
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
name|ipc
operator|.
name|RefreshCallQueueProtocol
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
name|ipc
operator|.
name|GenericRefreshProtocol
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
name|tracing
operator|.
name|TraceAdminProtocol
import|;
end_import

begin_comment
comment|/**  * {@link PolicyProvider} for HDFS protocols.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HDFSPolicyProvider
specifier|public
class|class
name|HDFSPolicyProvider
extends|extends
name|PolicyProvider
block|{
DECL|field|hdfsServices
specifier|private
specifier|static
specifier|final
name|Service
index|[]
name|hdfsServices
init|=
operator|new
name|Service
index|[]
block|{
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_CLIENT_PROTOCOL_ACL
argument_list|,
name|ClientProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_CLIENT_DATANODE_PROTOCOL_ACL
argument_list|,
name|ClientDatanodeProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_DATANODE_PROTOCOL_ACL
argument_list|,
name|DatanodeProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_INTER_DATANODE_PROTOCOL_ACL
argument_list|,
name|InterDatanodeProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_NAMENODE_PROTOCOL_ACL
argument_list|,
name|NamenodeProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_QJOURNAL_SERVICE_PROTOCOL_ACL
argument_list|,
name|QJournalProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_INTERQJOURNAL_SERVICE_PROTOCOL_ACL
argument_list|,
name|InterQJournalProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_HA_SERVICE_PROTOCOL_ACL
argument_list|,
name|HAServiceProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|SECURITY_ZKFC_PROTOCOL_ACL
argument_list|,
name|ZKFCProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_POLICY
argument_list|,
name|RefreshAuthorizationPolicyProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_USER_MAPPINGS
argument_list|,
name|RefreshUserMappingsProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_GET_USER_MAPPINGS
argument_list|,
name|GetUserMappingsProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_REFRESH_CALLQUEUE
argument_list|,
name|RefreshCallQueueProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_GENERIC_REFRESH
argument_list|,
name|GenericRefreshProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_TRACING
argument_list|,
name|TraceAdminProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_DATANODE_LIFELINE
argument_list|,
name|DatanodeLifelineProtocol
operator|.
name|class
argument_list|)
block|,
operator|new
name|Service
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_AUTHORIZATION_RECONFIGURATION
argument_list|,
name|ReconfigurationProtocol
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|getServices ()
specifier|public
name|Service
index|[]
name|getServices
parameter_list|()
block|{
return|return
name|hdfsServices
return|;
block|}
block|}
end_class

end_unit

