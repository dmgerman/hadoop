begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.layout
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
name|layout
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
name|yarn
operator|.
name|webapp
operator|.
name|SubView
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
name|view
operator|.
name|ClusterSpecificationBlock
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|ClusterSpecificationView
specifier|public
class|class
name|ClusterSpecificationView
extends|extends
name|WebUILayout
block|{
annotation|@
name|Override
DECL|method|content ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|content
parameter_list|()
block|{
return|return
name|ClusterSpecificationBlock
operator|.
name|class
return|;
block|}
block|}
end_class

end_unit

