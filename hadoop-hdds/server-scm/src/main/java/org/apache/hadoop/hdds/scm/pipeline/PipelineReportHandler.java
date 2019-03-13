begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
package|package
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
package|;
end_package

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
name|Preconditions
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|DatanodeDetails
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReport
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
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReportsProto
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
name|chillmode
operator|.
name|SCMChillModeManager
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
name|events
operator|.
name|SCMEvents
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
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
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
name|server
operator|.
name|events
operator|.
name|EventHandler
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
name|server
operator|.
name|events
operator|.
name|EventPublisher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Handles Pipeline Reports from datanode.  */
end_comment

begin_class
DECL|class|PipelineReportHandler
specifier|public
class|class
name|PipelineReportHandler
implements|implements
name|EventHandler
argument_list|<
name|PipelineReportFromDatanode
argument_list|>
block|{
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PipelineReportHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pipelineManager
specifier|private
specifier|final
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|scmChillModeManager
specifier|private
specifier|final
name|SCMChillModeManager
name|scmChillModeManager
decl_stmt|;
DECL|field|pipelineAvailabilityCheck
specifier|private
specifier|final
name|boolean
name|pipelineAvailabilityCheck
decl_stmt|;
DECL|method|PipelineReportHandler (SCMChillModeManager scmChillModeManager, PipelineManager pipelineManager, Configuration conf)
specifier|public
name|PipelineReportHandler
parameter_list|(
name|SCMChillModeManager
name|scmChillModeManager
parameter_list|,
name|PipelineManager
name|pipelineManager
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipelineManager
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scmChillModeManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmChillModeManager
operator|=
name|scmChillModeManager
expr_stmt|;
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|pipelineAvailabilityCheck
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK
argument_list|,
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK_DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (PipelineReportFromDatanode pipelineReportFromDatanode, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|PipelineReportFromDatanode
name|pipelineReportFromDatanode
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pipelineReportFromDatanode
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|dn
init|=
name|pipelineReportFromDatanode
operator|.
name|getDatanodeDetails
argument_list|()
decl_stmt|;
name|PipelineReportsProto
name|pipelineReport
init|=
name|pipelineReportFromDatanode
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dn
argument_list|,
literal|"Pipeline Report is "
operator|+
literal|"missing DatanodeDetails."
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"Processing pipeline report for dn: {}"
argument_list|,
name|dn
argument_list|)
expr_stmt|;
for|for
control|(
name|PipelineReport
name|report
range|:
name|pipelineReport
operator|.
name|getPipelineReportList
argument_list|()
control|)
block|{
try|try
block|{
name|processPipelineReport
argument_list|(
name|report
argument_list|,
name|dn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|error
argument_list|(
literal|"Could not process pipeline report={} from dn={} {}"
argument_list|,
name|report
argument_list|,
name|dn
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pipelineAvailabilityCheck
operator|&&
name|scmChillModeManager
operator|.
name|getInChillMode
argument_list|()
condition|)
block|{
name|publisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|PROCESSED_PIPELINE_REPORT
argument_list|,
name|pipelineReportFromDatanode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processPipelineReport (PipelineReport report, DatanodeDetails dn)
specifier|private
name|void
name|processPipelineReport
parameter_list|(
name|PipelineReport
name|report
parameter_list|,
name|DatanodeDetails
name|dn
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineID
name|pipelineID
init|=
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|report
operator|.
name|getPipelineID
argument_list|()
argument_list|)
decl_stmt|;
name|Pipeline
name|pipeline
decl_stmt|;
try|try
block|{
name|pipeline
operator|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PipelineNotFoundException
name|e
parameter_list|)
block|{
name|RatisPipelineUtils
operator|.
name|destroyPipeline
argument_list|(
name|dn
argument_list|,
name|pipelineID
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|pipeline
operator|.
name|getPipelineState
argument_list|()
operator|==
name|Pipeline
operator|.
name|PipelineState
operator|.
name|ALLOCATED
condition|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Pipeline {} reported by {}"
argument_list|,
name|pipeline
operator|.
name|getId
argument_list|()
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|reportDatanode
argument_list|(
name|dn
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipeline
operator|.
name|isHealthy
argument_list|()
condition|)
block|{
comment|// if all the dns have reported, pipeline can be moved to OPEN state
name|pipelineManager
operator|.
name|openPipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// In OPEN state case just report the datanode
name|pipeline
operator|.
name|reportDatanode
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

