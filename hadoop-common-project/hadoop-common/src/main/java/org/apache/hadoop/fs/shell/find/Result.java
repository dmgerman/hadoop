begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell.find
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|find
package|;
end_package

begin_class
DECL|class|Result
specifier|public
specifier|final
class|class
name|Result
block|{
comment|/** Result indicating {@link Expression} processing should continue. */
DECL|field|PASS
specifier|public
specifier|static
specifier|final
name|Result
name|PASS
init|=
operator|new
name|Result
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/** Result indicating {@link Expression} processing should stop. */
DECL|field|FAIL
specifier|public
specifier|static
specifier|final
name|Result
name|FAIL
init|=
operator|new
name|Result
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Result indicating {@link Expression} processing should not descend any more    * directories.    */
DECL|field|STOP
specifier|public
specifier|static
specifier|final
name|Result
name|STOP
init|=
operator|new
name|Result
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|descend
specifier|private
name|boolean
name|descend
decl_stmt|;
DECL|field|success
specifier|private
name|boolean
name|success
decl_stmt|;
DECL|method|Result (boolean success, boolean recurse)
specifier|private
name|Result
parameter_list|(
name|boolean
name|success
parameter_list|,
name|boolean
name|recurse
parameter_list|)
block|{
name|this
operator|.
name|success
operator|=
name|success
expr_stmt|;
name|this
operator|.
name|descend
operator|=
name|recurse
expr_stmt|;
block|}
comment|/** Should further directories be descended. */
DECL|method|isDescend ()
specifier|public
name|boolean
name|isDescend
parameter_list|()
block|{
return|return
name|this
operator|.
name|descend
return|;
block|}
comment|/** Should processing continue. */
DECL|method|isPass ()
specifier|public
name|boolean
name|isPass
parameter_list|()
block|{
return|return
name|this
operator|.
name|success
return|;
block|}
comment|/** Returns the combination of this and another result. */
DECL|method|combine (Result other)
specifier|public
name|Result
name|combine
parameter_list|(
name|Result
name|other
parameter_list|)
block|{
return|return
operator|new
name|Result
argument_list|(
name|this
operator|.
name|isPass
argument_list|()
operator|&&
name|other
operator|.
name|isPass
argument_list|()
argument_list|,
name|this
operator|.
name|isDescend
argument_list|()
operator|&&
name|other
operator|.
name|isDescend
argument_list|()
argument_list|)
return|;
block|}
comment|/** Negate this result. */
DECL|method|negate ()
specifier|public
name|Result
name|negate
parameter_list|()
block|{
return|return
operator|new
name|Result
argument_list|(
operator|!
name|this
operator|.
name|isPass
argument_list|()
argument_list|,
name|this
operator|.
name|isDescend
argument_list|()
argument_list|)
return|;
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
literal|"success="
operator|+
name|isPass
argument_list|()
operator|+
literal|"; recurse="
operator|+
name|isDescend
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|descend
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|success
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Result
name|other
init|=
operator|(
name|Result
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|descend
operator|!=
name|other
operator|.
name|descend
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|success
operator|!=
name|other
operator|.
name|success
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

