begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.example
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
name|example
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
name|contrib
operator|.
name|index
operator|.
name|mapred
operator|.
name|DocumentAndOp
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
name|Writable
import|;
end_import

begin_comment
comment|/**  * This class represents an operation. The operation can be an insert, a delete  * or an update. If the operation is an insert or an update, a (new) document,  * which is in the form of text, is specified.  */
end_comment

begin_class
DECL|class|LineDocTextAndOp
specifier|public
class|class
name|LineDocTextAndOp
implements|implements
name|Writable
block|{
DECL|field|op
specifier|private
name|DocumentAndOp
operator|.
name|Op
name|op
decl_stmt|;
DECL|field|doc
specifier|private
name|Text
name|doc
decl_stmt|;
comment|/**    * Constructor    */
DECL|method|LineDocTextAndOp ()
specifier|public
name|LineDocTextAndOp
parameter_list|()
block|{
name|doc
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the type of the operation.    * @param op  the type of the operation    */
DECL|method|setOp (DocumentAndOp.Op op)
specifier|public
name|void
name|setOp
parameter_list|(
name|DocumentAndOp
operator|.
name|Op
name|op
parameter_list|)
block|{
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
block|}
comment|/**    * Get the type of the operation.    * @return the type of the operation    */
DECL|method|getOp ()
specifier|public
name|DocumentAndOp
operator|.
name|Op
name|getOp
parameter_list|()
block|{
return|return
name|op
return|;
block|}
comment|/**    * Get the text that represents a document.    * @return the text that represents a document    */
DECL|method|getText ()
specifier|public
name|Text
name|getText
parameter_list|()
block|{
return|return
name|doc
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
literal|"[op="
operator|+
name|op
operator|+
literal|", text="
operator|+
name|doc
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

