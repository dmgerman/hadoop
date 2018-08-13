begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.common.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|api
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Container
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerState
import|;
end_import

begin_comment
comment|/**  * Status of component of training job  */
end_comment

begin_class
DECL|class|JobComponentStatus
specifier|public
class|class
name|JobComponentStatus
block|{
DECL|field|compName
specifier|private
name|String
name|compName
decl_stmt|;
DECL|field|numReadyContainers
specifier|private
name|long
name|numReadyContainers
init|=
literal|0
decl_stmt|;
DECL|field|numRunningButUnreadyContainers
specifier|private
name|long
name|numRunningButUnreadyContainers
init|=
literal|0
decl_stmt|;
DECL|field|totalAskedContainers
specifier|private
name|long
name|totalAskedContainers
decl_stmt|;
DECL|method|JobComponentStatus (String compName, long nReadyContainers, long nRunningButUnreadyContainers, long totalAskedContainers)
specifier|public
name|JobComponentStatus
parameter_list|(
name|String
name|compName
parameter_list|,
name|long
name|nReadyContainers
parameter_list|,
name|long
name|nRunningButUnreadyContainers
parameter_list|,
name|long
name|totalAskedContainers
parameter_list|)
block|{
name|this
operator|.
name|compName
operator|=
name|compName
expr_stmt|;
name|this
operator|.
name|numReadyContainers
operator|=
name|nReadyContainers
expr_stmt|;
name|this
operator|.
name|numRunningButUnreadyContainers
operator|=
name|nRunningButUnreadyContainers
expr_stmt|;
name|this
operator|.
name|totalAskedContainers
operator|=
name|totalAskedContainers
expr_stmt|;
block|}
DECL|method|getCompName ()
specifier|public
name|String
name|getCompName
parameter_list|()
block|{
return|return
name|compName
return|;
block|}
DECL|method|setCompName (String compName)
specifier|public
name|void
name|setCompName
parameter_list|(
name|String
name|compName
parameter_list|)
block|{
name|this
operator|.
name|compName
operator|=
name|compName
expr_stmt|;
block|}
DECL|method|getNumReadyContainers ()
specifier|public
name|long
name|getNumReadyContainers
parameter_list|()
block|{
return|return
name|numReadyContainers
return|;
block|}
DECL|method|setNumReadyContainers (long numReadyContainers)
specifier|public
name|void
name|setNumReadyContainers
parameter_list|(
name|long
name|numReadyContainers
parameter_list|)
block|{
name|this
operator|.
name|numReadyContainers
operator|=
name|numReadyContainers
expr_stmt|;
block|}
DECL|method|getNumRunningButUnreadyContainers ()
specifier|public
name|long
name|getNumRunningButUnreadyContainers
parameter_list|()
block|{
return|return
name|numRunningButUnreadyContainers
return|;
block|}
DECL|method|setNumRunningButUnreadyContainers ( long numRunningButUnreadyContainers)
specifier|public
name|void
name|setNumRunningButUnreadyContainers
parameter_list|(
name|long
name|numRunningButUnreadyContainers
parameter_list|)
block|{
name|this
operator|.
name|numRunningButUnreadyContainers
operator|=
name|numRunningButUnreadyContainers
expr_stmt|;
block|}
DECL|method|getTotalAskedContainers ()
specifier|public
name|long
name|getTotalAskedContainers
parameter_list|()
block|{
return|return
name|totalAskedContainers
return|;
block|}
DECL|method|setTotalAskedContainers (long totalAskedContainers)
specifier|public
name|void
name|setTotalAskedContainers
parameter_list|(
name|long
name|totalAskedContainers
parameter_list|)
block|{
name|this
operator|.
name|totalAskedContainers
operator|=
name|totalAskedContainers
expr_stmt|;
block|}
block|}
end_class

end_unit

