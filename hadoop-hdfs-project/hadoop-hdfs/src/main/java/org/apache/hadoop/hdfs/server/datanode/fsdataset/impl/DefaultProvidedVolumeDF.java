begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
package|package
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
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
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

begin_comment
comment|/**  * The default usage statistics for a provided volume.  */
end_comment

begin_class
DECL|class|DefaultProvidedVolumeDF
specifier|public
class|class
name|DefaultProvidedVolumeDF
implements|implements
name|ProvidedVolumeDF
implements|,
name|Configurable
block|{
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
block|{   }
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
annotation|@
name|Override
DECL|method|getSpaceUsed ()
specifier|public
name|long
name|getSpaceUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getBlockPoolUsed (String bpid)
specifier|public
name|long
name|getBlockPoolUsed
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getAvailable ()
specifier|public
name|long
name|getAvailable
parameter_list|()
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
block|}
end_class

end_unit

