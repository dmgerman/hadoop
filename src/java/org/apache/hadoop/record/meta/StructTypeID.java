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
name|java
operator|.
name|util
operator|.
name|*
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
name|RecordInput
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
comment|/**   * Represents typeID for a struct   *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|StructTypeID
specifier|public
class|class
name|StructTypeID
extends|extends
name|TypeID
block|{
DECL|field|typeInfos
specifier|private
name|ArrayList
argument_list|<
name|FieldTypeInfo
argument_list|>
name|typeInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldTypeInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|StructTypeID ()
name|StructTypeID
parameter_list|()
block|{
name|super
argument_list|(
name|RIOType
operator|.
name|STRUCT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a StructTypeID based on the RecordTypeInfo of some record    */
DECL|method|StructTypeID (RecordTypeInfo rti)
specifier|public
name|StructTypeID
parameter_list|(
name|RecordTypeInfo
name|rti
parameter_list|)
block|{
name|super
argument_list|(
name|RIOType
operator|.
name|STRUCT
argument_list|)
expr_stmt|;
name|typeInfos
operator|.
name|addAll
argument_list|(
name|rti
operator|.
name|getFieldTypeInfos
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|add (FieldTypeInfo ti)
name|void
name|add
parameter_list|(
name|FieldTypeInfo
name|ti
parameter_list|)
block|{
name|typeInfos
operator|.
name|add
argument_list|(
name|ti
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldTypeInfos ()
specifier|public
name|Collection
argument_list|<
name|FieldTypeInfo
argument_list|>
name|getFieldTypeInfos
parameter_list|()
block|{
return|return
name|typeInfos
return|;
block|}
comment|/*     * return the StructTypeiD, if any, of the given field     */
DECL|method|findStruct (String name)
name|StructTypeID
name|findStruct
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// walk through the list, searching. Not the most efficient way, but this
comment|// in intended to be used rarely, so we keep it simple.
comment|// As an optimization, we can keep a hashmap of record name to its RTI, for later.
for|for
control|(
name|FieldTypeInfo
name|ti
range|:
name|typeInfos
control|)
block|{
if|if
condition|(
operator|(
literal|0
operator|==
name|ti
operator|.
name|getFieldID
argument_list|()
operator|.
name|compareTo
argument_list|(
name|name
argument_list|)
operator|)
operator|&&
operator|(
name|ti
operator|.
name|getTypeID
argument_list|()
operator|.
name|getTypeVal
argument_list|()
operator|==
name|RIOType
operator|.
name|STRUCT
operator|)
condition|)
block|{
return|return
operator|(
name|StructTypeID
operator|)
name|ti
operator|.
name|getTypeID
argument_list|()
return|;
block|}
block|}
return|return
literal|null
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
name|writeByte
argument_list|(
name|typeVal
argument_list|,
name|tag
argument_list|)
expr_stmt|;
name|writeRest
argument_list|(
name|rout
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
comment|/*     * Writes rest of the struct (excluding type value).    * As an optimization, this method is directly called by RTI     * for the top level record so that we don't write out the byte    * indicating that this is a struct (since top level records are    * always structs).    */
DECL|method|writeRest (RecordOutput rout, String tag)
name|void
name|writeRest
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
name|writeInt
argument_list|(
name|typeInfos
operator|.
name|size
argument_list|()
argument_list|,
name|tag
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldTypeInfo
name|ti
range|:
name|typeInfos
control|)
block|{
name|ti
operator|.
name|write
argument_list|(
name|rout
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*     * deserialize ourselves. Called by RTI.     */
DECL|method|read (RecordInput rin, String tag)
name|void
name|read
parameter_list|(
name|RecordInput
name|rin
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
comment|// number of elements
name|int
name|numElems
init|=
name|rin
operator|.
name|readInt
argument_list|(
name|tag
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numElems
condition|;
name|i
operator|++
control|)
block|{
name|typeInfos
operator|.
name|add
argument_list|(
name|genericReadTypeInfo
argument_list|(
name|rin
argument_list|,
name|tag
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// generic reader: reads the next TypeInfo object from stream and returns it
DECL|method|genericReadTypeInfo (RecordInput rin, String tag)
specifier|private
name|FieldTypeInfo
name|genericReadTypeInfo
parameter_list|(
name|RecordInput
name|rin
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fieldName
init|=
name|rin
operator|.
name|readString
argument_list|(
name|tag
argument_list|)
decl_stmt|;
name|TypeID
name|id
init|=
name|genericReadTypeID
argument_list|(
name|rin
argument_list|,
name|tag
argument_list|)
decl_stmt|;
return|return
operator|new
name|FieldTypeInfo
argument_list|(
name|fieldName
argument_list|,
name|id
argument_list|)
return|;
block|}
comment|// generic reader: reads the next TypeID object from stream and returns it
DECL|method|genericReadTypeID (RecordInput rin, String tag)
specifier|private
name|TypeID
name|genericReadTypeID
parameter_list|(
name|RecordInput
name|rin
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|typeVal
init|=
name|rin
operator|.
name|readByte
argument_list|(
name|tag
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|typeVal
condition|)
block|{
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|BOOL
case|:
return|return
name|TypeID
operator|.
name|BoolTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|BUFFER
case|:
return|return
name|TypeID
operator|.
name|BufferTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|BYTE
case|:
return|return
name|TypeID
operator|.
name|ByteTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|DOUBLE
case|:
return|return
name|TypeID
operator|.
name|DoubleTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|FLOAT
case|:
return|return
name|TypeID
operator|.
name|FloatTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|INT
case|:
return|return
name|TypeID
operator|.
name|IntTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|LONG
case|:
return|return
name|TypeID
operator|.
name|LongTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|MAP
case|:
block|{
name|TypeID
name|tIDKey
init|=
name|genericReadTypeID
argument_list|(
name|rin
argument_list|,
name|tag
argument_list|)
decl_stmt|;
name|TypeID
name|tIDValue
init|=
name|genericReadTypeID
argument_list|(
name|rin
argument_list|,
name|tag
argument_list|)
decl_stmt|;
return|return
operator|new
name|MapTypeID
argument_list|(
name|tIDKey
argument_list|,
name|tIDValue
argument_list|)
return|;
block|}
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|STRING
case|:
return|return
name|TypeID
operator|.
name|StringTypeID
return|;
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|STRUCT
case|:
block|{
name|StructTypeID
name|stID
init|=
operator|new
name|StructTypeID
argument_list|()
decl_stmt|;
name|int
name|numElems
init|=
name|rin
operator|.
name|readInt
argument_list|(
name|tag
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numElems
condition|;
name|i
operator|++
control|)
block|{
name|stID
operator|.
name|add
argument_list|(
name|genericReadTypeInfo
argument_list|(
name|rin
argument_list|,
name|tag
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|stID
return|;
block|}
case|case
name|TypeID
operator|.
name|RIOType
operator|.
name|VECTOR
case|:
block|{
name|TypeID
name|tID
init|=
name|genericReadTypeID
argument_list|(
name|rin
argument_list|,
name|tag
argument_list|)
decl_stmt|;
return|return
operator|new
name|VectorTypeID
argument_list|(
name|tID
argument_list|)
return|;
block|}
default|default:
comment|// shouldn't be here
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown type read"
argument_list|)
throw|;
block|}
block|}
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

