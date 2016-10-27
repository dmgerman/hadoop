begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|LimitedPrivate
import|;
end_import

begin_comment
comment|/**  * Helper class for Timeline service.  */
end_comment

begin_class
annotation|@
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|,
literal|"YARN"
block|}
argument_list|)
DECL|class|TimelineServiceHelper
specifier|public
specifier|final
class|class
name|TimelineServiceHelper
block|{
DECL|method|TimelineServiceHelper ()
specifier|private
name|TimelineServiceHelper
parameter_list|()
block|{
comment|// Utility classes should not have a public or default constructor.
block|}
comment|/**    * Cast map to HashMap for generic type.    * @param originalMap the map need to be casted    * @param<E> key type    * @param<V> value type    * @return casted HashMap object    */
DECL|method|mapCastToHashMap ( Map<E, V> originalMap)
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|,
name|V
parameter_list|>
name|HashMap
argument_list|<
name|E
argument_list|,
name|V
argument_list|>
name|mapCastToHashMap
parameter_list|(
name|Map
argument_list|<
name|E
argument_list|,
name|V
argument_list|>
name|originalMap
parameter_list|)
block|{
return|return
name|originalMap
operator|==
literal|null
condition|?
literal|null
else|:
name|originalMap
operator|instanceof
name|HashMap
condition|?
operator|(
name|HashMap
argument_list|<
name|E
argument_list|,
name|V
argument_list|>
operator|)
name|originalMap
else|:
operator|new
name|HashMap
argument_list|<
name|E
argument_list|,
name|V
argument_list|>
argument_list|(
name|originalMap
argument_list|)
return|;
block|}
comment|/**    * Inverts the given key.    * @param key value to be inverted .    * @return inverted long    */
DECL|method|invertLong (long key)
specifier|public
specifier|static
name|long
name|invertLong
parameter_list|(
name|long
name|key
parameter_list|)
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
operator|-
name|key
return|;
block|}
block|}
end_class

end_unit

