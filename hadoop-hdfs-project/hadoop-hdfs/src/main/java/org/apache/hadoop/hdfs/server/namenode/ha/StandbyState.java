begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
operator|.
name|ha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
operator|.
name|ServiceFailedException
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
import|;
end_import

begin_comment
comment|/**  * Namenode standby state. In this state the namenode acts as warm standby and  * keeps the following updated:  *<ul>  *<li>Namespace by getting the edits.</li>  *<li>Block location information by receiving block reports and blocks  * received from the datanodes.</li>  *</ul>  *   * It does not handle read/write/checkpoint operations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StandbyState
specifier|public
class|class
name|StandbyState
extends|extends
name|HAState
block|{
DECL|method|StandbyState ()
specifier|public
name|StandbyState
parameter_list|()
block|{
name|super
argument_list|(
literal|"standby"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setState (HAContext context, HAState s)
specifier|public
name|void
name|setState
parameter_list|(
name|HAContext
name|context
parameter_list|,
name|HAState
name|s
parameter_list|)
throws|throws
name|ServiceFailedException
block|{
if|if
condition|(
name|s
operator|==
name|NameNode
operator|.
name|ACTIVE_STATE
condition|)
block|{
name|setStateInternal
argument_list|(
name|context
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return;
block|}
name|super
operator|.
name|setState
argument_list|(
name|context
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|enterState (HAContext context)
specifier|public
name|void
name|enterState
parameter_list|(
name|HAContext
name|context
parameter_list|)
throws|throws
name|ServiceFailedException
block|{
try|try
block|{
name|context
operator|.
name|startStandbyServices
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed to start standby services"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|exitState (HAContext context)
specifier|public
name|void
name|exitState
parameter_list|(
name|HAContext
name|context
parameter_list|)
throws|throws
name|ServiceFailedException
block|{
try|try
block|{
name|context
operator|.
name|stopStandbyServices
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceFailedException
argument_list|(
literal|"Failed to stop standby services"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

