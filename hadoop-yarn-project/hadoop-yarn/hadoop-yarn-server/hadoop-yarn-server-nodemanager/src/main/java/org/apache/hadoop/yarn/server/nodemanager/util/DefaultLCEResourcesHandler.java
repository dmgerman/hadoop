begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.util
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
name|nodemanager
operator|.
name|util
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|LinuxContainerExecutor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
annotation|@
name|Deprecated
DECL|class|DefaultLCEResourcesHandler
specifier|public
class|class
name|DefaultLCEResourcesHandler
implements|implements
name|LCEResourcesHandler
block|{
DECL|field|LOG
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DefaultLCEResourcesHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|DefaultLCEResourcesHandler ()
specifier|public
name|DefaultLCEResourcesHandler
parameter_list|()
block|{   }
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|init (LinuxContainerExecutor lce)
specifier|public
name|void
name|init
parameter_list|(
name|LinuxContainerExecutor
name|lce
parameter_list|)
block|{   }
comment|/*    * LCE Resources Handler interface    */
DECL|method|preExecute (ContainerId containerId, Resource containerResource)
specifier|public
name|void
name|preExecute
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|containerResource
parameter_list|)
block|{   }
DECL|method|postExecute (ContainerId containerId)
specifier|public
name|void
name|postExecute
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{   }
DECL|method|getResourcesOption (ContainerId containerId)
specifier|public
name|String
name|getResourcesOption
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
return|return
literal|"cgroups=none"
return|;
block|}
block|}
end_class

end_unit

