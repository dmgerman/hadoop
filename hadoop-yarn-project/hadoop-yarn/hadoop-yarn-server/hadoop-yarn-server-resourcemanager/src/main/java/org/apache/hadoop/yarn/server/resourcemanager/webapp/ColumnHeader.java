begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|resourcemanager
operator|.
name|webapp
package|;
end_package

begin_comment
comment|/**  * Header for a Web UI column. This used with TH.  */
end_comment

begin_class
DECL|class|ColumnHeader
specifier|public
class|class
name|ColumnHeader
block|{
DECL|field|selector
specifier|private
name|String
name|selector
decl_stmt|;
DECL|field|cdata
specifier|private
name|String
name|cdata
decl_stmt|;
DECL|method|ColumnHeader (String pselector, String pcdata)
specifier|public
name|ColumnHeader
parameter_list|(
name|String
name|pselector
parameter_list|,
name|String
name|pcdata
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|pselector
expr_stmt|;
name|this
operator|.
name|cdata
operator|=
name|pcdata
expr_stmt|;
block|}
comment|/**    * Get the selector field for the TH.    * @return Selector.    */
DECL|method|getSelector ()
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|this
operator|.
name|selector
return|;
block|}
comment|/**    * Get the cdata field for the TH.    * @return CData.    */
DECL|method|getCData ()
specifier|public
name|String
name|getCData
parameter_list|()
block|{
return|return
name|this
operator|.
name|cdata
return|;
block|}
block|}
end_class

end_unit

