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
comment|/**   * A record's Type Information object which can read/write itself.   *   * Type information for a record comprises metadata about the record,   * as well as a collection of type information for each field in the record.   *   * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|RecordTypeInfo
specifier|public
class|class
name|RecordTypeInfo
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
operator|.
name|Record
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|// A RecordTypeInfo is really just a wrapper around StructTypeID
DECL|field|sTid
name|StructTypeID
name|sTid
decl_stmt|;
comment|// A RecordTypeInfo object is just a collection of TypeInfo objects for each of its fields.
comment|//private ArrayList<FieldTypeInfo> typeInfos = new ArrayList<FieldTypeInfo>();
comment|// we keep a hashmap of struct/record names and their type information, as we need it to
comment|// set filters when reading nested structs. This map is used during deserialization.
comment|//private Map<String, RecordTypeInfo> structRTIs = new HashMap<String, RecordTypeInfo>();
comment|/**    * Create an empty RecordTypeInfo object.    */
DECL|method|RecordTypeInfo ()
specifier|public
name|RecordTypeInfo
parameter_list|()
block|{
name|sTid
operator|=
operator|new
name|StructTypeID
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a RecordTypeInfo object representing a record with the given name    * @param name Name of the record    */
DECL|method|RecordTypeInfo (String name)
specifier|public
name|RecordTypeInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|sTid
operator|=
operator|new
name|StructTypeID
argument_list|()
expr_stmt|;
block|}
comment|/*    * private constructor    */
DECL|method|RecordTypeInfo (String name, StructTypeID stid)
specifier|private
name|RecordTypeInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|StructTypeID
name|stid
parameter_list|)
block|{
name|this
operator|.
name|sTid
operator|=
name|stid
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * return the name of the record    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * set the name of the record    */
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * Add a field.     * @param fieldName Name of the field    * @param tid Type ID of the field    */
DECL|method|addField (String fieldName, TypeID tid)
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TypeID
name|tid
parameter_list|)
block|{
name|sTid
operator|.
name|getFieldTypeInfos
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|FieldTypeInfo
argument_list|(
name|fieldName
argument_list|,
name|tid
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addAll (Collection<FieldTypeInfo> tis)
specifier|private
name|void
name|addAll
parameter_list|(
name|Collection
argument_list|<
name|FieldTypeInfo
argument_list|>
name|tis
parameter_list|)
block|{
name|sTid
operator|.
name|getFieldTypeInfos
argument_list|()
operator|.
name|addAll
argument_list|(
name|tis
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a collection of field type infos    */
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
name|sTid
operator|.
name|getFieldTypeInfos
argument_list|()
return|;
block|}
comment|/**    * Return the type info of a nested record. We only consider nesting     * to one level.     * @param name Name of the nested record    */
DECL|method|getNestedStructTypeInfo (String name)
specifier|public
name|RecordTypeInfo
name|getNestedStructTypeInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|StructTypeID
name|stid
init|=
name|sTid
operator|.
name|findStruct
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|stid
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|RecordTypeInfo
argument_list|(
name|name
argument_list|,
name|stid
argument_list|)
return|;
block|}
comment|/**    * Serialize the type information for a record    */
DECL|method|serialize (RecordOutput rout, String tag)
specifier|public
name|void
name|serialize
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
comment|// write out any header, version info, here
name|rout
operator|.
name|startRecord
argument_list|(
name|this
argument_list|,
name|tag
argument_list|)
expr_stmt|;
name|rout
operator|.
name|writeString
argument_list|(
name|name
argument_list|,
name|tag
argument_list|)
expr_stmt|;
name|sTid
operator|.
name|writeRest
argument_list|(
name|rout
argument_list|,
name|tag
argument_list|)
expr_stmt|;
name|rout
operator|.
name|endRecord
argument_list|(
name|this
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deserialize the type information for a record    */
DECL|method|deserialize (RecordInput rin, String tag)
specifier|public
name|void
name|deserialize
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
comment|// read in any header, version info
name|rin
operator|.
name|startRecord
argument_list|(
name|tag
argument_list|)
expr_stmt|;
comment|// name
name|this
operator|.
name|name
operator|=
name|rin
operator|.
name|readString
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|sTid
operator|.
name|read
argument_list|(
name|rin
argument_list|,
name|tag
argument_list|)
expr_stmt|;
name|rin
operator|.
name|endRecord
argument_list|(
name|tag
argument_list|)
expr_stmt|;
block|}
comment|/**    * This class doesn't implement Comparable as it's not meant to be used     * for anything besides de/serializing.    * So we always throw an exception.    * Not implemented. Always returns 0 if another RecordTypeInfo is passed in.     */
DECL|method|compareTo (final Object peer_)
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|Object
name|peer_
parameter_list|)
throws|throws
name|ClassCastException
block|{
if|if
condition|(
operator|!
operator|(
name|peer_
operator|instanceof
name|RecordTypeInfo
operator|)
condition|)
block|{
throw|throw
operator|new
name|ClassCastException
argument_list|(
literal|"Comparing different types of records."
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"compareTo() is not supported"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

