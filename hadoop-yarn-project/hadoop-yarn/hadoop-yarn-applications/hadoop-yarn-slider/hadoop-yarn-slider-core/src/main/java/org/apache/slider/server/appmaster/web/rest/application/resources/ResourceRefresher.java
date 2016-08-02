begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.application.resources
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
name|rest
operator|.
name|application
operator|.
name|resources
package|;
end_package

begin_comment
comment|/**  * Interface which must be implemented to act as a source for cached content.  * @param<T> type to return  */
end_comment

begin_interface
DECL|interface|ResourceRefresher
specifier|public
interface|interface
name|ResourceRefresher
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Build an up to date version of the data    * @return a new instance of the (JSON serializable) data    */
DECL|method|refresh ()
name|T
name|refresh
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

