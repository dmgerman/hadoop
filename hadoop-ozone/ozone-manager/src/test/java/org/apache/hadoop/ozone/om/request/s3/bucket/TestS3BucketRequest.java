begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.request.s3.bucket
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
name|s3
operator|.
name|bucket
package|;
end_package

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
name|ratis
operator|.
name|utils
operator|.
name|OzoneManagerDoubleBufferHelper
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
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Base test class for S3 Bucket request.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"visibilityModifier"
argument_list|)
DECL|class|TestS3BucketRequest
specifier|public
class|class
name|TestS3BucketRequest
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
comment|// Just setting ozoneManagerDoubleBuffer which does nothing.
DECL|field|ozoneManagerDoubleBufferHelper
specifier|protected
name|OzoneManagerDoubleBufferHelper
name|ozoneManagerDoubleBufferHelper
init|=
operator|(
parameter_list|(
name|response
parameter_list|,
name|transactionIndex
parameter_list|)
lambda|->
block|{
return|return
literal|null
return|;
block|}
operator|)
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

