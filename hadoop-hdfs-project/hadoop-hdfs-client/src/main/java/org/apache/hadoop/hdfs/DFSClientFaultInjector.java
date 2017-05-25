begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Used for injecting faults in DFSClient and DFSOutputStream tests.  * Calls into this are a no-op in production code.  */
end_comment

begin_class
annotation|@
name|VisibleForTesting
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DFSClientFaultInjector
specifier|public
class|class
name|DFSClientFaultInjector
block|{
DECL|field|instance
specifier|private
specifier|static
name|DFSClientFaultInjector
name|instance
init|=
operator|new
name|DFSClientFaultInjector
argument_list|()
decl_stmt|;
DECL|field|exceptionNum
specifier|public
specifier|static
name|AtomicLong
name|exceptionNum
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|get ()
specifier|public
specifier|static
name|DFSClientFaultInjector
name|get
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
DECL|method|set (DFSClientFaultInjector instance)
specifier|public
specifier|static
name|void
name|set
parameter_list|(
name|DFSClientFaultInjector
name|instance
parameter_list|)
block|{
name|DFSClientFaultInjector
operator|.
name|instance
operator|=
name|instance
expr_stmt|;
block|}
DECL|method|corruptPacket ()
specifier|public
name|boolean
name|corruptPacket
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|uncorruptPacket ()
specifier|public
name|boolean
name|uncorruptPacket
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|failPacket ()
specifier|public
name|boolean
name|failPacket
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|startFetchFromDatanode ()
specifier|public
name|void
name|startFetchFromDatanode
parameter_list|()
block|{}
DECL|method|fetchFromDatanodeException ()
specifier|public
name|void
name|fetchFromDatanodeException
parameter_list|()
block|{}
DECL|method|readFromDatanodeDelay ()
specifier|public
name|void
name|readFromDatanodeDelay
parameter_list|()
block|{}
DECL|method|skipRollingRestartWait ()
specifier|public
name|boolean
name|skipRollingRestartWait
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

