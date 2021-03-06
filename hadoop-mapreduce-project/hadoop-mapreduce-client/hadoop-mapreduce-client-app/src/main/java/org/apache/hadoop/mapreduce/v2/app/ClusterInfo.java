begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|util
operator|.
name|Records
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"MapReduce"
argument_list|)
DECL|class|ClusterInfo
specifier|public
class|class
name|ClusterInfo
block|{
DECL|field|maxContainerCapability
specifier|private
name|Resource
name|maxContainerCapability
decl_stmt|;
DECL|method|ClusterInfo ()
specifier|public
name|ClusterInfo
parameter_list|()
block|{
name|this
operator|.
name|maxContainerCapability
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|ClusterInfo (Resource maxCapability)
specifier|public
name|ClusterInfo
parameter_list|(
name|Resource
name|maxCapability
parameter_list|)
block|{
name|this
operator|.
name|maxContainerCapability
operator|=
name|maxCapability
expr_stmt|;
block|}
DECL|method|getMaxContainerCapability ()
specifier|public
name|Resource
name|getMaxContainerCapability
parameter_list|()
block|{
return|return
name|maxContainerCapability
return|;
block|}
DECL|method|setMaxContainerCapability (Resource maxContainerCapability)
specifier|public
name|void
name|setMaxContainerCapability
parameter_list|(
name|Resource
name|maxContainerCapability
parameter_list|)
block|{
name|this
operator|.
name|maxContainerCapability
operator|=
name|maxContainerCapability
expr_stmt|;
block|}
block|}
end_class

end_unit

