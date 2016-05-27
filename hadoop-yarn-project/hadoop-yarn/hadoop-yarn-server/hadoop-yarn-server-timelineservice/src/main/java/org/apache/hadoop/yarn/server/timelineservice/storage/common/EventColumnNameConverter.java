begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
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
name|hbase
operator|.
name|util
operator|.
name|Bytes
import|;
end_import

begin_comment
comment|/**  * Encodes and decodes event column names for application and entity tables.  * The event column name is of the form : eventId=timestamp=infokey.  * If info is not associated with the event, event column name is of the form :  * eventId=timestamp=  * Event timestamp is long and rest are strings.  * Column prefixes are not part of the eventcolumn name passed for encoding. It  * is added later, if required in the associated ColumnPrefix implementations.  */
end_comment

begin_class
DECL|class|EventColumnNameConverter
specifier|public
specifier|final
class|class
name|EventColumnNameConverter
implements|implements
name|KeyConverter
argument_list|<
name|EventColumnName
argument_list|>
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|EventColumnNameConverter
name|INSTANCE
init|=
operator|new
name|EventColumnNameConverter
argument_list|()
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|EventColumnNameConverter
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
DECL|method|EventColumnNameConverter ()
specifier|private
name|EventColumnNameConverter
parameter_list|()
block|{   }
comment|// eventId=timestamp=infokey are of types String, Long String
comment|// Strings are variable in size (i.e. end whenever separator is encountered).
comment|// This is used while decoding and helps in determining where to split.
DECL|field|SEGMENT_SIZES
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|SEGMENT_SIZES
init|=
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
decl_stmt|;
comment|/*    * (non-Javadoc)    *    * Encodes EventColumnName into a byte array with each component/field in    * EventColumnName separated by Separator#VALUES. This leads to an event    * column name of the form eventId=timestamp=infokey.    * If timestamp in passed EventColumnName object is null (eventId is not null)    * this returns a column prefix of the form eventId= and if infokey in    * EventColumnName is null (other 2 components are not null), this returns a    * column name of the form eventId=timestamp=    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.KeyConverter    * #encode(java.lang.Object)    */
annotation|@
name|Override
DECL|method|encode (EventColumnName key)
specifier|public
name|byte
index|[]
name|encode
parameter_list|(
name|EventColumnName
name|key
parameter_list|)
block|{
name|byte
index|[]
name|first
init|=
name|Separator
operator|.
name|encode
argument_list|(
name|key
operator|.
name|getId
argument_list|()
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|,
name|Separator
operator|.
name|TAB
argument_list|,
name|Separator
operator|.
name|VALUES
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getTimestamp
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|Separator
operator|.
name|VALUES
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|Separator
operator|.
name|EMPTY_BYTES
argument_list|)
return|;
block|}
name|byte
index|[]
name|second
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|TimelineStorageUtils
operator|.
name|invertLong
argument_list|(
name|key
operator|.
name|getTimestamp
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getInfoKey
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|Separator
operator|.
name|VALUES
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|second
argument_list|,
name|Separator
operator|.
name|EMPTY_BYTES
argument_list|)
return|;
block|}
return|return
name|Separator
operator|.
name|VALUES
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|second
argument_list|,
name|Separator
operator|.
name|encode
argument_list|(
name|key
operator|.
name|getInfoKey
argument_list|()
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|,
name|Separator
operator|.
name|TAB
argument_list|,
name|Separator
operator|.
name|VALUES
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * (non-Javadoc)    *    * Decodes an event column name of the form eventId=timestamp= or    * eventId=timestamp=infoKey represented in byte format and converts it into    * an EventColumnName object.    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.KeyConverter    * #decode(byte[])    */
annotation|@
name|Override
DECL|method|decode (byte[] bytes)
specifier|public
name|EventColumnName
name|decode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|components
init|=
name|Separator
operator|.
name|VALUES
operator|.
name|split
argument_list|(
name|bytes
argument_list|,
name|SEGMENT_SIZES
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the column name is not valid"
argument_list|)
throw|;
block|}
name|String
name|id
init|=
name|Separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|components
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|Separator
operator|.
name|VALUES
argument_list|,
name|Separator
operator|.
name|TAB
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|)
decl_stmt|;
name|Long
name|ts
init|=
name|TimelineStorageUtils
operator|.
name|invertLong
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|components
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|infoKey
init|=
name|components
index|[
literal|2
index|]
operator|.
name|length
operator|==
literal|0
condition|?
literal|null
else|:
name|Separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|components
index|[
literal|2
index|]
argument_list|)
argument_list|,
name|Separator
operator|.
name|VALUES
argument_list|,
name|Separator
operator|.
name|TAB
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|)
decl_stmt|;
return|return
operator|new
name|EventColumnName
argument_list|(
name|id
argument_list|,
name|ts
argument_list|,
name|infoKey
argument_list|)
return|;
block|}
block|}
end_class

end_unit

