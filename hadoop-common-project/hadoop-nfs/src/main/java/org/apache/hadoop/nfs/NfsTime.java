begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|oncrpc
operator|.
name|XDR
import|;
end_import

begin_comment
comment|/**  * Class that encapsulates time.  */
end_comment

begin_class
DECL|class|NfsTime
specifier|public
class|class
name|NfsTime
block|{
DECL|field|MILLISECONDS_IN_SECOND
specifier|static
specifier|final
name|int
name|MILLISECONDS_IN_SECOND
init|=
literal|1000
decl_stmt|;
DECL|field|NANOSECONDS_IN_MILLISECOND
specifier|static
specifier|final
name|int
name|NANOSECONDS_IN_MILLISECOND
init|=
literal|1000000
decl_stmt|;
DECL|field|seconds
specifier|private
specifier|final
name|int
name|seconds
decl_stmt|;
DECL|field|nseconds
specifier|private
specifier|final
name|int
name|nseconds
decl_stmt|;
DECL|method|NfsTime (int seconds, int nseconds)
specifier|public
name|NfsTime
parameter_list|(
name|int
name|seconds
parameter_list|,
name|int
name|nseconds
parameter_list|)
block|{
name|this
operator|.
name|seconds
operator|=
name|seconds
expr_stmt|;
name|this
operator|.
name|nseconds
operator|=
name|nseconds
expr_stmt|;
block|}
DECL|method|NfsTime (NfsTime other)
specifier|public
name|NfsTime
parameter_list|(
name|NfsTime
name|other
parameter_list|)
block|{
name|seconds
operator|=
name|other
operator|.
name|getNseconds
argument_list|()
expr_stmt|;
name|nseconds
operator|=
name|other
operator|.
name|getNseconds
argument_list|()
expr_stmt|;
block|}
DECL|method|NfsTime (long milliseconds)
specifier|public
name|NfsTime
parameter_list|(
name|long
name|milliseconds
parameter_list|)
block|{
name|seconds
operator|=
call|(
name|int
call|)
argument_list|(
name|milliseconds
operator|/
name|MILLISECONDS_IN_SECOND
argument_list|)
expr_stmt|;
name|nseconds
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|milliseconds
operator|-
name|this
operator|.
name|seconds
operator|*
name|MILLISECONDS_IN_SECOND
operator|)
operator|*
name|NANOSECONDS_IN_MILLISECOND
argument_list|)
expr_stmt|;
block|}
DECL|method|getSeconds ()
specifier|public
name|int
name|getSeconds
parameter_list|()
block|{
return|return
name|seconds
return|;
block|}
DECL|method|getNseconds ()
specifier|public
name|int
name|getNseconds
parameter_list|()
block|{
return|return
name|nseconds
return|;
block|}
comment|/**    * Get the total time in milliseconds    * @return convert to milli seconds    */
DECL|method|getMilliSeconds ()
specifier|public
name|long
name|getMilliSeconds
parameter_list|()
block|{
return|return
call|(
name|long
call|)
argument_list|(
name|seconds
argument_list|)
operator|*
literal|1000
operator|+
call|(
name|long
call|)
argument_list|(
name|nseconds
argument_list|)
operator|/
literal|1000000
return|;
block|}
DECL|method|serialize (XDR xdr)
specifier|public
name|void
name|serialize
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
name|xdr
operator|.
name|writeInt
argument_list|(
name|getSeconds
argument_list|()
argument_list|)
expr_stmt|;
name|xdr
operator|.
name|writeInt
argument_list|(
name|getNseconds
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|deserialize (XDR xdr)
specifier|public
specifier|static
name|NfsTime
name|deserialize
parameter_list|(
name|XDR
name|xdr
parameter_list|)
block|{
return|return
operator|new
name|NfsTime
argument_list|(
name|xdr
operator|.
name|readInt
argument_list|()
argument_list|,
name|xdr
operator|.
name|readInt
argument_list|()
argument_list|)
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
return|return
name|seconds
operator|^
name|nseconds
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|NfsTime
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|NfsTime
operator|)
name|o
operator|)
operator|.
name|getMilliSeconds
argument_list|()
operator|==
name|this
operator|.
name|getMilliSeconds
argument_list|()
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
literal|"(NfsTime-"
operator|+
name|seconds
operator|+
literal|"s, "
operator|+
name|nseconds
operator|+
literal|"ns)"
return|;
block|}
block|}
end_class

end_unit

