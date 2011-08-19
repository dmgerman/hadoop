begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|index
operator|.
name|mapred
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
name|Text
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
comment|/**  * The class represents a document id, which is of type text.  */
end_comment

begin_class
DECL|class|DocumentID
specifier|public
class|class
name|DocumentID
implements|implements
name|WritableComparable
block|{
DECL|field|docID
specifier|private
specifier|final
name|Text
name|docID
decl_stmt|;
comment|/**    * Constructor.    */
DECL|method|DocumentID ()
specifier|public
name|DocumentID
parameter_list|()
block|{
name|docID
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
comment|/**    * The text of the document id.    * @return the text    */
DECL|method|getText ()
specifier|public
name|Text
name|getText
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Comparable#compareTo(java.lang.Object)    */
DECL|method|compareTo (Object obj)
specifier|public
name|int
name|compareTo
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
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|docID
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|DocumentID
operator|)
name|obj
operator|)
operator|.
name|docID
argument_list|)
return|;
block|}
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#hashCode()    */
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|docID
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#toString()    */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"["
operator|+
name|docID
operator|+
literal|"]"
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)    */
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
throw|throw
operator|new
name|IOException
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".write should never be called"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)    */
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
throw|throw
operator|new
name|IOException
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".readFields should never be called"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

