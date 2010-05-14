begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|system
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * Class to represent a control action which can be performed on Daemon.<br/>  *   */
end_comment

begin_class
DECL|class|ControlAction
specifier|public
specifier|abstract
class|class
name|ControlAction
parameter_list|<
name|T
extends|extends
name|Writable
parameter_list|>
implements|implements
name|Writable
block|{
DECL|field|target
specifier|private
name|T
name|target
decl_stmt|;
comment|/**    * Default constructor of the Control Action, sets the Action type to zero.<br/>    */
DECL|method|ControlAction ()
specifier|public
name|ControlAction
parameter_list|()
block|{   }
comment|/**    * Constructor which sets the type of the Control action to a specific type.<br/>    *     * @param target    *          of the control action.    */
DECL|method|ControlAction (T target)
specifier|public
name|ControlAction
parameter_list|(
name|T
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
comment|/**    * Gets the id of the control action<br/>    *     * @return target of action    */
DECL|method|getTarget ()
specifier|public
name|T
name|getTarget
parameter_list|()
block|{
return|return
name|target
return|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|target
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|target
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|ControlAction
condition|)
block|{
name|ControlAction
argument_list|<
name|T
argument_list|>
name|other
init|=
operator|(
name|ControlAction
argument_list|<
name|T
argument_list|>
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|target
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getTarget
argument_list|()
argument_list|)
operator|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Action Target : "
operator|+
name|this
operator|.
name|target
return|;
block|}
block|}
end_class

end_unit

