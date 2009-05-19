begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record.meta
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
operator|.
name|meta
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
operator|.
name|RecordOutput
import|;
end_import

begin_comment
comment|/**   * Represents a type information for a field, which is made up of its   * ID (name) and its type (a TypeID object).  */
end_comment

begin_class
DECL|class|FieldTypeInfo
specifier|public
class|class
name|FieldTypeInfo
block|{
DECL|field|fieldID
specifier|private
name|String
name|fieldID
decl_stmt|;
DECL|field|typeID
specifier|private
name|TypeID
name|typeID
decl_stmt|;
comment|/**    * Construct a FiledTypeInfo with the given field name and the type    */
DECL|method|FieldTypeInfo (String fieldID, TypeID typeID)
name|FieldTypeInfo
parameter_list|(
name|String
name|fieldID
parameter_list|,
name|TypeID
name|typeID
parameter_list|)
block|{
name|this
operator|.
name|fieldID
operator|=
name|fieldID
expr_stmt|;
name|this
operator|.
name|typeID
operator|=
name|typeID
expr_stmt|;
block|}
comment|/**    * get the field's TypeID object    */
DECL|method|getTypeID ()
specifier|public
name|TypeID
name|getTypeID
parameter_list|()
block|{
return|return
name|typeID
return|;
block|}
comment|/**    * get the field's id (name)    */
DECL|method|getFieldID ()
specifier|public
name|String
name|getFieldID
parameter_list|()
block|{
return|return
name|fieldID
return|;
block|}
DECL|method|write (RecordOutput rout, String tag)
name|void
name|write
parameter_list|(
name|RecordOutput
name|rout
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|rout
operator|.
name|writeString
argument_list|(
name|fieldID
argument_list|,
name|tag
argument_list|)
expr_stmt|;
name|typeID
operator|.
name|write
argument_list|(
name|rout
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
comment|/**    * Two FieldTypeInfos are equal if ach of their fields matches    */
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FieldTypeInfo
operator|)
condition|)
return|return
literal|false
return|;
name|FieldTypeInfo
name|fti
init|=
operator|(
name|FieldTypeInfo
operator|)
name|o
decl_stmt|;
comment|// first check if fieldID matches
if|if
condition|(
operator|!
name|this
operator|.
name|fieldID
operator|.
name|equals
argument_list|(
name|fti
operator|.
name|fieldID
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// now see if typeID matches
return|return
operator|(
name|this
operator|.
name|typeID
operator|.
name|equals
argument_list|(
name|fti
operator|.
name|typeID
argument_list|)
operator|)
return|;
block|}
comment|/**    * We use a basic hashcode implementation, since this class will likely not    * be used as a hashmap key     */
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|37
operator|*
literal|17
operator|+
name|typeID
operator|.
name|hashCode
argument_list|()
operator|+
literal|37
operator|*
literal|17
operator|+
name|fieldID
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|equals (FieldTypeInfo ti)
specifier|public
name|boolean
name|equals
parameter_list|(
name|FieldTypeInfo
name|ti
parameter_list|)
block|{
comment|// first check if fieldID matches
if|if
condition|(
operator|!
name|this
operator|.
name|fieldID
operator|.
name|equals
argument_list|(
name|ti
operator|.
name|fieldID
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// now see if typeID matches
return|return
operator|(
name|this
operator|.
name|typeID
operator|.
name|equals
argument_list|(
name|ti
operator|.
name|typeID
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

