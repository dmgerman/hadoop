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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ExportGroup
specifier|public
class|class
name|ExportGroup
implements|implements
name|Validate
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|exports
name|List
argument_list|<
name|Export
argument_list|>
name|exports
decl_stmt|;
DECL|method|ExportGroup ()
specifier|public
name|ExportGroup
parameter_list|()
block|{
name|exports
operator|=
operator|new
name|ArrayList
argument_list|<
name|Export
argument_list|>
argument_list|()
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
DECL|method|addExport (Export export)
specifier|public
name|void
name|addExport
parameter_list|(
name|Export
name|export
parameter_list|)
block|{
name|exports
operator|.
name|add
argument_list|(
name|export
argument_list|)
expr_stmt|;
block|}
DECL|method|getExports ()
specifier|public
name|List
argument_list|<
name|Export
argument_list|>
name|getExports
parameter_list|()
block|{
return|return
name|exports
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",\n\"name\": "
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",\n\"exports\" : {"
argument_list|)
expr_stmt|;
for|for
control|(
name|Export
name|export
range|:
name|exports
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
name|export
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n},"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
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
literal|"exportGroup"
argument_list|)
expr_stmt|;
for|for
control|(
name|Export
name|exp
range|:
name|getExports
argument_list|()
control|)
block|{
name|exp
operator|.
name|validate
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

