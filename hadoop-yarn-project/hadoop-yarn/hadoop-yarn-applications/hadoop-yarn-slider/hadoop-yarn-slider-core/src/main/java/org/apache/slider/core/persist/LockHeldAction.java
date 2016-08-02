begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.persist
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Optional action to add while the lock is held; this is needed to execute  * some other persistent operations within the scope at the same lock  * without inserting too much code into the persister  */
end_comment

begin_interface
DECL|interface|LockHeldAction
specifier|public
interface|interface
name|LockHeldAction
block|{
comment|/**    * Execute the action    * @throws IOException on any failure    */
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
throws|,
name|SliderException
function_decl|;
block|}
end_interface

end_unit

