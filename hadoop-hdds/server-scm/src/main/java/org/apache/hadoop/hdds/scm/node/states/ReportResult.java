begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node.states
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
name|node
operator|.
name|states
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A Container Report gets processsed by the Node2Container and returns the  * Report Result class.  */
end_comment

begin_class
DECL|class|ReportResult
specifier|public
class|class
name|ReportResult
block|{
DECL|field|status
specifier|private
name|Node2ContainerMap
operator|.
name|ReportStatus
name|status
decl_stmt|;
DECL|field|missingContainers
specifier|private
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|missingContainers
decl_stmt|;
DECL|field|newContainers
specifier|private
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|newContainers
decl_stmt|;
DECL|method|ReportResult (Node2ContainerMap.ReportStatus status, Set<ContainerID> missingContainers, Set<ContainerID> newContainers)
name|ReportResult
parameter_list|(
name|Node2ContainerMap
operator|.
name|ReportStatus
name|status
parameter_list|,
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|missingContainers
parameter_list|,
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|newContainers
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|missingContainers
operator|=
name|missingContainers
expr_stmt|;
name|this
operator|.
name|newContainers
operator|=
name|newContainers
expr_stmt|;
block|}
DECL|method|getStatus ()
specifier|public
name|Node2ContainerMap
operator|.
name|ReportStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|getMissingContainers ()
specifier|public
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getMissingContainers
parameter_list|()
block|{
return|return
name|missingContainers
return|;
block|}
DECL|method|getNewContainers ()
specifier|public
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|getNewContainers
parameter_list|()
block|{
return|return
name|newContainers
return|;
block|}
DECL|class|ReportResultBuilder
specifier|static
class|class
name|ReportResultBuilder
block|{
DECL|field|status
specifier|private
name|Node2ContainerMap
operator|.
name|ReportStatus
name|status
decl_stmt|;
DECL|field|missingContainers
specifier|private
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|missingContainers
decl_stmt|;
DECL|field|newContainers
specifier|private
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|newContainers
decl_stmt|;
DECL|method|newBuilder ()
specifier|static
name|ReportResultBuilder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|ReportResultBuilder
argument_list|()
return|;
block|}
DECL|method|setStatus ( Node2ContainerMap.ReportStatus newstatus)
specifier|public
name|ReportResultBuilder
name|setStatus
parameter_list|(
name|Node2ContainerMap
operator|.
name|ReportStatus
name|newstatus
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|newstatus
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMissingContainers ( Set<ContainerID> missingContainersLit)
specifier|public
name|ReportResultBuilder
name|setMissingContainers
parameter_list|(
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|missingContainersLit
parameter_list|)
block|{
name|this
operator|.
name|missingContainers
operator|=
name|missingContainersLit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNewContainers ( Set<ContainerID> newContainersList)
specifier|public
name|ReportResultBuilder
name|setNewContainers
parameter_list|(
name|Set
argument_list|<
name|ContainerID
argument_list|>
name|newContainersList
parameter_list|)
block|{
name|this
operator|.
name|newContainers
operator|=
name|newContainersList
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
name|ReportResult
name|build
parameter_list|()
block|{
return|return
operator|new
name|ReportResult
argument_list|(
name|status
argument_list|,
name|missingContainers
argument_list|,
name|newContainers
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

