begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver.order
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|order
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_comment
comment|/**  * Order of the destinations when we have multiple of them. When the resolver  * of files to subclusters (FileSubclusterResolver) has multiple destinations,  * this determines which location should be checked first.  */
end_comment

begin_enum
DECL|enum|DestinationOrder
specifier|public
enum|enum
name|DestinationOrder
block|{
DECL|enumConstant|HASH
name|HASH
block|,
comment|// Follow consistent hashing in the first folder level
DECL|enumConstant|LOCAL
name|LOCAL
block|,
comment|// Local first
DECL|enumConstant|RANDOM
name|RANDOM
block|,
comment|// Random order
DECL|enumConstant|HASH_ALL
name|HASH_ALL
block|,
comment|// Follow consistent hashing
DECL|enumConstant|SPACE
name|SPACE
block|;
comment|// Available space based order
comment|/** Approaches that write folders in all subclusters. */
DECL|field|FOLDER_ALL
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|DestinationOrder
argument_list|>
name|FOLDER_ALL
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|HASH_ALL
argument_list|,
name|RANDOM
argument_list|,
name|SPACE
argument_list|)
decl_stmt|;
block|}
end_enum

end_unit

