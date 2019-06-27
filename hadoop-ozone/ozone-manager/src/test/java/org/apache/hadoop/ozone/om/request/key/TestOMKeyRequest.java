begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|request
operator|.
name|key
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|hdds
operator|.
name|client
operator|.
name|ContainerBlockID
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ExcludeList
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|Pipeline
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
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|PipelineID
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
name|hdds
operator|.
name|scm
operator|.
name|protocol
operator|.
name|ScmBlockLocationProtocol
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
name|ozone
operator|.
name|audit
operator|.
name|AuditLogger
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
name|ozone
operator|.
name|audit
operator|.
name|AuditMessage
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
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
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
name|ozone
operator|.
name|om
operator|.
name|OMMetadataManager
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
name|ozone
operator|.
name|om
operator|.
name|OMMetrics
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
name|ozone
operator|.
name|om
operator|.
name|OmMetadataManagerImpl
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
name|ozone
operator|.
name|om
operator|.
name|OzoneManager
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
name|ozone
operator|.
name|om
operator|.
name|ScmClient
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
name|ozone
operator|.
name|security
operator|.
name|OzoneBlockTokenSecretManager
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
name|Time
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Base test class for key request.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"visibilitymodifier"
argument_list|)
DECL|class|TestOMKeyRequest
specifier|public
class|class
name|TestOMKeyRequest
block|{
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|ozoneManager
specifier|protected
name|OzoneManager
name|ozoneManager
decl_stmt|;
DECL|field|omMetrics
specifier|protected
name|OMMetrics
name|omMetrics
decl_stmt|;
DECL|field|omMetadataManager
specifier|protected
name|OMMetadataManager
name|omMetadataManager
decl_stmt|;
DECL|field|auditLogger
specifier|protected
name|AuditLogger
name|auditLogger
decl_stmt|;
DECL|field|scmClient
specifier|protected
name|ScmClient
name|scmClient
decl_stmt|;
DECL|field|ozoneBlockTokenSecretManager
specifier|protected
name|OzoneBlockTokenSecretManager
name|ozoneBlockTokenSecretManager
decl_stmt|;
DECL|field|scmBlockLocationProtocol
specifier|protected
name|ScmBlockLocationProtocol
name|scmBlockLocationProtocol
decl_stmt|;
DECL|field|containerID
specifier|protected
specifier|final
name|long
name|containerID
init|=
literal|1000L
decl_stmt|;
DECL|field|localID
specifier|protected
specifier|final
name|long
name|localID
init|=
literal|100L
decl_stmt|;
DECL|field|volumeName
specifier|protected
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|protected
name|String
name|bucketName
decl_stmt|;
DECL|field|keyName
specifier|protected
name|String
name|keyName
decl_stmt|;
DECL|field|replicationType
specifier|protected
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
decl_stmt|;
DECL|field|replicationFactor
specifier|protected
name|HddsProtos
operator|.
name|ReplicationFactor
name|replicationFactor
decl_stmt|;
DECL|field|clientID
specifier|protected
name|long
name|clientID
decl_stmt|;
DECL|field|scmBlockSize
specifier|protected
name|long
name|scmBlockSize
init|=
literal|1000L
decl_stmt|;
DECL|field|dataSize
specifier|protected
name|long
name|dataSize
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|ozoneManager
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|OzoneManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|omMetrics
operator|=
name|OMMetrics
operator|.
name|create
argument_list|()
expr_stmt|;
name|OzoneConfiguration
name|ozoneConfiguration
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ozoneConfiguration
operator|.
name|set
argument_list|(
name|OMConfigKeys
operator|.
name|OZONE_OM_DB_DIRS
argument_list|,
name|folder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|omMetadataManager
operator|=
operator|new
name|OmMetadataManagerImpl
argument_list|(
name|ozoneConfiguration
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|omMetrics
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getMetadataManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|omMetadataManager
argument_list|)
expr_stmt|;
name|auditLogger
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AuditLogger
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getAuditLogger
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|auditLogger
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|auditLogger
argument_list|)
operator|.
name|logWrite
argument_list|(
name|any
argument_list|(
name|AuditMessage
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|scmClient
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ScmClient
operator|.
name|class
argument_list|)
expr_stmt|;
name|ozoneBlockTokenSecretManager
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|OzoneBlockTokenSecretManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|scmBlockLocationProtocol
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ScmBlockLocationProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getScmClient
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scmClient
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getBlockTokenSecretManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ozoneBlockTokenSecretManager
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getScmBlockSize
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scmBlockSize
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getPreallocateBlocksMax
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|isGrpcBlockTokenEnabled
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|ozoneManager
operator|.
name|getOMNodeId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scmClient
operator|.
name|getBlockClient
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scmBlockLocationProtocol
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|Pipeline
operator|.
name|newBuilder
argument_list|()
operator|.
name|setState
argument_list|(
name|Pipeline
operator|.
name|PipelineState
operator|.
name|OPEN
argument_list|)
operator|.
name|setId
argument_list|(
name|PipelineID
operator|.
name|randomId
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|)
operator|.
name|setFactor
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
operator|.
name|setNodes
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AllocatedBlock
name|allocatedBlock
init|=
operator|new
name|AllocatedBlock
operator|.
name|Builder
argument_list|()
operator|.
name|setContainerBlockID
argument_list|(
operator|new
name|ContainerBlockID
argument_list|(
name|containerID
argument_list|,
name|localID
argument_list|)
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|pipeline
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AllocatedBlock
argument_list|>
name|allocatedBlocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|allocatedBlocks
operator|.
name|add
argument_list|(
name|allocatedBlock
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scmBlockLocationProtocol
operator|.
name|allocateBlock
argument_list|(
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|any
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|ExcludeList
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|allocatedBlocks
argument_list|)
expr_stmt|;
name|volumeName
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|bucketName
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|keyName
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|replicationFactor
operator|=
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
name|replicationType
operator|=
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
expr_stmt|;
name|clientID
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
name|dataSize
operator|=
literal|1000L
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|omMetrics
operator|.
name|unRegister
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|framework
argument_list|()
operator|.
name|clearInlineMocks
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

