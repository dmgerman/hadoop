begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent.application.metadata
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
operator|.
name|application
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|SliderException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|OSPackage
specifier|public
class|class
name|OSPackage
implements|implements
name|Validate
block|{
DECL|field|type
name|String
name|type
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|method|OSPackage ()
specifier|public
name|OSPackage
parameter_list|()
block|{   }
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|setType (String type)
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|validate (String version)
specifier|public
name|void
name|validate
parameter_list|(
name|String
name|version
parameter_list|)
throws|throws
name|SliderException
block|{
name|Metainfo
operator|.
name|checkNonNull
argument_list|(
name|getName
argument_list|()
argument_list|,
literal|"name"
argument_list|,
literal|"osPackage"
argument_list|)
expr_stmt|;
name|Metainfo
operator|.
name|checkNonNull
argument_list|(
name|getType
argument_list|()
argument_list|,
literal|"type"
argument_list|,
literal|"osPackage"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

