begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|testjar
package|package
name|testjar
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
name|WritableComparable
import|;
end_import

begin_comment
comment|/**  * This is an example simple writable class.  This is used as a class external   * to the Hadoop IO classes for testing of user Writable classes.  *   */
end_comment

begin_class
DECL|class|ExternalWritable
specifier|public
class|class
name|ExternalWritable
implements|implements
name|WritableComparable
block|{
DECL|field|message
specifier|private
name|String
name|message
init|=
literal|null
decl_stmt|;
DECL|method|ExternalWritable ()
specifier|public
name|ExternalWritable
parameter_list|()
block|{        }
DECL|method|ExternalWritable (String message)
specifier|public
name|ExternalWritable
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
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
DECL|method|setMessage (String message)
specifier|public
name|void
name|setMessage
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
name|message
operator|=
literal|null
expr_stmt|;
name|boolean
name|hasMessage
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasMessage
condition|)
block|{
name|message
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
block|}
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
name|boolean
name|hasMessage
init|=
operator|(
name|message
operator|!=
literal|null
operator|&&
name|message
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasMessage
condition|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
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
name|ExternalWritable
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Input not an ExternalWritable"
argument_list|)
throw|;
block|}
name|ExternalWritable
name|that
init|=
operator|(
name|ExternalWritable
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|message
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|message
argument_list|)
return|;
block|}
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
block|}
end_class

end_unit

