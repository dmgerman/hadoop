begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
package|;
end_package

begin_comment
comment|/**  * Details about some exported information from a provider to the AM web UI.  */
end_comment

begin_class
DECL|class|MonitorDetail
specifier|public
class|class
name|MonitorDetail
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|field|isUrl
specifier|private
specifier|final
name|boolean
name|isUrl
decl_stmt|;
DECL|method|MonitorDetail (String value, boolean isUrl)
specifier|public
name|MonitorDetail
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|isUrl
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|isUrl
operator|=
name|isUrl
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|isUrl ()
specifier|public
name|boolean
name|isUrl
parameter_list|()
block|{
return|return
name|isUrl
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MonitorDetail["
operator|+
name|value
operator|+
literal|" isUrl="
operator|+
name|isUrl
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

