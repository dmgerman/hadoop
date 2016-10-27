begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.entity
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
name|entity
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

begin_import
import|import
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
operator|.
name|AppIdKeyConverter
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|KeyConverter
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|LongConverter
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|Separator
import|;
end_import

begin_comment
comment|/**  * Represents a rowkey for the entity table.  */
end_comment

begin_class
DECL|class|EntityRowKey
specifier|public
class|class
name|EntityRowKey
block|{
DECL|field|clusterId
specifier|private
specifier|final
name|String
name|clusterId
decl_stmt|;
DECL|field|userId
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
DECL|field|flowName
specifier|private
specifier|final
name|String
name|flowName
decl_stmt|;
DECL|field|flowRunId
specifier|private
specifier|final
name|Long
name|flowRunId
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|String
name|appId
decl_stmt|;
DECL|field|entityType
specifier|private
specifier|final
name|String
name|entityType
decl_stmt|;
DECL|field|entityIdPrefix
specifier|private
specifier|final
name|long
name|entityIdPrefix
decl_stmt|;
DECL|field|entityId
specifier|private
specifier|final
name|String
name|entityId
decl_stmt|;
DECL|field|entityRowKeyConverter
specifier|private
specifier|final
name|KeyConverter
argument_list|<
name|EntityRowKey
argument_list|>
name|entityRowKeyConverter
init|=
operator|new
name|EntityRowKeyConverter
argument_list|()
decl_stmt|;
DECL|method|EntityRowKey (String clusterId, String userId, String flowName, Long flowRunId, String appId, String entityType, long entityIdPrefix, String entityId)
specifier|public
name|EntityRowKey
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|Long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|long
name|entityIdPrefix
parameter_list|,
name|String
name|entityId
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|flowName
operator|=
name|flowName
expr_stmt|;
name|this
operator|.
name|flowRunId
operator|=
name|flowRunId
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|entityType
operator|=
name|entityType
expr_stmt|;
name|this
operator|.
name|entityIdPrefix
operator|=
name|entityIdPrefix
expr_stmt|;
name|this
operator|.
name|entityId
operator|=
name|entityId
expr_stmt|;
block|}
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
DECL|method|getUserId ()
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
DECL|method|getFlowName ()
specifier|public
name|String
name|getFlowName
parameter_list|()
block|{
return|return
name|flowName
return|;
block|}
DECL|method|getFlowRunId ()
specifier|public
name|Long
name|getFlowRunId
parameter_list|()
block|{
return|return
name|flowRunId
return|;
block|}
DECL|method|getAppId ()
specifier|public
name|String
name|getAppId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getEntityType ()
specifier|public
name|String
name|getEntityType
parameter_list|()
block|{
return|return
name|entityType
return|;
block|}
DECL|method|getEntityId ()
specifier|public
name|String
name|getEntityId
parameter_list|()
block|{
return|return
name|entityId
return|;
block|}
DECL|method|getEntityIdPrefix ()
specifier|public
name|long
name|getEntityIdPrefix
parameter_list|()
block|{
return|return
name|entityIdPrefix
return|;
block|}
comment|/**    * Constructs a row key for the entity table as follows:    * {@code userName!clusterId!flowName!flowRunId!AppId!entityType!entityId}.    * Typically used while querying a specific entity.    *    * @return byte array with the row key.    */
DECL|method|getRowKey ()
specifier|public
name|byte
index|[]
name|getRowKey
parameter_list|()
block|{
return|return
name|entityRowKeyConverter
operator|.
name|encode
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Given the raw row key as bytes, returns the row key as an object.    *    * @param rowKey byte representation of row key.    * @return An<cite>EntityRowKey</cite> object.    */
DECL|method|parseRowKey (byte[] rowKey)
specifier|public
specifier|static
name|EntityRowKey
name|parseRowKey
parameter_list|(
name|byte
index|[]
name|rowKey
parameter_list|)
block|{
return|return
operator|new
name|EntityRowKeyConverter
argument_list|()
operator|.
name|decode
argument_list|(
name|rowKey
argument_list|)
return|;
block|}
comment|/**    * Encodes and decodes row key for entity table. The row key is of the form :    * userName!clusterId!flowName!flowRunId!appId!entityType!entityId. flowRunId    * is a long, appId is encoded/decoded using {@link AppIdKeyConverter} and    * rest are strings.    *<p>    */
DECL|class|EntityRowKeyConverter
specifier|final
specifier|private
specifier|static
class|class
name|EntityRowKeyConverter
implements|implements
name|KeyConverter
argument_list|<
name|EntityRowKey
argument_list|>
block|{
DECL|field|appIDKeyConverter
specifier|private
specifier|final
name|AppIdKeyConverter
name|appIDKeyConverter
init|=
operator|new
name|AppIdKeyConverter
argument_list|()
decl_stmt|;
DECL|method|EntityRowKeyConverter ()
specifier|private
name|EntityRowKeyConverter
parameter_list|()
block|{     }
comment|/**      * Entity row key is of the form      * userName!clusterId!flowName!flowRunId!appId!entityType!entityId w. each      * segment separated by !. The sizes below indicate sizes of each one of      * these segments in sequence. clusterId, userName, flowName, entityType and      * entityId are strings. flowrunId is a long hence 8 bytes in size. app id      * is represented as 12 bytes with cluster timestamp part of appid being 8      * bytes (long) and seq id being 4 bytes(int). Strings are variable in size      * (i.e. end whenever separator is encountered). This is used while decoding      * and helps in determining where to split.      */
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
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|AppIdKeyConverter
operator|.
name|getKeySize
argument_list|()
block|,
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
comment|/*      * (non-Javadoc)      *      * Encodes EntityRowKey object into a byte array with each component/field      * in EntityRowKey separated by Separator#QUALIFIERS. This leads to an      * entity table row key of the form      * userName!clusterId!flowName!flowRunId!appId!entityType!entityId If      * entityType in passed EntityRowKey object is null (and the fields      * preceding it i.e. clusterId, userId and flowName, flowRunId and appId      * are not null), this returns a row key prefix of the form      * userName!clusterId!flowName!flowRunId!appId! and if entityId in      * EntityRowKey is null (other 6 components are not null), this returns a      * row key prefix of the form      * userName!clusterId!flowName!flowRunId!appId!entityType! flowRunId is      * inverted while encoding as it helps maintain a descending order for row      * keys in entity table.      *      * @see org.apache.hadoop.yarn.server.timelineservice.storage.common      * .KeyConverter#encode(java.lang.Object)      */
annotation|@
name|Override
DECL|method|encode (EntityRowKey rowKey)
specifier|public
name|byte
index|[]
name|encode
parameter_list|(
name|EntityRowKey
name|rowKey
parameter_list|)
block|{
name|byte
index|[]
name|user
init|=
name|Separator
operator|.
name|encode
argument_list|(
name|rowKey
operator|.
name|getUserId
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
name|QUALIFIERS
argument_list|)
decl_stmt|;
name|byte
index|[]
name|cluster
init|=
name|Separator
operator|.
name|encode
argument_list|(
name|rowKey
operator|.
name|getClusterId
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
name|QUALIFIERS
argument_list|)
decl_stmt|;
name|byte
index|[]
name|flow
init|=
name|Separator
operator|.
name|encode
argument_list|(
name|rowKey
operator|.
name|getFlowName
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
name|QUALIFIERS
argument_list|)
decl_stmt|;
name|byte
index|[]
name|first
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|user
argument_list|,
name|cluster
argument_list|,
name|flow
argument_list|)
decl_stmt|;
comment|// Note that flowRunId is a long, so we can't encode them all at the same
comment|// time.
name|byte
index|[]
name|second
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|LongConverter
operator|.
name|invertLong
argument_list|(
name|rowKey
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|third
init|=
name|appIDKeyConverter
operator|.
name|encode
argument_list|(
name|rowKey
operator|.
name|getAppId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowKey
operator|.
name|getEntityType
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|second
argument_list|,
name|third
argument_list|,
name|Separator
operator|.
name|EMPTY_BYTES
argument_list|)
return|;
block|}
name|byte
index|[]
name|entityType
init|=
name|Separator
operator|.
name|encode
argument_list|(
name|rowKey
operator|.
name|getEntityType
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
name|QUALIFIERS
argument_list|)
decl_stmt|;
name|byte
index|[]
name|enitityIdPrefix
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|rowKey
operator|.
name|getEntityIdPrefix
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|entityId
init|=
name|rowKey
operator|.
name|getEntityId
argument_list|()
operator|==
literal|null
condition|?
name|Separator
operator|.
name|EMPTY_BYTES
else|:
name|Separator
operator|.
name|encode
argument_list|(
name|rowKey
operator|.
name|getEntityId
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
name|QUALIFIERS
argument_list|)
decl_stmt|;
name|byte
index|[]
name|fourth
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|entityType
argument_list|,
name|enitityIdPrefix
argument_list|,
name|entityId
argument_list|)
decl_stmt|;
return|return
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|second
argument_list|,
name|third
argument_list|,
name|fourth
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *      * Decodes an application row key of the form      * userName!clusterId!flowName!flowRunId!appId!entityType!entityId      * represented in byte format and converts it into an EntityRowKey object.      * flowRunId is inverted while decoding as it was inverted while encoding.      *      * @see      * org.apache.hadoop.yarn.server.timelineservice.storage.common      * .KeyConverter#decode(byte[])      */
annotation|@
name|Override
DECL|method|decode (byte[] rowKey)
specifier|public
name|EntityRowKey
name|decode
parameter_list|(
name|byte
index|[]
name|rowKey
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|rowKeyComponents
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|rowKey
argument_list|,
name|SEGMENT_SIZES
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowKeyComponents
operator|.
name|length
operator|!=
literal|8
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the row key is not valid for "
operator|+
literal|"an entity"
argument_list|)
throw|;
block|}
name|String
name|userId
init|=
name|Separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
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
name|String
name|clusterId
init|=
name|Separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
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
name|String
name|flowName
init|=
name|Separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|2
index|]
argument_list|)
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
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
name|flowRunId
init|=
name|LongConverter
operator|.
name|invertLong
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|rowKeyComponents
index|[
literal|3
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|appId
init|=
name|appIDKeyConverter
operator|.
name|decode
argument_list|(
name|rowKeyComponents
index|[
literal|4
index|]
argument_list|)
decl_stmt|;
name|String
name|entityType
init|=
name|Separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|5
index|]
argument_list|)
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
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
name|long
name|entityPrefixId
init|=
name|Bytes
operator|.
name|toLong
argument_list|(
name|rowKeyComponents
index|[
literal|6
index|]
argument_list|)
decl_stmt|;
name|String
name|entityId
init|=
name|Separator
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|7
index|]
argument_list|)
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
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
name|EntityRowKey
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowName
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entityType
argument_list|,
name|entityPrefixId
argument_list|,
name|entityId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

