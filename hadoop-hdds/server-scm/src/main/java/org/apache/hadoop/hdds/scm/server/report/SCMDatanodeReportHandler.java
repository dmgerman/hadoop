begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server.report
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
name|server
operator|.
name|report
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
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
name|Configurable
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
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
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

begin_comment
comment|/**  * Datanode Report handlers should implement this interface in order to get  * call back whenever the report is received from datanode.  *  * @param<T> Type of report the handler is interested in.  */
end_comment

begin_class
DECL|class|SCMDatanodeReportHandler
specifier|public
specifier|abstract
class|class
name|SCMDatanodeReportHandler
parameter_list|<
name|T
extends|extends
name|GeneratedMessage
parameter_list|>
implements|implements
name|Configurable
block|{
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
DECL|field|scm
specifier|private
name|StorageContainerManager
name|scm
decl_stmt|;
comment|/**    * Initializes SCMDatanodeReportHandler and associates it with the given    * StorageContainerManager instance.    *    * @param storageContainerManager StorageContainerManager instance to be    *                                associated with.    */
DECL|method|init (StorageContainerManager storageContainerManager)
specifier|public
name|void
name|init
parameter_list|(
name|StorageContainerManager
name|storageContainerManager
parameter_list|)
block|{
name|this
operator|.
name|scm
operator|=
name|storageContainerManager
expr_stmt|;
block|}
comment|/**    * Returns the associated StorageContainerManager instance. This will be    * used by the ReportHandler implementations.    *    * @return {@link StorageContainerManager}    */
DECL|method|getSCM ()
specifier|protected
name|StorageContainerManager
name|getSCM
parameter_list|()
block|{
return|return
name|scm
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/**    * Processes the report received from datanode. Each ReportHandler    * implementation is responsible for providing the logic to process the    * report it's interested in.    *    * @param datanodeDetails Datanode Information    * @param report Report to be processed    *    * @throws IOException In case of any exception    */
DECL|method|processReport (DatanodeDetails datanodeDetails, T report)
specifier|abstract
name|void
name|processReport
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|T
name|report
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

