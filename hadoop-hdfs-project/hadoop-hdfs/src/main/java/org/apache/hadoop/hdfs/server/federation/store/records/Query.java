begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.records
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
name|store
operator|.
name|records
package|;
end_package

begin_comment
comment|/**  * Check if a record matches a query. The query is usually a partial record.  *  * @param<T> Type of the record to query.  */
end_comment

begin_class
DECL|class|Query
specifier|public
class|class
name|Query
parameter_list|<
name|T
extends|extends
name|BaseRecord
parameter_list|>
block|{
comment|/** Partial object to compare against. */
DECL|field|partial
specifier|private
specifier|final
name|T
name|partial
decl_stmt|;
comment|/**    * Create a query to search for a partial record.    *    * @param partial It defines the attributes to search.    */
DECL|method|Query (final T part)
specifier|public
name|Query
parameter_list|(
specifier|final
name|T
name|part
parameter_list|)
block|{
name|this
operator|.
name|partial
operator|=
name|part
expr_stmt|;
block|}
comment|/**    * Get the partial record used to query.    *    * @return The partial record used for the query.    */
DECL|method|getPartial ()
specifier|public
name|T
name|getPartial
parameter_list|()
block|{
return|return
name|this
operator|.
name|partial
return|;
block|}
comment|/**    * Check if a record matches the primary keys or the partial record.    *    * @param other Record to check.    * @return If the record matches. Don't match if there is no partial.    */
DECL|method|matches (T other)
specifier|public
name|boolean
name|matches
parameter_list|(
name|T
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|partial
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|partial
operator|.
name|like
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Checking: "
operator|+
name|this
operator|.
name|partial
return|;
block|}
block|}
end_class

end_unit

