begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|datanode
operator|.
name|DataNode
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
operator|.
name|OzoneContainer
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
name|util
operator|.
name|ServicePlugin
import|;
end_import

begin_comment
comment|/**  * Stateless helper functions for MiniOzone based tests.  */
end_comment

begin_class
DECL|class|MiniOzoneTestHelper
specifier|public
class|class
name|MiniOzoneTestHelper
block|{
DECL|method|MiniOzoneTestHelper ()
specifier|private
name|MiniOzoneTestHelper
parameter_list|()
block|{   }
DECL|method|getOzoneContainer (DataNode dataNode)
specifier|public
specifier|static
name|OzoneContainer
name|getOzoneContainer
parameter_list|(
name|DataNode
name|dataNode
parameter_list|)
block|{
return|return
name|findHddsPlugin
argument_list|(
name|dataNode
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
return|;
block|}
DECL|method|getOzoneContainerManager (DataNode dataNode)
specifier|public
specifier|static
name|ContainerManager
name|getOzoneContainerManager
parameter_list|(
name|DataNode
name|dataNode
parameter_list|)
block|{
return|return
name|findHddsPlugin
argument_list|(
name|dataNode
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerManager
argument_list|()
return|;
block|}
DECL|method|getStateMachine (DataNode dataNode)
specifier|public
specifier|static
name|DatanodeStateMachine
name|getStateMachine
parameter_list|(
name|DataNode
name|dataNode
parameter_list|)
block|{
return|return
name|findHddsPlugin
argument_list|(
name|dataNode
argument_list|)
operator|.
name|getDatanodeStateMachine
argument_list|()
return|;
block|}
DECL|method|findHddsPlugin (DataNode dataNode)
specifier|private
specifier|static
name|HddsDatanodeService
name|findHddsPlugin
parameter_list|(
name|DataNode
name|dataNode
parameter_list|)
block|{
for|for
control|(
name|ServicePlugin
name|plugin
range|:
name|dataNode
operator|.
name|getPlugins
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|HddsDatanodeService
condition|)
block|{
return|return
operator|(
name|HddsDatanodeService
operator|)
name|plugin
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't find the Hdds server plugin in the"
operator|+
literal|" plugin collection of datanode"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

