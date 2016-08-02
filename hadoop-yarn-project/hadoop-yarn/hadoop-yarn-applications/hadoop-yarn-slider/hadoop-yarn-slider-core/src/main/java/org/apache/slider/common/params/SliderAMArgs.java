begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Parameters sent by the Client to the AM  */
end_comment

begin_class
DECL|class|SliderAMArgs
specifier|public
class|class
name|SliderAMArgs
extends|extends
name|CommonArgs
block|{
DECL|field|createAction
name|SliderAMCreateAction
name|createAction
init|=
operator|new
name|SliderAMCreateAction
argument_list|()
decl_stmt|;
DECL|method|SliderAMArgs (String[] args)
specifier|public
name|SliderAMArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addActionArguments ()
specifier|protected
name|void
name|addActionArguments
parameter_list|()
block|{
name|addActions
argument_list|(
name|createAction
argument_list|)
expr_stmt|;
block|}
DECL|method|getImage ()
specifier|public
name|String
name|getImage
parameter_list|()
block|{
return|return
name|createAction
operator|.
name|image
return|;
block|}
comment|/**    * This is the URI in the FS to the Slider cluster; the conf file (and any    * other cluster-specifics) can be picked up here    */
DECL|method|getSliderClusterURI ()
specifier|public
name|String
name|getSliderClusterURI
parameter_list|()
block|{
return|return
name|createAction
operator|.
name|sliderClusterURI
return|;
block|}
comment|/**    * Am binding is simple: there is only one action    */
annotation|@
name|Override
DECL|method|applyAction ()
specifier|public
name|void
name|applyAction
parameter_list|()
block|{
name|bindCoreAction
argument_list|(
name|createAction
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

