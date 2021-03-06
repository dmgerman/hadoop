begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.monitor.probe
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|monitor
operator|.
name|probe
package|;
end_package

begin_comment
comment|/**  * Build up log entries for ease of splunk  */
end_comment

begin_class
DECL|class|LogEntryBuilder
specifier|public
class|class
name|LogEntryBuilder
block|{
DECL|field|builder
specifier|private
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|method|LogEntryBuilder ()
specifier|public
name|LogEntryBuilder
parameter_list|()
block|{   }
DECL|method|LogEntryBuilder (String text)
specifier|public
name|LogEntryBuilder
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|elt
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
DECL|method|LogEntryBuilder (String name, Object value)
specifier|public
name|LogEntryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|entry
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|elt (String text)
specifier|public
name|LogEntryBuilder
name|elt
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|addComma
argument_list|()
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|elt (String name, Object value)
specifier|public
name|LogEntryBuilder
name|elt
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|addComma
argument_list|()
expr_stmt|;
name|entry
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addComma ()
specifier|private
name|void
name|addComma
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|entry (String name, Object value)
specifier|private
name|void
name|entry
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|isEmpty ()
specifier|private
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|builder
operator|.
name|length
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
end_class

end_unit

