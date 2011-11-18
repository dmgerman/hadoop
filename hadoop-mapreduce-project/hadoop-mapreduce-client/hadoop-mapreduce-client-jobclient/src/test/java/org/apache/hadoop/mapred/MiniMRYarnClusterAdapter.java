begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|Configuration
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
name|mapreduce
operator|.
name|v2
operator|.
name|MiniMRYarnCluster
import|;
end_import

begin_comment
comment|/**  * An adapter for MiniMRYarnCluster providing a MiniMRClientCluster interface.  * This interface could be used by tests across both MR1 and MR2.  */
end_comment

begin_class
DECL|class|MiniMRYarnClusterAdapter
specifier|public
class|class
name|MiniMRYarnClusterAdapter
implements|implements
name|MiniMRClientCluster
block|{
DECL|field|miniMRYarnCluster
specifier|private
name|MiniMRYarnCluster
name|miniMRYarnCluster
decl_stmt|;
DECL|method|MiniMRYarnClusterAdapter (MiniMRYarnCluster miniMRYarnCluster)
specifier|public
name|MiniMRYarnClusterAdapter
parameter_list|(
name|MiniMRYarnCluster
name|miniMRYarnCluster
parameter_list|)
block|{
name|this
operator|.
name|miniMRYarnCluster
operator|=
name|miniMRYarnCluster
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConfig ()
specifier|public
name|Configuration
name|getConfig
parameter_list|()
block|{
return|return
name|miniMRYarnCluster
operator|.
name|getConfig
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|miniMRYarnCluster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|miniMRYarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

