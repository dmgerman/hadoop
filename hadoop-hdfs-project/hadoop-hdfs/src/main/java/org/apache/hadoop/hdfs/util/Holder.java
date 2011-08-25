begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * A Holder is simply a wrapper around some other object. This is useful  * in particular for storing immutable values like boxed Integers in a  * collection without having to do the&quot;lookup&quot; of the value twice.  */
end_comment

begin_class
DECL|class|Holder
specifier|public
class|class
name|Holder
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|held
specifier|public
name|T
name|held
decl_stmt|;
DECL|method|Holder (T held)
specifier|public
name|Holder
parameter_list|(
name|T
name|held
parameter_list|)
block|{
name|this
operator|.
name|held
operator|=
name|held
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
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|held
argument_list|)
return|;
block|}
block|}
end_class

end_unit

