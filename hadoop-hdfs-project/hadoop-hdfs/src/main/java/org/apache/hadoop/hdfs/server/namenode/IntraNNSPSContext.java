begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|sps
operator|.
name|StoragePolicySatisfier
import|;
end_import

begin_comment
comment|/**  * This class is the Namenode implementation for analyzing the file blocks which  * are expecting to change its storages and assigning the block storage  * movements to satisfy the storage policy.  */
end_comment

begin_comment
comment|// TODO: Now, added one API which is required for sps package. Will refine
end_comment

begin_comment
comment|// this interface via HDFS-12911.
end_comment

begin_class
DECL|class|IntraNNSPSContext
specifier|public
class|class
name|IntraNNSPSContext
implements|implements
name|StoragePolicySatisfier
operator|.
name|Context
block|{
DECL|field|namesystem
specifier|private
specifier|final
name|Namesystem
name|namesystem
decl_stmt|;
DECL|method|IntraNNSPSContext (Namesystem namesystem)
specifier|public
name|IntraNNSPSContext
parameter_list|(
name|Namesystem
name|namesystem
parameter_list|)
block|{
name|this
operator|.
name|namesystem
operator|=
name|namesystem
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumLiveDataNodes ()
specifier|public
name|int
name|getNumLiveDataNodes
parameter_list|()
block|{
return|return
name|namesystem
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getNumLiveDataNodes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

