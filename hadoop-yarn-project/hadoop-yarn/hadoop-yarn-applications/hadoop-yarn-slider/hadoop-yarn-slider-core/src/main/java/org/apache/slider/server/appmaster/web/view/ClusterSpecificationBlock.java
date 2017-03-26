begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.view
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|view
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
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
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|WebAppApi
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|ClusterSpecificationBlock
specifier|public
class|class
name|ClusterSpecificationBlock
extends|extends
name|SliderHamletBlock
block|{
annotation|@
name|Inject
DECL|method|ClusterSpecificationBlock (WebAppApi slider)
specifier|public
name|ClusterSpecificationBlock
parameter_list|(
name|WebAppApi
name|slider
parameter_list|)
block|{
name|super
argument_list|(
name|slider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|doRender
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
comment|// An extra method to make testing easier since you can't make an instance of Block
DECL|method|doRender (Hamlet html)
specifier|protected
name|void
name|doRender
parameter_list|(
name|Hamlet
name|html
parameter_list|)
block|{
name|html
operator|.
name|div
argument_list|(
literal|"cluster_json"
argument_list|)
operator|.
name|h2
argument_list|(
literal|"JSON Cluster Specification"
argument_list|)
operator|.
name|pre
argument_list|()
operator|.
name|_
argument_list|(
name|getJson
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the JSON, catching any exceptions and returning error text instead    * @return    */
DECL|method|getJson ()
specifier|private
name|String
name|getJson
parameter_list|()
block|{
return|return
name|appState
operator|.
name|getApplication
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

