begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.container.common.helpers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
package|;
end_package

begin_comment
comment|/**  * Class wraps container + allocated info for containers managed by block svc.  */
end_comment

begin_class
DECL|class|BlockContainerInfo
specifier|public
class|class
name|BlockContainerInfo
extends|extends
name|ContainerInfo
block|{
DECL|field|allocated
specifier|private
name|long
name|allocated
decl_stmt|;
DECL|method|BlockContainerInfo (ContainerInfo container, long used)
specifier|public
name|BlockContainerInfo
parameter_list|(
name|ContainerInfo
name|container
parameter_list|,
name|long
name|used
parameter_list|)
block|{
name|super
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|this
operator|.
name|allocated
operator|=
name|used
expr_stmt|;
block|}
DECL|method|addAllocated (long size)
specifier|public
name|long
name|addAllocated
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|allocated
operator|+=
name|size
expr_stmt|;
return|return
name|allocated
return|;
block|}
DECL|method|subtractAllocated (long size)
specifier|public
name|long
name|subtractAllocated
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|allocated
operator|-=
name|size
expr_stmt|;
return|return
name|allocated
return|;
block|}
DECL|method|getAllocated ()
specifier|public
name|long
name|getAllocated
parameter_list|()
block|{
return|return
name|this
operator|.
name|allocated
return|;
block|}
block|}
end_class

end_unit

