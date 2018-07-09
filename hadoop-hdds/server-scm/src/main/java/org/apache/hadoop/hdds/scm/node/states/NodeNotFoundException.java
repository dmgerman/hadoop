begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node.states
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|states
package|;
end_package

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
comment|/**  * This exception represents that the node that is being accessed does not  * exist in NodeStateMap.  */
end_comment

begin_class
DECL|class|NodeNotFoundException
specifier|public
class|class
name|NodeNotFoundException
extends|extends
name|NodeException
block|{
comment|/**    * Constructs an {@code NodeNotFoundException} with {@code null}    * as its error detail message.    */
DECL|method|NodeNotFoundException ()
specifier|public
name|NodeNotFoundException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs an {@code NodeNotFoundException} with the specified    * detail message.    *    * @param message    *        The detail message (which is saved for later retrieval    *        by the {@link #getMessage()} method)    */
DECL|method|NodeNotFoundException (String message)
specifier|public
name|NodeNotFoundException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

