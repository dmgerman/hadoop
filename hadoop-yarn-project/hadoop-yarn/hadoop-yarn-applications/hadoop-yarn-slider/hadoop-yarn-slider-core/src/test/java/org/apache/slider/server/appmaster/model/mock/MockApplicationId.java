begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.model.mock
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|model
operator|.
name|mock
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
name|records
operator|.
name|ApplicationId
import|;
end_import

begin_comment
comment|/**  * Mock app id.  */
end_comment

begin_class
DECL|class|MockApplicationId
specifier|public
class|class
name|MockApplicationId
extends|extends
name|ApplicationId
block|{
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|field|clusterTimestamp
specifier|private
name|long
name|clusterTimestamp
decl_stmt|;
DECL|method|MockApplicationId ()
specifier|public
name|MockApplicationId
parameter_list|()
block|{   }
DECL|method|MockApplicationId (int id)
specifier|public
name|MockApplicationId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|MockApplicationId (int id, long clusterTimestamp)
specifier|public
name|MockApplicationId
parameter_list|(
name|int
name|id
parameter_list|,
name|long
name|clusterTimestamp
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|clusterTimestamp
operator|=
name|clusterTimestamp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|setId (int id)
specifier|public
name|void
name|setId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getClusterTimestamp ()
specifier|public
name|long
name|getClusterTimestamp
parameter_list|()
block|{
return|return
name|clusterTimestamp
return|;
block|}
annotation|@
name|Override
DECL|method|setClusterTimestamp (long clusterTimestamp)
specifier|public
name|void
name|setClusterTimestamp
parameter_list|(
name|long
name|clusterTimestamp
parameter_list|)
block|{
name|this
operator|.
name|clusterTimestamp
operator|=
name|clusterTimestamp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build ()
specifier|public
name|void
name|build
parameter_list|()
block|{    }
block|}
end_class

end_unit

