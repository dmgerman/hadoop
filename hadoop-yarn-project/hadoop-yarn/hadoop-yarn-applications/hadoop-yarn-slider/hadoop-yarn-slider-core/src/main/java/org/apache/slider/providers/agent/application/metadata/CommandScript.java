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
comment|/**  * CommandScript that implements all component commands  */
end_comment

begin_class
DECL|class|CommandScript
specifier|public
class|class
name|CommandScript
implements|implements
name|Validate
block|{
DECL|field|script
name|String
name|script
decl_stmt|;
DECL|field|scriptType
name|String
name|scriptType
decl_stmt|;
DECL|field|timeout
name|long
name|timeout
decl_stmt|;
DECL|method|CommandScript ()
specifier|public
name|CommandScript
parameter_list|()
block|{    }
DECL|method|getScript ()
specifier|public
name|String
name|getScript
parameter_list|()
block|{
return|return
name|script
return|;
block|}
DECL|method|setScript (String script)
specifier|public
name|void
name|setScript
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
DECL|method|getScriptType ()
specifier|public
name|String
name|getScriptType
parameter_list|()
block|{
return|return
name|scriptType
return|;
block|}
DECL|method|setScriptType (String scriptType)
specifier|public
name|void
name|setScriptType
parameter_list|(
name|String
name|scriptType
parameter_list|)
block|{
name|this
operator|.
name|scriptType
operator|=
name|scriptType
expr_stmt|;
block|}
DECL|method|getTimeout ()
specifier|public
name|long
name|getTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
DECL|method|setTimeout (long timeout)
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
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
literal|",\n\"script\": "
argument_list|)
operator|.
name|append
argument_list|(
name|script
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",\n\"scriptType\": "
argument_list|)
operator|.
name|append
argument_list|(
name|scriptType
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",\n\"timeout\" :"
argument_list|)
operator|.
name|append
argument_list|(
name|timeout
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
name|getScript
argument_list|()
argument_list|,
literal|"script"
argument_list|,
literal|"commandScript"
argument_list|)
expr_stmt|;
name|Metainfo
operator|.
name|checkNonNull
argument_list|(
name|getScriptType
argument_list|()
argument_list|,
literal|"scriptType"
argument_list|,
literal|"commandScript"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

