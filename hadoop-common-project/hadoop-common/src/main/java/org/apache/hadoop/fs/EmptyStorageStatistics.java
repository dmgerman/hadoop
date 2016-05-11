begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * EmptyStorageStatistics is a StorageStatistics implementation which has no  * data.  */
end_comment

begin_class
DECL|class|EmptyStorageStatistics
class|class
name|EmptyStorageStatistics
extends|extends
name|StorageStatistics
block|{
DECL|method|EmptyStorageStatistics (String name)
name|EmptyStorageStatistics
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getLongStatistics ()
specifier|public
name|Iterator
argument_list|<
name|LongStatistic
argument_list|>
name|getLongStatistics
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
DECL|method|getLong (String key)
specifier|public
name|Long
name|getLong
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|isTracked (String key)
specifier|public
name|boolean
name|isTracked
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

