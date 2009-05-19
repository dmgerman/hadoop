begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.dfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|dfs
package|;
end_package

begin_comment
comment|/**  * Interface to define content entities in the DFS browser  */
end_comment

begin_interface
DECL|interface|DFSContent
specifier|public
interface|interface
name|DFSContent
block|{
DECL|method|hasChildren ()
name|boolean
name|hasChildren
parameter_list|()
function_decl|;
DECL|method|getChildren ()
name|DFSContent
index|[]
name|getChildren
parameter_list|()
function_decl|;
DECL|method|refresh ()
name|void
name|refresh
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

