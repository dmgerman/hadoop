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
comment|/**  * DFS Content that displays a message.  */
end_comment

begin_class
DECL|class|DFSMessage
class|class
name|DFSMessage
implements|implements
name|DFSContent
block|{
DECL|field|message
specifier|private
name|String
name|message
decl_stmt|;
DECL|method|DFSMessage (String message)
name|DFSMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
comment|/*    * Implementation of DFSContent    */
comment|/* @inheritDoc */
DECL|method|getChildren ()
specifier|public
name|DFSContent
index|[]
name|getChildren
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* @inheritDoc */
DECL|method|hasChildren ()
specifier|public
name|boolean
name|hasChildren
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/* @inheritDoc */
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
block|{
comment|// Nothing to do
block|}
block|}
end_class

end_unit

