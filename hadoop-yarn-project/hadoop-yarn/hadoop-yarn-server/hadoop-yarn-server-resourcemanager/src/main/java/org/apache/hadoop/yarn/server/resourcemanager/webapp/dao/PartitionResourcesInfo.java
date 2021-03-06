begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_comment
comment|/**  * This class represents queue/user resource usage info for a given partition  */
end_comment

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|PartitionResourcesInfo
specifier|public
class|class
name|PartitionResourcesInfo
block|{
DECL|field|partitionName
specifier|private
name|String
name|partitionName
decl_stmt|;
DECL|field|used
specifier|private
name|ResourceInfo
name|used
init|=
operator|new
name|ResourceInfo
argument_list|()
decl_stmt|;
DECL|field|reserved
specifier|private
name|ResourceInfo
name|reserved
decl_stmt|;
DECL|field|pending
specifier|private
name|ResourceInfo
name|pending
decl_stmt|;
DECL|field|amUsed
specifier|private
name|ResourceInfo
name|amUsed
decl_stmt|;
DECL|field|amLimit
specifier|private
name|ResourceInfo
name|amLimit
init|=
operator|new
name|ResourceInfo
argument_list|()
decl_stmt|;
DECL|field|userAmLimit
specifier|private
name|ResourceInfo
name|userAmLimit
decl_stmt|;
DECL|method|PartitionResourcesInfo ()
specifier|public
name|PartitionResourcesInfo
parameter_list|()
block|{   }
DECL|method|PartitionResourcesInfo (String partitionName, ResourceInfo used, ResourceInfo reserved, ResourceInfo pending, ResourceInfo amResourceUsed, ResourceInfo amResourceLimit, ResourceInfo perUserAmResourceLimit)
specifier|public
name|PartitionResourcesInfo
parameter_list|(
name|String
name|partitionName
parameter_list|,
name|ResourceInfo
name|used
parameter_list|,
name|ResourceInfo
name|reserved
parameter_list|,
name|ResourceInfo
name|pending
parameter_list|,
name|ResourceInfo
name|amResourceUsed
parameter_list|,
name|ResourceInfo
name|amResourceLimit
parameter_list|,
name|ResourceInfo
name|perUserAmResourceLimit
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|partitionName
operator|=
name|partitionName
expr_stmt|;
name|this
operator|.
name|used
operator|=
name|used
expr_stmt|;
name|this
operator|.
name|reserved
operator|=
name|reserved
expr_stmt|;
name|this
operator|.
name|pending
operator|=
name|pending
expr_stmt|;
name|this
operator|.
name|amUsed
operator|=
name|amResourceUsed
expr_stmt|;
name|this
operator|.
name|amLimit
operator|=
name|amResourceLimit
expr_stmt|;
name|this
operator|.
name|userAmLimit
operator|=
name|perUserAmResourceLimit
expr_stmt|;
block|}
DECL|method|getPartitionName ()
specifier|public
name|String
name|getPartitionName
parameter_list|()
block|{
return|return
name|partitionName
return|;
block|}
DECL|method|setPartitionName (String partitionName)
specifier|public
name|void
name|setPartitionName
parameter_list|(
name|String
name|partitionName
parameter_list|)
block|{
name|this
operator|.
name|partitionName
operator|=
name|partitionName
expr_stmt|;
block|}
DECL|method|getUsed ()
specifier|public
name|ResourceInfo
name|getUsed
parameter_list|()
block|{
return|return
name|used
return|;
block|}
DECL|method|setUsed (ResourceInfo used)
specifier|public
name|void
name|setUsed
parameter_list|(
name|ResourceInfo
name|used
parameter_list|)
block|{
name|this
operator|.
name|used
operator|=
name|used
expr_stmt|;
block|}
DECL|method|getReserved ()
specifier|public
name|ResourceInfo
name|getReserved
parameter_list|()
block|{
return|return
name|reserved
return|;
block|}
DECL|method|setReserved (ResourceInfo reserved)
specifier|public
name|void
name|setReserved
parameter_list|(
name|ResourceInfo
name|reserved
parameter_list|)
block|{
name|this
operator|.
name|reserved
operator|=
name|reserved
expr_stmt|;
block|}
DECL|method|getPending ()
specifier|public
name|ResourceInfo
name|getPending
parameter_list|()
block|{
return|return
name|pending
return|;
block|}
DECL|method|setPending (ResourceInfo pending)
specifier|public
name|void
name|setPending
parameter_list|(
name|ResourceInfo
name|pending
parameter_list|)
block|{
name|this
operator|.
name|pending
operator|=
name|pending
expr_stmt|;
block|}
DECL|method|getAmUsed ()
specifier|public
name|ResourceInfo
name|getAmUsed
parameter_list|()
block|{
return|return
name|amUsed
return|;
block|}
DECL|method|setAmUsed (ResourceInfo amResourceUsed)
specifier|public
name|void
name|setAmUsed
parameter_list|(
name|ResourceInfo
name|amResourceUsed
parameter_list|)
block|{
name|this
operator|.
name|amUsed
operator|=
name|amResourceUsed
expr_stmt|;
block|}
DECL|method|getAMLimit ()
specifier|public
name|ResourceInfo
name|getAMLimit
parameter_list|()
block|{
return|return
name|amLimit
return|;
block|}
DECL|method|setAMLimit (ResourceInfo amLimit)
specifier|public
name|void
name|setAMLimit
parameter_list|(
name|ResourceInfo
name|amLimit
parameter_list|)
block|{
name|this
operator|.
name|amLimit
operator|=
name|amLimit
expr_stmt|;
block|}
comment|/**    * @return the userAmLimit    */
DECL|method|getUserAmLimit ()
specifier|public
name|ResourceInfo
name|getUserAmLimit
parameter_list|()
block|{
return|return
name|userAmLimit
return|;
block|}
comment|/**    * @param userAmLimit the userAmLimit to set    */
DECL|method|setUserAmLimit (ResourceInfo userAmLimit)
specifier|public
name|void
name|setUserAmLimit
parameter_list|(
name|ResourceInfo
name|userAmLimit
parameter_list|)
block|{
name|this
operator|.
name|userAmLimit
operator|=
name|userAmLimit
expr_stmt|;
block|}
block|}
end_class

end_unit

