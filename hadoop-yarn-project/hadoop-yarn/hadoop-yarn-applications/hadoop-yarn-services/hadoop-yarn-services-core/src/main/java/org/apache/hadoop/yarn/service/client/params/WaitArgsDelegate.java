begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client.params
package|package
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
name|client
operator|.
name|params
package|;
end_package

begin_import
import|import
name|com
operator|.
name|beust
operator|.
name|jcommander
operator|.
name|Parameter
import|;
end_import

begin_class
DECL|class|WaitArgsDelegate
specifier|public
class|class
name|WaitArgsDelegate
extends|extends
name|AbstractArgsDelegate
implements|implements
name|WaitTimeAccessor
block|{
comment|//--wait [timeout]
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
block|{
name|ARG_WAIT
block|}
argument_list|,
name|description
operator|=
literal|"time to wait for an action to complete"
argument_list|)
DECL|field|waittime
specifier|public
name|int
name|waittime
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|getWaittime ()
specifier|public
name|int
name|getWaittime
parameter_list|()
block|{
return|return
name|waittime
return|;
block|}
annotation|@
name|Override
DECL|method|setWaittime (int waittime)
specifier|public
name|void
name|setWaittime
parameter_list|(
name|int
name|waittime
parameter_list|)
block|{
name|this
operator|.
name|waittime
operator|=
name|waittime
expr_stmt|;
block|}
block|}
end_class

end_unit

