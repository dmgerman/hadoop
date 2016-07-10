begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Encodes and decodes column names / row keys which are merely strings.  * Column prefixes are not part of the column name passed for encoding. It is  * added later, if required in the associated ColumnPrefix implementations.  */
end_comment

begin_class
DECL|class|StringKeyConverter
specifier|public
specifier|final
class|class
name|StringKeyConverter
implements|implements
name|KeyConverter
argument_list|<
name|String
argument_list|>
block|{
DECL|method|StringKeyConverter ()
specifier|public
name|StringKeyConverter
parameter_list|()
block|{   }
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.KeyConverter    * #encode(java.lang.Object)    */
annotation|@
name|Override
DECL|method|encode (String key)
specifier|public
name|byte
index|[]
name|encode
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|Separator
operator|.
name|encode
argument_list|(
name|key
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|,
name|Separator
operator|.
name|TAB
argument_list|)
return|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.KeyConverter    * #decode(byte[])    */
annotation|@
name|Override
DECL|method|decode (byte[] bytes)
specifier|public
name|String
name|decode
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
name|Separator
operator|.
name|decode
argument_list|(
name|bytes
argument_list|,
name|Separator
operator|.
name|TAB
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

