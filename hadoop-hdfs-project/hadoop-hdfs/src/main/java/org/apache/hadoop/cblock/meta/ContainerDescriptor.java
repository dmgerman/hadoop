begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cblock.meta
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cblock
operator|.
name|meta
package|;
end_package

begin_comment
comment|/**  *  * The internal representation of a container maintained by CBlock server.  * Include enough information to exactly identify a container for read/write  * operation.  *  * NOTE that this class is work-in-progress. Depends on HDFS-7240 container  * implementation. Currently only to allow testing.  */
end_comment

begin_class
DECL|class|ContainerDescriptor
specifier|public
class|class
name|ContainerDescriptor
block|{
DECL|field|containerID
specifier|private
specifier|final
name|String
name|containerID
decl_stmt|;
comment|// the index of this container with in a volume
comment|// on creation, there is no way to know the index of the container
comment|// as it is a volume specific information
DECL|field|containerIndex
specifier|private
name|int
name|containerIndex
decl_stmt|;
DECL|method|ContainerDescriptor (String containerID)
specifier|public
name|ContainerDescriptor
parameter_list|(
name|String
name|containerID
parameter_list|)
block|{
name|this
operator|.
name|containerID
operator|=
name|containerID
expr_stmt|;
block|}
DECL|method|setContainerIndex (int idx)
specifier|public
name|void
name|setContainerIndex
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
name|this
operator|.
name|containerIndex
operator|=
name|idx
expr_stmt|;
block|}
DECL|method|getContainerID ()
specifier|public
name|String
name|getContainerID
parameter_list|()
block|{
return|return
name|containerID
return|;
block|}
DECL|method|getContainerIndex ()
specifier|public
name|int
name|getContainerIndex
parameter_list|()
block|{
return|return
name|containerIndex
return|;
block|}
DECL|method|getUtilization ()
specifier|public
name|long
name|getUtilization
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

