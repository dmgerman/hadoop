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

begin_import
import|import
name|com
operator|.
name|beust
operator|.
name|jcommander
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|com
operator|.
name|beust
operator|.
name|jcommander
operator|.
name|ParametersDelegate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_class
annotation|@
name|Parameters
argument_list|(
name|commandNames
operator|=
block|{
name|SliderActions
operator|.
name|ACTION_CREATE
block|}
argument_list|,
name|commandDescription
operator|=
name|SliderActions
operator|.
name|DESCRIBE_ACTION_CREATE
argument_list|)
DECL|class|SliderAMCreateAction
specifier|public
class|class
name|SliderAMCreateAction
extends|extends
name|AbstractActionArgs
implements|implements
name|LaunchArgsAccessor
block|{
annotation|@
name|Override
DECL|method|getActionName ()
specifier|public
name|String
name|getActionName
parameter_list|()
block|{
return|return
name|SliderActions
operator|.
name|ACTION_CREATE
return|;
block|}
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
name|ARG_IMAGE
argument_list|,
name|description
operator|=
literal|"image"
argument_list|,
name|required
operator|=
literal|false
argument_list|)
DECL|field|image
specifier|public
name|String
name|image
decl_stmt|;
comment|/**    * This is the URI in the FS to the Slider cluster; the conf file (and any    * other cluster-specifics) can be picked up here    */
annotation|@
name|Parameter
argument_list|(
name|names
operator|=
name|ARG_CLUSTER_URI
argument_list|,
name|description
operator|=
literal|"URI to the Slider cluster"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
DECL|field|sliderClusterURI
specifier|public
name|String
name|sliderClusterURI
decl_stmt|;
DECL|field|launchArgs
annotation|@
name|ParametersDelegate
name|LaunchArgsDelegate
name|launchArgs
init|=
operator|new
name|LaunchArgsDelegate
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getRmAddress ()
specifier|public
name|String
name|getRmAddress
parameter_list|()
block|{
return|return
name|launchArgs
operator|.
name|getRmAddress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getWaittime ()
specifier|public
name|int
name|getWaittime
parameter_list|()
block|{
return|return
name|launchArgs
operator|.
name|getWaittime
argument_list|()
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
name|launchArgs
operator|.
name|setWaittime
argument_list|(
name|waittime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOutputFile ()
specifier|public
name|File
name|getOutputFile
parameter_list|()
block|{
return|return
name|launchArgs
operator|.
name|getOutputFile
argument_list|()
return|;
block|}
block|}
end_class

end_unit

